package com.yuegongbao.ygb.domain.vo;

/**
 * 工伤状态流转请求。
 *
 * @author yuegongbao
 */
public class YgbInjuryStatusRequest
{
    private String injuryStatus;

    private String approvalResult;

    public String getInjuryStatus()
    {
        return injuryStatus;
    }

    public void setInjuryStatus(String injuryStatus)
    {
        this.injuryStatus = injuryStatus;
    }

    public String getApprovalResult()
    {
        return approvalResult;
    }

    public void setApprovalResult(String approvalResult)
    {
        this.approvalResult = approvalResult;
    }
}
