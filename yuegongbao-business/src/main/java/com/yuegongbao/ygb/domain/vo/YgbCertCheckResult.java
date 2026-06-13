package com.yuegongbao.ygb.domain.vo;

/**
 * 特种证校验结果。
 *
 * @author yuegongbao
 */
public class YgbCertCheckResult extends YgbStubResponse
{
    private boolean valid;

    private String certStatus;

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public String getCertStatus()
    {
        return certStatus;
    }

    public void setCertStatus(String certStatus)
    {
        this.certStatus = certStatus;
    }
}
