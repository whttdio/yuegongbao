package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.vo.WorkerAttendanceCheckRequest;
import com.yuegongbao.ygb.worker.service.WorkerAttendanceService;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;

@RestController
@RequestMapping("/app/worker/attendance")
public class WorkerAttendanceController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerAttendanceService workerAttendanceService;

    @PostMapping("/check-in")
    public AjaxResult checkIn(@Validated @RequestBody(required = false) WorkerAttendanceCheckRequest request)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerAttendanceService.checkIn(worker, SecurityUtils.getUsername(), request));
    }

    @PostMapping("/check-out")
    public AjaxResult checkOut(@Validated @RequestBody(required = false) WorkerAttendanceCheckRequest request)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerAttendanceService.checkOut(worker, SecurityUtils.getUsername(), request));
    }

    @GetMapping("/monthly")
    public AjaxResult monthly(@RequestParam(required = false) String month)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerAttendanceService.getMonthlyView(worker, month));
    }

    @GetMapping("/day")
    public AjaxResult day(@RequestParam String date)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerAttendanceService.getDayDetail(worker, date));
    }
}
