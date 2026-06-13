package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.system.domain.SysNotice;
import com.yuegongbao.system.service.ISysNoticeReadService;
import com.yuegongbao.system.service.ISysNoticeService;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.domain.vo.YgbUnionSyncResponse;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.integration.UnionAidClient;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import com.yuegongbao.ygb.worker.domain.WorkerNoticeMessage;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.WorkerSetting;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerComplaintMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerLegalConsultMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerNoticeMessageMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerPushGatewayService;

@Service
public class WorkerMessageServiceImpl implements WorkerMessageService
{
    private static final String PORTAL_CODE = "ygb";

    private static final String UNION_SECTION = "union";

    private static final String WORKER_NOTICE_OPERATOR = "worker-app";

    private static final String HOTLINE_NUMBER = "12351";

    private static final String HOTLINE_TEXT = "工会服务热线 12351";

    private static final String INSURANCE_ARREARS_TITLE = "保险欠费提醒";

    private static final String INSURANCE_ARREARS_ACTION = "查看社保";

    private static final String CERTIFICATE_REMINDER_TITLE = "证书到期提醒";

    private static final String CERTIFICATE_REMINDER_ACTION = "查看证书";

    private static final DateTimeFormatter REMINDER_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int REMARK_MAX_LENGTH = 255;

    @Autowired
    private WorkerComplaintMapper workerComplaintMapper;

    @Autowired
    private WorkerLegalConsultMapper workerLegalConsultMapper;

    @Autowired
    private WorkerNoticeMessageMapper workerNoticeMessageMapper;

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Autowired
    private ISysNoticeService sysNoticeService;

    @Autowired
    private ISysNoticeReadService sysNoticeReadService;

    @Autowired
    private WorkerPushGatewayService workerPushGatewayService;

    @Autowired
    private YgbPortalContentMapper portalContentMapper;

    @Autowired
    private UnionAidClient unionAidClient;

    @Override
    public Map<String, Object> createComplaint(YgbPerson worker, SysUser user, WorkerComplaintCreateRequest request)
    {
        validateComplaintRequest(request);
        WorkerComplaint complaint = new WorkerComplaint();
        complaint.setUserId(user.getUserId());
        complaint.setPersonId(worker.getPersonId());
        complaint.setPersonName(worker.getPersonName());
        complaint.setEnterpriseId(worker.getEnterpriseId());
        complaint.setEnterpriseName(worker.getEnterpriseName());
        complaint.setComplaintType(request.getComplaintType());
        complaint.setTitle(request.getTitle());
        complaint.setContent(request.getContent());
        complaint.setContactMobile(firstNonBlank(request.getContactMobile(), worker.getMobile(), user.getPhonenumber()));
        complaint.setAnonymousFlag(booleanFlag(request.getAnonymous()));
        complaint.setSyncUnionFlag(booleanFlag(request.getSyncUnion()));
        complaint.setAttachments(normalizeAttachmentText(request.getAttachments()));
        complaint.setStatus("0");
        complaint.setCreateBy(user.getUserName());
        workerComplaintMapper.insertWorkerComplaint(complaint);
        syncComplaintToUnion(complaint, user.getUserName());

        String title = "投诉提交成功";
        String content = "您的投诉/举报已提交成功，可在平台持续跟踪处理进度。";
        WorkerNoticeMessage message = buildWorkerNoticeMessage(user.getUserId(), worker.getPersonId(), worker.getPersonName(),
            title, content, "complaint", String.valueOf(complaint.getComplaintId()), "/pages/complaint/detail",
            Map.of("complaintId", complaint.getComplaintId()), "查看投诉", title, user.getUserName());
        workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

        boolean pushTriggered = sendWorkerPush(workerProfileMapper.selectWorkerSetting(user.getUserId()), title, content,
            pushPayload("/pages/complaint/detail", Map.of("complaintId", complaint.getComplaintId()), "查看投诉", title),
            metadata("worker-complaint-created", user.getUserId(), worker.getPersonId(), Map.of(
                "complaintId", complaint.getComplaintId(),
                "complaintType", firstNonBlank(complaint.getComplaintType(), ""))));

        Map<String, Object> result = complaintDetailMap(complaint);
        result.put("pushTriggered", pushTriggered);
        return result;
    }

