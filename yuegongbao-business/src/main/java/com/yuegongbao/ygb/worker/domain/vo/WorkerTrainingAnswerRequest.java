package com.yuegongbao.ygb.worker.domain.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WorkerTrainingAnswerRequest
{
    @NotBlank(message = "题目ID不能为空")
    private String questionId;

    @NotNull(message = "答案不能为空")
    private Integer answerIndex;

    public String getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }

    public Integer getAnswerIndex()
    {
        return answerIndex;
    }

    public void setAnswerIndex(Integer answerIndex)
    {
        this.answerIndex = answerIndex;
    }
}
