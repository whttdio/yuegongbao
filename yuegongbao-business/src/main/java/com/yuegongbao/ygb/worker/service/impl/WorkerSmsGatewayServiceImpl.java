package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.service.ISysConfigService;
import com.yuegongbao.ygb.worker.service.WorkerSmsGatewayService;

@Service
public class WorkerSmsGatewayServiceImpl implements WorkerSmsGatewayService
{
    private static final String CONFIG_ENABLED = "ygb.worker.sms.gateway.enabled";

    private static final String CONFIG_URL = "ygb.worker.sms.gateway.url";

    private static final String CONFIG_AUTH_TOKEN = "ygb.worker.sms.gateway.authToken";

    private static final String CONFIG_PROVIDER = "ygb.worker.sms.gateway.provider";

    private static final String CONFIG_SIGN_NAME = "ygb.worker.sms.gateway.signName";

    private static final String CONFIG_TEMPLATE_CODE = "ygb.worker.sms.gateway.templateCode";

    private static final String CONFIG_TEMPLATE = "ygb.worker.sms.gateway.template";

    private static final String DEFAULT_TEMPLATE = "【粤工保】您的登录验证码为${code}，${expireMinutes}分钟内有效。";

    @Autowired
    private ISysConfigService sysConfigService;

    @Override
    public Map<String, Object> sendLoginCode(String mobile, String code)
    {
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code))
        {
            throw new ServiceException("短信发送参数不完整。");
        }

        String enabled = firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_ENABLED), "false");
        if (!"true".equalsIgnoreCase(enabled) && !"1".equals(enabled))
        {
            throw new ServiceException("短信网关未启用，请先配置 ygb.worker.sms.gateway.enabled=true。");
        }

        String gatewayUrl = sysConfigService.selectConfigByKey(CONFIG_URL);
        if (StringUtils.isEmpty(gatewayUrl))
        {
            throw new ServiceException("短信网关地址未配置，请先设置 ygb.worker.sms.gateway.url。");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("provider", firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_PROVIDER), "custom"));
        requestBody.put("mobile", mobile);
        requestBody.put("code", code);
        requestBody.put("scene", "worker-login");
        requestBody.put("signName", firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_SIGN_NAME), "粤工保"));
        requestBody.put("templateCode", firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_TEMPLATE_CODE), "worker-login"));
        requestBody.put("content", buildSmsContent(code));
        requestBody.put("templateParams", Map.of("code", code, "expireMinutes", 5));

        String requestJson = JSON.toJSONString(requestBody);
        String responseBody = sendJson(gatewayUrl, requestJson, sysConfigService.selectConfigByKey(CONFIG_AUTH_TOKEN));
        if (StringUtils.isEmpty(responseBody))
        {
            throw new ServiceException("短信网关无响应或调用失败，请确认网关地址、鉴权和网络连通性。");
        }

        JSONObject parsedResponse = parseResponse(responseBody);
        if (!isGatewayCallSuccessful(parsedResponse))
        {
            throw new ServiceException(firstNonBlank(resolveFailureMessage(parsedResponse), "短信网关返回发送失败。"));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("gatewayUrl", gatewayUrl);
        result.put("provider", requestBody.get("provider"));
        result.put("responseBody", responseBody);
        return result;
    }

    private String buildSmsContent(String code)
    {
        String template = firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_TEMPLATE), DEFAULT_TEMPLATE);
        return template.replace("${code}", code).replace("${expireMinutes}", "5");
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
            throw new ServiceException("调用短信网关失败：" + e.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    @SuppressWarnings("unused")
    private String sendForm(String gatewayUrl, Map<String, Object> requestBody)
    {
        StringBuilder paramBuilder = new StringBuilder();
        try
        {
            for (Map.Entry<String, Object> entry : requestBody.entrySet())
            {
                if (paramBuilder.length() > 0)
                {
                    paramBuilder.append("&");
                }
                paramBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
                paramBuilder.append("=");
                paramBuilder.append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.name()));
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("拼装短信表单参数失败：" + e.getMessage());
        }
        throw new ServiceException("当前仅支持 JSON 短信网关，请配置 JSON 接收服务。");
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
