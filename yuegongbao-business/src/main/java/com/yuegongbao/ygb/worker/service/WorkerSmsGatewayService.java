package com.yuegongbao.ygb.worker.service;

import java.util.Map;

public interface WorkerSmsGatewayService
{
    Map<String, Object> sendLoginCode(String mobile, String code);
}
