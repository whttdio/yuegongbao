package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerActivityJoin extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long joinId;

    private String activityKey;

    private Long userId;

    private Long personId;

    private String personName;

    private String mobile;

    private String status;

    private String remark;

    private String delFlag;

    public Long getJoinId()
    {
        return joinId;
    }

    public void setJoinId(Long joinId)
    {
        this.joinId = joinId;
    }

    public String getActivityKey()
    {
        return activityKey;
    }

    public void setActivityKey(String activityKey)
    {
        this.activityKey = activityKey;
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

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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
