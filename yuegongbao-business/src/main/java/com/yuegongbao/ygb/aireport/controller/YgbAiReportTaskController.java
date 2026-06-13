package com.yuegongbao.ygb.aireport.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportTask;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportTaskService;

@RestController
@RequestMapping("/ygb/aiReport/task")
public class YgbAiReportTaskController extends BaseController
{
    @Autowired
    private IYgbAiReportTaskService aiReportTaskService;

    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAiReportTask task)
    {
        startPage();
        List<YgbAiReportTask> list = aiReportTaskService.selectAiReportTaskList(task);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbAiReportTask task)
    {
        return success(aiReportTaskService.selectAiReportTaskSummary(task));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        return success(aiReportTaskService.selectAiReportTaskById(taskId));
    }

    @Log(title = "AI报告建议任务", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAiReportTask task)
    {
        List<YgbAiReportTask> list = aiReportTaskService.selectAiReportTaskList(task);
        ExcelUtil<YgbAiReportTask> util = new ExcelUtil<>(YgbAiReportTask.class);
        util.exportExcel(response, list, "ai_report_task");
    }

    @Log(title = "AI报告建议任务", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:add')")
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody YgbAiReportTask task)
    {
        return toAjax(aiReportTaskService.insertAiReportTask(task, getUsername()));
    }

    @Log(title = "AI报告建议任务", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody YgbAiReportTask task)
    {
        return toAjax(aiReportTaskService.updateAiReportTask(task, getUsername()));
    }

    @Log(title = "AI报告建议任务", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:status')")
    @PutMapping("/status/{taskId}")
    public AjaxResult status(@PathVariable Long taskId, @RequestBody YgbAiReportTask task)
    {
        return toAjax(aiReportTaskService.updateAiReportTaskStatus(taskId, task, getUsername()));
    }

    @Log(title = "AI报告建议任务", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportTask:remove')")
    @DeleteMapping("/remove/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(aiReportTaskService.deleteAiReportTaskByIds(taskIds, getUsername()));
    }
}
