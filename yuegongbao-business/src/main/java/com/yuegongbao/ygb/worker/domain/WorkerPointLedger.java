package com.yuegongbao.ygb.worker.domain;

import java.math.BigDecimal;
import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerPointLedger extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long ledgerId;

    private Long userId;

    private Long personId;

    private String personName;

    private String changeType;

    private String title;

    private String summary;

    private BigDecimal scoreDelta;

    private BigDecimal balanceAfter;

    private String delFlag;

    public Long getLedgerId()
    {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId)
    {
        this.ledgerId = ledgerId;
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

    public String getChangeType()
    {
        return changeType;
    }

    public void setChangeType(String changeType)
    {
        this.changeType = changeType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public BigDecimal getScoreDelta()
    {
        return scoreDelta;
    }

    public void setScoreDelta(BigDecimal scoreDelta)
    {
        this.scoreDelta = scoreDelta;
    }

    public BigDecimal getBalanceAfter()
    {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter)
    {
        this.balanceAfter = balanceAfter;
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
