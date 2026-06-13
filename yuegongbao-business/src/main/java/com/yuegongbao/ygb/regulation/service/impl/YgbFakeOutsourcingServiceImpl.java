package com.yuegongbao.ygb.regulation.service.impl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingSummary;
import com.yuegongbao.ygb.domain.vo.YgbFakeOutsourcingAnalyzeRequest;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbFakeOutsourcingRecordMapper;
import com.yuegongbao.ygb.regulation.service.IYgbFakeOutsourcingService;
import com.yuegongbao.ygb.util.YgbRiskCalculator;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbFakeOutsourcingServiceImpl implements IYgbFakeOutsourcingService
{
    @Autowired
    private YgbFakeOutsourcingRecordMapper fakeOutsourcingRecordMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Override
    public List<YgbFakeOutsourcingRecord> selectFakeOutsourcingList(YgbFakeOutsourcingRecord fakeOutsourcingRecord)
    {
        return fakeOutsourcingRecordMapper.selectFakeOutsourcingList(fakeOutsourcingRecord);
    }

    @Override
    public YgbFakeOutsourcingSummary selectFakeOutsourcingSummary(YgbFakeOutsourcingRecord fakeOutsourcingRecord)
    {
        List<YgbFakeOutsourcingRecord> list = selectFakeOutsourcingList(fakeOutsourcingRecord);
        YgbFakeOutsourcingSummary summary = new YgbFakeOutsourcingSummary();
        summary.setTotalCount(list.size());

        int normalCount = 0;
        int suspectedCount = 0;
        int warnedCount = 0;
        int lowScoreCount = 0;
        for (YgbFakeOutsourcingRecord item : list)
        {
            if ("1".equals(item.getSuspectedFlag()))
            {
                suspectedCount++;
            }
            else
            {
                normalCount++;
            }
            if ("1".equals(item.getWarningStatus()))
            {
                warnedCount++;
            }
            if (item.getTotalScore() != null && item.getTotalScore() < 50)
            {
                lowScoreCount++;
            }
        }

        summary.setNormalCount(normalCount);
        summary.setSuspectedCount(suspectedCount);
        summary.setWarnedCount(warnedCount);
        summary.setLowScoreCount(lowScoreCount);
        summary.setYgbExplanation(buildYgbExplanation(fakeOutsourcingRecord, summary));
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long analyze(YgbFakeOutsourcingAnalyzeRequest request, String operator)
    {
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(request.getEnterpriseId());
        if (enterprise == null)
        {
            throw new ServiceException("企业不存在。");
        }

        YgbFakeOutsourcingRecord record = new YgbFakeOutsourcingRecord();
        record.setStatMonth(StringUtils.isEmpty(request.getStatMonth()) ? YearMonth.now().toString() : request.getStatMonth());
        record.setEnterpriseId(enterprise.getEnterpriseId());
        record.setEnterpriseName(enterprise.getEnterpriseName());
        record.setRegionCode(enterprise.getRegionCode());
        record.setAttendanceScore(defaultScore(request.getAttendanceScore(), 55));
        record.setScheduleScore(defaultScore(request.getScheduleScore(), 58));
        record.setRewardScore(defaultScore(request.getRewardScore(), 60));
        record.setTrainingScore(defaultScore(request.getTrainingScore(), 62));

        int totalScore = YgbRiskCalculator.calculateFakeOutsourcingScore(record.getAttendanceScore(),
            record.getScheduleScore(), record.getRewardScore(), record.getTrainingScore());
        record.setTotalScore(totalScore);
        boolean suspected = YgbRiskCalculator.isSuspectedFakeOutsourcing(totalScore);
        record.setSuspectedFlag(suspected ? "1" : "0");
        record.setWarningStatus(suspected ? "1" : "0");
        record.setEvidenceSummary(StringUtils.isEmpty(request.getEvidenceSummary())
            ? "基于考勤、排班、奖惩和培训维度的综合评分结果。"
            : request.getEvidenceSummary());
        record.setRemark(suspected ? "综合评分低于 60，疑似假外包。" : "综合评分正常。");
        record.setCreateBy(operator);
        fakeOutsourcingRecordMapper.insertFakeOutsourcingRecord(record);

        if (suspected)
        {
            YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
            warning.setWarnLevel("2");
            warning.setWarnType("FAKE_OUTSOURCING");
            warning.setSourceModule("SPECIAL");
            warning.setTargetObjectId(enterprise.getEnterpriseId());
            warning.setTargetType("1");
            warning.setEnterpriseId(enterprise.getEnterpriseId());
            warning.setEnterpriseName(enterprise.getEnterpriseName());
            warning.setRegionCode(enterprise.getRegionCode());
            warning.setContent("企业疑似假外包，综合评分：" + totalScore);
            warningService.createWarningIfAbsent(warning, operator);
        }
        return record.getRecordId();
    }

    private Integer defaultScore(Integer value, int defaultValue)
    {
        return value == null ? defaultValue : value;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbFakeOutsourcingRecord query,
        YgbFakeOutsourcingSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("suspected", "疑似假外包对象", summary.getSuspectedCount(), 0,
            "综合分偏低且已命中疑似标记的对象应优先进入复核办理。", "fakeOutsourcing",
            "fakeOutsourcing", "530.1 假外包治理解释", baseQuery));
        list.add(explanationItem("lowScore", "低分对象", summary.getLowScoreCount(), ">= 60",
            "综合分较低但尚未触发疑似标记的对象也需要先回查四维短板。", "fakeOutsourcing",
            "fakeOutsourcing", "530.1 假外包治理解释", baseQuery));
        list.add(explanationItem("warned", "已预警对象", summary.getWarnedCount(), 0,
            "已写入预警中心的对象要继续承接假外包复核和预警闭环。", "fakeOutsourcing",
            "fakeOutsourcing", "530.1 假外包治理解释", baseQuery));
        list.add(explanationItem("all", "当前纳管总量", summary.getTotalCount(), "-",
            "用于统看当前月份和企业范围内的识别复核台账盘子。", "fakeOutsourcing",
            "fakeOutsourcing", "530.1 假外包治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbFakeOutsourcingRecord query)
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
        if (StringUtils.isNotEmpty(query.getSuspectedFlag()))
        {
            map.put("suspectedFlag", query.getSuspectedFlag());
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
