package com.yuegongbao.ygb.worker.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.worker.domain.WorkerJobApply;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;

public interface WorkerJobMapper
{
    List<WorkerJobPost> selectJobList(@Param("keyword") String keyword, @Param("jobType") String jobType,
        @Param("salaryMin") BigDecimal salaryMin, @Param("salaryMax") BigDecimal salaryMax);

    WorkerJobPost selectJobDetail(@Param("jobId") Long jobId);

    Long countUserApplied(@Param("jobId") Long jobId, @Param("userId") Long userId);

    int insertJobApply(WorkerJobApply apply);

    List<WorkerJobApply> selectJobApplyList(@Param("userId") Long userId);

    List<WorkerJobPost> selectAdminJobPostList(WorkerJobPost query);

    WorkerJobPost selectAdminJobPostById(@Param("jobId") Long jobId);

    int insertJobPost(WorkerJobPost job);

    int updateJobPost(WorkerJobPost job);

    int deleteJobPostByIds(@Param("jobIds") Long[] jobIds, @Param("updateBy") String updateBy);
}
