package com.yuegongbao.ygb.worker.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.vo.WorkerActivityHandleRequest;
import com.yuegongbao.ygb.worker.service.WorkerContentService;

@RestController
@RequestMapping("/ygb/worker/activity/manage")
public class YgbWorkerActivityManageController extends BaseController
{
    @Autowired
    private WorkerContentService workerContentService;

    @PreAuthorize("@ss.hasPermi('ygb:workerActivity:list')")
    @GetMapping("/list")
    public TableDataInfo list(WorkerActivityJoin query)
    {
        startPage();
        List<java.util.Map<String, Object>> list = workerContentService.listActivityManageRecords(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerActivity:list')")
    @GetMapping("/{joinId}")
    public AjaxResult detail(@PathVariable Long joinId)
    {
        return success(workerContentService.getActivityManageDetail(joinId));
    }

    @Log(title = "劳动者活动处理", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:workerActivity:handle')")
    @PostMapping("/status/{joinId}")
    public AjaxResult handle(@PathVariable Long joinId, @RequestBody WorkerActivityHandleRequest request)
    {
        return AjaxResult.success("活动状态已更新。",
            workerContentService.updateActivityHandle(joinId, request, getUsername()));
    }
}
