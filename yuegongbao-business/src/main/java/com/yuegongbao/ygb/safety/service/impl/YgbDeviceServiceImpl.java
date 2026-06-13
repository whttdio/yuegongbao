package com.yuegongbao.ygb.safety.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceCommandLog;
import com.yuegongbao.ygb.safety.domain.YgbDeviceEvent;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.domain.vo.YgbCertCheckResult;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAiEventRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeDecision;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceBatchActionRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceHeartbeatRequest;
import com.yuegongbao.ygb.domain.vo.YgbInsuranceCheckResult;
import com.yuegongbao.ygb.domain.vo.YgbStubResponse;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.DeviceGateway;
import com.yuegongbao.ygb.integration.EmergencyCertClient;
import com.yuegongbao.ygb.integration.SocialClient;
import com.yuegongbao.ygb.safety.mapper.YgbDeviceCommandLogMapper;
import com.yuegongbao.ygb.safety.mapper.YgbDeviceEventMapper;
import com.yuegongbao.ygb.safety.mapper.YgbDeviceMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;
import com.yuegongbao.ygb.util.YgbRiskCalculator;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbDeviceServiceImpl implements IYgbDeviceService
{
    @Autowired
    private YgbDeviceMapper deviceMapper;

    @Autowired
    private YgbDeviceCommandLogMapper deviceCommandLogMapper;

    @Autowired
    private YgbDeviceEventMapper deviceEventMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private DeviceGateway deviceGateway;

    @Autowired
    private EmergencyCertClient emergencyCertClient;

    @Autowired
    private SocialClient socialClient;

    @Autowired
    private IYgbWarningService warningService;

    @Override
    public List<YgbDevice> selectDeviceList(YgbDevice device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    @Override
    public YgbDeviceSummary selectDeviceSummary(YgbDevice device)
    {
        return buildDeviceSummary(selectDeviceList(device), device);
    }

    @Override
    public YgbDevice selectDeviceById(Long deviceId)
    {
        return requireDevice(deviceId);
    }

    @Override
    public List<YgbDevice> selectDeviceSubledgerList(String viewCode, YgbDevice device)
    {
        return filterDevicesByViewCode(selectDeviceList(device), viewCode);
    }

    @Override
    public YgbDeviceSummary selectDeviceSubledgerSummary(String viewCode, YgbDevice device)
    {
        return buildDeviceSummary(selectDeviceSubledgerList(viewCode, device), device);
    }

    @Override
    public List<YgbDevice> selectIotCardList(YgbDevice device)
    {
        List<YgbDevice> list = selectDeviceList(device);
        List<YgbDevice> result = new ArrayList<>();
        for (YgbDevice item : list)
        {
            if (StringUtils.isNotEmpty(item.getSimCardNo()))
            {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public YgbDeviceSummary selectIotCardSummary(YgbDevice device)
    {
        return buildDeviceSummary(selectIotCardList(device), device);
    }

    @Override
    public List<YgbDevice> selectChipInventoryList(YgbDevice device)
    {
        List<YgbDevice> list = selectDeviceList(device);
        List<YgbDevice> result = new ArrayList<>();
        for (YgbDevice item : list)
        {
            if (StringUtils.isNotEmpty(item.getChipId()))
            {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public YgbDeviceSummary selectChipInventorySummary(YgbDevice device)
    {
        return buildDeviceSummary(selectChipInventoryList(device), device);
    }

    @Override
    public List<YgbDeviceEvent> selectUninstallAlertList(YgbDeviceEvent query)
    {
        List<YgbDeviceEvent> events = selectDeviceEventList(query);
        List<YgbDeviceEvent> result = new ArrayList<>();
        for (YgbDeviceEvent event : events)
        {
            String eventCode = StringUtils.defaultString(event.getEventCode());
            String eventContent = StringUtils.defaultString(event.getEventContent());
            if (eventCode.contains("UNINSTALL") || eventCode.contains("TAMPER") || eventContent.contains("拆卸"))
            {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public int insertDevice(YgbDevice device)
    {
        fillEnterpriseSnapshot(device);
        if (StringUtils.isEmpty(device.getDeviceStatus()))
        {
            device.setDeviceStatus("0");
        }
        if (StringUtils.isEmpty(device.getAuthStatus()))
        {
            device.setAuthStatus("0");
        }
        return deviceMapper.insertDevice(device);
    }

    @Override
    public int updateDevice(YgbDevice device)
    {
        fillEnterpriseSnapshot(device);
        return deviceMapper.updateDevice(device);
    }

    @Override
    public int deleteDeviceByIds(Long[] deviceIds, String updateBy)
    {
        return deviceMapper.deleteDeviceByIds(deviceIds, updateBy);
    }

    @Override
    public List<YgbDeviceCommandLog> selectDeviceCommandLogList(YgbDeviceCommandLog commandLog)
    {
        return deviceCommandLogMapper.selectDeviceCommandLogList(commandLog);
    }

    @Override
    public List<YgbDeviceEvent> selectDeviceEventList(YgbDeviceEvent deviceEvent)
    {
        return deviceEventMapper.selectDeviceEventList(deviceEvent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> lock(Long deviceId, String operator)
    {
        return operateCommand(deviceId, "1", "2", operator, "设备锁机");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unlock(Long deviceId, String operator)
    {
        return operateCommand(deviceId, "2", "1", operator, "设备解锁");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> authorize(YgbDeviceAuthorizeRequest request, String operator)
    {
        YgbDevice device = requireDevice(request.getDeviceId());
        YgbPerson person = personMapper.selectPersonById(request.getPersonId());
        if (person == null)
        {
            throw new ServiceException("授权人员不存在。");
        }

        YgbCertCheckResult certResult = emergencyCertClient.checkValidity(person.getIdCard(), request.getCertNo());
        YgbInsuranceCheckResult insuranceResult = socialClient.checkInsurance(person.getIdCard());
        YgbDeviceAuthorizeDecision decision = YgbRiskCalculator.decideDeviceAuthorize(certResult.isValid(),
            insuranceResult.isInsured(), certResult.getSourceMessage(), insuranceResult.getSourceMessage());

        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceId", device.getDeviceId());
        payload.put("deviceCode", device.getDeviceCode());
        payload.put("personId", person.getPersonId());
        payload.put("personName", person.getPersonName());
        payload.put("decision", decision.getReason());
        payload.put("certStatus", certResult.getCertStatus());
        payload.put("insured", insuranceResult.isInsured());
        YgbStubResponse stubResponse = deviceGateway.sendCommand(device.getDeviceCode(), "AUTHORIZE",
            JSON.toJSONString(payload));

        deviceMapper.updateAuthStatus(device.getDeviceId(), decision.isAuthorized() ? "1" : "2", operator);
        insertCommandLog(device, "3", decision.isAuthorized() ? "1" : "2", decision.getReason(), stubResponse,
            JSON.toJSONString(payload), operator);
        insertDeviceEvent(device, "3", decision.isAuthorized() ? "AUTH_PASS" : "AUTH_DENIED", decision.getReason(),
            "", decision.isAuthorized() ? "1" : "0", stubResponse, operator);

        if (!decision.isAuthorized())
        {
            YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
            warning.setWarnLevel("3");
            warning.setWarnType("DEVICE_AUTHORIZE_FAIL");
            warning.setSourceModule("DEVICE");
            warning.setTargetObjectId(device.getDeviceId());
            warning.setTargetType("2");
            warning.setEnterpriseId(device.getEnterpriseId());
            warning.setEnterpriseName(device.getEnterpriseName());
            warning.setRegionCode(device.getRegionCode());
            warning.setContent("设备授权失败，设备编码：" + device.getDeviceCode() + "，原因：" + decision.getReason());
            warningService.createWarningIfAbsent(warning, operator);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("authorized", decision.isAuthorized());
        result.put("reason", decision.getReason());
        result.put("certResult", certResult);
        result.put("insuranceResult", insuranceResult);
        result.put("gatewayResult", stubResponse);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> heartbeat(YgbDeviceHeartbeatRequest request, String operator)
    {
        YgbDevice device = resolveDevice(request.getDeviceId(), request.getDeviceCode());
        String nextStatus = StringUtils.isEmpty(request.getHeartbeatStatus()) ? "1" : request.getHeartbeatStatus();
        deviceMapper.updateHeartbeat(device.getDeviceId(), nextStatus, operator);
        YgbStubResponse stubResponse = buildSyntheticResponse("SUCCESS", "模拟设备心跳");
        insertDeviceEvent(device, "1", "HEARTBEAT", "模拟心跳上报", "", "1", stubResponse, operator);

        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", device.getDeviceId());
        result.put("deviceCode", device.getDeviceCode());
        result.put("deviceStatus", nextStatus);
        result.put("response", stubResponse);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> aiEvent(YgbDeviceAiEventRequest request, String operator)
    {
        YgbDevice device = resolveDevice(request.getDeviceId(), request.getDeviceCode());
        YgbStubResponse stubResponse = buildSyntheticResponse("SUCCESS", "模拟 AI 事件");
        String eventCode = StringUtils.isEmpty(request.getEventCode()) ? "AI_ALERT" : request.getEventCode();
        String eventContent = StringUtils.isEmpty(request.getEventContent()) ? "AI 识别到异常施工行为。"
            : request.getEventContent();
        insertDeviceEvent(device, "2", eventCode, eventContent, request.getEvidenceUrl(), "0", stubResponse,
            operator);

        YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
        warning.setWarnLevel("2");
        warning.setWarnType("DEVICE_AI_EVENT");
        warning.setSourceModule("DEVICE");
        warning.setTargetObjectId(device.getDeviceId());
        warning.setTargetType("2");
        warning.setEnterpriseId(device.getEnterpriseId());
        warning.setEnterpriseName(device.getEnterpriseName());
        warning.setRegionCode(device.getRegionCode());
        warning.setContent("设备 AI 事件告警，设备编码：" + device.getDeviceCode() + "，内容：" + eventContent);
        warning.setEvidenceUrl(request.getEvidenceUrl());
        warningService.createWarningIfAbsent(warning, operator);

        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", device.getDeviceId());
        result.put("deviceCode", device.getDeviceCode());
        result.put("eventCode", eventCode);
        result.put("response", stubResponse);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchLock(YgbDeviceBatchActionRequest request, String operator)
    {
        return batchOperate(request, operator, "lock");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchUnlock(YgbDeviceBatchActionRequest request, String operator)
    {
        return batchOperate(request, operator, "unlock");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchAuthorize(YgbDeviceBatchActionRequest request, String operator)
    {
        if (request.getPersonId() == null)
        {
            throw new ServiceException("personId cannot be empty when batch authorizing devices");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        int successCount = 0;
        for (Long deviceId : request.getDeviceIds())
        {
            YgbDeviceAuthorizeRequest authorizeRequest = new YgbDeviceAuthorizeRequest();
            authorizeRequest.setDeviceId(deviceId);
            authorizeRequest.setPersonId(request.getPersonId());
            authorizeRequest.setCertNo(request.getCertNo());
            Map<String, Object> row = authorize(authorizeRequest, operator);
            rows.add(row);
            if (Boolean.TRUE.equals(row.get("authorized")))
            {
                successCount++;
            }
        }
        result.put("totalCount", request.getDeviceIds().length);
        result.put("successCount", successCount);
        result.put("failedCount", request.getDeviceIds().length - successCount);
        result.put("rows", rows);
        return result;
    }

    private Map<String, Object> operateCommand(Long deviceId, String commandType, String nextStatus, String operator,
        String message)
    {
        YgbDevice device = requireDevice(deviceId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceId", device.getDeviceId());
        payload.put("deviceCode", device.getDeviceCode());
        payload.put("action", message);
        YgbStubResponse stubResponse = deviceGateway.sendCommand(device.getDeviceCode(), commandType,
            JSON.toJSONString(payload));
        deviceMapper.updateDeviceStatus(device.getDeviceId(), nextStatus, operator);
        insertCommandLog(device, commandType, "1", message, stubResponse, JSON.toJSONString(payload), operator);

        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", device.getDeviceId());
        result.put("deviceCode", device.getDeviceCode());
        result.put("response", stubResponse);
        return result;
    }

    private void fillEnterpriseSnapshot(YgbDevice device)
    {
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(device.getEnterpriseId());
        if (enterprise == null)
        {
            throw new ServiceException("设备所属企业不存在。");
        }
        device.setEnterpriseName(enterprise.getEnterpriseName());
        device.setRegionCode(enterprise.getRegionCode());
    }

    private YgbDevice requireDevice(Long deviceId)
    {
        YgbDevice device = deviceMapper.selectDeviceById(deviceId);
        if (device == null)
        {
            throw new ServiceException("设备不存在。");
        }
        return device;
    }

    private YgbDevice resolveDevice(Long deviceId, String deviceCode)
    {
        YgbDevice device = null;
        if (deviceId != null)
        {
            device = deviceMapper.selectDeviceById(deviceId);
        }
        if (device == null && StringUtils.isNotEmpty(deviceCode))
        {
            device = deviceMapper.selectDeviceByCode(deviceCode);
        }
        if (device == null)
        {
            throw new ServiceException("设备不存在。");
        }
        return device;
    }

    private void insertCommandLog(YgbDevice device, String commandType, String commandResult, String resultMessage,
        YgbStubResponse response, String payload, String operator)
    {
        YgbDeviceCommandLog log = new YgbDeviceCommandLog();
        log.setDeviceId(device.getDeviceId());
        log.setDeviceCode(device.getDeviceCode());
        log.setCommandType(commandType);
        log.setCommandPayload(payload);
        log.setCommandResult(commandResult);
        log.setResultMessage(resultMessage);
        log.setSourceSerialNo(response.getExternalSerialNo());
        log.setSourceStatus(response.getSourceStatus());
        log.setSourceMessage(response.getSourceMessage());
        log.setCallbackTime(response.getCallbackTime());
        log.setRawPayload(response.getRawPayload());
        log.setOperatorName(operator);
        log.setCreateBy(operator);
        deviceCommandLogMapper.insertDeviceCommandLog(log);
    }

    private void insertDeviceEvent(YgbDevice device, String eventType, String eventCode, String eventContent,
        String evidenceUrl, String eventStatus, YgbStubResponse response, String operator)
    {
        YgbDeviceEvent event = new YgbDeviceEvent();
        event.setDeviceId(device.getDeviceId());
        event.setDeviceCode(device.getDeviceCode());
        event.setEnterpriseId(device.getEnterpriseId());
        event.setEnterpriseName(device.getEnterpriseName());
        event.setRegionCode(device.getRegionCode());
        event.setEventType(eventType);
        event.setEventCode(eventCode);
        event.setEventContent(eventContent);
        event.setEvidenceUrl(evidenceUrl);
        event.setEventStatus(eventStatus);
        event.setSourceSerialNo(response.getExternalSerialNo());
        event.setSourceStatus(response.getSourceStatus());
        event.setSourceMessage(response.getSourceMessage());
        event.setCallbackTime(response.getCallbackTime());
        event.setRawPayload(response.getRawPayload());
        event.setEventTime(new Date());
        event.setCreateBy(operator);
        deviceEventMapper.insertDeviceEvent(event);
    }

    private YgbStubResponse buildSyntheticResponse(String sourceStatus, String sourceMessage)
    {
        YgbStubResponse response = new YgbStubResponse();
        response.setExternalSerialNo(String.valueOf(System.currentTimeMillis()));
        response.setSourceStatus(sourceStatus);
        response.setSourceMessage(sourceMessage);
        response.setCallbackTime(new Date());
        response.setRawPayload("{}");
        return response;
    }

    private YgbDeviceSummary buildDeviceSummary(List<YgbDevice> list, YgbDevice query)
    {
        YgbDeviceSummary summary = new YgbDeviceSummary();
        summary.setTotalCount(list.size());

        int onlineCount = 0;
        int lockedOrFaultCount = 0;
        int authDeniedCount = 0;
        int unauthorizedCount = 0;
        int chipDeviceCount = 0;
        int aiCameraCount = 0;
        for (YgbDevice item : list)
        {
            if ("1".equals(item.getDeviceStatus()))
            {
                onlineCount++;
            }
            if ("2".equals(item.getDeviceStatus()) || "3".equals(item.getDeviceStatus()))
            {
                lockedOrFaultCount++;
            }
            if ("2".equals(item.getAuthStatus()))
            {
                authDeniedCount++;
            }
            if ("0".equals(item.getAuthStatus()))
            {
                unauthorizedCount++;
            }
            if ("2".equals(item.getDeviceType()))
            {
                chipDeviceCount++;
            }
            if ("3".equals(item.getDeviceType()))
            {
                aiCameraCount++;
            }
        }
        summary.setOnlineCount(onlineCount);
        summary.setLockedOrFaultCount(lockedOrFaultCount);
        summary.setAuthDeniedCount(authDeniedCount);
        summary.setUnauthorizedCount(unauthorizedCount);
        summary.setChipDeviceCount(chipDeviceCount);
        summary.setAiCameraCount(aiCameraCount);
        summary.setYgbExplanation(buildYgbExplanation(query, summary));
        summary.setAzbExplanation(buildAzbExplanation(query, summary));
        return summary;
    }

    private List<YgbDevice> filterDevicesByViewCode(List<YgbDevice> list, String viewCode)
    {
        if (StringUtils.isEmpty(viewCode))
        {
            return list;
        }
        String expectedType = resolveDeviceTypeByViewCode(viewCode);
        if (expectedType == null)
        {
            return list;
        }
        List<YgbDevice> result = new ArrayList<>();
        for (YgbDevice item : list)
        {
            if (Objects.equals(expectedType, item.getDeviceType()))
            {
                result.add(item);
            }
        }
        return result;
    }

    private String resolveDeviceTypeByViewCode(String viewCode)
    {
        if ("ATTENDANCE".equalsIgnoreCase(viewCode))
        {
            return "1";
        }
        if ("CHIP".equalsIgnoreCase(viewCode))
        {
            return "2";
        }
        if ("AI".equalsIgnoreCase(viewCode))
        {
            return "3";
        }
        return null;
    }

    private Map<String, Object> batchOperate(YgbDeviceBatchActionRequest request, String operator, String action)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Long deviceId : request.getDeviceIds())
        {
            Map<String, Object> row = "unlock".equals(action) ? unlock(deviceId, operator) : lock(deviceId, operator);
            rows.add(row);
        }
        result.put("totalCount", request.getDeviceIds().length);
        result.put("action", action);
        result.put("rows", rows);
        return result;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbDevice query, YgbDeviceSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("unauthorized", "未授权设备", summary.getUnauthorizedCount(), 0,
            "未授权设备无法稳定承接人员和现场数据，应先补授权再进入后续办理链路。", "device", "device",
            "530.1 设备办理解释", baseQuery));
        list.add(explanationItem("authDenied", "授权拒绝", summary.getAuthDeniedCount(), 0,
            "授权拒绝会直接中断设备接入链，应优先回看证书校验和授权留痕。", "device", "device",
            "530.1 设备办理解释", baseQuery));
        list.add(explanationItem("fault", "锁定/故障设备", summary.getLockedOrFaultCount(), 0,
            "锁定或故障设备会影响采集、心跳和异常留痕，应优先恢复可用状态。", "device", "device",
            "530.1 设备办理解释", baseQuery));
        list.add(explanationItem("aiCamera", "AI 摄像头", summary.getAiCameraCount(), "持续在线",
            "AI 摄像头是现场异常留痕关键采集点，应单独跟踪接入和回写稳定性。", "device", "device",
            "530.1 设备办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbDevice query, YgbDeviceSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        int offlineCount = Math.max((summary.getTotalCount() == null ? 0 : summary.getTotalCount())
            - (summary.getOnlineCount() == null ? 0 : summary.getOnlineCount()), 0);
        list.add(explanationItem("fault", "故障与锁定设备", summary.getLockedOrFaultCount(), 0,
            "故障与锁定设备会直接削弱现场治理感知能力，应优先纳入监管处置链。", "device", "device",
            "6.1 设备治理解释", baseQuery));
        list.add(explanationItem("unauthorized", "未授权设备", summary.getUnauthorizedCount(), 0,
            "未授权设备越多，区域感知链越不完整，应优先补齐准入和授权。", "device", "device",
            "6.1 设备治理解释", baseQuery));
        list.add(explanationItem("authDenied", "授权拒绝设备", summary.getAuthDeniedCount(), 0,
            "授权拒绝说明证书或准入链条存在明显风险，适合作为治理复核入口。", "device", "device",
            "6.1 设备治理解释", baseQuery));
        list.add(explanationItem("offline", "离线设备", offlineCount, 0,
            "离线设备会降低区域实时治理能力，应持续压降离线缺口。", "device", "device", "6.1 设备治理解释",
            baseQuery));
        list.add(explanationItem("ai", "AI 感知设备", summary.getAiCameraCount(), "持续在线",
            "AI 感知设备更适合作为事故预防和现场隐患治理的协同入口。", "device", "device", "6.1 设备治理解释",
            mergeQuery(baseQuery, "deviceType", "3")));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbDevice query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getDeviceType()))
        {
            map.put("deviceType", query.getDeviceType());
        }
        if (StringUtils.isNotEmpty(query.getDeviceStatus()))
        {
            map.put("deviceStatus", query.getDeviceStatus());
        }
        if (StringUtils.isNotEmpty(query.getAuthStatus()))
        {
            map.put("authStatus", query.getAuthStatus());
        }
        return map;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> baseQuery)
    {
        Map<String, Object> defaultQuery = new LinkedHashMap<>(baseQuery);
        defaultQuery.put("focusKey", focusKey);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", focusKey);
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }

    private Map<String, Object> mergeQuery(Map<String, Object> baseQuery, String key, Object value)
    {
        Map<String, Object> query = new LinkedHashMap<>(baseQuery);
        if (value != null && StringUtils.isNotEmpty(String.valueOf(value)))
        {
            query.put(key, value);
        }
        return query;
    }
}
