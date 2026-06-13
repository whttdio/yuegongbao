package com.yuegongbao.ygb.domain.vo;

/**
 * 用工比例计算请求。
 *
 * @author yuegongbao
 */
public class YgbEmploymentRatioCalcRequest
{
    private String statMonth;

    private Long employerEnterpriseId;

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public Long getEmployerEnterpriseId()
    {
        return employerEnterpriseId;
    }

    public void setEmployerEnterpriseId(Long employerEnterpriseId)
    {
        this.employerEnterpriseId = employerEnterpriseId;
    }
}
