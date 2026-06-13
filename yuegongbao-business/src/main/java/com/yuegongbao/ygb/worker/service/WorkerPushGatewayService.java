package com.yuegongbao.ygb.worker.service;

import java.util.Map;

public interface WorkerPushGatewayService
{
    Map<String, Object> sendPush(String pushClientId, String title, String content, Object payload,
        Map<String, Object> metadata);
}
