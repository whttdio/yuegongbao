package com.yuegongbao.ygb.integration;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.domain.vo.YgbUnionSyncResponse;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 工会法律援助系统 HTTP 客户端。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "remote")
public class UnionAidHttpClient implements UnionAidClient
{
    @Value("${ygb.integration.union.complaint-url:}")
    private String complaintUrl;

    @Value("${ygb.integration.union.legal-url:}")
    private String legalUrl;

    @Value("${ygb.integration.union.auth-token:}")
    private String authToken;

    @Value("${ygb.integration.union.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${ygb.integration.union.read-timeout-ms:8000}")
    private int readTimeoutMs;

    @Override
    public YgbUnionSyncResponse submitComplaint(WorkerComplaint complaint)
    {
        if (complaint == null)
        {
            throw new ServiceException("投诉同步工会失败：投诉信息不能为空。");
        }
        return postJson(complaintUrl, complaintPayload(complaint));
    }

    @Override
    public YgbUnionSyncResponse submitLegalConsult(WorkerLegalConsult consult)
    {
        if (consult == null)
        {
            throw new ServiceException("法律咨询同步工会失败：咨询信息不能为空。");
        }
        return postJson(legalUrl, legalConsultPayload(consult));
    }

    private YgbUnionSyncResponse postJson(String urlText, JSONObject payload)
    {
        if (StringUtils.isEmpty(urlText))
        {
            throw new ServiceException("工会联动地址未配置，请先设置 ygb.integration.union 对应 URL。");
        }

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(urlText.trim());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if (StringUtils.isNotEmpty(authToken))
            {
                connection.setRequestProperty("Authorization", authToken.trim());
            }

            byte[] requestBody = JSON.toJSONString(payload).getBytes(StandardCharsets.UTF_8);
            try (OutputStream outputStream = connection.getOutputStream())
            {
                outputStream.write(requestBody);
                outputStream.flush();
            }

            int httpStatus = connection.getResponseCode();
            String responseText = readBody(httpStatus >= 200 && httpStatus < 400
                ? connection.getInputStream() : connection.getErrorStream());
            JSONObject responseObject = parseObject(responseText);

            YgbUnionSyncResponse response = new YgbUnionSyncResponse();
            response.setSuccess(httpStatus >= 200 && httpStatus < 300);
            response.setSyncStatus(response.isSuccess() ? firstNonBlank(responseObject.getString("syncStatus"), "SUCCESS")
                : firstNonBlank(responseObject.getString("syncStatus"), "FAIL"));
            response.setSyncMessage(firstNonBlank(responseObject.getString("syncMessage"),
                responseObject.getString("message"),
                response.isSuccess() ? "工会法律援助系统已受理。" : "工会法律援助系统未受理。"));
            response.setTicketNo(firstNonBlank(responseObject.getString("ticketNo"), responseObject.getString("orderNo")));
            response.setExternalSerialNo(firstNonBlank(responseObject.getString("externalSerialNo"),
                responseObject.getString("traceId"), responseObject.getString("requestId")));
            response.setCallbackTime(new Date());
            response.setRawPayload(StringUtils.isEmpty(responseText) ? "{}" : responseText);
            return response;
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("工会联动调用失败：" + ex.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    private JSONObject complaintPayload(WorkerComplaint complaint)
    {
        JSONObject payload = new JSONObject();
        payload.put("bizType", "complaint");
        payload.put("bizId", complaint.getComplaintId());
        payload.put("userId", complaint.getUserId());
        payload.put("personId", complaint.getPersonId());
        payload.put("personName", complaint.getPersonName());
        payload.put("enterpriseId", complaint.getEnterpriseId());
        payload.put("enterpriseName", complaint.getEnterpriseName());
        payload.put("complaintType", complaint.getComplaintType());
        payload.put("title", complaint.getTitle());
        payload.put("content", complaint.getContent());
        payload.put("contactMobile", complaint.getContactMobile());
        payload.put("anonymousFlag", complaint.getAnonymousFlag());
        payload.put("attachments", complaint.getAttachments());
        payload.put("createTime", complaint.getCreateTime());
        return payload;
    }

    private JSONObject legalConsultPayload(WorkerLegalConsult consult)
    {
        JSONObject payload = new JSONObject();
        payload.put("bizType", "legal-consult");
        payload.put("bizId", consult.getConsultId());
        payload.put("userId", consult.getUserId());
        payload.put("personId", consult.getPersonId());
        payload.put("personName", consult.getPersonName());
        payload.put("enterpriseId", consult.getEnterpriseId());
        payload.put("enterpriseName", consult.getEnterpriseName());
        payload.put("consultType", consult.getConsultType());
        payload.put("title", consult.getTitle());
        payload.put("content", consult.getContent());
        payload.put("contactMobile", consult.getContactMobile());
        payload.put("attachments", consult.getAttachments());
        payload.put("createTime", consult.getCreateTime());
        return payload;
    }

    private String readBody(InputStream inputStream) throws Exception
    {
        if (inputStream == null)
        {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private JSONObject parseObject(String jsonText)
    {
        if (StringUtils.isEmpty(jsonText))
        {
            return new JSONObject();
        }
        try
        {
            JSONObject object = JSON.parseObject(jsonText);
            return object == null ? new JSONObject() : object;
        }
        catch (Exception ignored)
        {
            return new JSONObject();
        }
    }

    private String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return "";
        }
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value))
            {
                return value.trim();
            }
        }
        return "";
    }
}
