package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.core.redis.RedisCache;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingQuestion;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@Service
public class WorkerTrainingServiceImpl implements WorkerTrainingService
{
    private static final String PORTAL_CODE = "ygb";

    private static final String SECTION_TRAINING_QUIZ = "worker_training_quiz";

    private static final String SECTION_TRAINING_COURSE = "worker_training_course";

    private static final int TOTAL_QUESTIONS = 10;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private YgbPortalContentMapper portalContentMapper;

    @Override
    public WorkerTrainingProgress getProgress(YgbPerson worker)
    {
        Map<String, Integer> answered = getAnsweredMap(worker.getPersonId(), currentMonth());
        int completed = answered == null ? 0 : answered.size();
        int total = resolveQuestionBank().size();
        if (total <= 0)
        {
            total = TOTAL_QUESTIONS;
        }
        WorkerTrainingProgress progress = new WorkerTrainingProgress();
        progress.setCompleted(completed);
        progress.setTotal(total);
        progress.setNeedComplete(completed < total);
        progress.setLockAttendance(completed < total);
        progress.setLockSalary(completed < total);
        return progress;
    }

    @Override
    public List<WorkerTrainingQuestion> listQuestions(YgbPerson worker)
    {
        Map<String, Integer> answered = getAnsweredMap(worker.getPersonId(), currentMonth());
        List<WorkerTrainingQuestion> questions = new ArrayList<>();
        for (Map<String, Object> item : resolveQuestionBank())
        {
            WorkerTrainingQuestion question = new WorkerTrainingQuestion();
            question.setQuestionId((String) item.get("id"));
            question.setTitle((String) item.get("title"));
            question.setOptions((List<String>) item.get("options"));
            question.setCompleted(answered != null && answered.containsKey(question.getQuestionId()));
            questions.add(question);
        }
        return questions;
    }

    @Override
    public Map<String, Object> answerQuestion(YgbPerson worker, String questionId, Integer answerIndex)
    {
        Map<String, Object> question = findQuestion(questionId);
        if (question == null)
        {
            throw new ServiceException("题目不存在。");
        }
        boolean correct = question.get("answer").equals(answerIndex);
        if (correct)
        {
            String key = buildTrainingKey(worker.getPersonId(), currentMonth());
            redisCache.setCacheMapValue(key, questionId, answerIndex);
            redisCache.expire(key, 40, TimeUnit.DAYS);
        }

        WorkerTrainingProgress progress = getProgress(worker);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("correct", correct);
        result.put("newScore", progress.getCompleted());
        result.put("progress", progress);
        return result;
    }

