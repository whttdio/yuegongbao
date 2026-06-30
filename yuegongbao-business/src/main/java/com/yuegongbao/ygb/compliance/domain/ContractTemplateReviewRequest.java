package com.yuegongbao.ygb.compliance.domain;

public class ContractTemplateReviewRequest
{
    private String reviewStatus;

    private String reviewRemark;

    private String portalScope;

    public String getReviewStatus()
    {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewRemark()
    {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark)
    {
        this.reviewRemark = reviewRemark;
    }

    public String getPortalScope()
    {
        return portalScope;
    }

    public void setPortalScope(String portalScope)
    {
        this.portalScope = portalScope;
    }
}
