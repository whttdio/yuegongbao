package com.yuegongbao.ygb.worker.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerJobApply;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerCareerService;
import com.yuegongbao.ygb.worker.support.WorkerResumeSupport;

@Service
public class WorkerCareerServiceImpl implements WorkerCareerService
{
    private static final double DEFAULT_CENTER_LATITUDE = 23.12911D;
    private static final double DEFAULT_CENTER_LONGITUDE = 113.264385D;
    private static final double DEFAULT_RADIUS_KM = 20D;
    private static final double MIN_RADIUS_KM = 1D;
    private static final double MAX_RADIUS_KM = 100D;

    @Autowired
    private WorkerJobMapper workerJobMapper;

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Override
    public Map<String, Object> getJobList(YgbPerson worker, Long userId, String keyword, String jobType, BigDecimal salaryMin,
        BigDecimal salaryMax, Double latitude, Double longitude, Double radiusKm)
    {
        List<WorkerJobPost> rows = workerJobMapper.selectJobList(keyword, jobType, salaryMin, salaryMax);
        Map<String, Object> center = resolveCenter(worker, latitude, longitude, rows);
        double centerLatitude = ((Number) center.get("latitude")).doubleValue();
        double centerLongitude = ((Number) center.get("longitude")).doubleValue();
        double radius = normalizeRadius(radiusKm);

        List<Map<String, Object>> jobs = new ArrayList<>();
        for (WorkerJobPost item : rows)
        {
            Map<String, Double> point = resolveJobPoint(item);
            double distanceKm = calculateDistanceKm(centerLatitude, centerLongitude, point.get("latitude"), point.get("longitude"));
            if (radiusKm != null && radiusKm > 0 && distanceKm > radius)
            {
                continue;
            }
            Map<String, Object> row = jobSummary(item, workerJobMapper.countUserApplied(item.getJobId(), userId) > 0,
                point.get("latitude"), point.get("longitude"));
            row.put("distanceKm", roundDistance(distanceKm));
            row.put("distanceText", buildDistanceText(distanceKm));
            jobs.add(row);
        }
        jobs.sort(Comparator.comparing(item -> new BigDecimal(String.valueOf(item.get("distanceKm")))));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("keyword", keyword);
        result.put("jobType", jobType);
        result.put("salaryMin", salaryMin);
        result.put("salaryMax", salaryMax);
        result.put("radiusKm", latitude != null && longitude != null ? radius : null);
        result.put("locationAvailable", latitude != null && longitude != null);
        result.put("rows", jobs);
        result.put("total", jobs.size());
        result.put("applies", buildApplyRows(workerJobMapper.selectJobApplyList(userId)));
        result.put("workerName", worker.getPersonName());
        result.put("jobTypeOptions", buildJobTypeOptions());
        result.put("salaryOptions", buildSalaryOptions());
        return result;
    }

