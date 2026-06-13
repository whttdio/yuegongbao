package com.yuegongbao.ygb.worker.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.WorkerVideoProgress;
import com.yuegongbao.ygb.worker.domain.vo.WorkerActivityHandleRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerContentMapper;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerContentServiceImplTest
{
    @Mock
    private WorkerContentMapper workerContentMapper;

    @Mock
    private YgbPortalContentMapper portalContentMapper;

    @Mock
    private WorkerTrainingService workerTrainingService;

    @Mock
    private WorkerMessageService workerMessageService;

    @InjectMocks
    private WorkerContentServiceImpl service;

    @Test
    void getActivityDetailUsesCmsContentWhenAvailable()
    {
        YgbPortalContent content = portalContent(8001L, "worker_activity", "promotion",
            "Monthly Activity", "Finish training to join", "activity body",
            null, "/activity/monthly-discount.html",
            "{\"activityKey\":\"monthly-discount\",\"ruleList\":[\"rule-1\",\"rule-2\"]}");

        when(portalContentMapper.selectPublishedPortalContentList(eq("ygb"), eq("worker_activity"), any(), any(), eq(1)))
            .thenReturn(List.of(content));
        when(workerContentMapper.countActivityJoin(eq("monthly-discount"), eq(1001L))).thenReturn(1L);

        Map<String, Object> result = service.getActivityDetail(1001L);

        assertEquals("monthly-discount", result.get("activityKey"));
        assertEquals("Monthly Activity", result.get("title"));
        assertEquals("/activity/monthly-discount.html", result.get("externalUrl"));
        assertTrue((Boolean) result.get("joined"));
        @SuppressWarnings("unchecked")
        List<String> ruleList = (List<String>) result.get("ruleList");
        assertEquals(2, ruleList.size());
    }

    @Test
    void getVideoListUsesCmsContentWhenAvailable()
    {
        YgbPortalContent video1 = portalContent(8101L, "worker_video", "video-1", "Video 1",
            "summary 1", "body 1", null, null, "{\"videoKey\":\"video-1\",\"durationSeconds\":96}");
        YgbPortalContent video2 = portalContent(8102L, "worker_video", "video-2", "Video 2",
            "summary 2", "body 2", null, null, "{\"videoKey\":\"video-2\",\"durationSeconds\":118}");

        when(portalContentMapper.selectPublishedPortalContentList(eq("ygb"), eq("worker_video"), any(), any(), eq(10)))
            .thenReturn(List.of(video1, video2));
        when(workerContentMapper.selectVideoProgress(eq("video-1"), eq(1001L)))
            .thenReturn(videoProgress("video-1", 12, 96, false));
        when(workerContentMapper.selectVideoProgress(eq("video-2"), eq(1001L))).thenReturn(null);

        Map<String, Object> result = service.getVideoList(1001L);

        assertEquals(2, result.get("total"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals("video-1", rows.get(0).get("videoKey"));
        assertEquals("01:36", rows.get(0).get("durationText"));
        assertEquals(12, rows.get(0).get("watchedSeconds"));
        assertFalse((Boolean) rows.get(0).get("completed"));
        assertEquals("video-2", rows.get(1).get("videoKey"));
    }

    @Test
    void getVideoDetailUsesCmsContentWhenAvailable()
    {
        YgbPortalContent video = portalContent(8201L, "worker_video", "video-1", "Video 1",
            "summary 1", "body 1", "https://cdn.example.com/poster.png", null,
            "{\"videoKey\":\"video-1\",\"durationSeconds\":96,\"videoUrl\":\"https://cdn.example.com/video.mp4\","
                + "\"keyPoints\":[\"point-1\",\"point-2\"],\"fallbackTips\":[\"tip-1\"],\"sourceText\":\"cms-source\"}");

        when(portalContentMapper.selectPublishedPortalContentList(eq("ygb"), eq("worker_video"), any(), any(), eq(10)))
            .thenReturn(List.of(video));
        when(workerContentMapper.selectVideoProgress(eq("video-1"), eq(1001L)))
            .thenReturn(videoProgress("video-1", 24, 96, false));

        Map<String, Object> result = service.getVideoDetail(1001L, "video-1");

        assertEquals("video-1", result.get("videoKey"));
        assertEquals("https://cdn.example.com/video.mp4", result.get("videoUrl"));
        assertEquals("https://cdn.example.com/poster.png", result.get("posterUrl"));
        assertEquals("cms-source", result.get("sourceText"));
        assertEquals("01:36", result.get("durationText"));
        @SuppressWarnings("unchecked")
        List<String> keyPoints = (List<String>) result.get("keyPoints");
        assertEquals(2, keyPoints.size());
    }

    @Test
    void getAiTrainingDetailFallsBackToMinimalContentWhenCmsMissing()
    {
        when(portalContentMapper.selectPublishedPortalContentList(eq("ygb"), eq("worker_ai_training"), any(), any(), eq(1)))
            .thenReturn(List.of());

        Map<String, Object> result = service.getAiTrainingDetail(worker());

        assertEquals("寮犱笁", result.get("workerName"));
        assertEquals("楂樺浣滀笟", result.get("jobType"));
        @SuppressWarnings("unchecked")
        List<String> questionList = (List<String>) result.get("questionList");
        assertEquals(2, questionList.size());
    }

    @Test
    void getActivityDetailIncludesOrderSnapshotWhenJoined()
    {
        WorkerActivityJoin join = activityJoin();
        when(portalContentMapper.selectPublishedPortalContentList(eq("ygb"), eq("worker_activity"), any(), any(), eq(1)))
            .thenReturn(List.of());
        when(workerContentMapper.countActivityJoin(eq("monthly-discount"), eq(1001L))).thenReturn(1L);
        when(workerContentMapper.selectLatestActivityJoin(eq("monthly-discount"), eq(1001L))).thenReturn(join);

        Map<String, Object> result = service.getActivityDetail(1001L);

        @SuppressWarnings("unchecked")
        Map<String, Object> orderSnapshot = (Map<String, Object>) result.get("orderSnapshot");
        assertEquals("ACT-000321", orderSnapshot.get("orderNo"));
        assertEquals("寰呭紑濂?", orderSnapshot.get("lotteryStatusText"));
        assertEquals("寰呭彂鏀?", orderSnapshot.get("deliveryStatusText"));
    }

    @Test
    void joinActivityReturnsOrderSnapshot()
    {
        when(workerContentMapper.countActivityJoin(eq("monthly-discount"), eq(1001L))).thenReturn(0L);
        when(workerTrainingService.getProgress(any())).thenReturn(completedTrainingProgress());

        Map<String, Object> result = service.joinActivity(worker(), user(), "monthly-discount");

        @SuppressWarnings("unchecked")
        Map<String, Object> orderSnapshot = (Map<String, Object>) result.get("orderSnapshot");
        assertTrue(String.valueOf(orderSnapshot.get("orderNo")).startsWith("ACT-"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> statusTimeline = (List<Map<String, Object>>) orderSnapshot.get("statusTimeline");
        assertEquals(4, statusTimeline.size());
        verify(workerContentMapper).updateActivityJoin(any(WorkerActivityJoin.class));
    }

    @Test
    void joinActivityRequiresTrainingCompletion()
    {
        when(workerTrainingService.getProgress(any())).thenReturn(pendingTrainingProgress());

        assertThrows(ServiceException.class, () -> service.joinActivity(worker(), user(), "monthly-discount"));
    }

    @Test
    void updateActivityHandleUpdatesLotteryAndDeliveryStatus()
    {
        WorkerActivityJoin join = activityJoin();
        WorkerActivityJoin updated = activityJoin();
        updated.setStatus("2");
        updated.setUpdateTime(new Date());
        updated.setRemark("{\"orderNo\":\"ACT-000321\",\"lotteryStatus\":\"WON\",\"lotteryStatusText\":\"宸蹭腑濂?\","
            + "\"deliveryStatus\":\"DELIVERED\",\"deliveryStatusText\":\"宸插彂鏀?\",\"rewardTitle\":\"88鍏冪珛鍑忛噾\","
            + "\"qualifiedTime\":\"2026-06-09 10:00:00\",\"deliveryTime\":\"2026-06-09 12:00:00\","
            + "\"paymentChannel\":\"鏀粯鍒嗚祫鏍?\",\"deliveryText\":\"reward delivered\"}");
        when(workerContentMapper.selectActivityJoinByIdForManage(eq(321L))).thenReturn(join, updated);
        when(workerContentMapper.updateActivityJoin(any(WorkerActivityJoin.class))).thenReturn(1);
        when(workerMessageService.notifyActivityHandleUpdated(any(), any(), eq("adminTester"), eq("reward delivered")))
            .thenReturn(true);

        WorkerActivityHandleRequest request = new WorkerActivityHandleRequest();
        request.setLotteryStatus("WON");
        request.setDeliveryStatus("DELIVERED");
        request.setRewardTitle("88鍏冪珛鍑忛噾");
        request.setStatusMessage("reward delivered");
        request.setDeliveryTimeText("2026-06-09 12:00:00");

        Map<String, Object> result = service.updateActivityHandle(321L, request, "adminTester");

        assertEquals("2", result.get("status"));
        assertEquals("88鍏冪珛鍑忛噾", result.get("rewardTitle"));
        assertEquals("宸插彂鏀?", result.get("deliveryStatusText"));
        assertEquals("宸蹭腑濂?", result.get("lotteryStatusText"));
        assertTrue((Boolean) result.get("messageTriggered"));
        assertTrue((Boolean) result.get("pushTriggered"));
        verify(workerContentMapper).updateActivityJoin(any(WorkerActivityJoin.class));
        verify(workerMessageService).notifyActivityHandleUpdated(any(), any(), eq("adminTester"), eq("reward delivered"));
    }

    @Test
    void updateActivityHandleRejectsInvalidStatusCombination()
    {
        WorkerActivityHandleRequest request = new WorkerActivityHandleRequest();
        request.setLotteryStatus("LOST");
        request.setDeliveryStatus("DELIVERED");

        assertThrows(ServiceException.class, () -> service.updateActivityHandle(321L, request, "adminTester"));
    }

    private YgbPerson worker()
    {
        YgbPerson worker = new YgbPerson();
        worker.setPersonId(2001L);
        worker.setPersonName("寮犱笁");
        worker.setJobType("楂樺浣滀笟");
        return worker;
    }

    private WorkerVideoProgress videoProgress(String videoKey, int watchedSeconds, int totalSeconds, boolean completed)
    {
        WorkerVideoProgress progress = new WorkerVideoProgress();
        progress.setVideoKey(videoKey);
        progress.setWatchedSeconds(watchedSeconds);
        progress.setTotalSeconds(totalSeconds);
        progress.setCompletedFlag(completed ? "1" : "0");
        return progress;
    }

    private WorkerActivityJoin activityJoin()
    {
        WorkerActivityJoin join = new WorkerActivityJoin();
        join.setJoinId(321L);
        join.setActivityKey("monthly-discount");
        join.setUserId(1001L);
        join.setPersonId(2001L);
        join.setPersonName("寮犱笁");
        join.setMobile("13800000000");
        join.setStatus("0");
        join.setCreateTime(new Date());
        join.setRemark("{\"orderNo\":\"ACT-000321\",\"lotteryStatusText\":\"寰呭紑濂?\",\"deliveryStatusText\":\"寰呭彂鏀?\","
            + "\"qualifiedTime\":\"2026-06-09 10:00:00\",\"paymentChannel\":\"鏀粯鍒嗚祫鏍?\"}");
        return join;
    }

    private WorkerTrainingProgress completedTrainingProgress()
    {
        WorkerTrainingProgress progress = new WorkerTrainingProgress();
        progress.setCompleted(10);
        progress.setTotal(10);
        progress.setNeedComplete(false);
        progress.setLockAttendance(false);
        progress.setLockSalary(false);
        return progress;
    }

    private WorkerTrainingProgress pendingTrainingProgress()
    {
        WorkerTrainingProgress progress = new WorkerTrainingProgress();
        progress.setCompleted(3);
        progress.setTotal(10);
        progress.setNeedComplete(true);
        progress.setLockAttendance(true);
        progress.setLockSalary(true);
        return progress;
    }

    private SysUser user()
    {
        SysUser user = new SysUser();
        user.setUserId(1001L);
        user.setUserName("workerTester");
        return user;
    }

    private YgbPortalContent portalContent(Long contentId, String sectionCode, String categoryCode, String title,
        String summary, String content, String coverUrl, String linkUrl, String extraJson)
    {
        YgbPortalContent item = new YgbPortalContent();
        item.setContentId(contentId);
        item.setPortalCode("ygb");
        item.setSectionCode(sectionCode);
        item.setCategoryCode(categoryCode);
        item.setTitle(title);
        item.setSummary(summary);
        item.setContent(content);
        item.setCoverUrl(coverUrl);
        item.setLinkUrl(linkUrl);
        item.setExtraJson(extraJson);
        item.setPublishTime(new Date());
        item.setStatus("0");
        return item;
    }
}
