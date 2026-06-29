package com.yuegongbao.ygb.newform.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.domain.vo.YgbNewformWorkerStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.NewformClient;
import com.yuegongbao.ygb.newform.domain.YgbNewformPlatformStat;
import com.yuegongbao.ygb.newform.domain.YgbNewformPlatformSummary;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorkerSummary;
import com.yuegongbao.ygb.newform.mapper.YgbNewformWorkerMapper;
import com.yuegongbao.ygb.newform.service.IYgbNewformWorkerService;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbNewformWorkerServiceImpl implements IYgbNewformWorkerService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");
    private static final String STATUS_UNINSURED = "0";
    private static final String STATUS_INSURED = "1";
    private static final String STATUS_STOPPED = "2";
    private static final String WARNING_NONE = "0";
    private static final String WARNING_EXISTS = "1";

    @Autowired
    private YgbNewformWorkerMapper newformWorkerMapper;

    @Autowired
    private NewformClient newformClient;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Override
    public List<YgbNewformWorker> selectNewformWorkerList(YgbNewformWorker query)
    {
        YgbNewformWorker scopedQuery = prepareQuery(query);
        List<YgbNewformWorker> list = newformWorkerMapper.selectNewformWorkerList(scopedQuery);
        list.forEach(item -> item.setRegionName(YgbRegionHelper.resolveRegionName(item.getRegionCode())));
        return list;
    }

    @Override
    public YgbNewformWorkerSummary selectNewformWorkerSummary(YgbNewformWorker query)
    {
        List<YgbNewformWorker> list = selectNewformWorkerList(query);
        YgbNewformWorkerSummary summary = new YgbNewformWorkerSummary();
        summary.setTotalCount(list.size());

        int insuredCount = 0;
        int uninsuredCount = 0;
        int stoppedCount = 0;
        int warningCount = 0;
        int incomeAbnormalCount = 0;
        BigDecimal incomeTotal = BigDecimal.ZERO;
        int incomeCount = 0;
        for (YgbNewformWorker item : list)
        {
            if ("1".equals(item.getInjuryInsuranceStatus()))
            {
                insuredCount++;
            }
            else if ("0".equals(item.getInjuryInsuranceStatus()))
            {
                uninsuredCount++;
            }
            else if ("2".equals(item.getInjuryInsuranceStatus()))
            {
                stoppedCount++;
            }

            if ("1".equals(item.getWarningStatus()))
            {
                warningCount++;
            }

            if (item.getMonthlyIncome() != null)
            {
                incomeTotal = incomeTotal.add(item.getMonthlyIncome());
                incomeCount++;
            }
            if (item.getMonthlyIncome() == null || item.getMonthlyIncome().compareTo(BigDecimal.ZERO) <= 0)
            {
                incomeAbnormalCount++;
            }
        }

        summary.setInsuredCount(insuredCount);
        summary.setUninsuredCount(uninsuredCount);
        summary.setStoppedCount(stoppedCount);
        summary.setWarningCount(warningCount);
        summary.setAverageIncome(incomeCount == 0
            ? BigDecimal.ZERO
            : incomeTotal.divide(BigDecimal.valueOf(incomeCount), 2, RoundingMode.HALF_UP));
        summary.setYgbExplanation(buildYgbExplanation(query, summary, incomeAbnormalCount));
        return summary;
    }

    @Override
    public List<YgbNewformPlatformStat> selectNewformPlatformList(YgbNewformWorker query)
    {
        YgbNewformWorker scopedQuery = prepareQuery(query);
        List<YgbNewformPlatformStat> list = newformWorkerMapper.selectNewformPlatformList(scopedQuery);
        list.forEach(item -> item.setRegionName(YgbRegionHelper.resolveRegionName(item.getRegionCode())));
        return list;
    }

    @Override
    public YgbNewformPlatformSummary selectNewformPlatformSummary(YgbNewformWorker query)
    {
        return buildPlatformSummary(selectNewformPlatformList(query), true);
    }

    @Override
    public List<YgbNewformPlatformStat> selectNewformInjuryMonitorList(YgbNewformWorker query)
    {
        YgbNewformWorker scopedQuery = prepareQuery(query);
        List<YgbNewformPlatformStat> list = newformWorkerMapper.selectNewformInjuryMonitorList(scopedQuery);
        list.forEach(item -> item.setRegionName(YgbRegionHelper.resolveRegionName(item.getRegionCode())));
        return list;
    }

    @Override
    public YgbNewformPlatformSummary selectNewformInjuryMonitorSummary(YgbNewformWorker query)
    {
        return buildPlatformSummary(selectNewformInjuryMonitorList(query), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncNewformWorker(String statMonth, Long enterpriseId, String operator)
    {
        validateMonth(statMonth);
        if (enterpriseId != null)
        {
            enterpriseScopeHelper.assertEnterpriseAuthorized(enterpriseId);
        }
        YgbNewformWorker deleteScope = buildScopedWorkerQuery(statMonth, enterpriseId);
        newformWorkerMapper.deleteByScope(deleteScope);
        List<YgbNewformWorkerStubItem> records = newformClient.pullWorkerRecords(statMonth);

        int rows = 0;
        for (YgbNewformWorkerStubItem item : records)
        {
            if (!isItemInScope(item, enterpriseId))
            {
                continue;
            }

            YgbNewformWorker worker = new YgbNewformWorker();
            worker.setStatMonth(statMonth);
            worker.setEnterpriseId(item.getEnterpriseId());
            worker.setEnterpriseName(item.getEnterpriseName());
            worker.setPersonId(item.getPersonId());
            worker.setPersonName(item.getPersonName());
            worker.setIdCard(item.getIdCard());
            worker.setRegionCode(item.getRegionCode());
            worker.setPlatformName(item.getPlatformName());
            worker.setEmploymentType(item.getEmploymentType());
            worker.setInsuranceStatus(item.getInsuranceStatus());
            worker.setInjuryInsuranceStatus(item.getInjuryInsuranceStatus());
            worker.setMonthlyIncome(item.getMonthlyIncome());
            worker.setWarningStatus("1".equals(item.getInjuryInsuranceStatus()) ? "0" : "1");
            worker.setSourceSerialNo(item.getExternalSerialNo());
            worker.setSourceStatus(item.getSourceStatus());
            worker.setSourceMessage(item.getSourceMessage());
            worker.setCallbackTime(item.getCallbackTime());
            worker.setRawPayload(item.getRawPayload());
            worker.setCreateBy(operator);
            newformWorkerMapper.insertNewformWorker(worker);
            rows++;

            if (!"1".equals(item.getInjuryInsuranceStatus()))
            {
                YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
                warning.setWarnLevel("2".equals(item.getInjuryInsuranceStatus()) ? "2" : "3");
                warning.setWarnType("NEWFORM_INJURY_INSURANCE");
                warning.setSourceModule("NEWFORM");
                warning.setTargetObjectId(item.getPersonId());
                warning.setTargetType("3");
                warning.setEnterpriseId(item.getEnterpriseId());
                warning.setEnterpriseName(item.getEnterpriseName());
                warning.setRegionCode(item.getRegionCode());
                warning.setContent("新业态人员职业伤害参保异常，人员：" + item.getPersonName()
                    + "，平台：" + item.getPlatformName() + "。");
                warningService.createWarningIfAbsent(warning, operator);
            }
        }
        return rows;
    }

    private YgbNewformWorker buildScopedWorkerQuery(String statMonth, Long enterpriseId)
    {
        YgbNewformWorker query = new YgbNewformWorker();
        query.setStatMonth(statMonth);
        query.setEnterpriseId(enterpriseId);
        applyDataScope(query);
        return query;
    }

    private YgbNewformWorker prepareQuery(YgbNewformWorker query)
    {
        YgbNewformWorker scopedQuery = query == null ? new YgbNewformWorker() : query;
        validateQuery(scopedQuery);
        applyDataScope(scopedQuery);
        return scopedQuery;
    }

    private void applyDataScope(YgbNewformWorker query)
    {
        if (enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            enterpriseScopeHelper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "enterprise_id");
            return;
        }
        regionScopeHelper.applyRegionDataScope(query, "region_code");
    }

    private void validateQuery(YgbNewformWorker query)
    {
        if (query == null)
        {
            return;
        }
        if (StringUtils.isNotEmpty(query.getStatMonth()))
        {
            validateMonth(query.getStatMonth());
        }
        validateStatus(query.getInsuranceStatus(), "参保状态", STATUS_UNINSURED, STATUS_INSURED, STATUS_STOPPED);
        validateStatus(query.getInjuryInsuranceStatus(), "职业伤害参保状态", STATUS_UNINSURED, STATUS_INSURED,
            STATUS_STOPPED);
        validateStatus(query.getWarningStatus(), "预警状态", WARNING_NONE, WARNING_EXISTS);
    }

    private boolean isItemInScope(YgbNewformWorkerStubItem item, Long requestedEnterpriseId)
    {
        if (item == null)
        {
            return false;
        }
        if (requestedEnterpriseId != null && !requestedEnterpriseId.equals(item.getEnterpriseId()))
        {
            return false;
        }
        try
        {
            regionScopeHelper.assertRegionAuthorized(item.getRegionCode());
            enterpriseScopeHelper.assertEnterpriseAuthorized(item.getEnterpriseId());
            return true;
        }
        catch (ServiceException e)
        {
            return false;
        }
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }

    private void validateStatus(String value, String label, String... allowedValues)
    {
        if (StringUtils.isEmpty(value))
        {
            return;
        }
        for (String allowedValue : allowedValues)
        {
            if (allowedValue.equals(value))
            {
                return;
            }
        }
        throw new ServiceException(label + "不合法。");
    }

    private YgbNewformPlatformSummary buildPlatformSummary(List<YgbNewformPlatformStat> list, boolean includeAverageIncome)
    {
        List<YgbNewformPlatformStat> safeList = list == null ? Collections.emptyList() : list;
        YgbNewformPlatformSummary summary = new YgbNewformPlatformSummary();
        summary.setPlatformCount(safeList.size());

        int workerCount = 0;
        int insuredCount = 0;
        int injuryInsuredCount = 0;
        int injuryUninsuredCount = 0;
        int injuryStoppedCount = 0;
        int warningCount = 0;
        BigDecimal incomeTotal = BigDecimal.ZERO;
        int incomeRecordCount = 0;
        for (YgbNewformPlatformStat item : safeList)
        {
            workerCount += safeInt(item.getWorkerCount());
            insuredCount += safeInt(item.getInsuredCount());
            injuryInsuredCount += safeInt(item.getInjuryInsuredCount());
            injuryUninsuredCount += safeInt(item.getInjuryUninsuredCount());
            injuryStoppedCount += safeInt(item.getInjuryStoppedCount());
            warningCount += safeInt(item.getWarningCount());
            incomeTotal = incomeTotal.add(item.getIncomeTotal() == null ? BigDecimal.ZERO : item.getIncomeTotal());
            incomeRecordCount += safeInt(item.getIncomeRecordCount());
        }

        summary.setWorkerCount(workerCount);
        summary.setInsuredCount(insuredCount);
        summary.setInjuryInsuredCount(injuryInsuredCount);
        summary.setInjuryUninsuredCount(injuryUninsuredCount);
        summary.setInjuryStoppedCount(injuryStoppedCount);
        summary.setWarningCount(warningCount);
        if (includeAverageIncome && incomeRecordCount > 0)
        {
            summary.setAverageIncome(incomeTotal.divide(BigDecimal.valueOf(incomeRecordCount), 2, RoundingMode.HALF_UP));
        }
        return summary;
    }

    private int safeInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbNewformWorker query, YgbNewformWorkerSummary summary,
        int incomeAbnormalCount)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("uninsured", "未参保对象", summary.getUninsuredCount(), 0,
            "优先核对平台归属、从业类型和参保说明，避免未参保对象继续积压。", "newformWorker",
            "newformWorker", "530.1 新业态办理解释", baseQuery));
        list.add(explanationItem("stopped", "停保对象", summary.getStoppedCount(), 0,
            "停保对象应先确认停保原因与时间，再决定是否恢复保障或补录办理说明。", "newformWorker",
            "newformWorker", "530.1 新业态办理解释", baseQuery));
        list.add(explanationItem("warning", "预警对象", summary.getWarningCount(), 0,
            "已触发预警的对象应继续承接到后续处置闭环，不停留在同步结果页。", "newformWorker",
            "newformWorker", "530.1 新业态办理解释", baseQuery));
        list.add(explanationItem("income", "收入异常对象", incomeAbnormalCount, 0,
            "收入回写为 0 或缺失的对象应先修正来源口径，再继续月度归档。", "newformWorker",
            "newformWorker", "530.1 新业态办理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbNewformWorker query)
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
        if (StringUtils.isNotEmpty(query.getPersonName()))
        {
            map.put("personName", query.getPersonName());
        }
        if (StringUtils.isNotEmpty(query.getPlatformName()))
        {
            map.put("platformName", query.getPlatformName());
        }
        if (StringUtils.isNotEmpty(query.getEmploymentType()))
        {
            map.put("employmentType", query.getEmploymentType());
        }
        if (StringUtils.isNotEmpty(query.getInjuryInsuranceStatus()))
        {
            map.put("injuryInsuranceStatus", query.getInjuryInsuranceStatus());
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
