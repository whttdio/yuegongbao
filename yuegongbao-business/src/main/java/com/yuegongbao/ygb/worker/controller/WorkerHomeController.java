package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;
import com.yuegongbao.ygb.worker.service.WorkerHomeService;

@RestController
@RequestMapping("/app/worker")
public class WorkerHomeController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerHomeService workerHomeService;

    @GetMapping("/home")
    public AjaxResult home()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerHomeService.getHomeView(worker, user.getUserId()));
    }

    @GetMapping("/profile")
    public AjaxResult profile()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerHomeService.getProfile(worker, user.getUserId(), user.getUserName(),
            user.getNickName()));
    }

    @GetMapping("/workbench")
    public AjaxResult workbench()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerHomeService.getWorkbenchView(worker, user.getUserId(), user.getUserName(),
            user.getNickName()));
    }
}
