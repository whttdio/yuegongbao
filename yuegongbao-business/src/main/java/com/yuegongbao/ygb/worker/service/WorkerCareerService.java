package com.yuegongbao.ygb.worker.service;

import java.math.BigDecimal;
import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface WorkerCareerService
{
    Map<String, Object> getJobList(YgbPerson worker, Long userId, String keyword, String jobType, BigDecimal salaryMin,
        BigDecimal salaryMax, Double latitude, Double longitude, Double radiusKm);

    Map<String, Object> getNearbyJobMapConfig(YgbPerson worker, Double latitude, Double longitude);

    Map<String, Object> getNearbyJobList(YgbPerson worker, Long userId, String keyword, String jobType, BigDecimal salaryMin,
        BigDecimal salaryMax, Double latitude, Double longitude, Double radiusKm);

    Map<String, Object> getJobDetail(YgbPerson worker, Long userId, Long jobId);

    Map<String, Object> applyJob(YgbPerson worker, SysUser user, Long jobId);

    Map<String, Object> getJobApplyList(Long userId);
}
