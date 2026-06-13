package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * 银行代发明细。
 *
 * @author yuegongbao
 */
public class YgbSalaryPaymentItem
{
    private String personName;

    private String bankCardNo;

    private BigDecimal amount;

    private String attCheck;

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public String getBankCardNo()
    {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo)
    {
        this.bankCardNo = bankCardNo;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getAttCheck()
    {
        return attCheck;
    }

    public void setAttCheck(String attCheck)
    {
        this.attCheck = attCheck;
    }
}