    @Override
    public Map<String, Object> listComplaints(Long userId, String status)
    {
        List<WorkerComplaint> rows = workerComplaintMapper.selectWorkerComplaintList(userId, status);
        List<Map<String, Object>> resultRows = new ArrayList<>();
        for (WorkerComplaint item : rows)
        {
            resultRows.add(complaintSummaryMap(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", resultRows);
        result.put("total", resultRows.size());
        return result;
    }

    @Override
    public List<WorkerComplaint> listComplaintManageRecords(WorkerComplaint query)
    {
        return workerComplaintMapper.selectWorkerComplaintManageList(query == null ? new WorkerComplaint() : query);
    }

    @Override
    public Map<String, Object> getComplaintDetail(Long userId, Long complaintId)
    {
        WorkerComplaint complaint = workerComplaintMapper.selectWorkerComplaintById(complaintId, userId);
        if (complaint == null)
        {
            throw new ServiceException("投诉记录不存在");
        }
        return complaintDetailMap(complaint);
    }

    @Override
    public WorkerComplaint getComplaintManageDetail(Long complaintId)
    {
        WorkerComplaint complaint = workerComplaintMapper.selectWorkerComplaintByIdForManage(complaintId);
        if (complaint == null)
        {
            throw new ServiceException("投诉记录不存在");
        }
        return complaint;
    }

    @Override
    public Map<String, Object> updateComplaintHandle(Long complaintId, WorkerMessageHandleRequest request, String operator)
    {
        validateHandleRequest(request);
        WorkerComplaint before = workerComplaintMapper.selectWorkerComplaintByIdForManage(complaintId);
        if (before == null)
        {
            throw new ServiceException("投诉记录不存在");
        }

        WorkerComplaint target = new WorkerComplaint();
        target.setComplaintId(complaintId);
        target.setStatus(request.getStatus());
        target.setReplyContent(request.getReplyContent());
        target.setHandleTimeText(request.getHandleTimeText());
        target.setUpdateBy(operator);
        int rows = workerComplaintMapper.updateWorkerComplaintHandle(target);
        WorkerComplaint after = workerComplaintMapper.selectWorkerComplaintByIdForManage(complaintId);
        if (after == null)
        {
            after = before;
        }

        boolean messageTriggered = rows > 0 && complaintChanged(before, after);
        boolean pushTriggered = false;
        if (messageTriggered)
        {
            String title = "投诉处理结果更新";
            String content = firstNonBlank(after.getReplyContent(), "您的投诉处理结果已更新，请及时查看平台反馈。");
            WorkerNoticeMessage message = buildWorkerNoticeMessage(after.getUserId(), after.getPersonId(), after.getPersonName(),
                title, content, "complaint", String.valueOf(after.getComplaintId()), "/pages/complaint/detail",
                Map.of("complaintId", after.getComplaintId()), "查看投诉", "投诉处理结果", operator);
            workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

            pushTriggered = sendWorkerPush(workerProfileMapper.selectWorkerSetting(after.getUserId()), title, content,
                pushPayload("/pages/complaint/detail", Map.of("complaintId", after.getComplaintId()), "查看投诉", "投诉处理结果"),
                metadata("worker-complaint-handle-updated", after.getUserId(), after.getPersonId(), Map.of(
                    "complaintId", after.getComplaintId(),
                    "status", firstNonBlank(after.getStatus(), ""))));
        }

        Map<String, Object> result = complaintDetailMap(after);
        result.put("messageTriggered", messageTriggered);
        result.put("pushTriggered", pushTriggered);
        return result;
    }

    @Override
    public Map<String, Object> createLegalConsult(YgbPerson worker, SysUser user, WorkerLegalConsultCreateRequest request)
    {
        validateLegalConsultRequest(request);
        WorkerLegalConsult consult = new WorkerLegalConsult();
        consult.setUserId(user.getUserId());
        consult.setPersonId(worker.getPersonId());
        consult.setPersonName(worker.getPersonName());
        consult.setEnterpriseId(worker.getEnterpriseId());
        consult.setEnterpriseName(worker.getEnterpriseName());
        consult.setConsultType(request.getConsultType());
        consult.setTitle(request.getTitle());
        consult.setContent(request.getContent());
        consult.setContactMobile(firstNonBlank(request.getContactMobile(), worker.getMobile(), user.getPhonenumber()));
        consult.setAttachments(normalizeAttachmentText(request.getAttachments()));
        consult.setStatus("0");
        consult.setCreateBy(user.getUserName());
        workerLegalConsultMapper.insertWorkerLegalConsult(consult);
        syncLegalConsultToUnion(consult, user.getUserName());

        String title = "法律咨询提交成功";
        String content = "您的法律咨询已提交成功，可在平台查看状态和回复进展。";
        WorkerNoticeMessage message = buildWorkerNoticeMessage(user.getUserId(), worker.getPersonId(), worker.getPersonName(),
            title, content, "legal-consult", String.valueOf(consult.getConsultId()), "/pages/legal/detail",
            Map.of("consultId", consult.getConsultId()), "查看咨询", title, user.getUserName());
        workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

        boolean pushTriggered = sendWorkerPush(workerProfileMapper.selectWorkerSetting(user.getUserId()), title, content,
            pushPayload("/pages/legal/detail", Map.of("consultId", consult.getConsultId()), "查看咨询", title),
            metadata("worker-legal-consult-created", user.getUserId(), worker.getPersonId(), Map.of(
                "consultId", consult.getConsultId(),
                "consultType", firstNonBlank(consult.getConsultType(), ""))));

        Map<String, Object> result = legalConsultDetailMap(consult);
        result.put("pushTriggered", pushTriggered);
        return result;
    }

    @Override
    public Map<String, Object> listLegalConsults(Long userId, String status)
    {
        List<WorkerLegalConsult> rows = workerLegalConsultMapper.selectWorkerLegalConsultList(userId, status);
        List<Map<String, Object>> resultRows = new ArrayList<>();
        for (WorkerLegalConsult item : rows)
        {
            resultRows.add(legalConsultSummaryMap(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", resultRows);
        result.put("total", resultRows.size());
        return result;
    }

    @Override
    public List<WorkerLegalConsult> listLegalConsultManageRecords(WorkerLegalConsult query)
    {
        return workerLegalConsultMapper.selectWorkerLegalConsultManageList(query == null ? new WorkerLegalConsult() : query);
    }

    @Override
    public Map<String, Object> getLegalConsultDetail(Long userId, Long consultId)
    {
        WorkerLegalConsult consult = workerLegalConsultMapper.selectWorkerLegalConsultById(consultId, userId);
        if (consult == null)
        {
            throw new ServiceException("法律咨询记录不存在");
        }
        return legalConsultDetailMap(consult);
    }

    @Override
    public WorkerLegalConsult getLegalConsultManageDetail(Long consultId)
    {
        WorkerLegalConsult consult = workerLegalConsultMapper.selectWorkerLegalConsultByIdForManage(consultId);
        if (consult == null)
        {
            throw new ServiceException("法律咨询记录不存在");
        }
        return consult;
    }

    @Override
    public Map<String, Object> updateLegalConsultHandle(Long consultId, WorkerMessageHandleRequest request, String operator)
    {
        validateHandleRequest(request);
        WorkerLegalConsult before = workerLegalConsultMapper.selectWorkerLegalConsultByIdForManage(consultId);
        if (before == null)
        {
            throw new ServiceException("法律咨询记录不存在");
        }

        WorkerLegalConsult target = new WorkerLegalConsult();
        target.setConsultId(consultId);
        target.setStatus(request.getStatus());
        target.setReplyContent(request.getReplyContent());
        target.setReplyTimeText(request.getHandleTimeText());
        target.setUpdateBy(operator);
        int rows = workerLegalConsultMapper.updateWorkerLegalConsultHandle(target);
        WorkerLegalConsult after = workerLegalConsultMapper.selectWorkerLegalConsultByIdForManage(consultId);
        if (after == null)
        {
            after = before;
        }

        boolean messageTriggered = rows > 0 && legalConsultChanged(before, after);
        boolean pushTriggered = false;
        if (messageTriggered)
        {
            String title = "法律咨询处理结果更新";
            String content = firstNonBlank(after.getReplyContent(), "您的法律咨询处理结果已更新，请及时查看平台反馈。");
            WorkerNoticeMessage message = buildWorkerNoticeMessage(after.getUserId(), after.getPersonId(), after.getPersonName(),
                title, content, "legal-consult", String.valueOf(after.getConsultId()), "/pages/legal/detail",
                Map.of("consultId", after.getConsultId()), "查看咨询", "法律咨询处理结果", operator);
            workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

            pushTriggered = sendWorkerPush(workerProfileMapper.selectWorkerSetting(after.getUserId()), title, content,
                pushPayload("/pages/legal/detail", Map.of("consultId", after.getConsultId()), "查看咨询", "法律咨询处理结果"),
                metadata("worker-legal-consult-handle-updated", after.getUserId(), after.getPersonId(), Map.of(
                    "consultId", after.getConsultId(),
                    "status", firstNonBlank(after.getStatus(), ""))));
        }

        Map<String, Object> result = legalConsultDetailMap(after);
        result.put("messageTriggered", messageTriggered);
        result.put("pushTriggered", pushTriggered);
        return result;
    }

    @Override
    public boolean notifyActivityHandleUpdated(WorkerActivityJoin before, WorkerActivityJoin after, String operator,
        String statusMessage)
    {
        if (after == null || after.getUserId() == null || after.getPersonId() == null)
        {
            return false;
        }
        String title = "福利活动发放结果更新";
        String content = firstNonBlank(statusMessage, "您的福利活动发放结果已更新，请查看活动记录。");
        WorkerNoticeMessage message = buildWorkerNoticeMessage(after.getUserId(), after.getPersonId(), after.getPersonName(),
            title, content, "activity", String.valueOf(after.getJoinId()), "/pages/activity/join-list",
            Map.of("activityKey", firstNonBlank(after.getActivityKey(), ""), "joinId", after.getJoinId()),
            "查看活动记录", "福利活动发放结果", operator);
        workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

        return sendWorkerPush(workerProfileMapper.selectWorkerSetting(after.getUserId()), title, content,
            pushPayload("/pages/activity/join-list",
                Map.of("activityKey", firstNonBlank(after.getActivityKey(), ""), "joinId", after.getJoinId()),
                "查看活动记录", "福利活动发放结果"),
            metadata("worker-activity-handle-updated", after.getUserId(), after.getPersonId(), Map.of(
                "joinId", after.getJoinId(),
                "activityKey", firstNonBlank(after.getActivityKey(), ""),
                "status", firstNonBlank(after.getStatus(), ""))));
    }

    @Override
    public int notifySalaryBatchSubmitted(YgbSalaryBatch batch, List<YgbSalaryDetail> detailList, String operator)
    {
        if (batch == null || detailList == null || detailList.isEmpty())
        {
            return 0;
        }
        int pushedCount = 0;
        for (YgbSalaryDetail detail : detailList)
        {
            if (detail == null || detail.getPersonId() == null || !"0".equals(detail.getPayStatus()))
            {
                continue;
            }
            WorkerSetting setting = workerProfileMapper.selectWorkerSettingByPersonId(detail.getPersonId());
            if (setting == null || setting.getUserId() == null)
            {
                continue;
            }
            String amountText = defaultAmount(detail.getNetAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
            String content = firstNonBlank(batch.getStatMonth(), "") + " 工资已发放到账，实发金额 " + amountText + " 元。";
            WorkerNoticeMessage message = buildWorkerNoticeMessage(setting.getUserId(), detail.getPersonId(), detail.getPersonName(),
                "工资发放提醒", content, "salary", String.valueOf(detail.getDetailId()), "/pages/salary/list",
                Map.of("from", "notice", "month", firstNonBlank(batch.getStatMonth(), "")),
                "查看工资", "工资发放提醒", operator);
            workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

            boolean pushed = sendWorkerPush(setting, "工资发放提醒", content,
                pushPayload("/pages/salary/list", Map.of("from", "notice", "month", firstNonBlank(batch.getStatMonth(), "")),
                    "查看工资", "工资发放提醒"),
                metadata("worker-salary-batch-submitted", setting.getUserId(), detail.getPersonId(), Map.of(
                    "batchId", batch.getBatchId(),
                    "batchNo", firstNonBlank(batch.getBatchNo(), ""),
                    "detailId", detail.getDetailId(),
                    "statMonth", firstNonBlank(batch.getStatMonth(), ""))));
            if (pushed)
            {
                pushedCount++;
            }
        }
        return pushedCount;
    }

    @Override
    public boolean notifyTrainingLockReminder(YgbPerson worker, Long userId, WorkerTrainingProgress progress, String source)
    {
        if (worker == null || userId == null || progress == null || !progress.isNeedComplete())
        {
            return false;
        }
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(userId);
        String bizId = worker.getPersonId() + ":" + LocalDate.now().format(REMINDER_MONTH_FORMATTER);
        if (workerNoticeMessageMapper.selectLatestWorkerNoticeMessageByBiz(userId, "training-lock", bizId) != null)
        {
            return false;
        }
        String content = "您本月安全培训进度为 " + progress.getCompleted() + "/" + progress.getTotal()
            + "，请尽快完成培训后解锁打卡与工资查询。";
        WorkerNoticeMessage message = buildWorkerNoticeMessage(userId, worker.getPersonId(), worker.getPersonName(),
            "本月培训未完成提醒", content, "training-lock", bizId, "/pages/training/index",
            Map.of("from", "notice", "scene", "month-lock"), "去完成培训", "本月培训未完成提醒", source);
        workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

        sendWorkerPush(setting, "本月培训未完成提醒", content,
            pushPayload("/pages/training/index", Map.of("from", "notice", "scene", "month-lock"), "去完成培训",
                "本月培训未完成提醒"),
            metadata("worker-training-lock-reminder", userId, worker.getPersonId(), Map.of(
                "completed", progress.getCompleted(),
                "total", progress.getTotal())));
        return true;
    }

    @Override
    public boolean notifyInsuranceArrearsReminder(YgbPerson worker, Long userId, String insuranceStatus,
        String latestArrearsMonth, String source)
    {
        if (worker == null || userId == null)
        {
            return false;
        }
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(userId);
        boolean needNotify = "0".equals(insuranceStatus) || "2".equals(insuranceStatus)
            || StringUtils.isNotEmpty(latestArrearsMonth);
        if (!needNotify)
        {
            return false;
        }
        String bizId = worker.getPersonId() + ":" + LocalDate.now().format(REMINDER_MONTH_FORMATTER);
        if (workerNoticeMessageMapper.selectLatestWorkerNoticeMessageByBiz(userId, "insurance-arrears", bizId) != null)
        {
            return false;
        }
        String monthText = firstNonBlank(latestArrearsMonth, "本月");
        String content = monthText + " 存在社保欠费或断缴情况，请尽快核查并关注补缴进度。";
        WorkerNoticeMessage message = buildWorkerNoticeMessage(userId, worker.getPersonId(), worker.getPersonName(),
            INSURANCE_ARREARS_TITLE, content, "insurance-arrears", bizId, "/pages/social/list",
            Map.of("from", "notice", "scene", "arrears"), INSURANCE_ARREARS_ACTION, INSURANCE_ARREARS_TITLE, source);
        workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

        sendWorkerPush(setting, INSURANCE_ARREARS_TITLE, content,
            pushPayload("/pages/social/list", Map.of("from", "notice", "scene", "arrears"), INSURANCE_ARREARS_ACTION,
                INSURANCE_ARREARS_TITLE),
            metadata("worker-insurance-arrears-reminder", userId, worker.getPersonId(), Map.of(
                "insuranceStatus", firstNonBlank(insuranceStatus, ""),
                "latestArrearsMonth", firstNonBlank(latestArrearsMonth, ""))));
        return true;
    }

    @Override
    public boolean notifyCertificateReminder(YgbPerson worker, Long userId, WorkerResume resume, String source)
    {
        if (worker == null || userId == null)
        {
            return false;
        }
        String certStatus = firstNonBlank(worker.getCertStatus(), "0");
        if (!"2".equals(certStatus) && !"3".equals(certStatus))
        {
            return false;
        }
        List<Map<String, Object>> certificateList = parseCertificateList(resume == null ? null : resume.getCertificateText());
        String certificateSummary = firstCertificateSummary(certificateList,
            "3".equals(certStatus) ? "证书已过期" : "证书即将到期");
        String bizId = worker.getPersonId() + ":" + certStatus + ":" + LocalDate.now().format(REMINDER_MONTH_FORMATTER);
        if (workerNoticeMessageMapper.selectLatestWorkerNoticeMessageByBiz(userId, "certificate-reminder", bizId) != null)
        {
            return false;
        }
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(userId);
        String content = certificateSummary + "，请及时处理证书补办、续期或更新材料。";
        WorkerNoticeMessage message = buildWorkerNoticeMessage(userId, worker.getPersonId(), worker.getPersonName(),
            CERTIFICATE_REMINDER_TITLE, content, "certificate-reminder", bizId, "/pages/profile/resume",
            Map.of("from", "notice", "scene", "certificate", "status", certStatus), CERTIFICATE_REMINDER_ACTION,
            CERTIFICATE_REMINDER_TITLE, source);
        workerNoticeMessageMapper.insertWorkerNoticeMessage(message);

        sendWorkerPush(setting, CERTIFICATE_REMINDER_TITLE, content,
            pushPayload("/pages/profile/resume", Map.of("from", "notice", "scene", "certificate", "status", certStatus),
                CERTIFICATE_REMINDER_ACTION, CERTIFICATE_REMINDER_TITLE),
            metadata("worker-certificate-reminder", userId, worker.getPersonId(), Map.of(
                "certStatus", certStatus,
                "certificateCount", certificateList.size(),
                "certificateSummary", certificateSummary)));
        return true;
    }

    @Override
    public Map<String, Object> getNoticeList(Long userId, int pageNum, int pageSize)
    {
        int resolvedPageSize = pageSize <= 0 ? 10 : pageSize;
        List<Map<String, Object>> rows = new ArrayList<>();
        List<WorkerNoticeMessage> workerRows = workerNoticeMessageMapper.selectWorkerNoticeMessageList(userId, resolvedPageSize);
        for (WorkerNoticeMessage item : workerRows)
        {
            rows.add(workerNoticeRow(item));
        }
        List<SysNotice> sysRows = sysNoticeReadService.selectNoticeListWithReadStatus(userId, resolvedPageSize);
        for (SysNotice item : sysRows)
        {
            rows.add(sysNoticeRow(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        result.put("pageNum", pageNum);
        result.put("pageSize", resolvedPageSize);
        return result;
    }

    @Override
    public Map<String, Object> getNoticeDetail(Long userId, Long noticeId)
    {
        if (noticeId == null)
        {
            throw new ServiceException("?????????");
        }
        if (noticeId < 0)
        {
            Long messageId = Math.abs(noticeId);
            WorkerNoticeMessage message = workerNoticeMessageMapper.selectWorkerNoticeMessageById(messageId, userId);
            if (message == null)
            {
                throw new ServiceException("????????");
            }
            workerNoticeMessageMapper.markWorkerNoticeMessageRead(messageId, userId, WORKER_NOTICE_OPERATOR);
            return workerNoticeDetail(message);
        }

        SysNotice notice = sysNoticeService.selectNoticeById(noticeId);
        if (notice == null)
        {
            throw new ServiceException("????????");
        }
        sysNoticeReadService.markRead(noticeId, userId);
        return sysNoticeDetail(notice);
    }

    @Override
    public void markNoticeRead(Long userId, Long noticeId)
    {
        if (noticeId == null)
        {
            return;
        }
        if (noticeId < 0)
        {
            workerNoticeMessageMapper.markWorkerNoticeMessageRead(Math.abs(noticeId), userId, WORKER_NOTICE_OPERATOR);
            return;
        }
        sysNoticeReadService.markRead(noticeId, userId);
    }

    @Override
    public int getUnreadNoticeCount(Long userId)
    {
        return workerNoticeMessageMapper.countUnreadWorkerNoticeMessage(userId)
            + sysNoticeReadService.selectUnreadCount(userId);
    }

    @Override
    public Map<String, Object> getHotline()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("phoneNumber", HOTLINE_NUMBER);
        result.put("displayText", HOTLINE_TEXT);
        result.put("serviceTime", "闁诲氦顫夐幃鍫曞磿閹殿喚绀婇柡鍐ㄧ墕缁?09:00-18:00");
        return result;
    }

    @Override
    public Map<String, Object> getLegalArticleList()
    {
        List<YgbPortalContent> cmsRows = portalContentMapper.selectPublishedPortalContentList(PORTAL_CODE, UNION_SECTION, null,
            null, 20);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbPortalContent item : cmsRows)
        {
            if ("intro".equals(item.getCategoryCode()))
            {
                continue;
            }
            rows.add(articleSummary(item));
        }
        if (rows.isEmpty())
        {
            rows.add(simpleArticle("article-1", "??????", "????",
                "???????????????????????????????????"));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getLegalArticleDetail(String articleKey)
    {
        YgbPortalContent cms = resolveCmsContent(articleKey);
        if (cms != null)
        {
            return articleDetail(cms);
        }
        return detailWithParagraphs("??????", "????",
            Arrays.asList("???????????????????????????????????",
                "?????????????????????????????????"));
    }

    @Override
    public Map<String, Object> getLegalFaqList(String keyword)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(faq("faq-1", "???????", "????",
            Arrays.asList("?????????????????????", "????????????????????????????")));
        rows.add(faq("faq-2", "?????????", "????",
            Arrays.asList("?????????????", "??????????????????????????????")));
        rows.add(faq("faq-3", "????????", "????",
            Arrays.asList("??????????????????????", "???????????????????")));

        List<Map<String, Object>> filtered = new ArrayList<>();
        String normalizedKeyword = trimToEmpty(keyword);
        for (Map<String, Object> item : rows)
        {
            if (normalizedKeyword.isEmpty()
                || String.valueOf(item.get("title")).contains(normalizedKeyword)
                || String.valueOf(item.get("category")).contains(normalizedKeyword))
            {
                filtered.add(summaryFromFaq(item));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", filtered);
        result.put("total", filtered.size());
        return result;
    }

    @Override
    public Map<String, Object> getLegalFaqDetail(String faqKey)
    {
        Map<String, Object> faq = switch (trimToEmpty(faqKey))
        {
            case "faq-1" -> faq("faq-1", "???????", "????",
                Arrays.asList("?????????????????????", "????????????????????????????"));
            case "faq-2" -> faq("faq-2", "?????????", "????",
                Arrays.asList("?????????????", "??????????????????????????????"));
            case "faq-3" -> faq("faq-3", "????????", "????",
                Arrays.asList("??????????????????????", "???????????????????"));
            default -> null;
        };
        if (faq == null)
        {
            throw new ServiceException("??????????");
        }
        return faq;
    }

    @Override
    public Map<String, Object> getUnionServiceHome()
    {
        List<YgbPortalContent> cmsRows = portalContentMapper.selectPublishedPortalContentList(PORTAL_CODE, UNION_SECTION, null,
            null, 20);
        String hotline = HOTLINE_NUMBER;
        for (YgbPortalContent item : cmsRows)
        {
            JSONObject extra = parseObject(item.getExtraJson());
            String candidate = jsonString(extra, "hotline");
            if (StringUtils.isNotEmpty(candidate))
            {
                hotline = candidate;
                break;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hotline", Map.of("phoneNumber", hotline, "displayText", "闁诲氦顫夐幃鍫曞磿閹殿喗顫曢柡澶婄氨閺嬫牠鏌￠崶鈺佷户濞村吋鍔欓弻锟犲醇濠靛熆銏犫攽椤旀儳鏋涢柟顔界矒椤㈡﹢鎮╅崣澶屾瀮 " + hotline));
        result.put("quickActions", Arrays.asList(
            quickAction("union-legal", "??????", "/pages/legal/index"),
            quickAction("union-lecture", "????", "/pages/legal/article-list"),
            quickAction("union-contract", "??????", "/pages/union/contracts"),
            quickAction("union-notice", "????", "/pages/union/index")));
        result.put("contractList", getUnionContractList().get("rows"));
        return result;
    }

    @Override
    public Map<String, Object> getUnionCaseList()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<YgbPortalContent> cmsRows = portalContentMapper.selectPublishedPortalContentList(PORTAL_CODE, UNION_SECTION, "case",
            null, 20);
        for (YgbPortalContent item : cmsRows)
        {
            rows.add(caseSummary(item));
        }
        if (rows.isEmpty())
        {
            rows.add(caseSummary("case-1", "??????", "??????????????????????????"));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getUnionNoticeList()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<YgbPortalContent> cmsRows = portalContentMapper.selectPublishedPortalContentList(PORTAL_CODE, UNION_SECTION, "notice",
            null, 20);
        for (YgbPortalContent item : cmsRows)
        {
            rows.add(noticeSummary(item));
        }
        if (rows.isEmpty())
        {
            rows.add(noticeSummary("notice-1", "??????", "???????????????????????????????"));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getUnionCaseDetail(String caseKey)
    {
        YgbPortalContent cms = resolveCmsContent(caseKey);
        if (cms != null)
        {
            return detailWithParagraphs(firstNonBlank(cms.getTitle(), "-"), "", extractParagraphs(cms));
        }
        return detailWithParagraphs("??????", "", Arrays.asList(
            "?????????????????????????????????????",
            "?????????????????????????????????"));
    }

    @Override
    public Map<String, Object> getUnionNoticeDetail(String noticeKey)
    {
        YgbPortalContent cms = resolveCmsContent(noticeKey);
        if (cms != null)
        {
            return detailWithParagraphs(firstNonBlank(cms.getTitle(), "-"), "", extractParagraphs(cms));
        }
        return detailWithParagraphs("??????", "", Arrays.asList(
            "???????????????????????????????",
            "???????????????????????????????????"));
    }

    @Override
    public Map<String, Object> getUnionContractList()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<YgbPortalContent> cmsRows = portalContentMapper.selectPublishedPortalContentList(PORTAL_CODE, UNION_SECTION, "contract",
            null, 20);
        for (YgbPortalContent item : cmsRows)
        {
            rows.add(contractSummary(item));
        }
        if (rows.isEmpty())
        {
            rows.add(contractSummary("contract-1", "??????", "??????????", "2026-01-01 ? 2026-12-31",
                "??????????????????????????????"));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getUnionContractDetail(String contractKey)
    {
        YgbPortalContent cms = resolveCmsContent(contractKey);
        if (cms != null)
        {
            Map<String, Object> result = contractDetail(cms);
            result.put("clauses", extractParagraphs(cms));
            return result;
        }
        Map<String, Object> result = contractSummary("contract-1", "??????", "??????????", "2026-01-01 ? 2026-12-31",
            "??????????????????????????????");
        result.put("clauses", Arrays.asList("????????????????????????????", "????????????????????????????????"));
        return result;
    }

    private void validateComplaintRequest(WorkerComplaintCreateRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getTitle()) || StringUtils.isEmpty(request.getContent())
            || StringUtils.isEmpty(request.getComplaintType()))
        {
            throw new ServiceException("????????????????");
        }
    }

    private void validateLegalConsultRequest(WorkerLegalConsultCreateRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getTitle()) || StringUtils.isEmpty(request.getContent())
            || StringUtils.isEmpty(request.getConsultType()))
        {
            throw new ServiceException("????????????????");
        }
    }

    private void validateHandleRequest(WorkerMessageHandleRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getStatus()))
        {
            throw new ServiceException("?????????");
        }
    }

    private boolean complaintChanged(WorkerComplaint before, WorkerComplaint after)
    {
        return !sameText(before == null ? null : before.getStatus(), after == null ? null : after.getStatus())
            || !sameText(before == null ? null : before.getReplyContent(), after == null ? null : after.getReplyContent())
            || !sameText(before == null ? null : before.getHandleTimeText(), after == null ? null : after.getHandleTimeText());
    }

    private boolean legalConsultChanged(WorkerLegalConsult before, WorkerLegalConsult after)
    {
        return !sameText(before == null ? null : before.getStatus(), after == null ? null : after.getStatus())
            || !sameText(before == null ? null : before.getReplyContent(), after == null ? null : after.getReplyContent())
            || !sameText(before == null ? null : before.getReplyTimeText(), after == null ? null : after.getReplyTimeText());
    }

    private boolean sendWorkerPush(WorkerSetting setting, String title, String content, Map<String, Object> payload,
        Map<String, Object> metadata)
    {
        if (!pushAvailable(setting))
        {
            return false;
        }
        try
        {
            workerPushGatewayService.sendPush(setting.getPushClientId(), title, content, payload, metadata);
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private boolean pushAvailable(WorkerSetting setting)
    {
        return setting != null
            && "1".equals(firstNonBlank(setting.getNotifyEnabled(), "1"))
            && StringUtils.isNotEmpty(setting.getPushClientId());
    }

    private WorkerNoticeMessage buildWorkerNoticeMessage(Long userId, Long personId, String personName, String title,
        String content, String bizType, String bizId, String jumpPath, Map<String, Object> jumpQuery, String actionLabel,
        String sourceLabel, String createBy)
    {
        WorkerNoticeMessage message = new WorkerNoticeMessage();
        message.setUserId(userId);
        message.setPersonId(personId);
        message.setPersonName(personName);
        message.setMessageType("BUSINESS");
        message.setTitle(title);
        message.setSummary(content);
        message.setContent(content);
        message.setBizType(bizType);
        message.setBizId(bizId);
        message.setJumpPath(jumpPath);
        message.setJumpQueryText(JSON.toJSONString(jumpQuery == null ? Collections.emptyMap() : jumpQuery));
        message.setActionLabel(actionLabel);
        message.setSourceLabel(sourceLabel);
        message.setReadFlag("0");
        message.setCreateBy(firstNonBlank(createBy, "system"));
        return message;
    }

    private Map<String, Object> complaintSummaryMap(WorkerComplaint item)
    {
        Map<String, Object> result = complaintDetailMap(item);
        result.remove("content");
        result.remove("replyContent");
        return result;
    }

    private Map<String, Object> complaintDetailMap(WorkerComplaint item)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("complaintId", item.getComplaintId());
        result.put("complaintType", item.getComplaintType());
        result.put("title", item.getTitle());
        result.put("content", item.getContent());
        result.put("contactMobile", item.getContactMobile());
        result.put("attachments", firstNonBlank(item.getAttachments(), ""));
        result.put("status", item.getStatus());
        result.put("statusText", complaintStatusText(item.getStatus()));
        result.put("anonymousText", "1".equals(item.getAnonymousFlag()) ? "????" : "????");
        appendUnionSyncResult(result, "1".equals(item.getSyncUnionFlag()) || "2".equals(item.getSyncUnionFlag()), item.getRemark());
        result.put("replyContent", item.getReplyContent());
        result.put("handleTimeText", item.getHandleTimeText());
        result.put("createTime", formatDateTime(item.getCreateTime()));
        return result;
    }

    private Map<String, Object> legalConsultSummaryMap(WorkerLegalConsult item)
    {
        Map<String, Object> result = legalConsultDetailMap(item);
        result.remove("content");
        result.remove("replyContent");
        return result;
    }

    private Map<String, Object> legalConsultDetailMap(WorkerLegalConsult item)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("consultId", item.getConsultId());
        result.put("consultType", item.getConsultType());
        result.put("title", item.getTitle());
        result.put("content", item.getContent());
        result.put("contactMobile", item.getContactMobile());
        result.put("attachments", firstNonBlank(item.getAttachments(), ""));
        result.put("status", item.getStatus());
        result.put("statusText", legalConsultStatusText(item.getStatus()));
        appendUnionSyncResult(result, true, item.getRemark());
        result.put("replyContent", item.getReplyContent());
        result.put("replyTimeText", item.getReplyTimeText());
        result.put("createTime", formatDateTime(item.getCreateTime()));
        return result;
    }

    private List<Map<String, Object>> parseCertificateList(String certificateText)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (StringUtils.isEmpty(certificateText))
        {
            return rows;
        }
        try
        {
            JSONArray array = JSON.parseArray(certificateText);
            if (array == null)
            {
                return rows;
            }
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject item = array.getJSONObject(i);
                if (item == null)
                {
                    continue;
                }
                String certificateName = firstNonBlank(item.getString("certificateName"), item.getString("name"));
                if (StringUtils.isEmpty(certificateName))
                {
                    continue;
                }
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("certificateName", certificateName);
                row.put("certificateNo", firstNonBlank(item.getString("certificateNo"), item.getString("no"), ""));
                row.put("issuer", firstNonBlank(item.getString("issuer"), ""));
                row.put("expireDate", firstNonBlank(item.getString("expireDate"), ""));
                rows.add(row);
            }
            if (!rows.isEmpty())
            {
                return rows;
            }
        }
        catch (Exception ignored)
        {
        }
        for (String segment : certificateText.split("[,闂?闂備焦瀵х粙鎺楁儗閹旦\\n]+"))
        {
            String value = trimToEmpty(segment);
            if (value.isEmpty())
            {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("certificateName", value);
            row.put("certificateNo", "");
            row.put("issuer", "");
            row.put("expireDate", "");
            rows.add(row);
        }
        return rows;
    }

    private String firstCertificateSummary(List<Map<String, Object>> certificateList, String defaultText)
    {
        if (certificateList == null || certificateList.isEmpty())
        {
            return defaultText;
        }
        Map<String, Object> first = certificateList.get(0);
        String certificateName = firstNonBlank(String.valueOf(first.get("certificateName")), defaultText);
        String expireDate = firstNonBlank(String.valueOf(first.get("expireDate")), "");
        if (StringUtils.isEmpty(expireDate))
        {
            return certificateName + " " + defaultText;
        }
        return certificateName + " 证书有效期至 " + expireDate;
    }

    private String complaintStatusText(String status)
    {
        return switch (firstNonBlank(status, "0"))
        {
            case "1" -> "处理中";
            case "2" -> "已处理";
            default -> "待处理";
        };
    }

    private String legalConsultStatusText(String status)
    {
        return switch (firstNonBlank(status, "0"))
        {
            case "1" -> "已回复";
            case "2" -> "已办结";
            default -> "待回复";
        };
    }

    private Map<String, Object> workerNoticeRow(WorkerNoticeMessage item)
    {
        Map<String, Object> result = workerNoticeDetail(item);
        result.put("noticeId", item.getMessageId() == null ? null : -item.getMessageId());
        result.put("isRead", "1".equals(item.getReadFlag()));
        return result;
    }

    private Map<String, Object> workerNoticeDetail(WorkerNoticeMessage item)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("noticeId", item.getMessageId() == null ? null : -item.getMessageId());
        result.put("title", item.getTitle());
        result.put("summary", firstNonBlank(item.getSummary(), item.getContent(), ""));
        result.put("content", firstNonBlank(item.getContent(), item.getSummary(), ""));
        result.put("noticeType", item.getMessageType());
        result.put("publishTime", formatDateTime(item.getCreateTime()));
        result.put("sourceLabel", item.getSourceLabel());
        result.put("actionLabel", item.getActionLabel());
        result.put("jumpPath", item.getJumpPath());
        result.put("jumpQuery", parseMap(item.getJumpQueryText()));
        result.put("readFlag", item.getReadFlag());
        return result;
    }

    private Map<String, Object> sysNoticeRow(SysNotice item)
    {
        Map<String, Object> result = sysNoticeDetail(item);
        result.put("summary", firstNonBlank(item.getRemark(), item.getNoticeContent(), ""));
        result.put("isRead", item.getIsRead());
        result.put("readFlag", item.getIsRead() ? "1" : "0");
        return result;
    }

    private Map<String, Object> sysNoticeDetail(SysNotice item)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("noticeId", item.getNoticeId());
        result.put("title", item.getNoticeTitle());
        result.put("summary", firstNonBlank(item.getRemark(), item.getNoticeContent(), ""));
        result.put("content", firstNonBlank(item.getNoticeContent(), ""));
        result.put("noticeType", item.getNoticeType());
        result.put("publishTime", formatDateTime(item.getCreateTime()));
        result.put("readFlag", item.getIsRead() ? "1" : "0");
        result.put("isRead", item.getIsRead());
        return result;
    }

    private Map<String, Object> articleSummary(YgbPortalContent item)
    {
        JSONObject extra = parseObject(item.getExtraJson());
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("articleKey", "cms-" + item.getContentId());
        row.put("title", firstNonBlank(item.getTitle(), "-"));
        row.put("summary", firstNonBlank(item.getSummary(), item.getContent(), ""));
        row.put("category", firstNonBlank(jsonString(extra, "typeLabel"), item.getCategoryCode(), "????"));
        return row;
    }

    private Map<String, Object> simpleArticle(String articleKey, String title, String category, String summary)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("articleKey", articleKey);
        row.put("title", title);
        row.put("summary", summary);
        row.put("category", category);
        return row;
    }

    private Map<String, Object> articleDetail(YgbPortalContent item)
    {
        JSONObject extra = parseObject(item.getExtraJson());
        return detailWithParagraphs(firstNonBlank(item.getTitle(), "-"),
            firstNonBlank(jsonString(extra, "typeLabel"), item.getCategoryCode(), "????"), extractParagraphs(item));
    }

    private Map<String, Object> faq(String faqKey, String title, String category, List<String> paragraphs)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("faqKey", faqKey);
        row.put("title", title);
        row.put("category", category);
        row.put("paragraphs", paragraphs);
        return row;
    }

    private Map<String, Object> summaryFromFaq(Map<String, Object> faq)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("faqKey", faq.get("faqKey"));
        row.put("title", faq.get("title"));
        row.put("category", faq.get("category"));
        return row;
    }

    private Map<String, Object> quickAction(String key, String label, String path)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("label", label);
        row.put("path", path);
        return row;
    }

    private Map<String, Object> caseSummary(YgbPortalContent item)
    {
        return caseSummary("cms-" + item.getContentId(), firstNonBlank(item.getTitle(), "-"),
            firstNonBlank(item.getSummary(), item.getContent(), ""));
    }

    private Map<String, Object> caseSummary(String caseKey, String title, String summary)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("caseKey", caseKey);
        row.put("title", title);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> noticeSummary(YgbPortalContent item)
    {
        return noticeSummary("cms-" + item.getContentId(), firstNonBlank(item.getTitle(), "-"),
            firstNonBlank(item.getSummary(), item.getContent(), ""));
    }

    private Map<String, Object> noticeSummary(String noticeKey, String title, String summary)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("noticeKey", noticeKey);
        row.put("title", title);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> contractSummary(YgbPortalContent item)
    {
        JSONObject extra = parseObject(item.getExtraJson());
        return contractSummary("cms-" + item.getContentId(), firstNonBlank(item.getTitle(), "-"),
            firstNonBlank(jsonString(extra, "enterpriseName"), item.getSourceName(), "????"),
            firstNonBlank(jsonString(extra, "period"), formatPublishPeriod(item.getPublishTime())),
            firstNonBlank(item.getSummary(), item.getContent(), ""));
    }

    private Map<String, Object> contractSummary(String contractKey, String title, String enterpriseName, String period,
        String summary)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("contractKey", contractKey);
        row.put("title", title);
        row.put("enterpriseName", enterpriseName);
        row.put("period", period);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> contractDetail(YgbPortalContent item)
    {
        JSONObject extra = parseObject(item.getExtraJson());
        Map<String, Object> result = contractSummary(item);
        result.put("enterpriseName", firstNonBlank(jsonString(extra, "enterpriseName"), item.getSourceName(), "????"));
        result.put("period", firstNonBlank(jsonString(extra, "period"), formatPublishPeriod(item.getPublishTime())));
        return result;
    }

    private Map<String, Object> detailWithParagraphs(String title, String category, List<String> paragraphs)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("title", title);
        row.put("category", category);
        row.put("paragraphs", paragraphs);
        return row;
    }

    private List<String> extractParagraphs(YgbPortalContent item)
    {
        String source = firstNonBlank(item.getContent(), item.getSummary(), item.getTitle(), "");
        if (source.isEmpty())
        {
            return Collections.singletonList("");
        }
        String normalized = source.replaceAll("(?i)</p>", "\n")
            .replaceAll("(?i)<br\\s*/?>", "\n")
            .replaceAll("<[^>]+>", "")
            .replace("&nbsp;", " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .trim();
        List<String> rows = new ArrayList<>();
        for (String part : normalized.split("\\r?\\n"))
        {
            String trimmed = trimToEmpty(part);
            if (!trimmed.isEmpty())
            {
                rows.add(trimmed);
            }
        }
        if (rows.isEmpty())
        {
            rows.add(normalized);
        }
        return rows;
    }

    private YgbPortalContent resolveCmsContent(String key)
    {
        if (StringUtils.isEmpty(key) || !key.startsWith("cms-"))
        {
            return null;
        }
        try
        {
            return portalContentMapper.selectPortalContentById(Long.parseLong(key.substring(4)));
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    private Map<String, Object> pushPayload(String jumpPath, Map<String, Object> jumpQuery, String actionLabel,
        String sourceLabel)
    {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("jumpPath", jumpPath);
        payload.put("jumpQuery", jumpQuery);
        payload.put("actionLabel", actionLabel);
        payload.put("sourceLabel", sourceLabel);
        return payload;
    }

    private Map<String, Object> metadata(String source, Long userId, Long personId, Map<String, Object> extra)
    {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("source", source);
        metadata.put("userId", userId);
        metadata.put("personId", personId);
        if (extra != null)
        {
            metadata.putAll(extra);
        }
        return metadata;
    }

    private JSONObject parseObject(String jsonText)
    {
        if (StringUtils.isEmpty(jsonText))
        {
            return new JSONObject();
        }
        try
        {
            JSONObject result = JSON.parseObject(jsonText);
            return result == null ? new JSONObject() : result;
        }
        catch (Exception ignored)
        {
            return new JSONObject();
        }
    }

    private Map<String, Object> parseMap(String jsonText)
    {
        JSONObject object = parseObject(jsonText);
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : object.keySet())
        {
            result.put(key, object.get(key));
        }
        return result;
    }

    private String jsonString(JSONObject object, String key)
    {
        if (object == null || StringUtils.isEmpty(key))
        {
            return null;
        }
        String value = object.getString(key);
        return StringUtils.isEmpty(value) ? null : value.trim();
    }

    private String normalizeAttachmentText(String rawText)
    {
        if (StringUtils.isEmpty(rawText))
        {
            return "";
        }
        String trimmed = rawText.trim();
        if (!trimmed.startsWith("["))
        {
            return trimmed;
        }
        try
        {
            JSONArray array = JSON.parseArray(trimmed);
            List<String> rows = new ArrayList<>();
            for (Object item : array)
            {
                if (item == null)
                {
                    continue;
                }
                String value = trimToEmpty(String.valueOf(item));
                if (!value.isEmpty())
                {
                    rows.add(value);
                }
            }
            return String.join(",", rows);
        }
        catch (Exception ignored)
        {
            return trimmed;
        }
    }

    private String formatDateTime(Date date)
    {
        return date == null ? "" : DATE_TIME_FORMAT.format(date);
    }

    private String formatPublishPeriod(Date publishTime)
    {
        return publishTime == null ? "" : DATE_TIME_FORMAT.format(publishTime);
    }

    private void syncComplaintToUnion(WorkerComplaint complaint, String operator)
    {
        if (complaint == null || !"1".equals(complaint.getSyncUnionFlag()))
        {
            return;
        }
        YgbUnionSyncResponse response = executeUnionSync(() -> unionAidClient.submitComplaint(complaint), "投诉");
        complaint.setRemark(buildUnionSyncRemark(response, complaint.getRemark()));
        complaint.setSyncUnionFlag(response.isSuccess() ? "1" : "2");
        complaint.setUpdateBy(firstNonBlank(operator, complaint.getCreateBy(), "system"));
        workerComplaintMapper.updateWorkerComplaintSyncResult(complaint);
    }

    private void syncLegalConsultToUnion(WorkerLegalConsult consult, String operator)
    {
        if (consult == null)
        {
            return;
        }
        YgbUnionSyncResponse response = executeUnionSync(() -> unionAidClient.submitLegalConsult(consult), "法律咨询");
        consult.setRemark(buildUnionSyncRemark(response, consult.getRemark()));
        consult.setUpdateBy(firstNonBlank(operator, consult.getCreateBy(), "system"));
        workerLegalConsultMapper.updateWorkerLegalConsultSyncResult(consult);
    }

    private YgbUnionSyncResponse executeUnionSync(UnionSyncExecutor executor, String bizLabel)
    {
        try
        {
            YgbUnionSyncResponse response = executor.execute();
            if (response == null)
            {
                return failedUnionResponse(bizLabel + "同步工会失败");
            }
            response.setSyncStatus(firstNonBlank(response.getSyncStatus(), response.isSuccess() ? "SUCCESS" : "FAIL"));
            response.setSyncMessage(firstNonBlank(response.getSyncMessage(),
                response.isSuccess() ? "已同步工会" : bizLabel + "同步失败"));
            return response;
        }
        catch (Exception ex)
        {
            return failedUnionResponse(firstNonBlank(ex.getMessage(), bizLabel + "同步失败"));
        }
    }

    private YgbUnionSyncResponse failedUnionResponse(String message)
    {
        YgbUnionSyncResponse response = new YgbUnionSyncResponse();
        response.setSuccess(false);
        response.setSyncStatus("FAIL");
        response.setSyncMessage(firstNonBlank(message, "工会同步失败"));
        response.setCallbackTime(new Date());
        response.setRawPayload("{}");
        return response;
    }

    private String buildUnionSyncRemark(YgbUnionSyncResponse response, String rawRemark)
    {
        JSONObject remark = parseObject(rawRemark);
        JSONObject sync = new JSONObject();
        sync.put("success", response != null && response.isSuccess());
        sync.put("syncStatus", response == null ? "FAIL" : firstNonBlank(response.getSyncStatus(), "FAIL"));
        sync.put("syncMessage", response == null ? "工会同步失败"
            : firstNonBlank(response.getSyncMessage(), "工会同步失败"));
        sync.put("ticketNo", response == null ? "" : firstNonBlank(response.getTicketNo(), ""));
        sync.put("externalSerialNo", response == null ? "" : firstNonBlank(response.getExternalSerialNo(), ""));
        sync.put("callbackTime", response == null ? "" : formatDateTime(response.getCallbackTime()));
        sync.put("sourceStatus", response == null ? "FAIL" : firstNonBlank(response.getSourceStatus(), ""));
        sync.put("sourceMessage", limitText(response == null ? "" : firstNonBlank(response.getSourceMessage(), ""), 60));
        remark.put("unionSync", sync);
        return limitText(JSON.toJSONString(remark), REMARK_MAX_LENGTH);
    }

    private String limitText(String value, int maxLength)
    {
        if (value == null || value.length() <= maxLength)
        {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength));
    }

    private void appendUnionSyncResult(Map<String, Object> result, boolean syncRequested, String remarkText)
    {
        JSONObject sync = parseObject(remarkText).getJSONObject("unionSync");
        String syncStatus = sync == null ? "" : firstNonBlank(sync.getString("syncStatus"), "");
        String syncMessage = sync == null ? "" : firstNonBlank(sync.getString("syncMessage"), "");
        result.put("syncUnionRequested", syncRequested);
        result.put("syncUnionStatus", syncRequested ? firstNonBlank(syncStatus, "PENDING") : "OFF");
        result.put("syncUnionTicketNo", sync == null ? "" : firstNonBlank(sync.getString("ticketNo"), ""));
        result.put("syncUnionExternalSerialNo", sync == null ? "" : firstNonBlank(sync.getString("externalSerialNo"), ""));
        result.put("syncUnionCallbackTime", sync == null ? "" : firstNonBlank(sync.getString("callbackTime"), ""));
        result.put("syncUnionMessage", syncRequested ? firstNonBlank(syncMessage, "工会同步处理中") : "未同步工会");
        result.put("syncUnionText", unionSyncText(syncRequested, syncStatus, syncMessage));
    }

    private String unionSyncText(boolean syncRequested, String syncStatus, String syncMessage)
    {
        if (!syncRequested)
        {
            return "未同步工会";
        }
        if ("SUCCESS".equalsIgnoreCase(firstNonBlank(syncStatus, "")))
        {
            return firstNonBlank(syncMessage, "已同步工会");
        }
        if ("FAIL".equalsIgnoreCase(firstNonBlank(syncStatus, "")))
        {
            return firstNonBlank(syncMessage, "工会同步失败");
        }
        return firstNonBlank(syncMessage, "工会同步处理中");
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String booleanFlag(Boolean value)
    {
        return Boolean.TRUE.equals(value) ? "1" : "0";
    }

    private boolean sameText(String left, String right)
    {
        return trimToEmpty(left).equals(trimToEmpty(right));
    }

    private String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return "";
        }
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value))
            {
                return value.trim();
            }
        }
        return "";
    }

    private String trimToEmpty(String value)
    {
        return value == null ? "" : value.trim();
    }

    @FunctionalInterface
    private interface UnionSyncExecutor
    {
        YgbUnionSyncResponse execute();
    }
}
