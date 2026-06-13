package com.yuegongbao.ygb.domain.vo;

/**
 * 设备授权判定结果。
 *
 * @author yuegongbao
 */
public class YgbDeviceAuthorizeDecision
{
    private boolean authorized;

    private String reason;

    public YgbDeviceAuthorizeDecision()
    {
    }

    public YgbDeviceAuthorizeDecision(boolean authorized, String reason)
    {
        this.authorized = authorized;
        this.reason = reason;
    }

    public boolean isAuthorized()
    {
        return authorized;
    }

    public void setAuthorized(boolean authorized)
    {
        this.authorized = authorized;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }
}
