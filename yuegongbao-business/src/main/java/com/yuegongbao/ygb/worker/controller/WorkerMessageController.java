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
import com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;

@RestController
@RequestMapping("/app/worker")
public class WorkerMessageController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerMessageService workerMessageService;

    @PostMapping("/complaint/create")
    public AjaxResult createComplaint(@RequestBody WorkerComplaintCreateRequest request)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.createComplaint(worker, user, request));
    }

    @GetMapping("/complaint/list")
    public AjaxResult complaintList(@RequestParam(required = false) String status)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.listComplaints(user.getUserId(), status));
    }

    @GetMapping("/complaint/detail")
    public AjaxResult complaintDetail(@RequestParam Long complaintId)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.getComplaintDetail(user.getUserId(), complaintId));
    }

    @PostMapping("/legal-consult/create")
    public AjaxResult createLegalConsult(@RequestBody WorkerLegalConsultCreateRequest request)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.createLegalConsult(worker, user, request));
    }

    @GetMapping("/legal-consult/list")
    public AjaxResult legalConsultList(@RequestParam(required = false) String status)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.listLegalConsults(user.getUserId(), status));
    }

    @GetMapping("/legal-consult/detail")
    public AjaxResult legalConsultDetail(@RequestParam Long consultId)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.getLegalConsultDetail(user.getUserId(), consultId));
    }

    @GetMapping("/legal-consult/hotline")
    public AjaxResult hotline()
    {
        return AjaxResult.success(workerMessageService.getHotline());
    }

    @GetMapping("/legal-article/list")
    public AjaxResult legalArticleList()
    {
        return AjaxResult.success(workerMessageService.getLegalArticleList());
    }

    @GetMapping("/legal-article/detail")
    public AjaxResult legalArticleDetail(@RequestParam String articleKey)
    {
        return AjaxResult.success(workerMessageService.getLegalArticleDetail(articleKey));
    }

    @GetMapping("/legal-faq/list")
    public AjaxResult legalFaqList(@RequestParam(required = false) String keyword)
    {
        return AjaxResult.success(workerMessageService.getLegalFaqList(keyword));
    }

    @GetMapping("/legal-faq/detail")
    public AjaxResult legalFaqDetail(@RequestParam String faqKey)
    {
        return AjaxResult.success(workerMessageService.getLegalFaqDetail(faqKey));
    }

    @GetMapping("/union-service/home")
    public AjaxResult unionServiceHome()
    {
        return AjaxResult.success(workerMessageService.getUnionServiceHome());
    }

    @GetMapping("/union-service/cases")
    public AjaxResult unionCases()
    {
        return AjaxResult.success(workerMessageService.getUnionCaseList());
    }

    @GetMapping("/union-service/notices")
    public AjaxResult unionNotices()
    {
        return AjaxResult.success(workerMessageService.getUnionNoticeList());
    }

    @GetMapping("/union-service/case-detail")
    public AjaxResult unionCaseDetail(@RequestParam String caseKey)
    {
        return AjaxResult.success(workerMessageService.getUnionCaseDetail(caseKey));
    }

    @GetMapping("/union-service/notice-detail")
    public AjaxResult unionNoticeDetail(@RequestParam String noticeKey)
    {
        return AjaxResult.success(workerMessageService.getUnionNoticeDetail(noticeKey));
    }

    @GetMapping("/union-service/contracts")
    public AjaxResult unionContracts()
    {
        return AjaxResult.success(workerMessageService.getUnionContractList());
    }

    @GetMapping("/union-service/contract-detail")
    public AjaxResult unionContractDetail(@RequestParam String contractKey)
    {
        return AjaxResult.success(workerMessageService.getUnionContractDetail(contractKey));
    }

    @GetMapping("/notice/list")
    public AjaxResult noticeList(@RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.getNoticeList(user.getUserId(), pageNum, pageSize));
    }

    @GetMapping("/notice/detail")
    public AjaxResult noticeDetail(@RequestParam Long noticeId)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerMessageService.getNoticeDetail(user.getUserId(), noticeId));
    }

    @PostMapping("/notice/read")
    public AjaxResult noticeRead(@RequestParam Long noticeId)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        workerMessageService.markNoticeRead(user.getUserId(), noticeId);
        return AjaxResult.success();
    }
}
