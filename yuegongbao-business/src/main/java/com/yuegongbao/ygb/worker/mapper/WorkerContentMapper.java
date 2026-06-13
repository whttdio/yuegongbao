package com.yuegongbao.ygb.worker.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.WorkerVideoProgress;

public interface WorkerContentMapper
{
    Long countActivityJoin(@Param("activityKey") String activityKey, @Param("userId") Long userId);

    int insertActivityJoin(WorkerActivityJoin join);

    int updateActivityJoin(WorkerActivityJoin join);

    List<WorkerActivityJoin> selectActivityJoinList(@Param("userId") Long userId);

    WorkerActivityJoin selectLatestActivityJoin(@Param("activityKey") String activityKey, @Param("userId") Long userId);

    List<WorkerActivityJoin> selectActivityJoinManageList(WorkerActivityJoin query);

    WorkerActivityJoin selectActivityJoinByIdForManage(@Param("joinId") Long joinId);

    WorkerVideoProgress selectVideoProgress(@Param("videoKey") String videoKey, @Param("userId") Long userId);

    int insertVideoProgress(WorkerVideoProgress progress);

    int updateVideoProgress(WorkerVideoProgress progress);
}
