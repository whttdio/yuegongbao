package com.yuegongbao.ygb.credit.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreGenerateRequest;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary;
import com.yuegongbao.ygb.credit.mapper.YgbCreditScoreMapper;
import com.yuegongbao.ygb.credit.service.IYgbCreditScoreService;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

/**
 * 企业信用评分Service实现。
 *
 * @author yuegongbao
 */
@Service
public class YgbCreditScoreServiceImpl implements IYgbCreditScoreService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal LEVEL_A_LINE = new BigDecimal("90");
    private static final BigDecimal LEVEL_B_LINE = new BigDecimal("80");
    private static final BigDecimal LEVEL_C_LINE = new BigDecimal("70");

    @Autowired
    private YgbCreditScoreMapper creditScoreMapper;

    @Autowired
    private IYgbEnterpriseService enterpriseService;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Autowired
    private com.yuegongbao.ygb.util.YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbCreditScore> selectCreditScoreList(YgbCreditScore creditScore)
    {
        List<YgbCreditScore> list = creditScoreMapper.selectCreditScoreList(creditScore);
        list.forEach(this::hydrateRegionName);
        return list;
    }

    @Override
    public YgbCreditScoreSummary selectCreditScoreSummary(YgbCreditScore creditScore)
    {
        List<YgbCreditScore> list = selectCreditScoreList(creditScore);
        YgbCreditScoreSummary summary = new YgbCreditScoreSummary();
        summary.setTotalCount(list.size());

        BigDecimal totalScore = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        int redCount = 0;
        int highGradeCount = 0;
        int dCount = 0;
        int yellowCount = 0;
        for (YgbCreditScore item : list)
        {
            totalScore = totalScore.add(nvl(item.getTotalScore()));
            if ("RED".equals(item.getColorCode()))
            {
                redCount++;
            }
            if ("A".equals(item.getCreditLevel()) || "B".equals(item.getCreditLevel()))
            {
                highGradeCount++;
            }
            if ("D".equals(item.getCreditLevel()))
            {
                dCount++;
            }
            if ("YELLOW".equals(item.getColorCode()))
            {
                yellowCount++;
            }
        }

        summary.setAverageScore(list.isEmpty() ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : totalScore.divide(new BigDecimal(list.size()), 2, RoundingMode.HALF_UP));
        summary.setRedCount(redCount);
        summary.setHighGradeCount(highGradeCount);
        summary.setDCount(dCount);
        summary.setYellowCount(yellowCount);
        summary.setYgbExplanation(buildYgbExplanation(creditScore, summary));
        summary.setAzbExplanation(buildAzbExplanation(creditScore, summary));
        return summary;
    }

    @Override
    public YgbCreditScore selectCreditScoreById(Long scoreId)
    {
        YgbCreditScore score = creditScoreMapper.selectCreditScoreById(scoreId);
        if (score == null)
        {
            throw new ServiceException("信用评分记录不存在。");
        }
        hydrateRegionName(score);
        dataScopeGuard.assertEntityAllowed(score);
        return score;
    }

    @Override
    public YgbCreditScore selectLatestCreditScore(Long enterpriseId)
    {
        YgbCreditScore score = creditScoreMapper.selectLatestCreditScoreByEnterpriseId(enterpriseId);
        if (score != null)
        {
            hydrateRegionName(score);
        }
        return score;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateCreditScores(YgbCreditScoreGenerateRequest request, String operator)
    {
        String statMonth = normalizeMonth(request.getStatMonth());
        String regionCode = regionScopeHelper.resolveAuthorizedRegionCode(request.getRegionCode());
        if (request.getEnterpriseId() != null)
        {
            enterpriseScopeHelper.assertEnterpriseAuthorized(request.getEnterpriseId());
        }
        Long scopedEnterpriseId = enterpriseScopeHelper.isEnterpriseScopedUser()
            ? enterpriseScopeHelper.resolveScopedEnterpriseId()
            : null;
        Long targetEnterpriseId = scopedEnterpriseId == null ? request.getEnterpriseId() : scopedEnterpriseId;
        YearMonth month = YearMonth.parse(statMonth, MONTH_FORMATTER);
        Date periodStart = java.sql.Date.valueOf(month.atDay(1));
        Date periodEnd = java.sql.Date.valueOf(month.atEndOfMonth());
        String regionPrefix = YgbRegionHelper.toRegionPrefix(regionCode);

        List<YgbEnterprise> enterprises = enterpriseService.selectEnterpriseOptions().stream()
            .filter(enterprise -> !"4".equals(enterprise.getEnterpriseType()))
            .filter(enterprise -> targetEnterpriseId == null
                || targetEnterpriseId.equals(enterprise.getEnterpriseId()))
            .filter(enterprise -> StringUtils.isEmpty(regionPrefix)
                || enterprise.getRegionCode().startsWith(regionPrefix))
            .filter(enterprise -> isEnterpriseInScope(enterprise, scopedEnterpriseId))
            .collect(Collectors.toList());
        if (enterprises.isEmpty())
        {
            throw new ServiceException("未找到可评分企业。");
        }

        int rows = 0;
        for (YgbEnterprise enterprise : enterprises)
        {
            ScoreContext context = buildScoreContext(statMonth, periodStart, periodEnd, enterprise);
            upsertCreditScore(context, operator);
            createLowScoreWarningIfNeeded(context, operator);
            rows++;
        }
        if (scopedEnterpriseId == null)
        {
            refreshRanks(statMonth, operator);
        }
        return rows;
    }

    private boolean isEnterpriseInScope(YgbEnterprise enterprise, Long scopedEnterpriseId)
    {
        if (enterprise == null)
        {
            return false;
        }
        if (scopedEnterpriseId != null && !scopedEnterpriseId.equals(enterprise.getEnterpriseId()))
        {
            return false;
        }
        try
        {
            dataScopeGuard.assertEntityAllowed(enterprise);
            return true;
        }
        catch (ServiceException e)
        {
            return false;
        }
    }

    private ScoreContext buildScoreContext(String statMonth, Date periodStart, Date periodEnd, YgbEnterprise enterprise)
    {
        ScoreContext context = new ScoreContext();
        context.enterprise = enterprise;
        context.statMonth = statMonth;
        context.periodStart = periodStart;
        context.periodEnd = periodEnd;

        int contractTotal = creditScoreMapper.countContractTotal(enterprise.getEnterpriseId(), periodStart, periodEnd);
        int contractFiled = creditScoreMapper.countContractFiled(enterprise.getEnterpriseId(), periodStart, periodEnd);
        int attendanceTotal = creditScoreMapper.countAttendanceTotal(enterprise.getEnterpriseId(), statMonth);
        int attendancePassed = creditScoreMapper.countAttendancePassed(enterprise.getEnterpriseId(), statMonth);
        int salaryTotal = creditScoreMapper.countSalaryTotal(enterprise.getEnterpriseId(), statMonth);
        int salarySuccess = creditScoreMapper.countSalarySuccess(enterprise.getEnterpriseId(), statMonth);
        int socialTotal = creditScoreMapper.countSocialBaseTotal(enterprise.getEnterpriseId(), statMonth);
        int socialNormal = creditScoreMapper.countSocialBaseNormal(enterprise.getEnterpriseId(), statMonth);
        int taxTotal = creditScoreMapper.countTaxCompareTotal(enterprise.getEnterpriseId(), statMonth);
        int taxNormal = creditScoreMapper.countTaxCompareNormal(enterprise.getEnterpriseId(), statMonth);
        int uninsuredTotal = creditScoreMapper.countUninsuredTotal(enterprise.getEnterpriseId(), statMonth);
        String aqPolicyStatus = creditScoreMapper.selectAqPolicyStatus(enterprise.getEnterpriseId(), statMonth);
        BigDecimal aqRemainingFund = creditScoreMapper.selectAqRemainingFund(enterprise.getEnterpriseId(), statMonth);
        int injuryTotal = creditScoreMapper.countInjuryTotal(enterprise.getEnterpriseId(), periodStart, periodEnd);
        int injuryOverdue = creditScoreMapper.countInjuryOverdue(enterprise.getEnterpriseId());
        int warningTotal = creditScoreMapper.countWarningTotal(enterprise.getEnterpriseId(), periodStart, periodEnd);
        int warningClosed = creditScoreMapper.countWarningClosed(enterprise.getEnterpriseId(), periodStart, periodEnd);
        int activeRedWarning = creditScoreMapper.countActiveRedWarning(enterprise.getEnterpriseId());

        BigDecimal contractRate = rate(contractFiled, contractTotal);
        BigDecimal attendanceRate = rate(attendancePassed, attendanceTotal);
        BigDecimal salaryRate = rate(salarySuccess, salaryTotal);
        BigDecimal socialRate = rate(socialNormal, socialTotal);
        BigDecimal taxRate = rate(taxNormal, taxTotal);
        BigDecimal warningCloseRate = rate(warningClosed, warningTotal);

        context.contractScore = rateToScore(contractRate, new BigDecimal("100"), new BigDecimal("65"));
        context.attendanceScore = rateToScore(attendanceRate, new BigDecimal("95"), new BigDecimal("60"));
        context.salaryScore = rateToScore(salaryRate, new BigDecimal("98"), new BigDecimal("60"));
        context.socialTaxScore = calculateSocialTaxScore(socialRate, socialTotal, taxRate, taxTotal, uninsuredTotal);
        context.safetyScore = calculateSafetyScore(aqPolicyStatus, aqRemainingFund, injuryTotal, injuryOverdue);
        context.governanceScore = calculateGovernanceScore(warningCloseRate, warningTotal, activeRedWarning);
        context.totalScore = weightedAverage(context);
        context.creditLevel = resolveLevel(context.totalScore);
        context.colorCode = resolveColor(context.creditLevel);
        context.warningStatus = context.totalScore.compareTo(LEVEL_C_LINE) < 0 ? "1" : "0";
        context.summaryText = buildSummary(context);
        context.factorJson = buildFactorJson(context, contractFiled, contractTotal, attendancePassed, attendanceTotal,
            salarySuccess, salaryTotal, socialNormal, socialTotal, taxNormal, taxTotal, uninsuredTotal, aqPolicyStatus,
            aqRemainingFund, injuryTotal, injuryOverdue, warningClosed, warningTotal, activeRedWarning);
        return context;
    }

    private void upsertCreditScore(ScoreContext context, String operator)
    {
        YgbCreditScore existed = creditScoreMapper.selectCreditScoreByScope(context.statMonth,
            context.enterprise.getEnterpriseId());
        YgbCreditScore score = existed == null ? new YgbCreditScore() : existed;
        score.setStatMonth(context.statMonth);
        score.setEnterpriseId(context.enterprise.getEnterpriseId());
        score.setEnterpriseName(context.enterprise.getEnterpriseName());
        score.setRegionCode(context.enterprise.getRegionCode());
        score.setEnterpriseType(context.enterprise.getEnterpriseType());
        score.setContractScore(scale(context.contractScore));
        score.setAttendanceScore(scale(context.attendanceScore));
        score.setSalaryScore(scale(context.salaryScore));
        score.setSocialTaxScore(scale(context.socialTaxScore));
        score.setSafetyScore(scale(context.safetyScore));
        score.setGovernanceScore(scale(context.governanceScore));
        score.setTotalScore(scale(context.totalScore));
        score.setCreditLevel(context.creditLevel);
        score.setColorCode(context.colorCode);
        score.setFactorJson(context.factorJson);
        score.setSummaryText(context.summaryText);
        score.setWarningStatus(context.warningStatus);
        score.setSourceMode("internal");
        score.setEvaluateTime(new Date());

        if (existed == null)
        {
            score.setCreateBy(operator);
            creditScoreMapper.insertCreditScore(score);
        }
        else
        {
            score.setScoreId(existed.getScoreId());
            score.setUpdateBy(operator);
            creditScoreMapper.updateCreditScore(score);
        }
    }

    private void refreshRanks(String statMonth, String operator)
    {
        List<YgbCreditScore> ranking = creditScoreMapper.selectCreditScoreRanking(statMonth);
        int rankNo = 1;
        for (YgbCreditScore score : ranking)
        {
            creditScoreMapper.updateRankNo(score.getScoreId(), rankNo++, operator);
        }
    }

    private void createLowScoreWarningIfNeeded(ScoreContext context, String operator)
    {
        if (context.totalScore.compareTo(LEVEL_C_LINE) >= 0)
        {
            return;
        }

        YgbWarningCreateRequest request = new YgbWarningCreateRequest();
        request.setWarnLevel(context.totalScore.compareTo(new BigDecimal("60")) < 0 ? "3" : "2");
        request.setWarnType("CREDIT_LOW_SCORE");
        request.setSourceModule("CREDIT");
        request.setTargetObjectId(context.enterprise.getEnterpriseId());
        request.setTargetType("1");
        request.setEnterpriseId(context.enterprise.getEnterpriseId());
        request.setEnterpriseName(context.enterprise.getEnterpriseName());
        request.setRegionCode(context.enterprise.getRegionCode());
        request.setContent("企业信用分偏低，当前得分 " + scale(context.totalScore) + "，等级 "
            + context.creditLevel + "，请重点核查：" + context.summaryText);
        warningService.createWarningIfAbsent(request, operator);
    }

    private String buildSummary(ScoreContext context)
    {
        Map<String, BigDecimal> factors = new LinkedHashMap<>();
        factors.put("合同备案", context.contractScore);
        factors.put("考勤归集", context.attendanceScore);
        factors.put("工资发放", context.salaryScore);
        factors.put("社税合规", context.socialTaxScore);
        factors.put("安全保障", context.safetyScore);
        factors.put("预警治理", context.governanceScore);

        String weakest = factors.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(2)
            .map(Map.Entry::getKey)
            .collect(Collectors.joining("、"));
        return "总分 " + scale(context.totalScore) + "，信用等级 " + context.creditLevel + "，需重点关注 "
            + weakest + "。";
    }

    private String buildFactorJson(ScoreContext context, int contractFiled, int contractTotal, int attendancePassed,
        int attendanceTotal, int salarySuccess, int salaryTotal, int socialNormal, int socialTotal, int taxNormal,
        int taxTotal, int uninsuredTotal, String aqPolicyStatus, BigDecimal aqRemainingFund, int injuryTotal,
        int injuryOverdue, int warningClosed, int warningTotal, int activeRedWarning)
    {
        Map<String, Object> factors = new LinkedHashMap<>();
        factors.put("contract", factor("合同备案", context.contractScore, "备案率", rate(contractFiled, contractTotal),
            new BigDecimal("100"), contractFiled, contractTotal, "合同备案率越高，信用稳定性越好。"));
        factors.put("attendance", factor("考勤归集", context.attendanceScore, "校验通过率",
            rate(attendancePassed, attendanceTotal), new BigDecimal("95"), attendancePassed, attendanceTotal,
            "考勤归集异常会直接影响工资和合规核验。"));
        factors.put("salary", factor("工资发放", context.salaryScore, "发放成功率", rate(salarySuccess, salaryTotal),
            new BigDecimal("98"), salarySuccess, salaryTotal, "工资发放成功率越高，劳动纠纷风险越低。"));
        factors.put("socialTax", factor("社税合规", context.socialTaxScore, "综合合规率",
            average(rate(socialNormal, socialTotal), rate(taxNormal, taxTotal)), new BigDecimal("90"),
            socialNormal + taxNormal, socialTotal + taxTotal,
            "同时考虑社保基数、个税申报与漏保清单，漏保每人追加扣分。" + uninsuredTotal));
        factors.put("safety", factor("安全保障", context.safetyScore, "安全保障评分",
            safetyMetric(aqPolicyStatus, injuryTotal, injuryOverdue), new BigDecimal("100"), injuryOverdue, injuryTotal,
            "考虑安责险状态、资金池余额和工伤超期事件。余额：" + scale(nvl(aqRemainingFund)) + "。"));
        factors.put("governance", factor("预警治理", context.governanceScore, "闭环率", rate(warningClosed, warningTotal),
            new BigDecimal("90"), warningClosed, warningTotal, "存在活动红警 " + activeRedWarning + " 条时会扣分。"));
        return JSON.toJSONString(factors);
    }

    private Map<String, Object> factor(String name, BigDecimal score, String metricLabel, BigDecimal metricValue,
        BigDecimal targetValue, int numerator, int denominator, String description)
    {
        Map<String, Object> factor = new LinkedHashMap<>();
        factor.put("name", name);
        factor.put("score", scale(score));
        factor.put("metricLabel", metricLabel);
        factor.put("metricValue", scale(metricValue));
        factor.put("targetValue", scale(targetValue));
        factor.put("numerator", numerator);
        factor.put("denominator", denominator);
        factor.put("description", description);
        return factor;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbCreditScore creditScore, YgbCreditScoreSummary summary)
    {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        Map<String, Object> query = buildExplanationQuery(creditScore);
        list.add(explanationItem("办理合规评分", summary.getAverageScore(), LEVEL_B_LINE,
            "同一企业在 ygb 口径下优先解释办理合规与基础数据质量。", "creditScore", "statReport", "530.1 办理链解释", query));
        list.add(explanationItem("工资社税联动", summary.getYellowCount(), 0,
            "尾部对象继续回到工资、社保、个税办理链和差异回查链复核。", "socialTax", "socialBaseCompare", "530.1 办理链解释", query));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbCreditScore creditScore, YgbCreditScoreSummary summary)
    {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        Map<String, Object> query = buildExplanationQuery(creditScore);
        list.add(explanationItem("风险等级", summary.getAverageScore(), LEVEL_C_LINE,
            "6.1 口径优先解释风险等级和治理压降压力。", "creditScore", "creditScore", "6.1 风险分层解释", query));
        list.add(explanationItem("尾部对象", summary.getDCount(), 0,
            "尾部对象数量直接决定治理链优先级。", "creditScore", "warning", "6.1 风险分层解释", query));
        list.add(explanationItem("分层解释", summary.getRedCount() + summary.getYellowCount(), 0,
            "信用评价页面只使用 azb 风险分层解释，不混用企业办理口径。", "creditScore", "statReport", "6.1 风险分层解释", query));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbCreditScore creditScore)
    {
        Map<String, Object> query = new LinkedHashMap<>();
        if (creditScore == null)
        {
            return query;
        }
        if (StringUtils.isNotEmpty(creditScore.getStatMonth()))
        {
            query.put("statMonth", normalizeMonth(creditScore.getStatMonth()));
        }
        if (creditScore.getEnterpriseId() != null)
        {
            query.put("enterpriseId", creditScore.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(creditScore.getRegionCode()))
        {
            query.put("regionCode", creditScore.getRegionCode());
        }
        return query;
    }

    private Map<String, Object> explanationItem(String dimensionName, Object currentValue, Object targetValue, String summary,
        String evidenceModule, String recommendModule, String sourceLabel, Map<String, Object> defaultQuery)
    {
        Map<String, Object> item = new LinkedHashMap<>();
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

    private void hydrateRegionName(YgbCreditScore score)
    {
        score.setRegionName(YgbRegionHelper.resolveRegionName(score.getRegionCode()));
    }

    static String resolveLevel(BigDecimal totalScore)
    {
        BigDecimal score = nvl(totalScore);
        if (score.compareTo(LEVEL_A_LINE) >= 0)
        {
            return "A";
        }
        if (score.compareTo(LEVEL_B_LINE) >= 0)
        {
            return "B";
        }
        if (score.compareTo(LEVEL_C_LINE) >= 0)
        {
            return "C";
        }
        return "D";
    }

    static String resolveColor(String level)
    {
        if ("A".equals(level) || "B".equals(level))
        {
            return "GREEN";
        }
        if ("C".equals(level))
        {
            return "YELLOW";
        }
        return "RED";
    }

    private static BigDecimal rate(int successCount, int totalCount)
    {
        if (totalCount <= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(successCount).multiply(ONE_HUNDRED)
            .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal rateToScore(BigDecimal actualRate, BigDecimal targetRate, BigDecimal emptyScore)
    {
        if (targetRate == null || targetRate.compareTo(BigDecimal.ZERO) <= 0)
        {
            return ONE_HUNDRED;
        }
        if (actualRate == null || actualRate.compareTo(BigDecimal.ZERO) <= 0)
        {
            return emptyScore;
        }
        BigDecimal score = actualRate.multiply(ONE_HUNDRED)
            .divide(targetRate, 2, RoundingMode.HALF_UP);
        if (score.compareTo(ONE_HUNDRED) > 0)
        {
            return ONE_HUNDRED;
        }
        return score;
    }

    private static BigDecimal calculateSocialTaxScore(BigDecimal socialRate, int socialTotal, BigDecimal taxRate,
        int taxTotal, int uninsuredTotal)
    {
        BigDecimal socialScore = socialTotal > 0 ? rateToScore(socialRate, new BigDecimal("90"), new BigDecimal("75"))
            : new BigDecimal("75");
        BigDecimal taxScore = taxTotal > 0 ? rateToScore(taxRate, new BigDecimal("90"), new BigDecimal("75"))
            : new BigDecimal("75");
        BigDecimal score = average(socialScore, taxScore).subtract(new BigDecimal(Math.min(20, uninsuredTotal * 10)));
        return clamp(score);
    }

    private static BigDecimal calculateSafetyScore(String aqPolicyStatus, BigDecimal aqRemainingFund, int injuryTotal,
        int injuryOverdue)
    {
        BigDecimal score = switch (StringUtils.isEmpty(aqPolicyStatus) ? "" : aqPolicyStatus)
        {
            case "1" -> ONE_HUNDRED;
            case "2" -> new BigDecimal("85");
            case "0" -> new BigDecimal("75");
            case "3" -> new BigDecimal("40");
            default -> new BigDecimal("60");
        };

        if (aqRemainingFund != null && aqRemainingFund.compareTo(new BigDecimal("20")) < 0)
        {
            score = score.subtract(new BigDecimal("5"));
        }
        score = score.subtract(new BigDecimal(Math.min(30, injuryTotal * 8)));
        score = score.subtract(new BigDecimal(Math.min(30, injuryOverdue * 20)));
        return clamp(score);
    }

    private static BigDecimal calculateGovernanceScore(BigDecimal warningCloseRate, int warningTotal, int activeRedWarning)
    {
        BigDecimal score = warningTotal > 0 ? rateToScore(warningCloseRate, new BigDecimal("90"), new BigDecimal("90"))
            : new BigDecimal("90");
        score = score.subtract(new BigDecimal(Math.min(30, activeRedWarning * 15)));
        return clamp(score);
    }

    private static BigDecimal weightedAverage(ScoreContext context)
    {
        BigDecimal weighted = nvl(context.contractScore).multiply(new BigDecimal("20"))
            .add(nvl(context.attendanceScore).multiply(new BigDecimal("15")))
            .add(nvl(context.salaryScore).multiply(new BigDecimal("15")))
            .add(nvl(context.socialTaxScore).multiply(new BigDecimal("20")))
            .add(nvl(context.safetyScore).multiply(new BigDecimal("20")))
            .add(nvl(context.governanceScore).multiply(new BigDecimal("10")));
        return weighted.divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal safetyMetric(String aqPolicyStatus, int injuryTotal, int injuryOverdue)
    {
        BigDecimal statusScore = switch (StringUtils.isEmpty(aqPolicyStatus) ? "" : aqPolicyStatus)
        {
            case "1" -> ONE_HUNDRED;
            case "2" -> new BigDecimal("85");
            case "0" -> new BigDecimal("75");
            case "3" -> new BigDecimal("40");
            default -> new BigDecimal("60");
        };
        return clamp(statusScore.subtract(new BigDecimal(Math.min(25, injuryTotal * 5 + injuryOverdue * 10))));
    }

    private static BigDecimal average(BigDecimal left, BigDecimal right)
    {
        return nvl(left).add(nvl(right)).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal clamp(BigDecimal score)
    {
        BigDecimal value = nvl(score);
        if (value.compareTo(BigDecimal.ZERO) < 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (value.compareTo(ONE_HUNDRED) > 0)
        {
            return ONE_HUNDRED.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nvl(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal scale(BigDecimal value)
    {
        return nvl(value);
    }

    private String normalizeMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
        YearMonth.parse(statMonth, MONTH_FORMATTER);
        return statMonth;
    }

    private static class ScoreContext
    {
        private YgbEnterprise enterprise;
        private String statMonth;
        private Date periodStart;
        private Date periodEnd;
        private BigDecimal contractScore;
        private BigDecimal attendanceScore;
        private BigDecimal salaryScore;
        private BigDecimal socialTaxScore;
        private BigDecimal safetyScore;
        private BigDecimal governanceScore;
        private BigDecimal totalScore;
        private String creditLevel;
        private String colorCode;
        private String factorJson;
        private String summaryText;
        private String warningStatus;
    }
}
