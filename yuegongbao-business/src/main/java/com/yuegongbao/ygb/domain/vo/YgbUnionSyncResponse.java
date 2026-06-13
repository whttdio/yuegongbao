package com.yuegongbao.ygb.domain.vo;

/**
 * 工会法律援助同步回执。
 *
 * @author yuegongbao
 */
public class YgbUnionSyncResponse extends YgbStubResponse
{
    private boolean success;

    private String syncStatus;

    private String syncMessage;

    private String ticketNo;

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getSyncStatus()
    {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
    }

    public String getSyncMessage()
    {
        return syncMessage;
    }

    public void setSyncMessage(String syncMessage)
    {
        this.syncMessage = syncMessage;
    }

    public String getTicketNo()
    {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo)
    {
        this.ticketNo = ticketNo;
    }
}
