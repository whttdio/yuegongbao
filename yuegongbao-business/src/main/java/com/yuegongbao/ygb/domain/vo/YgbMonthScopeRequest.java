package com.yuegongbao.ygb.domain.vo;

/**
 * 月度范围请求。
 *
 * @author yuegongbao
 */
public class YgbMonthScopeRequest
{
    private String statMonth;

    private Long enterpriseId;

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
}
