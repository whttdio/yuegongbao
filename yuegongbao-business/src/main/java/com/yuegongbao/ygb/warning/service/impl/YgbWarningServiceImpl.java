package com.yuegongbao.ygb.warning.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.warning.domain.YgbWarningAnalysisSummary;
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.warning.domain.YgbWarningHandleLog;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.warning.mapper.YgbWarningHandleLogMapper;
import com.yuegongbao.ygb.warning.mapper.YgbWarningMapper;
import com.yuegongbao.ygb.util.YgbWarningTransitionHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;

@Service
public class YgbWarningServiceImpl implements IYgbWarningService
{
    @Autowired
    private YgbWarningMapper warningMapper;

    @Autowired
    private YgbWarningHandleLogMapper warningHandleLogMapper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbWarning> selectWarningList(YgbWarning warning)
    {
        return warningMapper.selectWarningList(warning);
    }

    @Override
    public YgbWarningSummary selectWarningSummary(YgbWarning warning)
    {
        YgbWarningSummary summary = warningMapper.selectWarningSummary(warning);
        if (summary == null)
        {
            summary = new YgbWarningSummary();
        }
        summary.setYgbExplanation(buildYgbExplanation(warning, summary));
        summary.setAzbExplanation(buildAzbExplanation(warning, summary));
        return summary;
    }

    @Override
    public YgbWarningAnalysisSummary selectWarningAnalysis(YgbWarning warning)
    {
        YgbWarningAnalysisSummary analysis = new YgbWarningAnalysisSummary();
        analysis.setLevelStats(warningMapper.selectWarningLevelStats(warning));
        analysis.setSourceStats(warningMapper.selectWarningSourceStats(warning));
        analysis.setRegionStats(warningMapper.selectWarningRegionStats(warning));
        analysis.setStatusStats(warningMapper.selectWarningStatusStats(warning));
        analysis.setOverduePendingCount(safeCount(warningMapper.countOverduePendingWarnings(warning)));
        analysis.setClosedWithin72hCount(safeCount(warningMapper.countClosedWithin72hWarnings(warning)));
        return analysis;
    }

    @Override
    public YgbWarning selectWarningById(Long warnId)
    {
        YgbWarning warning = warningMapper.selectWarningById(warnId);
        if (warning == null)
        {
            throw new ServiceException("预警记录不存在。");
        }
        assertEntityAllowed(warning);
        return warning;
    }

