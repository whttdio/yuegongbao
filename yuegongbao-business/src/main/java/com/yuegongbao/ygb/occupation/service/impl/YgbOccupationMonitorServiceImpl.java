package com.yuegongbao.ygb.occupation.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.domain.vo.YgbOccupationMonitorStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.OccupationClient;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitorSummary;
import com.yuegongbao.ygb.occupation.mapper.YgbOccupationMonitorMapper;
import com.yuegongbao.ygb.occupation.service.IYgbOccupationMonitorService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbOccupationMonitorServiceImpl implements IYgbOccupationMonitorService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbOccupationMonitorMapper occupationMonitorMapper;

    @Autowired
    private OccupationClient occupationClient;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbOccupationMonitor> selectOccupationMonitorList(YgbOccupationMonitor query)
    {
        List<YgbOccupationMonitor> list = occupationMonitorMapper.selectOccupationMonitorList(query);
        list.forEach(item -> item.setRegionName(YgbRegionHelper.resolveRegionName(item.getRegionCode())));
        return list;
    }

    @Override
    public YgbOccupationMonitorSummary selectOccupationMonitorSummary(YgbOccupationMonitor query)
    {
        List<YgbOccupationMonitor> list = selectOccupationMonitorList(query);
        YgbOccupationMonitorSummary summary = new YgbOccupationMonitorSummary();
        summary.setTotalCount(list.size());

        int yellowCount = 0;
        int redCount = 0;
        int totalCaseCount = 0;
        int totalHighRiskEnterpriseCount = 0;
        int highCaseCount = 0;
        int highRiskGroupCount = 0;
        BigDecimal incidenceTotal = BigDecimal.ZERO;
        int incidenceCount = 0;
        for (YgbOccupationMonitor item : list)
        {
            if ("1".equals(item.getWarningLevel()))
            {
                yellowCount++;
            }
            else if ("2".equals(item.getWarningLevel()))
            {
                redCount++;
            }
            totalCaseCount += item.getCaseCount() == null ? 0 : item.getCaseCount();
            totalHighRiskEnterpriseCount += item.getHighRiskEnterpriseCount() == null ? 0 : item.getHighRiskEnterpriseCount();
            if (item.getCaseCount() != null && item.getCaseCount() >= 3)
            {
                highCaseCount++;
            }
            if (item.getHighRiskEnterpriseCount() != null && item.getHighRiskEnterpriseCount() > 0)
            {
                highRiskGroupCount++;
            }
            if (item.getIncidenceRate() != null)
            {
                incidenceTotal = incidenceTotal.add(item.getIncidenceRate());
                incidenceCount++;
            }
        }

        summary.setYellowCount(yellowCount);
        summary.setRedCount(redCount);
        summary.setTotalCaseCount(totalCaseCount);
        summary.setTotalHighRiskEnterpriseCount(totalHighRiskEnterpriseCount);
        summary.setAverageIncidence(incidenceCount == 0
            ? BigDecimal.ZERO
            : incidenceTotal.divide(BigDecimal.valueOf(incidenceCount), 2, RoundingMode.HALF_UP));
        summary.setYgbExplanation(buildYgbExplanation(query, summary, highCaseCount, highRiskGroupCount));
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncOccupationMonitor(String statMonth, String operator)
    {
        validateMonth(statMonth);
        YgbOccupationMonitor deleteScope = buildScopedMonitorQuery(statMonth);
        occupationMonitorMapper.deleteByScope(deleteScope);
        List<YgbOccupationMonitorStubItem> records = occupationClient.pullMonitorRecords(statMonth);
        int rows = 0;
        for (YgbOccupationMonitorStubItem item : records)
        {
            if (!isItemInScope(item.getRegionCode()))
            {
                continue;
            }
            YgbOccupationMonitor monitor = new YgbOccupationMonitor();
            monitor.setStatMonth(statMonth);
            monitor.setRegionCode(item.getRegionCode());
            monitor.setIndustryType(item.getIndustryType());
            monitor.setEnterpriseCount(item.getEnterpriseCount());
            monitor.setWorkerCount(item.getWorkerCount());
            monitor.setCaseCount(item.getCaseCount());
            monitor.setHighRiskEnterpriseCount(item.getHighRiskEnterpriseCount());
            monitor.setIncidenceRate(item.getIncidenceRate());
            monitor.setWarningLevel(resolveWarningLevel(item.getIncidenceRate(), item.getCaseCount()));
            monitor.setWarningStatus("0".equals(monitor.getWarningLevel()) ? "0" : "1");
            monitor.setSourceChannel(item.getSourceChannel());
            monitor.setSourceSerialNo(item.getExternalSerialNo());
            monitor.setSourceStatus(item.getSourceStatus());
            monitor.setSourceMessage(item.getSourceMessage());
            monitor.setCallbackTime(item.getCallbackTime());
            monitor.setRawPayload(item.getRawPayload());
            monitor.setCreateBy(operator);
            occupationMonitorMapper.insertOccupationMonitor(monitor);
            rows++;

            if (!"0".equals(monitor.getWarningLevel()))
            {
                YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
                warning.setWarnLevel("2".equals(monitor.getWarningLevel()) ? "3" : "2");
                warning.setWarnType("OCCUPATION_MONITOR");
                warning.setSourceModule("OCCUPATION");
                warning.setTargetObjectId(monitor.getMonitorId());
                warning.setTargetType("4");
                warning.setRegionCode(monitor.getRegionCode());
                warning.setContent("职业病监测异常，区域：" + YgbRegionHelper.resolveRegionName(monitor.getRegionCode())
                    + "，行业：" + monitor.getIndustryType() + "，发病人数：" + monitor.getCaseCount() + "。");
                warningService.createWarningIfAbsent(warning, operator);
            }
        }
        return rows;
    }

    private YgbOccupationMonitor buildScopedMonitorQuery(String statMonth)
    {
        YgbOccupationMonitor query = new YgbOccupationMonitor();
        query.setStatMonth(statMonth);
        regionScopeHelper.applyRegionDataScope(query, "region_code");
        return query;
    }

    private boolean isItemInScope(String regionCode)
    {
        try
        {
            regionScopeHelper.assertRegionAuthorized(regionCode);
            return true;
        }
        catch (ServiceException e)
        {
            return false;
        }
    }

    static String resolveWarningLevel(BigDecimal incidenceRate, Integer caseCount)
    {
        BigDecimal rate = incidenceRate == null ? BigDecimal.ZERO : incidenceRate;
        int count = caseCount == null ? 0 : caseCount;
        if (rate.compareTo(new BigDecimal("3.50")) >= 0 || count >= 5)
        {
            return "2";
        }
        if (rate.compareTo(new BigDecimal("2.00")) >= 0 || count >= 3)
        {
            return "1";
        }
        return "0";
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }
    private List<Map<String, Object>> buildYgbExplanation(YgbOccupationMonitor query,
        YgbOccupationMonitorSummary summary, int highCaseCount, int highRiskGroupCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("red", "红警组合", summary.getRedCount(), 0,
            "优先承接红警区域行业组合，直接进入监管和预警闭环。", "occupationMonitor",
            "occupationMonitor", "530.1 职业监测办理解释", baseQuery));
        list.add(explanationItem("yellow", "黄警组合", summary.getYellowCount(), 0,
            "黄警组合应先复核行业归类、样本覆盖和发病统计口径，再决定是否升级处置。", "occupationMonitor",
            "occupationMonitor", "530.1 职业监测办理解释", baseQuery));
        list.add(explanationItem("case", "高发病组合", highCaseCount, 0,
            "发病人数较高的组合应继续跟踪发病趋势并准备联动企业侧复核。", "occupationMonitor",
            "occupationMonitor", "530.1 职业监测办理解释", baseQuery));
        list.add(explanationItem("highRiskEnterprise", "高风险企业聚集组合", highRiskGroupCount, 0,
            "存在高风险企业聚集的组合应继续下钻企业清单，判断是否进入办理复核。", "occupationMonitor",
            "occupationMonitor", "530.1 职业监测办理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbOccupationMonitor query)
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
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (StringUtils.isNotEmpty(query.getIndustryType()))
        {
            map.put("industryType", query.getIndustryType());
        }
        if (StringUtils.isNotEmpty(query.getWarningLevel()))
        {
            map.put("warningLevel", query.getWarningLevel());
        }
        if (StringUtils.isNotEmpty(query.getWarningStatus()))
        {
            map.put("warningStatus", query.getWarningStatus());
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
