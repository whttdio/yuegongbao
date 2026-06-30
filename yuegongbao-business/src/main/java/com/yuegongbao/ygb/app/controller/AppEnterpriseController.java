package com.yuegongbao.ygb.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.app.domain.vo.AppEnterpriseActionRequest;
import com.yuegongbao.ygb.app.service.AppEnterpriseService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;

@RestController
@RequestMapping("/app/enterprise")
public class AppEnterpriseController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private AppEnterpriseService appEnterpriseService;

    @GetMapping("/home/dashboard")
    public AjaxResult homeDashboard()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getHomeDashboard(user, worker));
    }

    @GetMapping("/workbench/dashboard")
    public AjaxResult workbenchDashboard()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getWorkbenchDashboard(user, worker));
    }

    @GetMapping("/people/ledger")
    public AjaxResult peopleLedger()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getPeopleLedger(user, worker));
    }

    @PostMapping("/people/action")
    public AjaxResult peopleAction(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitPeopleAction(user, worker, request));
    }

    @PostMapping("/people/export")
    public AjaxResult exportPeople(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.exportPeopleLedger(user, worker, request));
    }

    @GetMapping("/device/ledger")
    public AjaxResult deviceLedger()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getDeviceLedger(user, worker));
    }

    @PostMapping("/device/action")
    public AjaxResult deviceAction(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitDeviceAction(user, worker, request));
    }

    @PostMapping("/device/export")
    public AjaxResult exportDevice(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.exportDeviceLedger(user, worker, request));
    }

    @GetMapping("/salary/dashboard")
    public AjaxResult salaryDashboard()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getSalaryDashboard(user, worker));
    }

    @PostMapping("/salary/confirm")
    public AjaxResult salaryConfirm(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitSalaryConfirm(user, worker, request));
    }

    @PostMapping("/salary/import-draft")
    public AjaxResult salaryImportDraft(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.importSalaryDraft(user, worker, request));
    }

    @GetMapping("/operation-approval/dashboard")
    public AjaxResult operationApprovalDashboard()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getOperationApprovalDashboard(user, worker));
    }

    @PostMapping("/operation-approval/submit")
    public AjaxResult operationApprovalSubmit(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitOperationApproval(user, worker, request));
    }

    @PostMapping("/operation-approval/export")
    public AjaxResult operationApprovalExport()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.exportOperationLedger(user, worker));
    }

    @GetMapping("/insurance/dashboard")
    public AjaxResult insuranceDashboard()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getInsuranceDashboard(user, worker));
    }

    @PostMapping("/insurance/action")
    public AjaxResult insuranceAction(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitInsuranceAction(user, worker, request));
    }

    @GetMapping("/training/dashboard")
    public AjaxResult trainingDashboard()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getTrainingDashboard(user, worker));
    }

    @PostMapping("/training/save-draft")
    public AjaxResult trainingSaveDraft(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.saveTrainingPlanDraft(user, worker, request));
    }

    @PostMapping("/training/action")
    public AjaxResult trainingAction(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitTrainingAction(user, worker, request));
    }

    @GetMapping("/job-publish/draft")
    public AjaxResult jobPublishDraft()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.getJobPublishDraft(user, worker));
    }

    @PostMapping("/job-publish/save-draft")
    public AjaxResult jobPublishSaveDraft(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.saveJobPublishDraft(user, worker, request));
    }

    @PostMapping("/job-publish/submit")
    public AjaxResult jobPublishSubmit(@RequestBody(required = false) AppEnterpriseActionRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorkerIfPresent();
        return AjaxResult.success(appEnterpriseService.submitJobPublish(user, worker, request));
    }
}
