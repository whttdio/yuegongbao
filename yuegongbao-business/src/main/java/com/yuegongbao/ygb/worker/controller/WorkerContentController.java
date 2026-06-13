package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.service.WorkerContentService;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;

@RestController
@RequestMapping("/app/worker")
public class WorkerContentController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerContentService workerContentService;

    @GetMapping("/activity/detail")
    public AjaxResult activityDetail()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerContentService.getActivityDetail(user.getUserId()));
    }

    @PostMapping("/activity/join")
    public AjaxResult activityJoin(@RequestParam String activityKey)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerContentService.joinActivity(worker, user, activityKey));
    }

    @GetMapping("/activity/join-list")
    public AjaxResult activityJoinList()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerContentService.getActivityJoinList(user.getUserId()));
    }

    @GetMapping("/video/list")
    public AjaxResult videoList()
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerContentService.getVideoList(user.getUserId()));
    }

    @GetMapping("/video/detail")
    public AjaxResult videoDetail(@RequestParam String videoKey)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        return AjaxResult.success(workerContentService.getVideoDetail(user.getUserId(), videoKey));
    }

    @PostMapping("/video/progress")
    public AjaxResult videoProgress(@RequestParam String videoKey, @RequestParam Integer watchedSeconds,
        @RequestParam(required = false) Integer totalSeconds)
    {
        SysUser user = workerCurrentUserService.getCurrentSysUser();
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerContentService.saveVideoProgress(worker, user.getUserId(), videoKey, watchedSeconds, totalSeconds));
    }

    @GetMapping("/ai-training/detail")
    public AjaxResult aiTrainingDetail()
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerContentService.getAiTrainingDetail(worker));
    }
}
