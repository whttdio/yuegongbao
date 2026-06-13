package com.yuegongbao.ygb.integration.stub;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.domain.vo.YgbBlockchainStoreResponse;
import com.yuegongbao.ygb.integration.BlockchainClient;

/**
 * 区块链存证 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubBlockchainClient extends AbstractYgbStubClient implements BlockchainClient
{
    @Override
    public YgbBlockchainStoreResponse storeContract(String contractNo, String contractFileUrl)
    {
        YgbBlockchainStoreResponse response = new YgbBlockchainStoreResponse();
        response.setHashValue("stub-" + UUID.randomUUID().toString().replace("-", ""));
        Map<String, Object> payload = new HashMap<>();
        payload.put("contractNo", contractNo);
        payload.put("contractFileUrl", contractFileUrl);
        fillStubMeta(response, "SUCCESS", "区块链存证 Stub 已完成", payload);
        return response;
    }
}
