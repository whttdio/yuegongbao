package com.yuegongbao.ygb.safety.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEvent;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEventSummary;
import com.yuegongbao.ygb.safety.mapper.YgbInjuryEventMapper;
import com.yuegongbao.ygb.safety.service.IYgbInjuryEventService;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbInjuryEventServiceImpl implements IYgbInjuryEventService
{
    @Autowired
    private YgbInjuryEventMapper injuryEventMapper;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Override
    public List<YgbInjuryEvent> selectInjuryEventList(YgbInjuryEvent injuryEvent)
    {
        return injuryEventMapper.selectInjuryEventList(injuryEvent);
    }

    @Override
    public YgbInjuryEventSummary selectInjuryEventSummary(YgbInjuryEvent injuryEvent)
    {
        List<YgbInjuryEvent> list = selectInjuryEventList(injuryEvent);
        YgbInjuryEventSummary summary = new YgbInjuryEventSummary();
        summary.setTotalCount(list.size());

        int pendingCount = 0;
        int warningCount = 0;
        int overdueCount = 0;
        int recognizingCount = 0;
        int claimingCount = 0;
        int finishedCount = 0;
        for (YgbInjuryEvent item : list)
        {
            if (!"4".equals(item.getInjuryStatus()))
            {
                pendingCount++;
            }
            if ("1".equals(item.getWarningStatus()))
            {
                warningCount++;
            }
            if (defaultInt(item.getRemainingDays()) < 0)
            {
                overdueCount++;
            }
            if ("1".equals(item.getInjuryStatus()))
            {
                recognizingCount++;
            }
            if ("3".equals(item.getInjuryStatus()))
            {
                claimingCount++;
            }
            if ("4".equals(item.getInjuryStatus()))
            {
                finishedCount++;
            }
        }

        summary.setPendingCount(pendingCount);
        summary.setWarningCount(warningCount);
        summary.setOverdueCount(overdueCount);
        summary.setRecognizingCount(recognizingCount);
        summary.setClaimingCount(claimingCount);
        summary.setFinishedCount(finishedCount);
        summary.setYgbExplanation(buildYgbExplanation(injuryEvent, summary));
        summary.setAzbExplanation(buildAzbExplanation(injuryEvent, summary));
        return summary;
    }

    @Override
    public YgbInjuryEvent selectInjuryEventById(Long eventId)
    {
        YgbInjuryEvent injuryEvent = injuryEventMapper.selectInjuryEventById(eventId);
        if (injuryEvent == null)
        {
            throw new ServiceException("工伤事件不存在");
        }
        return injuryEvent;
    }

    @Override
    public Map<String, Object> selectInjuryEventAnalysis(YgbInjuryEvent injuryEvent)
    {
        List<YgbInjuryEvent> list = selectInjuryEventList(injuryEvent);
        Map<String, Long> statusCounter = new LinkedHashMap<>();
        Map<String, Long> regionCounter = new LinkedHashMap<>();
        Map<String, Long> enterpriseCounter = new LinkedHashMap<>();
        Map<String, Long> warningCounter = new LinkedHashMap<>();
        for (YgbInjuryEvent item : list)
        {
            statusCounter.merge(StringUtils.defaultIfEmpty(item.getInjuryStatus(), "UNKNOWN"), 1L, Long::sum);
            regionCounter.merge(StringUtils.defaultIfEmpty(item.getRegionCode(), "-"), 1L, Long::sum);
            enterpriseCounter.merge(StringUtils.defaultIfEmpty(item.getEnterpriseName(), "-"), 1L, Long::sum);
            warningCounter.merge("1".equals(item.getWarningStatus()) ? "warning" : "normal", 1L, Long::sum);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCount", list.size());
        result.put("statusStats", toDimensionStats(statusCounter));
        result.put("regionStats", toDimensionStats(regionCounter));
        result.put("enterpriseStats", toDimensionStats(enterpriseCounter));
        result.put("warningStats", toDimensionStats(warningCounter));
        result.put("overdueCount", list.stream().filter(item -> defaultInt(item.getRemainingDays()) < 0).count());
        result.put("recognizingCount", list.stream().filter(item -> "1".equals(item.getInjuryStatus())).count());
        result.put("claimingCount", list.stream().filter(item -> "3".equals(item.getInjuryStatus())).count());
        return result;
    }

    @Override
    public Map<String, Object> selectInjuryEventMonitor(YgbInjuryEvent injuryEvent)
    {
        List<YgbInjuryEvent> list = selectInjuryEventList(injuryEvent);
        Map<String, Long> regionRiskCounter = new HashMap<>();
        Map<String, Long> enterpriseRiskCounter = new HashMap<>();
        List<YgbInjuryEvent> overdueRows = new ArrayList<>();
        for (YgbInjuryEvent item : list)
        {
            if (defaultInt(item.getRemainingDays()) < 0 || "1".equals(item.getWarningStatus()))
            {
                regionRiskCounter.merge(StringUtils.defaultIfEmpty(item.getRegionCode(), "-"), 1L, Long::sum);
                enterpriseRiskCounter.merge(StringUtils.defaultIfEmpty(item.getEnterpriseName(), "-"), 1L, Long::sum);
            }
            if (defaultInt(item.getRemainingDays()) < 0)
            {
                overdueRows.add(item);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("highRiskRegions", toSortedDimensionStats(regionRiskCounter, 10));
        result.put("highRiskEnterprises", toSortedDimensionStats(enterpriseRiskCounter, 10));
        result.put("overdueRows", overdueRows.stream().limit(10).map(this::toMonitorRow).collect(Collectors.toList()));
        result.put("monitorCount", overdueRows.size());
        return result;
    }

    @Override
    public List<YgbModuleRecord> selectRecognitionAssistList(YgbInjuryEvent injuryEvent)
    {
        return selectInjuryEventList(injuryEvent).stream().map(this::toRecognitionAssistRecord).collect(Collectors.toList());
    }

    @Override
    public YgbModuleRecord selectRecognitionAssistById(Long eventId)
    {
        return toRecognitionAssistRecord(selectInjuryEventById(eventId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInjuryEvent(YgbInjuryEvent injuryEvent)
    {
        fillSnapshot(injuryEvent);
        recalculateDeadline(injuryEvent);
        int rows = injuryEventMapper.insertInjuryEvent(injuryEvent);
        createOverdueWarningIfNeeded(injuryEvent, injuryEvent.getCreateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInjuryEvent(YgbInjuryEvent injuryEvent)
    {
        fillSnapshot(injuryEvent);
        recalculateDeadline(injuryEvent);
        int rows = injuryEventMapper.updateInjuryEvent(injuryEvent);
        createOverdueWarningIfNeeded(injuryEvent, injuryEvent.getUpdateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long eventId, String injuryStatus, String approvalResult, String operator)
    {
        YgbInjuryEvent injuryEvent = selectInjuryEventById(eventId);
        injuryEvent.setInjuryStatus(injuryStatus);
        injuryEvent.setApprovalResult(approvalResult);
        injuryEvent.setUpdateBy(operator);
        recalculateDeadline(injuryEvent);
        int rows = injuryEventMapper.updateInjuryEvent(injuryEvent);
        createOverdueWarningIfNeeded(injuryEvent, operator);
        return rows;
    }

    @Override
    public int deleteInjuryEventByIds(Long[] eventIds, String updateBy)
    {
        return injuryEventMapper.deleteInjuryEventByIds(eventIds, updateBy);
    }

    private void fillSnapshot(YgbInjuryEvent injuryEvent)
    {
        YgbPerson person = personMapper.selectPersonById(injuryEvent.getPersonId());
        if (person == null)
        {
            throw new ServiceException("工伤人员不存在");
        }

        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(injuryEvent.getEnterpriseId());
        if (enterprise == null)
        {
            throw new ServiceException("工伤所属企业不存在");
        }

        injuryEvent.setPersonName(person.getPersonName());
        injuryEvent.setEnterpriseName(enterprise.getEnterpriseName());
        injuryEvent.setRegionCode(enterprise.getRegionCode());
        if (injuryEvent.getReportTime() == null)
        {
            injuryEvent.setReportTime(new Date());
        }
        if (injuryEvent.getInjuryStatus() == null)
        {
            injuryEvent.setInjuryStatus("0");
        }
    }

    private void recalculateDeadline(YgbInjuryEvent injuryEvent)
    {
        if (injuryEvent.getApprovalDeadline() == null)
        {
            injuryEvent.setRemainingDays(0);
            injuryEvent.setWarningStatus("0");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate deadline = injuryEvent.getApprovalDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long remaining = ChronoUnit.DAYS.between(today, deadline);
        injuryEvent.setRemainingDays((int) remaining);
        injuryEvent.setWarningStatus(remaining < 0 && !"4".equals(injuryEvent.getInjuryStatus()) ? "1" : "0");
    }

    private void createOverdueWarningIfNeeded(YgbInjuryEvent injuryEvent, String operator)
    {
        if (!"1".equals(injuryEvent.getWarningStatus()) || injuryEvent.getEventId() == null)
        {
            return;
        }

        YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
        warning.setWarnLevel("2");
        warning.setWarnType("INJURY_OVERDUE");
        warning.setSourceModule("INJURY");
        warning.setTargetObjectId(injuryEvent.getEventId());
        warning.setTargetType("3");
        warning.setEnterpriseId(injuryEvent.getEnterpriseId());
        warning.setEnterpriseName(injuryEvent.getEnterpriseName());
        warning.setRegionCode(injuryEvent.getRegionCode());
        warning.setContent("工伤事件超期未处理，人员：" + injuryEvent.getPersonName());
        warningService.createWarningIfAbsent(warning, operator == null ? "system" : operator);
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private List<Map<String, Object>> toDimensionStats(Map<String, Long> counter)
    {
        return counter.entrySet().stream().map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("dimension", entry.getKey());
            row.put("count", entry.getValue());
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> toSortedDimensionStats(Map<String, Long> counter, int limit)
    {
        return counter.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .map(entry -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("dimension", entry.getKey());
                row.put("count", entry.getValue());
                return row;
            }).collect(Collectors.toList());
    }

    private Map<String, Object> toMonitorRow(YgbInjuryEvent injuryEvent)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("eventId", injuryEvent.getEventId());
        row.put("personName", injuryEvent.getPersonName());
        row.put("enterpriseName", injuryEvent.getEnterpriseName());
        row.put("regionCode", injuryEvent.getRegionCode());
        row.put("injuryStatus", injuryEvent.getInjuryStatus());
        row.put("warningStatus", injuryEvent.getWarningStatus());
        row.put("remainingDays", injuryEvent.getRemainingDays());
        return row;
    }

    private YgbModuleRecord toRecognitionAssistRecord(YgbInjuryEvent injuryEvent)
    {
        YgbModuleRecord record = new YgbModuleRecord();
        record.setRecordId(injuryEvent.getEventId());
        record.setRecordType("INJURY_RECOGNITION_ASSIST");
        record.setRecordName(StringUtils.defaultIfEmpty(injuryEvent.getPersonName(), "工伤认定辅助"));
        record.setCategoryCode(StringUtils.defaultIfEmpty(injuryEvent.getInjuryStatus(), "0"));
        record.setRegionCode(injuryEvent.getRegionCode());
        record.setEnterpriseId(injuryEvent.getEnterpriseId());
        record.setEnterpriseName(injuryEvent.getEnterpriseName());
        record.setPersonId(injuryEvent.getPersonId());
        record.setPersonName(injuryEvent.getPersonName());
        record.setRelatedId(injuryEvent.getEventId());
        record.setRelatedCode("injuryEvent");
        record.setWorkflowStatus(resolveRecognitionWorkflowStatus(injuryEvent));
        record.setStatus("0");
        record.setSourceLabel("工伤事件");
        record.setRemark(buildRecognitionRemark(injuryEvent));
        return record;
    }

    private String resolveRecognitionWorkflowStatus(YgbInjuryEvent injuryEvent)
    {
        if (defaultInt(injuryEvent.getRemainingDays()) < 0)
        {
            return "overdue";
        }
        if ("4".equals(injuryEvent.getInjuryStatus()))
        {
            return "closed";
        }
        if ("1".equals(injuryEvent.getInjuryStatus()) || "3".equals(injuryEvent.getInjuryStatus()))
        {
            return "processing";
        }
        return "pending";
    }

    private String buildRecognitionRemark(YgbInjuryEvent injuryEvent)
    {
        List<String> remarks = new ArrayList<>();
        if (StringUtils.isEmpty(injuryEvent.getDiagnosisUrl()))
        {
            remarks.add("缺少诊断证明");
        }
        if (StringUtils.isEmpty(injuryEvent.getApprovalResult()) && !"0".equals(injuryEvent.getInjuryStatus()))
        {
            remarks.add("缺少审批结果");
        }
        if (defaultInt(injuryEvent.getRemainingDays()) < 0)
        {
            remarks.add("已超时未办");
        }
        if (remarks.isEmpty())
        {
            remarks.add("材料基本完整，可继续认定流转");
        }
        return String.join("；", remarks);
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbInjuryEvent query, YgbInjuryEventSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("warning", "预警工伤事件", summary.getWarningCount(), 0,
            "已触发预警的工伤事件应优先回看认定、申领和材料补充进度，避免办理链超时。", "injuryEvent",
            "injuryEvent", "530.1 工伤办理解释", baseQuery));
        list.add(explanationItem("overdue", "超时未办事件", summary.getOverdueCount(), 0,
            "超时事件会直接影响工伤办理闭环，应优先推进责任人和时限回写。", "injuryEvent", "injuryEvent",
            "530.1 工伤办理解释", baseQuery));
        list.add(explanationItem("recognizing", "认定处理中事件", summary.getRecognizingCount(), 0,
            "认定处理中事件适合作为工伤办理主链的跟踪入口。", "injuryEvent", "injuryEvent", "530.1 工伤办理解释",
            baseQuery));
        list.add(explanationItem("claiming", "待遇申领中事件", summary.getClaimingCount(), 0,
            "待遇申领中的事件需要持续补齐材料与结果回写。", "injuryEvent", "injuryEvent", "530.1 工伤办理解释",
            baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbInjuryEvent query, YgbInjuryEventSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("warning", "事故预警对象", summary.getWarningCount(), 0,
            "工伤预警对象更适合作为区域事故治理和风险压降的观察入口。", "injuryEvent", "injuryEvent",
            "6.1 工伤治理解释", baseQuery));
        list.add(explanationItem("overdue", "超时治理对象", summary.getOverdueCount(), 0,
            "超时未办说明事故处置与治理链条存在明显堵点，应优先督办。", "injuryEvent", "injuryEvent",
            "6.1 工伤治理解释", baseQuery));
        list.add(explanationItem("recognizing", "认定中对象", summary.getRecognizingCount(), 0,
            "认定中对象用于观察当前事故治理链路是否顺畅。", "injuryEvent", "injuryEvent", "6.1 工伤治理解释",
            baseQuery));
        list.add(explanationItem("claiming", "待遇申领对象", summary.getClaimingCount(), 0,
            "待遇申领对象适合作为事故后续协同处理的主入口。", "injuryEvent", "injuryEvent", "6.1 工伤治理解释",
            baseQuery));
        list.add(explanationItem("finished", "已办结复盘对象", summary.getFinishedCount(), "持续复盘",
            "已办结对象用于复盘区域事故治理成效和时效。", "injuryEvent", "injuryEvent", "6.1 工伤治理解释",
            baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbInjuryEvent query)
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
        if (StringUtils.isNotEmpty(query.getEnterpriseName()))
        {
            map.put("enterpriseName", query.getEnterpriseName());
        }
        if (StringUtils.isNotEmpty(query.getInjuryStatus()))
        {
            map.put("injuryStatus", query.getInjuryStatus());
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
