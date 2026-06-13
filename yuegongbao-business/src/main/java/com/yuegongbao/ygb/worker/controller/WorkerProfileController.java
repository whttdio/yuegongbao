package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.vo.WorkerFeedbackCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPointExchangeRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushRegisterRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushTestRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerRealnameSubmitRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerResumeSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerSettingSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerUploadRecordSaveRequest;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;
import com.yuegongbao.ygb.worker.service.WorkerProfileService;

@RestController
@RequestMapping("/app/worker")
public class WorkerProfileController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerProfileService workerProfileService;

    @GetMapping("/resume/detail")
    public AjaxResult resumeDetail()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getResume(worker, user.getUserId()));
    }

    @GetMapping("/labor-contract/list")
    public AjaxResult laborContractList()
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getLaborContractList(worker));
    }

    @GetMapping("/labor-contract/detail")
    public AjaxResult laborContractDetail(@RequestParam Long contractId)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getLaborContractDetail(worker, contractId));
    }

    @PostMapping("/resume/save")
    public AjaxResult resumeSave(@RequestBody WorkerResumeSaveRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.saveResume(worker, user, request));
    }

    @GetMapping("/help/list")
    public AjaxResult helpList()
    {
        return AjaxResult.success(workerProfileService.getHelpList());
    }

    @GetMapping("/help/detail")
    public AjaxResult helpDetail(@RequestParam String articleKey)
    {
        return AjaxResult.success(workerProfileService.getHelpDetail(articleKey));
    }

    @GetMapping("/real-name/detail")
    public AjaxResult realnameDetail()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getRealnameDetail(worker, user.getUserId(), user.getUserName()));
    }

    @PostMapping("/real-name/submit")
    public AjaxResult submitRealname(@RequestBody WorkerRealnameSubmitRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.submitRealnameApply(worker, user, request));
    }

    @PostMapping("/feedback/create")
    public AjaxResult feedbackCreate(@RequestBody WorkerFeedbackCreateRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.createFeedback(worker, user, request));
    }

    @GetMapping("/settings/detail")
    public AjaxResult settingsDetail()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getSettings(worker, user.getUserId()));
    }

    @PostMapping("/settings/save")
    public AjaxResult settingsSave(@RequestBody WorkerSettingSaveRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.saveSettings(worker, user, request));
    }

    @PostMapping("/settings/push-register")
    public AjaxResult registerPush(@RequestBody WorkerPushRegisterRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.registerPush(worker, user, request));
    }

    @PostMapping("/settings/push-test")
    public AjaxResult sendPushTest(@RequestBody(required = false) WorkerPushTestRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.sendPushTest(worker, user, request));
    }

    @GetMapping("/points/account")
    public AjaxResult pointsAccount()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getPointAccount(worker, user.getUserId()));
    }

    @PostMapping("/points/exchange")
    public AjaxResult pointsExchange(@RequestBody WorkerPointExchangeRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.exchangePointGoods(worker, user, request));
    }

    @GetMapping("/insurance/security")
    public AjaxResult insuranceSecurity()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.getInsuranceSecurity(worker, user.getUserId()));
    }

    @PostMapping("/upload-record/create")
    public AjaxResult uploadRecordCreate(@RequestBody WorkerUploadRecordSaveRequest request)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerProfileService.createUploadRecord(worker, user, request));
    }

    @GetMapping("/upload-record/list")
    public AjaxResult uploadRecordList(@RequestParam(required = false) String categoryCode)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerProfileService.getUploadRecordList(user.getUserId(), categoryCode));
    }
}
