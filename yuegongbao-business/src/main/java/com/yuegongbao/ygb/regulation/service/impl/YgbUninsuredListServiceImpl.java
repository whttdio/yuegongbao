package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredList;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredListSummary;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbTaxCompareMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbUninsuredListMapper;
import com.yuegongbao.ygb.regulation.service.IYgbUninsuredListService;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbUninsuredListServiceImpl implements IYgbUninsuredListService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbUninsuredListMapper uninsuredListMapper;

    @Autowired
    private YgbTaxCompareMapper taxCompareMapper;

    @Autowired
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbUninsuredList> selectUninsuredList(YgbUninsuredList uninsuredList)
    {
        return uninsuredListMapper.selectUninsuredList(uninsuredList);
    }

    @Override
    public YgbUninsuredListSummary selectUninsuredSummary(YgbUninsuredList uninsuredList)
    {
        List<YgbUninsuredList> list = selectUninsuredList(uninsuredList);
        YgbUninsuredListSummary summary = new YgbUninsuredListSummary();
        summary.setTotalCount(list.size());

        int pendingCount = 0;
        int unwarnedCount = 0;
        int completedCount = 0;
        int highSalaryCount = 0;
        int pushedCount = 0;
        int enforcedCount = 0;
        int misreportCount = 0;
        Set<Long> enterpriseIds = new HashSet<>();
        for (YgbUninsuredList item : list)
        {
            if ("0".equals(item.getDisposalStatus()) || "1".equals(item.getDisposalStatus()))
            {
                pendingCount++;
            }
            if ("0".equals(item.getWarningStatus()))
            {
                unwarnedCount++;
            }
            if ("3".equals(item.getDisposalStatus()))
            {
                completedCount++;
            }
            if ("2".equals(item.getDisposalStatus()))
            {
                pushedCount++;
            }
            if ("4".equals(item.getDisposalStatus()))
            {
                enforcedCount++;
            }
            if ("5".equals(item.getDisposalStatus()))
            {
                misreportCount++;
            }
            if (defaultAmount(item.getSalaryAmount()).compareTo(new BigDecimal("10000")) >= 0)
            {
                highSalaryCount++;
            }
            if (item.getEnterpriseId() != null)
            {
                enterpriseIds.add(item.getEnterpriseId());
            }
        }

        summary.setPendingCount(pendingCount);
        summary.setUnwarnedCount(unwarnedCount);
        summary.setCompletedCount(completedCount);
        summary.setHighSalaryCount(highSalaryCount);
        summary.setEnterpriseCount(enterpriseIds.size());
        summary.setYgbExplanation(buildYgbExplanation(uninsuredList, summary, pushedCount, enforcedCount, misreportCount));
        summary.setAzbExplanation(buildAzbExplanation(uninsuredList, summary));
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generate(String statMonth, Long enterpriseId, String operator)
    {
        validateMonth(statMonth);
        YgbTaxCompare taxQuery = new YgbTaxCompare();
        taxQuery.setStatMonth(statMonth);
        taxQuery.setEnterpriseId(enterpriseId);
        applyUninsuredScope(taxQuery);
        List<YgbTaxCompare> taxList = taxCompareMapper.selectTaxCompareList(taxQuery);

        YgbSocialPayment socialQuery = new YgbSocialPayment();
        socialQuery.setStatMonth(statMonth);
        socialQuery.setEnterpriseId(enterpriseId);
        applyUninsuredScope(socialQuery);
        List<YgbSocialPayment> socialList = socialPaymentMapper.selectSocialPaymentList(socialQuery);

        Set<Long> insuredPersonIds = new HashSet<>();
        for (YgbSocialPayment payment : socialList)
        {
            insuredPersonIds.add(payment.getPersonId());
        }

        YgbUninsuredList deleteScope = new YgbUninsuredList();
        deleteScope.setStatMonth(statMonth);
        deleteScope.setEnterpriseId(enterpriseId);
        applyUninsuredScope(deleteScope);
        uninsuredListMapper.deleteByScope(deleteScope);
        int rows = 0;
        String batchNo = "UNINS-" + statMonth.replace("-", "");
        for (YgbTaxCompare item : taxList)
        {
            if (insuredPersonIds.contains(item.getPersonId()))
            {
                continue;
            }

            YgbUninsuredList uninsured = new YgbUninsuredList();
            uninsured.setBatchNo(batchNo);
            uninsured.setStatMonth(statMonth);
            uninsured.setListType("1");
            uninsured.setEnterpriseId(item.getEnterpriseId());
            uninsured.setEnterpriseName(item.getEnterpriseName());
            uninsured.setPersonId(item.getPersonId());
            uninsured.setPersonName(item.getPersonName());
            uninsured.setIdCard(item.getIdCard());
            uninsured.setRegionCode(item.getRegionCode());
            uninsured.setSalaryAmount(item.getSalaryAmount() == null ? BigDecimal.ZERO : item.getSalaryAmount());
            uninsured.setDetectedReason("税务申报名单存在，但社保参保名单缺失。");
            uninsured.setDisposalStatus("0");
            uninsured.setWarningStatus("1");
            uninsured.setRemark("由税务名单减去社保参保名单生成。");
            uninsured.setCreateBy(operator);
            uninsuredListMapper.insertUninsured(uninsured);
            rows++;

            YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
            warning.setWarnLevel("2");
            warning.setWarnType("UNINSURED");
            warning.setSourceModule("EXPANSION");
            warning.setTargetObjectId(item.getPersonId());
            warning.setTargetType("3");
            warning.setEnterpriseId(item.getEnterpriseId());
            warning.setEnterpriseName(item.getEnterpriseName());
            warning.setRegionCode(item.getRegionCode());
            warning.setContent("发现漏保人员：" + item.getPersonName());
            warningService.createWarningIfAbsent(warning, operator);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handle(Long listId, String disposalStatus, String remark, String operator)
    {
        YgbUninsuredList record = uninsuredListMapper.selectUninsuredById(listId);
        if (record == null)
        {
            throw new ServiceException("漏保清单记录不存在。");
        }
        dataScopeGuard.assertEntityAllowed(record);
        String targetStatus = normalizeStatus(disposalStatus);
        validateDisposalTransition(record.getDisposalStatus(), targetStatus, remark);
        record.setDisposalStatus(targetStatus);
        record.setRemark(StringUtils.isEmpty(remark) ? record.getRemark() : remark.trim());
        record.setUpdateBy(operator);
        return uninsuredListMapper.updateUninsuredHandle(record);
    }

    private void applyUninsuredScope(com.yuegongbao.common.core.domain.BaseEntity query)
    {
        if (enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            enterpriseScopeHelper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "enterprise_id");
            return;
        }
        regionScopeHelper.applyRegionDataScope(query, "region_code");
    }

    private String normalizeStatus(String disposalStatus)
    {
        if (StringUtils.isEmpty(disposalStatus))
        {
            throw new ServiceException("漏保处置状态不能为空。");
        }
        String normalized = disposalStatus.trim();
        if (!"0".equals(normalized) && !"1".equals(normalized) && !"2".equals(normalized)
            && !"3".equals(normalized) && !"4".equals(normalized) && !"5".equals(normalized))
        {
            throw new ServiceException("漏保处置状态值不合法。");
        }
        return normalized;
    }

    private void validateDisposalTransition(String currentStatus, String targetStatus, String remark)
    {
        String current = StringUtils.defaultIfEmpty(currentStatus, "0");
        if (current.equals(targetStatus))
        {
            throw new ServiceException("漏保处置状态未发生变更。");
        }
        if ("3".equals(current) || "4".equals(current) || "5".equals(current))
        {
            throw new ServiceException("已补缴、强制执行或误报对象不允许再次处置。");
        }
        if (!isAllowedDisposalTransition(current, targetStatus))
        {
            throw new ServiceException("漏保处置状态只能按核查、催缴、结果回写链路流转。");
        }
        if (requiresRemark(targetStatus) && StringUtils.isEmpty(remark))
        {
            throw new ServiceException("进入催缴、补缴、强制执行或误报状态时，处置说明不能为空。");
        }
    }

    private boolean isAllowedDisposalTransition(String currentStatus, String targetStatus)
    {
        if ("0".equals(currentStatus))
        {
            return "1".equals(targetStatus) || "5".equals(targetStatus);
        }
        if ("1".equals(currentStatus))
        {
            return "2".equals(targetStatus) || "5".equals(targetStatus);
        }
        if ("2".equals(currentStatus))
        {
            return "3".equals(targetStatus) || "4".equals(targetStatus) || "5".equals(targetStatus);
        }
        return false;
    }

    private boolean requiresRemark(String targetStatus)
    {
        return "2".equals(targetStatus) || "3".equals(targetStatus) || "4".equals(targetStatus)
            || "5".equals(targetStatus);
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }

    private BigDecimal defaultAmount(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbUninsuredList query, YgbUninsuredListSummary summary,
        int pushedCount, int enforcedCount, int misreportCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("pending", "待核查对象", summary.getPendingCount(), 0,
            "优先处理仍停留在待核查和核查中的漏保对象，避免扩面减损闭环长期积压。", "uninsuredList",
            "uninsuredList", "530.1 漏保办理解释", baseQuery));
        list.add(explanationItem("highSalary", "高工资漏保对象", summary.getHighSalaryCount(), 0,
            "高工资漏保对象应联动工资、社保和个税链路复核，优先确认补缴口径和异常来源。", "uninsuredList",
            "uninsuredList", "530.1 漏保办理解释", baseQuery));
        list.add(explanationItem("unwarned", "未预警对象", summary.getUnwarnedCount(), 0,
            "先补齐联动预警和催办留痕，再继续推进企业核查与补缴回写。", "uninsuredList", "uninsuredList",
            "530.1 漏保办理解释", baseQuery));
        list.add(explanationItem("pushed", "已催缴待回写对象", pushedCount, 0,
            "已催缴对象要继续补充催缴结果、补缴时间和台账回写，避免长期停在中间状态。", "uninsuredList",
            "uninsuredList", "530.1 漏保办理解释", baseQuery));
        list.add(explanationItem("completed", "已补缴对象", summary.getCompletedCount(), 0,
            "已补缴对象应优先回看补缴结果和月度归档，确保办理链闭环已经落地。", "uninsuredList",
            "uninsuredList", "530.1 漏保办理解释", baseQuery));
        list.add(explanationItem("enforced", "强制执行对象", enforcedCount, 0,
            "强制执行对象适合作为历史积压和执行结果回写的重点复核入口。", "uninsuredList", "uninsuredList",
            "530.1 漏保办理解释", baseQuery));
        list.add(explanationItem("misreport", "误报对象", misreportCount, 0,
            "误报对象需要保留规则校正说明和证据，避免同类对象在后续月份继续误入漏保链。", "uninsuredList",
            "uninsuredList", "530.1 漏保办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbUninsuredList query, YgbUninsuredListSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("unwarned", "未预警对象", summary.getUnwarnedCount(), 0,
            "6.1 口径优先识别尚未进入联动预警的漏保对象，避免高风险对象停留在静态台账。", "uninsuredList",
            "uninsuredList", "6.1 漏保治理解释", baseQuery));
        list.add(explanationItem("pending", "待核查对象", summary.getPendingCount(), 0,
            "持续积压的漏保对象会拖慢区域治理节奏，应优先推进督办闭环。", "uninsuredList", "uninsuredList",
            "6.1 漏保治理解释", baseQuery));
        list.add(explanationItem("highSalary", "高工资漏保对象", summary.getHighSalaryCount(), 0,
            "高工资漏保对象通常暴露更高的区域风险，需要优先核实治理优先级。", "uninsuredList",
            "uninsuredList", "6.1 漏保治理解释", baseQuery));
        list.add(explanationItem("completed", "已完成处置对象", summary.getCompletedCount(), 0,
            "已完成处置对象应抽查回写质量，确认预警联动和治理结果已恢复稳定。", "uninsuredList",
            "uninsuredList", "6.1 漏保治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbUninsuredList query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getStatMonth()))
        {
            map.put("statMonth", query.getStatMonth());
        }
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getWarningStatus()))
        {
            map.put("warningStatus", query.getWarningStatus());
        }
        if (StringUtils.isNotEmpty(query.getDisposalStatus()))
        {
            map.put("disposalStatus", query.getDisposalStatus());
        }
        return map;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> baseQuery)
    {
        Map<String, Object> defaultQuery = new LinkedHashMap<>(baseQuery);
        defaultQuery.put("focusKey", focusKey);

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
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }
}
