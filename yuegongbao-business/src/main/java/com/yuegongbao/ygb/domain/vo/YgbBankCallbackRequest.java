package com.yuegongbao.ygb.domain.vo;

import java.util.List;

/**
 * 银行回调请求。
 *
 * @author yuegongbao
 */
public class YgbBankCallbackRequest
{
    private String batchNo;

    private List<YgbBankCallbackResultItem> results;

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public List<YgbBankCallbackResultItem> getResults()
    {
        return results;
    }

    public void setResults(List<YgbBankCallbackResultItem> results)
    {
        this.results = results;
    }
}
