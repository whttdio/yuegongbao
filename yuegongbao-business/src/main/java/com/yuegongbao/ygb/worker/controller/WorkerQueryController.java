package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;
import com.yuegongbao.ygb.worker.service.WorkerQueryService;

@RestController
@RequestMapping("/app/worker")
public class WorkerQueryController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerQueryService workerQueryService;

    @GetMapping("/salary/list")
    public AjaxResult salaryList(@RequestParam(required = false) String year)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerQueryService.getSalaryList(worker, year));
    }

    @GetMapping("/salary/detail")
    public AjaxResult salaryDetail(@RequestParam String month)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerQueryService.getSalaryDetail(worker, month));
    }

    @GetMapping("/social-security/list")
    public AjaxResult socialSecurityList(@RequestParam(required = false) String year)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerQueryService.getSocialSecurityList(worker, year));
    }

    @GetMapping("/social-security/detail")
    public AjaxResult socialSecurityDetail(@RequestParam String month)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerQueryService.getSocialSecurityDetail(worker, month));
    }

    @GetMapping("/tax/list")
    public AjaxResult taxList(@RequestParam(required = false) String year)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerQueryService.getTaxList(worker, year));
    }

    @GetMapping("/tax/detail")
    public AjaxResult taxDetail(@RequestParam String month)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerQueryService.getTaxDetail(worker, month));
    }
}
