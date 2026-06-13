package com.yuegongbao.ygb.integration.stub;

import java.util.Date;
import java.util.UUID;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.ygb.domain.vo.YgbStubResponse;

/**
 * Stub 客户端基础能力。
 *
 * @author yuegongbao
 */
public abstract class AbstractYgbStubClient
{
    protected void fillStubMeta(YgbStubResponse response, String sourceStatus, String sourceMessage, Object payload)
    {
        response.setExternalSerialNo(UUID.randomUUID().toString().replace("-", ""));
        response.setSourceStatus(sourceStatus);
        response.setSourceMessage(sourceMessage);
        response.setCallbackTime(new Date());
        response.setRawPayload(payload == null ? "{}" : JSON.toJSONString(payload));
    }
}
