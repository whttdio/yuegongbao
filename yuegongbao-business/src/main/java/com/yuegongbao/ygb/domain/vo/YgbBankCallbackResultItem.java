package com.yuegongbao.ygb.domain.vo;

/**
 * 银行回调明细。
 *
 * @author yuegongbao
 */
public class YgbBankCallbackResultItem
{
    private String cardNo;

    private String status;

    private String reason;

    private String txnId;

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getTxnId()
    {
        return txnId;
    }

    public void setTxnId(String txnId)
    {
        this.txnId = txnId;
    }
}
