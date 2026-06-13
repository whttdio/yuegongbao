package com.yuegongbao.ygb.worker.controller;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.service.WorkerCareerService;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;

@RestController
@RequestMapping("/app/worker/job")
public class WorkerCareerController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerCareerService workerCareerService;

    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) String keyword,
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) BigDecimal salaryMin,
        @RequestParam(required = false) BigDecimal salaryMax,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) Double radiusKm)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(
            workerCareerService.getJobList(worker, user.getUserId(), keyword, jobType, salaryMin, salaryMax, latitude, longitude,
                radiusKm));
    }

    @GetMapping("/map-config")
    public AjaxResult mapConfig(@RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerCareerService.getNearbyJobMapConfig(worker, latitude, longitude));
    }

    @GetMapping("/nearby")
    public AjaxResult nearby(@RequestParam(required = false) String keyword,
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) BigDecimal salaryMin,
        @RequestParam(required = false) BigDecimal salaryMax,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) Double radiusKm)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(
            workerCareerService.getNearbyJobList(worker, user.getUserId(), keyword, jobType, salaryMin, salaryMax, latitude,
                longitude, radiusKm));
    }

    @GetMapping("/detail")
    public AjaxResult detail(@RequestParam Long jobId)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerCareerService.getJobDetail(worker, user.getUserId(), jobId));
    }

    @PostMapping("/apply")
    public AjaxResult apply(@RequestParam Long jobId)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerCareerService.applyJob(worker, user, jobId));
    }

    @GetMapping("/apply/list")
    public AjaxResult applyList()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerCareerService.getJobApplyList(user.getUserId()));
    }
}
