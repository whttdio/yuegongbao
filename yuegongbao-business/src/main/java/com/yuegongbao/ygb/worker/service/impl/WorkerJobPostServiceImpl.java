package com.yuegongbao.ygb.worker.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import com.yuegongbao.ygb.worker.service.IWorkerJobPostService;

@Service
public class WorkerJobPostServiceImpl implements IWorkerJobPostService
{
    @Autowired
    private WorkerJobMapper workerJobMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Override
    public WorkerJobPost selectJobPostById(Long jobId)
    {
        WorkerJobPost job = workerJobMapper.selectAdminJobPostById(jobId);
        if (job != null)
        {
            dataScopeGuard.assertEntityAllowed(job);
        }
        return job;
    }

    @Override
    public List<WorkerJobPost> selectJobPostList(WorkerJobPost query)
    {
        applyScopedEnterprise(query);
        return workerJobMapper.selectAdminJobPostList(query);
    }

    @Override
    public int insertJobPost(WorkerJobPost job)
    {
        normalizeJobPost(job);
        bindAndValidateEnterprise(job);
        dataScopeGuard.assertEntityAllowed(job);
        return workerJobMapper.insertJobPost(job);
    }

    @Override
    public int updateJobPost(WorkerJobPost job)
    {
        requireJobPostAllowed(job.getJobId());
        normalizeJobPost(job);
        bindAndValidateEnterprise(job);
        dataScopeGuard.assertEntityAllowed(job);
        return workerJobMapper.updateJobPost(job);
    }

    @Override
    public int reviewJobPost(Long jobId, String status, String opinion, String operator)
    {
        WorkerJobPost job = requireJobPostAllowed(jobId);
        if (!"0".equals(status) && !"1".equals(status))
        {
            throw new ServiceException("审核状态只能为通过或驳回。");
        }
        WorkerJobPost update = new WorkerJobPost();
        update.setJobId(job.getJobId());
        update.setStatus(status);
        update.setRemark(StringUtils.defaultIfEmpty(opinion, "岗位审核处理"));
        update.setUpdateBy(operator);
        if ("0".equals(status))
        {
            update.setPublishTime(job.getPublishTime() == null ? new Date() : job.getPublishTime());
        }
        return workerJobMapper.updateJobReview(update);
    }

    @Override
    public int deleteJobPostByIds(Long[] jobIds, String updateBy)
    {
        for (Long jobId : jobIds)
        {
            requireJobPostAllowed(jobId);
        }
        return workerJobMapper.deleteJobPostByIds(jobIds, updateBy);
    }

    private WorkerJobPost requireJobPostAllowed(Long jobId)
    {
        if (jobId == null)
        {
            throw new ServiceException("岗位ID不能为空。");
        }
        WorkerJobPost job = workerJobMapper.selectAdminJobPostById(jobId);
        if (job == null)
        {
            throw new ServiceException("岗位不存在。");
        }
        dataScopeGuard.assertEntityAllowed(job);
        return job;
    }

    private void normalizeJobPost(WorkerJobPost job)
    {
        if (job == null)
        {
            throw new ServiceException("job post cannot be empty.");
        }
        if (StringUtils.isEmpty(job.getTitle()))
        {
            throw new ServiceException("job title cannot be empty.");
        }
        if (StringUtils.isEmpty(job.getJobType()))
        {
            throw new ServiceException("job type cannot be empty.");
        }
        if (job.getRecruitCount() == null || job.getRecruitCount() <= 0)
        {
            throw new ServiceException("recruit count must be greater than 0.");
        }
        if (job.getSalaryMin() != null && job.getSalaryMax() != null
            && job.getSalaryMin().compareTo(job.getSalaryMax()) > 0)
        {
            throw new ServiceException("salary min cannot exceed salary max.");
        }
        if (StringUtils.isEmpty(job.getStatus()))
        {
            job.setStatus("1");
        }
        if (!"0".equals(job.getStatus()) && !"1".equals(job.getStatus()))
        {
            throw new ServiceException("job status must be draft or published.");
        }
    }

    private void bindAndValidateEnterprise(WorkerJobPost job)
    {
        applyScopedEnterprise(job);
        if (job.getEnterpriseId() == null)
        {
            throw new ServiceException("enterprise id cannot be empty.");
        }
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(job.getEnterpriseId());
        if (enterprise == null)
        {
            throw new ServiceException("enterprise does not exist.");
        }
        dataScopeGuard.assertEntityAllowed(enterprise);
        job.setEnterpriseName(enterprise.getEnterpriseName());
    }

    private void applyScopedEnterprise(WorkerJobPost job)
    {
        if (job == null || !enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            return;
        }
        Long enterpriseId = enterpriseScopeHelper.resolveScopedEnterpriseId();
        if (enterpriseId == null)
        {
            throw new ServiceException("current user has no enterprise scope.");
        }
        job.setEnterpriseId(enterpriseId);
    }
}
