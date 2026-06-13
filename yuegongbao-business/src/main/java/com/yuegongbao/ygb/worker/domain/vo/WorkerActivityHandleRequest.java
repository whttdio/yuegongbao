package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerActivityHandleRequest
{
    private String lotteryStatus;

    private String deliveryStatus;

    private String rewardTitle;

    private String statusMessage;

    private String deliveryTimeText;

    public String getLotteryStatus()
    {
        return lotteryStatus;
    }

    public void setLotteryStatus(String lotteryStatus)
    {
        this.lotteryStatus = lotteryStatus;
    }

    public String getDeliveryStatus()
    {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus)
    {
        this.deliveryStatus = deliveryStatus;
    }

    public String getRewardTitle()
    {
        return rewardTitle;
    }

    public void setRewardTitle(String rewardTitle)
    {
        this.rewardTitle = rewardTitle;
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage)
    {
        this.statusMessage = statusMessage;
    }

    public String getDeliveryTimeText()
    {
        return deliveryTimeText;
    }

    public void setDeliveryTimeText(String deliveryTimeText)
    {
        this.deliveryTimeText = deliveryTimeText;
    }
}
