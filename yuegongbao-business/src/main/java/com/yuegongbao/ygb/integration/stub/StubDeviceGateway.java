package com.yuegongbao.ygb.integration.stub;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.domain.vo.YgbStubResponse;
import com.yuegongbao.ygb.integration.DeviceGateway;

/**
 * 设备网关 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubDeviceGateway extends AbstractYgbStubClient implements DeviceGateway
{
    @Override
    public YgbStubResponse sendCommand(String deviceCode, String commandType, String payload)
    {
        YgbStubResponse response = new YgbStubResponse();
        Map<String, Object> raw = new HashMap<>();
        raw.put("deviceCode", deviceCode);
        raw.put("commandType", commandType);
        raw.put("payload", payload);
        fillStubMeta(response, "SUCCESS", "设备指令 Stub 已接收", raw);
        return response;
    }
}
