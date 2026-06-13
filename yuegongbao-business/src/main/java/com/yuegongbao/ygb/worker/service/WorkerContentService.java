package com.yuegongbao.ygb.worker.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.vo.WorkerActivityHandleRequest;

public interface WorkerContentService
{
    Map<String, Object> getActivityDetail(Long userId);

    Map<String, Object> joinActivity(YgbPerson worker, SysUser user, String activityKey);

    Map<String, Object> getActivityJoinList(Long userId);

    List<Map<String, Object>> listActivityManageRecords(WorkerActivityJoin query);

    Map<String, Object> getActivityManageDetail(Long joinId);

    Map<String, Object> updateActivityHandle(Long joinId, WorkerActivityHandleRequest request, String operator);

    Map<String, Object> getVideoList(Long userId);

    Map<String, Object> getVideoDetail(Long userId, String videoKey);

    Map<String, Object> saveVideoProgress(YgbPerson worker, Long userId, String videoKey, Integer watchedSeconds, Integer totalSeconds);

    Map<String, Object> getAiTrainingDetail(YgbPerson worker);
}
