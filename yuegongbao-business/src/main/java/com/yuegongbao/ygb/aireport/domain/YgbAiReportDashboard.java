package com.yuegongbao.ygb.aireport.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbAiReportDashboard
{
    private Long totalCount;

    private BigDecimal averageScore;

    private Integer highRiskCount;

    private YgbAiReport activeReport;

    private List<YgbAiReportItem> activeItems = new ArrayList<>();

    private List<YgbAiReportDashboardCard> scoreCards = new ArrayList<>();

    private YgbAiReportDashboardConclusion conclusion;

    private List<YgbAiReportDashboardTrendPoint> trendPoints = new ArrayList<>();

    private List<YgbAiReportDashboardRankItem> topRankingList = new ArrayList<>();

    private List<YgbAiReportDashboardRankItem> bottomRankingList = new ArrayList<>();

    private List<YgbAiReportDashboardRankItem> highRiskList = new ArrayList<>();

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }

    public BigDecimal getAverageScore()
    {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore)
    {
        this.averageScore = averageScore;
    }

    public Integer getHighRiskCount()
    {
        return highRiskCount;
    }

    public void setHighRiskCount(Integer highRiskCount)
    {
        this.highRiskCount = highRiskCount;
    }

    public YgbAiReport getActiveReport()
    {
        return activeReport;
    }

    public void setActiveReport(YgbAiReport activeReport)
    {
        this.activeReport = activeReport;
    }

    public List<YgbAiReportItem> getActiveItems()
    {
        return activeItems;
    }

    public void setActiveItems(List<YgbAiReportItem> activeItems)
    {
        this.activeItems = activeItems;
    }

    public List<YgbAiReportDashboardCard> getScoreCards()
    {
        return scoreCards;
    }

    public void setScoreCards(List<YgbAiReportDashboardCard> scoreCards)
    {
        this.scoreCards = scoreCards;
    }

    public YgbAiReportDashboardConclusion getConclusion()
    {
        return conclusion;
    }

    public void setConclusion(YgbAiReportDashboardConclusion conclusion)
    {
        this.conclusion = conclusion;
    }

    public List<YgbAiReportDashboardTrendPoint> getTrendPoints()
    {
        return trendPoints;
    }

    public void setTrendPoints(List<YgbAiReportDashboardTrendPoint> trendPoints)
    {
        this.trendPoints = trendPoints;
    }

    public List<YgbAiReportDashboardRankItem> getTopRankingList()
    {
        return topRankingList;
    }

    public void setTopRankingList(List<YgbAiReportDashboardRankItem> topRankingList)
    {
        this.topRankingList = topRankingList;
    }

    public List<YgbAiReportDashboardRankItem> getBottomRankingList()
    {
        return bottomRankingList;
    }

    public void setBottomRankingList(List<YgbAiReportDashboardRankItem> bottomRankingList)
    {
        this.bottomRankingList = bottomRankingList;
    }

    public List<YgbAiReportDashboardRankItem> getHighRiskList()
    {
        return highRiskList;
    }

    public void setHighRiskList(List<YgbAiReportDashboardRankItem> highRiskList)
    {
        this.highRiskList = highRiskList;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }

    public List<Map<String, Object>> getAzbExplanation()
    {
        return azbExplanation;
    }

    public void setAzbExplanation(List<Map<String, Object>> azbExplanation)
    {
        this.azbExplanation = azbExplanation;
    }
}
