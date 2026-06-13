package com.yuegongbao.ygb.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.vo.WorkerTrainingAnswerRequest;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@RestController
@RequestMapping("/app/worker/training")
public class WorkerTrainingController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private WorkerTrainingService workerTrainingService;

    @GetMapping("/progress")
    public AjaxResult progress()
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.getProgress(worker));
    }

    @GetMapping("/questions")
    public AjaxResult questions()
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.listQuestions(worker));
    }

    @PostMapping("/answer")
    public AjaxResult answer(@Validated @RequestBody WorkerTrainingAnswerRequest request)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.answerQuestion(worker, request.getQuestionId(),
            request.getAnswerIndex()));
    }

    @GetMapping("/history")
    public AjaxResult history()
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.getHistory(worker));
    }

    @GetMapping("/history-detail")
    public AjaxResult historyDetail(@RequestParam String month)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.getHistoryDetail(worker, month));
    }

    @GetMapping("/courses")
    public AjaxResult courses()
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.getCourses(worker));
    }

    @GetMapping("/course-detail")
    public AjaxResult courseDetail(@RequestParam String courseKey)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.getCourseDetail(worker, courseKey));
    }

    @PostMapping("/study-progress")
    public AjaxResult studyProgress(@RequestParam String courseKey, @RequestParam Integer studiedSeconds)
    {
        YgbPerson worker = workerCurrentUserService.getCurrentWorker();
        return AjaxResult.success(workerTrainingService.saveStudyProgress(worker, courseKey, studiedSeconds));
    }
}
