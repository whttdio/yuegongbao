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
import com.yuegongbao.ygb.worker.domain.WorkerFeedback;
import com.yuegongbao.ygb.worker.domain.WorkerUploadRecord;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.service.WorkerProfileService;

@RestController
@RequestMapping("/ygb/worker/profile/manage")
public class YgbWorkerProfileManageController extends BaseController
{
    @Autowired
    private WorkerProfileService workerProfileService;

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/feedback/list")
    public TableDataInfo feedbackList(WorkerFeedback query)
    {
        startPage();
        List<WorkerFeedback> list = workerProfileService.listFeedbackManageRecords(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/feedback/{feedbackId}")
    public AjaxResult feedbackDetail(@PathVariable Long feedbackId)
    {
        return success(workerProfileService.getFeedbackManageDetail(feedbackId));
    }

    @Log(title = "Worker Feedback Handle", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:flow')")
    @PostMapping("/feedback/status/{feedbackId}")
    public AjaxResult updateFeedbackStatus(@PathVariable Long feedbackId, @RequestBody WorkerMessageHandleRequest request)
    {
        return success(workerProfileService.updateFeedbackHandle(feedbackId, request, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/upload-record/list")
    public TableDataInfo uploadRecordList(WorkerUploadRecord query)
    {
        startPage();
        List<WorkerUploadRecord> list = workerProfileService.listUploadRecordManageRecords(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/upload-record/{uploadId}")
    public AjaxResult uploadRecordDetail(@PathVariable Long uploadId)
    {
        return success(workerProfileService.getUploadRecordManageDetail(uploadId));
    }
}
