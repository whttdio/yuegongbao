package com.yuegongbao.ygb.aqins.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.aqins.mapper.YgbPreventionFundMapper;
import com.yuegongbao.ygb.aqins.service.IYgbAqInsuranceService;
import com.yuegongbao.ygb.domain.vo.YgbAqInsuranceStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.AqInsuranceClient;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbAqInsuranceServiceImpl implements IYgbAqInsuranceService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Autowired
    private YgbPreventionFundMapper preventionFundMapper;

    @Autowired
    private AqInsuranceClient aqInsuranceClient;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbAqInsurance> selectAqInsuranceList(YgbAqInsurance aqInsurance)
    {
        return aqInsuranceMapper.selectAqInsuranceList(aqInsurance);
    }

    @Override
    public YgbAqInsuranceSummary selectAqInsuranceSummary(YgbAqInsurance aqInsurance)
    {
        List<YgbAqInsurance> list = selectAqInsuranceList(aqInsurance);
        YgbAqInsuranceSummary summary = new YgbAqInsuranceSummary();
        summary.setTotalCount(list.size());

        int validCount = 0;
        int riskCount = 0;
        int soonExpireCount = 0;
        int expiredCount = 0;
        BigDecimal remainingFundAmountTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        Set<String> insurerNames = new HashSet<>();
        for (YgbAqInsurance item : list)
        {
            if ("1".equals(item.getPolicyStatus()))
            {
                validCount++;
            }
            if ("2".equals(item.getPolicyStatus()))
            {
                riskCount++;
                soonExpireCount++;
            }
            if ("3".equals(item.getPolicyStatus()))
            {
                riskCount++;
                expiredCount++;
            }
            remainingFundAmountTotal = remainingFundAmountTotal.add(defaultAmount(item.getRemainingFundAmount()));
            if (StringUtils.isNotEmpty(item.getInsurerName()))
            {
                insurerNames.add(item.getInsurerName());
            }
        }

        summary.setValidCount(validCount);
        summary.setRiskCount(riskCount);
        summary.setSoonExpireCount(soonExpireCount);
        summary.setExpiredCount(expiredCount);
        summary.setRemainingFundAmountTotal(remainingFundAmountTotal);
        summary.setInsurerCount(insurerNames.size());
        if (list.isEmpty())
        {
            summary.setCoverageRate(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP));
        }
        else
        {
            summary.setCoverageRate(BigDecimal.valueOf(validCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(list.size()), 1, RoundingMode.HALF_UP));
        }
        summary.setYgbExplanation(buildYgbExplanation(aqInsurance, summary));
        summary.setAzbExplanation(buildAzbExplanation(aqInsurance, summary));
        return summary;
    }

    @Override
    public YgbAqInsurance selectAqInsuranceById(Long policyId)
    {
        YgbAqInsurance aqInsurance = aqInsuranceMapper.selectAqInsuranceById(policyId);
        if (aqInsurance != null)
        {
            dataScopeGuard.assertEntityAllowed(aqInsurance);
        }
        return aqInsurance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncAqInsurance(String statMonth, Long enterpriseId, String operator)
    {
        validateMonth(statMonth);
        if (enterpriseId != null)
        {
            enterpriseScopeHelper.assertEnterpriseAuthorized(enterpriseId);
        }
        List<YgbAqInsuranceStubItem> items = aqInsuranceClient.pullMonthlyPolicies(statMonth);
        int rows = 0;
        for (YgbAqInsuranceStubItem item : items)
        {
            if (!isItemInScope(item, enterpriseId))
            {
                continue;
            }
            BigDecimal preventionFundAmount = defaultAmount(item.getPreventionFundAmount());
            YgbAqInsurance existed = aqInsuranceMapper.selectAqInsuranceByScope(statMonth, item.getEnterpriseId());
            BigDecimal usedAmount = existed == null || existed.getUsedFundAmount() == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : existed.getUsedFundAmount();
            BigDecimal remainingAmount = preventionFundAmount.subtract(usedAmount).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

            YgbAqInsurance record = existed == null ? new YgbAqInsurance() : existed;
            record.setStatMonth(statMonth);
            record.setEnterpriseId(item.getEnterpriseId());
            record.setEnterpriseName(item.getEnterpriseName());
            record.setRegionCode(item.getRegionCode());
            record.setInsurerName(item.getInsurerName());
            record.setPolicyNo(item.getPolicyNo());
            record.setPremium(item.getPremium());
            record.setStartDate(item.getStartDate());
            record.setEndDate(item.getEndDate());
            record.setPolicyStatus(resolvePolicyStatus(item.getPolicyStatus(), item.getStartDate(), item.getEndDate()));
            record.setInsuredPersonCount(item.getInsuredPersonCount());
            record.setPreventionFundRatio(item.getPreventionFundRatio());
            record.setPreventionFundAmount(preventionFundAmount);
            record.setUsedFundAmount(usedAmount);
            record.setRemainingFundAmount(remainingAmount);
            record.setExpireInDays(resolveExpireInDays(item.getEndDate()));
            record.setSourceSerialNo(item.getExternalSerialNo());
            record.setSourceStatus(item.getSourceStatus());
            record.setSourceMessage(item.getSourceMessage());
            record.setCallbackTime(item.getCallbackTime());
            record.setRawPayload(item.getRawPayload());

            if (existed == null)
            {
                record.setCreateBy(operator);
                aqInsuranceMapper.insertAqInsurance(record);
            }
            else
            {
                record.setUpdateBy(operator);
                aqInsuranceMapper.updateAqInsurance(record);
            }

            upsertPreventionFund(record, operator);
            createPolicyWarning(record, operator);
            rows++;
        }
        return rows;
    }

    private boolean isItemInScope(YgbAqInsuranceStubItem item, Long requestedEnterpriseId)
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

    private void upsertPreventionFund(YgbAqInsurance record, String operator)
    {
        YgbPreventionFund existedFund = preventionFundMapper.selectPreventionFundByScope(record.getPolicyId(),
            record.getStatMonth());
        YgbPreventionFund fund = existedFund == null ? new YgbPreventionFund() : existedFund;
        BigDecimal usedAmount = existedFund == null || existedFund.getUsedAmount() == null
            ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : existedFund.getUsedAmount();
        BigDecimal remainingAmount = record.getPreventionFundAmount().subtract(usedAmount).max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);

        fund.setPolicyId(record.getPolicyId());
        fund.setStatMonth(record.getStatMonth());
        fund.setEnterpriseId(record.getEnterpriseId());
        fund.setEnterpriseName(record.getEnterpriseName());
        fund.setRegionCode(record.getRegionCode());
        fund.setAccruedAmount(record.getPreventionFundAmount());
        fund.setUsedAmount(usedAmount);
        fund.setRemainingAmount(remainingAmount);
        fund.setFundStatus(resolveFundStatus(usedAmount, remainingAmount));
        fund.setSourceMode("stub");

        if (existedFund == null)
        {
            fund.setCreateBy(operator);
            preventionFundMapper.insertPreventionFund(fund);
        }
        else
        {
            fund.setFundId(existedFund.getFundId());
            fund.setUsagePurpose(existedFund.getUsagePurpose());
            fund.setEvidenceUrl(existedFund.getEvidenceUrl());
            fund.setLastSettleTime(existedFund.getLastSettleTime());
            fund.setUpdateBy(operator);
            preventionFundMapper.updatePreventionFund(fund);
        }
        aqInsuranceMapper.updateFundAmounts(record.getPolicyId(), usedAmount, remainingAmount, operator);
    }

    private void createPolicyWarning(YgbAqInsurance record, String operator)
    {
        if (!"2".equals(record.getPolicyStatus()) && !"3".equals(record.getPolicyStatus()))
        {
            return;
        }
        YgbWarningCreateRequest request = new YgbWarningCreateRequest();
        request.setWarnLevel("3".equals(record.getPolicyStatus()) ? "3" : "2");
        request.setWarnType("3".equals(record.getPolicyStatus()) ? "AQ_POLICY_EXPIRED" : "AQ_POLICY_EXPIRING");
        request.setSourceModule("AQINS");
        request.setTargetObjectId(record.getEnterpriseId());
        request.setTargetType("1");
        request.setEnterpriseId(record.getEnterpriseId());
        request.setEnterpriseName(record.getEnterpriseName());
        request.setRegionCode(record.getRegionCode());
        request.setContent("安责险保单" + ("3".equals(record.getPolicyStatus()) ? "已过期" : "即将到期")
            + "，企业：" + record.getEnterpriseName() + "，保单号：" + record.getPolicyNo()
            + "，止保日期：" + record.getEndDate());
        warningService.createWarningIfAbsent(request, operator);
    }

    static String resolvePolicyStatus(String stubStatus, java.util.Date startDate, java.util.Date endDate)
    {
        LocalDate now = LocalDate.now();
        LocalDate begin = toLocalDate(startDate);
        LocalDate finish = toLocalDate(endDate);
        if (begin != null && begin.isAfter(now))
        {
            return "0";
        }
        if (finish == null)
        {
            return StringUtils.isEmpty(stubStatus) ? "1" : stubStatus;
        }
        long days = ChronoUnit.DAYS.between(now, finish);
        if (days < 0)
        {
            return "3";
        }
        if (days <= 30)
        {
            return "2";
        }
        return "1";
    }

    static Integer resolveExpireInDays(java.util.Date endDate)
    {
        if (endDate == null)
        {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), toLocalDate(endDate));
    }

    static String resolveFundStatus(BigDecimal usedAmount, BigDecimal remainingAmount)
    {
        if (remainingAmount == null || remainingAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "3";
        }
        if (usedAmount != null && usedAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            return "2";
        }
        return "1";
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
        YearMonth.parse(statMonth, MONTH_FORMATTER);
    }

    private BigDecimal defaultAmount(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbAqInsurance query, YgbAqInsuranceSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("risk", "续保风险保单", summary.getRiskCount(), 0,
            "临期与过期保单会直接影响企业投保覆盖连续性，应优先回到投保办理链核查续保衔接。", "aqInsurance", "aqInsurance",
            "530.1 投保续保办理解释", mergeQuery(baseQuery, "policyStatus", "2")));
        list.add(explanationItem("valid", "有效投保覆盖", summary.getValidCount(), "持续稳定",
            "有效保单决定当前企业是否处于稳定保障状态，适合作为办理归档和企业核查的底数。", "aqInsurance", "aqInsurance",
            "530.1 投保续保办理解释", mergeQuery(baseQuery, "policyStatus", "1")));
        list.add(explanationItem("remaining", "预防费余额", summary.getRemainingFundAmountTotal(), "-",
            "预防费余额应与月度投保、服务留痕和回写结果保持一致，避免台账与资金记录脱节。", "aqInsurance", "aqInsurance",
            "530.1 投保续保办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbAqInsurance query, YgbAqInsuranceSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("risk", "到期风险对象", summary.getRiskCount(), 0,
            "到期风险保单会降低区域安责险治理稳定度，需优先进入监管与协同复核链路。", "aqInsurance", "aqInsurance",
            "6.1 安责险治理解释", mergeQuery(baseQuery, "policyStatus", "2")));
        list.add(explanationItem("coverage", "安责险覆盖率", summary.getCoverageRate(), "100",
            "覆盖率用于衡量当前区域投保治理成效，覆盖率偏低时应优先回看重点企业和治理缺口。", "aqInsurance", "aqInsurance",
            "6.1 安责险治理解释", baseQuery));
        list.add(explanationItem("soon", "即将到期保单", summary.getSoonExpireCount(), 0,
            "即将到期保单适合作为监管提醒与保险协同的主入口，防止风险对象滑入过期状态。", "aqInsurance", "aqInsurance",
            "6.1 安责险治理解释", mergeQuery(baseQuery, "policyStatus", "2")));
        list.add(explanationItem("remaining", "预防费协同余额", summary.getRemainingFundAmountTotal(), "-",
            "预防费余额反映后续事故预防服务和治理投入空间，应结合回写结果持续复核。", "aqInsurance", "aqInsurance",
            "6.1 安责险治理解释", baseQuery));
        list.add(explanationItem("insurer", "保险机构分布", summary.getInsurerCount(), "持续覆盖",
            "保险机构分布决定当前区域协同复杂度，适合作为安责险治理侧的复核入口。", "aqInsurance", "aqInsurance",
            "6.1 安责险治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbAqInsurance query)
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
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getEnterpriseName()))
        {
            map.put("enterpriseName", query.getEnterpriseName());
        }
        if (StringUtils.isNotEmpty(query.getPolicyStatus()))
        {
            map.put("policyStatus", query.getPolicyStatus());
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

    private static LocalDate toLocalDate(java.util.Date date)
    {
        return date == null ? null : new java.sql.Date(date.getTime()).toLocalDate();
    }
}
