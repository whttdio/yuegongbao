package com.yuegongbao.ygb.worker.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerCareerServiceImplTest
{
    @Mock
    private WorkerJobMapper workerJobMapper;

    @Mock
    private WorkerProfileMapper workerProfileMapper;

    @InjectMocks
    private WorkerCareerServiceImpl service;

    @Test
    void getNearbyJobListUsesRealCoordinatesWhenPresent()
    {
        WorkerJobPost job = jobPost(1001L, 23.205280D, 113.129030D);
        when(workerJobMapper.selectJobList(any(), any(), any(), any())).thenReturn(List.of(job));
        when(workerJobMapper.countUserApplied(eq(1001L), eq(1001L))).thenReturn(0L);

        Map<String, Object> result = service.getNearbyJobList(worker(), 1001L, null, null, null, null,
            23.205280D, 113.129030D, 5D);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals(1, rows.size());
        assertEquals(23.205280D, rows.get(0).get("latitude"));
        assertEquals(113.129030D, rows.get(0).get("longitude"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> markers = (List<Map<String, Object>>) result.get("markers");
        assertEquals(1, markers.size());
        assertEquals(23.205280D, markers.get(0).get("latitude"));
        assertEquals(113.129030D, markers.get(0).get("longitude"));
    }

    @Test
    void getNearbyJobMapConfigUsesEnterpriseJobCoordinatesWhenLocationMissing()
    {
        WorkerJobPost job = jobPost(1001L, 23.205280D, 113.129030D);
        when(workerJobMapper.selectJobList(any(), any(), any(), any())).thenReturn(List.of(job));

        Map<String, Object> result = service.getNearbyJobMapConfig(worker(), null, null);

        assertEquals(23.205280D, result.get("centerLatitude"));
        assertEquals(113.129030D, result.get("centerLongitude"));
        assertEquals("岗位推荐中心", result.get("sourceLabel"));
        assertTrue(String.valueOf(result.get("sourceDescription")).contains("真实坐标"));
    }

    @Test
    void getNearbyJobMapConfigFallsBackToDefaultCenterWhenNoRealCoordinates()
    {
        WorkerJobPost job = jobPost(1001L, null, null);
        when(workerJobMapper.selectJobList(any(), any(), any(), any())).thenReturn(List.of(job));

        Map<String, Object> result = service.getNearbyJobMapConfig(worker(), null, null);

        assertEquals(23.12911D, result.get("centerLatitude"));
        assertEquals(113.264385D, result.get("centerLongitude"));
        assertEquals("默认推荐中心", result.get("sourceLabel"));
    }

    private WorkerJobPost jobPost(Long enterpriseId, Double latitude, Double longitude)
    {
        WorkerJobPost job = new WorkerJobPost();
        job.setJobId(1001L);
        job.setEnterpriseId(enterpriseId);
        job.setEnterpriseName("广东安联劳务有限公司");
        job.setTitle("焊工");
        job.setJobType("焊接作业");
        job.setWorkAddress("佛山市南海区里水镇");
        job.setSalaryMin(new BigDecimal("7000"));
        job.setSalaryMax(new BigDecimal("9000"));
        job.setSalaryText("7000-9000 元/月");
        job.setRecruitCount(5);
        if (latitude != null)
        {
            job.setLatitude(BigDecimal.valueOf(latitude));
        }
        if (longitude != null)
        {
            job.setLongitude(BigDecimal.valueOf(longitude));
        }
        return job;
    }

    private YgbPerson worker()
    {
        YgbPerson worker = new YgbPerson();
        worker.setPersonId(2001L);
        worker.setEnterpriseId(1001L);
        worker.setEnterpriseName("广东安联劳务有限公司");
        worker.setPersonName("张三");
        return worker;
    }
}
