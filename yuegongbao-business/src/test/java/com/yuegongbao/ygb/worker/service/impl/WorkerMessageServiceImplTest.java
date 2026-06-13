package com.yuegongbao.ygb.worker.service.impl;

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
import com.yuegongbao.system.domain.SysNotice;
import com.yuegongbao.system.service.ISysNoticeReadService;
import com.yuegongbao.system.service.ISysNoticeService;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.domain.vo.YgbUnionSyncResponse;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.integration.UnionAidClient;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import com.yuegongbao.ygb.worker.domain.WorkerNoticeMessage;
import com.yuegongbao.ygb.worker.domain.WorkerSetting;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerComplaintMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerLegalConsultMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerNoticeMessageMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerPushGatewayService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerMessageServiceImplTest
{
    @Mock
    private WorkerComplaintMapper workerComplaintMapper;

    @Mock
    private WorkerLegalConsultMapper workerLegalConsultMapper;

    @Mock
    private WorkerNoticeMessageMapper workerNoticeMessageMapper;

    @Mock
    private WorkerProfileMapper workerProfileMapper;

    @Mock
    private ISysNoticeService sysNoticeService;

    @Mock
    private ISysNoticeReadService sysNoticeReadService;

    @Mock
    private WorkerPushGatewayService workerPushGatewayService;

    @Mock
    private YgbPortalContentMapper portalContentMapper;

    @Mock
    private UnionAidClient unionAidClient;

    @InjectMocks
    private WorkerMessageServiceImpl service;

    @Test
    void createComplaintReturnsPushTriggeredWhenBusinessPushAccepted()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerComplaintCreateRequest request = complaintRequest();
        WorkerSetting setting = pushReadySetting();

        doAnswer(invocation -> {
            WorkerComplaint complaint = invocation.getArgument(0);
            complaint.setComplaintId(3001L);
            complaint.setCreateTime(new Date());
            return 1;
        }).when(workerComplaintMapper).insertWorkerComplaint(any(WorkerComplaint.class));
        when(workerProfileMapper.selectWorkerSetting(eq(user.getUserId()))).thenReturn(setting);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("投诉提交成功"), any(), any(), any()))
            .thenReturn(new LinkedHashMap<>());

        Map<String, Object> result = service.createComplaint(worker, user, request);

        assertEquals(3001L, result.get("complaintId"));
        assertTrue((Boolean) result.get("pushTriggered"));

        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals("complaint", message.getBizType());
        assertEquals("3001", message.getBizId());
        assertEquals("/pages/complaint/detail", message.getJumpPath());
        assertEquals("查看投诉", message.getActionLabel());
        assertEquals("投诉提交成功", message.getSourceLabel());
        assertEquals("0", message.getReadFlag());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("投诉提交成功"),
            eq("您的投诉/举报已提交成功，可在平台持续跟踪处理进度。"), payloadCaptor.capture(), metadataCaptor.capture());

        Map<String, Object> payload = payloadCaptor.getValue();
        assertEquals("/pages/complaint/detail", payload.get("jumpPath"));
        assertInstanceOf(Map.class, payload.get("jumpQuery"));
        @SuppressWarnings("unchecked")
        Map<String, Object> jumpQuery = (Map<String, Object>) payload.get("jumpQuery");
        assertEquals(3001L, jumpQuery.get("complaintId"));
        assertEquals("查看投诉", payload.get("actionLabel"));
        assertEquals("投诉提交成功", payload.get("sourceLabel"));

        Map<String, Object> metadata = metadataCaptor.getValue();
        assertEquals("worker-complaint-created", metadata.get("source"));
        assertEquals(1001L, metadata.get("userId"));
        assertEquals(2001L, metadata.get("personId"));
        assertEquals(3001L, metadata.get("complaintId"));
        assertEquals("欠薪维权", metadata.get("complaintType"));
    }

    @Test
    void createComplaintSkipsBusinessPushWhenNotifyDisabled()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerComplaintCreateRequest request = complaintRequest();
        WorkerSetting setting = pushReadySetting();
        setting.setNotifyEnabled("0");

        doAnswer(invocation -> {
            WorkerComplaint complaint = invocation.getArgument(0);
            complaint.setComplaintId(3002L);
            complaint.setCreateTime(new Date());
            return 1;
        }).when(workerComplaintMapper).insertWorkerComplaint(any(WorkerComplaint.class));
        when(workerProfileMapper.selectWorkerSetting(eq(user.getUserId()))).thenReturn(setting);

        Map<String, Object> result = service.createComplaint(worker, user, request);

        assertEquals(3002L, result.get("complaintId"));
        assertFalse((Boolean) result.get("pushTriggered"));
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(any(WorkerNoticeMessage.class));
        verify(workerPushGatewayService, never()).sendPush(any(), any(), any(), any(), any());
    }

    @Test
    void createLegalConsultReturnsPushTriggeredWhenBusinessPushAccepted()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerLegalConsultCreateRequest request = legalConsultRequest();
        WorkerSetting setting = pushReadySetting();

        doAnswer(invocation -> {
            WorkerLegalConsult consult = invocation.getArgument(0);
            consult.setConsultId(4001L);
            consult.setCreateTime(new Date());
            return 1;
        }).when(workerLegalConsultMapper).insertWorkerLegalConsult(any(WorkerLegalConsult.class));
        when(workerProfileMapper.selectWorkerSetting(eq(user.getUserId()))).thenReturn(setting);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("法律咨询提交成功"), any(), any(), any()))
            .thenReturn(new LinkedHashMap<>());

        Map<String, Object> result = service.createLegalConsult(worker, user, request);

        assertEquals(4001L, result.get("consultId"));
        assertTrue((Boolean) result.get("pushTriggered"));

        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals("legal-consult", message.getBizType());
        assertEquals("4001", message.getBizId());
        assertEquals("/pages/legal/detail", message.getJumpPath());
        assertEquals("查看咨询", message.getActionLabel());
        assertEquals("法律咨询提交成功", message.getSourceLabel());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("法律咨询提交成功"),
            eq("您的法律咨询已提交成功，可在平台查看状态和回复进展。"), payloadCaptor.capture(), any());
        Map<String, Object> payload = payloadCaptor.getValue();
        assertEquals("/pages/legal/detail", payload.get("jumpPath"));
        assertEquals("查看咨询", payload.get("actionLabel"));
        assertEquals("法律咨询提交成功", payload.get("sourceLabel"));
    }

    @Test
    void createLegalConsultStillSucceedsWhenBusinessPushFails()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerLegalConsultCreateRequest request = legalConsultRequest();
        WorkerSetting setting = pushReadySetting();

        doAnswer(invocation -> {
            WorkerLegalConsult consult = invocation.getArgument(0);
            consult.setConsultId(4002L);
            consult.setCreateTime(new Date());
            return 1;
        }).when(workerLegalConsultMapper).insertWorkerLegalConsult(any(WorkerLegalConsult.class));
        when(workerProfileMapper.selectWorkerSetting(eq(user.getUserId()))).thenReturn(setting);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("法律咨询提交成功"), any(), any(), any()))
            .thenThrow(new ServiceException("gateway down"));

        Map<String, Object> result = service.createLegalConsult(worker, user, request);

        assertEquals(4002L, result.get("consultId"));
        assertFalse((Boolean) result.get("pushTriggered"));
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(any(WorkerNoticeMessage.class));
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("法律咨询提交成功"), any(), any(), any());
    }

    @Test
    void getNoticeListMergesWorkerMessageAndSysNotice()
    {
        WorkerNoticeMessage workerMessage = workerNoticeMessage();
        SysNotice sysNotice = sysNotice();
        when(workerNoticeMessageMapper.selectWorkerNoticeMessageList(eq(1001L), eq(10))).thenReturn(List.of(workerMessage));
        when(sysNoticeReadService.selectNoticeListWithReadStatus(eq(1001L), eq(10))).thenReturn(List.of(sysNotice));

        Map<String, Object> result = service.getNoticeList(1001L, 1, 10);

        assertEquals(2, result.get("total"));
        Object rowsValue = result.get("rows");
        assertInstanceOf(List.class, rowsValue);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) rowsValue;
        assertEquals(2, rows.size());
        assertEquals(-5001L, rows.get(0).get("noticeId"));
        assertEquals("投诉提交成功", rows.get(0).get("title"));
        assertEquals("查看投诉", rows.get(0).get("actionLabel"));
        assertEquals("投诉提交成功", rows.get(0).get("sourceLabel"));
        assertEquals(9001L, rows.get(1).get("noticeId"));
        assertEquals("平台公告", rows.get(1).get("title"));
    }

    @Test
    void getNoticeDetailReadsWorkerMessageAndMarksRead()
    {
        WorkerNoticeMessage workerMessage = workerNoticeMessage();
        when(workerNoticeMessageMapper.selectWorkerNoticeMessageById(eq(5001L), eq(1001L))).thenReturn(workerMessage);

        Map<String, Object> result = service.getNoticeDetail(1001L, -5001L);

        assertEquals(-5001L, result.get("noticeId"));
        assertEquals("投诉提交成功", result.get("title"));
        assertEquals("/pages/complaint/detail", result.get("jumpPath"));
        assertEquals("投诉提交成功", result.get("sourceLabel"));
        verify(workerNoticeMessageMapper).markWorkerNoticeMessageRead(eq(5001L), eq(1001L), eq("worker-app"));
    }

    @Test
    void markNoticeReadUsesWorkerMessageMapperForNegativeNoticeId()
    {
        service.markNoticeRead(1001L, -5001L);

        verify(workerNoticeMessageMapper).markWorkerNoticeMessageRead(eq(5001L), eq(1001L), eq("worker-app"));
        verify(sysNoticeReadService, never()).markRead(any(), any());
    }

    @Test
    void getUnreadNoticeCountSumsWorkerMessageAndSysNotice()
    {
        when(workerNoticeMessageMapper.countUnreadWorkerNoticeMessage(eq(1001L))).thenReturn(2);
        when(sysNoticeReadService.selectUnreadCount(eq(1001L))).thenReturn(3);

        int count = service.getUnreadNoticeCount(1001L);

        assertEquals(5, count);
    }

    @Test
    void updateComplaintHandleCreatesNoticeAndPushWhenStatusChanges()
    {
        WorkerComplaint complaint = complaint();
        WorkerSetting setting = pushReadySetting();
        WorkerMessageHandleRequest request = new WorkerMessageHandleRequest();
        request.setStatus("2");
        request.setReplyContent("已核实并完成处理。");
        request.setHandleTimeText("2026-06-09 09:30");

        when(workerComplaintMapper.selectWorkerComplaintByIdForManage(eq(3001L)))
            .thenReturn(complaint)
            .thenReturn(updatedComplaint("2", "已核实并完成处理。", "2026-06-09 09:30"));
        when(workerComplaintMapper.updateWorkerComplaintHandle(any(WorkerComplaint.class))).thenReturn(1);
        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(setting);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("投诉处理结果更新"), eq("已核实并完成处理。"), any(), any()))
            .thenReturn(new LinkedHashMap<>());

        Map<String, Object> result = service.updateComplaintHandle(3001L, request, "admin");

        assertEquals("2", result.get("status"));
        assertEquals("已处理", result.get("statusText"));
        assertTrue((Boolean) result.get("messageTriggered"));
        assertTrue((Boolean) result.get("pushTriggered"));

        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals("complaint", message.getBizType());
        assertEquals("投诉处理结果更新", message.getTitle());
        assertEquals("投诉处理结果", message.getSourceLabel());
    }

    @Test
    void updateLegalConsultHandleCreatesNoticeWithoutPushWhenNoSetting()
    {
        WorkerLegalConsult consult = legalConsult();
        WorkerMessageHandleRequest request = new WorkerMessageHandleRequest();
        request.setStatus("1");
        request.setReplyContent("请补充劳动合同首页与签字页。");
        request.setHandleTimeText("2026-06-09 09:45");

        when(workerLegalConsultMapper.selectWorkerLegalConsultByIdForManage(eq(4001L)))
            .thenReturn(consult)
            .thenReturn(updatedLegalConsult("1", "请补充劳动合同首页与签字页。", "2026-06-09 09:45"));
        when(workerLegalConsultMapper.updateWorkerLegalConsultHandle(any(WorkerLegalConsult.class))).thenReturn(1);
        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(null);

        Map<String, Object> result = service.updateLegalConsultHandle(4001L, request, "admin");

        assertEquals("1", result.get("status"));
        assertEquals("已回复", result.get("statusText"));
        assertTrue((Boolean) result.get("messageTriggered"));
        assertFalse((Boolean) result.get("pushTriggered"));
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(any(WorkerNoticeMessage.class));
        verify(workerPushGatewayService, never()).sendPush(any(), any(), any(), any(), any());
    }

    @Test
    void notifyActivityHandleUpdatedCreatesNoticeAndPush()
    {
        WorkerActivityJoin before = activityJoin();
        WorkerActivityJoin after = activityJoin();
        after.setStatus("2");
        after.setRemark("{\"lotteryStatus\":\"WON\",\"deliveryStatus\":\"DELIVERED\",\"deliveryText\":\"奖励已发放，请查看活动记录。\"}");
        WorkerSetting setting = pushReadySetting();

        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(setting);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("福利活动发放结果更新"),
            eq("奖励已发放，请查看活动记录。"), any(), any())).thenReturn(new LinkedHashMap<>());

        boolean pushed = service.notifyActivityHandleUpdated(before, after, "admin", "奖励已发放，请查看活动记录。");

        assertTrue(pushed);
        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals("activity", message.getBizType());
        assertEquals("321", message.getBizId());
        assertEquals("/pages/activity/join-list", message.getJumpPath());
        assertEquals("查看活动记录", message.getActionLabel());
        assertEquals("福利活动发放结果", message.getSourceLabel());
        assertEquals("福利活动发放结果更新", message.getTitle());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("福利活动发放结果更新"),
            eq("奖励已发放，请查看活动记录。"), payloadCaptor.capture(), metadataCaptor.capture());
        Map<String, Object> payload = payloadCaptor.getValue();
        assertEquals("/pages/activity/join-list", payload.get("jumpPath"));
        assertEquals("查看活动记录", payload.get("actionLabel"));
        assertEquals("福利活动发放结果", payload.get("sourceLabel"));
        @SuppressWarnings("unchecked")
        Map<String, Object> jumpQuery = (Map<String, Object>) payload.get("jumpQuery");
        assertEquals("monthly-discount", jumpQuery.get("activityKey"));
        assertEquals(321L, jumpQuery.get("joinId"));

        Map<String, Object> metadata = metadataCaptor.getValue();
        assertEquals("worker-activity-handle-updated", metadata.get("source"));
        assertEquals(1001L, metadata.get("userId"));
        assertEquals(2001L, metadata.get("personId"));
        assertEquals(321L, metadata.get("joinId"));
        assertEquals("monthly-discount", metadata.get("activityKey"));
        assertEquals("2", metadata.get("status"));
    }

    @Test
    void notifySalaryBatchSubmittedCreatesNoticeAndPushForPendingDetail()
    {
        YgbSalaryBatch batch = new YgbSalaryBatch();
        batch.setBatchId(9001L);
        batch.setBatchNo("BATCH-202606");
        batch.setStatMonth("2026-06");

        YgbSalaryDetail detail = new YgbSalaryDetail();
        detail.setDetailId(8001L);
        detail.setPersonId(2001L);
        detail.setPersonName("寮犱笁");
        detail.setPayStatus("0");
        detail.setNetAmount(new java.math.BigDecimal("1234.56"));

        WorkerSetting setting = pushReadySetting();
        when(workerProfileMapper.selectWorkerSettingByPersonId(eq(2001L))).thenReturn(setting);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("工资发放提醒"), any(), any(), any()))
            .thenReturn(new LinkedHashMap<>());

        int pushed = service.notifySalaryBatchSubmitted(batch, List.of(detail), "tester");

        assertEquals(1, pushed);

        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals(1001L, message.getUserId());
        assertEquals(2001L, message.getPersonId());
        assertEquals("salary", message.getBizType());
        assertEquals("8001", message.getBizId());
        assertEquals("/pages/salary/list", message.getJumpPath());
        assertEquals("查看工资", message.getActionLabel());
        assertEquals("工资发放提醒", message.getSourceLabel());
        assertTrue(message.getContent().contains("2026-06"));
        assertTrue(message.getContent().contains("1234.56"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("工资发放提醒"), any(),
            payloadCaptor.capture(), metadataCaptor.capture());

        Map<String, Object> payload = payloadCaptor.getValue();
        assertEquals("/pages/salary/list", payload.get("jumpPath"));
        assertEquals("查看工资", payload.get("actionLabel"));
        assertEquals("工资发放提醒", payload.get("sourceLabel"));
        @SuppressWarnings("unchecked")
        Map<String, Object> jumpQuery = (Map<String, Object>) payload.get("jumpQuery");
        assertEquals("notice", jumpQuery.get("from"));
        assertEquals("2026-06", jumpQuery.get("month"));

        Map<String, Object> metadata = metadataCaptor.getValue();
        assertEquals("worker-salary-batch-submitted", metadata.get("source"));
        assertEquals(1001L, metadata.get("userId"));
        assertEquals(2001L, metadata.get("personId"));
        assertEquals(9001L, metadata.get("batchId"));
        assertEquals("BATCH-202606", metadata.get("batchNo"));
        assertEquals(8001L, metadata.get("detailId"));
        assertEquals("2026-06", metadata.get("statMonth"));
    }

    @Test
    void notifyTrainingLockReminderCreatesMonthlyDeduplicatedNoticeAndPush()
    {
        WorkerTrainingProgress progress = new WorkerTrainingProgress();
        progress.setCompleted(3);
        progress.setTotal(10);
        progress.setNeedComplete(true);
        progress.setLockAttendance(true);
        progress.setLockSalary(true);

        WorkerSetting setting = pushReadySetting();
        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(setting);
        when(workerNoticeMessageMapper.selectLatestWorkerNoticeMessageByBiz(eq(1001L), eq("training-lock"), any()))
            .thenReturn(null);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("本月培训未完成提醒"), any(), any(), any()))
            .thenReturn(new LinkedHashMap<>());

        boolean pushed = service.notifyTrainingLockReminder(worker(), 1001L, progress, "worker-home");

        assertTrue(pushed);
        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals(1001L, message.getUserId());
        assertEquals(2001L, message.getPersonId());
        assertEquals("training-lock", message.getBizType());
        assertEquals("/pages/training/index", message.getJumpPath());
        assertEquals("去完成培训", message.getActionLabel());
        assertEquals("本月培训未完成提醒", message.getSourceLabel());
        assertTrue(message.getContent().contains("3/10"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("本月培训未完成提醒"), any(),
            payloadCaptor.capture(), any());
        @SuppressWarnings("unchecked")
        Map<String, Object> jumpQuery = (Map<String, Object>) payloadCaptor.getValue().get("jumpQuery");
        assertEquals("notice", jumpQuery.get("from"));
        assertEquals("month-lock", jumpQuery.get("scene"));
    }

    @Test
    void notifyInsuranceArrearsReminderCreatesMonthlyDeduplicatedNoticeAndPush()
    {
        WorkerSetting setting = pushReadySetting();
        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(setting);
        when(workerNoticeMessageMapper.selectLatestWorkerNoticeMessageByBiz(eq(1001L), eq("insurance-arrears"), any()))
            .thenReturn(null);
        when(workerPushGatewayService.sendPush(eq("cid-12345678"), eq("保险欠费提醒"), any(), any(), any()))
            .thenReturn(new LinkedHashMap<>());

        boolean pushed = service.notifyInsuranceArrearsReminder(worker(), 1001L, "2", "2026-06", "worker-home");

        assertTrue(pushed);
        ArgumentCaptor<WorkerNoticeMessage> messageCaptor = ArgumentCaptor.forClass(WorkerNoticeMessage.class);
        verify(workerNoticeMessageMapper).insertWorkerNoticeMessage(messageCaptor.capture());
        WorkerNoticeMessage message = messageCaptor.getValue();
        assertEquals(1001L, message.getUserId());
        assertEquals(2001L, message.getPersonId());
        assertEquals("insurance-arrears", message.getBizType());
        assertEquals("/pages/social/list", message.getJumpPath());
        assertEquals("查看社保", message.getActionLabel());
        assertEquals("保险欠费提醒", message.getSourceLabel());
        assertTrue(message.getContent().contains("2026-06"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workerPushGatewayService).sendPush(eq("cid-12345678"), eq("保险欠费提醒"), any(),
            payloadCaptor.capture(), any());
        @SuppressWarnings("unchecked")
        Map<String, Object> jumpQuery = (Map<String, Object>) payloadCaptor.getValue().get("jumpQuery");
        assertEquals("notice", jumpQuery.get("from"));
        assertEquals("arrears", jumpQuery.get("scene"));
    }

    @Test
    void notifyInsuranceArrearsReminderSkipsWhenAlreadyTriggeredThisMonth()
    {
        WorkerSetting setting = pushReadySetting();
        WorkerNoticeMessage existing = new WorkerNoticeMessage();
        existing.setMessageId(1L);
        when(workerProfileMapper.selectWorkerSetting(eq(1001L))).thenReturn(setting);
        when(workerNoticeMessageMapper.selectLatestWorkerNoticeMessageByBiz(eq(1001L), eq("insurance-arrears"), any()))
            .thenReturn(existing);

        boolean pushed = service.notifyInsuranceArrearsReminder(worker(), 1001L, "2", "2026-06", "worker-home");

        assertFalse(pushed);
        verify(workerNoticeMessageMapper, never()).insertWorkerNoticeMessage(any(WorkerNoticeMessage.class));
        verify(workerPushGatewayService, never()).sendPush(any(), any(), any(), any(), any());
    }

    @Test
    void getLegalArticleListUsesCmsContentWhenAvailable()
    {
        YgbPortalContent intro = portalContent(7001L, "union", "intro", "工会组织架构", "工会服务简介",
            null, "{\"hotline\":\"12351\"}");
        YgbPortalContent guide = portalContent(7002L, "union", "guide", "法律援助申请流程", "劳动者可通过热线申请援助",
            "第一步：提交申请。第二步：等待响应。", "{\"typeLabel\":\"维权指南\"}");

        when(portalContentMapper.selectPublishedPortalContentList(eq("ygb"), eq("union"), any(), any(), any()))
            .thenReturn(List.of(intro, guide));

        Map<String, Object> result = service.getLegalArticleList();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals(1, rows.size());
        assertEquals("cms-7002", rows.get(0).get("articleKey"));
        assertEquals("维权指南", rows.get(0).get("category"));
        assertEquals("法律援助申请流程", rows.get(0).get("title"));
    }

    @Test
    void getUnionNoticeDetailUsesCmsContentWhenAvailable()
    {
        YgbPortalContent guide = portalContent(7003L, "union", "guide", "法律援助申请流程", "劳动者可通过热线申请援助",
            "<p>第一步：提交申请。</p><p>第二步：等待响应。</p>", "{\"typeLabel\":\"维权指南\"}");

        when(portalContentMapper.selectPortalContentById(eq(7003L))).thenReturn(guide);

        Map<String, Object> result = service.getUnionNoticeDetail("cms-7003");

        assertEquals("法律援助申请流程", result.get("title"));
        @SuppressWarnings("unchecked")
        List<String> paragraphs = (List<String>) result.get("paragraphs");
        assertEquals(2, paragraphs.size());
        assertTrue(paragraphs.get(0).contains("第一步"));
        assertTrue(paragraphs.get(1).contains("第二步"));
    }

    @Test
    void createComplaintSyncsUnionWhenRequested()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerComplaintCreateRequest request = complaintRequest();

        doAnswer(invocation -> {
            WorkerComplaint complaint = invocation.getArgument(0);
            complaint.setComplaintId(3010L);
            complaint.setCreateTime(new Date());
            return 1;
        }).when(workerComplaintMapper).insertWorkerComplaint(any(WorkerComplaint.class));
        when(unionAidClient.submitComplaint(any(WorkerComplaint.class))).thenReturn(successUnionResponse("UNION-C-3010"));
        when(workerComplaintMapper.updateWorkerComplaintSyncResult(any(WorkerComplaint.class))).thenReturn(1);

        Map<String, Object> result = service.createComplaint(worker, user, request);

        assertEquals(3010L, result.get("complaintId"));
        assertEquals("SUCCESS", result.get("syncUnionStatus"));
        assertEquals("UNION-C-3010", result.get("syncUnionTicketNo"));
        verify(unionAidClient).submitComplaint(any(WorkerComplaint.class));
        verify(workerComplaintMapper).updateWorkerComplaintSyncResult(any(WorkerComplaint.class));
    }

    @Test
    void createComplaintStillSucceedsWhenUnionSyncFails()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerComplaintCreateRequest request = complaintRequest();

        doAnswer(invocation -> {
            WorkerComplaint complaint = invocation.getArgument(0);
            complaint.setComplaintId(3011L);
            complaint.setCreateTime(new Date());
            return 1;
        }).when(workerComplaintMapper).insertWorkerComplaint(any(WorkerComplaint.class));
        when(unionAidClient.submitComplaint(any(WorkerComplaint.class))).thenThrow(new ServiceException("union gateway down"));
        when(workerComplaintMapper.updateWorkerComplaintSyncResult(any(WorkerComplaint.class))).thenReturn(1);

        Map<String, Object> result = service.createComplaint(worker, user, request);

        assertEquals(3011L, result.get("complaintId"));
        assertEquals("FAIL", result.get("syncUnionStatus"));
        verify(unionAidClient).submitComplaint(any(WorkerComplaint.class));
        verify(workerComplaintMapper).updateWorkerComplaintSyncResult(any(WorkerComplaint.class));
    }

    @Test
    void createLegalConsultAlwaysSyncsUnion()
    {
        YgbPerson worker = worker();
        SysUser user = user();
        WorkerLegalConsultCreateRequest request = legalConsultRequest();

        doAnswer(invocation -> {
            WorkerLegalConsult consult = invocation.getArgument(0);
            consult.setConsultId(4010L);
            consult.setCreateTime(new Date());
            return 1;
        }).when(workerLegalConsultMapper).insertWorkerLegalConsult(any(WorkerLegalConsult.class));
        when(unionAidClient.submitLegalConsult(any(WorkerLegalConsult.class))).thenReturn(successUnionResponse("UNION-L-4010"));
        when(workerLegalConsultMapper.updateWorkerLegalConsultSyncResult(any(WorkerLegalConsult.class))).thenReturn(1);

        Map<String, Object> result = service.createLegalConsult(worker, user, request);

        assertEquals(4010L, result.get("consultId"));
        assertEquals("SUCCESS", result.get("syncUnionStatus"));
        assertEquals("UNION-L-4010", result.get("syncUnionTicketNo"));
        verify(unionAidClient).submitLegalConsult(any(WorkerLegalConsult.class));
        verify(workerLegalConsultMapper).updateWorkerLegalConsultSyncResult(any(WorkerLegalConsult.class));
    }

    private YgbPerson worker()
    {
        YgbPerson worker = new YgbPerson();
        worker.setPersonId(2001L);
        worker.setPersonName("张三");
        worker.setEnterpriseId(9001L);
        worker.setEnterpriseName("广东测试劳务有限公司");
        worker.setMobile("13800000000");
        return worker;
    }

    private SysUser user()
    {
        SysUser user = new SysUser();
        user.setUserId(1001L);
        user.setUserName("workerTester");
        user.setPhonenumber("13800000000");
        return user;
    }

    private WorkerSetting pushReadySetting()
    {
        WorkerSetting setting = new WorkerSetting();
        setting.setSettingId(1L);
        setting.setUserId(1001L);
        setting.setPersonId(2001L);
        setting.setNotifyEnabled("1");
        setting.setPushClientId("cid-12345678");
        setting.setPushPlatform("app-plus");
        setting.setNotificationPermission("authorized");
        return setting;
    }

    private YgbUnionSyncResponse successUnionResponse(String ticketNo)
    {
        YgbUnionSyncResponse response = new YgbUnionSyncResponse();
        response.setSuccess(true);
        response.setSyncStatus("SUCCESS");
        response.setSyncMessage("工会法律援助系统已受理。");
        response.setTicketNo(ticketNo);
        response.setExternalSerialNo("trace-" + ticketNo);
        response.setCallbackTime(new Date());
        response.setRawPayload("{\"ticketNo\":\"" + ticketNo + "\"}");
        return response;
    }

    private WorkerComplaintCreateRequest complaintRequest()
    {
        WorkerComplaintCreateRequest request = new WorkerComplaintCreateRequest();
        request.setComplaintType("欠薪维权");
        request.setTitle("投诉标题");
        request.setContent("投诉内容");
        request.setContactMobile("13800000000");
        request.setAnonymous(Boolean.FALSE);
        request.setSyncUnion(Boolean.TRUE);
        request.setAttachments("[\"/upload/complaint-1.png\"]");
        return request;
    }

    private WorkerLegalConsultCreateRequest legalConsultRequest()
    {
        WorkerLegalConsultCreateRequest request = new WorkerLegalConsultCreateRequest();
        request.setConsultType("劳动合同");
        request.setTitle("咨询标题");
        request.setContent("咨询内容");
        request.setContactMobile("13800000000");
        request.setAttachments("[\"/upload/legal-1.png\"]");
        return request;
    }

    private WorkerNoticeMessage workerNoticeMessage()
    {
        WorkerNoticeMessage message = new WorkerNoticeMessage();
        message.setMessageId(5001L);
        message.setUserId(1001L);
        message.setPersonId(2001L);
        message.setPersonName("张三");
        message.setMessageType("BUSINESS");
        message.setTitle("投诉提交成功");
        message.setSummary("您的投诉/举报已提交成功，可在平台持续跟踪处理进度。");
        message.setContent("您的投诉/举报已提交成功，可在平台持续跟踪处理进度。");
        message.setBizType("complaint");
        message.setBizId("3001");
        message.setJumpPath("/pages/complaint/detail");
        message.setJumpQueryText("{\"complaintId\":3001}");
        message.setActionLabel("查看投诉");
        message.setSourceLabel("投诉提交成功");
        message.setReadFlag("0");
        message.setCreateTime(new Date(1_700_000_000_000L));
        return message;
    }

    private WorkerComplaint complaint()
    {
        WorkerComplaint complaint = new WorkerComplaint();
        complaint.setComplaintId(3001L);
        complaint.setUserId(1001L);
        complaint.setPersonId(2001L);
        complaint.setPersonName("寮犱笁");
        complaint.setComplaintType("娆犺柂缁存潈");
        complaint.setTitle("鎶曡瘔鏍囬");
        complaint.setStatus("0");
        return complaint;
    }

    private WorkerComplaint updatedComplaint(String status, String replyContent, String handleTimeText)
    {
        WorkerComplaint complaint = complaint();
        complaint.setStatus(status);
        complaint.setReplyContent(replyContent);
        complaint.setHandleTimeText(handleTimeText);
        return complaint;
    }

    private WorkerLegalConsult legalConsult()
    {
        WorkerLegalConsult consult = new WorkerLegalConsult();
        consult.setConsultId(4001L);
        consult.setUserId(1001L);
        consult.setPersonId(2001L);
        consult.setPersonName("寮犱笁");
        consult.setConsultType("鍔冲姩鍚堝悓");
        consult.setTitle("鍜ㄨ鏍囬");
        consult.setStatus("0");
        return consult;
    }

    private WorkerLegalConsult updatedLegalConsult(String status, String replyContent, String replyTimeText)
    {
        WorkerLegalConsult consult = legalConsult();
        consult.setStatus(status);
        consult.setReplyContent(replyContent);
        consult.setReplyTimeText(replyTimeText);
        return consult;
    }

    private WorkerActivityJoin activityJoin()
    {
        WorkerActivityJoin join = new WorkerActivityJoin();
        join.setJoinId(321L);
        join.setActivityKey("monthly-discount");
        join.setUserId(1001L);
        join.setPersonId(2001L);
        join.setPersonName("张三");
        join.setMobile("13800000000");
        join.setStatus("0");
        join.setRemark("{\"lotteryStatus\":\"PENDING\",\"deliveryStatus\":\"PENDING\"}");
        return join;
    }

    private YgbPortalContent portalContent(Long contentId, String sectionCode, String categoryCode, String title,
        String summary, String content, String extraJson)
    {
        YgbPortalContent item = new YgbPortalContent();
        item.setContentId(contentId);
        item.setPortalCode("ygb");
        item.setSectionCode(sectionCode);
        item.setCategoryCode(categoryCode);
        item.setTitle(title);
        item.setSummary(summary);
        item.setContent(content);
        item.setExtraJson(extraJson);
        item.setStatus("0");
        return item;
    }

    private SysNotice sysNotice()
    {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(9001L);
        notice.setNoticeTitle("平台公告");
        notice.setNoticeType("1");
        notice.setNoticeContent("这是一条平台公告。");
        notice.setStatus("0");
        notice.setIsRead(false);
        notice.setCreateTime(new Date(1_600_000_000_000L));
        return notice;
    }
}
