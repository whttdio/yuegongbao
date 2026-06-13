package com.yuegongbao.ygb.domain.vo;

/**
 * 漏保清单处置请求。
 *
 * @author yuegongbao
 */
public class YgbUninsuredHandleRequest
{
    private String disposalStatus;

    private String remark;

    public String getDisposalStatus()
    {
        return disposalStatus;
    }

    public void setDisposalStatus(String disposalStatus)
    {
        this.disposalStatus = disposalStatus;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
