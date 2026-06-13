package com.yuegongbao.ygb.domain.vo;

/**
 * 银行代发 Stub 返回。
 *
 * @author yuegongbao
 */
public class YgbBankPaymentResponse extends YgbStubResponse
{
    private String batchNo;

    private String paymentStatus;

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getPaymentStatus()
    {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }
}
