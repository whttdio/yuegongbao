package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;

@RestController
@RequestMapping("/ygb/worker/message")
public class YgbWorkerMessageController extends BaseController
{
    @Autowired
    private WorkerMessageService workerMessageService;

    @Log(title = "劳动者投诉处理", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:flow')")
    @PostMapping("/complaint/status/{complaintId}")
    public AjaxResult updateComplaintStatus(@PathVariable Long complaintId, @RequestBody WorkerMessageHandleRequest request)
    {
        return AjaxResult.success("投诉处理状态已更新。",
            workerMessageService.updateComplaintHandle(complaintId, request, getUsername()));
    }

    @Log(title = "劳动者法律咨询处理", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:flow')")
    @PostMapping("/legal-consult/status/{consultId}")
    public AjaxResult updateLegalConsultStatus(@PathVariable Long consultId, @RequestBody WorkerMessageHandleRequest request)
    {
        return AjaxResult.success("法律咨询处理状态已更新。",
            workerMessageService.updateLegalConsultHandle(consultId, request, getUsername()));
    }
}
