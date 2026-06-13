package com.yuegongbao.ygb.worker.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.worker.domain.WorkerNoticeMessage;

public interface WorkerNoticeMessageMapper
{
    int insertWorkerNoticeMessage(WorkerNoticeMessage message);

    int countUnreadWorkerNoticeMessage(@Param("userId") Long userId);

    List<WorkerNoticeMessage> selectWorkerNoticeMessageList(@Param("userId") Long userId,
        @Param("limit") Integer limit);

    WorkerNoticeMessage selectWorkerNoticeMessageById(@Param("messageId") Long messageId,
        @Param("userId") Long userId);

    WorkerNoticeMessage selectLatestWorkerNoticeMessageByBiz(@Param("userId") Long userId,
        @Param("bizType") String bizType, @Param("bizId") String bizId);

    int markWorkerNoticeMessageRead(@Param("messageId") Long messageId, @Param("userId") Long userId,
        @Param("updateBy") String updateBy);
}
