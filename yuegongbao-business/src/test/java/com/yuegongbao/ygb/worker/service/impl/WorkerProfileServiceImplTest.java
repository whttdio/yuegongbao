package com.yuegongbao.ygb.worker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.system.service.ISysConfigService;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerFeedback;
import com.yuegongbao.ygb.worker.domain.WorkerPushTestRecord;
import com.yuegongbao.ygb.worker.domain.WorkerRealnameApply;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.WorkerSetting;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushTestRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerRealnameSubmitRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerResumeSaveRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerPushGatewayService;

@ExtendWith(MockitoExtension.class)
class WorkerProfileServiceImplTest
{
    @Mock
    private WorkerProfileMapper workerProfileMapper;

    @Mock
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Mock
    private YgbContractMapper contractMapper;

    @Mock
    private WorkerMessageService workerMessageService;

    @Mock
    private WorkerPushGatewayService workerPushGatewayService;

    @Mock
    private ISysConfigService sysConfigService;

    @InjectMocks
    private WorkerProfileServiceImpl service;

    @Test
    void sendPushTestPersistsSuccessRecord()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerSetting setting = pushSetting();
        WorkerPushTestRequest request = pushTestRequest();

        when(workerProfileMapper.selectWorkerSetting(eq(user.getUserId()))).thenReturn(setting);
        mockGatewayConfig("true", "http://push-gateway.local/api/push/send", "unipush");

