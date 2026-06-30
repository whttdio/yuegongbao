package com.yuegongbao.ygb.compliance.domain;

public class ContractBatchActionRequest
{
    private Long[] contractIds;

    private YgbContract query;

    private String portalScope;

    private String remark;

    public Long[] getContractIds()
    {
        return contractIds;
    }

    public void setContractIds(Long[] contractIds)
    {
        this.contractIds = contractIds;
    }

    public YgbContract getQuery()
    {
        return query;
    }

    public void setQuery(YgbContract query)
    {
        this.query = query;
    }

    public String getPortalScope()
    {
        return portalScope;
    }

    public void setPortalScope(String portalScope)
    {
        this.portalScope = portalScope;
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
