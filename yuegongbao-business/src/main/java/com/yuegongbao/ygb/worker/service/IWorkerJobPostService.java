package com.yuegongbao.ygb.worker.service;

import java.util.List;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;

public interface IWorkerJobPostService
{
    WorkerJobPost selectJobPostById(Long jobId);

    List<WorkerJobPost> selectJobPostList(WorkerJobPost query);

    int insertJobPost(WorkerJobPost job);

    int updateJobPost(WorkerJobPost job);

    int reviewJobPost(Long jobId, String status, String opinion, String operator);

    int deleteJobPostByIds(Long[] jobIds, String updateBy);
}
