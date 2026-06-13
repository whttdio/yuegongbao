package com.yuegongbao.ygb.compliance.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceDimensionCount;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawOverview;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawSummary;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.mapper.YgbAttendanceRawMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceRawService;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;

@Service
public class YgbAttendanceRawServiceImpl implements IYgbAttendanceRawService
{
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Autowired
    private YgbAttendanceRawMapper attendanceRawMapper;

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private IYgbDeviceService deviceService;

    @Override
    public List<YgbAttendanceRaw> selectAttendanceRawList(YgbAttendanceRaw attendanceRaw)
    {
        return attendanceRawMapper.selectAttendanceRawList(attendanceRaw);
    }

    @Override
    public YgbAttendanceRawSummary selectAttendanceRawSummary(YgbAttendanceRaw attendanceRaw)
    {
        List<YgbAttendanceRaw> list = selectAttendanceRawList(attendanceRaw);
        YgbAttendanceRawSummary summary = new YgbAttendanceRawSummary();
        summary.setTotalCount(list.size());

        int uncollectedCount = 0;
        int collectedCount = 0;
        int abnormalCount = 0;
        int manualFillCount = 0;
        int checkFailedCount = 0;
        for (YgbAttendanceRaw item : list)
        {
            if ("0".equals(item.getCollectStatus()))
            {
                uncollectedCount++;
            }
            if ("1".equals(item.getCollectStatus()))
            {
                collectedCount++;
            }
            if (isAttendanceAbnormal(item.getAttendanceStatus()))
            {
                abnormalCount++;
            }
            if ("4".equals(item.getSourceType()))
            {
                manualFillCount++;
            }
            if ("0".equals(item.getAttCheck()))
            {
                checkFailedCount++;
            }
        }

        summary.setUncollectedCount(uncollectedCount);
        summary.setCollectedCount(collectedCount);
        summary.setAbnormalCount(abnormalCount);
        summary.setManualFillCount(manualFillCount);
        summary.setCheckFailedCount(checkFailedCount);
        summary.setYgbExplanation(buildYgbExplanation(attendanceRaw, summary));
        return summary;
    }

    @Override
    public YgbAttendanceRawOverview selectAttendanceRawOverview(YgbAttendanceRaw attendanceRaw)
    {
        List<YgbAttendanceRaw> list = selectAttendanceRawList(attendanceRaw);
        YgbAttendanceRawSummary summary = selectAttendanceRawSummary(attendanceRaw);
        YgbAttendanceRawOverview overview = new YgbAttendanceRawOverview();
        overview.setTotalCount(summary.getTotalCount());
        overview.setUncollectedCount(summary.getUncollectedCount());
        overview.setCollectedCount(summary.getCollectedCount());
        overview.setAbnormalCount(summary.getAbnormalCount());
        overview.setManualFillCount(summary.getManualFillCount());
        overview.setCheckFailedCount(summary.getCheckFailedCount());
        overview.setRegionStats(buildDimensionCounts(list, true));
        overview.setSourceStats(buildDimensionCounts(list, false));

        YgbDevice deviceQuery = new YgbDevice();
        deviceQuery.setDeviceType("1");
        deviceQuery.setRegionCode(attendanceRaw == null ? null : attendanceRaw.getRegionCode());
        if (attendanceRaw != null && attendanceRaw.getDispatchEnterpriseId() != null)
        {
            deviceQuery.setEnterpriseId(attendanceRaw.getDispatchEnterpriseId());
        }
        YgbDeviceSummary deviceSummary = deviceService.selectDeviceSummary(deviceQuery);
        int totalDeviceCount = defaultInt(deviceSummary == null ? null : deviceSummary.getTotalCount());
        int onlineDeviceCount = defaultInt(deviceSummary == null ? null : deviceSummary.getOnlineCount());
        overview.setOnlineDeviceCount(onlineDeviceCount);
        overview.setOfflineDeviceCount(Math.max(totalDeviceCount - onlineDeviceCount, 0));
        overview.setDeviceOnlineRate(calculateRate(onlineDeviceCount, totalDeviceCount));
        return overview;
    }

    @Override
    public YgbAttendanceRaw selectAttendanceRawById(Long attendanceId)
    {
        return attendanceRawMapper.selectAttendanceRawById(attendanceId);
    }

