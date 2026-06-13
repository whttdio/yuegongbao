package com.yuegongbao.ygb.aireport.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfigSummary;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportMapper;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportConfigService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbAiReportConfigServiceImpl implements IYgbAiReportConfigService
{
    @Autowired
    private YgbAiReportMapper aiReportMapper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbAiReportConfig> selectAiReportConfigList(YgbAiReportConfig config)
    {
        List<YgbAiReportConfig> list = aiReportMapper.selectAiReportConfigList(config);
        list.forEach(this::hydrateRegionName);
        return list;
    }

    @Override
    public YgbAiReportConfigSummary selectAiReportConfigSummary(YgbAiReportConfig config)
    {
        List<YgbAiReportConfig> list = selectAiReportConfigList(config);
        YgbAiReportConfigSummary summary = new YgbAiReportConfigSummary();
        summary.setTotalCount(list.size());

        int activeCount = 0;
        int upcomingCount = 0;
        int totalWeightSum = 0;
        int weightMismatchCount = 0;
        int closeRateLowCount = 0;
        int payTargetLowCount = 0;
        int attendanceTargetLowCount = 0;
        int injuryTargetHighCount = 0;
        Set<String> regionCodes = new HashSet<>();
        YgbAiReportConfig activeConfig = null;
        Date today = new Date();

        for (YgbAiReportConfig item : list)
        {
            if (StringUtils.isNotEmpty(item.getRegionCode()))
            {
                regionCodes.add(item.getRegionCode());
            }
            if ("1".equals(item.getConfigStatus()))
            {
                activeCount++;
                if (activeConfig == null)
                {
                    activeConfig = item;
                }
            }
            if (item.getEffectiveDate() != null && item.getEffectiveDate().after(today))
            {
                upcomingCount++;
            }
            int weightSum = sumWeights(item.getDimensionWeights());
            totalWeightSum += weightSum;
            if (weightSum != 100)
            {
                weightMismatchCount++;
            }

            JSONObject targetObject = parseJsonObject(item.getTargetValues());
            if (isPositiveAndBelow(targetObject.getBigDecimal("warningCloseRate"), new BigDecimal("90")))
            {
                closeRateLowCount++;
            }
            if (isPositiveAndBelow(targetObject.getBigDecimal("paySuccessRate"), new BigDecimal("98")))
            {
                payTargetLowCount++;
            }
            if (isPositiveAndBelow(targetObject.getBigDecimal("attendanceRate"), new BigDecimal("95")))
            {
                attendanceTargetLowCount++;
            }
            if (isAbove(targetObject.getBigDecimal("injuryRate"), new BigDecimal("2.5")))
            {
                injuryTargetHighCount++;
            }
        }

        summary.setActiveCount(activeCount);
        summary.setUpcomingCount(upcomingCount);
        summary.setCoveredRegionCount(regionCodes.size());
        summary.setAverageWeightSum(list.isEmpty() ? 0 : Math.round((float) totalWeightSum / list.size()));
        summary.setActiveVersion(activeConfig == null ? "-" : activeConfig.getVersion());
        summary.setActiveWarningCloseRate(resolveWarningCloseRate(activeConfig));
        summary.setYgbExplanation(buildYgbExplanation(config, summary, weightMismatchCount, closeRateLowCount,
            payTargetLowCount, attendanceTargetLowCount, injuryTargetHighCount));
        summary.setAzbExplanation(buildAzbExplanation(config, summary, weightMismatchCount, closeRateLowCount));
        return summary;
    }

    @Override
    public YgbAiReportConfig selectAiReportConfigById(Long configId)
    {
        YgbAiReportConfig config = aiReportMapper.selectAiReportConfigById(configId);
        if (config != null)
        {
            hydrateRegionName(config);
        }
        return config;
    }

    @Override
    public YgbAiReportConfig selectCurrentConfig(String regionCode, Date effectiveDate)
    {
        String normalizedRegionCode = regionScopeHelper.resolveAuthorizedRegionCode(regionCode);
        Date compareDate = effectiveDate == null ? new Date() : effectiveDate;

        YgbAiReportConfig config = aiReportMapper.selectActiveConfigByRegion(normalizedRegionCode, compareDate);
        if (config == null && !"440000".equals(normalizedRegionCode))
        {
            config = aiReportMapper.selectActiveConfigByRegion("440000", compareDate);
        }
        if (config == null)
        {
            config = buildDefaultConfig(normalizedRegionCode, compareDate);
        }
        hydrateRegionName(config);
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveAiReportConfig(YgbAiReportConfig config, String operator)
    {
        if (config == null)
        {
            throw new ServiceException("评分模型配置不能为空。");
        }

        config.setRegionCode(YgbRegionHelper.defaultDashboardRegion(config.getRegionCode()));
        if (config.getEffectiveDate() == null)
        {
            config.setEffectiveDate(new Date());
        }
        if (StringUtils.isEmpty(config.getVersion()))
        {
            config.setVersion("V" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        }
        if (StringUtils.isEmpty(config.getConfigStatus()))
        {
            config.setConfigStatus("1");
        }
        if (StringUtils.isEmpty(config.getSourceMode()))
        {
            config.setSourceMode("stub");
        }

        validateJsonPayload(config.getDimensionWeights(), "维度权重");
        validateJsonPayload(config.getTargetValues(), "目标值");

        int rows;
        if (config.getConfigId() == null)
        {
            config.setCreateBy(operator);
            config.setCreateTime(new Date());
            config.setUpdateBy(operator);
            config.setUpdateTime(new Date());
            rows = aiReportMapper.insertAiReportConfig(config);
        }
        else
        {
            config.setUpdateBy(operator);
            config.setUpdateTime(new Date());
            rows = aiReportMapper.updateAiReportConfig(config);
        }

        if ("1".equals(config.getConfigStatus()))
        {
            aiReportMapper.deactivateConfigsByRegion(config.getRegionCode(), config.getConfigId(), operator);
            config.setUpdateBy(operator);
            config.setUpdateTime(new Date());
            aiReportMapper.updateAiReportConfig(config);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int activateConfig(Long configId, String operator)
    {
        YgbAiReportConfig config = aiReportMapper.selectAiReportConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("评分模型配置不存在。");
        }
        aiReportMapper.deactivateConfigsByRegion(config.getRegionCode(), configId, operator);
        config.setConfigStatus("1");
        config.setUpdateBy(operator);
        config.setUpdateTime(new Date());
        return aiReportMapper.updateAiReportConfig(config);
    }

    private void hydrateRegionName(YgbAiReportConfig config)
    {
        config.setRegionName(YgbRegionHelper.resolveRegionName(config.getRegionCode()));
    }

    private void validateJsonPayload(String payload, String label)
    {
        if (StringUtils.isEmpty(payload))
        {
            throw new ServiceException(label + "不能为空。");
        }
        try
        {
            JSON.parseObject(payload);
        }
        catch (Exception ex)
        {
            throw new ServiceException(label + "格式不正确。");
        }
    }

    private YgbAiReportConfig buildDefaultConfig(String regionCode, Date effectiveDate)
    {
        YgbAiReportConfig config = new YgbAiReportConfig();
        config.setRegionCode(regionCode);
        config.setVersion("DEFAULT-STUB");
        config.setConfigStatus("1");
        config.setEffectiveDate(effectiveDate);
        config.setSourceMode("stub");
        config.setDimensionWeights(JSON.toJSONString(defaultDimensionWeights()));
        config.setTargetValues(JSON.toJSONString(defaultTargetValues()));
        config.setRemark("系统默认评分模型，可在评分模型配置中调整。");
        return config;
    }

    static Map<String, BigDecimal> defaultDimensionWeights()
    {
        Map<String, BigDecimal> weights = new LinkedHashMap<>();
        weights.put("A", new BigDecimal("25"));
        weights.put("B", new BigDecimal("20"));
        weights.put("C", new BigDecimal("20"));
        weights.put("D", new BigDecimal("20"));
        weights.put("E", new BigDecimal("15"));
        return weights;
    }

    static Map<String, BigDecimal> defaultTargetValues()
    {
        Map<String, BigDecimal> targets = new LinkedHashMap<>();
        targets.put("contractRate", new BigDecimal("100"));
        targets.put("attendanceRate", new BigDecimal("95"));
        targets.put("paySuccessRate", new BigDecimal("98"));
        targets.put("onlineRate", new BigDecimal("95"));
        targets.put("injuryRate", new BigDecimal("2.50"));
        targets.put("warningCloseRate", new BigDecimal("90"));
        return targets;
    }

    private int sumWeights(String payload)
    {
        JSONObject object = parseJsonObject(payload);
        int total = 0;
        total += object.getIntValue("A");
        total += object.getIntValue("B");
        total += object.getIntValue("C");
        total += object.getIntValue("D");
        total += object.getIntValue("E");
        return total;
    }

    private BigDecimal resolveWarningCloseRate(YgbAiReportConfig config)
    {
        if (config == null)
        {
            return null;
        }
        JSONObject object = parseJsonObject(config.getTargetValues());
        BigDecimal value = object.getBigDecimal("warningCloseRate");
        return value == null ? null : value.setScale(1, RoundingMode.HALF_UP);
    }

    private JSONObject parseJsonObject(String payload)
    {
        if (StringUtils.isEmpty(payload))
        {
            return new JSONObject();
        }
        try
        {
            return JSON.parseObject(payload);
        }
        catch (Exception ex)
        {
            return new JSONObject();
        }
    }
    private List<Map<String, Object>> buildYgbExplanation(YgbAiReportConfig config, YgbAiReportConfigSummary summary,
        int weightMismatchCount, int closeRateLowCount, int payTargetLowCount, int attendanceTargetLowCount,
        int injuryTargetHighCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(config);
        list.add(explanationItem("active", "当前启用版本", summary.getActiveCount(), 1,
            "优先确认当前启用版本与 AI 报告、信用评分和统计解释口径保持一致。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("upcoming", "待启用版本", summary.getUpcomingCount(), 0,
            "切换前应先补齐版本说明、生效日期和启用准备，避免配置口径在切换时断档。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("weight", "权重待校准版本", weightMismatchCount, 0,
            "权重总和不为 100 的版本不能直接进入正式启用，应先完成校准。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("closeRate", "闭环目标偏低版本", closeRateLowCount, 0,
            "闭环率目标偏低会弱化预警与办理闭环解释，应优先复核。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("payTarget", "工资成功率目标偏低版本", payTargetLowCount, 0,
            "工资成功率目标应与工资台账、月报和信用评分口径保持一致。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("attendanceTarget", "考勤通过率目标偏低版本", attendanceTargetLowCount, 0,
            "考勤通过率目标偏低会影响归集解释口径，应与考勤办理链重新对齐。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("injuryTarget", "工伤发生率阈值偏松版本", injuryTargetHighCount, 0,
            "工伤发生率阈值偏松会影响工伤事件与监管解释链口径，应优先复核。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        list.add(explanationItem("covered", "区域覆盖现状", summary.getCoveredRegionCount(), "全覆盖",
            "区域口径覆盖不足时，应先补齐配置范围，再承接月报与信用解释链。", "aiReportConfig",
            "aiReportConfig", "530.1 AI 配置办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbAiReportConfig config, YgbAiReportConfigSummary summary,
        int weightMismatchCount, int closeRateLowCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(config);
        list.add(explanationItem("active", "当前启用版本", summary.getActiveCount(), 1,
            "6.1 口径优先确认当前区域正在生效的风险研判版本。", "aiReportConfig",
            "aiReportConfig", "6.1 AI 配置治理解释", baseQuery));
        list.add(explanationItem("upcoming", "待生效版本", summary.getUpcomingCount(), 0,
            "待生效版本越多，越需要提前复核切换风险和解释口径变化。", "aiReportConfig",
            "aiReportConfig", "6.1 AI 配置治理解释", baseQuery));
        list.add(explanationItem("weight", "权重总和", summary.getAverageWeightSum(), 100,
            "权重异常值得优先进入详情复核，避免风险分层口径偏移。", "aiReportConfig",
            "aiReportConfig", "6.1 AI 配置治理解释", baseQuery));
        list.add(explanationItem("closeRate", "闭环目标复核", closeRateLowCount, 0,
            "闭环目标偏低时更值得联合复核，避免治理阈值过松。", "aiReportConfig",
            "aiReportConfig", "6.1 AI 配置治理解释", baseQuery));
        list.add(explanationItem("covered", "区域覆盖复核", summary.getCoveredRegionCount(), "全覆盖",
            "覆盖不足时应优先回到区域补齐配置，避免治理口径断层。", "aiReportConfig",
            "aiReportConfig", "6.1 AI 配置治理解释", baseQuery));
        if (weightMismatchCount > 0)
        {
            list.add(explanationItem("weightMismatch", "权重异常版本", weightMismatchCount, 0,
                "存在权重总和异常的版本时，应优先校准模型配置后再扩展治理解释。", "aiReportConfig",
                "aiReportConfig", "6.1 AI 配置治理解释", baseQuery));
        }
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbAiReportConfig config)
    {
        Map<String, Object> query = new LinkedHashMap<>();
        if (config == null)
        {
            return query;
        }
        if (StringUtils.isNotEmpty(config.getRegionCode()))
        {
            query.put("regionCode", config.getRegionCode());
        }
        if (StringUtils.isNotEmpty(config.getConfigStatus()))
        {
            query.put("configStatus", config.getConfigStatus());
        }
        return query;
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

    private boolean isPositiveAndBelow(BigDecimal value, BigDecimal target)
    {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0 && value.compareTo(target) < 0;
    }

    private boolean isAbove(BigDecimal value, BigDecimal target)
    {
        return value != null && value.compareTo(target) > 0;
    }
}
