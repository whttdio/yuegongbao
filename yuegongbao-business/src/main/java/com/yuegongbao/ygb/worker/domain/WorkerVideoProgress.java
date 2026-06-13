package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerVideoProgress extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long progressId;

    private String videoKey;

    private Long userId;

    private Long personId;

    private Integer watchedSeconds;

    private Integer totalSeconds;

    private String completedFlag;

    private String delFlag;

    public Long getProgressId()
    {
        return progressId;
    }

    public void setProgressId(Long progressId)
    {
        this.progressId = progressId;
    }

    public String getVideoKey()
    {
        return videoKey;
    }

    public void setVideoKey(String videoKey)
    {
        this.videoKey = videoKey;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public Integer getWatchedSeconds()
    {
        return watchedSeconds;
    }

    public void setWatchedSeconds(Integer watchedSeconds)
    {
        this.watchedSeconds = watchedSeconds;
    }

    public Integer getTotalSeconds()
    {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds)
    {
        this.totalSeconds = totalSeconds;
    }

    public String getCompletedFlag()
    {
        return completedFlag;
    }

    public void setCompletedFlag(String completedFlag)
    {
        this.completedFlag = completedFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
