package com.yuegongbao.ygb.domain.vo;

import jakarta.validation.constraints.NotNull;

/**
 * 假外包识别请求。
 *
 * @author yuegongbao
 */
public class YgbFakeOutsourcingAnalyzeRequest
{
    private String statMonth;

    @NotNull(message = "企业不能为空")
    private Long enterpriseId;

    private Integer attendanceScore;

    private Integer scheduleScore;

    private Integer rewardScore;

    private Integer trainingScore;

    private String evidenceSummary;

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public Integer getAttendanceScore()
    {
        return attendanceScore;
    }

    public void setAttendanceScore(Integer attendanceScore)
    {
        this.attendanceScore = attendanceScore;
    }

    public Integer getScheduleScore()
    {
        return scheduleScore;
    }

    public void setScheduleScore(Integer scheduleScore)
    {
        this.scheduleScore = scheduleScore;
    }

    public Integer getRewardScore()
    {
        return rewardScore;
    }

    public void setRewardScore(Integer rewardScore)
    {
        this.rewardScore = rewardScore;
    }

    public Integer getTrainingScore()
    {
        return trainingScore;
    }

    public void setTrainingScore(Integer trainingScore)
    {
        this.trainingScore = trainingScore;
    }

    public String getEvidenceSummary()
    {
        return evidenceSummary;
    }

    public void setEvidenceSummary(String evidenceSummary)
    {
        this.evidenceSummary = evidenceSummary;
    }
}
