package com.yuegongbao.ygb.worker.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.common.utils.DateUtils;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.WorkerVideoProgress;
import com.yuegongbao.ygb.worker.domain.vo.WorkerActivityHandleRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerContentMapper;
import com.yuegongbao.ygb.worker.service.WorkerContentService;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@Service
public class WorkerContentServiceImpl implements WorkerContentService
{
    private static final String PORTAL_CODE = "ygb";

    private static final String SECTION_ACTIVITY = "worker_activity";

    private static final String SECTION_VIDEO = "worker_video";

    private static final String SECTION_AI_TRAINING = "worker_ai_training";

    private static final String DEFAULT_ACTIVITY_KEY = "monthly-discount";

    private static final String DEFAULT_ACTIVITY_TITLE = "月月抽最高 188 元立减金";

    private static final String DEFAULT_ACTIVITY_SUBTITLE = "完成培训、保持良好用工记录即可参与";

    private static final String DEFAULT_ACTIVITY_EXTERNAL_URL = "/activity/monthly-discount.html";

    private static final int REMARK_MAX_LENGTH = 255;

    private static final String DEFAULT_AI_TRAINING_TITLE = "工伤 AI 培训";

    private static final String DEFAULT_AI_TRAINING_SUBTITLE = "结合个人工种和常见风险，给出训练建议。";

    @Autowired
    private WorkerContentMapper workerContentMapper;

    @Autowired
    private YgbPortalContentMapper portalContentMapper;

    @Autowired
    private WorkerTrainingService workerTrainingService;

    @Autowired
    private WorkerMessageService workerMessageService;