    @Override
    public Map<String, Object> getHistory(YgbPerson worker)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        LocalDate now = LocalDate.now();
        rows.add(historyRow(worker, now.minusMonths(2), 10, 10));
        rows.add(historyRow(worker, now.minusMonths(1), 10, 10));
        WorkerTrainingProgress current = getProgress(worker);
        rows.add(historyRow(worker, now, current.getCompleted(), current.getTotal()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getCourses(YgbPerson worker)
    {
        List<Map<String, Object>> cmsRows = buildCmsCourseRows(worker);
        if (!cmsRows.isEmpty())
        {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("rows", cmsRows);
            result.put("total", cmsRows.size());
            return result;
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(courseSummary(worker, "course-1", "入场安全基础课", 420, "围绕入场、劳保用品和现场纪律的基础培训。"));
        rows.add(courseSummary(worker, "course-2", "高处作业风险提示", 560, "重点提示安全带、临边防护和监护要求。"));
        rows.add(courseSummary(worker, "course-3", "工伤报案与留痕", 360, "发生工伤后，如何固定证据并启动报案流程。"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getCourseDetail(YgbPerson worker, String courseKey)
    {
        Map<String, Object> cmsDetail = buildCmsCourseDetail(worker, courseKey);
        if (cmsDetail != null)
        {
            return cmsDetail;
        }

        Map<String, Object> detail = switch (courseKey)
        {
            case "course-1" -> courseDetail(worker, "course-1", "入场安全基础课", 420,
                "围绕入场、劳保用品和现场纪律的基础培训。", Arrays.asList("入场前核验身份与工种。", "按工种佩戴安全帽、反光衣和劳保用品。", "现场发现异常立即留痕并上报。"));
            case "course-2" -> courseDetail(worker, "course-2", "高处作业风险提示", 560,
                "重点提示安全带、临边防护和监护要求。", Arrays.asList("作业前先检查安全带与挂点。", "临边、洞口区域必须设置围栏。", "高处作业应有现场监护。"));
            case "course-3" -> courseDetail(worker, "course-3", "工伤报案与留痕", 360,
                "发生工伤后，如何固定证据并启动报案流程。", Arrays.asList("第一时间就医并保留病历。", "补齐现场照片、考勤和证人信息。", "通过平台发起法律咨询或投诉。"));
            default -> null;
        };
        if (detail == null)
        {
            throw new ServiceException("未找到培训课程。");
        }
        return detail;
    }

    @Override
    public Map<String, Object> saveStudyProgress(YgbPerson worker, String courseKey, Integer studiedSeconds)
    {
        if (courseKey == null || courseKey.isBlank())
        {
            throw new ServiceException("课程标识不能为空。");
        }
        int totalSeconds = courseTotalSeconds(courseKey);
        int seconds = studiedSeconds == null ? 0 : Math.max(0, studiedSeconds);
        redisCache.setCacheMapValue(buildCourseProgressKey(worker.getPersonId()), courseKey, seconds);
        redisCache.expire(buildCourseProgressKey(worker.getPersonId()), 60, TimeUnit.DAYS);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("courseKey", courseKey);
        result.put("studiedSeconds", Math.min(seconds, totalSeconds));
        result.put("totalSeconds", totalSeconds);
        result.put("completed", seconds >= totalSeconds);
        return result;
    }

    private Map<String, Integer> getAnsweredMap(Long personId, String month)
    {
        return redisCache.getCacheMap(buildTrainingKey(personId, month));
    }

    private Integer getCourseProgress(Long personId, String courseKey)
    {
        Integer value = redisCache.getCacheMapValue(buildCourseProgressKey(personId), courseKey);
        return value == null ? 0 : value;
    }

    private String buildTrainingKey(Long personId, String month)
    {
        return "worker:training:" + personId + ":" + month;
    }

    private String buildCourseProgressKey(Long personId)
    {
        return "worker:training:course:" + personId;
    }

    private String currentMonth()
    {
        return LocalDate.now().format(MONTH_FORMATTER);
    }

    private Map<String, Object> findQuestion(String questionId)
    {
        for (Map<String, Object> item : resolveQuestionBank())
        {
            if (questionId.equals(item.get("id")))
            {
                return item;
            }
        }
        return null;
    }

    private List<Map<String, Object>> resolveQuestionBank()
    {
        List<Map<String, Object>> cmsQuestions = buildCmsQuestionBank();
        if (!cmsQuestions.isEmpty())
        {
            return cmsQuestions;
        }
        return defaultQuestionBank();
    }

    private List<Map<String, Object>> defaultQuestionBank()
    {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(question("Q01", "高温天气室外作业前，首先应该确认什么？", 1,
            "是否穿了新工服", "防暑用品和补水条件是否到位", "当天工资是否到账", "手机电量是否充足"));
        list.add(question("Q02", "发现安全隐患后，正确做法是什么？", 2,
            "继续作业，稍后再说", "拍照发朋友圈", "立即上报并做好现场留痕", "只口头提醒同事"));
        list.add(question("Q03", "进入施工现场时，最基本的个人防护要求是？", 0,
            "按规定佩戴安全防护用品", "只要带工牌即可", "穿便装也可以", "由同事代替佩戴"));
        list.add(question("Q04", "工伤预防培训的直接目的是什么？", 3,
            "增加考勤天数", "方便拍照打卡", "提高工资标准", "降低事故风险和违规操作"));
        list.add(question("Q05", "发现考勤记录缺失时，应优先做什么？", 2,
            "删除当天记录", "直接投诉企业", "及时补录并说明原因", "等月底统一处理"));
        list.add(question("Q06", "工资条中出现异常扣款，第一步应如何处理？", 1,
            "自行修改金额", "先核对工资条明细并保留证据", "忽略不管", "更换银行卡"));
        list.add(question("Q07", "社保缴费记录异常时，劳动者可以通过什么方式留痕？", 0,
            "截图保存并通过平台反馈", "删除历史记录", "停用账号", "更换手机号"));
        list.add(question("Q08", "工伤事故发生后，最重要的事项之一是什么？", 2,
            "先删除现场照片", "只通知同事", "及时固定证据并按流程上报", "等企业统一处理"));
        list.add(question("Q09", "打卡时启用定位的主要目的是什么？", 1,
            "增加流量消耗", "确认作业地点与考勤真实性", "提升手机性能", "生成更多通知"));
        list.add(question("Q10", "法律咨询模块最适合处理哪类问题？", 3,
            "外卖点单", "天气查询", "娱乐活动报名", "劳动争议、欠薪、社保等维权问题"));
        return list;
    }

    private List<Map<String, Object>> buildCmsQuestionBank()
    {
        List<YgbPortalContent> list = selectPublishedContent(SECTION_TRAINING_QUIZ, 20);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbPortalContent item : list)
        {
            JSONObject extra = parseExtraJson(item.getExtraJson());
            String questionId = firstNonBlank(jsonString(extra, "questionId"), item.getCategoryCode(),
                item.getContentId() == null ? null : "cms-question-" + item.getContentId());
            String title = firstNonBlank(item.getTitle(), jsonString(extra, "title"), item.getSummary());
            List<String> options = stringList(extra, "options");
            Integer answerIndex = jsonInteger(extra, "answerIndex");
            if (StringUtils.isEmpty(questionId) || StringUtils.isEmpty(title) || options.size() < 2 || answerIndex == null
                || answerIndex < 0 || answerIndex >= options.size())
            {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", questionId);
            row.put("title", title);
            row.put("answer", answerIndex);
            row.put("options", options);
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> question(String id, String title, Integer answer, String... options)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("title", title);
        item.put("answer", answer);
        item.put("options", List.of(options));
        return item;
    }

    private Map<String, Object> historyRow(YgbPerson worker, LocalDate month, int completed, int total)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("month", month.format(MONTH_FORMATTER));
        row.put("completed", completed);
        row.put("total", total);
        row.put("passed", completed >= total);
        row.put("workerName", worker.getPersonName());
        return row;
    }

    private Map<String, Object> courseSummary(YgbPerson worker, String courseKey, String title, int totalSeconds, String summary)
    {
        int studiedSeconds = getCourseProgress(worker.getPersonId(), courseKey);
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("courseKey", courseKey);
        row.put("title", title);
        row.put("summary", summary);
        row.put("totalSeconds", totalSeconds);
        row.put("durationText", formatDuration(totalSeconds));
        row.put("studiedSeconds", Math.min(studiedSeconds, totalSeconds));
        row.put("completed", studiedSeconds >= totalSeconds);
        return row;
    }

    private Map<String, Object> courseDetail(YgbPerson worker, String courseKey, String title, int totalSeconds,
        String summary, List<String> outlines)
    {
        int studiedSeconds = getCourseProgress(worker.getPersonId(), courseKey);
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("courseKey", courseKey);
        row.put("title", title);
        row.put("summary", summary);
        row.put("totalSeconds", totalSeconds);
        row.put("durationText", formatDuration(totalSeconds));
        row.put("studiedSeconds", Math.min(studiedSeconds, totalSeconds));
        row.put("completed", studiedSeconds >= totalSeconds);
        row.put("outlineList", outlines);
        return row;
    }

    private List<Map<String, Object>> buildCmsCourseRows(YgbPerson worker)
    {
        List<YgbPortalContent> list = selectPublishedContent(SECTION_TRAINING_COURSE, 20);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbPortalContent item : list)
        {
            JSONObject extra = parseExtraJson(item.getExtraJson());
            String courseKey = resolveCourseKey(item, extra);
            if (StringUtils.isEmpty(courseKey))
            {
                continue;
            }
            int totalSeconds = resolveCourseTotalSeconds(extra);
            int studiedSeconds = getCourseProgress(worker.getPersonId(), courseKey);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("courseKey", courseKey);
            row.put("title", firstNonBlank(item.getTitle(), "培训课程"));
            row.put("summary", firstNonBlank(item.getSummary(), item.getContent(), "请按课程提示完成学习。"));
            row.put("totalSeconds", totalSeconds);
            row.put("durationText", formatDuration(totalSeconds));
            row.put("studiedSeconds", Math.min(studiedSeconds, totalSeconds));
            row.put("completed", studiedSeconds >= totalSeconds);
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> buildCmsCourseDetail(YgbPerson worker, String courseKey)
    {
        if (StringUtils.isEmpty(courseKey))
        {
            return null;
        }
        for (YgbPortalContent item : selectPublishedContent(SECTION_TRAINING_COURSE, 20))
        {
            JSONObject extra = parseExtraJson(item.getExtraJson());
            if (!courseKey.equals(resolveCourseKey(item, extra)))
            {
                continue;
            }
            int totalSeconds = resolveCourseTotalSeconds(extra);
            int studiedSeconds = getCourseProgress(worker.getPersonId(), courseKey);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("courseKey", courseKey);
            row.put("title", firstNonBlank(item.getTitle(), "培训课程"));
            row.put("summary", firstNonBlank(item.getSummary(), item.getContent(), "请按课程提示完成学习。"));
            row.put("totalSeconds", totalSeconds);
            row.put("durationText", formatDuration(totalSeconds));
            row.put("studiedSeconds", Math.min(studiedSeconds, totalSeconds));
            row.put("completed", studiedSeconds >= totalSeconds);
            row.put("outlineList", outlineList(item, extra));
            return row;
        }
        return null;
    }

    private int courseTotalSeconds(String courseKey)
    {
        return switch (courseKey)
        {
            case "course-1" -> 420;
            case "course-2" -> 560;
            case "course-3" -> 360;
            default -> 300;
        };
    }

    private String formatDuration(int totalSeconds)
    {
        int minute = totalSeconds / 60;
        int second = totalSeconds % 60;
        return String.format("%02d:%02d", minute, second);
    }

    private List<YgbPortalContent> selectPublishedContent(String sectionCode, Integer limit)
    {
        List<YgbPortalContent> list = portalContentMapper.selectPublishedPortalContentList(
            PORTAL_CODE, sectionCode, null, null, limit);
        return list == null ? Collections.emptyList() : list;
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

    private String resolveCourseKey(YgbPortalContent item, JSONObject extra)
    {
        return firstNonBlank(jsonString(extra, "courseKey"), item == null ? null : item.getCategoryCode(),
            item == null || item.getContentId() == null ? null : "cms-course-" + item.getContentId());
    }

    private int resolveCourseTotalSeconds(JSONObject extra)
    {
        Integer durationSeconds = jsonInteger(extra, "durationSeconds");
        return durationSeconds != null && durationSeconds > 0 ? durationSeconds : 300;
    }

    private List<String> outlineList(YgbPortalContent item, JSONObject extra)
    {
        List<String> outlines = stringList(extra, "outlineList");
        if (!outlines.isEmpty())
        {
            return outlines;
        }
        return splitContentLines(firstNonBlank(item == null ? null : item.getContent(), item == null ? null : item.getSummary(), ""));
    }

    private List<String> splitContentLines(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return new ArrayList<>();
        }
        String cleaned = content
            .replaceAll("(?i)<br\\s*/?>", "\n")
            .replaceAll("(?i)</p>", "\n")
            .replaceAll("<[^>]+>", " ")
            .replace("&nbsp;", " ")
            .replace("\r", "\n")
            .trim();
        List<String> rows = new ArrayList<>();
        for (String block : cleaned.split("\\n+"))
        {
            String line = block.replaceAll("\\s+", " ").trim();
            if (StringUtils.isEmpty(line))
            {
                continue;
            }
            rows.add(line);
        }
        return rows;
    }

    private List<String> stringList(JSONObject extra, String key)
    {
        if (extra == null || StringUtils.isEmpty(key))
        {
            return new ArrayList<>();
        }
        JSONArray array = extra.getJSONArray(key);
        if (array == null || array.isEmpty())
        {
            return new ArrayList<>();
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
        return rows;
    }

    private Integer jsonInteger(JSONObject extra, String key)
    {
        if (extra == null || StringUtils.isEmpty(key) || !extra.containsKey(key))
        {
            return null;
        }
        Object value = extra.get(key);
        if (value == null)
        {
            return null;
        }
        try
        {
            return Integer.valueOf(String.valueOf(value));
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private String jsonString(JSONObject extra, String key)
    {
        if (extra == null || StringUtils.isEmpty(key) || !extra.containsKey(key))
        {
            return null;
        }
        Object value = extra.get(key);
        if (value == null)
        {
            return null;
        }
        String text = String.valueOf(value).trim();
        return StringUtils.isEmpty(text) ? null : text;
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
