package com.yuegongbao.ygb.worker.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;

@RestController
@RequestMapping("/ygb/worker/message/manage")
public class YgbWorkerMessageManageController extends BaseController
{
    @Autowired
    private WorkerMessageService workerMessageService;

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/complaint/list")
    public TableDataInfo complaintList(WorkerComplaint query)
    {
        startPage();
        List<WorkerComplaint> list = workerMessageService.listComplaintManageRecords(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/complaint/{complaintId}")
    public AjaxResult complaintDetail(@PathVariable Long complaintId)
    {
        return success(workerMessageService.getComplaintManageDetail(complaintId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/legal-consult/list")
    public TableDataInfo legalConsultList(WorkerLegalConsult query)
    {
        startPage();
        List<WorkerLegalConsult> list = workerMessageService.listLegalConsultManageRecords(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:workerMessage:list')")
    @GetMapping("/legal-consult/{consultId}")
    public AjaxResult legalConsultDetail(@PathVariable Long consultId)
    {
        return success(workerMessageService.getLegalConsultManageDetail(consultId));
    }
}
