package com.yuegongbao.ygb.credit.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * 企业信用评分生成请求。
 *
 * @author yuegongbao
 */
public class YgbCreditScoreGenerateRequest
{
    @NotBlank(message = "统计月份不能为空")
    private String statMonth;

    private String regionCode;

    private Long enterpriseId;

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
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
