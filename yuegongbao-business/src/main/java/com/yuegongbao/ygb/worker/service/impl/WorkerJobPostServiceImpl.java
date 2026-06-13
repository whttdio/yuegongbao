package com.yuegongbao.ygb.worker.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import com.yuegongbao.ygb.worker.service.IWorkerJobPostService;

@Service
public class WorkerJobPostServiceImpl implements IWorkerJobPostService
{
    @Autowired
    private WorkerJobMapper workerJobMapper;

    @Override
    public WorkerJobPost selectJobPostById(Long jobId)
    {
        return workerJobMapper.selectAdminJobPostById(jobId);
    }

    @Override
    public List<WorkerJobPost> selectJobPostList(WorkerJobPost query)
    {
        return workerJobMapper.selectAdminJobPostList(query);
    }

    @Override
    public int insertJobPost(WorkerJobPost job)
    {
        return workerJobMapper.insertJobPost(job);
    }

    @Override
    public int updateJobPost(WorkerJobPost job)
    {
        return workerJobMapper.updateJobPost(job);
    }

    @Override
    public int deleteJobPostByIds(Long[] jobIds, String updateBy)
    {
        return workerJobMapper.deleteJobPostByIds(jobIds, updateBy);
    }
}
