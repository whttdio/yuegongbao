package com.yuegongbao.ygb.worker.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.utils.DateUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerContentService;
import com.yuegongbao.ygb.worker.service.WorkerHomeService;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerProfileService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@Service
public class WorkerHomeServiceImpl implements WorkerHomeService
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private static final Locale LOCALE_ZH = Locale.CHINA;

    @Autowired
    private WorkerTrainingService workerTrainingService;

    @Autowired
    private WorkerContentService workerContentService;

    @Autowired
    private WorkerMessageService workerMessageService;

    @Autowired
    private WorkerProfileService workerProfileService;

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Override
    public Map<String, Object> getHomeView(YgbPerson worker, Long userId)
    {
        WorkerTrainingProgress trainingProgress = workerTrainingService.getProgress(worker);
        WorkerResume resume = workerProfileMapper.selectWorkerResume(userId);
        boolean trainingReminderTriggered = workerMessageService.notifyTrainingLockReminder(worker, userId,
            trainingProgress, "worker-home");
        String latestArrearsMonth = resolveLatestArrearsMonth(worker);
        boolean insuranceReminderTriggered = workerMessageService.notifyInsuranceArrearsReminder(worker, userId,
            worker == null ? null : worker.getInsuranceStatus(), latestArrearsMonth, "worker-home");
        boolean certificateReminderTriggered = workerMessageService.notifyCertificateReminder(worker, userId, resume,
            "worker-home");
        Map<String, Object> activityDetail = workerContentService.getActivityDetail(userId);
        Map<String, Object> videoResult = workerContentService.getVideoList(userId);
        Map<String, Object> articleResult = workerMessageService.getLegalArticleList();
        Map<String, Object> noticeResult = workerMessageService.getNoticeList(userId, 1, 3);
        int unreadNoticeCount = workerMessageService.getUnreadNoticeCount(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platformLabel", "广东省用工保障A监测平台 v6.1");
        result.put("brandTitle", "阳光劳务");
        result.put("enterpriseName", resolveEmployerEnterpriseName(worker));
        result.put("dateText", DATE_FORMATTER.format(LocalDate.now()));
        result.put("weekText", currentWeekText());
        result.put("trainingProgress", trainingProgressMap(trainingProgress));
        result.put("canCheckIn", !trainingProgress.isLockAttendance());
        result.put("canCheckOut", !trainingProgress.isLockAttendance());
        result.put("canViewSalary", !trainingProgress.isLockSalary());
        result.put("clockRuleTip", buildClockRuleTip(trainingProgress));
        result.put("workerCard", buildWorkerCard(worker));
        result.put("unreadNoticeCount", unreadNoticeCount);
        result.put("trainingReminderTriggered", trainingReminderTriggered);
        result.put("insuranceReminderTriggered", insuranceReminderTriggered);
        result.put("certificateReminderTriggered", certificateReminderTriggered);
        result.put("latestArrearsMonth", latestArrearsMonth);
        result.put("quickEntries", buildQuickEntries(trainingProgress));
        result.put("moreEntries", buildMoreEntries());
        result.put("noticeList", adaptNoticeList(noticeResult.get("rows")));
        result.put("activityCard", buildActivityCard(activityDetail));
        result.put("recommendCards", buildRecommendCards(articleResult));
        result.put("videoCards", buildVideoCards(videoResult.get("rows")));
        return result;
    }

    @Override
    public Map<String, Object> getProfile(YgbPerson worker, Long userId, String userName, String nickName)
    {
        WorkerResume resume = workerProfileMapper.selectWorkerResume(userId);
        boolean certificateReminderTriggered = workerMessageService.notifyCertificateReminder(worker, userId, resume,
            "worker-profile");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", userId);
        result.put("userName", userName);
        result.put("nickName", nickName);
        result.put("personId", worker.getPersonId());
        result.put("personName", worker.getPersonName());
        result.put("personNameMasked", maskName(worker.getPersonName()));
        result.put("mobile", worker.getMobile());
        result.put("mobileMasked", maskMobile(worker.getMobile()));
        result.put("idCard", worker.getIdCard());
        result.put("idCardMasked", maskIdCard(worker.getIdCard()));
        result.put("enterpriseId", worker.getEnterpriseId());
        result.put("enterpriseName", resolveEmployerEnterpriseName(worker));
        result.put("jobType", firstNonBlank(worker.getJobType(), "待同步"));
        result.put("workerType", workerTypeText(worker.getWorkerType()));
        result.put("insuranceStatus", insuranceStatusText(worker.getInsuranceStatus()));
        result.put("employmentStatus", employmentStatusText(worker.getEmploymentStatus()));
        result.put("certStatus", firstNonBlank(worker.getCertStatus(), "0"));
        result.put("certStatusText", certStatusText(worker.getCertStatus()));
        result.put("realNameVerified", isRealNameVerified(worker));
        result.put("realNameStatusText", isRealNameVerified(worker) ? "已实名核验" : "待实名核验");
        result.put("unreadNoticeCount", workerMessageService.getUnreadNoticeCount(userId));
        result.put("certificateReminderTriggered", certificateReminderTriggered);
        return result;
    }

    @Override
    public Map<String, Object> getWorkbenchView(YgbPerson worker, Long userId, String userName, String nickName)
    {
        Map<String, Object> homeView = getHomeView(worker, userId);
        Map<String, Object> profileView = getProfile(worker, userId, userName, nickName);
        Map<String, Object> helpView = workerProfileService.getHelpList();

        List<Map<String, Object>> quickEntries = castRows(homeView.get("quickEntries"));
        List<Map<String, Object>> moreEntries = castRows(homeView.get("moreEntries"));
        List<Map<String, Object>> quickActions = castRows(helpView.get("quickActions"));
        List<Map<String, Object>> serviceCards = castRows(helpView.get("serviceCards"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("heroSummary", buildWorkbenchHeroSummary(homeView, profileView, quickActions));
        result.put("clockRuleTip", homeView.get("clockRuleTip"));
        result.put("trainingProgress", homeView.get("trainingProgress"));
        result.put("unreadNoticeCount", profileView.get("unreadNoticeCount"));
        result.put("quickActions", quickActions);
        result.put("serviceCards", serviceCards);
        result.put("commonEntries", filterEntries(quickEntries,
            Arrays.asList("attendance", "salary", "social", "tax", "training", "camera", "job")));
        result.put("rightsEntries", buildWorkbenchRightsEntries(quickEntries));
        result.put("contentEntries", buildWorkbenchContentEntries());
        result.put("personalEntries", buildWorkbenchPersonalEntries(moreEntries));
        return result;
    }

    private Map<String, Object> trainingProgressMap(WorkerTrainingProgress progress)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("completed", progress.getCompleted());
        result.put("total", progress.getTotal());
        result.put("needComplete", progress.isNeedComplete());
        result.put("lockAttendance", progress.isLockAttendance());
        result.put("lockSalary", progress.isLockSalary());
        return result;
    }

    private Map<String, Object> buildWorkerCard(YgbPerson worker)
    {
        String qrText = "工牌码 " + firstNonBlank(String.valueOf(worker.getPersonId()), "待同步");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("personName", worker.getPersonName());
        result.put("personNameMasked", maskName(worker.getPersonName()));
        result.put("jobType", firstNonBlank(worker.getJobType(), "待同步"));
        result.put("workerType", workerTypeText(worker.getWorkerType()));
        result.put("qrCodeText", qrText);
        result.put("qrCodeUrl", buildQrSvgDataUrl(qrText));
        return result;
    }

    private String buildWorkbenchHeroSummary(Map<String, Object> homeView, Map<String, Object> profileView,
        List<Map<String, Object>> quickActions)
    {
        boolean canCheckIn = !Boolean.FALSE.equals(homeView.get("canCheckIn"));
        int unreadNoticeCount = parseInt(profileView.get("unreadNoticeCount"));
        if (!canCheckIn)
        {
            return "优先完成本月培训，再继续打卡和工资相关动作。";
        }
        if (unreadNoticeCount > 0)
        {
            return "你有新的通知、协同服务和扩展入口可继续处理。";
        }
        if (!quickActions.isEmpty())
        {
            return "集中进入培训、维权、内容和个人服务能力。";
        }
        return "工作台已汇总当前账号的常用服务与协同入口。";
    }

    private List<Map<String, Object>> buildQuickEntries(WorkerTrainingProgress progress)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(entry("attendance", "考勤", "/pages/attendance/checkin", progress.isLockAttendance(),
            "请先完成本月安全培训后再打卡"));
        rows.add(entry("salary", "工资", "/pages/salary/list", progress.isLockSalary(),
            "请先完成本月安全培训后再查看工资"));
        rows.add(entry("social", "社保", "/pages/social/list", false, ""));
        rows.add(entry("tax", "个税", "/pages/tax/list", false, ""));
        rows.add(entry("training", "培训", "/pages/training/index", false, ""));
        rows.add(entry("camera", "拍照", "/pages/camera/index", false, ""));
        rows.add(entry("job", "找工作", "/pages/job/list", false, ""));
        rows.add(entry("legal", "法律咨询", "/pages/legal/index", false, ""));
        rows.add(entry("complaint", "投诉举报", "/pages/complaint/index", false, ""));
        return rows;
    }

    private List<Map<String, Object>> buildMoreEntries()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(entry("resume", "我的简历", "/pages/profile/resume", false, ""));
        rows.add(entry("contract", "我的合同", "/pages/profile/labor-contracts", false, ""));
        rows.add(entry("security", "保险保障", "/pages/profile/security", false, ""));
        rows.add(entry("union", "工会服务", "/pages/union/index", false, ""));
        rows.add(entry("points", "积分商城", "/pages/profile/points", false, ""));
        rows.add(entry("help", "帮助中心", "/pages/profile/help", false, ""));
        rows.add(entry("settings", "设置", "/pages/profile/settings", false, ""));
        return rows;
    }

    private List<Map<String, Object>> buildWorkbenchRightsEntries(List<Map<String, Object>> quickEntries)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        appendEntryIfPresent(rows, findEntry(quickEntries, "legal"));
        appendEntryIfPresent(rows, findEntry(quickEntries, "complaint"));
        rows.add(entry("help", "帮助中心", "/pages/profile/help", false, ""));
        rows.add(entry("union", "工会服务", "/pages/union/index", false, ""));
        return rows;
    }

    private List<Map<String, Object>> buildWorkbenchContentEntries()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(entry("lecture", "公益讲座", "/pages/legal/article-list", false, ""));
        rows.add(entry("ai", "AI培训", "/pages/ai-training/detail", false, ""));
        rows.add(entry("video", "预防视频", "/pages/video/list", false, ""));
        rows.add(entry("activity", "福利活动", "/pages/activity/detail", false, ""));
        return rows;
    }

    private List<Map<String, Object>> buildWorkbenchPersonalEntries(List<Map<String, Object>> moreEntries)
    {
        List<Map<String, Object>> rows = new ArrayList<>(moreEntries);
        rows.add(entry("apply-list", "投递记录", "/pages/job/apply-list", false, ""));
        rows.add(entry("profile", "个人中心", "/pages/profile/index", false, ""));
        return rows;
    }

    private Map<String, Object> buildActivityCard(Map<String, Object> activityDetail)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activityKey", activityDetail.get("activityKey"));
        result.put("title", firstNonBlank((String) activityDetail.get("title"), "月月抽最高188元立减金"));
        result.put("subtitle", firstNonBlank((String) activityDetail.get("subtitle"), "完成培训并保持良好用工记录即可参与"));
        result.put("joined", Boolean.TRUE.equals(activityDetail.get("joined")));
        result.put("buttonText", Boolean.TRUE.equals(activityDetail.get("joined")) ? "已报名" : "立即参与");
        result.put("externalUrl", activityDetail.get("externalUrl"));
        result.put("jumpUrl", "/pages/activity/detail");
        result.put("target", Map.of(
            "path", "/pages/activity/detail",
            "query", Map.of("activityKey", firstNonBlank((String) activityDetail.get("activityKey"), ""))));
        return result;
    }

    private List<Map<String, Object>> buildRecommendCards(Map<String, Object> articleResult)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> articles = castRows(articleResult.get("rows"));
        if (!articles.isEmpty())
        {
            Map<String, Object> article = articles.get(0);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("key", "legal-article");
            row.put("title", firstNonBlank((String) article.get("title"), "法律公益讲座"));
            row.put("desc", firstNonBlank((String) article.get("summary"), "结合工资、考勤和合同场景梳理维权要点。"));
            row.put("path", "/pages/legal/article-detail?articleKey="
                + encode((String) article.get("articleKey")));
            rows.add(row);
        }

        Map<String, Object> aiTraining = new LinkedHashMap<>();
        aiTraining.put("key", "ai-training");
        aiTraining.put("title", "工伤 AI 培训");
        aiTraining.put("desc", "结合岗位风险、工伤留痕和维权链路给出学习建议。");
        aiTraining.put("path", "/pages/ai-training/detail");
        rows.add(aiTraining);
        return rows;
    }

    private List<Map<String, Object>> buildVideoCards(Object videoRows)
    {
        List<Map<String, Object>> sourceRows = castRows(videoRows);
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < sourceRows.size() && i < 3; i++)
        {
            Map<String, Object> item = sourceRows.get(i);
            Map<String, Object> row = new LinkedHashMap<>(item);
            row.put("duration", item.get("durationText"));
            row.put("path", "/pages/video/detail?videoKey=" + encode((String) item.get("videoKey")));
            result.add(row);
        }
        return result;
    }

    private List<Map<String, Object>> adaptNoticeList(Object rows)
    {
        List<Map<String, Object>> sourceRows = castRows(rows);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : sourceRows)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("noticeId", item.get("noticeId"));
            row.put("title", firstNonBlank((String) item.get("title"), "平台通知"));
            row.put("summary", item.get("summary"));
            row.put("noticeType", item.get("noticeType"));
            row.put("publishTime", item.get("publishTime"));
            row.put("isRead", readFlag(item.get("readFlag")));
            result.add(row);
        }
        return result;
    }

    private Map<String, Object> entry(String key, String label, String path, boolean locked, String lockReason)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("label", label);
        row.put("path", path);
        row.put("locked", locked);
        row.put("lockReason", lockReason);
        return row;
    }

    private List<Map<String, Object>> filterEntries(List<Map<String, Object>> sourceRows, List<String> keys)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String key : keys)
        {
            Map<String, Object> row = findEntry(sourceRows, key);
            if (row != null)
            {
                rows.add(row);
            }
        }
        return rows;
    }

    private Map<String, Object> findEntry(List<Map<String, Object>> rows, String key)
    {
        for (Map<String, Object> row : rows)
        {
            if (key.equals(row.get("key")))
            {
                return row;
            }
        }
        return null;
    }

    private void appendEntryIfPresent(List<Map<String, Object>> rows, Map<String, Object> row)
    {
        if (row != null)
        {
            rows.add(row);
        }
    }

    private boolean readFlag(Object value)
    {
        if (value instanceof Boolean booleanValue)
        {
            return booleanValue;
        }
        if (value instanceof Number number)
        {
            return number.intValue() > 0;
        }
        return "1".equals(String.valueOf(value)) || "true".equalsIgnoreCase(String.valueOf(value));
    }

    private int parseInt(Object value)
    {
        if (value instanceof Number number)
        {
            return number.intValue();
        }
        if (value == null)
        {
            return 0;
        }
        try
        {
            return Integer.parseInt(String.valueOf(value));
        }
        catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    private List<Map<String, Object>> castRows(Object rows)
    {
        if (!(rows instanceof List<?> list))
        {
            return new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : list)
        {
            if (item instanceof Map<?, ?> map)
            {
                Map<String, Object> row = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet())
                {
                    row.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                result.add(row);
            }
        }
        return result;
    }

    private String buildClockRuleTip(WorkerTrainingProgress progress)
    {
        if (progress.isNeedComplete())
        {
            return "本月安全培训已完成 " + progress.getCompleted() + "/" + progress.getTotal()
                + "，完成后才能解锁打卡和工资查询。";
        }
        return "本月安全培训已完成，可正常打卡并查看工资。";
    }

    private String currentWeekText()
    {
        return "星期" + LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.NARROW, LOCALE_ZH);
    }

    private String resolveEmployerEnterpriseName(YgbPerson worker)
    {
        if (worker == null || worker.getPersonId() == null)
        {
            return "鏈粦瀹氱敤宸ュ崟浣?";
        }
        YgbContract contract = contractMapper.selectEffectiveContractByPersonId(worker.getPersonId(),
            DateUtils.toDate(LocalDate.now()));
        if (contract != null)
        {
            return firstNonBlank(contract.getEmployerEnterpriseName(), contract.getDispatchEnterpriseName(),
                worker.getEnterpriseName(), "鏈粦瀹氱敤宸ュ崟浣?");
        }
        return defaultEnterpriseName(worker);
    }

    private String defaultEnterpriseName(YgbPerson worker)
    {
        return firstNonBlank(worker.getEnterpriseName(), "未绑定用工单位");
    }

    private String resolveLatestArrearsMonth(YgbPerson worker)
    {
        if (worker == null || worker.getPersonId() == null)
        {
            return null;
        }
        List<YgbSocialPayment> payments = socialPaymentMapper.selectWorkerSocialPaymentList(
            worker.getPersonId(), String.valueOf(LocalDate.now().getYear()));
        if (payments == null)
        {
            return null;
        }
        for (YgbSocialPayment item : payments)
        {
            if (item != null && "2".equals(item.getPaymentStatus()) && StringUtils.isNotEmpty(item.getStatMonth()))
            {
                return item.getStatMonth();
            }
        }
        return null;
    }

    private String workerTypeText(String value)
    {
        return switch (value)
        {
            case "1" -> "派遣工";
            case "2" -> "正式工";
            case "3" -> "外包工";
            case "4" -> "新业态人员";
            default -> firstNonBlank(value, "待同步");
        };
    }

    private String insuranceStatusText(String value)
    {
        return switch (value)
        {
            case "0" -> "未参保";
            case "1" -> "已参保";
            case "2" -> "停保";
            default -> firstNonBlank(value, "待同步");
        };
    }

    private String employmentStatusText(String value)
    {
        return switch (value)
        {
            case "0" -> "在岗";
            case "1" -> "离岗";
            default -> firstNonBlank(value, "待同步");
        };
    }

    private String certStatusText(String value)
    {
        return switch (value)
        {
            case "1" -> "证书有效";
            case "2" -> "证书临期";
            case "3" -> "证书过期";
            default -> "待校验证书";
        };
    }

    private boolean isRealNameVerified(YgbPerson worker)
    {
        return StringUtils.isNotEmpty(worker.getPersonName())
            && StringUtils.isNotEmpty(worker.getIdCard())
            && StringUtils.isNotEmpty(worker.getMobile());
    }

    private String maskName(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return "";
        }
        if (value.length() <= 1)
        {
            return value;
        }
        if (value.length() == 2)
        {
            return value.charAt(0) + "*";
        }
        return value.charAt(0) + "*" + value.charAt(value.length() - 1);
    }

    private String maskMobile(String value)
    {
        if (StringUtils.isEmpty(value) || value.length() < 7)
        {
            return firstNonBlank(value, "");
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    private String maskIdCard(String value)
    {
        if (StringUtils.isEmpty(value) || value.length() < 8)
        {
            return firstNonBlank(value, "");
        }
        return value.substring(0, 4) + "**********" + value.substring(value.length() - 4);
    }

    private String buildQrSvgDataUrl(String text)
    {
        String safeText = escapeXml(text);
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"160\" height=\"160\" viewBox=\"0 0 160 160\">"
            + "<rect width=\"160\" height=\"160\" rx=\"16\" fill=\"#ffffff\"/>"
            + "<rect x=\"18\" y=\"18\" width=\"34\" height=\"34\" rx=\"6\" fill=\"#16324f\"/>"
            + "<rect x=\"108\" y=\"18\" width=\"34\" height=\"34\" rx=\"6\" fill=\"#16324f\"/>"
            + "<rect x=\"18\" y=\"108\" width=\"34\" height=\"34\" rx=\"6\" fill=\"#16324f\"/>"
            + "<rect x=\"66\" y=\"24\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"84\" y=\"24\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"66\" y=\"42\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"84\" y=\"42\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"60\" y=\"72\" width=\"12\" height=\"12\" fill=\"#16324f\"/>"
            + "<rect x=\"78\" y=\"72\" width=\"12\" height=\"12\" fill=\"#16324f\"/>"
            + "<rect x=\"96\" y=\"72\" width=\"12\" height=\"12\" fill=\"#16324f\"/>"
            + "<rect x=\"42\" y=\"90\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"60\" y=\"90\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"78\" y=\"90\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"96\" y=\"90\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"114\" y=\"90\" width=\"12\" height=\"12\" fill=\"#1f6fd6\"/>"
            + "<rect x=\"60\" y=\"108\" width=\"12\" height=\"12\" fill=\"#16324f\"/>"
            + "<rect x=\"96\" y=\"108\" width=\"12\" height=\"12\" fill=\"#16324f\"/>"
            + "<rect x=\"78\" y=\"126\" width=\"12\" height=\"12\" fill=\"#16324f\"/>"
            + "<text x=\"80\" y=\"154\" text-anchor=\"middle\" font-size=\"12\" fill=\"#36506b\">"
            + safeText + "</text></svg>";
        return "data:image/svg+xml;base64," + Base64.getEncoder()
            .encodeToString(svg.getBytes(StandardCharsets.UTF_8));
    }

    private String escapeXml(String value)
    {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String encode(String value)
    {
        return URLEncoder.encode(firstNonBlank(value, ""), StandardCharsets.UTF_8);
    }

    private String firstNonBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value) && !value.isBlank())
            {
                return value;
            }
        }
        return "";
    }
}
