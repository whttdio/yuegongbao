package com.yuegongbao.ygb.safety.service.impl;

import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceCommandLog;
import com.yuegongbao.ygb.safety.domain.YgbDeviceEvent;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.domain.vo.YgbCertCheckResult;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeRequest;
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
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbDeviceServiceImplTest
{
    @Mock
    private YgbDeviceMapper deviceMapper;

    @Mock
    private YgbDeviceCommandLogMapper deviceCommandLogMapper;

    @Mock
    private YgbDeviceEventMapper deviceEventMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private YgbPersonMapper personMapper;

    @Mock
    private DeviceGateway deviceGateway;

    @Mock
    private EmergencyCertClient emergencyCertClient;

    @Mock
    private SocialClient socialClient;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbDeviceServiceImpl service;

    @Test
    void authorizeDeniedPersistsFailureLogEventAndWarning()
    {
        when(deviceMapper.selectDeviceById(11L)).thenReturn(device());
        when(personMapper.selectPersonById(21L)).thenReturn(person());
        when(emergencyCertClient.checkValidity("ID-21", "CERT-1")).thenReturn(cert(false, "INVALID", "CERT_INVALID"));
        when(socialClient.checkInsurance("ID-21")).thenReturn(insurance(true, "INSURED"));
        when(deviceGateway.sendCommand(eq("DEV-001"), eq("AUTHORIZE"), anyString())).thenReturn(stub("SER-1"));

        Map<String, Object> result = service.authorize(request(), "tester");

        assertFalse((Boolean) result.get("authorized"));
        assertEquals("CERT_INVALID", result.get("reason"));
        verify(deviceMapper).updateAuthStatus(11L, "2", "tester");

        ArgumentCaptor<YgbDeviceCommandLog> commandCaptor = ArgumentCaptor.forClass(YgbDeviceCommandLog.class);
        verify(deviceCommandLogMapper).insertDeviceCommandLog(commandCaptor.capture());
        assertEquals("3", commandCaptor.getValue().getCommandType());
        assertEquals("2", commandCaptor.getValue().getCommandResult());
        assertEquals("CERT_INVALID", commandCaptor.getValue().getResultMessage());

        ArgumentCaptor<YgbDeviceEvent> eventCaptor = ArgumentCaptor.forClass(YgbDeviceEvent.class);
        verify(deviceEventMapper).insertDeviceEvent(eventCaptor.capture());
        assertEquals("AUTH_DENIED", eventCaptor.getValue().getEventCode());
        assertEquals("0", eventCaptor.getValue().getEventStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("DEVICE_AUTHORIZE_FAIL", warningCaptor.getValue().getWarnType());
        assertTrue(warningCaptor.getValue().getContent().contains("DEV-001"));
        assertTrue(warningCaptor.getValue().getContent().contains("CERT_INVALID"));
    }

    @Test
    void authorizeSuccessMarksPassWithoutWarning()
    {
        when(deviceMapper.selectDeviceById(11L)).thenReturn(device());
        when(personMapper.selectPersonById(21L)).thenReturn(person());
        when(emergencyCertClient.checkValidity("ID-21", "CERT-1")).thenReturn(cert(true, "VALID", "OK"));
        when(socialClient.checkInsurance("ID-21")).thenReturn(insurance(true, "INSURED"));
        when(deviceGateway.sendCommand(eq("DEV-001"), eq("AUTHORIZE"), anyString())).thenReturn(stub("SER-2"));

        Map<String, Object> result = service.authorize(request(), "tester");

        assertTrue((Boolean) result.get("authorized"));
        assertEquals("授权通过", result.get("reason"));
        verify(deviceMapper).updateAuthStatus(11L, "1", "tester");

        ArgumentCaptor<YgbDeviceCommandLog> commandCaptor = ArgumentCaptor.forClass(YgbDeviceCommandLog.class);
        verify(deviceCommandLogMapper).insertDeviceCommandLog(commandCaptor.capture());
        assertEquals("1", commandCaptor.getValue().getCommandResult());

        ArgumentCaptor<YgbDeviceEvent> eventCaptor = ArgumentCaptor.forClass(YgbDeviceEvent.class);
        verify(deviceEventMapper).insertDeviceEvent(eventCaptor.capture());
        assertEquals("AUTH_PASS", eventCaptor.getValue().getEventCode());
        assertEquals("1", eventCaptor.getValue().getEventStatus());

        verify(warningService, never()).createWarningIfAbsent(any(YgbWarningCreateRequest.class), anyString());
    }

    private YgbDeviceAuthorizeRequest request()
    {
        YgbDeviceAuthorizeRequest request = new YgbDeviceAuthorizeRequest();
        request.setDeviceId(11L);
        request.setPersonId(21L);
        request.setCertNo("CERT-1");
        return request;
    }

    private YgbDevice device()
    {
        YgbDevice device = new YgbDevice();
        device.setDeviceId(11L);
        device.setDeviceCode("DEV-001");
        device.setEnterpriseId(10L);
        device.setEnterpriseName("ENT-10");
        device.setRegionCode("440100");
        return device;
    }

    private YgbPerson person()
    {
        YgbPerson person = new YgbPerson();
        person.setPersonId(21L);
        person.setPersonName("P21");
        person.setIdCard("ID-21");
        return person;
    }

    private YgbCertCheckResult cert(boolean valid, String certStatus, String sourceMessage)
    {
        YgbCertCheckResult result = new YgbCertCheckResult();
        result.setValid(valid);
        result.setCertStatus(certStatus);
        result.setSourceMessage(sourceMessage);
        return result;
    }

    private YgbInsuranceCheckResult insurance(boolean insured, String sourceMessage)
    {
        YgbInsuranceCheckResult result = new YgbInsuranceCheckResult();
        result.setInsured(insured);
        result.setSourceMessage(sourceMessage);
        return result;
    }

    private YgbStubResponse stub(String serialNo)
    {
        YgbStubResponse response = new YgbStubResponse();
        response.setExternalSerialNo(serialNo);
        response.setSourceStatus("SUCCESS");
        response.setSourceMessage("OK");
        response.setCallbackTime(new Date());
        response.setRawPayload("{}");
        return response;
    }
}
