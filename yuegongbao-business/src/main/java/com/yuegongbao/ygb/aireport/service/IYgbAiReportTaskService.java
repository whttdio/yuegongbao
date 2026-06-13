package com.yuegongbao.ygb.aireport.service;

import java.util.List;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportTask;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportTaskSummary;

public interface IYgbAiReportTaskService
{
    List<YgbAiReportTask> selectAiReportTaskList(YgbAiReportTask task);

    YgbAiReportTaskSummary selectAiReportTaskSummary(YgbAiReportTask task);

    YgbAiReportTask selectAiReportTaskById(Long taskId);

    int insertAiReportTask(YgbAiReportTask task, String operator);

    int updateAiReportTask(YgbAiReportTask task, String operator);

    int updateAiReportTaskStatus(Long taskId, YgbAiReportTask task, String operator);

    int deleteAiReportTaskByIds(Long[] taskIds, String operator);
}
