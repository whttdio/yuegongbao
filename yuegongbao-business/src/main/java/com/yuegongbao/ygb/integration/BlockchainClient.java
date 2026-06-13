package com.yuegongbao.ygb.integration;

import com.yuegongbao.ygb.domain.vo.YgbBlockchainStoreResponse;

/**
 * 区块链存证接口。
 *
 * @author yuegongbao
 */
public interface BlockchainClient
{
    YgbBlockchainStoreResponse storeContract(String contractNo, String contractFileUrl);
}
