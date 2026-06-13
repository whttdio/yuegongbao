package com.yuegongbao.ygb.domain.vo;

/**
 * 区块链存证返回。
 *
 * @author yuegongbao
 */
public class YgbBlockchainStoreResponse extends YgbStubResponse
{
    private String hashValue;

    public String getHashValue()
    {
        return hashValue;
    }

    public void setHashValue(String hashValue)
    {
        this.hashValue = hashValue;
    }
}
