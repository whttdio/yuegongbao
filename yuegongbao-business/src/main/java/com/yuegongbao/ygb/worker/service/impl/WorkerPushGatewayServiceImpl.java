package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.service.ISysConfigService;
import com.yuegongbao.ygb.worker.service.WorkerPushGatewayService;

@Service
public class WorkerPushGatewayServiceImpl implements WorkerPushGatewayService
{
    private static final String CONFIG_ENABLED = "ygb.worker.push.gateway.enabled";

    private static final String CONFIG_URL = "ygb.worker.push.gateway.url";

    private static final String CONFIG_AUTH_TOKEN = "ygb.worker.push.gateway.authToken";

    private static final String CONFIG_PROVIDER = "ygb.worker.push.gateway.provider";

    @Autowired
    private ISysConfigService sysConfigService;

    @Override
    public Map<String, Object> sendPush(String pushClientId, String title, String content, Object payload,
        Map<String, Object> metadata)
    {
        if (StringUtils.isEmpty(pushClientId))
        {
            throw new ServiceException("当前账号未注册推送 clientId，暂无法发送推送。");
        }

        String enabled = firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_ENABLED), "false");
        if (!"true".equalsIgnoreCase(enabled) && !"1".equals(enabled))
        {
            throw new ServiceException("推送网关未启用，请先配置 ygb.worker.push.gateway.enabled=true。");
        }

        String gatewayUrl = sysConfigService.selectConfigByKey(CONFIG_URL);
        if (StringUtils.isEmpty(gatewayUrl))
        {
            throw new ServiceException("推送网关地址未配置，请先设置 ygb.worker.push.gateway.url。");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("provider", firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_PROVIDER), "custom"));
        requestBody.put("clientId", pushClientId);
        requestBody.put("title", firstNonBlank(title, "粤工保劳动者端测试推送"));
        requestBody.put("content", firstNonBlank(content, "这是一条来自服务端网关的测试推送。"));
        requestBody.put("payload", payload);
        if (metadata != null && !metadata.isEmpty())
        {
            requestBody.put("metadata", metadata);
        }

        String requestJson = JSON.toJSONString(requestBody);
        String responseBody = sendJson(gatewayUrl, requestJson, sysConfigService.selectConfigByKey(CONFIG_AUTH_TOKEN));
        if (StringUtils.isEmpty(responseBody))
        {
            throw new ServiceException("推送网关无响应或调用失败，请确认网关服务地址、鉴权和网络连通性。");
        }

        JSONObject parsedResponse = parseResponse(responseBody);
        if (!isGatewayCallSuccessful(parsedResponse))
        {
            throw new ServiceException(firstNonBlank(resolveFailureMessage(parsedResponse), "推送网关返回失败。"));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("gatewayUrl", gatewayUrl);
        result.put("provider", requestBody.get("provider"));
        result.put("requestBody", requestBody);
        result.put("responseBody", responseBody);
        return result;
    }

    private String sendJson(String gatewayUrl, String requestJson, String authToken)
    {
        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(gatewayUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if (StringUtils.isNotEmpty(authToken))
            {
                connection.setRequestProperty("Authorization", "Bearer " + authToken.trim());
            }

            try (OutputStream outputStream = connection.getOutputStream())
            {
                outputStream.write(requestJson.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            int statusCode = connection.getResponseCode();
            InputStream inputStream = statusCode >= 200 && statusCode < 400
                ? connection.getInputStream()
                : connection.getErrorStream();
            if (inputStream == null)
            {
                return "";
            }
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    responseBuilder.append(line);
                }
                return responseBuilder.toString();
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("调用推送网关失败：" + e.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    private JSONObject parseResponse(String responseBody)
    {
        try
        {
            return JSON.parseObject(responseBody);
        }
        catch (Exception ignored)
        {
            return new JSONObject();
        }
    }

    private boolean isGatewayCallSuccessful(JSONObject response)
    {
        if (response == null || response.isEmpty())
        {
            return true;
        }
        Object success = response.get("success");
        if (success instanceof Boolean booleanValue)
        {
            return booleanValue;
        }
        String code = response.getString("code");
        if (StringUtils.isNotEmpty(code))
        {
            return "200".equals(code) || "0".equals(code) || "OK".equalsIgnoreCase(code);
        }
        String status = response.getString("status");
        if (StringUtils.isNotEmpty(status))
        {
            return "success".equalsIgnoreCase(status) || "ok".equalsIgnoreCase(status)
                || "accepted".equalsIgnoreCase(status);
        }
        return true;
    }

    private String resolveFailureMessage(JSONObject response)
    {
        if (response == null)
        {
            return null;
        }
        return firstNonBlank(response.getString("msg"), response.getString("message"), response.getString("error"));
    }

    private String firstNonBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value))
            {
                return value;
            }
        }
        return null;
    }
}
