package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * 参保校验结果。
 *
 * @author yuegongbao
 */
public class YgbInsuranceCheckResult extends YgbStubResponse
{
    private boolean insured;

    private BigDecimal baseAmount;

    public boolean isInsured()
    {
        return insured;
    }

    public void setInsured(boolean insured)
    {
        this.insured = insured;
    }

    public BigDecimal getBaseAmount()
    {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount)
    {
        this.baseAmount = baseAmount;
    }
}
