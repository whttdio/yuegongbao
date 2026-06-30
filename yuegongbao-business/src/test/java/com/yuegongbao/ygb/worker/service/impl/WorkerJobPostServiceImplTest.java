package com.yuegongbao.ygb.worker.service.impl;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerJobPostServiceImplTest
{
    @Mock
    private WorkerJobMapper workerJobMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @Mock
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @InjectMocks
    private WorkerJobPostServiceImpl service;

    @Test
    void insertJobPostBackfillsEnterpriseNameAndDefaultsDraftStatus()
    {
        WorkerJobPost job = validJob();
        YgbEnterprise enterprise = enterprise(1001L, "ENT-1001");
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise);

        service.insertJobPost(job);

        verify(dataScopeGuard).assertEntityAllowed(enterprise);
        verify(dataScopeGuard).assertEntityAllowed(job);

        ArgumentCaptor<WorkerJobPost> captor = ArgumentCaptor.forClass(WorkerJobPost.class);
        verify(workerJobMapper).insertJobPost(captor.capture());
        assertEquals("ENT-1001", captor.getValue().getEnterpriseName());
        assertEquals("1", captor.getValue().getStatus());
    }

    @Test
    void insertJobPostRejectsInvalidSalaryRange()
    {
        WorkerJobPost job = validJob();
        job.setSalaryMin(new BigDecimal("9000"));
        job.setSalaryMax(new BigDecimal("6000"));

        assertThrows(ServiceException.class, () -> service.insertJobPost(job));
    }

    @Test
    void reviewJobPostPublishesApprovedJob()
    {
        WorkerJobPost existing = validJob();
        existing.setJobId(8L);
        existing.setStatus("1");
        when(workerJobMapper.selectAdminJobPostById(8L)).thenReturn(existing);

        service.reviewJobPost(8L, "0", "审核通过", "reviewer");

        ArgumentCaptor<WorkerJobPost> captor = ArgumentCaptor.forClass(WorkerJobPost.class);
        verify(workerJobMapper).updateJobReview(captor.capture());
        assertEquals(Long.valueOf(8L), captor.getValue().getJobId());
        assertEquals("0", captor.getValue().getStatus());
        assertEquals("审核通过", captor.getValue().getRemark());
        assertEquals("reviewer", captor.getValue().getUpdateBy());
    }

    @Test
    void reviewJobPostRejectsUnsupportedStatus()
    {
        WorkerJobPost existing = validJob();
        existing.setJobId(9L);
        when(workerJobMapper.selectAdminJobPostById(9L)).thenReturn(existing);

        assertThrows(ServiceException.class, () -> service.reviewJobPost(9L, "2", "invalid", "reviewer"));
    }

    private WorkerJobPost validJob()
    {
        WorkerJobPost job = new WorkerJobPost();
        job.setEnterpriseId(1001L);
        job.setTitle("Welder");
        job.setJobType("skilled");
        job.setRecruitCount(2);
        job.setSalaryMin(new BigDecimal("6000"));
        job.setSalaryMax(new BigDecimal("9000"));
        return job;
    }

    private YgbEnterprise enterprise(Long enterpriseId, String enterpriseName)
    {
        YgbEnterprise enterprise = new YgbEnterprise();
        enterprise.setEnterpriseId(enterpriseId);
        enterprise.setEnterpriseName(enterpriseName);
        enterprise.setRegionCode("440100");
        return enterprise;
    }
}
