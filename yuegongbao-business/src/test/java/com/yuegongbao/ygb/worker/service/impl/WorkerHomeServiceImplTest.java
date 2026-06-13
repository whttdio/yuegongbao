package com.yuegongbao.ygb.worker.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.service.WorkerContentService;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerProfileService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerHomeServiceImplTest
{
    @Mock
    private WorkerTrainingService workerTrainingService;

    @Mock
    private WorkerContentService workerContentService;

    @Mock
    private WorkerMessageService workerMessageService;

    @Mock
    private WorkerProfileService workerProfileService;

    @Mock
    private YgbContractMapper contractMapper;

    @Mock
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Mock
    private WorkerProfileMapper workerProfileMapper;

    @InjectMocks
    private WorkerHomeServiceImpl service;

    @Test
    void getHomeViewTriggersTrainingReminderWhenTrainingLocked()
    {
        WorkerTrainingProgress progress = new WorkerTrainingProgress();
        progress.setCompleted(3);
        progress.setTotal(10);
        progress.setNeedComplete(true);
        progress.setLockAttendance(true);
        progress.setLockSalary(true);

        when(workerTrainingService.getProgress(any())).thenReturn(progress);
        when(workerProfileMapper.selectWorkerResume(eq(1001L))).thenReturn(new WorkerResume());
        when(workerMessageService.notifyTrainingLockReminder(any(), eq(1001L), any(), eq("worker-home")))
            .thenReturn(true);
        when(workerMessageService.notifyInsuranceArrearsReminder(any(), eq(1001L), eq("1"), eq(null), eq("worker-home")))
            .thenReturn(false);
        when(workerMessageService.notifyCertificateReminder(any(), eq(1001L), any(), eq("worker-home")))
            .thenReturn(false);
        when(socialPaymentMapper.selectWorkerSocialPaymentList(eq(2001L), any())).thenReturn(List.of());
        when(workerContentService.getActivityDetail(eq(1001L))).thenReturn(activityDetail());
        when(workerContentService.getVideoList(eq(1001L))).thenReturn(Map.of("rows", List.of()));
        when(workerMessageService.getLegalArticleList()).thenReturn(Map.of("rows", List.of()));
        when(workerMessageService.getNoticeList(eq(1001L), eq(1), eq(3))).thenReturn(Map.of("rows", List.of()));
        when(workerMessageService.getUnreadNoticeCount(eq(1001L))).thenReturn(1);

        Map<String, Object> result = service.getHomeView(worker(), 1001L);

        assertTrue((Boolean) result.get("trainingReminderTriggered"));
        assertEquals(false, result.get("canCheckIn"));
        assertEquals(false, result.get("canViewSalary"));
        verify(workerMessageService).notifyTrainingLockReminder(any(), eq(1001L), any(), eq("worker-home"));
    }

    @Test
    void getHomeViewTriggersInsuranceArrearsReminderWhenArrearsExists()
    {
        WorkerTrainingProgress progress = new WorkerTrainingProgress();
        progress.setCompleted(10);
        progress.setTotal(10);
        progress.setNeedComplete(false);
        progress.setLockAttendance(false);
        progress.setLockSalary(false);

        YgbSocialPayment arrears = new YgbSocialPayment();
        arrears.setStatMonth("2026-06");
        arrears.setPaymentStatus("2");

        when(workerTrainingService.getProgress(any())).thenReturn(progress);
        when(workerProfileMapper.selectWorkerResume(eq(1001L))).thenReturn(new WorkerResume());
        when(workerMessageService.notifyTrainingLockReminder(any(), eq(1001L), any(), eq("worker-home")))
            .thenReturn(false);
        when(socialPaymentMapper.selectWorkerSocialPaymentList(eq(2001L), any())).thenReturn(List.of(arrears));
        when(workerMessageService.notifyInsuranceArrearsReminder(any(), eq(1001L), eq("1"), eq("2026-06"),
            eq("worker-home"))).thenReturn(true);
        when(workerMessageService.notifyCertificateReminder(any(), eq(1001L), any(), eq("worker-home")))
            .thenReturn(false);
        when(workerContentService.getActivityDetail(eq(1001L))).thenReturn(activityDetail());
        when(workerContentService.getVideoList(eq(1001L))).thenReturn(Map.of("rows", List.of()));
        when(workerMessageService.getLegalArticleList()).thenReturn(Map.of("rows", List.of()));
        when(workerMessageService.getNoticeList(eq(1001L), eq(1), eq(3))).thenReturn(Map.of("rows", List.of()));
        when(workerMessageService.getUnreadNoticeCount(eq(1001L))).thenReturn(0);

        Map<String, Object> result = service.getHomeView(worker(), 1001L);

        assertTrue((Boolean) result.get("insuranceReminderTriggered"));
        assertEquals("2026-06", result.get("latestArrearsMonth"));
        verify(workerMessageService).notifyInsuranceArrearsReminder(any(), eq(1001L), eq("1"), eq("2026-06"),
            eq("worker-home"));
    }

    private YgbPerson worker()
    {
        YgbPerson worker = new YgbPerson();
        worker.setPersonId(2001L);
        worker.setPersonName("张三");
        worker.setEnterpriseName("示例企业");
        worker.setJobType("电工");
        worker.setWorkerType("1");
        worker.setInsuranceStatus("1");
        worker.setEmploymentStatus("0");
        worker.setMobile("13800000000");
        worker.setIdCard("440111199001011234");
        return worker;
    }

    private Map<String, Object> activityDetail()
    {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("activityKey", "monthly-discount");
        detail.put("title", "月月抽");
        detail.put("subtitle", "完成培训后可参与");
        detail.put("joined", false);
        detail.put("externalUrl", "");
        return detail;
    }
}
