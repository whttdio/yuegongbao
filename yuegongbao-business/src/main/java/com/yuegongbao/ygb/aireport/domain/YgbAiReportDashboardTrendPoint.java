package com.yuegongbao.ygb.aireport.domain;

import java.math.BigDecimal;

public class YgbAiReportDashboardTrendPoint
{
    private String label;

    private BigDecimal score;

    private Integer rankingNo;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public BigDecimal getScore()
    {
        return score;
    }

    public void setScore(BigDecimal score)
    {
        this.score = score;
    }

    public Integer getRankingNo()
    {
        return rankingNo;
    }

    public void setRankingNo(Integer rankingNo)
    {
        this.rankingNo = rankingNo;
    }
}
