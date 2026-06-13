package com.yuegongbao.ygb.techdefense.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

public class YgbHeightWorkFinishRequest
{
    @NotNull(message = "实际结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualEndTime;

    private String endPhotoUrl;

    public Date getActualEndTime()
    {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime)
    {
        this.actualEndTime = actualEndTime;
    }

    public String getEndPhotoUrl()
    {
        return endPhotoUrl;
    }

    public void setEndPhotoUrl(String endPhotoUrl)
    {
        this.endPhotoUrl = endPhotoUrl;
    }
}
