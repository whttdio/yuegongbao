package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.app.domain.vo.AppWorkerHighRiskActionRequest;
import com.yuegongbao.ygb.app.service.AppWorkerHighRiskService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;

@RestController
@RequestMapping("/app/worker/high-risk")
public class WorkerHighRiskController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private AppWorkerHighRiskService appWorkerHighRiskService;

    @GetMapping("/unlock/dashboard")
    public AjaxResult unlockDashboard()
    {
        return AjaxResult.success(appWorkerHighRiskService.getUnlockDashboard(currentUser(), currentWorker()));
    }

    @PostMapping("/unlock/scan-verify")
    public AjaxResult verifyScan(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.verifyScan(currentUser(), currentWorker(), request));
    }

    @PostMapping("/unlock/face-verify")
    public AjaxResult verifyFace(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.verifyFace(currentUser(), currentWorker(), request));
    }

    @PostMapping("/unlock/submit")
    public AjaxResult submitUnlock(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.submitUnlock(currentUser(), currentWorker(), request));
    }

    @GetMapping("/outwork/draft")
    public AjaxResult outworkDraft()
    {
        return AjaxResult.success(appWorkerHighRiskService.getOutworkDraft(currentUser(), currentWorker()));
    }

    @PostMapping("/outwork/attachment")
    public AjaxResult saveOutworkAttachment(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.saveOutworkAttachment(currentUser(), currentWorker(), request));
    }

    @PostMapping("/outwork/submit")
    public AjaxResult submitOutworkApply(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.submitOutworkApply(currentUser(), currentWorker(), request));
    }

    @GetMapping("/certificates")
    public AjaxResult certificates()
    {
        return AjaxResult.success(appWorkerHighRiskService.getCertificates(currentUser(), currentWorker()));
    }

    @PostMapping("/certificates/renew")
    public AjaxResult renewCertificates(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.submitCertificateRenew(currentUser(), currentWorker(), request));
    }

    @PostMapping("/certificates/upload")
    public AjaxResult uploadCertificates(@RequestBody(required = false) AppWorkerHighRiskActionRequest request)
    {
        return AjaxResult.success(appWorkerHighRiskService.submitCertificateUpload(currentUser(), currentWorker(), request));
    }

    @GetMapping("/work-records")
    public AjaxResult workRecords()
    {
        return AjaxResult.success(appWorkerHighRiskService.getWorkRecords(currentUser(), currentWorker()));
    }

    private SysUser currentUser()
    {
        return workerCurrentUserService.getCurrentSysUser();
    }

    private YgbPerson currentWorker()
    {
        return workerCurrentUserService.getCurrentWorker();
    }
}
