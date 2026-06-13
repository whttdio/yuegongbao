package com.yuegongbao.ygb.compliance.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceDimensionCount;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthlySummary;
import com.yuegongbao.ygb.compliance.mapper.YgbAttendanceMonthlyMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbAttendanceRawMapper;
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceMonthlyService;

@Service
public class YgbAttendanceMonthlyServiceImpl implements IYgbAttendanceMonthlyService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbAttendanceMonthlyMapper attendanceMonthlyMapper;

    @Autowired
    private YgbAttendanceRawMapper attendanceRawMapper;

    @Override
    public List<YgbAttendanceMonthly> selectAttendanceMonthlyList(YgbAttendanceMonthly attendanceMonthly)
    {
        return attendanceMonthlyMapper.selectAttendanceMonthlyList(attendanceMonthly);
    }

    @Override
    public YgbAttendanceMonthlySummary selectAttendanceMonthlySummary(YgbAttendanceMonthly attendanceMonthly)
    {
        List<YgbAttendanceMonthly> list = selectAttendanceMonthlyList(attendanceMonthly);
        YgbAttendanceMonthlySummary summary = new YgbAttendanceMonthlySummary();
        summary.setTotalCount(list.size());

        int pendingAggregateCount = 0;
        int aggregatedCount = 0;
        int verifiedCount = 0;
        int paidCount = 0;
        int checkFailedCount = 0;
        int workHourAbnormalCount = 0;
        for (YgbAttendanceMonthly item : list)
        {
            if ("1".equals(item.getSummaryStatus()))
            {
                pendingAggregateCount++;
            }
            if ("2".equals(item.getSummaryStatus()))
            {
                aggregatedCount++;
            }
            if ("3".equals(item.getSummaryStatus()))
            {
                verifiedCount++;
            }
            if ("4".equals(item.getSummaryStatus()))
            {
                paidCount++;
            }
            if ("0".equals(item.getAttCheck()))
            {
                checkFailedCount++;
            }
            if (hasWorkHourAbnormal(item))
            {
                workHourAbnormalCount++;
            }
        }

        summary.setPendingAggregateCount(pendingAggregateCount);
        summary.setAggregatedCount(aggregatedCount);
        summary.setVerifiedCount(verifiedCount);
        summary.setPaidCount(paidCount);
        summary.setCheckFailedCount(checkFailedCount);
        summary.setWorkHourAbnormalCount(workHourAbnormalCount);
        summary.setRegionStats(buildDimensionCounts(list, true));
        summary.setSummaryStatusStats(buildDimensionCounts(list, false));
        summary.setYgbExplanation(buildYgbExplanation(attendanceMonthly, summary, workHourAbnormalCount));
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int aggregateMonthly(String statMonth, Long dispatchEnterpriseId, String operator)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM");
        }

        List<YgbAttendanceMonthly> aggregates = attendanceMonthlyMapper.selectMonthlyAggregateRows(statMonth,
            dispatchEnterpriseId);
        attendanceMonthlyMapper.deleteMonthlyByScope(statMonth, dispatchEnterpriseId, operator);

        int rows = 0;
        for (YgbAttendanceMonthly aggregate : aggregates)
        {
            aggregate.setSummaryStatus("2");
            aggregate.setCreateBy(operator);
            rows += attendanceMonthlyMapper.insertAttendanceMonthly(aggregate);
        }

        attendanceRawMapper.updateCollectStatusByMonth(statMonth, dispatchEnterpriseId, operator);
        return rows;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbAttendanceMonthly attendanceMonthly,
        YgbAttendanceMonthlySummary summary, int workHourAbnormalCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(attendanceMonthly);
        list.add(explanationItem("pendingAggregate", "待归集对象", summary.getPendingAggregateCount(), 0,
            "优先补跑月度归集，避免工时台账缺失影响工资与月报主链。", "attendanceMonthly", "attendanceMonthly",
            "530.1 月考勤办理解释", baseQuery));
        list.add(explanationItem("checkFailed", "校验失败对象", summary.getCheckFailedCount(), 0,
            "先回查工时、缺勤和合同口径，再决定是否重新归集，避免错误数据进入工资链路。", "attendanceMonthly",
            "attendanceMonthly", "530.1 月考勤办理解释", baseQuery));
        list.add(explanationItem("aggregatedNotPaid", "已归集未发薪对象",
            summary.getAggregatedCount() + summary.getVerifiedCount(), 0,
            "已归集或已核验但尚未进入发薪闭环的对象，应继续承接到工资明细和批量发薪。", "attendanceMonthly",
            "attendanceMonthly", "530.1 月考勤办理解释", baseQuery));
        list.add(explanationItem("workHourAbnormal", "异常工时对象", workHourAbnormalCount, 0,
            "存在缺勤、迟到或早退的对象应先核对考勤口径，再承接后续工资计算。", "attendanceMonthly",
            "attendanceMonthly", "530.1 月考勤办理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbAttendanceMonthly attendanceMonthly)
    {
        Map<String, Object> query = new LinkedHashMap<>();
        if (attendanceMonthly == null)
        {
            return query;
        }
        if (StringUtils.isNotEmpty(attendanceMonthly.getStatMonth()))
        {
            query.put("statMonth", attendanceMonthly.getStatMonth());
        }
        if (attendanceMonthly.getDispatchEnterpriseId() != null)
        {
            query.put("dispatchEnterpriseId", attendanceMonthly.getDispatchEnterpriseId());
        }
        if (StringUtils.isNotEmpty(attendanceMonthly.getRegionCode()))
        {
            query.put("regionCode", attendanceMonthly.getRegionCode());
        }
        if (StringUtils.isNotEmpty(attendanceMonthly.getSummaryStatus()))
        {
            query.put("summaryStatus", attendanceMonthly.getSummaryStatus());
        }
        if (StringUtils.isNotEmpty(attendanceMonthly.getAttCheck()))
        {
            query.put("attCheck", attendanceMonthly.getAttCheck());
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

    private boolean hasWorkHourAbnormal(YgbAttendanceMonthly item)
    {
        return nvl(item.getAbsenceDays()) > 0 || nvl(item.getLateDays()) > 0 || nvl(item.getEarlyLeaveDays()) > 0;
    }

    private List<YgbAttendanceDimensionCount> buildDimensionCounts(List<YgbAttendanceMonthly> list, boolean byRegion)
    {
        Map<String, Integer> counter = new HashMap<>();
        for (YgbAttendanceMonthly item : list)
        {
            String key = byRegion ? item.getRegionCode() : item.getSummaryStatus();
            if (StringUtils.isEmpty(key))
            {
                continue;
            }
            counter.put(key, counter.getOrDefault(key, 0) + 1);
        }
        List<YgbAttendanceDimensionCount> result = new ArrayList<>();
        counter.forEach((key, value) -> {
            YgbAttendanceDimensionCount count = new YgbAttendanceDimensionCount();
            count.setDimensionKey(key);
            count.setDimensionLabel(key);
            count.setDimensionCount(value);
            result.add(count);
        });
        result.sort(Comparator.comparing(YgbAttendanceDimensionCount::getDimensionCount,
            Comparator.nullsFirst(Comparator.reverseOrder())).thenComparing(YgbAttendanceDimensionCount::getDimensionKey,
                Comparator.nullsLast(String::compareTo)));
        return result;
    }

    private int nvl(Integer value)
    {
        return value == null ? 0 : value;
    }
}