    @Override
    public List<YgbWarningHandleLog> selectWarningHandleLogs(Long warnId)
    {
        return warningHandleLogMapper.selectWarningHandleLogList(warnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWarning(YgbWarningCreateRequest request, String operator)
    {
        if (StringUtils.isEmpty(request.getContent()))
        {
            throw new ServiceException("预警内容不能为空。");
        }

        YgbWarning warning = new YgbWarning();
        warning.setWarnLevel(StringUtils.isEmpty(request.getWarnLevel()) ? "2" : request.getWarnLevel());
        warning.setWarnType(request.getWarnType());
        warning.setSourceModule(request.getSourceModule());
        warning.setTargetObjectId(request.getTargetObjectId());
        warning.setTargetType(request.getTargetType());
        warning.setEnterpriseId(request.getEnterpriseId());
        warning.setEnterpriseName(request.getEnterpriseName());
        warning.setRegionCode(request.getRegionCode());
        warning.setContent(request.getContent());
        warning.setEvidenceUrl(request.getEvidenceUrl());
        warning.setWarnStatus("0");
        warning.setCreateBy(operator);
        warning.setCreateTime(new Date());
        warningMapper.insertWarning(warning);
        return warning.getWarnId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWarningIfAbsent(YgbWarningCreateRequest request, String operator)
    {
        int exists = warningMapper.countActiveWarning(request.getSourceModule(), request.getWarnType(),
            request.getTargetObjectId());
        if (exists > 0)
        {
            return 0L;
        }
        return createWarning(request, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleWarning(Long warnId, String action, String opinion, String attachmentUrls, String operator)
    {
        if (StringUtils.isEmpty(opinion))
        {
            throw new ServiceException("处置意见不能为空。");
        }
        YgbWarning warning = selectWarningById(warnId);
        String afterStatus = YgbWarningTransitionHelper.transit(warning.getWarnStatus(), action);
        warningMapper.updateWarningStatus(warnId, afterStatus, operator, operator, "2".equals(afterStatus));

        YgbWarningHandleLog handleLog = new YgbWarningHandleLog();
        handleLog.setWarnId(warnId);
        handleLog.setActionType(action);
        handleLog.setOpinion(opinion);
        handleLog.setAttachmentUrls(attachmentUrls);
        handleLog.setBeforeStatus(warning.getWarnStatus());
        handleLog.setAfterStatus(afterStatus);
        handleLog.setHandlerName(operator);
        handleLog.setHandleTime(new Date());
        handleLog.setCreateBy(operator);
        return warningHandleLogMapper.insertWarningHandleLog(handleLog);
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbWarning query, YgbWarningSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("pending", "待处理工单", summary.getPendingCount(), 0,
            "待处理工单决定当前办理链是否持续积压，应优先消化当前待办。", "warning", "warning", "530.1 预警办理链解释",
            mergeQuery(baseQuery, "warnStatus", "0")));
        list.add(explanationItem("processing", "处理中工单", summary.getProcessingCount(), 0,
            "处理中工单应持续补齐责任人、处置意见和整改回写，避免停在处理中。", "warning", "warning", "530.1 预警办理链解释",
            mergeQuery(baseQuery, "warnStatus", "1")));
        list.add(explanationItem("financial", "社保税务联动", safeCount(summary.getSocialCount()) + safeCount(summary.getTaxCount()), 0,
            "社保与个税联动预警直接影响企业财务合规闭环，应优先回到企业办理主链核实。", "warning", "warning", "530.1 预警办理链解释",
            mergeQuery(baseQuery, "focusKey", "financial")));
        list.add(explanationItem("expansion", "扩面与专项治理", safeCount(summary.getExpansionCount()) + safeCount(summary.getSpecialCount()), 0,
            "扩面减损和专项治理来源预警应结合企业核查结果持续推进整改闭环。", "warning", "warning", "530.1 预警办理链解释",
            mergeQuery(baseQuery, "focusKey", "expansion")));
        list.add(explanationItem("red", "红警工单", summary.getRedCount(), 0,
            "红警工单会优先占用办理资源，应尽快核定责任主体并决定升级还是闭环。", "warning", "warning", "530.1 预警办理链解释",
            mergeQuery(baseQuery, "warnLevel", "3")));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbWarning query, YgbWarningSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("red", "红警对象", summary.getRedCount(), 0,
            "红警工单应优先进入现场治理和升级复核链路，避免高风险对象持续外溢。", "warning", "warning", "6.1 预警治理解释",
            mergeQuery(mergeQuery(baseQuery, "warnLevel", "3"), "focusKey", "red")));
        list.add(explanationItem("pending", "待处理工单", summary.getPendingCount(), 0,
            "待处理工单决定区域闭环压力，应优先压降当前待办积压。", "warning", "warning", "6.1 预警治理解释",
            mergeQuery(mergeQuery(baseQuery, "warnStatus", "0"), "focusKey", "pending")));
        list.add(explanationItem("device", "设备与 AI 来源", summary.getDeviceCount(), 0,
            "设备和 AI 来源更贴近现场实时风险，应优先回看设备授权、在线状态和异常留痕。", "warning", "warning", "6.1 预警治理解释",
            mergeQuery(mergeQuery(baseQuery, "sourceModule", "DEVICE"), "focusKey", "device")));
        list.add(explanationItem("upgrade", "升级处置工单", summary.getUpgradedCount(), 0,
            "已升级工单仍需持续跟踪后续处置结果，避免升级后失去闭环追踪。", "warning", "warning", "6.1 预警治理解释",
            mergeQuery(mergeQuery(baseQuery, "warnStatus", "4"), "focusKey", "upgrade")));
        list.add(explanationItem("injury", "工伤事故来源", summary.getInjuryCount(), 0,
            "工伤来源预警更适合作为事故预防、保单服务和区域治理的联动入口。", "warning", "warning", "6.1 预警治理解释",
            mergeQuery(mergeQuery(baseQuery, "sourceModule", "INJURY"), "focusKey", "injury")));
        list.add(explanationItem("closed", "已办结复核", summary.getClosedCount(), "持续复盘",
            "已办结工单用于复盘区域闭环稳定度，不再承担优先处置动作但应保留追溯能力。", "warning", "warning", "6.1 预警治理解释",
            mergeQuery(mergeQuery(baseQuery, "warnStatus", "2"), "focusKey", "closed")));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbWarning query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getWarnLevel()))
        {
            map.put("warnLevel", query.getWarnLevel());
        }
        if (StringUtils.isNotEmpty(query.getSourceModule()))
        {
            map.put("sourceModule", query.getSourceModule());
        }
        if (StringUtils.isNotEmpty(query.getWarnStatus()))
        {
            map.put("warnStatus", query.getWarnStatus());
        }
        if (StringUtils.isNotEmpty(query.getContent()))
        {
            map.put("content", query.getContent());
        }
        return map;
    }

    private Map<String, Object> mergeQuery(Map<String, Object> baseQuery, String key, Object value)
    {
        Map<String, Object> query = new LinkedHashMap<>(baseQuery);
        if (value != null && StringUtils.isNotEmpty(String.valueOf(value)))
        {
            query.put(key, value);
        }
        return query;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> defaultQuery)
    {
        Map<String, Object> nextQuery = new LinkedHashMap<>(defaultQuery);
        nextQuery.put("focusKey", focusKey);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", focusKey);
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", nextQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }

    private int safeCount(Integer value)
    {
        return value == null ? 0 : value;
    }

    private void assertEntityAllowed(Object entity)
    {
        if (dataScopeGuard != null)
        {
            dataScopeGuard.assertEntityAllowed(entity);
        }
    }
}
