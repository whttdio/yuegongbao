package com.yuegongbao.ygb.aireport.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aireport.domain.YgbAiReport;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportTask;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportTaskSummary;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportMapper;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportTaskMapper;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportTaskService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbAiReportTaskServiceImpl implements IYgbAiReportTaskService
{
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_PROCESSING = "processing";
    private static final String STATUS_CLOSED = "closed";
    private static final String STATUS_REJECTED = "rejected";

    @Autowired
    private YgbAiReportTaskMapper aiReportTaskMapper;

    @Autowired
    private YgbAiReportMapper aiReportMapper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbAiReportTask> selectAiReportTaskList(YgbAiReportTask task)
    {
        List<YgbAiReportTask> list = aiReportTaskMapper.selectAiReportTaskList(task);
        list.forEach(this::hydrateRegionName);
        return list;
    }

    @Override
    public YgbAiReportTaskSummary selectAiReportTaskSummary(YgbAiReportTask task)
    {
        List<YgbAiReportTask> list = selectAiReportTaskList(task);
        YgbAiReportTaskSummary summary = new YgbAiReportTaskSummary();
        summary.setTotalCount(list.size());

        int pendingCount = 0;
        int processingCount = 0;
        int closedCount = 0;
        int overdueCount = 0;
        Date now = new Date();

        for (YgbAiReportTask item : list)
        {
            if ("pending".equals(item.getHandleStatus()))
            {
                pendingCount++;
            }
            if ("processing".equals(item.getHandleStatus()))
            {
                processingCount++;
            }
            if ("closed".equals(item.getHandleStatus()))
            {
                closedCount++;
            }
            if (item.getDueDate() != null && item.getDueDate().before(now) && !"closed".equals(item.getHandleStatus()))
            {
                overdueCount++;
            }
        }

        summary.setPendingCount(pendingCount);
        summary.setProcessingCount(processingCount);
        summary.setClosedCount(closedCount);
        summary.setOverdueCount(overdueCount);
        return summary;
    }

    @Override
    public YgbAiReportTask selectAiReportTaskById(Long taskId)
    {
        YgbAiReportTask task = aiReportTaskMapper.selectAiReportTaskById(taskId);
        if (task != null)
        {
            regionScopeHelper.assertEntityRegionAllowed(task);
            hydrateRegionName(task);
        }
        return task;
    }

    @Override
    public int insertAiReportTask(YgbAiReportTask task, String operator)
    {
        fillTaskDefaults(task);
        validateInitialStatus(task.getHandleStatus());
        regionScopeHelper.assertEntityRegionAllowed(task);
        task.setCreateBy(operator);
        task.setCreateTime(new Date());
        task.setUpdateBy(operator);
        task.setUpdateTime(new Date());
        return aiReportTaskMapper.insertAiReportTask(task);
    }

    @Override
    public int updateAiReportTask(YgbAiReportTask task, String operator)
    {
        if (task == null || task.getTaskId() == null)
        {
            throw new ServiceException("Task ID cannot be empty");
        }
        YgbAiReportTask current = requireAiReportTaskAllowed(task.getTaskId());
        fillTaskDefaults(task);
        regionScopeHelper.assertEntityRegionAllowed(task);
        task.setHandleStatus(current.getHandleStatus());
        task.setUpdateBy(operator);
        task.setUpdateTime(new Date());
        return aiReportTaskMapper.updateAiReportTask(task);
    }

    @Override
    public int updateAiReportTaskStatus(Long taskId, YgbAiReportTask task, String operator)
    {
        YgbAiReportTask current = requireAiReportTaskAllowed(taskId);
        String nextStatus = transitStatus(current.getHandleStatus(), task.getHandleStatus());
        if ((STATUS_CLOSED.equals(nextStatus) || STATUS_REJECTED.equals(nextStatus))
            && StringUtils.isEmpty(task.getFeedbackText()))
        {
            throw new ServiceException("Task feedback cannot be empty when closing or rejecting");
        }
        current.setHandleStatus(nextStatus);
        current.setFeedbackText(task.getFeedbackText());
        current.setRemark(task.getRemark());
        current.setUpdateBy(operator);
        current.setUpdateTime(new Date());
        return aiReportTaskMapper.updateAiReportTask(current);
    }

    @Override
    public int deleteAiReportTaskByIds(Long[] taskIds, String operator)
    {
        for (Long taskId : taskIds)
        {
            requireAiReportTaskAllowed(taskId);
        }
        return aiReportTaskMapper.deleteAiReportTaskByIds(taskIds, operator);
    }

    private YgbAiReportTask requireAiReportTaskAllowed(Long taskId)
    {
        if (taskId == null)
        {
            throw new ServiceException("Task ID cannot be empty");
        }
        YgbAiReportTask task = aiReportTaskMapper.selectAiReportTaskById(taskId);
        if (task == null)
        {
            throw new ServiceException("AI report task does not exist");
        }
        regionScopeHelper.assertEntityRegionAllowed(task);
        return task;
    }

    private void fillTaskDefaults(YgbAiReportTask task)
    {
        if (task == null)
        {
            throw new ServiceException("AI report task cannot be null");
        }
        bindReportInfo(task);
        if (StringUtils.isEmpty(task.getTaskType()))
        {
            task.setTaskType("review");
        }
        if (StringUtils.isEmpty(task.getTaskName()))
        {
            task.setTaskName("AI风险处置任务");
        }
        if (StringUtils.isEmpty(task.getRegionCode()))
        {
            task.setRegionCode(YgbRegionHelper.defaultDashboardRegion(null));
        }
        if (StringUtils.isEmpty(task.getHandleStatus()))
        {
            task.setHandleStatus(STATUS_PENDING);
        }
        if (task.getDueDate() == null)
        {
            task.setDueDate(Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()));
        }
        if (StringUtils.isEmpty(task.getSourceMode()))
        {
            task.setSourceMode("manual");
        }
    }

    private void bindReportInfo(YgbAiReportTask task)
    {
        if (task.getReportId() == null)
        {
            return;
        }
        YgbAiReport report = aiReportMapper.selectAiReportById(task.getReportId());
        if (report == null)
        {
            return;
        }
        if (StringUtils.isEmpty(task.getReportType()))
        {
            task.setReportType(report.getReportType());
        }
        if (StringUtils.isEmpty(task.getRegionCode()))
        {
            task.setRegionCode(report.getRegionCode());
        }
        if (StringUtils.isEmpty(task.getRiskLevel()))
        {
            task.setRiskLevel(report.getRiskLevel());
        }
        if (StringUtils.isEmpty(task.getReportPeriod()) && report.getPeriodStart() != null && report.getPeriodEnd() != null)
        {
            LocalDateTime start = LocalDateTime.ofInstant(report.getPeriodStart().toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(report.getPeriodEnd().toInstant(), ZoneId.systemDefault());
            task.setReportPeriod(PERIOD_FORMATTER.format(start) + " ~ " + PERIOD_FORMATTER.format(end));
        }
        if (StringUtils.isEmpty(task.getReportSummary()))
        {
            task.setReportSummary(report.getReportSummary());
        }
        if (StringUtils.isEmpty(task.getSuggestionText()))
        {
            task.setSuggestionText(report.getReportSummary());
        }
        if (StringUtils.isEmpty(task.getTaskName()))
        {
            task.setTaskName("AI建议任务-" + task.getReportId());
        }
    }

    private void hydrateRegionName(YgbAiReportTask task)
    {
        task.setRegionName(YgbRegionHelper.resolveRegionName(task.getRegionCode()));
    }

    private void validateInitialStatus(String status)
    {
        if (!STATUS_DRAFT.equals(status) && !STATUS_PENDING.equals(status))
        {
            throw new ServiceException("New AI report task must be draft or pending");
        }
    }

    private String transitStatus(String currentStatus, String requestedStatus)
    {
        String current = StringUtils.defaultIfEmpty(currentStatus, STATUS_PENDING);
        String next = StringUtils.defaultIfEmpty(requestedStatus, current);
        if (next.equals(current))
        {
            return next;
        }
        if (STATUS_PROCESSING.equals(next))
        {
            requireCurrent(current, STATUS_DRAFT, STATUS_PENDING);
            return next;
        }
        if (STATUS_CLOSED.equals(next) || STATUS_REJECTED.equals(next))
        {
            requireCurrent(current, STATUS_PROCESSING);
            return next;
        }
        if (STATUS_PENDING.equals(next))
        {
            requireCurrent(current, STATUS_DRAFT);
            return next;
        }
        throw new ServiceException("Unsupported AI report task status");
    }

    private void requireCurrent(String current, String... allowedStatuses)
    {
        for (String allowedStatus : allowedStatuses)
        {
            if (allowedStatus.equals(current))
            {
                return;
            }
        }
        throw new ServiceException("Current AI report task status does not allow this operation");
    }
}
