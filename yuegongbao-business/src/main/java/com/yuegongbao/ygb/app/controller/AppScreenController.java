package com.yuegongbao.ygb.app.controller;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.app.domain.vo.AppScreenDeviceConfigRequest;
import com.yuegongbao.ygb.app.domain.vo.AppScreenFaceAuthRequest;
import com.yuegongbao.ygb.app.service.AppScreenService;
import com.yuegongbao.common.annotation.Anonymous;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.vo.WorkerAttendanceCheckRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerFeedbackCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPointExchangeRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushRegisterRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushTestRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerRealnameSubmitRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerResumeSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerSettingSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerTrainingAnswerRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerUploadRecordSaveRequest;
import com.yuegongbao.ygb.worker.service.WorkerAttendanceService;
import com.yuegongbao.ygb.worker.service.WorkerCareerService;
import com.yuegongbao.ygb.worker.service.WorkerContentService;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;
import com.yuegongbao.ygb.worker.service.WorkerHomeService;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerProfileService;
import com.yuegongbao.ygb.worker.service.WorkerQueryService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@RestController
@RequestMapping("/app/screen")
public class AppScreenController
{
    @Autowired
    private WorkerCurrentUserService workerCurrentUserService;

    @Autowired
    private AppScreenService appScreenService;

    @Autowired
    private WorkerHomeService workerHomeService;

    @Autowired
    private WorkerAttendanceService workerAttendanceService;

    @Autowired
    private WorkerQueryService workerQueryService;

    @Autowired
    private WorkerMessageService workerMessageService;

    @Autowired
    private WorkerCareerService workerCareerService;

    @Autowired
    private WorkerTrainingService workerTrainingService;

    @Autowired
    private WorkerProfileService workerProfileService;

    @Autowired
    private WorkerContentService workerContentService;

    @GetMapping("/dashboard")
    public AjaxResult dashboard()
    {
        return AjaxResult.success(appScreenService.getDashboard(currentUser(), currentWorker()));
    }

    @PostMapping("/face-auth/mock")
    @Anonymous
    public AjaxResult faceAuthMock(@RequestBody(required = false) AppScreenFaceAuthRequest request)
    {
        return AjaxResult.success(appScreenService.faceAuth(request));
    }

    @GetMapping("/device/dashboard")
    public AjaxResult deviceDashboard()
    {
        return AjaxResult.success(appScreenService.getDeviceDashboard(currentUser(), currentWorker()));
    }

    @PostMapping("/device/save-config")
    public AjaxResult saveDeviceConfig(@RequestBody(required = false) AppScreenDeviceConfigRequest request)
    {
        return AjaxResult.success(appScreenService.saveDeviceConfig(currentUser(), currentWorker(), request));
    }

    @PostMapping("/device/restart")
    public AjaxResult restartDevice()
    {
        return AjaxResult.success(appScreenService.restartDevice(currentUser(), currentWorker()));
    }

    @PostMapping("/device/shutdown")
    public AjaxResult shutdownDevice()
    {
        return AjaxResult.success(appScreenService.shutdownDevice(currentUser(), currentWorker()));
    }

    @PostMapping("/device/export-log")
    public AjaxResult exportDeviceLog()
    {
        return AjaxResult.success(appScreenService.exportDeviceLog(currentUser(), currentWorker()));
    }

