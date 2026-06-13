package com.yuegongbao.ygb.aireport.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aireport.domain.YgbAiReport;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboard;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboardCard;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboardConclusion;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboardRankItem;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboardTrendPoint;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportGenerateRequest;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportItem;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportMapper;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportConfigService;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbAiReportServiceImpl implements IYgbAiReportService
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final List<String> DEFAULT_DIMENSIONS = List.of("A", "B", "C", "D", "E");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Autowired
    private YgbAiReportMapper aiReportMapper;

    @Autowired
    private IYgbAiReportConfigService aiReportConfigService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbAiReport> selectAiReportList(YgbAiReport report)
    {
        List<YgbAiReport> list = aiReportMapper.selectAiReportList(report);
        if (list == null)
        {
            return new ArrayList<>();
        }
        list.forEach(this::hydrateReport);
        return list;
    }

    @Override
    public YgbAiReport selectAiReportById(Long reportId)
    {
        YgbAiReport report = aiReportMapper.selectAiReportById(reportId);
        if (report != null)
        {
            hydrateReport(report);
        }
        return report;
    }

    @Override
    public List<YgbAiReportItem> selectAiReportItems(Long reportId)
    {
        return aiReportMapper.selectAiReportItemList(reportId);
    }

    @Override
    public YgbAiReportDashboard selectDashboard(YgbAiReport report, Long activeReportId)
    {
        List<YgbAiReport> reports = aiReportMapper.selectAiReportList(report);
        if (reports == null)
        {
            reports = new ArrayList<>();
        }
        reports.forEach(this::hydrateReport);
        Map<Long, List<YgbAiReportItem>> itemCache = new LinkedHashMap<>();
        applyDashboardRanking(reports, itemCache);

        YgbAiReportDashboard dashboard = new YgbAiReportDashboard();
        dashboard.setTotalCount((long) reports.size());
        dashboard.setAverageScore(calculateAverageScore(reports));
        dashboard.setHighRiskCount((int) reports.stream()
            .filter(this::isHighRiskReport)
            .count());

        if (reports.isEmpty())
        {
            dashboard.setScoreCards(buildDashboardCards(null, dashboard.getAverageScore(),
                dashboard.getHighRiskCount(), dashboard.getTotalCount()));
            dashboard.setConclusion(buildDashboardConclusion(null, List.of()));
            return dashboard;
        }

        YgbAiReport activeReport = resolveActiveReport(reports, activeReportId);
        List<YgbAiReportItem> activeItems = itemCache.computeIfAbsent(activeReport.getReportId(), this::getSortedItems);
        dashboard.setActiveReport(activeReport);
        dashboard.setActiveItems(activeItems);
        dashboard.setScoreCards(buildDashboardCards(activeReport, dashboard.getAverageScore(),
            dashboard.getHighRiskCount(), dashboard.getTotalCount()));
        dashboard.setConclusion(buildDashboardConclusion(activeReport, activeItems));
        dashboard.setTrendPoints(buildTrendPoints(reports));

        dashboard.setTopRankingList(buildRankingList(sortReportsByScoreDesc(reports).stream()
            .limit(5)
            .collect(Collectors.toList()), itemCache, true));
        dashboard.setBottomRankingList(buildRankingList(sortReportsByScoreAsc(reports).stream()
            .limit(5)
            .collect(Collectors.toList()), itemCache, false));
        dashboard.setHighRiskList(buildRankingList(reports.stream()
            .filter(this::isHighRiskReport)
            .sorted(buildScoreAscComparator())
            .limit(8)
            .collect(Collectors.toList()), itemCache, false));
        dashboard.setYgbExplanation(buildYgbExplanation(activeReport, activeItems, dashboard));
        dashboard.setAzbExplanation(buildAzbExplanation(activeReport, activeItems, dashboard));
        return dashboard;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateReport(YgbAiReportGenerateRequest request, String operator)
    {
        String reportType = normalizeReportType(request.getReportType());
        String regionCode = regionScopeHelper.resolveAuthorizedRegionCode(request.getRegionCode());
        String enterpriseType = normalizeEnterpriseType(request.getEnterpriseType());
        PeriodRange periodRange = resolvePeriodRange(reportType, request.getStartDate(), request.getEndDate());
        List<String> dimensions = normalizeDimensions(request.getDimensions());
        String regionPrefix = YgbRegionHelper.toRegionPrefix(regionCode);
        String startMonth = periodRange.getStart().format(MONTH_FORMATTER);
        String endMonth = periodRange.getEnd().format(MONTH_FORMATTER);

        YgbAiReportConfig config = aiReportConfigService.selectCurrentConfig(regionCode, toDate(periodRange.getEnd()));
        Map<String, BigDecimal> weights = parseDecimalMap(config.getDimensionWeights(),
            YgbAiReportConfigServiceImpl.defaultDimensionWeights());
        Map<String, BigDecimal> targets = parseDecimalMap(config.getTargetValues(),
            YgbAiReportConfigServiceImpl.defaultTargetValues());

        List<YgbAiReportItem> items = buildDimensionItems(regionPrefix, periodRange, startMonth, endMonth, dimensions,
            weights, targets);
        if (items.isEmpty())
        {
            throw new ServiceException("未生成任何评分维度，请检查报告参数。");
        }

        BigDecimal totalScore = calculateTotalScore(items);
        String riskLevel = resolveRiskLevel(totalScore);

        YgbAiReport report = aiReportMapper.selectAiReportByScope(reportType, regionCode, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()), enterpriseType);
        boolean isNew = report == null;
        if (isNew)
        {
            report = new YgbAiReport();
            report.setCreateBy(operator);
            report.setCreateTime(new Date());
        }

        report.setReportType(reportType);
        report.setRegionCode(regionCode);
        report.setPeriodStart(toDate(periodRange.getStart()));
        report.setPeriodEnd(toDate(periodRange.getEnd()));
        report.setEnterpriseType(enterpriseType);
        report.setSelectedDimensions(String.join(",", dimensions));
        report.setTotalScore(totalScore);
        report.setRiskLevel(riskLevel);
        report.setConfigVersion(config.getVersion());
        report.setSourceMode("stub");
        report.setReportPdfUrl(buildPdfUrl(reportType, regionCode, periodRange));
        report.setGeneratedTime(new Date());
        report.setRankingNo(aiReportMapper.countHigherScoreReports(reportType, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()), totalScore, report.getReportId()) + 1);
        report.setReportSummary(buildSummary(items, totalScore, riskLevel, report.getRankingNo()));
        report.setReportHtml(buildHtml(regionCode, reportType, periodRange, totalScore, riskLevel, items));
        report.setUpdateBy(operator);
        report.setUpdateTime(new Date());

        if (isNew)
        {
            aiReportMapper.insertAiReport(report);
        }
        else
        {
            aiReportMapper.updateAiReport(report);
            aiReportMapper.deleteAiReportItemsByReportId(report.getReportId());
        }

        for (YgbAiReportItem item : items)
        {
            item.setReportId(report.getReportId());
            aiReportMapper.insertAiReportItem(item);
        }
        refreshScopeRankings(reportType, toDate(periodRange.getStart()), toDate(periodRange.getEnd()));
        return report.getReportId();
    }

    private void hydrateReport(YgbAiReport report)
    {
        report.setRegionName(YgbRegionHelper.resolveRegionName(report.getRegionCode()));
    }

    private BigDecimal calculateAverageScore(List<YgbAiReport> reports)
    {
        if (reports == null || reports.isEmpty())
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = reports.stream()
            .map(YgbAiReport::getTotalScore)
            .filter(score -> score != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(new BigDecimal(reports.size()), 2, RoundingMode.HALF_UP);
    }

    private YgbAiReport resolveActiveReport(List<YgbAiReport> reports, Long activeReportId)
    {
        if (activeReportId != null)
        {
            for (YgbAiReport report : reports)
            {
                if (activeReportId.equals(report.getReportId()))
                {
                    return report;
                }
            }
        }
        return reports.get(0);
    }

    private List<YgbAiReportItem> getSortedItems(Long reportId)
    {
        List<YgbAiReportItem> items = aiReportMapper.selectAiReportItemList(reportId);
        if (items == null)
        {
            return new ArrayList<>();
        }
        return items.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getSortNo, Comparator.nullsLast(Integer::compareTo)))
            .collect(Collectors.toList());
    }

    private void applyDashboardRanking(List<YgbAiReport> reports, Map<Long, List<YgbAiReportItem>> itemCache)
    {
        List<YgbAiReport> rankedReports = sortReportsByScoreDesc(reports);
        for (int i = 0; i < rankedReports.size(); i++)
        {
            YgbAiReport rankedReport = rankedReports.get(i);
            rankedReport.setRankingNo(i + 1);
            List<YgbAiReportItem> items = itemCache.computeIfAbsent(rankedReport.getReportId(), this::getSortedItems);
            rankedReport.setReportSummary(buildSummary(items, safeScore(rankedReport.getTotalScore()),
                resolveRiskLevelByReport(rankedReport), rankedReport.getRankingNo()));
        }
    }

    private void refreshScopeRankings(String reportType, Date periodStart, Date periodEnd)
    {
        YgbAiReport query = new YgbAiReport();
        query.setReportType(reportType);
        query.setPeriodStart(periodStart);
        query.setPeriodEnd(periodEnd);
        List<YgbAiReport> scopedReports = aiReportMapper.selectAiReportList(query);
        if (scopedReports == null || scopedReports.isEmpty())
        {
            return;
        }
        scopedReports.forEach(this::hydrateReport);
        List<YgbAiReport> rankedReports = sortReportsByScoreDesc(scopedReports);
        Map<Long, List<YgbAiReportItem>> itemCache = new LinkedHashMap<>();
        for (int i = 0; i < rankedReports.size(); i++)
        {
            YgbAiReport rankedReport = rankedReports.get(i);
            Integer rankingNo = i + 1;
            List<YgbAiReportItem> items = itemCache.computeIfAbsent(rankedReport.getReportId(), this::getSortedItems);
            String reportSummary = buildSummary(items, safeScore(rankedReport.getTotalScore()),
                resolveRiskLevelByReport(rankedReport), rankingNo);
            aiReportMapper.updateAiReportRankingSummary(rankedReport.getReportId(), rankingNo, reportSummary);
            rankedReport.setRankingNo(rankingNo);
            rankedReport.setReportSummary(reportSummary);
        }
    }

    private List<YgbAiReport> sortReportsByScoreDesc(List<YgbAiReport> reports)
    {
        return reports.stream()
            .sorted(buildScoreDescComparator())
            .collect(Collectors.toList());
    }

    private List<YgbAiReport> sortReportsByScoreAsc(List<YgbAiReport> reports)
    {
        return reports.stream()
            .sorted(buildScoreAscComparator())
            .collect(Collectors.toList());
    }

    private Comparator<YgbAiReport> buildScoreDescComparator()
    {
        return Comparator.comparing(YgbAiReport::getTotalScore, Comparator.nullsLast(BigDecimal::compareTo))
            .reversed()
            .thenComparing(YgbAiReport::getGeneratedTime, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(YgbAiReport::getReportId, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private Comparator<YgbAiReport> buildScoreAscComparator()
    {
        return Comparator.comparing(YgbAiReport::getTotalScore, Comparator.nullsLast(BigDecimal::compareTo))
            .thenComparing(YgbAiReport::getGeneratedTime, Comparator.nullsLast(Date::compareTo))
            .thenComparing(YgbAiReport::getReportId, Comparator.nullsLast(Long::compareTo));
    }

    private List<YgbAiReportDashboardCard> buildDashboardCards(YgbAiReport activeReport, BigDecimal averageScore,
        Integer highRiskCount, Long totalCount)
    {
        List<YgbAiReportDashboardCard> cards = new ArrayList<>();
        BigDecimal activeScore = activeReport == null ? null : safeScore(activeReport.getTotalScore());
        cards.add(createCard("综合得分", activeReport == null ? "--" : scale(activeScore).toPlainString(),
            activeReport == null ? "暂无数据"
                : resolveRiskLabel(resolveRiskLevelByReport(activeReport)) + " · 当前排名第 "
                    + (activeReport.getRankingNo() == null ? "-" : activeReport.getRankingNo()) + " 名"));
        cards.add(createCard("平均得分",
            averageScore == null ? "0.00" : averageScore.setScale(2, RoundingMode.HALF_UP).toPlainString(),
            "当前筛选样本均值"));
        cards.add(createCard("高风险样本数", String.valueOf(highRiskCount == null ? 0 : highRiskCount),
            "总样本 " + (totalCount == null ? 0 : totalCount)));
        cards.add(createCard("模型版本",
            activeReport == null ? "DEFAULT-STUB" : StringUtils.defaultIfEmpty(activeReport.getConfigVersion(), "DEFAULT-STUB"),
            "评分标准与目标值同步生效"));
        if (activeReport != null)
        {
            cards.set(1, createCard("当前样本得分偏离",
                scale(activeScore.subtract(averageScore == null ? BigDecimal.ZERO : averageScore)).toPlainString(),
                "相对筛选均值 " + (averageScore == null ? "0.00" : averageScore.toPlainString())));
        }
        return cards;
    }

    private YgbAiReportDashboardCard createCard(String label, String value, String trend)
    {
        YgbAiReportDashboardCard card = new YgbAiReportDashboardCard();
        card.setLabel(label);
        card.setValue(value);
        card.setTrend(trend);
        return card;
    }

    private YgbAiReportDashboardConclusion buildDashboardConclusion(YgbAiReport activeReport, List<YgbAiReportItem> items)
    {
        YgbAiReportDashboardConclusion conclusion = new YgbAiReportDashboardConclusion();
        if (activeReport == null)
        {
            conclusion.setSummary("当前暂无报告数据。");
            conclusion.setStrength("暂无优势维度识别结果。");
            conclusion.setWeakness("暂无薄弱维度识别结果。");
            conclusion.setAdvice("建议先生成报告样本，再进行对比分析。");
            return conclusion;
        }

        List<YgbAiReportItem> orderedItems = items.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getDimensionScore, Comparator.nullsLast(BigDecimal::compareTo)))
            .collect(Collectors.toList());
        YgbAiReportItem weakest = orderedItems.isEmpty() ? null : orderedItems.get(0);
        YgbAiReportItem strongest = orderedItems.isEmpty() ? null : orderedItems.get(orderedItems.size() - 1);

        conclusion.setSummary(buildSummary(items, safeScore(activeReport.getTotalScore()), resolveRiskLevelByReport(activeReport),
            activeReport.getRankingNo() == null ? 0 : activeReport.getRankingNo()));
        conclusion.setStrength(strongest == null ? "暂无优势维度识别结果。"
            : strongest.getDimensionName() + " 得分 " + strongest.getDimensionScore() + "，"
                + StringUtils.defaultIfEmpty(strongest.getSuggestionText(), "建议继续保持当前治理节奏。"));
        conclusion.setWeakness(weakest == null ? "暂无薄弱维度识别结果。"
            : weakest.getDimensionName() + " 得分 " + weakest.getDimensionScore() + "，当前为 "
                + resolveRiskLabel(resolveRiskLevelByItem(weakest)) + "，需重点核查 " + weakest.getMetricLabel() + "。");
        conclusion.setAdvice(orderedItems.stream()
            .limit(2)
            .map(YgbAiReportItem::getSuggestionText)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.joining("；")));
        if (StringUtils.isEmpty(conclusion.getAdvice()))
        {
            conclusion.setAdvice("建议围绕低分维度持续跟踪整改进度。");
        }
        return conclusion;
    }

    private List<YgbAiReportDashboardTrendPoint> buildTrendPoints(List<YgbAiReport> reports)
    {
        return reports.stream()
            .sorted(Comparator.comparing(YgbAiReport::getPeriodEnd, Comparator.nullsLast(Date::compareTo))
                .thenComparing(YgbAiReport::getGeneratedTime, Comparator.nullsLast(Date::compareTo))
                .thenComparing(YgbAiReport::getReportId, Comparator.nullsLast(Long::compareTo)))
            .skip(Math.max(0, reports.size() - 6))
            .map(report -> {
                YgbAiReportDashboardTrendPoint point = new YgbAiReportDashboardTrendPoint();
                point.setLabel(resolveReportTypeLabel(report.getReportType()) + " "
                    + MONTH_FORMATTER.format(toLocalDate(report.getPeriodEnd())));
                point.setScore(scale(safeScore(report.getTotalScore())));
                point.setRankingNo(report.getRankingNo());
                return point;
            })
            .collect(Collectors.toList());
    }

    private List<YgbAiReportDashboardRankItem> buildRankingList(List<YgbAiReport> reports,
        Map<Long, List<YgbAiReportItem>> itemCache, boolean positive)
    {
        return reports.stream()
            .map(report -> buildRankItem(report, itemCache, positive))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbAiReport activeReport, List<YgbAiReportItem> activeItems,
        YgbAiReportDashboard dashboard)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(activeReport);
        list.add(explanationItem("业务影响", safeScore(activeReport == null ? null : activeReport.getTotalScore()),
            dashboard.getAverageScore(), "AI 报告优先解释业务影响与经办承接结果。", "aiReport", "aiReport", "530.1 业务影响解释", baseQuery));
        list.add(explanationItem("合规风险", dashboard.getHighRiskCount(), 0,
            "高风险样本优先落到整改链和办理链，不混入治理链优先描述。", "warning", "warning", "530.1 业务影响解释", baseQuery));
        list.add(explanationItem("闭环建议", topSuggestion(activeItems), "完成整改",
            "建议先处理最低分维度，再回到月报归档、异常回查和整改闭环。", "statReport", "statReport", "530.1 业务影响解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbAiReport activeReport, List<YgbAiReportItem> activeItems,
        YgbAiReportDashboard dashboard)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(activeReport);
        list.add(explanationItem("高风险对象", dashboard.getHighRiskCount(), 0,
            "6.1 口径优先解释高风险对象和区域压降压力。", "aiReport", "aiReport", "6.1 隐患压降解释", baseQuery));
        list.add(explanationItem("重复隐患", lowScoreDimensionCount(activeItems), 0,
            "同一报告中的低分维度越多，越需要联动隐患整改与复核。", "warning", "warning", "6.1 隐患压降解释", baseQuery));
        list.add(explanationItem("压降建议", topSuggestion(activeItems), "完成压降",
            "建议按风险对象、隐患原因和建议动作组织处置顺序。", "creditScore", "creditScore", "6.1 隐患压降解释", baseQuery));
        list.add(explanationItem("处置时效", activeReport == null ? null : activeReport.getRankingNo(), 1,
            "首页只保留一个主下钻入口，继续追溯治理解释来源和处置时效。", "statReport", "statReport", "6.1 隐患压降解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbAiReport activeReport)
    {
        Map<String, Object> query = new LinkedHashMap<>();
        if (activeReport == null)
        {
            return query;
        }
        query.put("regionCode", activeReport.getRegionCode());
        query.put("reportType", activeReport.getReportType());
        query.put("riskLevel", activeReport.getRiskLevel());
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

    private String topSuggestion(List<YgbAiReportItem> items)
    {
        if (items == null || items.isEmpty())
        {
            return "暂无建议";
        }
        return items.stream()
            .map(YgbAiReportItem::getSuggestionText)
            .filter(StringUtils::isNotEmpty)
            .findFirst()
            .orElse("继续跟进最低分维度");
    }

    private int lowScoreDimensionCount(List<YgbAiReportItem> items)
    {
        if (items == null)
        {
            return 0;
        }
        return (int) items.stream()
            .filter(item -> item.getDimensionScore() != null && item.getDimensionScore().compareTo(new BigDecimal("70")) < 0)
            .count();
    }

    private YgbAiReportDashboardRankItem buildRankItem(YgbAiReport report, Map<Long, List<YgbAiReportItem>> itemCache,
        boolean positive)
    {
        List<YgbAiReportItem> items = itemCache.computeIfAbsent(report.getReportId(), this::getSortedItems);
        List<YgbAiReportItem> orderedByScore = items.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getDimensionScore, Comparator.nullsLast(BigDecimal::compareTo)))
            .collect(Collectors.toList());
        YgbAiReportItem weakest = orderedByScore.isEmpty() ? null : orderedByScore.get(0);
        YgbAiReportItem strongest = orderedByScore.isEmpty() ? null : orderedByScore.get(orderedByScore.size() - 1);

        YgbAiReportDashboardRankItem item = new YgbAiReportDashboardRankItem();
        item.setReportId(report.getReportId());
        item.setReportType(report.getReportType());
        item.setRegionCode(report.getRegionCode());
        item.setRegionName(report.getRegionName());
        item.setEnterpriseType(report.getEnterpriseType());
        item.setTotalScore(scale(safeScore(report.getTotalScore())));
        item.setRiskLevel(resolveRiskLevelByReport(report));
        item.setRankingNo(report.getRankingNo());
        item.setReportSummary(report.getReportSummary());
        item.setHighlight(positive
            ? (strongest == null ? "当前样本得分较高。" : strongest.getDimensionName() + " 表现较好，得分 "
                + strongest.getDimensionScore() + "。")
            : StringUtils.defaultIfEmpty(report.getReportSummary(), "当前样本得分偏低，需重点核查薄弱维度。"));
        item.setRiskReason(weakest == null ? "暂无风险因子。" : weakest.getDimensionName() + " 得分 "
            + weakest.getDimensionScore() + "，重点指标 " + weakest.getMetricLabel() + "。");
        item.setAdvice(weakest == null ? "建议补充维度明细。" : weakest.getSuggestionText());
        return item;
    }

    private boolean isHighRiskReport(YgbAiReport report)
    {
        return "HIGH".equals(resolveRiskLevelByReport(report))
            || safeScore(report.getTotalScore()).compareTo(new BigDecimal("70")) < 0;
    }

    private LocalDate toLocalDate(Date date)
    {
        if (date == null)
        {
            return LocalDate.now();
        }
        if (date instanceof java.sql.Date sqlDate)
        {
            return sqlDate.toLocalDate();
        }
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    private List<YgbAiReportItem> buildDimensionItems(String regionPrefix, PeriodRange periodRange, String startMonth,
        String endMonth, List<String> dimensions, Map<String, BigDecimal> weights, Map<String, BigDecimal> targets)
    {
        List<YgbAiReportItem> items = new ArrayList<>();
        for (int i = 0; i < dimensions.size(); i++)
        {
            String dimensionCode = dimensions.get(i);
            YgbAiReportItem item = switch (dimensionCode)
            {
                case "A" -> buildContractItem(regionPrefix, periodRange, weights, targets);
                case "B" -> buildAttendanceItem(regionPrefix, startMonth, endMonth, weights, targets);
                case "C" -> buildSalaryItem(regionPrefix, startMonth, endMonth, weights, targets);
                case "D" -> buildSafetyItem(regionPrefix, periodRange, weights, targets);
                case "E" -> buildWarningItem(regionPrefix, periodRange, weights, targets);
                default -> null;
            };
            if (item != null)
            {
                item.setSortNo(i + 1);
                items.add(item);
            }
        }
        return items;
    }

    private YgbAiReportItem buildContractItem(String regionPrefix, PeriodRange periodRange,
        Map<String, BigDecimal> weights, Map<String, BigDecimal> targets)
    {
        int total = aiReportMapper.countContractTotal(regionPrefix, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()));
        int filed = aiReportMapper.countContractFiled(regionPrefix, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()));
        BigDecimal actualRate = ratio(filed, total);
        BigDecimal targetRate = targetOf(targets, "contractRate", new BigDecimal("100"));
        BigDecimal score = scoreHigherBetter(actualRate, targetRate);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("filedCount", filed);
        detail.put("totalCount", total);

        return buildItem("A", "合同备案合规", "备案率", actualRate, targetRate, weightOf(weights, "A"), score,
            score.compareTo(new BigDecimal("90")) >= 0 ? "保持 OCR 和链上存证闭环，持续做抽样复核。"
                : "补齐未备案合同，优先处理到期续签和资料缺失的备案任务。",
            detail);
    }

    private YgbAiReportItem buildAttendanceItem(String regionPrefix, String startMonth, String endMonth,
        Map<String, BigDecimal> weights, Map<String, BigDecimal> targets)
    {
        int total = aiReportMapper.countAttendanceTotal(startMonth, endMonth, regionPrefix);
        int passed = aiReportMapper.countAttendancePassed(startMonth, endMonth, regionPrefix);
        BigDecimal actualRate = ratio(passed, total);
        BigDecimal targetRate = targetOf(targets, "attendanceRate", new BigDecimal("95"));
        BigDecimal score = scoreHigherBetter(actualRate, targetRate);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("passedCount", passed);
        detail.put("totalCount", total);

        return buildItem("B", "考勤归集合规", "校验通过率", actualRate, targetRate, weightOf(weights, "B"), score,
            score.compareTo(new BigDecimal("90")) >= 0 ? "继续保持考勤归集和工资校验联动。"
                : "复核异常考勤和未通过 att_check 的记录，提升归集数据准确率。",
            detail);
    }

    private YgbAiReportItem buildSalaryItem(String regionPrefix, String startMonth, String endMonth,
        Map<String, BigDecimal> weights, Map<String, BigDecimal> targets)
    {
        int total = aiReportMapper.countSalaryTotal(startMonth, endMonth, regionPrefix);
        int success = aiReportMapper.countSalarySuccess(startMonth, endMonth, regionPrefix);
        BigDecimal actualRate = ratio(success, total);
        BigDecimal targetRate = targetOf(targets, "paySuccessRate", new BigDecimal("98"));
        BigDecimal score = scoreHigherBetter(actualRate, targetRate);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("successCount", success);
        detail.put("totalCount", total);

        return buildItem("C", "工资发放合规", "发放成功率", actualRate, targetRate, weightOf(weights, "C"), score,
            score.compareTo(new BigDecimal("90")) >= 0 ? "保持银行回盘闭环，持续压降失败重试量。"
                : "重点复核失败工资明细和银行回盘异常，缩短补发闭环时间。",
            detail);
    }

    private YgbAiReportItem buildSafetyItem(String regionPrefix, PeriodRange periodRange,
        Map<String, BigDecimal> weights, Map<String, BigDecimal> targets)
    {
        int deviceTotal = aiReportMapper.countDeviceTotal(regionPrefix);
        int deviceOnline = aiReportMapper.countDeviceOnline(regionPrefix);
        int injuryTotal = aiReportMapper.countInjuryTotal(regionPrefix, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()));
        int insuredCount = aiReportMapper.countInsuredPerson(regionPrefix);

        BigDecimal onlineRate = ratio(deviceOnline, deviceTotal);
        BigDecimal injuryRate = ratioThousand(injuryTotal, insuredCount);
        BigDecimal targetOnlineRate = targetOf(targets, "onlineRate", new BigDecimal("95"));
        BigDecimal targetInjuryRate = targetOf(targets, "injuryRate", new BigDecimal("2.50"));
        BigDecimal onlineScore = scoreHigherBetter(onlineRate, targetOnlineRate);
        BigDecimal injuryScore = scoreLowerBetter(injuryRate, targetInjuryRate);
        BigDecimal score = onlineScore.add(injuryScore).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("onlineCount", deviceOnline);
        detail.put("deviceTotal", deviceTotal);
        detail.put("onlineRate", onlineRate);
        detail.put("injuryCount", injuryTotal);
        detail.put("insuredCount", insuredCount);
        detail.put("injuryRate", injuryRate);

        String suggestion = score.compareTo(new BigDecimal("90")) >= 0
            ? "保持设备在线巡检和工伤处置时效，持续巩固高危岗位防控。"
            : (onlineRate.compareTo(targetOnlineRate) < 0
                ? "优先提升设备在线率和心跳覆盖，补齐离线设备巡检。"
                : "压降工伤发生率和超期事件，强化高危作业授权前置校验。");

        return buildItem("D", "设备工伤安全", "设备在线率", onlineRate, targetOnlineRate, weightOf(weights, "D"), score,
            suggestion, detail);
    }

    private YgbAiReportItem buildWarningItem(String regionPrefix, PeriodRange periodRange,
        Map<String, BigDecimal> weights, Map<String, BigDecimal> targets)
    {
        int total = aiReportMapper.countWarningTotal(regionPrefix, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()));
        int closed = aiReportMapper.countWarningClosed(regionPrefix, toDate(periodRange.getStart()),
            toDate(periodRange.getEnd()));
        BigDecimal actualRate = ratio(closed, total);
        BigDecimal targetRate = targetOf(targets, "warningCloseRate", new BigDecimal("90"));
        BigDecimal score = scoreHigherBetter(actualRate, targetRate);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("closedCount", closed);
        detail.put("totalCount", total);

        return buildItem("E", "预警闭环治理", "闭环率", actualRate, targetRate, weightOf(weights, "E"), score,
            score.compareTo(new BigDecimal("90")) >= 0 ? "保持预警闭环时效，继续沉淀处置经验。"
                : "加快待处理预警工单闭环，重点清理长时间未办结记录。",
            detail);
    }

    private YgbAiReportItem buildItem(String dimensionCode, String dimensionName, String metricLabel,
        BigDecimal metricValue, BigDecimal targetValue, BigDecimal weight, BigDecimal score, String suggestion,
        Map<String, Object> detail)
    {
        YgbAiReportItem item = new YgbAiReportItem();
        item.setDimensionCode(dimensionCode);
        item.setDimensionName(dimensionName);
        item.setMetricLabel(metricLabel);
        item.setMetricValue(scale(metricValue));
        item.setTargetValue(scale(targetValue));
        item.setDimensionWeight(scale(weight));
        item.setDimensionScore(scale(score));
        item.setRiskLevel(resolveRiskLevel(score));
        item.setSuggestionText(suggestion);
        item.setDetailJson(JSON.toJSONString(detail));
        return item;
    }

    private BigDecimal calculateTotalScore(List<YgbAiReportItem> items)
    {
        BigDecimal totalWeight = items.stream()
            .map(YgbAiReportItem::getDimensionWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal weightedScore = items.stream()
            .map(item -> item.getDimensionScore().multiply(item.getDimensionWeight()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return weightedScore.divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    private String buildSummary(List<YgbAiReportItem> items, BigDecimal totalScore, String riskLevel, Integer rankingNo)
    {
        List<YgbAiReportItem> weakest = items.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getDimensionScore))
            .limit(2)
            .collect(Collectors.toList());
        String weakestLabels = weakest.stream()
            .map(YgbAiReportItem::getDimensionName)
            .collect(Collectors.joining("、"));
        return "综合得分 " + scale(totalScore) + "，风险等级 " + resolveRiskLabel(riskLevel) + "，区域排名第 "
            + rankingNo + "，重点关注：" + weakestLabels + "。";
    }

    private String buildHtml(String regionCode, String reportType, PeriodRange periodRange, BigDecimal totalScore,
        String riskLevel, List<YgbAiReportItem> items)
    {
        List<YgbAiReportItem> orderedItems = items.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getSortNo, Comparator.nullsLast(Integer::compareTo)))
            .collect(Collectors.toList());
        List<YgbAiReportItem> strongestItems = orderedItems.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getDimensionScore).reversed())
            .limit(2)
            .collect(Collectors.toList());
        List<YgbAiReportItem> weakestItems = orderedItems.stream()
            .sorted(Comparator.comparing(YgbAiReportItem::getDimensionScore))
            .limit(2)
            .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        builder.append("<section class=\"ai-report-fragment\">")
            .append("<style>")
            .append(".ai-report-fragment{font-family:'Microsoft YaHei',sans-serif;color:#21364a;line-height:1.8;}")
            .append(".ai-report-fragment__header{padding:18px 20px;border-radius:16px;background:#f4f8fc;border:1px solid #e0eaf3;}")
            .append(".ai-report-fragment__header h3{margin:0 0 8px;font-size:22px;color:#0f2b3d;}")
            .append(".ai-report-fragment__meta{display:flex;flex-wrap:wrap;gap:12px 20px;color:#5b7187;font-size:13px;}")
            .append(".ai-report-fragment__summary{margin-top:16px;padding:16px 18px;border-left:4px solid #c8282d;border-radius:12px;background:#f8fbfe;}")
            .append(".ai-report-fragment__summary p{margin:0 0 8px;}")
            .append(".ai-report-fragment__section{margin-top:18px;}")
            .append(".ai-report-fragment__section h4{margin:0 0 12px;font-size:17px;color:#17324a;}")
            .append(".ai-report-fragment__list{margin:0;padding-left:18px;}")
            .append(".ai-report-fragment__table{width:100%;border-collapse:collapse;font-size:13px;}")
            .append(".ai-report-fragment__table th,.ai-report-fragment__table td{padding:10px 12px;border:1px solid #e4ebf2;text-align:left;vertical-align:top;}")
            .append(".ai-report-fragment__table th{background:#f7fafc;color:#17324a;}")
            .append(".ai-report-fragment__tag{display:inline-block;padding:2px 10px;border-radius:999px;background:#eef4fa;color:#36546d;font-size:12px;}")
            .append("</style>")
            .append("<div class=\"ai-report-fragment__header\"><h3>AI监测报告</h3><div class=\"ai-report-fragment__meta\">")
            .append("<span>区域：").append(escapeHtml(YgbRegionHelper.resolveRegionName(regionCode))).append("</span>")
            .append("<span>类型：").append(escapeHtml(resolveReportTypeLabel(reportType))).append("</span>")
            .append("<span>统计区间：").append(periodRange.getStart()).append(" 至 ").append(periodRange.getEnd()).append("</span>")
            .append("<span>综合得分：").append(scale(totalScore)).append("</span>")
            .append("<span>风险等级：").append(escapeHtml(resolveRiskLabel(riskLevel))).append("</span>")
            .append("</div></div>")
            .append("<div class=\"ai-report-fragment__summary\">")
            .append("<p><strong>综合判断：</strong>当前监测样本综合得分 ")
            .append(scale(totalScore))
            .append("，风险等级为 ")
            .append(escapeHtml(resolveRiskLabel(riskLevel)))
            .append("，建议围绕低分维度持续跟踪整改进度。</p>")
            .append("<p><strong>优势维度：</strong>")
            .append(escapeHtml(joinDimensionSummary(strongestItems)))
            .append("</p>")
            .append("<p><strong>重点风险：</strong>")
            .append(escapeHtml(joinDimensionSummary(weakestItems)))
            .append("</p>")
            .append("</div>")
            .append("<div class=\"ai-report-fragment__section\"><h4>维度评分明细</h4>")
            .append("<table class=\"ai-report-fragment__table\"><thead><tr>")
            .append("<th>维度</th><th>指标</th><th>指标值</th><th>目标值</th><th>权重</th><th>得分</th><th>风险等级</th><th>建议</th>")
            .append("</tr></thead><tbody>");
        for (YgbAiReportItem item : orderedItems)
        {
            builder.append("<tr><td>")
                .append(escapeHtml(item.getDimensionCode()))
                .append(" ")
                .append(escapeHtml(item.getDimensionName()))
                .append("</td><td>")
                .append(escapeHtml(item.getMetricLabel()))
                .append("</td><td>")
                .append(item.getMetricValue())
                .append("</td><td>")
                .append(item.getTargetValue())
                .append("</td><td>")
                .append(item.getDimensionWeight())
                .append("</td><td>")
                .append(item.getDimensionScore())
                .append("</td><td><span class=\"ai-report-fragment__tag\">")
                .append(escapeHtml(resolveRiskLabel(item.getRiskLevel())))
                .append("</span></td><td>")
                .append(escapeHtml(item.getSuggestionText()))
                .append("</td></tr>");
        }
        builder.append("</tbody></table></div>")
            .append("<div class=\"ai-report-fragment__section\"><h4>整改建议</h4><ul class=\"ai-report-fragment__list\">");
        for (YgbAiReportItem item : weakestItems)
        {
            builder.append("<li>")
                .append(escapeHtml(item.getDimensionName()))
                .append("：")
                .append(escapeHtml(item.getSuggestionText()))
                .append("</li>");
        }
        builder.append("</ul></div></section>");
        return builder.toString();
    }

    private String joinDimensionSummary(List<YgbAiReportItem> items)
    {
        return items.stream()
            .map(item -> item.getDimensionName() + "（" + item.getDimensionScore() + "分）")
            .collect(Collectors.joining("、"));
    }

    private String escapeHtml(String text)
    {
        if (text == null)
        {
            return "";
        }
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    private String normalizeReportType(String reportType)
    {
        if (StringUtils.isEmpty(reportType))
        {
            return "MONTHLY";
        }
        String normalized = reportType.trim().toUpperCase();
        return switch (normalized)
        {
            case "DAILY", "WEEKLY", "MONTHLY", "YEARLY" -> normalized;
            default -> throw new ServiceException("不支持的报告类型：" + reportType);
        };
    }

    private String normalizeEnterpriseType(String enterpriseType)
    {
        if (StringUtils.isEmpty(enterpriseType))
        {
            return "ALL";
        }
        return enterpriseType.trim().toUpperCase();
    }

    private List<String> normalizeDimensions(List<String> dimensions)
    {
        if (dimensions == null || dimensions.isEmpty())
        {
            return DEFAULT_DIMENSIONS;
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String dimension : dimensions)
        {
            if (StringUtils.isNotEmpty(dimension))
            {
                normalized.add(dimension.trim().toUpperCase());
            }
        }
        if (normalized.isEmpty())
        {
            return DEFAULT_DIMENSIONS;
        }
        return new ArrayList<>(normalized);
    }

    private PeriodRange resolvePeriodRange(String reportType, String startDate, String endDate)
    {
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate))
        {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            if (end.isBefore(start))
            {
                throw new ServiceException("报告结束日期不能早于开始日期。");
            }
            return new PeriodRange(start, end);
        }

        LocalDate today = LocalDate.now();
        return switch (reportType)
        {
            case "DAILY" -> new PeriodRange(today, today);
            case "WEEKLY" -> new PeriodRange(today.with(DayOfWeek.MONDAY), today.with(DayOfWeek.SUNDAY));
            case "YEARLY" -> new PeriodRange(Year.of(today.getYear()).atDay(1), Year.of(today.getYear()).atMonth(12).atEndOfMonth());
            default ->
            {
                YearMonth yearMonth = YearMonth.from(today);
                yield new PeriodRange(yearMonth.atDay(1), yearMonth.atEndOfMonth());
            }
        };
    }

    private Map<String, BigDecimal> parseDecimalMap(String json, Map<String, BigDecimal> defaults)
    {
        Map<String, BigDecimal> result = new LinkedHashMap<>(defaults);
        if (StringUtils.isEmpty(json))
        {
            return result;
        }
        try
        {
            Map<String, Object> raw = JSON.parseObject(json);
            for (Map.Entry<String, Object> entry : raw.entrySet())
            {
                if (entry.getValue() != null)
                {
                    result.put(entry.getKey(), new BigDecimal(String.valueOf(entry.getValue())));
                }
            }
            return result;
        }
        catch (Exception ex)
        {
            return result;
        }
    }

    private BigDecimal weightOf(Map<String, BigDecimal> weights, String dimensionCode)
    {
        return weights.getOrDefault(dimensionCode, BigDecimal.ZERO);
    }

    private BigDecimal targetOf(Map<String, BigDecimal> targets, String key, BigDecimal defaultValue)
    {
        return targets.getOrDefault(key, defaultValue);
    }

    private BigDecimal ratio(int numerator, int denominator)
    {
        if (denominator <= 0)
        {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(numerator).multiply(ONE_HUNDRED)
            .divide(new BigDecimal(denominator), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal ratioThousand(int numerator, int denominator)
    {
        if (denominator <= 0)
        {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(numerator).multiply(new BigDecimal("1000"))
            .divide(new BigDecimal(denominator), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal scoreHigherBetter(BigDecimal actual, BigDecimal target)
    {
        if (target.compareTo(BigDecimal.ZERO) <= 0)
        {
            return scale(actual.min(ONE_HUNDRED));
        }
        BigDecimal score = actual.multiply(ONE_HUNDRED).divide(target, 2, RoundingMode.HALF_UP);
        return scale(score.min(ONE_HUNDRED).max(BigDecimal.ZERO));
    }

    private BigDecimal scoreLowerBetter(BigDecimal actual, BigDecimal target)
    {
        if (actual.compareTo(BigDecimal.ZERO) <= 0 || target.compareTo(BigDecimal.ZERO) <= 0)
        {
            return ONE_HUNDRED;
        }
        if (actual.compareTo(target) <= 0)
        {
            return ONE_HUNDRED;
        }
        BigDecimal exceedRatio = actual.subtract(target).multiply(ONE_HUNDRED)
            .divide(target, 2, RoundingMode.HALF_UP);
        BigDecimal score = ONE_HUNDRED.subtract(exceedRatio);
        return scale(score.max(BigDecimal.ZERO));
    }

    private String resolveRiskLevel(BigDecimal score)
    {
        if (score.compareTo(new BigDecimal("85")) >= 0)
        {
            return "LOW";
        }
        if (score.compareTo(new BigDecimal("70")) >= 0)
        {
            return "MEDIUM";
        }
        return "HIGH";
    }

    private String resolveRiskLabel(String riskLevel)
    {
        return switch (riskLevel)
        {
            case "LOW" -> "低风险";
            case "MEDIUM" -> "中风险";
            default -> "高风险";
        };
    }

    private String resolveReportTypeLabel(String reportType)
    {
        return switch (reportType)
        {
            case "DAILY" -> "日报";
            case "WEEKLY" -> "周报";
            case "YEARLY" -> "年报";
            default -> "月报";
        };
    }

    private String buildPdfUrl(String reportType, String regionCode, PeriodRange periodRange)
    {
        return "stub://ai-report/" + reportType.toLowerCase() + "/" + regionCode + "/"
            + periodRange.getStart() + "_" + periodRange.getEnd() + ".pdf";
    }

    private BigDecimal scale(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeScore(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String resolveRiskLevelByReport(YgbAiReport report)
    {
        if (report == null)
        {
            return "HIGH";
        }
        if (StringUtils.isNotEmpty(report.getRiskLevel()))
        {
            return report.getRiskLevel();
        }
        return resolveRiskLevel(safeScore(report.getTotalScore()));
    }

    private String resolveRiskLevelByItem(YgbAiReportItem item)
    {
        if (item == null)
        {
            return "HIGH";
        }
        if (StringUtils.isNotEmpty(item.getRiskLevel()))
        {
            return item.getRiskLevel();
        }
        return resolveRiskLevel(safeScore(item.getDimensionScore()));
    }

    private Date toDate(LocalDate localDate)
    {
        return java.sql.Date.valueOf(localDate);
    }

    private static final class PeriodRange
    {
        private final LocalDate start;

        private final LocalDate end;

        private PeriodRange(LocalDate start, LocalDate end)
        {
            this.start = start;
            this.end = end;
        }

        public LocalDate getStart()
        {
            return start;
        }

        public LocalDate getEnd()
        {
            return end;
        }
    }
}