    @Override
    public boolean checkAttendanceNoUnique(YgbAttendanceRaw attendanceRaw)
    {
        Long attendanceId = StringUtils.isNull(attendanceRaw.getAttendanceId()) ? -1L : attendanceRaw.getAttendanceId();
        YgbAttendanceRaw info = attendanceRawMapper.checkAttendanceNoUnique(attendanceRaw.getAttendanceNo());
        if (StringUtils.isNotNull(info) && info.getAttendanceId().longValue() != attendanceId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertAttendanceRaw(YgbAttendanceRaw attendanceRaw)
    {
        fillSnapshots(attendanceRaw);
        return attendanceRawMapper.insertAttendanceRaw(attendanceRaw);
    }

    @Override
    public int updateAttendanceRaw(YgbAttendanceRaw attendanceRaw)
    {
        fillSnapshots(attendanceRaw);
        return attendanceRawMapper.updateAttendanceRaw(attendanceRaw);
    }

    @Override
    public int deleteAttendanceRawByIds(Long[] attendanceIds, String updateBy)
    {
        return attendanceRawMapper.deleteAttendanceRawByIds(attendanceIds, updateBy);
    }

    private void fillSnapshots(YgbAttendanceRaw attendanceRaw)
    {
        YgbContract contract = contractMapper.selectContractById(attendanceRaw.getContractId());
        if (StringUtils.isNull(contract))
        {
            throw new ServiceException("关联合同不存在");
        }

        attendanceRaw.setContractNo(contract.getContractNo());
        attendanceRaw.setDispatchEnterpriseId(contract.getDispatchEnterpriseId());
        attendanceRaw.setDispatchEnterpriseName(contract.getDispatchEnterpriseName());
        attendanceRaw.setEmployerEnterpriseId(contract.getEmployerEnterpriseId());
        attendanceRaw.setEmployerEnterpriseName(contract.getEmployerEnterpriseName());
        attendanceRaw.setPersonId(contract.getPersonId());
        attendanceRaw.setPersonName(contract.getPersonName());
        attendanceRaw.setIdCard(contract.getIdCard());
        attendanceRaw.setRegionCode(contract.getRegionCode());

        if (StringUtils.isEmpty(attendanceRaw.getCollectStatus()))
        {
            attendanceRaw.setCollectStatus("0");
        }
        if (StringUtils.isEmpty(attendanceRaw.getAttCheck()))
        {
            attendanceRaw.setAttCheck("1");
        }
    }

    private boolean isAttendanceAbnormal(String attendanceStatus)
    {
        return "0".equals(attendanceStatus) || "2".equals(attendanceStatus) || "3".equals(attendanceStatus)
            || "5".equals(attendanceStatus);
    }

    private List<YgbAttendanceDimensionCount> buildDimensionCounts(List<YgbAttendanceRaw> list, boolean byRegion)
    {
        Map<String, Integer> counter = new HashMap<>();
        for (YgbAttendanceRaw item : list)
        {
            String key = byRegion ? item.getRegionCode() : item.getSourceType();
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

    private BigDecimal calculateRate(int numerator, int denominator)
    {
        if (denominator <= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(numerator).multiply(HUNDRED).divide(new BigDecimal(denominator), 2,
            RoundingMode.HALF_UP);
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbAttendanceRaw query, YgbAttendanceRawSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("uncollected", "未归集记录", summary.getUncollectedCount(), 0,
            "未归集原始考勤应在月度归集前先补齐，避免后续工时和工资链路中断。", "attendanceRaw", "attendanceRaw",
            "530.1 原始考勤办理解释", baseQuery));
        list.add(explanationItem("checkFailed", "校验失败记录", summary.getCheckFailedCount(), 0,
            "工资校验未通过对象应先复核，再进入后续工资处理链路。", "attendanceRaw", "attendanceRaw",
            "530.1 原始考勤办理解释", baseQuery));
        list.add(explanationItem("manualFill", "人工补录记录", summary.getManualFillCount(), "保持可追溯",
            "人工补录记录进入月度归集前，应保留佐证材料和来源说明。", "attendanceRaw", "attendanceRaw",
            "530.1 原始考勤办理解释", baseQuery));
        list.add(explanationItem("collected", "已归集记录", summary.getCollectedCount(), "保持流转",
            "已归集记录可继续支撑月度工时核算和工资下游校验。", "attendanceRaw", "attendanceRaw",
            "530.1 原始考勤办理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbAttendanceRaw query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getAttendanceNo()))
        {
            map.put("attendanceNo", query.getAttendanceNo());
        }
        if (query.getContractId() != null)
        {
            map.put("contractId", query.getContractId());
        }
        if (query.getDispatchEnterpriseId() != null)
        {
            map.put("dispatchEnterpriseId", query.getDispatchEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (StringUtils.isNotEmpty(query.getAttendanceStatus()))
        {
            map.put("attendanceStatus", query.getAttendanceStatus());
        }
        if (StringUtils.isNotEmpty(query.getCollectStatus()))
        {
            map.put("collectStatus", query.getCollectStatus());
        }
        if (StringUtils.isNotEmpty(query.getSourceType()))
        {
            map.put("sourceType", query.getSourceType());
        }
        if (StringUtils.isNotEmpty(query.getAttCheck()))
        {
            map.put("attCheck", query.getAttCheck());
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

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value;
    }
}
