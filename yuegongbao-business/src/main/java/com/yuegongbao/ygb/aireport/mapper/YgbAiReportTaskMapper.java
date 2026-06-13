package com.yuegongbao.ygb.aireport.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportTask;

public interface YgbAiReportTaskMapper
{
    List<YgbAiReportTask> selectAiReportTaskList(YgbAiReportTask task);

    YgbAiReportTask selectAiReportTaskById(Long taskId);

    int insertAiReportTask(YgbAiReportTask task);

    int updateAiReportTask(YgbAiReportTask task);

    int deleteAiReportTaskByIds(@Param("taskIds") Long[] taskIds, @Param("updateBy") String updateBy);
}
