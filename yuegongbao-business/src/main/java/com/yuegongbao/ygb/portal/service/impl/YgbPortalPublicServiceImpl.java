package com.yuegongbao.ygb.portal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;
import com.yuegongbao.ygb.cockpit.mapper.YgbCockpitMapper;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.portal.service.IYgbPortalPublicService;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;

@Service
public class YgbPortalPublicServiceImpl implements IYgbPortalPublicService
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private YgbPortalContentMapper portalContentMapper;

    @Autowired
    private WorkerJobMapper workerJobMapper;

    @Autowired
    private YgbCockpitMapper cockpitMapper;

    @Override
    public Map<String, Object> getPortalHome(String portalCode)
    {
        String resolvedPortalCode = StringUtils.defaultIfBlank(portalCode, "ygb");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("portalCode", resolvedPortalCode);
        result.put("siteConfig", buildSiteConfig(resolvedPortalCode));
        result.put("intro", buildIntro(resolvedPortalCode));
        result.put("banners", buildBanners(resolvedPortalCode));
        result.put("stats", buildPublicStats());
        result.put("trend", buildPublicTrend());
        result.put("news", buildNews(resolvedPortalCode));
        result.put("policies", buildPolicies(resolvedPortalCode));
        result.put("solutions", buildSolutions(resolvedPortalCode));
        result.put("union", buildUnion(resolvedPortalCode));
        result.put("guides", buildGuides(resolvedPortalCode));
        result.put("about", buildAbout(resolvedPortalCode));
        result.put("partners", buildPartners(resolvedPortalCode));
        result.put("downloads", buildDownloads(resolvedPortalCode));
        result.put("quickEntries", buildQuickEntries(resolvedPortalCode));
        result.put("jobGuides", buildJobGuides(resolvedPortalCode));
        result.put("warmMap", buildSectionCards(resolvedPortalCode, "warm_map", 12));
        result.put("trainingCourses", buildSectionCards(resolvedPortalCode, "training_course", 12));
        result.put("lawLibrary", buildSectionCards(resolvedPortalCode, "law_library", 12));
        result.put("mutualHelp", buildSectionCards(resolvedPortalCode, "mutual_help", 12));
        result.put("recruitMarket", buildSectionCards(resolvedPortalCode, "recruit_market", 12));
        result.put("hotJobs", listPortalJobs(null, null, null, 3));
        result.put("jobs", listPortalJobs(null, null, null, 20));
        result.put("enterprises", buildEnterprises(resolvedPortalCode));
        return result;
    }

    @Override
    public List<Map<String, Object>> searchPortalContent(String portalCode, String keyword)
    {
        if (StringUtils.isBlank(keyword))
        {
            return Collections.emptyList();
        }
        List<YgbPortalContent> list = portalContentMapper.selectPublishedPortalContentList(
            StringUtils.defaultIfBlank(portalCode, "ygb"), null, null, keyword, 20);
        return list.stream().map(this::toSearchItem).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> listPortalJobs(String keyword, String location, String salaryText, Integer limit)
    {
        List<WorkerJobPost> jobs = workerJobMapper.selectJobList(keyword, null, null, null);
        int max = limit == null || limit <= 0 ? 20 : limit;
        List<Map<String, Object>> result = new ArrayList<>();
        for (WorkerJobPost job : jobs)
        {
            String salary = StringUtils.defaultIfBlank(job.getSalaryText(), formatSalary(job.getSalaryMin(), job.getSalaryMax()));
            if (StringUtils.isNotBlank(location) && !StringUtils.contains(job.getWorkAddress(), location))
            {
                continue;
            }
            if (StringUtils.isNotBlank(salaryText) && !StringUtils.equals(salary, salaryText))
            {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("jobId", job.getJobId());
            item.put("title", job.getTitle());
            item.put("company", job.getEnterpriseName());
            item.put("salary", salary);
            item.put("location", job.getWorkAddress());
            item.put("jobType", job.getJobType());
            result.add(item);
            if (result.size() >= max)
            {
                break;
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getPortalContentDetail(Long contentId)
    {
        YgbPortalContent content = portalContentMapper.selectPortalContentById(contentId);
        if (content == null || !"0".equals(content.getStatus()))
        {
            return Collections.emptyMap();
        }
        return toContentMap(content);
    }

    @Override
    public Map<String, Object> getPortalJobDetail(Long jobId)
    {
        WorkerJobPost job = workerJobMapper.selectJobDetail(jobId);
        if (job == null)
        {
            return Collections.emptyMap();
        }
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("jobId", job.getJobId());
        item.put("title", job.getTitle());
        item.put("company", job.getEnterpriseName());
        item.put("salary", StringUtils.defaultIfBlank(job.getSalaryText(), formatSalary(job.getSalaryMin(), job.getSalaryMax())));
        item.put("location", job.getWorkAddress());
        item.put("jobType", job.getJobType());
        item.put("recruitCount", job.getRecruitCount());
        item.put("contactName", job.getContactName());
        item.put("contactMobile", job.getContactMobile());
        item.put("description", job.getDescription());
        item.put("requirementText", job.getRequirementText());
        item.put("publishTime", job.getPublishTime());
        return item;
    }

    private Map<String, Object> buildSiteConfig(String portalCode)
    {
        Map<String, Object> config = new LinkedHashMap<>();
        List<YgbPortalContent> list = portalContentMapper.selectPublishedPortalContentList(portalCode, "site_config", null, null, null);
        for (YgbPortalContent item : list)
        {
            if ("brand".equals(item.getCategoryCode()))
            {
                config.put("title", item.getTitle());
                config.put("subtitle", item.getSummary());
                mergeExtra(config, item.getExtraJson());
            }
            else if ("contact".equals(item.getCategoryCode()))
            {
                config.put("contactTitle", item.getTitle());
                config.put("contactSummary", item.getSummary());
                mergeExtra(config, item.getExtraJson());
            }
        }
        return config;
    }

    private Map<String, Object> buildIntro(String portalCode)
    {
        YgbPortalContent intro = firstContent(portalCode, "intro", "main");
        Map<String, Object> map = new LinkedHashMap<>();
        if (intro == null)
        {
            return map;
        }
        map.put("title", intro.getTitle());
        map.put("summary", intro.getSummary());
        JSONObject extra = parseExtra(intro.getExtraJson());
        map.put("pillars", extra.getJSONArray("pillars"));
        return map;
    }

    private List<Map<String, Object>> buildBanners(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "banner", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("tag", extra.getString("tag"));
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                map.put("date", formatDate(item.getPublishTime()));
                map.put("meta", extra.getString("meta"));
                map.put("background", extra.getString("background"));
                return map;
            }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildPublicStats()
    {
        YgbCockpitSnapshot snapshot = cockpitMapper.selectLatestSnapshot("440000", LocalDate.now().format(DATE_FORMATTER));
        List<Map<String, Object>> stats = new ArrayList<>();
        if (snapshot != null)
        {
            stats.add(statCard("覆盖企业数", formatCount(snapshot.getDispatchCompanyCount()), "全省归集"));
            stats.add(statCard("参保人数", formatWorkerCount(snapshot.getDispatchedWorkerCount()), "脱敏展示"));
            stats.add(statCard("预警闭环率", formatRate(calcWarningCloseRate(snapshot)), "脱敏展示"));
            stats.add(statCard("扩面完成率", formatPercent(snapshot.getExpandCompletionRate()), "脱敏展示"));
        }
        if (stats.isEmpty())
        {
            stats.add(statCard("覆盖企业数", "3,820", "同比 +12.4%"));
            stats.add(statCard("参保人数", "28.6万", "扩面新增2.1万"));
            stats.add(statCard("预警闭环率", "96.8%", "同比 +4.2%"));
            stats.add(statCard("合同备案率", "94.5%", "全省均值"));
        }
        return stats;
    }

    private Map<String, Object> buildPublicTrend()
    {
        List<YgbCockpitSnapshot> trend = cockpitMapper.selectSnapshotTrend("440000",
            LocalDate.now().minusMonths(11).format(DATE_FORMATTER), LocalDate.now().format(DATE_FORMATTER));
        Map<String, Object> chart = new LinkedHashMap<>();
        List<String> months = new ArrayList<>();
        List<BigDecimal> expandRates = new ArrayList<>();
        List<BigDecimal> closeRates = new ArrayList<>();
        if (trend != null && !trend.isEmpty())
        {
            for (YgbCockpitSnapshot item : trend)
            {
                months.add(new SimpleDateFormat("M月").format(item.getStatDate()));
                expandRates.add(item.getExpandCompletionRate() == null ? BigDecimal.ZERO : item.getExpandCompletionRate());
                closeRates.add(calcWarningCloseRate(item));
            }
        }
        else
        {
            months.addAll(Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"));
            expandRates.addAll(Arrays.asList(
                bd(82), bd(84), bd(85), bd(87), bd(88), bd(89), bd(90), bd(91), bd(91.5), bd(92), bd(92.3), bd(92.6)));
            closeRates.addAll(Arrays.asList(
                bd(88), bd(89), bd(90), bd(91), bd(92), bd(93), bd(94), bd(94.5), bd(95), bd(95.5), bd(96.2), bd(96.8)));
        }
        chart.put("months", months);
        chart.put("expandRates", expandRates);
        chart.put("closeRates", closeRates);
        return chart;
    }

    private List<Map<String, Object>> buildNews(String portalCode)
    {
        Map<String, String> labelMap = new HashMap<>();
        labelMap.put("policy", "政策发布");
        labelMap.put("announcement", "平台公告");
        labelMap.put("industry", "行业新闻");
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "news", null, null, 10)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("category", StringUtils.defaultIfBlank(extra.getString("categoryLabel"), labelMap.get(item.getCategoryCode())));
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                map.put("date", formatDate(item.getPublishTime()));
                map.put("source", item.getSourceName());
                return map;
            }).collect(Collectors.toList());
    }

    private Map<String, List<Map<String, Object>>> buildPolicies(String portalCode)
    {
        Map<String, List<Map<String, Object>>> policies = new LinkedHashMap<>();
        for (String category : Arrays.asList("national", "guangdong", "interpretation"))
        {
            policies.put(category, portalContentMapper.selectPublishedPortalContentList(portalCode, "policy", category, null, null)
                .stream().map(item -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    JSONObject extra = parseExtra(item.getExtraJson());
                    map.put("contentId", item.getContentId());
                    map.put("type", extra.getString("typeLabel"));
                    map.put("title", item.getTitle());
                    map.put("summary", item.getSummary());
                    map.put("date", formatDate(item.getPublishTime()));
                    return map;
                }).collect(Collectors.toList()));
        }
        return policies;
    }

    private List<Map<String, Object>> buildSolutions(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "solution", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("icon", extra.getString("icon"));
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                map.put("features", extra.getJSONArray("features"));
                return map;
            }).collect(Collectors.toList());
    }

    private Map<String, Object> buildUnion(String portalCode)
    {
        Map<String, Object> union = new LinkedHashMap<>();
        YgbPortalContent intro = firstContent(portalCode, "union", "intro");
        if (intro != null)
        {
            union.put("introTitle", intro.getTitle());
            union.put("introSummary", intro.getSummary());
            mergeExtra(union, intro.getExtraJson());
        }
        union.put("guides", portalContentMapper.selectPublishedPortalContentList(portalCode, "union", null, null, null)
            .stream().filter(item -> !"intro".equals(item.getCategoryCode()))
            .map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("type", extra.getString("typeLabel"));
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                return map;
            }).collect(Collectors.toList()));
        return union;
    }

    private List<Map<String, Object>> buildGuides(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "guide", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("icon", extra.getString("icon"));
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                map.put("steps", extra.getJSONArray("steps"));
                return map;
            }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildAbout(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "about", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("contentId", item.getContentId());
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                return map;
            }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildPartners(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "partner", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("icon", extra.getString("icon"));
                map.put("name", item.getTitle());
                map.put("role", item.getSummary());
                return map;
            }).collect(Collectors.toList());
    }

    private Map<String, Object> buildDownloads(String portalCode)
    {
        Map<String, Object> downloads = new LinkedHashMap<>();
        downloads.put("miniPrograms", mapByCategory(portalCode, "download", "miniprogram", "codeLabel"));
        downloads.put("apps", mapByCategory(portalCode, "download", "app", "icon"));
        downloads.put("manuals", portalContentMapper.selectPublishedPortalContentList(portalCode, "download", "manual", null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                map.put("type", extra.getString("fileType"));
                map.put("linkUrl", item.getLinkUrl());
                return map;
            }).collect(Collectors.toList()));
        return downloads;
    }

    private List<Map<String, Object>> buildQuickEntries(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "quick_entry", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("icon", extra.getString("icon"));
                map.put("label", item.getTitle());
                map.put("desc", item.getSummary());
                map.put("entryType", extra.getString("entryType"));
                map.put("anchor", extra.getString("anchor"));
                map.put("linkUrl", item.getLinkUrl());
                return map;
            }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildJobGuides(String portalCode)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, "job_guide", null, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("contentId", item.getContentId());
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                return map;
            }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildSectionCards(String portalCode, String sectionCode, Integer limit)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, sectionCode, null, null, limit)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("contentId", item.getContentId());
                map.put("sectionCode", item.getSectionCode());
                map.put("categoryCode", item.getCategoryCode());
                map.put("title", item.getTitle());
                map.put("summary", item.getSummary());
                map.put("coverUrl", item.getCoverUrl());
                map.put("linkUrl", item.getLinkUrl());
                map.put("extra", parseExtra(item.getExtraJson()));
                return map;
            }).collect(Collectors.toList());
    }

    private List<String> buildEnterprises(String portalCode)
    {
        return listPortalJobs(null, null, null, 20).stream()
            .map(item -> String.valueOf(item.get("company")))
            .filter(StringUtils::isNotBlank)
            .distinct()
            .limit(8)
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> mapByCategory(String portalCode, String section, String category, String extraKey)
    {
        return portalContentMapper.selectPublishedPortalContentList(portalCode, section, category, null, null)
            .stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject extra = parseExtra(item.getExtraJson());
                map.put("contentId", item.getContentId());
                map.put("title", item.getTitle());
                map.put("desc", item.getSummary());
                map.put("code", extra.getString(extraKey));
                map.put("icon", extra.getString("icon"));
                map.put("coverUrl", item.getCoverUrl());
                map.put("linkUrl", item.getLinkUrl());
                return map;
            }).collect(Collectors.toList());
    }

    private YgbPortalContent firstContent(String portalCode, String section, String category)
    {
        List<YgbPortalContent> list = portalContentMapper.selectPublishedPortalContentList(portalCode, section, category, null, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    private Map<String, Object> toSearchItem(YgbPortalContent item)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("contentId", item.getContentId());
        map.put("sectionCode", item.getSectionCode());
        map.put("categoryCode", item.getCategoryCode());
        map.put("title", item.getTitle());
        map.put("summary", item.getSummary());
        return map;
    }

    private Map<String, Object> toContentMap(YgbPortalContent item)
    {
        Map<String, Object> map = toSearchItem(item);
        map.put("content", resolvePortalContentBody(item));
        map.put("source", item.getSourceName());
        map.put("publishTime", item.getPublishTime());
        map.put("coverUrl", item.getCoverUrl());
        map.put("linkUrl", item.getLinkUrl());
        map.put("extra", parseExtra(item.getExtraJson()));
        return map;
    }

    private String resolvePortalContentBody(YgbPortalContent item)
    {
        if (StringUtils.isNotBlank(item.getContent()))
        {
            return item.getContent();
        }
        if (StringUtils.isNotBlank(item.getSummary()))
        {
            return item.getSummary();
        }
        return item.getTitle();
    }

    private Map<String, Object> statCard(String label, String value, String trend)
    {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("label", label);
        card.put("value", value);
        card.put("trend", trend);
        return card;
    }

    private BigDecimal calcWarningCloseRate(YgbCockpitSnapshot snapshot)
    {
        if (snapshot == null || snapshot.getTodayWarningCount() == null || snapshot.getTodayWarningCount() == 0)
        {
            return bd(96.8);
        }
        int pending = snapshot.getPendingWarningCount() == null ? 0 : snapshot.getPendingWarningCount();
        int total = snapshot.getTodayWarningCount();
        return bd(100).subtract(bd(pending).multiply(bd(100)).divide(bd(total), 1, RoundingMode.HALF_UP));
    }

    private String formatCount(Integer value)
    {
        return value == null ? "--" : String.format("%,d", value);
    }

    private String formatWorkerCount(Integer value)
    {
        if (value == null)
        {
            return "--";
        }
        if (value >= 10000)
        {
            return String.format("%.1f万", value / 10000.0);
        }
        return String.format("%,d", value);
    }

    private String formatPercent(BigDecimal value)
    {
        return value == null ? "--" : value.setScale(1, RoundingMode.HALF_UP) + "%";
    }

    private String formatRate(BigDecimal value)
    {
        return formatPercent(value);
    }

    private String formatSalary(BigDecimal min, BigDecimal max)
    {
        if (StringUtils.isNotNull(min) && StringUtils.isNotNull(max))
        {
            return min.stripTrailingZeros().toPlainString() + "-" + max.stripTrailingZeros().toPlainString();
        }
        return "--";
    }

    private String formatDate(java.util.Date date)
    {
        return date == null ? "" : DISPLAY_DATE.format(date);
    }

    private JSONObject parseExtra(String extraJson)
    {
        if (StringUtils.isBlank(extraJson))
        {
            return new JSONObject();
        }
        try
        {
            return JSON.parseObject(extraJson);
        }
        catch (Exception ex)
        {
            return new JSONObject();
        }
    }

    private void mergeExtra(Map<String, Object> target, String extraJson)
    {
        JSONObject extra = parseExtra(extraJson);
        extra.forEach((key, value) -> target.put(key, value));
    }

    private BigDecimal bd(double value)
    {
        return BigDecimal.valueOf(value);
    }
}
