package com.yuegongbao.ygb.warning.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.warning.domain.YgbWarningRule;
import com.yuegongbao.ygb.warning.domain.YgbWarningRuleSummary;
import com.yuegongbao.ygb.warning.mapper.YgbWarningRuleMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningRuleService;

@Service
public class YgbWarningRuleServiceImpl implements IYgbWarningRuleService
{
    @Autowired
    private YgbWarningRuleMapper warningRuleMapper;

    @Override
    public List<YgbWarningRule> selectWarningRuleList(YgbWarningRule warningRule)
    {
        return warningRuleMapper.selectWarningRuleList(warningRule);
    }

    @Override
    public YgbWarningRuleSummary selectWarningRuleSummary(YgbWarningRule warningRule)
    {
        List<YgbWarningRule> list = selectWarningRuleList(warningRule);
        YgbWarningRuleSummary summary = new YgbWarningRuleSummary();
        summary.setTotalCount(list.size());

        int enabledCount = 0;
        int redCount = 0;
        int deviceInjuryCount = 0;
        int deviceCount = 0;
        int expansionCount = 0;
        int totalTimeout = 0;
        int financialCount = 0;
        int disabledCount = 0;
        int targetMissingCount = 0;
        int timeoutShortCount = 0;
        int timeoutLongCount = 0;
        int highTimeoutCount = 0;
        for (YgbWarningRule item : list)
        {
            if ("1".equals(item.getRuleStatus()))
            {
                enabledCount++;
            }
            else if ("0".equals(item.getRuleStatus()))
            {
                disabledCount++;
            }
            if ("3".equals(item.getWarnLevel()))
            {
                redCount++;
            }
            if ("DEVICE".equals(item.getSourceModule()) || "INJURY".equals(item.getSourceModule()))
            {
                deviceInjuryCount++;
            }
            if ("DEVICE".equals(item.getSourceModule()))
            {
                deviceCount++;
            }
            if ("EXPANSION".equals(item.getSourceModule()))
            {
                expansionCount++;
            }
            if ("SOCIAL".equals(item.getSourceModule()) || "TAX".equals(item.getSourceModule())
                || "EXPANSION".equals(item.getSourceModule()))
            {
                financialCount++;
            }
            if (StringUtils.isEmpty(item.getPushTargets()))
            {
                targetMissingCount++;
            }
            int timeoutMinutes = item.getTimeoutMinutes() == null ? 0 : item.getTimeoutMinutes();
            if (timeoutMinutes > 0 && timeoutMinutes <= 60)
            {
                timeoutShortCount++;
            }
            if (timeoutMinutes >= 180)
            {
                timeoutLongCount++;
                highTimeoutCount++;
            }
            totalTimeout += timeoutMinutes;
        }

        summary.setEnabledCount(enabledCount);
        summary.setRedCount(redCount);
        summary.setDeviceInjuryCount(deviceInjuryCount);
        summary.setDeviceCount(deviceCount);
        summary.setExpansionCount(expansionCount);
        summary.setEnabledRatio(list.isEmpty() ? BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
            : BigDecimal.valueOf(enabledCount * 100D / list.size()).setScale(1, RoundingMode.HALF_UP));
        summary.setAverageTimeoutMinutes(list.isEmpty() ? 0 : Math.round((float) totalTimeout / list.size()));
        summary.setYgbExplanation(buildYgbExplanation(warningRule, summary, financialCount, disabledCount,
            targetMissingCount, timeoutShortCount, timeoutLongCount));
        summary.setAzbExplanation(buildAzbExplanation(warningRule, summary, disabledCount, targetMissingCount,
            highTimeoutCount, timeoutShortCount));
        return summary;
    }

    @Override
    public YgbWarningRule selectWarningRuleById(Long ruleId)
    {
        return warningRuleMapper.selectWarningRuleById(ruleId);
    }

    @Override
    public int insertWarningRule(YgbWarningRule warningRule)
    {
        return warningRuleMapper.insertWarningRule(warningRule);
    }

    @Override
    public int updateWarningRule(YgbWarningRule warningRule)
    {
        return warningRuleMapper.updateWarningRule(warningRule);
    }