    @Override
    public Map<String, Object> getActivityDetail(Long userId)
    {
        Map<String, Object> cmsDetail = buildCmsActivityDetail(userId);
        if (cmsDetail != null)
        {
            appendActivityOrderSnapshot(cmsDetail, userId, String.valueOf(cmsDetail.get("activityKey")));
            return cmsDetail;
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("activityKey", DEFAULT_ACTIVITY_KEY);
        detail.put("title", DEFAULT_ACTIVITY_TITLE);
        detail.put("subtitle", DEFAULT_ACTIVITY_SUBTITLE);
        detail.put("ruleList", defaultActivityRules());
        detail.put("joined", joinedActivity(DEFAULT_ACTIVITY_KEY, userId));
        detail.put("externalUrl", DEFAULT_ACTIVITY_EXTERNAL_URL);
        appendActivityOrderSnapshot(detail, userId, DEFAULT_ACTIVITY_KEY);
        return detail;
    }

    @Override
    public Map<String, Object> joinActivity(YgbPerson worker, SysUser user, String activityKey)
    {
        if (StringUtils.isEmpty(activityKey))
        {
            throw new ServiceException("活动标识不能为空。");
        }
        WorkerTrainingProgress trainingProgress = workerTrainingService.getProgress(worker);
        if (trainingProgress != null && trainingProgress.isNeedComplete())
        {
            throw new ServiceException("当前活动需先完成本月培训任务后才能报名。");
        }
        if (joinedActivity(activityKey, user == null ? null : user.getUserId()))
        {
            throw new ServiceException("您已报名过该活动。");
        }
        WorkerActivityJoin join = new WorkerActivityJoin();
        join.setActivityKey(activityKey);
        join.setUserId(user.getUserId());
        join.setPersonId(worker.getPersonId());
        join.setPersonName(worker.getPersonName());
        join.setMobile(worker.getMobile());
        join.setStatus("0");
        join.setRemark(buildActivityOrderRemark(activityKey, null));
        join.setCreateBy(user.getUserName());
        workerContentMapper.insertActivityJoin(join);
        join.setRemark(buildActivityOrderRemark(activityKey, join));
        join.setUpdateBy(user.getUserName());
        workerContentMapper.updateActivityJoin(join);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("joinId", join.getJoinId());
        result.put("joined", true);
        result.put("orderSnapshot", buildActivityOrderSnapshot(join));
        return result;
    }

    @Override
    public Map<String, Object> getActivityJoinList(Long userId)
    {
        List<WorkerActivityJoin> joins = workerContentMapper.selectActivityJoinList(userId);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (WorkerActivityJoin item : joins)
        {
            rows.add(activityJoinRow(item));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> listActivityManageRecords(WorkerActivityJoin query)
    {
        List<WorkerActivityJoin> joins = workerContentMapper.selectActivityJoinManageList(query == null ? new WorkerActivityJoin() : query);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (WorkerActivityJoin item : joins)
        {
            rows.add(activityJoinRow(item));
        }
        return rows;
    }

    @Override
    public Map<String, Object> getActivityManageDetail(Long joinId)
    {
        WorkerActivityJoin join = workerContentMapper.selectActivityJoinByIdForManage(joinId);
        if (join == null)
        {
            throw new ServiceException("未找到活动参与记录。");
        }
        return activityJoinRow(join);
    }

    @Override
    public Map<String, Object> updateActivityHandle(Long joinId, WorkerActivityHandleRequest request, String operator)
    {
        validateActivityHandleRequest(request);
        WorkerActivityJoin before = workerContentMapper.selectActivityJoinByIdForManage(joinId);
        if (before == null)
        {
            throw new ServiceException("未找到活动参与记录。");
        }
        JSONObject extra = parseExtraJson(before.getRemark());
        String lotteryStatus = request.getLotteryStatus().trim();
        String deliveryStatus = request.getDeliveryStatus().trim();
        String rewardTitle = firstNonBlank(request.getRewardTitle(), jsonString(extra, "rewardTitle"), "最高 188 元立减金");
        String statusMessage = firstNonBlank(request.getStatusMessage(), defaultActivityStatusMessage(lotteryStatus, deliveryStatus));
        String deliveryTimeText = firstNonBlank(request.getDeliveryTimeText(), "DELIVERED".equals(deliveryStatus) ? formatDateTime(new Date()) : "");

        extra.put("lotteryStatus", lotteryStatus);
        extra.put("lotteryStatusText", activityLotteryStatusText(lotteryStatus));
        extra.put("deliveryStatus", deliveryStatus);
        extra.put("deliveryStatusText", activityDeliveryStatusText(deliveryStatus));
        extra.put("rewardTitle", rewardTitle);
        extra.put("deliveryText", statusMessage);
        if ("WON".equals(lotteryStatus) || "LOST".equals(lotteryStatus))
        {
            extra.put("lotteryTime", formatDateTime(new Date()));
        }
        if (StringUtils.isNotEmpty(deliveryTimeText))
        {
            extra.put("deliveryTime", deliveryTimeText);
        }

        WorkerActivityJoin target = new WorkerActivityJoin();
        target.setJoinId(before.getJoinId());
        target.setStatus(resolveActivityManageStatus(lotteryStatus, deliveryStatus));
        target.setRemark(JSON.toJSONString(extra));
        target.setUpdateBy(operator);
        int rows = workerContentMapper.updateActivityJoin(target);

        WorkerActivityJoin latest = workerContentMapper.selectActivityJoinByIdForManage(joinId);
        Map<String, Object> result = activityJoinRow(latest == null ? before : latest);
        result.put("statusMessage", statusMessage);
        boolean messageTriggered = false;
        boolean pushTriggered = false;
        if (rows > 0 && activityHandleChanged(before, target))
        {
            messageTriggered = true;
            pushTriggered = workerMessageService.notifyActivityHandleUpdated(before, latest == null ? target : latest,
                operator, statusMessage);
        }
        result.put("messageTriggered", messageTriggered);
        result.put("pushTriggered", pushTriggered);
        return result;
    }

    @Override
    public Map<String, Object> getVideoList(Long userId)
    {
        List<Map<String, Object>> cmsRows = buildCmsVideoRows(userId);
        if (!cmsRows.isEmpty())
        {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("rows", cmsRows);
            result.put("total", cmsRows.size());
            return result;
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(videoSummary(userId, "video-1", "工伤预防基础知识", 36));
        rows.add(videoSummary(userId, "video-2", "高处作业安全提示", 58));
        rows.add(videoSummary(userId, "video-3", "设备作业前自检", 42));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getVideoDetail(Long userId, String videoKey)
    {
        Map<String, Object> cmsDetail = buildCmsVideoDetail(userId, videoKey);
        if (cmsDetail != null)
        {
            return cmsDetail;
        }

        Map<String, Object> detail = switch (videoKey)
        {
            case "video-1" -> videoDetail(userId, "video-1", "工伤预防基础知识", 36,
                "围绕入场、作业、防护和报案流程的基础培训。",
                Arrays.asList("入场前先核对培训、考勤和劳动合同状态。", "作业中佩戴好基础防护用品，发现异常先停工。",
                    "发生工伤后第一时间留痕并补齐病历、照片和考勤。"));
            case "video-2" -> videoDetail(userId, "video-2", "高处作业安全提示", 58,
                "重点提示安全带、脚手架和现场监护要求。",
                Arrays.asList("高处作业前检查安全带、安全绳和脚手架状态。", "现场必须有监护措施，严禁单人冒险作业。",
                    "发现围栏、支撑或坠落防护异常时立即停工上报。"));
            case "video-3" -> videoDetail(userId, "video-3", "设备作业前自检", 42,
                "开工前先检查电源、围栏、设备外观和作业记录。",
                Arrays.asList("开工前先看设备电源、围栏和外观是否异常。", "核对点检记录和当日作业授权，再进行开机。",
                    "发现设备异响、报警或防护缺失时不得强行作业。"));
            default -> null;
        };
        if (detail == null)
        {
            throw new ServiceException("未找到视频内容。");
        }
        return detail;
    }

    @Override
    public Map<String, Object> saveVideoProgress(YgbPerson worker, Long userId, String videoKey, Integer watchedSeconds,
        Integer totalSeconds)
    {
        if (StringUtils.isEmpty(videoKey))
        {
            throw new ServiceException("视频标识不能为空。");
        }
        WorkerVideoProgress progress = workerContentMapper.selectVideoProgress(videoKey, userId);
        int resolvedTotalSeconds = totalSeconds == null || totalSeconds <= 0 ? videoTotalSeconds(videoKey) : totalSeconds;
        int seconds = watchedSeconds == null ? 0 : Math.max(0, watchedSeconds);
        if (progress == null)
        {
            progress = new WorkerVideoProgress();
            progress.setVideoKey(videoKey);
            progress.setUserId(userId);
            progress.setPersonId(worker.getPersonId());
            progress.setWatchedSeconds(seconds);
            progress.setTotalSeconds(resolvedTotalSeconds);
            progress.setCompletedFlag(seconds >= resolvedTotalSeconds ? "1" : "0");
            workerContentMapper.insertVideoProgress(progress);
        }
        else
        {
            progress.setWatchedSeconds(seconds);
            progress.setTotalSeconds(resolvedTotalSeconds);
            progress.setCompletedFlag(seconds >= resolvedTotalSeconds ? "1" : "0");
            workerContentMapper.updateVideoProgress(progress);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("videoKey", videoKey);
        result.put("watchedSeconds", progress.getWatchedSeconds());
        result.put("totalSeconds", progress.getTotalSeconds());
        result.put("completed", "1".equals(progress.getCompletedFlag()));
        return result;
    }

    @Override
    public Map<String, Object> getAiTrainingDetail(YgbPerson worker)
    {
        Map<String, Object> cmsDetail = buildCmsAiTrainingDetail(worker);
        if (cmsDetail != null)
        {
            return cmsDetail;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title", DEFAULT_AI_TRAINING_TITLE);
        result.put("subtitle", DEFAULT_AI_TRAINING_SUBTITLE);
        result.put("workerName", worker == null ? "" : worker.getPersonName());
        result.put("jobType", worker == null ? "" : worker.getJobType());
        result.put("questionList", defaultAiQuestionList());
        result.put("caseList", defaultAiCaseList());
        result.put("actionTips", defaultAiActionTips());
        return result;
    }

    private Map<String, Object> buildCmsActivityDetail(Long userId)
    {
        List<YgbPortalContent> list = selectPublishedContent(SECTION_ACTIVITY, 1);
        if (list.isEmpty())
        {
            return null;
        }
        YgbPortalContent item = list.get(0);
        JSONObject extra = parseExtraJson(item.getExtraJson());
        String activityKey = firstNonBlank(jsonString(extra, "activityKey"), item.getCategoryCode(), DEFAULT_ACTIVITY_KEY);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("activityKey", activityKey);
        detail.put("title", firstNonBlank(item.getTitle(), DEFAULT_ACTIVITY_TITLE));
        detail.put("subtitle", firstNonBlank(item.getSummary(), item.getContent(), DEFAULT_ACTIVITY_SUBTITLE));
        detail.put("ruleList", stringList(extra, "ruleList", defaultActivityRules()));
        detail.put("joined", joinedActivity(activityKey, userId));
        detail.put("externalUrl", firstNonBlank(item.getLinkUrl(), jsonString(extra, "externalUrl"),
            DEFAULT_ACTIVITY_EXTERNAL_URL));
        detail.put("rewardTitle", firstNonBlank(jsonString(extra, "rewardTitle"), "最高 188 元立减金"));
        detail.put("eligibilityText", firstNonBlank(jsonString(extra, "eligibilityText"), "支付分达标后进入开奖队列"));
        detail.put("deliveryText", firstNonBlank(jsonString(extra, "deliveryText"), "活动结果以平台通知和发放记录为准"));
        return detail;
    }

    private List<Map<String, Object>> buildCmsVideoRows(Long userId)
    {
        List<YgbPortalContent> list = selectPublishedContent(SECTION_VIDEO, 10);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbPortalContent item : list)
        {
            Map<String, Object> row = buildCmsVideoSummary(userId, item);
            if (row != null)
            {
                rows.add(row);
            }
        }
        return rows;
    }

    private Map<String, Object> buildCmsVideoSummary(Long userId, YgbPortalContent item)
    {
        JSONObject extra = parseExtraJson(item == null ? null : item.getExtraJson());
        String videoKey = resolveVideoKey(item, extra);
        if (StringUtils.isEmpty(videoKey))
        {
            return null;
        }
        int totalSeconds = resolveVideoTotalSeconds(videoKey, extra);
        WorkerVideoProgress progress = workerContentMapper.selectVideoProgress(videoKey, userId);

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("videoKey", videoKey);
        row.put("title", firstNonBlank(item.getTitle(), fallbackVideoTitle(videoKey)));
        row.put("totalSeconds", totalSeconds);
        row.put("durationText", formatDuration(totalSeconds));
        row.put("watchedSeconds", progress == null ? 0 : progress.getWatchedSeconds());
        row.put("completed", progress != null && "1".equals(progress.getCompletedFlag()));
        return row;
    }

    private Map<String, Object> buildCmsVideoDetail(Long userId, String videoKey)
    {
        if (StringUtils.isEmpty(videoKey))
        {
            return null;
        }
        for (YgbPortalContent item : selectPublishedContent(SECTION_VIDEO, 10))
        {
            JSONObject extra = parseExtraJson(item.getExtraJson());
            String resolvedKey = resolveVideoKey(item, extra);
            if (!videoKey.equals(resolvedKey))
            {
                continue;
            }
            int totalSeconds = resolveVideoTotalSeconds(videoKey, extra);
            WorkerVideoProgress progress = workerContentMapper.selectVideoProgress(videoKey, userId);
            String videoUrl = firstNonBlank(jsonString(extra, "videoUrl"), item.getLinkUrl());
            String posterUrl = firstNonBlank(jsonString(extra, "posterUrl"), item.getCoverUrl(), "");

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("videoKey", videoKey);
            row.put("title", firstNonBlank(item.getTitle(), fallbackVideoTitle(videoKey)));
            row.put("desc", firstNonBlank(item.getSummary(), item.getContent(), fallbackVideoDesc(videoKey)));
            row.put("videoUrl", videoUrl);
            row.put("posterUrl", posterUrl);
            row.put("sourceText", firstNonBlank(jsonString(extra, "sourceText"),
                StringUtils.isNotEmpty(videoUrl) ? "当前视频资源已接入在线播放源。"
                    : "当前视频资源暂未同步在线播放地址，已展示培训摘要供劳动者先学习关键要点。"));
            row.put("fallbackTips", stringList(extra, "fallbackTips", defaultVideoFallbackTips(videoUrl)));
            row.put("keyPoints", stringList(extra, "keyPoints", fallbackVideoKeyPoints(videoKey)));
            row.put("totalSeconds", totalSeconds);
            row.put("durationText", formatDuration(totalSeconds));
            row.put("watchedSeconds", progress == null ? 0 : progress.getWatchedSeconds());
            row.put("completed", progress != null && "1".equals(progress.getCompletedFlag()));
            return row;
        }
        return null;
    }

    private Map<String, Object> buildCmsAiTrainingDetail(YgbPerson worker)
    {
        List<YgbPortalContent> list = selectPublishedContent(SECTION_AI_TRAINING, 1);
        if (list.isEmpty())
        {
            return null;
        }
        YgbPortalContent item = list.get(0);
        JSONObject extra = parseExtraJson(item.getExtraJson());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title", firstNonBlank(item.getTitle(), DEFAULT_AI_TRAINING_TITLE));
        result.put("subtitle", firstNonBlank(item.getSummary(), item.getContent(), DEFAULT_AI_TRAINING_SUBTITLE));
        result.put("workerName", worker == null ? "" : worker.getPersonName());
        result.put("jobType", worker == null ? "" : worker.getJobType());
        result.put("questionList", stringList(extra, "questionList", defaultAiQuestionList()));
        result.put("caseList", stringList(extra, "caseList", defaultAiCaseList()));
        result.put("actionTips", stringList(extra, "actionTips", defaultAiActionTips()));
        return result;
    }

    private List<YgbPortalContent> selectPublishedContent(String sectionCode, Integer limit)
    {
        List<YgbPortalContent> list = portalContentMapper.selectPublishedPortalContentList(
            PORTAL_CODE, sectionCode, null, null, limit);
        return list == null ? Collections.emptyList() : list;
    }

    private Map<String, Object> videoSummary(Long userId, String videoKey, String title, int totalSeconds)
    {
        WorkerVideoProgress progress = workerContentMapper.selectVideoProgress(videoKey, userId);
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("videoKey", videoKey);
        row.put("title", title);
        row.put("totalSeconds", totalSeconds);
        row.put("durationText", formatDuration(totalSeconds));
        row.put("watchedSeconds", progress == null ? 0 : progress.getWatchedSeconds());
        row.put("completed", progress != null && "1".equals(progress.getCompletedFlag()));
        return row;
    }

    private Map<String, Object> videoDetail(Long userId, String videoKey, String title, int totalSeconds, String desc,
        List<String> keyPoints)
    {
        WorkerVideoProgress progress = workerContentMapper.selectVideoProgress(videoKey, userId);
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("videoKey", videoKey);
        row.put("title", title);
        row.put("desc", desc);
        row.put("videoUrl", "");
        row.put("sourceText", "当前在线视频资源暂未同步，已切换为文字摘要模式，劳动者可先学习关键要点。");
        row.put("fallbackTips", defaultVideoFallbackTips(""));
        row.put("keyPoints", keyPoints);
        row.put("totalSeconds", totalSeconds);
        row.put("durationText", formatDuration(totalSeconds));
        row.put("watchedSeconds", progress == null ? 0 : progress.getWatchedSeconds());
        row.put("completed", progress != null && "1".equals(progress.getCompletedFlag()));
        return row;
    }

    private int videoTotalSeconds(String videoKey)
    {
        Integer cmsTotalSeconds = resolveCmsVideoTotalSeconds(videoKey);
        if (cmsTotalSeconds != null && cmsTotalSeconds > 0)
        {
            return cmsTotalSeconds;
        }
        return switch (videoKey)
        {
            case "video-1" -> 36;
            case "video-2" -> 58;
            case "video-3" -> 42;
            default -> 60;
        };
    }

    private Integer resolveCmsVideoTotalSeconds(String videoKey)
    {
        if (StringUtils.isEmpty(videoKey))
        {
            return null;
        }
        for (YgbPortalContent item : selectPublishedContent(SECTION_VIDEO, 10))
        {
            JSONObject extra = parseExtraJson(item.getExtraJson());
            if (!videoKey.equals(resolveVideoKey(item, extra)))
            {
                continue;
            }
            return resolveVideoTotalSeconds(videoKey, extra);
        }
        return null;
    }

    private int resolveVideoTotalSeconds(String videoKey, JSONObject extra)
    {
        Integer value = jsonInteger(extra, "durationSeconds");
        return value != null && value > 0 ? value : fallbackVideoSeconds(videoKey);
    }

    private int fallbackVideoSeconds(String videoKey)
    {
        return switch (videoKey)
        {
            case "video-1" -> 36;
            case "video-2" -> 58;
            case "video-3" -> 42;
            default -> 60;
        };
    }

    private String fallbackVideoTitle(String videoKey)
    {
        return switch (videoKey)
        {
            case "video-1" -> "工伤预防基础知识";
            case "video-2" -> "高处作业安全提示";
            case "video-3" -> "设备作业前自检";
            default -> "工伤预防视频";
        };
    }

    private String fallbackVideoDesc(String videoKey)
    {
        return switch (videoKey)
        {
            case "video-1" -> "围绕入场、作业、防护和报案流程的基础培训。";
            case "video-2" -> "重点提示安全带、脚手架和现场监护要求。";
            case "video-3" -> "开工前先检查电源、围栏、设备外观和作业记录。";
            default -> "工伤预防视频学习内容。";
        };
    }

    private List<String> fallbackVideoKeyPoints(String videoKey)
    {
        return switch (videoKey)
        {
            case "video-1" -> Arrays.asList("入场前先核对培训、考勤和劳动合同状态。", "作业中佩戴好基础防护用品，发现异常先停工。",
                "发生工伤后第一时间留痕并补齐病历、照片和考勤。");
            case "video-2" -> Arrays.asList("高处作业前检查安全带、安全绳和脚手架状态。", "现场必须有监护措施，严禁单人冒险作业。",
                "发现围栏、支撑或坠落防护异常时立即停工上报。");
            case "video-3" -> Arrays.asList("开工前先看设备电源、围栏和外观是否异常。", "核对点检记录和当日作业授权，再进行开机。",
                "发现设备异响、报警或防护缺失时不得强行作业。");
            default -> Arrays.asList("结合岗位风险完成本次视频学习。");
        };
    }

    private List<String> defaultVideoFallbackTips(String videoUrl)
    {
        if (StringUtils.isNotEmpty(videoUrl))
        {
            return Arrays.asList(
                "如视频播放异常，可先阅读下方关键要点，再回到视频继续学习。",
                "完成学习后建议保存进度，并结合本月培训题目复核风险要点。");
        }
        return Arrays.asList(
            "在线视频暂未同步，当前已切换为文字摘要模式。",
            "请先阅读下方关键要点，后续再结合培训题目完成复核。");
    }

    private List<String> defaultActivityRules()
    {
        return Arrays.asList(
            "完成本月培训任务。",
            "每个账号每月可报名 1 次。",
            "活动结果以平台公告为准。");
    }

    private List<String> defaultAiQuestionList()
    {
        return Arrays.asList(
            "进入岗位前，今天有哪些高风险动作需要先确认防护？",
            "发现设备、脚手架或电源异常时，第一步该做什么？");
    }

    private List<String> defaultAiCaseList()
    {
        return Collections.singletonList(
            "案例提示：未先完成培训或未排查岗位风险就作业，往往会同时影响打卡、工资查询和事故留痕。");
    }

    private List<String> defaultAiActionTips()
    {
        return Arrays.asList(
            "先完成本月培训题目。",
            "结合岗位查看工伤预防视频，确认当日风险点。",
            "遇到异常先停工留痕，再进入法律咨询或投诉举报。");
    }

    private String resolveVideoKey(YgbPortalContent item, JSONObject extra)
    {
        return firstNonBlank(jsonString(extra, "videoKey"), item == null ? null : item.getCategoryCode());
    }

    private boolean joinedActivity(String activityKey, Long userId)
    {
        if (StringUtils.isEmpty(activityKey) || userId == null)
        {
            return false;
        }
        Long count = workerContentMapper.countActivityJoin(activityKey, userId);
        return count != null && count > 0;
    }

    private String formatDuration(int totalSeconds)
    {
        int minute = totalSeconds / 60;
        int second = totalSeconds % 60;
        return String.format("%02d:%02d", minute, second);
    }

    private String activityTitle(String activityKey)
    {
        if (StringUtils.isNotEmpty(activityKey))
        {
            for (YgbPortalContent item : selectPublishedContent(SECTION_ACTIVITY, 10))
            {
                JSONObject extra = parseExtraJson(item.getExtraJson());
                String resolvedKey = firstNonBlank(jsonString(extra, "activityKey"), item.getCategoryCode());
                if (activityKey.equals(resolvedKey))
                {
                    return firstNonBlank(item.getTitle(), DEFAULT_ACTIVITY_TITLE);
                }
            }
        }
        if (DEFAULT_ACTIVITY_KEY.equals(activityKey))
        {
            return DEFAULT_ACTIVITY_TITLE;
        }
        return "福利活动";
    }

    private String activityStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "已完成";
        }
        if ("2".equals(status))
        {
            return "已发放";
        }
        if ("3".equals(status))
        {
            return "未中奖";
        }
        return "已报名";
    }

    private String activityLotteryStatusText(String status)
    {
        return switch (firstNonBlank(status, "")) {
            case "WON" -> "已中奖";
            case "LOST" -> "未中奖";
            case "NOT_JOINED" -> "未进入开奖队列";
            default -> "待开奖";
        };
    }

    private String activityDeliveryStatusText(String status)
    {
        return switch (firstNonBlank(status, "")) {
            case "DELIVERED" -> "已发放";
            case "FAILED" -> "发放失败";
            case "NOT_JOINED" -> "未发放";
            default -> "待发放";
        };
    }

    private String resolveActivityManageStatus(String lotteryStatus, String deliveryStatus)
    {
        if ("DELIVERED".equals(deliveryStatus))
        {
            return "2";
        }
        if ("LOST".equals(lotteryStatus))
        {
            return "3";
        }
        if ("WON".equals(lotteryStatus))
        {
            return "1";
        }
        return "0";
    }

    private String defaultActivityStatusMessage(String lotteryStatus, String deliveryStatus)
    {
        if ("DELIVERED".equals(deliveryStatus))
        {
            return "奖品已发放，请以平台到账或发放记录为准。";
        }
        if ("FAILED".equals(deliveryStatus))
        {
            return "奖品发放失败，请核对发放条件或联系管理员处理。";
        }
        if ("WON".equals(lotteryStatus))
        {
            return "当前已中奖，等待平台完成奖品发放。";
        }
        if ("LOST".equals(lotteryStatus))
        {
            return "本次活动未中奖，可继续关注后续活动。";
        }
        return "当前活动仍在开奖队列中，请等待平台后续通知。";
    }

    private boolean activityHandleChanged(WorkerActivityJoin before, WorkerActivityJoin target)
    {
        return !sameText(before == null ? null : before.getStatus(), target == null ? null : target.getStatus())
            || !sameText(before == null ? null : before.getRemark(), target == null ? null : target.getRemark());
    }

    private void appendActivityOrderSnapshot(Map<String, Object> detail, Long userId, String activityKey)
    {
        if (detail == null || StringUtils.isEmpty(activityKey))
        {
            return;
        }
        WorkerActivityJoin join = workerContentMapper.selectLatestActivityJoin(activityKey, userId);
        if (join == null)
        {
            detail.put("orderSnapshot", defaultActivityOrderSnapshot(activityKey));
            return;
        }
        detail.put("orderSnapshot", buildActivityOrderSnapshot(join));
    }

    private Map<String, Object> buildActivityOrderSnapshot(WorkerActivityJoin join)
    {
        JSONObject extra = parseExtraJson(join == null ? null : join.getRemark());
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("orderNo", firstNonBlank(jsonString(extra, "orderNo"),
            "ACT-" + String.format("%06d", join == null || join.getJoinId() == null ? 0L : join.getJoinId())));
        snapshot.put("payStatus", firstNonBlank(jsonString(extra, "payStatus"), "QUALIFIED"));
        snapshot.put("payStatusText", firstNonBlank(jsonString(extra, "payStatusText"), "资格已锁定"));
        snapshot.put("lotteryStatus", firstNonBlank(jsonString(extra, "lotteryStatus"), "PENDING"));
        snapshot.put("lotteryStatusText", firstNonBlank(jsonString(extra, "lotteryStatusText"), "待开奖"));
        snapshot.put("rewardTitle", firstNonBlank(jsonString(extra, "rewardTitle"), "最高 188 元立减金"));
        snapshot.put("deliveryStatus", firstNonBlank(jsonString(extra, "deliveryStatus"), "PENDING"));
        snapshot.put("deliveryStatusText", firstNonBlank(jsonString(extra, "deliveryStatusText"), "待发放"));
        snapshot.put("eligibilityText", firstNonBlank(jsonString(extra, "eligibilityText"), "支付分达标后进入开奖队列"));
        snapshot.put("deliveryText", firstNonBlank(jsonString(extra, "deliveryText"), "活动结果以平台通知和发放记录为准"));
        snapshot.put("joinTime", join == null ? "" : formatDateTime(join.getCreateTime()));
        snapshot.put("qualifiedTime", firstNonBlank(jsonString(extra, "qualifiedTime"), join == null ? "" : formatDateTime(join.getCreateTime())));
        snapshot.put("lotteryTime", firstNonBlank(jsonString(extra, "lotteryTime"), ""));
        snapshot.put("deliveryTime", firstNonBlank(jsonString(extra, "deliveryTime"), ""));
        snapshot.put("paymentChannel", firstNonBlank(jsonString(extra, "paymentChannel"), "支付分资格"));
        snapshot.put("statusTimeline", buildActivityStatusTimeline(extra, join));
        snapshot.put("joined", true);
        return snapshot;
    }

    private Map<String, Object> activityJoinRow(WorkerActivityJoin item)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("joinId", item.getJoinId());
        row.put("activityKey", item.getActivityKey());
        row.put("activityTitle", activityTitle(item.getActivityKey()));
        row.put("status", item.getStatus());
        row.put("statusText", activityStatusText(item.getStatus()));
        row.put("joinTime", formatDateTime(item.getCreateTime()));
        row.put("updateTime", formatDateTime(item.getUpdateTime()));
        row.put("personName", firstNonBlank(item.getPersonName(), ""));
        row.put("mobile", firstNonBlank(item.getMobile(), ""));
        row.putAll(buildActivityOrderSnapshot(item));
        return row;
    }

    private Map<String, Object> defaultActivityOrderSnapshot(String activityKey)
    {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("orderNo", "");
        snapshot.put("payStatus", "NOT_JOINED");
        snapshot.put("payStatusText", "未报名");
        snapshot.put("lotteryStatus", "NOT_JOINED");
        snapshot.put("lotteryStatusText", "未进入开奖队列");
        snapshot.put("rewardTitle", activityTitle(activityKey));
        snapshot.put("deliveryStatus", "NOT_JOINED");
        snapshot.put("deliveryStatusText", "未发放");
        snapshot.put("eligibilityText", "完成报名后可查看资格和开奖进度");
        snapshot.put("deliveryText", "当前尚未生成活动订单记录");
        snapshot.put("joinTime", "");
        snapshot.put("qualifiedTime", "");
        snapshot.put("lotteryTime", "");
        snapshot.put("deliveryTime", "");
        snapshot.put("paymentChannel", "");
        snapshot.put("statusTimeline", defaultActivityStatusTimeline());
        snapshot.put("joined", false);
        return snapshot;
    }

    private String buildActivityOrderRemark(String activityKey, WorkerActivityJoin join)
    {
        String orderNo = buildActivityOrderNo(join);
        String joinTime = join == null ? formatDateTime(new Date()) : formatDateTime(firstNonNull(join.getCreateTime(), new Date()));
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("orderNo", orderNo);
        snapshot.put("payStatus", "QUALIFIED");
        snapshot.put("payStatusText", "资格已锁定");
        snapshot.put("lotteryStatus", "PENDING");
        snapshot.put("lotteryStatusText", "待开奖");
        snapshot.put("rewardTitle", "最高 188 元立减金");
        snapshot.put("deliveryStatus", "PENDING");
        snapshot.put("deliveryStatusText", "待发放");
        snapshot.put("eligibilityText", "当前活动按支付分达标规则进入开奖队列");
        snapshot.put("deliveryText", "开奖结果和发放结果将通过活动记录与通知中心同步");
        snapshot.put("joinTime", joinTime);
        snapshot.put("qualifiedTime", joinTime);
        snapshot.put("lotteryTime", "");
        snapshot.put("deliveryTime", "");
        snapshot.put("paymentChannel", "支付分资格");
        snapshot.put("activityKey", activityKey);
        Map<String, Object> compactSnapshot = new LinkedHashMap<>();
        compactSnapshot.put("orderNo", orderNo);
        compactSnapshot.put("payStatus", "QUALIFIED");
        compactSnapshot.put("lotteryStatus", "PENDING");
        compactSnapshot.put("deliveryStatus", "PENDING");
        compactSnapshot.put("joinTime", joinTime);
        compactSnapshot.put("activityKey", activityKey);
        return limitRemark(JSON.toJSONString(compactSnapshot));
    }

    private String limitRemark(String value)
    {
        if (value == null || value.length() <= REMARK_MAX_LENGTH)
        {
            return value;
        }
        return value.substring(0, REMARK_MAX_LENGTH);
    }

    private String buildActivityOrderNo(WorkerActivityJoin join)
    {
        if (join != null && join.getJoinId() != null)
        {
            return "ACT-" + String.format("%06d", join.getJoinId());
        }
        return "ACT-" + DateUtils.dateTimeNow();
    }

    private List<Map<String, Object>> buildActivityStatusTimeline(JSONObject extra, WorkerActivityJoin join)
    {
        List<Map<String, Object>> timeline = new ArrayList<>();
        String joinTime = firstNonBlank(jsonString(extra, "joinTime"), join == null ? "" : formatDateTime(join.getCreateTime()));
        String qualifiedTime = firstNonBlank(jsonString(extra, "qualifiedTime"), joinTime);
        String lotteryTime = firstNonBlank(jsonString(extra, "lotteryTime"), "");
        String deliveryTime = firstNonBlank(jsonString(extra, "deliveryTime"), "");
        timeline.add(activityTimelineNode("报名成功", joinTime, "活动资格已登记，已生成活动订单。"));
        timeline.add(activityTimelineNode("资格锁定", qualifiedTime, firstNonBlank(jsonString(extra, "eligibilityText"), "当前活动按支付分达标规则进入开奖队列")));
        timeline.add(activityTimelineNode("待开奖", lotteryTime, firstNonBlank(jsonString(extra, "lotteryStatusText"), "待开奖")));
        timeline.add(activityTimelineNode("待发放", deliveryTime, firstNonBlank(jsonString(extra, "deliveryText"), "开奖结果和发放结果将通过活动记录与通知中心同步")));
        return timeline;
    }

    private List<Map<String, Object>> defaultActivityStatusTimeline()
    {
        List<Map<String, Object>> timeline = new ArrayList<>();
        timeline.add(activityTimelineNode("未报名", "", "完成报名后生成活动订单。"));
        timeline.add(activityTimelineNode("资格校验", "", "完成培训并满足活动条件后进入资格校验。"));
        timeline.add(activityTimelineNode("开奖结果", "", "资格达标后进入开奖队列。"));
        timeline.add(activityTimelineNode("奖品发放", "", "活动结果以平台通知和发放记录为准。"));
        return timeline;
    }

    private Map<String, Object> activityTimelineNode(String title, String time, String desc)
    {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("title", title);
        node.put("time", firstNonBlank(time, ""));
        node.put("desc", firstNonBlank(desc, ""));
        return node;
    }

    private void validateActivityHandleRequest(WorkerActivityHandleRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getLotteryStatus()) || StringUtils.isEmpty(request.getDeliveryStatus()))
        {
            throw new ServiceException("活动处理参数不完整。");
        }
        String lotteryStatus = request.getLotteryStatus().trim();
        String deliveryStatus = request.getDeliveryStatus().trim();
        if (!Arrays.asList("PENDING", "WON", "LOST").contains(lotteryStatus))
        {
            throw new ServiceException("不支持的开奖状态。");
        }
        if (!Arrays.asList("PENDING", "DELIVERED", "FAILED").contains(deliveryStatus))
        {
            throw new ServiceException("不支持的发放状态。");
        }
        if ("LOST".equals(lotteryStatus) && "DELIVERED".equals(deliveryStatus))
        {
            throw new ServiceException("未中奖记录不能直接标记为已发放。");
        }
    }

    private JSONObject parseExtraJson(String extraJson)
    {
        if (StringUtils.isEmpty(extraJson))
        {
            return new JSONObject();
        }
        try
        {
            JSONObject json = JSON.parseObject(extraJson);
            return json == null ? new JSONObject() : json;
        }
        catch (Exception ignored)
        {
            return new JSONObject();
        }
    }

    private List<String> stringList(JSONObject json, String key, List<String> defaultValue)
    {
        if (json == null || StringUtils.isEmpty(key))
        {
            return defaultValue;
        }
        JSONArray array = json.getJSONArray(key);
        if (array == null || array.isEmpty())
        {
            String single = jsonString(json, key);
            if (StringUtils.isEmpty(single))
            {
                return defaultValue;
            }
            return Arrays.asList(single.split("\\r?\\n"));
        }
        List<String> rows = new ArrayList<>();
        for (Object item : array)
        {
            if (item == null)
            {
                continue;
            }
            String text = String.valueOf(item).trim();
            if (StringUtils.isNotEmpty(text))
            {
                rows.add(text);
            }
        }
        return rows.isEmpty() ? defaultValue : rows;
    }

    private String jsonString(JSONObject json, String key)
    {
        if (json == null || StringUtils.isEmpty(key))
        {
            return null;
        }
        String value = json.getString(key);
        return StringUtils.isEmpty(value) ? null : value.trim();
    }

    private Integer jsonInteger(JSONObject json, String key)
    {
        if (json == null || StringUtils.isEmpty(key))
        {
            return null;
        }
        Integer value = json.getInteger(key);
        return value != null ? value : null;
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

    private boolean sameText(String left, String right)
    {
        String normalizedLeft = trimToNull(left);
        String normalizedRight = trimToNull(right);
        if (normalizedLeft == null)
        {
            return normalizedRight == null;
        }
        return normalizedLeft.equals(normalizedRight);
    }

    private String trimToNull(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String formatDateTime(Date date)
    {
        return date == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, date);
    }

    private Date firstNonNull(Date... values)
    {
        if (values == null)
        {
            return null;
        }
        for (Date value : values)
        {
            if (value != null)
            {
                return value;
            }
        }
        return null;
    }
}