    @GetMapping("/home")
    public AjaxResult home()
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerHomeService.getHomeView(currentWorker(), user.getUserId()));
    }

    @GetMapping("/profile")
    public AjaxResult profile()
    {
        SysUser user = currentUser();
        return AjaxResult.success(
            workerHomeService.getProfile(currentWorker(), user.getUserId(), user.getUserName(), user.getNickName()));
    }

    @GetMapping("/workbench")
    public AjaxResult workbench()
    {
        SysUser user = currentUser();
        return AjaxResult.success(
            workerHomeService.getWorkbenchView(currentWorker(), user.getUserId(), user.getUserName(), user.getNickName()));
    }

    @PostMapping("/attendance/check-in")
    public AjaxResult checkIn(@Validated @RequestBody(required = false) WorkerAttendanceCheckRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerAttendanceService.checkIn(currentWorker(), user.getUserName(), request));
    }

    @PostMapping("/attendance/check-out")
    public AjaxResult checkOut(@Validated @RequestBody(required = false) WorkerAttendanceCheckRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerAttendanceService.checkOut(currentWorker(), user.getUserName(), request));
    }

    @GetMapping("/attendance/monthly")
    public AjaxResult attendanceMonthly(@RequestParam(required = false) String month)
    {
        return AjaxResult.success(workerAttendanceService.getMonthlyView(currentWorker(), month));
    }

    @GetMapping("/attendance/day")
    public AjaxResult attendanceDay(@RequestParam String date)
    {
        return AjaxResult.success(workerAttendanceService.getDayDetail(currentWorker(), date));
    }

    @GetMapping("/salary/list")
    public AjaxResult salaryList(@RequestParam(required = false) String year)
    {
        return AjaxResult.success(workerQueryService.getSalaryList(currentWorker(), year));
    }

    @GetMapping("/salary/detail")
    public AjaxResult salaryDetail(@RequestParam String month)
    {
        return AjaxResult.success(workerQueryService.getSalaryDetail(currentWorker(), month));
    }

    @GetMapping({ "/social-security/list", "/social/list" })
    public AjaxResult socialSecurityList(@RequestParam(required = false) String year)
    {
        return AjaxResult.success(workerQueryService.getSocialSecurityList(currentWorker(), year));
    }

    @GetMapping({ "/social-security/detail", "/social/detail" })
    public AjaxResult socialSecurityDetail(@RequestParam String month)
    {
        return AjaxResult.success(workerQueryService.getSocialSecurityDetail(currentWorker(), month));
    }

    @GetMapping("/tax/list")
    public AjaxResult taxList(@RequestParam(required = false) String year)
    {
        return AjaxResult.success(workerQueryService.getTaxList(currentWorker(), year));
    }

    @GetMapping("/tax/detail")
    public AjaxResult taxDetail(@RequestParam String month)
    {
        return AjaxResult.success(workerQueryService.getTaxDetail(currentWorker(), month));
    }

    @PostMapping("/complaint/create")
    public AjaxResult createComplaint(@RequestBody(required = false) WorkerComplaintCreateRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.createComplaint(currentWorker(), user, request));
    }

    @GetMapping("/complaint/list")
    public AjaxResult complaintList(@RequestParam(required = false) String status)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.listComplaints(user.getUserId(), status));
    }

    @GetMapping("/complaint/detail")
    public AjaxResult complaintDetail(@RequestParam Long complaintId)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.getComplaintDetail(user.getUserId(), complaintId));
    }

    @PostMapping("/legal-consult/create")
    public AjaxResult createLegalConsult(@RequestBody(required = false) WorkerLegalConsultCreateRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.createLegalConsult(currentWorker(), user, request));
    }

    @GetMapping("/legal-consult/list")
    public AjaxResult legalConsultList(@RequestParam(required = false) String status)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.listLegalConsults(user.getUserId(), status));
    }

    @GetMapping("/legal-consult/detail")
    public AjaxResult legalConsultDetail(@RequestParam Long consultId)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.getLegalConsultDetail(user.getUserId(), consultId));
    }

    @GetMapping("/legal-consult/hotline")
    public AjaxResult legalHotline()
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

    @GetMapping("/notice/list")
    public AjaxResult noticeList(@RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.getNoticeList(user.getUserId(), pageNum, pageSize));
    }

    @GetMapping("/notice/detail")
    public AjaxResult noticeDetail(@RequestParam Long noticeId)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerMessageService.getNoticeDetail(user.getUserId(), noticeId));
    }

    @PostMapping("/notice/read")
    public AjaxResult markNoticeRead(@RequestParam Long noticeId)
    {
        SysUser user = currentUser();
        workerMessageService.markNoticeRead(user.getUserId(), noticeId);
        return AjaxResult.success();
    }

    @GetMapping("/job/list")
    public AjaxResult jobList(@RequestParam(required = false) String keyword,
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) BigDecimal salaryMin,
        @RequestParam(required = false) BigDecimal salaryMax,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) Double radiusKm)
    {
        SysUser user = currentUser();
        return AjaxResult.success(
            workerCareerService.getJobList(currentWorker(), user.getUserId(), keyword, jobType, salaryMin, salaryMax,
                latitude, longitude, radiusKm));
    }

    @GetMapping("/job/map-config")
    public AjaxResult nearbyJobMapConfig(@RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude)
    {
        return AjaxResult.success(workerCareerService.getNearbyJobMapConfig(currentWorker(), latitude, longitude));
    }

    @GetMapping("/job/nearby")
    public AjaxResult nearbyJobList(@RequestParam(required = false) String keyword,
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) BigDecimal salaryMin,
        @RequestParam(required = false) BigDecimal salaryMax,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) Double radiusKm)
    {
        SysUser user = currentUser();
        return AjaxResult.success(
            workerCareerService.getNearbyJobList(currentWorker(), user.getUserId(), keyword, jobType, salaryMin, salaryMax,
                latitude, longitude, radiusKm));
    }

    @GetMapping("/job/detail")
    public AjaxResult jobDetail(@RequestParam Long jobId)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerCareerService.getJobDetail(currentWorker(), user.getUserId(), jobId));
    }

    @PostMapping("/job/apply")
    public AjaxResult applyJob(@RequestParam Long jobId)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerCareerService.applyJob(currentWorker(), user, jobId));
    }

    @GetMapping("/job/apply/list")
    public AjaxResult jobApplyList()
    {
        return AjaxResult.success(workerCareerService.getJobApplyList(currentUser().getUserId()));
    }

    @GetMapping("/training/progress")
    public AjaxResult trainingProgress()
    {
        return AjaxResult.success(workerTrainingService.getProgress(currentWorker()));
    }

    @GetMapping("/training/questions")
    public AjaxResult trainingQuestions()
    {
        return AjaxResult.success(workerTrainingService.listQuestions(currentWorker()));
    }

    @PostMapping("/training/answer")
    public AjaxResult answerTrainingQuestion(@Validated @RequestBody WorkerTrainingAnswerRequest request)
    {
        return AjaxResult.success(
            workerTrainingService.answerQuestion(currentWorker(), request.getQuestionId(), request.getAnswerIndex()));
    }

    @GetMapping("/training/history")
    public AjaxResult trainingHistory()
    {
        return AjaxResult.success(workerTrainingService.getHistory(currentWorker()));
    }

    @GetMapping("/training/history-detail")
    public AjaxResult trainingHistoryDetail(@RequestParam String month)
    {
        return AjaxResult.success(workerTrainingService.getHistoryDetail(currentWorker(), month));
    }

    @GetMapping("/training/courses")
    public AjaxResult trainingCourses()
    {
        return AjaxResult.success(workerTrainingService.getCourses(currentWorker()));
    }

    @GetMapping("/training/course-detail")
    public AjaxResult trainingCourseDetail(@RequestParam String courseKey)
    {
        return AjaxResult.success(workerTrainingService.getCourseDetail(currentWorker(), courseKey));
    }

    @PostMapping("/training/study-progress")
    public AjaxResult saveTrainingStudyProgress(@RequestParam String courseKey, @RequestParam Integer studiedSeconds)
    {
        return AjaxResult.success(workerTrainingService.saveStudyProgress(currentWorker(), courseKey, studiedSeconds));
    }

    @GetMapping("/resume/detail")
    public AjaxResult resumeDetail()
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.getResume(currentWorker(), user.getUserId()));
    }

    @PostMapping("/resume/save")
    public AjaxResult saveResume(@RequestBody(required = false) WorkerResumeSaveRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.saveResume(currentWorker(), user, request));
    }

    @GetMapping("/labor-contract/list")
    public AjaxResult laborContractList()
    {
        return AjaxResult.success(workerProfileService.getLaborContractList(currentWorker()));
    }

    @GetMapping("/labor-contract/detail")
    public AjaxResult laborContractDetail(@RequestParam Long contractId)
    {
        return AjaxResult.success(workerProfileService.getLaborContractDetail(currentWorker(), contractId));
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
        SysUser user = currentUser();
        return AjaxResult.success(
            workerProfileService.getRealnameDetail(currentWorker(), user.getUserId(), user.getUserName()));
    }

    @PostMapping("/real-name/submit")
    public AjaxResult submitRealname(@RequestBody(required = false) WorkerRealnameSubmitRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.submitRealnameApply(currentWorker(), user, request));
    }

    @PostMapping("/feedback/create")
    public AjaxResult createFeedback(@RequestBody(required = false) WorkerFeedbackCreateRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.createFeedback(currentWorker(), user, request));
    }

    @GetMapping("/settings/detail")
    public AjaxResult settingsDetail()
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.getSettings(currentWorker(), user.getUserId()));
    }

    @PostMapping("/settings/save")
    public AjaxResult saveSettings(@RequestBody(required = false) WorkerSettingSaveRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.saveSettings(currentWorker(), user, request));
    }

    @PostMapping("/settings/push-register")
    public AjaxResult registerPush(@RequestBody(required = false) WorkerPushRegisterRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.registerPush(currentWorker(), user, request));
    }

    @PostMapping("/settings/push-test")
    public AjaxResult sendPushTest(@RequestBody(required = false) WorkerPushTestRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.sendPushTest(currentWorker(), user, request));
    }

    @GetMapping("/points/account")
    public AjaxResult pointAccount()
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.getPointAccount(currentWorker(), user.getUserId()));
    }

    @PostMapping("/points/exchange")
    public AjaxResult exchangePointGoods(@RequestBody(required = false) WorkerPointExchangeRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.exchangePointGoods(currentWorker(), user, request));
    }

    @GetMapping("/insurance/security")
    public AjaxResult insuranceSecurity()
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.getInsuranceSecurity(currentWorker(), user.getUserId()));
    }

    @PostMapping("/upload-record/create")
    public AjaxResult createUploadRecord(@RequestBody(required = false) WorkerUploadRecordSaveRequest request)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerProfileService.createUploadRecord(currentWorker(), user, request));
    }

    @GetMapping("/upload-record/list")
    public AjaxResult uploadRecordList(@RequestParam(required = false) String categoryCode)
    {
        return AjaxResult.success(workerProfileService.getUploadRecordList(currentUser().getUserId(), categoryCode));
    }

    @GetMapping("/activity/detail")
    public AjaxResult activityDetail()
    {
        return AjaxResult.success(workerContentService.getActivityDetail(currentUser().getUserId()));
    }

    @PostMapping("/activity/join")
    public AjaxResult joinActivity(@RequestParam String activityKey)
    {
        SysUser user = currentUser();
        return AjaxResult.success(workerContentService.joinActivity(currentWorker(), user, activityKey));
    }

    @GetMapping("/activity/join-list")
    public AjaxResult activityJoinList()
    {
        return AjaxResult.success(workerContentService.getActivityJoinList(currentUser().getUserId()));
    }

    @GetMapping("/video/list")
    public AjaxResult videoList()
    {
        return AjaxResult.success(workerContentService.getVideoList(currentUser().getUserId()));
    }

    @GetMapping("/video/detail")
    public AjaxResult videoDetail(@RequestParam String videoKey)
    {
        return AjaxResult.success(workerContentService.getVideoDetail(currentUser().getUserId(), videoKey));
    }

    @PostMapping("/video/progress")
    public AjaxResult saveVideoProgress(@RequestParam String videoKey, @RequestParam Integer watchedSeconds,
        @RequestParam(required = false) Integer totalSeconds)
    {
        SysUser user = currentUser();
        return AjaxResult.success(
            workerContentService.saveVideoProgress(currentWorker(), user.getUserId(), videoKey, watchedSeconds, totalSeconds));
    }

    @GetMapping("/ai-training/detail")
    public AjaxResult aiTrainingDetail()
    {
        return AjaxResult.success(workerContentService.getAiTrainingDetail(currentWorker()));
    }

    @GetMapping({ "/union-service/home", "/union/home" })
    public AjaxResult unionServiceHome()
    {
        return AjaxResult.success(workerMessageService.getUnionServiceHome());
    }

    @GetMapping({ "/union-service/cases", "/union/cases" })
    public AjaxResult unionCaseList()
    {
        return AjaxResult.success(workerMessageService.getUnionCaseList());
    }

    @GetMapping({ "/union-service/notices", "/union/notices" })
    public AjaxResult unionNoticeList()
    {
        return AjaxResult.success(workerMessageService.getUnionNoticeList());
    }

    @GetMapping({ "/union-service/case-detail", "/union/case-detail" })
    public AjaxResult unionCaseDetail(@RequestParam String caseKey)
    {
        return AjaxResult.success(workerMessageService.getUnionCaseDetail(caseKey));
    }

    @GetMapping({ "/union-service/notice-detail", "/union/notice-detail" })
    public AjaxResult unionNoticeDetail(@RequestParam String noticeKey)
    {
        return AjaxResult.success(workerMessageService.getUnionNoticeDetail(noticeKey));
    }

    @GetMapping({ "/union-service/contracts", "/union/contracts" })
    public AjaxResult unionContractList()
    {
        return AjaxResult.success(workerMessageService.getUnionContractList());
    }

    @GetMapping({ "/union-service/contract-detail", "/union/contract-detail" })
    public AjaxResult unionContractDetail(@RequestParam String contractKey)
    {
        return AjaxResult.success(workerMessageService.getUnionContractDetail(contractKey));
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