    @Override
    public Map<String, Object> getNearbyJobMapConfig(YgbPerson worker, Double latitude, Double longitude)
    {
        List<WorkerJobPost> rows = workerJobMapper.selectJobList(null, null, null, null);
        Map<String, Object> center = resolveCenter(worker, latitude, longitude, rows);
        String centerSource = String.valueOf(center.get("source"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("centerLatitude", center.get("latitude"));
        result.put("centerLongitude", center.get("longitude"));
        result.put("defaultRadiusKm", DEFAULT_RADIUS_KM);
        result.put("radiusOptions", List.of(3, 5, 10, 20, 50));
        result.put("locationAvailable", latitude != null && longitude != null);
        result.put("sourceLabel", resolveCenterSourceLabel(centerSource));
        result.put("sourceDescription", resolveCenterSourceDescription(centerSource));
        result.put("locationTip", "如需更精准的附近岗位，请开启定位权限。");
        result.put("jobTypeOptions", buildJobTypeOptions());
        result.put("salaryOptions", buildSalaryOptions());
        return result;
    }

    @Override
    public Map<String, Object> getNearbyJobList(YgbPerson worker, Long userId, String keyword, String jobType, BigDecimal salaryMin,
        BigDecimal salaryMax, Double latitude, Double longitude, Double radiusKm)
    {
        List<WorkerJobPost> rows = workerJobMapper.selectJobList(keyword, jobType, salaryMin, salaryMax);
        Map<String, Object> center = resolveCenter(worker, latitude, longitude, rows);
        double centerLatitude = ((Number) center.get("latitude")).doubleValue();
        double centerLongitude = ((Number) center.get("longitude")).doubleValue();
        String centerSource = String.valueOf(center.get("source"));
        double radius = normalizeRadius(radiusKm);
        List<Map<String, Object>> jobs = new ArrayList<>();
        List<Map<String, Object>> markers = new ArrayList<>();
        int markerId = 1;
        for (WorkerJobPost item : rows)
        {
            Map<String, Double> point = resolveJobPoint(item);
            double distanceKm = calculateDistanceKm(centerLatitude, centerLongitude, point.get("latitude"), point.get("longitude"));
            if (distanceKm > radius)
            {
                continue;
            }
            boolean applied = workerJobMapper.countUserApplied(item.getJobId(), userId) > 0;
            Map<String, Object> row = jobSummary(item, applied, point.get("latitude"), point.get("longitude"));
            row.put("distanceKm", roundDistance(distanceKm));
            row.put("distanceText", buildDistanceText(distanceKm));
            row.put("markerId", markerId);
            jobs.add(row);

            Map<String, Object> marker = new LinkedHashMap<>();
            marker.put("id", markerId);
            marker.put("latitude", point.get("latitude"));
            marker.put("longitude", point.get("longitude"));
            marker.put("title", item.getTitle());
            marker.put("callout", buildMarkerCallout(item, distanceKm));
            markers.add(marker);
            markerId++;
        }
        jobs.sort(Comparator.comparing(item -> new BigDecimal(String.valueOf(item.get("distanceKm")))));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("keyword", keyword);
        result.put("jobType", jobType);
        result.put("salaryMin", salaryMin);
        result.put("salaryMax", salaryMax);
        result.put("centerLatitude", centerLatitude);
        result.put("centerLongitude", centerLongitude);
        result.put("radiusKm", radius);
        result.put("rows", jobs);
        result.put("markers", markers);
        result.put("total", jobs.size());
        result.put("locationAvailable", latitude != null && longitude != null);
        result.put("sourceLabel", "current-location".equals(centerSource) ? "当前位置附近岗位" : resolveCenterSourceLabel(centerSource));
        result.put("sourceDescription", resolveCenterSourceDescription(centerSource));
        result.put("jobTypeOptions", buildJobTypeOptions());
        result.put("salaryOptions", buildSalaryOptions());
        return result;
    }

    @Override
    public Map<String, Object> getJobDetail(YgbPerson worker, Long userId, Long jobId)
    {
        WorkerJobPost item = workerJobMapper.selectJobDetail(jobId);
        if (item == null)
        {
            throw new ServiceException("未找到岗位信息。");
        }
        Map<String, Double> point = resolveJobPoint(item);
        Map<String, Object> result = jobSummary(item, workerJobMapper.countUserApplied(jobId, userId) > 0, point.get("latitude"),
            point.get("longitude"));
        result.put("description", item.getDescription());
        result.put("requirementText", item.getRequirementText());
        result.put("contactName", item.getContactName());
        result.put("contactMobile", item.getContactMobile());
        result.put("publishTime", item.getPublishTime());
        result.put("workerName", worker.getPersonName());
        return result;
    }

    @Override
    public Map<String, Object> applyJob(YgbPerson worker, SysUser user, Long jobId)
    {
        WorkerJobPost job = workerJobMapper.selectJobDetail(jobId);
        if (job == null)
        {
            throw new ServiceException("岗位不存在或已下架。");
        }
        WorkerResume resume = workerProfileMapper.selectWorkerResume(user.getUserId());
        WorkerResumeSupport.assertResumeReadyForApply(resume);
        if (workerJobMapper.countUserApplied(jobId, user.getUserId()) > 0)
        {
            throw new ServiceException("您已投递过该岗位。");
        }

        WorkerJobApply apply = new WorkerJobApply();
        apply.setJobId(jobId);
        apply.setUserId(user.getUserId());
        apply.setPersonId(worker.getPersonId());
        apply.setPersonName(worker.getPersonName());
        apply.setMobile(firstNonBlank(worker.getMobile(), user.getPhonenumber()));
        apply.setStatus("0");
        apply.setCreateBy(user.getUserName());
        workerJobMapper.insertJobApply(apply);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("applyId", apply.getApplyId());
        result.put("jobId", jobId);
        result.put("status", apply.getStatus());
        result.put("statusText", applyStatusText(apply.getStatus()));
        return result;
    }

    @Override
    public Map<String, Object> getJobApplyList(Long userId)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        List<WorkerJobApply> applies = workerJobMapper.selectJobApplyList(userId);
        result.put("rows", buildApplyRows(applies));
        result.put("total", applies.size());
        return result;
    }

    private List<Map<String, Object>> buildApplyRows(List<WorkerJobApply> applies)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (WorkerJobApply item : applies)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("applyId", item.getApplyId());
            row.put("jobId", item.getJobId());
            row.put("jobTitle", item.getJobTitle());
            row.put("enterpriseName", item.getEnterpriseName());
            row.put("status", item.getStatus());
            row.put("statusText", applyStatusText(item.getStatus()));
            row.put("applyTime", item.getApplyTime());
            rows.add(row);
        }
        return rows;
    }

    private List<String> buildJobTypeOptions()
    {
        List<WorkerJobPost> rows = workerJobMapper.selectJobList(null, null, null, null);
        List<String> options = new ArrayList<>();
        for (WorkerJobPost item : rows)
        {
            if (StringUtils.isEmpty(item.getJobType()) || options.contains(item.getJobType()))
            {
                continue;
            }
            options.add(item.getJobType());
        }
        return options;
    }

    private List<Map<String, Object>> buildSalaryOptions()
    {
        List<Map<String, Object>> options = new ArrayList<>();
        options.add(salaryOption("不限薪资", null, null));
        options.add(salaryOption("3000元以下", null, BigDecimal.valueOf(3000L)));
        options.add(salaryOption("3000-5000元", BigDecimal.valueOf(3000L), BigDecimal.valueOf(5000L)));
        options.add(salaryOption("5000-8000元", BigDecimal.valueOf(5000L), BigDecimal.valueOf(8000L)));
        options.add(salaryOption("8000元以上", BigDecimal.valueOf(8000L), null));
        return options;
    }

    private Map<String, Object> salaryOption(String label, BigDecimal min, BigDecimal max)
    {
        Map<String, Object> option = new LinkedHashMap<>();
        option.put("label", label);
        option.put("salaryMin", min);
        option.put("salaryMax", max);
        return option;
    }

    private Map<String, Object> jobSummary(WorkerJobPost item, boolean applied, Double latitude, Double longitude)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("jobId", item.getJobId());
        row.put("title", item.getTitle());
        row.put("jobType", item.getJobType());
        row.put("enterpriseName", item.getEnterpriseName());
        row.put("workAddress", item.getWorkAddress());
        row.put("salaryMin", defaultAmount(item.getSalaryMin()));
        row.put("salaryMax", defaultAmount(item.getSalaryMax()));
        row.put("salaryText", firstNonBlank(item.getSalaryText(), buildSalaryText(item.getSalaryMin(), item.getSalaryMax())));
        row.put("recruitCount", item.getRecruitCount());
        row.put("applied", applied);
        row.put("latitude", latitude);
        row.put("longitude", longitude);
        return row;
    }

    private Map<String, Object> resolveCenter(YgbPerson worker, Double latitude, Double longitude, List<WorkerJobPost> rows)
    {
        Map<String, Object> center = new LinkedHashMap<>();
        if (latitude != null && longitude != null)
        {
            center.put("latitude", latitude);
            center.put("longitude", longitude);
            center.put("source", "current-location");
            return center;
        }
        WorkerJobPost centerJob = resolveCenterJob(worker, rows);
        if (centerJob != null)
        {
            Map<String, Double> point = realPoint(centerJob);
            center.put("latitude", point.get("latitude"));
            center.put("longitude", point.get("longitude"));
            center.put("source", "job-coordinate");
            return center;
        }
        center.put("latitude", DEFAULT_CENTER_LATITUDE);
        center.put("longitude", DEFAULT_CENTER_LONGITUDE);
        center.put("source", "default-center");
        return center;
    }

    private Map<String, Double> resolveJobPoint(WorkerJobPost item)
    {
        if (hasRealCoordinates(item))
        {
            return realPoint(item);
        }
        return inferPointByText(firstNonBlank(item.getWorkAddress(), item.getEnterpriseName(), item.getTitle(), String.valueOf(item.getJobId())));
    }

    private WorkerJobPost resolveCenterJob(YgbPerson worker, List<WorkerJobPost> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            return null;
        }
        Long enterpriseId = worker == null ? null : worker.getEnterpriseId();
        if (enterpriseId != null)
        {
            for (WorkerJobPost item : rows)
            {
                if (enterpriseId.equals(item.getEnterpriseId()) && hasRealCoordinates(item))
                {
                    return item;
                }
            }
        }
        for (WorkerJobPost item : rows)
        {
            if (hasRealCoordinates(item))
            {
                return item;
            }
        }
        return null;
    }

    private boolean hasRealCoordinates(WorkerJobPost item)
    {
        return item != null && item.getLatitude() != null && item.getLongitude() != null;
    }

    private Map<String, Double> realPoint(WorkerJobPost item)
    {
        Map<String, Double> point = new LinkedHashMap<>();
        point.put("latitude", roundCoordinate(item.getLatitude().doubleValue()));
        point.put("longitude", roundCoordinate(item.getLongitude().doubleValue()));
        return point;
    }

    private Map<String, Double> inferPointByText(String seed)
    {
        String text = StringUtils.isEmpty(seed) ? "ygb-worker-job" : seed;
        int hash = Math.abs(text.hashCode());
        double latitudeOffset = ((hash % 7000) / 10000D) - 0.35D;
        double longitudeOffset = (((hash / 7000) % 9000) / 10000D) - 0.45D;
        Map<String, Double> point = new LinkedHashMap<>();
        point.put("latitude", roundCoordinate(DEFAULT_CENTER_LATITUDE + latitudeOffset));
        point.put("longitude", roundCoordinate(DEFAULT_CENTER_LONGITUDE + longitudeOffset));
        return point;
    }

    private String resolveCenterSourceLabel(String centerSource)
    {
        if ("current-location".equals(centerSource))
        {
            return "当前位置";
        }
        if ("job-coordinate".equals(centerSource))
        {
            return "岗位推荐中心";
        }
        return "默认推荐中心";
    }

    private String resolveCenterSourceDescription(String centerSource)
    {
        if ("current-location".equals(centerSource))
        {
            return "已按您的当前位置展示附近岗位。";
        }
        if ("job-coordinate".equals(centerSource))
        {
            return "未获取到定位，当前按岗位真实坐标推荐中心展示附近岗位。";
        }
        return "未获取到定位，且岗位未维护真实坐标，当前按默认推荐中心展示附近岗位。";
    }

    private double roundCoordinate(double value)
    {
        return new BigDecimal(String.valueOf(value)).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }

    private double normalizeRadius(Double radiusKm)
    {
        if (radiusKm == null)
        {
            return DEFAULT_RADIUS_KM;
        }
        return Math.max(MIN_RADIUS_KM, Math.min(MAX_RADIUS_KM, radiusKm));
    }

    private double calculateDistanceKm(double lat1, double lng1, double lat2, double lng2)
    {
        double earthRadiusKm = 6371D;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private BigDecimal roundDistance(double distanceKm)
    {
        return new BigDecimal(String.valueOf(distanceKm)).setScale(1, RoundingMode.HALF_UP);
    }

    private String buildDistanceText(double distanceKm)
    {
        BigDecimal rounded = roundDistance(distanceKm);
        if (rounded.compareTo(BigDecimal.ONE) < 0)
        {
            return rounded.multiply(BigDecimal.valueOf(1000)).setScale(0, RoundingMode.HALF_UP).toPlainString() + " 米";
        }
        return rounded.toPlainString() + " 公里";
    }

    private Map<String, Object> buildMarkerCallout(WorkerJobPost item, double distanceKm)
    {
        Map<String, Object> callout = new LinkedHashMap<>();
        callout.put("content", item.getTitle() + "\n" + buildDistanceText(distanceKm));
        callout.put("display", "ALWAYS");
        callout.put("padding", 8);
        callout.put("borderRadius", 8);
        callout.put("bgColor", "#ffffff");
        callout.put("color", "#16324f");
        callout.put("fontSize", 12);
        return callout;
    }

    private String buildSalaryText(BigDecimal min, BigDecimal max)
    {
        if (min == null && max == null)
        {
            return "面议";
        }
        if (min != null && max != null)
        {
            return min.stripTrailingZeros().toPlainString() + "-" + max.stripTrailingZeros().toPlainString() + " 元/月";
        }
        BigDecimal amount = min != null ? min : max;
        return amount.stripTrailingZeros().toPlainString() + " 元/月";
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String applyStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "已查看";
        }
        if ("2".equals(status))
        {
            return "已邀约";
        }
        if ("3".equals(status))
        {
            return "不合适";
        }
        return "已投递";
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
