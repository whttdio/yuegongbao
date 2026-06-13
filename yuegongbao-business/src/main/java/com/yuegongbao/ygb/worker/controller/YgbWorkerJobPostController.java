package com.yuegongbao.ygb.worker.controller;

import java.util.List;
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
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.service.IWorkerJobPostService;

@RestController
@RequestMapping("/ygb/worker/job")
public class YgbWorkerJobPostController extends BaseController
{
    @Autowired
    private IWorkerJobPostService workerJobPostService;

    @PreAuthorize("@ss.hasPermi('ygb:workerJob:list')")
    @GetMapping("/list")
    public TableDataInfo list(WorkerJobPost query)
    {
        startPage();
        List<WorkerJobPost> list = workerJobPostService.selectJobPostList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerJob:query')")
    @GetMapping("/{jobId}")
    public AjaxResult getInfo(@PathVariable Long jobId)
    {
        return success(workerJobPostService.selectJobPostById(jobId));
    }

    @Log(title = "招聘岗位", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:workerJob:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WorkerJobPost job)
    {
        job.setCreateBy(getUsername());
        return toAjax(workerJobPostService.insertJobPost(job));
    }

    @Log(title = "招聘岗位", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:workerJob:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WorkerJobPost job)
    {
        job.setUpdateBy(getUsername());
        return toAjax(workerJobPostService.updateJobPost(job));
    }

    @Log(title = "招聘岗位", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:workerJob:remove')")
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds)
    {
        return toAjax(workerJobPostService.deleteJobPostByIds(jobIds, getUsername()));
    }
}