    @Override
    public int deleteWarningRuleByIds(Long[] ruleIds, String updateBy)
    {
        return warningRuleMapper.deleteWarningRuleByIds(ruleIds, updateBy);
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbWarningRule query, YgbWarningRuleSummary summary,
        int financialCount, int disabledCount, int targetMissingCount, int timeoutShortCount, int timeoutLongCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("red", "红警规则", summary.getRedCount(), 0,
            "优先回查最高等级规则，确认触发条件和升级对象是否准确，避免影响企业办理链闭环。", "warningRule",
            "warningRule", "530.1 预警规则办理解释", baseQuery));
        list.add(explanationItem("financial", "财务来源规则", financialCount, 0,
            "工资、社保、税务和扩面减损来源规则应优先回到企业办理主链，确保预警解释与业务链一致。", "warningRule",
            "warningRule", "530.1 预警规则办理解释", baseQuery));
        list.add(explanationItem("targetMissing", "缺推送对象规则", targetMissingCount, 0,
            "先补齐责任人和接收对象，再继续推进升级校正，避免规则触发后没有承接人。", "warningRule",
            "warningRule", "530.1 预警规则办理解释", baseQuery));
        list.add(explanationItem("disabled", "停用规则", disabledCount, 0,
            "停用规则应优先回看停用原因和历史依赖，避免办理链存在隐性断点。", "warningRule", "warningRule",
            "530.1 预警规则办理解释", baseQuery));
        list.add(explanationItem("timeoutShort", "短时效规则", timeoutShortCount, 0,
            "时效过短的规则需要先补说明或调整时效，避免企业侧反复被催办。", "warningRule",
            "warningRule", "530.1 预警规则办理解释", baseQuery));
        list.add(explanationItem("timeoutLong", "长超时规则", timeoutLongCount, 0,
            "超时过长的规则会拖慢整改和回写节奏，应优先复核升级时效。", "warningRule", "warningRule",
            "530.1 预警规则办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbWarningRule query, YgbWarningRuleSummary summary,
        int disabledCount, int targetMissingCount, int highTimeoutCount, int timeoutShortCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("red", "红警规则", summary.getRedCount(), 0,
            "6.1 口径优先识别高等级规则，确保高风险事件能够被优先触发、督办和升级。", "warningRule",
            "warningRule", "6.1 预警规则治理解释", baseQuery));
        list.add(explanationItem("deviceInjury", "设备/工伤来源规则", summary.getDeviceInjuryCount(), 0,
            "现场治理链优先依赖设备和工伤来源规则，需要先确认来源模块和联动链路是否稳定。", "warningRule",
            "warningRule", "6.1 预警规则治理解释", baseQuery));
        list.add(explanationItem("expansion", "参保风险来源规则", summary.getExpansionCount(), 0,
            "区域治理需要确认参保风险规则是否仍在影响筛查、报表和对象分层口径。", "warningRule",
            "warningRule", "6.1 预警规则治理解释", baseQuery));
        list.add(explanationItem("missingTargets", "推送目标缺失", targetMissingCount, 0,
            "推送目标缺失会导致协同链路断点，应优先补齐治理承接对象。", "warningRule", "warningRule",
            "6.1 预警规则治理解释", baseQuery));
        list.add(explanationItem("inactive", "停用规则", disabledCount, 0,
            "停用规则需要确认是否仍被历史台账、报表或协同模块依赖。", "warningRule", "warningRule",
            "6.1 预警规则治理解释", baseQuery));
        list.add(explanationItem("highTimeout", "超时偏长规则", highTimeoutCount, 0,
            "时效偏长的规则会拖慢治理闭环，应优先复核当前处置节奏。", "warningRule", "warningRule",
            "6.1 预警规则治理解释", baseQuery));
        list.add(explanationItem("fastTimeout", "高时效规则", timeoutShortCount, "<= 60",
            "需要结合现场承接能力复核是否存在时效过紧、导致治理压力异常的规则配置。", "warningRule",
            "warningRule", "6.1 预警规则治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbWarningRule query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getSourceModule()))
        {
            map.put("sourceModule", query.getSourceModule());
        }
        if (StringUtils.isNotEmpty(query.getWarnLevel()))
        {
            map.put("warnLevel", query.getWarnLevel());
        }
        if (StringUtils.isNotEmpty(query.getRuleStatus()))
        {
            map.put("ruleStatus", query.getRuleStatus());
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
