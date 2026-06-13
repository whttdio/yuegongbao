package com.yuegongbao.ygb.integration;

import com.yuegongbao.ygb.domain.vo.YgbStubResponse;

/**
 * 设备网关接口。
 *
 * @author yuegongbao
 */
public interface DeviceGateway
{
    YgbStubResponse sendCommand(String deviceCode, String commandType, String payload);
}
