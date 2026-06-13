package com.yuegongbao.ygb.worker.domain;

import java.math.BigDecimal;
import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerPointExchange extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long exchangeId;

    private Long userId;

    private Long personId;

    private String personName;

    private String goodsKey;

    private String goodsName;

    private String goodsType;

    private BigDecimal scoreCost;

    private String exchangeStatus;

    private String deliveryRemark;

    private String delFlag;

    public Long getExchangeId()
    {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId)
    {
        this.exchangeId = exchangeId;
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

    public String getGoodsKey()
    {
        return goodsKey;
    }

    public void setGoodsKey(String goodsKey)
    {
        this.goodsKey = goodsKey;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsType()
    {
        return goodsType;
    }

    public void setGoodsType(String goodsType)
    {
        this.goodsType = goodsType;
    }

    public BigDecimal getScoreCost()
    {
        return scoreCost;
    }

    public void setScoreCost(BigDecimal scoreCost)
    {
        this.scoreCost = scoreCost;
    }

    public String getExchangeStatus()
    {
        return exchangeStatus;
    }

    public void setExchangeStatus(String exchangeStatus)
    {
        this.exchangeStatus = exchangeStatus;
    }

    public String getDeliveryRemark()
    {
        return deliveryRemark;
    }

    public void setDeliveryRemark(String deliveryRemark)
    {
        this.deliveryRemark = deliveryRemark;
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