        Map<String, Object> gatewayResult = new LinkedHashMap<>();
        gatewayResult.put("gatewayUrl", "http://push-gateway.local/api/push/send");
        gatewayResult.put("provider", "unipush");
        gatewayResult.put("requestBody", Map.of("provider", "unipush", "clientId", "cid-12345678"));
        gatewayResult.put("responseBody", "{\"code\":0,\"msg\":\"accepted\"}");
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("Push Test Title"), eq("Push test content"), any(), any()))
            .thenReturn(gatewayResult);

        Map<String, Object> result = service.sendPushTest(worker, user, request);

        assertEquals("SUCCESS", result.get("testStatus"));
        assertEquals("/pages/notice/detail", result.get("targetPath"));
        assertEquals("Open Detail", result.get("actionLabel"));
        assertEquals("Test Source", result.get("sourceLabel"));
        assertEquals("unipush", result.get("provider"));

        ArgumentCaptor<WorkerPushTestRecord> recordCaptor = ArgumentCaptor.forClass(WorkerPushTestRecord.class);
        verify(workerProfileMapper).insertWorkerPushTestRecord(recordCaptor.capture());
        WorkerPushTestRecord record = recordCaptor.getValue();
        assertEquals(user.getUserId(), record.getUserId());
        assertEquals(worker.getPersonId(), record.getPersonId());
        assertEquals("SUCCESS", record.getTestStatus());
        assertEquals("/pages/notice/detail", record.getTargetPath());
        assertTrue(record.getTargetQueryText().contains("\"noticeId\":1001"));
        assertEquals("Open Detail", record.getActionLabel());
        assertEquals("Test Source", record.getSourceLabel());
        assertEquals("unipush", record.getGatewayProvider());
        assertEquals("http://push-gateway.local/api/push/send", record.getGatewayUrl());
        assertTrue(record.getRequestBody().contains("cid-12345678"));
        assertTrue(record.getResponseBody().contains("accepted"));
        assertNotNull(record.getTraceId());
        assertTrue(record.getTraceId().startsWith("worker-push-"));
    }

    @Test
    void sendPushTestPersistsFailRecordBeforeThrowing()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerSetting setting = pushSetting();
        WorkerPushTestRequest request = pushTestRequest();

        when(workerProfileMapper.selectWorkerSetting(eq(user.getUserId()))).thenReturn(setting);
        mockGatewayConfig("true", "http://push-gateway.local/api/push/send", "custom");
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("Push Test Title"), eq("Push test content"), any(), any()))
            .thenThrow(new ServiceException("gateway down"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.sendPushTest(worker, user, request));

        assertTrue(ex.getMessage().contains("gateway down"));
        assertTrue(ex.getMessage().contains("traceId="));

        ArgumentCaptor<WorkerPushTestRecord> recordCaptor = ArgumentCaptor.forClass(WorkerPushTestRecord.class);
        verify(workerProfileMapper).insertWorkerPushTestRecord(recordCaptor.capture());
        WorkerPushTestRecord record = recordCaptor.getValue();
        assertEquals("FAIL", record.getTestStatus());
        assertEquals("gateway down", record.getStatusMessage());
        assertEquals("/pages/notice/detail", record.getTargetPath());
        assertTrue(record.getRequestBody().contains("Push Test Title"));
        assertEquals("", record.getResponseBody());
    }

    @Test
    void getSettingsIncludesLatestServerPushTestRecord()
    {
        YgbPerson worker = worker();
        WorkerSetting setting = pushSetting();
        setting.setNotifyEnabled("0");
        WorkerPushTestRecord latest = latestRecord();
        WorkerPushTestRecord success = successRecord();

        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(setting);
        when(workerProfileMapper.selectLatestWorkerPushTestRecord(eq(1001L))).thenReturn(latest);
        when(workerProfileMapper.selectWorkerPushTestRecordList(eq(1001L), eq(5))).thenReturn(List.of(latest, success));
        mockGatewayConfig("true", "http://push-gateway.local/api/push/send", "getui");

        Map<String, Object> result = service.getSettings(worker, 1001L);

        assertFalse((Boolean) result.get("notifyEnabled"));
        assertTrue((Boolean) result.get("pushRegistered"));
        assertTrue((Boolean) result.get("pushGatewayReady"));

        Object latestValue = result.get("lastServerPushTest");
        assertInstanceOf(Map.class, latestValue);
        @SuppressWarnings("unchecked")
        Map<String, Object> latestMap = (Map<String, Object>) latestValue;
        assertEquals("trace-001", latestMap.get("traceId"));
        assertEquals("FAIL", latestMap.get("testStatus"));
        assertEquals("gateway timeout", latestMap.get("statusMessage"));
        assertEquals("gateway timeout", latestMap.get("testStatusText"));
        assertEquals("/pages/notice/detail", latestMap.get("targetPath"));
        assertEquals("Test Source", latestMap.get("sourceLabel"));
        assertEquals("getui", latestMap.get("provider"));
        assertEquals("http://push-gateway.local/api/push/send", latestMap.get("gatewayUrl"));

        Object historyValue = result.get("lastServerPushTests");
        assertInstanceOf(List.class, historyValue);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> historyList = (List<Map<String, Object>>) historyValue;
        assertEquals(2, historyList.size());
        assertEquals("trace-001", historyList.get(0).get("traceId"));
        assertEquals("FAIL", historyList.get(0).get("testStatus"));
        assertEquals("trace-002", historyList.get(1).get("traceId"));
        assertEquals("SUCCESS", historyList.get(1).get("testStatus"));
    }

    @Test
    void updateFeedbackHandleRequiresOpinionWhenClosing()
    {
        WorkerMessageHandleRequest request = new WorkerMessageHandleRequest();
        request.setStatus("1");

        ServiceException ex = assertThrows(ServiceException.class,
            () -> service.updateFeedbackHandle(10L, request, "operator"));

        assertEquals("标记已处理时必须填写处理意见。", ex.getMessage());
    }

    @Test
    void updateFeedbackHandleCanReopenFeedback()
    {
        WorkerFeedback feedback = new WorkerFeedback();
        feedback.setFeedbackId(10L);
        feedback.setStatus("1");
        when(workerProfileMapper.selectWorkerFeedbackById(eq(10L))).thenReturn(feedback);
        when(workerProfileMapper.updateWorkerFeedbackHandle(any(WorkerFeedback.class))).thenReturn(1);

        WorkerMessageHandleRequest request = new WorkerMessageHandleRequest();
        request.setStatus("0");
        request.setReplyContent("需要继续跟进");

        Map<String, Object> result = service.updateFeedbackHandle(10L, request, "operator");

        assertEquals("0", result.get("status"));
        assertEquals("待处理", result.get("statusText"));
        assertEquals(Boolean.TRUE, result.get("updated"));

        ArgumentCaptor<WorkerFeedback> feedbackCaptor = ArgumentCaptor.forClass(WorkerFeedback.class);
        verify(workerProfileMapper).updateWorkerFeedbackHandle(feedbackCaptor.capture());
        WorkerFeedback updated = feedbackCaptor.getValue();
        assertEquals(10L, updated.getFeedbackId());
        assertEquals("0", updated.getStatus());
        assertEquals("operator", updated.getUpdateBy());
        assertEquals("反馈处理：重新打开；需要继续跟进", updated.getRemark());
    }

    @Test
    void saveResumeStoresStructuredCertificateJson()
    {
        YgbPerson worker = worker();
        worker.setJobType("Welder");
        SysUser user = user();
        WorkerResumeSaveRequest request = new WorkerResumeSaveRequest();
        request.setExpectedJob("Welder");
        request.setExpectedCity("Shenzhen");
        request.setExpectedSalary("8000-10000");
        request.setSkillTags("welding,height");
        request.setCertificateText("[{\"certificateName\":\"Special Operation\",\"certificateNo\":\"GA-001\",\"issuer\":\"Emergency Bureau\",\"expireDate\":\"2027-12-31\"}]");
        request.setIntro("Ready to start immediately");

        when(workerProfileMapper.selectWorkerResume(eq(user.getUserId()))).thenReturn(null);

        Map<String, Object> result = service.saveResume(worker, user, request);

        ArgumentCaptor<WorkerResume> resumeCaptor = ArgumentCaptor.forClass(WorkerResume.class);
        verify(workerProfileMapper).insertWorkerResume(resumeCaptor.capture());
        WorkerResume savedResume = resumeCaptor.getValue();
        assertTrue(savedResume.getCertificateText().contains("\"certificateName\":\"Special Operation\""));
        Object certificateListValue = result.get("certificateList");
        assertInstanceOf(List.class, certificateListValue);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> certificateList = (List<Map<String, Object>>) certificateListValue;
        assertEquals(1, certificateList.size());
        assertEquals("Special Operation", certificateList.get(0).get("certificateName"));
        assertEquals("GA-001", certificateList.get(0).get("certificateNo"));
    }

    @Test
    void getResumeParsesLegacyCertificateTextToCertificateList()
    {
        WorkerResume resume = new WorkerResume();
        resume.setResumeId(1L);
        resume.setPersonName("Zhang San");
        resume.setMobile("13800000000");
        resume.setJobType("Welder");
        resume.setExpectedJob("Welder");
        resume.setExpectedCity("Shenzhen");
        resume.setExpectedSalary("9000");
        resume.setSkillTags("welding");
        resume.setCertificateText("Special Operation Certificate, Safety Certificate");
        resume.setIntro("Experienced worker");

        when(workerProfileMapper.selectWorkerResume(eq(1001L))).thenReturn(resume);

        Map<String, Object> result = service.getResume(worker(), 1001L);

        Object certificateListValue = result.get("certificateList");
        assertInstanceOf(List.class, certificateListValue);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> certificateList = (List<Map<String, Object>>) certificateListValue;
        assertEquals(2, certificateList.size());
        assertEquals("Special Operation Certificate", certificateList.get(0).get("certificateName"));
        assertEquals("Safety Certificate", certificateList.get(1).get("certificateName"));
    }

    @Test
    void submitRealnameApplyPersistsPendingRecord()
    {
        YgbPerson worker = worker();
        worker.setPersonName("Zhang San");
        worker.setMobile("13800000000");
        worker.setIdCard("440111199001011234");
        SysUser user = user();
        WorkerRealnameSubmitRequest request = realnameRequest();

        when(workerProfileMapper.selectLatestWorkerRealnameApply(eq(user.getUserId()))).thenReturn(null);

        Map<String, Object> result = service.submitRealnameApply(worker, user, request);

        assertEquals("0", result.get("applyStatus"));

        ArgumentCaptor<WorkerRealnameApply> applyCaptor = ArgumentCaptor.forClass(WorkerRealnameApply.class);
        verify(workerProfileMapper).insertWorkerRealnameApply(applyCaptor.capture());
        WorkerRealnameApply apply = applyCaptor.getValue();
        assertEquals(user.getUserId(), apply.getUserId());
        assertEquals(worker.getPersonId(), apply.getPersonId());
        assertEquals("Zhang San", apply.getPersonName());
        assertEquals("13800000000", apply.getMobile());
        assertEquals("440111199001011234", apply.getIdCard());
        assertEquals("0", apply.getApplyStatus());
        assertEquals("worker-uniapp", apply.getSourceModule());
    }

    @Test
    void submitRealnameApplyRejectsInvalidMobile()
    {
        YgbPerson worker = worker();
        worker.setPersonName("Zhang San");
        worker.setMobile("13800000000");
        worker.setIdCard("440111199001011234");
        WorkerRealnameSubmitRequest request = realnameRequest();
        request.setMobile("123456");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.submitRealnameApply(worker, user(), request));

        assertEquals("Mobile format is invalid. Use an 11-digit mobile number.", ex.getMessage());
    }

    @Test
    void submitRealnameApplyRejectsArchiveMismatch()
    {
        YgbPerson worker = worker();
        worker.setPersonName("Li Si");
        worker.setMobile("13800000000");
        worker.setIdCard("440111199001011234");
        WorkerRealnameSubmitRequest request = realnameRequest();

        ServiceException ex = assertThrows(ServiceException.class, () -> service.submitRealnameApply(worker, user(), request));

        assertEquals("Person name does not match the worker archive.", ex.getMessage());
    }

    @Test
    void submitRealnameApplyRejectsPendingDuplicate()
    {
        WorkerRealnameApply latest = new WorkerRealnameApply();
        latest.setApplyStatus("0");
        when(workerProfileMapper.selectLatestWorkerRealnameApply(eq(1001L))).thenReturn(latest);

        ServiceException ex = assertThrows(ServiceException.class,
            () -> service.submitRealnameApply(workerWithArchive(), user(), realnameRequest()));

        assertTrue(ex.getMessage().contains("待审核"));
    }

    private void mockGatewayConfig(String enabled, String url, String provider)
    {
        when(sysConfigService.selectConfigByKey("ygb.worker.push.gateway.enabled")).thenReturn(enabled);
        when(sysConfigService.selectConfigByKey("ygb.worker.push.gateway.url")).thenReturn(url);
        when(sysConfigService.selectConfigByKey("ygb.worker.push.gateway.provider")).thenReturn(provider);
    }

    private YgbPerson worker()
    {
        YgbPerson worker = new YgbPerson();
        worker.setPersonId(2001L);
        worker.setPersonName("Worker User");
        worker.setMobile("13800000000");
        worker.setEnterpriseName("Demo Enterprise");
        worker.setJobType("Electrician");
        return worker;
    }

    private YgbPerson workerWithArchive()
    {
        YgbPerson worker = worker();
        worker.setPersonName("Zhang San");
        worker.setIdCard("440111199001011234");
        return worker;
    }

    private SysUser user()
    {
        SysUser user = new SysUser();
        user.setUserId(1001L);
        user.setUserName("tester");
        user.setPhonenumber("13800000000");
        return user;
    }

    private WorkerSetting pushSetting()
    {
        WorkerSetting setting = new WorkerSetting();
        setting.setUserId(1001L);
        setting.setPersonId(2001L);
        setting.setNotifyEnabled("1");
        setting.setPushClientId("cid-12345678");
        setting.setNotificationPermission("authorized");
        setting.setPushPlatform("android");
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        return setting;
    }

    private WorkerPushTestRequest pushTestRequest()
    {
        WorkerPushTestRequest request = new WorkerPushTestRequest();
        request.setTitle("Push Test Title");
        request.setContent("Push test content");
        request.setJumpPath("/pages/notice/detail");
        request.setJumpQuery(Map.of("noticeId", 1001));
        request.setActionLabel("Open Detail");
        request.setSourceLabel("Test Source");
        return request;
    }

    private WorkerRealnameSubmitRequest realnameRequest()
    {
        WorkerRealnameSubmitRequest request = new WorkerRealnameSubmitRequest();
        request.setPersonName("Zhang San");
        request.setMobile("13800000000");
        request.setIdCard("440111199001011234");
        request.setIdCardFrontUrl("https://cdn/front.jpg");
        request.setIdCardBackUrl("https://cdn/back.jpg");
        request.setSelfieUrl("https://cdn/selfie.jpg");
        request.setSourceModule("worker-uniapp");
        return request;
    }

    private WorkerPushTestRecord latestRecord()
    {
        WorkerPushTestRecord record = new WorkerPushTestRecord();
        record.setRecordId(1L);
        record.setTraceId("trace-001");
        record.setTestStatus("FAIL");
        record.setStatusMessage("gateway timeout");
        record.setTargetPath("/pages/notice/detail");
        record.setTargetQueryText("{\"noticeId\":1001}");
        record.setActionLabel("Open Detail");
        record.setSourceLabel("Test Source");
        record.setGatewayProvider("getui");
        record.setGatewayUrl("http://push-gateway.local/api/push/send");
        record.setCreateTime(new Date());
        return record;
    }

    private WorkerPushTestRecord successRecord()
    {
        WorkerPushTestRecord record = new WorkerPushTestRecord();
        record.setRecordId(2L);
        record.setTraceId("trace-002");
        record.setTestStatus("SUCCESS");
        record.setStatusMessage("accepted");
        record.setTargetPath("/pages/notice/detail");
        record.setTargetQueryText("{\"noticeId\":1002}");
        record.setActionLabel("Open Detail");
        record.setSourceLabel("Test Source");
        record.setGatewayProvider("getui");
        record.setGatewayUrl("http://push-gateway.local/api/push/send");
        record.setCreateTime(new Date());
        return record;
    }
}
