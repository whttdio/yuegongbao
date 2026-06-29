package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.service.ISysConfigService;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerFeedback;
import com.yuegongbao.ygb.worker.domain.WorkerPointExchange;
import com.yuegongbao.ygb.worker.domain.WorkerPointGoods;
import com.yuegongbao.ygb.worker.domain.WorkerPointLedger;
import com.yuegongbao.ygb.worker.domain.WorkerPushTestRecord;
import com.yuegongbao.ygb.worker.domain.WorkerRealnameApply;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.WorkerSetting;
import com.yuegongbao.ygb.worker.domain.WorkerUploadRecord;
import com.yuegongbao.ygb.worker.domain.vo.WorkerFeedbackCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPointExchangeRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushRegisterRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushTestRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerRealnameSubmitRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerResumeSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerSettingSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerUploadRecordSaveRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerPushGatewayService;
import com.yuegongbao.ygb.worker.service.WorkerProfileService;
import com.yuegongbao.ygb.worker.support.WorkerResumeSupport;

@Service
public class WorkerProfileServiceImpl implements WorkerProfileService
{
    private static final Logger log = LoggerFactory.getLogger(WorkerProfileServiceImpl.class);

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Autowired
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private WorkerMessageService workerMessageService;

    @Autowired
    private WorkerPushGatewayService workerPushGatewayService;

    @Autowired
    private ISysConfigService sysConfigService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String CONFIG_PUSH_GATEWAY_ENABLED = "ygb.worker.push.gateway.enabled";

    private static final String CONFIG_PUSH_GATEWAY_URL = "ygb.worker.push.gateway.url";

    private static final String CONFIG_PUSH_GATEWAY_PROVIDER = "ygb.worker.push.gateway.provider";

    private static final String REALNAME_STATUS_PENDING = "0";

    private static final String REALNAME_STATUS_APPROVED = "1";

    private static final String REALNAME_STATUS_REJECTED = "2";

    private static final Pattern REALNAME_PERSON_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5A-Za-z·\\s]{2,20}$");

    private static final Pattern REALNAME_MOBILE_PATTERN = Pattern.compile("^1\\d{10}$");

    private static final Pattern REALNAME_IDCARD_PATTERN = Pattern.compile("^(?:\\d{15}|\\d{17}[\\dX])$");

    @Override
    public Map<String, Object> getResume(YgbPerson worker, Long userId)
    {
        WorkerResume resume = workerProfileMapper.selectWorkerResume(userId);
        if (resume == null)
        {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("personName", worker.getPersonName());
            result.put("mobile", worker.getMobile());
            result.put("jobType", worker.getJobType());
            result.put("expectedJob", worker.getJobType());
            result.put("expectedCity", worker.getEnterpriseName());
            result.put("expectedSalary", "");
            result.put("skillTags", "");
            result.put("certificateText", "");
            result.put("certificateList", new ArrayList<>());
            result.put("certificateCount", 0);
            result.put("certificateStatusText", certificateStatusText(worker.getCertStatus()));
            result.put("certificateStatusCode", firstNonBlank(worker.getCertStatus(), "0"));
            result.put("intro", "");
            return result;
        }
        return resumeMap(resume);
    }

    @Override
    public Map<String, Object> getLaborContractList(YgbPerson worker)
    {
        YgbContract query = new YgbContract();
        query.setPersonId(worker.getPersonId());
        List<YgbContract> contracts = contractMapper.selectContractList(query);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbContract contract : contracts)
        {
            rows.add(contractSummary(contract));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("personName", worker.getPersonName());
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    @Override
    public Map<String, Object> getLaborContractDetail(YgbPerson worker, Long contractId)
    {
        YgbContract contract = contractMapper.selectContractById(contractId);
        if (contract == null || contract.getPersonId() == null || !contract.getPersonId().equals(worker.getPersonId()))
        {
            throw new ServiceException("未找到当前劳动者可查看的劳动合同。");
        }
        Map<String, Object> result = contractSummary(contract);
        result.put("personName", worker.getPersonName());
        result.put("dispatchEnterpriseName", nvl(contract.getDispatchEnterpriseName(), "待同步派遣单位"));
        result.put("employerEnterpriseName", nvl(contract.getEmployerEnterpriseName(), "待同步用工单位"));
        result.put("signDate", formatDate(contract.getSignDate()));
        result.put("startDate", formatDate(contract.getStartDate()));
        result.put("endDate", formatDate(contract.getEndDate()));
        result.put("monthlyWage", contract.getMonthlyWage() == null ? "-" : contract.getMonthlyWage().setScale(2, RoundingMode.HALF_UP).toPlainString());
        result.put("filingNo", nvl(contract.getFilingNo(), "未备案"));
        result.put("filingTime", formatDateTime(contract.getFilingTime()));
        result.put("ocrStatusText", ocrStatusText(contract.getOcrStatus()));
        result.put("clauseCheckStatusText", clauseCheckStatusText(contract.getClauseCheckStatus()));
        result.put("blockchainHash", nvl(contract.getBlockchainHash(), "待补充"));
        result.put("contractFileUrl", contract.getContractFileUrl());
        result.put("hasOriginalFile", StringUtils.isNotEmpty(contract.getContractFileUrl()));
        result.put("viewTip", StringUtils.isEmpty(contract.getContractFileUrl())
            ? "当前合同暂未同步电子版原件，页面已提供合同摘要预览，方便劳动者先核对合同主体、期限、工资标准和备案状态。"
            : "可点击下方按钮查看劳动合同电子版原件；如原件暂时无法打开，也可进入合同摘要预览先核对关键信息。");
        result.put("previewTitle", contractTypeText(contract.getContractType()) + "摘要预览");
        result.put("previewSections", buildContractPreviewSections(contract));
        return result;
    }

    @Override
    public Map<String, Object> saveResume(YgbPerson worker, SysUser user, WorkerResumeSaveRequest request)
    {
        WorkerResumeSupport.assertResumeRequestComplete(request);
        WorkerResume resume = workerProfileMapper.selectWorkerResume(user.getUserId());
        if (resume == null)
        {
            resume = new WorkerResume();
            resume.setUserId(user.getUserId());
            resume.setPersonId(worker.getPersonId());
            resume.setPersonName(worker.getPersonName());
            resume.setMobile(worker.getMobile());
            resume.setJobType(worker.getJobType());
            fillResume(resume, request);
            resume.setCreateBy(user.getUserName());
            workerProfileMapper.insertWorkerResume(resume);
        }
        else
        {
            fillResume(resume, request);
            resume.setUpdateBy(user.getUserName());
            workerProfileMapper.updateWorkerResume(resume);
        }
        return resumeMap(resume);
    }

    @Override
    public Map<String, Object> getHelpList()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(help("help-1", "实名认证失败怎么办", "登录与认证", "检查姓名、身份证号和实名手机号是否一致。",
            "/pages/profile/real-name", "去实名认证"));
        rows.add(help("help-2", "打卡失败如何处理", "考勤", "确认已完成培训，并开启定位权限后重试。",
            "/pages/attendance/checkin", "去考勤"));
        rows.add(help("help-3", "工资查询被锁定", "工资", "本月培训未完成时，工资页会被锁定。",
            "/pages/training/index", "去完成培训"));
        rows.add(help("help-4", "如何发起投诉举报", "维权", "进入投诉举报页填写问题、证据和联系方式。",
            "/pages/complaint/index", "去投诉举报"));
        rows.add(help("help-5", "社保断缴后怎么办", "社保争议", "先核对断缴月份，再准备劳动关系和工资证明。",
            "/pages/legal/index", "去法律咨询"));
        rows.add(help("help-6", "劳动合同怎么核对", "劳动合同", "先查看合同摘要或电子版原件，核对主体、期限和工资标准。",
            "/pages/profile/labor-contracts", "去查看合同"));
        rows.add(help("help-7", "找工作后如何查看投递记录", "求职服务", "岗位投递后可在投递记录里查看处理进度。",
            "/pages/job/apply-list", "去投递记录"));
        rows.add(help("help-8", "发生争议先联系谁", "协同服务", "优先通过工会热线、在线法律咨询和投诉举报三条链路留痕。",
            "/pages/union/index", "去工会服务"));

        Map<String, Object> hotline = workerMessageService.getHotline();
        Map<String, Object> faqResult = workerMessageService.getLegalFaqList("");
        Map<String, Object> unionHome = workerMessageService.getUnionServiceHome();
        List<Map<String, Object>> faqRows = castRows(faqResult.get("rows"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", rows.size());
        result.put("hotline", hotline);
        result.put("quickActions", Arrays.asList(
            actionCard("help-legal", "法律咨询", "先提交在线法律咨询，再补充证据附件。", "/pages/legal/index"),
            actionCard("help-complaint", "投诉举报", "适合欠薪、社保异常和安全隐患线索。", "/pages/complaint/index"),
            actionCard("help-union", "工会服务", "进入工会案例、通知和集体合同协同入口。", "/pages/union/index"),
            actionCard("help-contract", "合同核对", "查看劳动合同电子版或摘要预览。", "/pages/profile/labor-contracts"),
            actionCard("help-training", "培训解锁", "工资和打卡未解锁时先完成本月培训。", "/pages/training/index"),
            actionCard("help-feedback", "提交反馈", "提交产品问题、功能建议和服务意见。", "")));
        result.put("serviceCards", Arrays.asList(
            serviceCard("hotline", "工会法律热线", nvl((String) hotline.get("displayText"), "工会法律服务热线 12351"),
                "工作时间可优先拨打热线，复杂争议建议同步补齐工资、考勤、合同等证据。", "立即拨打",
                null, (String) hotline.get("phoneNumber")),
            serviceCard("legal", "在线法律咨询", "法律咨询 + FAQ",
                "适合欠薪、社保断缴、劳动合同、工伤赔付等问题在线留痕。", "进入咨询", "/pages/legal/index", null),
            serviceCard("union", "工会协同服务", "工会案例 / 通知 / 集体合同",
                "适合先看工会协同案例，再决定走热线、咨询或投诉链路。", "进入工会服务", "/pages/union/index", null),
            serviceCard("contract", "劳动合同核对", "电子版原件 + 合同摘要",
                "合同原件暂未同步时，也可先通过合同摘要核对主体、期限和工资标准。", "查看我的合同",
                "/pages/profile/labor-contracts", null)));
        result.put("faqRows", faqRows.size() > 3 ? faqRows.subList(0, 3) : faqRows);
        result.put("unionQuickActions", unionHome.get("quickActions"));
        return result;
    }

    @Override
    public Map<String, Object> getHelpDetail(String articleKey)
    {
        Map<String, Object> detail = switch (articleKey)
        {
            case "help-1" -> helpDetail("help-1", "实名认证失败怎么办", "登录与认证",
                Arrays.asList("确认姓名、身份证号填写无误。", "检查当前登录手机号是否与实名信息一致。", "如仍失败，请联系平台管理员核对劳动者档案。"),
                "/pages/profile/real-name", "去实名认证");
            case "help-2" -> helpDetail("help-2", "打卡失败如何处理", "考勤",
                Arrays.asList("确认已完成本月培训任务。", "检查定位权限是否开启。", "如网络异常，可稍后重试，系统支持补传。"),
                "/pages/attendance/checkin", "去考勤");
            case "help-3" -> helpDetail("help-3", "工资查询被锁定", "工资",
                Arrays.asList("工资查询与培训考核强关联。", "未完成本月 10 题安全培训时，工资页会被锁定。", "完成培训后重新进入工资页即可。"),
                "/pages/training/index", "去完成培训");
            case "help-4" -> helpDetail("help-4", "如何发起投诉举报", "维权",
                Arrays.asList("进入投诉举报页选择问题类型。", "尽量补充图片、聊天记录和工资凭证。", "提交后可在投诉详情中查看处理状态。"),
                "/pages/complaint/index", "去投诉举报");
            case "help-5" -> helpDetail("help-5", "社保断缴后怎么办", "社保争议",
                Arrays.asList("先核对断缴的具体月份和险种。", "准备劳动合同、工资记录、在岗证明和社保截图。", "优先走法律咨询或工会协同渠道，补齐证据后再推动补缴。"),
                "/pages/legal/index", "去法律咨询");
            case "help-6" -> helpDetail("help-6", "劳动合同怎么核对", "劳动合同",
                Arrays.asList("优先查看劳动合同电子版原件。", "如原件暂未同步，可先查看合同摘要，核对主体、期限、工资标准和备案状态。", "发现条款与实际不符时，可同步保留证据并发起咨询或投诉。"),
                "/pages/profile/labor-contracts", "去查看合同");
            case "help-7" -> helpDetail("help-7", "找工作后如何查看投递记录", "求职服务",
                Arrays.asList("岗位投递后可进入投递记录查看处理状态。", "如岗位长期无反馈，可回到岗位列表继续筛选或更换简历方向。", "发现招聘信息与实际不符时，建议保留截图并反馈。"),
                "/pages/job/apply-list", "去投递记录");
            case "help-8" -> helpDetail("help-8", "发生争议先联系谁", "协同服务",
                Arrays.asList("优先通过 12351 工会法律热线获取人工指引。", "同时可在线提交法律咨询，补齐工资、社保、合同等证据。", "若已形成明确线索，再进入投诉举报链路留痕。"),
                "/pages/union/index", "去工会服务");
            default -> null;
        };
        if (detail == null)
        {
            throw new ServiceException("未找到帮助内容。");
        }
        return detail;
    }

    @Override
    public Map<String, Object> getRealnameDetail(YgbPerson worker, Long userId, String userName)
    {
        WorkerRealnameApply latestApply = workerProfileMapper.selectLatestWorkerRealnameApply(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userName", userName);
        result.put("personName", worker.getPersonName());
        result.put("personNameMasked", maskName(worker.getPersonName()));
        result.put("mobile", worker.getMobile());
        result.put("mobileMasked", maskMobile(worker.getMobile()));
        result.put("idCard", worker.getIdCard());
        result.put("idCardMasked", maskIdCard(worker.getIdCard()));
        result.put("realNameVerified", isRealNameVerified(worker));
        result.put("realNameStatusText", isRealNameVerified(worker) ? "已实名核验" : "待实名核验");
        result.put("applyStatus", latestApply == null ? "" : latestApply.getApplyStatus());
        result.put("applyStatusText", latestApply == null ? "未提交申请" : realnameApplyStatusText(latestApply.getApplyStatus()));
        result.put("rejectReason", latestApply == null ? "" : nvl(latestApply.getRejectReason(), ""));
        result.put("idCardFrontUrl", latestApply == null ? "" : nvl(latestApply.getIdCardFrontUrl(), ""));
        result.put("idCardBackUrl", latestApply == null ? "" : nvl(latestApply.getIdCardBackUrl(), ""));
        result.put("selfieUrl", latestApply == null ? "" : nvl(latestApply.getSelfieUrl(), ""));
        result.put("sourceModule", latestApply == null ? "" : nvl(latestApply.getSourceModule(), ""));
        result.put("applyTime", latestApply == null ? "" : formatDateTime(latestApply.getCreateTime()));
        result.put("lastUpdateTime", latestApply == null ? "" : formatDateTime(latestApply.getUpdateTime()));
        result.put("statusHint", buildRealnameStatusHint(worker, latestApply));
        result.put("canResubmit", latestApply == null || REALNAME_STATUS_REJECTED.equals(latestApply.getApplyStatus()));
        result.put("tips", Arrays.asList(
            "请填写与身份证一致的姓名、手机号和身份证号。",
            "请上传身份证人像面、国徽面和本人免冠照片。",
            "如被驳回，可根据驳回原因补充材料后重新提交。"));
        return result;
    }

    @Override
    public Map<String, Object> submitRealnameApply(YgbPerson worker, SysUser user, WorkerRealnameSubmitRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("实名认证参数不能为空。");
        }
        if (StringUtils.isEmpty(request.getPersonName()) || StringUtils.isEmpty(request.getMobile())
            || StringUtils.isEmpty(request.getIdCard()) || StringUtils.isEmpty(request.getIdCardFrontUrl())
            || StringUtils.isEmpty(request.getIdCardBackUrl()) || StringUtils.isEmpty(request.getSelfieUrl()))
        {
            throw new ServiceException("请完整填写实名信息并上传证件材料。");
        }

        normalizeRealnameSubmitRequest(request);
        validateRealnameSubmitRequest(worker, request);

        WorkerRealnameApply latestApply = workerProfileMapper.selectLatestWorkerRealnameApply(user.getUserId());
        if (latestApply != null && REALNAME_STATUS_PENDING.equals(latestApply.getApplyStatus()))
        {
            throw new ServiceException("当前已有待审核的实名认证申请，请等待处理结果。");
        }

        WorkerRealnameApply apply = new WorkerRealnameApply();
        apply.setUserId(user.getUserId());
        apply.setPersonId(worker.getPersonId());
        apply.setPersonName(request.getPersonName());
        apply.setMobile(request.getMobile());
        apply.setIdCard(request.getIdCard());
        apply.setApplyStatus(REALNAME_STATUS_PENDING);
        apply.setRejectReason("");
        apply.setIdCardFrontUrl(request.getIdCardFrontUrl());
        apply.setIdCardBackUrl(request.getIdCardBackUrl());
        apply.setSelfieUrl(request.getSelfieUrl());
        apply.setSourceModule(firstNonBlank(request.getSourceModule(), "worker-uniapp"));
        apply.setCreateBy(user.getUserName());
        apply.setRemark(buildRealnameApplyRemark(worker, latestApply));
        workerProfileMapper.insertWorkerRealnameApply(apply);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("applyId", apply.getApplyId());
        result.put("applyStatus", apply.getApplyStatus());
        result.put("applyStatusText", realnameApplyStatusText(apply.getApplyStatus()));
        result.put("statusHint", "实名认证申请已提交，请等待管理员审核。");
        result.put("applyTime", formatDateTime(apply.getCreateTime()));
        return result;
    }

    @Override
    public Map<String, Object> createFeedback(YgbPerson worker, SysUser user, WorkerFeedbackCreateRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getTitle()) || StringUtils.isEmpty(request.getContent()))
        {
            throw new ServiceException("反馈内容不完整。");
        }
        WorkerFeedback feedback = new WorkerFeedback();
        feedback.setUserId(user.getUserId());
        feedback.setPersonId(worker.getPersonId());
        feedback.setPersonName(worker.getPersonName());
        feedback.setMobile(worker.getMobile());
        feedback.setTitle(request.getTitle());
        feedback.setContent(request.getContent());
        feedback.setContactMobile(firstNonBlank(request.getContactMobile(), worker.getMobile(), user.getPhonenumber()));
        feedback.setStatus("0");
        feedback.setCreateBy(user.getUserName());
        workerProfileMapper.insertWorkerFeedback(feedback);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("feedbackId", feedback.getFeedbackId());
        result.put("status", feedback.getStatus());
        return result;
    }

    @Override
    public List<WorkerFeedback> listFeedbackManageRecords(WorkerFeedback query)
    {
        return workerProfileMapper.selectWorkerFeedbackManageList(query);
    }

    @Override
    public WorkerFeedback getFeedbackManageDetail(Long feedbackId)
    {
        WorkerFeedback feedback = workerProfileMapper.selectWorkerFeedbackById(feedbackId);
        if (feedback == null)
        {
            throw new ServiceException("未找到反馈记录。");
        }
        return feedback;
    }

    @Override
    public Map<String, Object> updateFeedbackHandle(Long feedbackId, WorkerMessageHandleRequest request, String operator)
    {
        if (request == null || StringUtils.isEmpty(request.getStatus()))
        {
            throw new ServiceException("反馈处理状态不能为空。");
        }
        String status = request.getStatus();
        if (!"0".equals(status) && !"1".equals(status))
        {
            throw new ServiceException("反馈处理状态不支持。");
        }
        if ("1".equals(status) && StringUtils.isEmpty(request.getReplyContent()))
        {
            throw new ServiceException("标记已处理时必须填写处理意见。");
        }
        WorkerFeedback feedback = workerProfileMapper.selectWorkerFeedbackById(feedbackId);
        if (feedback == null)
        {
            throw new ServiceException("未找到反馈记录。");
        }

        WorkerFeedback target = new WorkerFeedback();
        target.setFeedbackId(feedbackId);
        target.setStatus(status);
        target.setRemark(buildFeedbackHandleRemark(status, request.getReplyContent()));
        target.setUpdateBy(operator);
        int rows = workerProfileMapper.updateWorkerFeedbackHandle(target);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("feedbackId", feedbackId);
        result.put("status", status);
        result.put("statusText", "1".equals(status) ? "已处理" : "待处理");
        result.put("updated", rows > 0);
        return result;
    }

    @Override
    public Map<String, Object> getSettings(YgbPerson worker, Long userId)
    {
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        boolean pushRegistered = isPushRegistered(setting);
        result.put("personName", worker.getPersonName());
        result.put("mobile", worker.getMobile());
        result.put("notifyEnabled", setting == null || !"0".equals(setting.getNotifyEnabled()));
        result.put("pushClientId", setting == null ? "" : nvl(setting.getPushClientId(), ""));
        result.put("pushClientIdMasked", maskPushClientId(setting == null ? null : setting.getPushClientId()));
        result.put("notificationPermission", setting == null ? "" : nvl(setting.getNotificationPermission(), ""));
        result.put("pushPlatform", setting == null ? "" : nvl(setting.getPushPlatform(), ""));
        result.put("pushSyncTime", setting == null ? "" : formatDateTime(firstNonNull(setting.getUpdateTime(), setting.getCreateTime())));
        result.put("pushRegistered", pushRegistered);
        result.put("pushStatusText", resolvePushStatusText(setting));
        result.putAll(buildPushGatewayStatusSnapshot());
        appendLatestPushTestRecord(result, userId);
        return result;
    }

    @Override
    public Map<String, Object> saveSettings(YgbPerson worker, SysUser user, WorkerSettingSaveRequest request)
    {
        if (request == null || request.getNotifyEnabled() == null)
        {
            throw new ServiceException("设置参数不完整。");
        }
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(user.getUserId());
        String notifyEnabled = Boolean.TRUE.equals(request.getNotifyEnabled()) ? "1" : "0";
        if (setting == null)
        {
            setting = initWorkerSetting(worker, user);
            setting.setNotifyEnabled(notifyEnabled);
            workerProfileMapper.insertWorkerSetting(setting);
        }
        else
        {
            setting.setNotifyEnabled(notifyEnabled);
            setting.setUpdateBy(user.getUserName());
            workerProfileMapper.updateWorkerSetting(setting);
        }
        return getSettings(worker, user.getUserId());
    }

    @Override
    public Map<String, Object> registerPush(YgbPerson worker, SysUser user, WorkerPushRegisterRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("推送注册参数不完整。");
        }
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(user.getUserId());
        if (setting == null)
        {
            setting = initWorkerSetting(worker, user);
            setting.setNotifyEnabled("1");
            fillPushRegistration(setting, request);
            workerProfileMapper.insertWorkerSetting(setting);
        }
        else
        {
            fillPushRegistration(setting, request);
            setting.setUpdateBy(user.getUserName());
            workerProfileMapper.updateWorkerSetting(setting);
        }
        return getSettings(worker, user.getUserId());
    }

    @Override
    public Map<String, Object> sendPushTest(YgbPerson worker, SysUser user, WorkerPushTestRequest request)
    {
        WorkerSetting setting = workerProfileMapper.selectWorkerSetting(user.getUserId());
        if (setting == null || StringUtils.isEmpty(setting.getPushClientId()))
        {
            throw new ServiceException("当前账号尚未完成推送注册，请先登录 APP 并完成 push 注册上报。");
        }

        String traceId = buildPushTraceId();
        Map<String, Object> payload = buildPushPayload(request);
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("traceId", traceId);
        metadata.put("userId", user.getUserId());
        metadata.put("userName", user.getUserName());
        metadata.put("personId", worker.getPersonId());
        metadata.put("personName", worker.getPersonName());
        metadata.put("pushPlatform", setting.getPushPlatform());
        metadata.put("notificationPermission", setting.getNotificationPermission());
        metadata.put("notifyEnabled", "1".equals(setting.getNotifyEnabled()));
        metadata.put("source", "worker-settings-push-test");
        Map<String, Object> gatewaySnapshot = buildPushGatewayStatusSnapshot();
        String pushClientIdMasked = maskPushClientId(setting.getPushClientId());
        String targetPath = String.valueOf(payload.get("jumpPath"));
        String targetQueryText = stringifyPushTestField(payload.get("jumpQuery"));
        String actionLabel = String.valueOf(payload.get("actionLabel"));
        String sourceLabel = String.valueOf(payload.get("sourceLabel"));
        Object requestBodySnapshot = buildPushGatewayRequestBodySnapshot(setting, request, payload, metadata);

        log.info("worker push test start traceId={} userId={} personId={} pushClientIdMasked={} targetPath={} provider={}",
            traceId,
            user.getUserId(),
            worker.getPersonId(),
            pushClientIdMasked,
            targetPath,
            gatewaySnapshot.get("pushGatewayProvider"));

        Map<String, Object> gatewayResult;
        try
        {
            gatewayResult = workerPushGatewayService.sendPush(
                setting.getPushClientId(),
                resolvePushTitle(request),
                resolvePushContent(request),
                payload,
                metadata);
        }
        catch (ServiceException ex)
        {
            saveWorkerPushTestRecord(worker, user, setting, gatewaySnapshot, traceId, targetPath, targetQueryText,
                actionLabel, sourceLabel, requestBodySnapshot, null, "FAIL", ex.getMessage());
            log.error("worker push test fail traceId={} userId={} personId={} message={}",
                traceId,
                user.getUserId(),
                worker.getPersonId(),
                ex.getMessage(),
                ex);
            throw new ServiceException(ex.getMessage() + " traceId=" + traceId);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("traceId", traceId);
        result.put("pushClientId", setting.getPushClientId());
        result.put("pushClientIdMasked", pushClientIdMasked);
        result.put("notifyEnabled", "1".equals(setting.getNotifyEnabled()));
        result.put("notificationPermission", nvl(setting.getNotificationPermission(), ""));
        result.put("pushPlatform", nvl(setting.getPushPlatform(), ""));
        result.put("pushRegistered", true);
        result.put("pushStatusText", resolvePushStatusText(setting));
        result.put("testedAt", formatDateTime(new Date()));
        result.put("targetPath", targetPath);
        result.put("targetQuery", payload.get("jumpQuery"));
        result.put("targetQueryText", targetQueryText);
        result.put("actionLabel", actionLabel);
        result.put("sourceLabel", sourceLabel);
        result.put("testStatus", "SUCCESS");
        result.put("testStatusText", "服务端推送网关已受理本次测试请求，请在真机上确认送达和点击落页。");
        result.put("payload", payload);
        result.putAll(gatewaySnapshot);
        result.putAll(gatewayResult);
        saveWorkerPushTestRecord(worker, user, setting, gatewaySnapshot, traceId, targetPath, targetQueryText,
            actionLabel, sourceLabel, gatewayResult.get("requestBody"), gatewayResult.get("responseBody"), "SUCCESS",
            String.valueOf(result.get("testStatusText")));
        log.info("worker push test success traceId={} userId={} personId={} gatewayUrl={} provider={}",
            traceId,
            user.getUserId(),
            worker.getPersonId(),
            result.get("gatewayUrl"),
            result.get("provider"));
        return result;
    }

    @Override
    public Map<String, Object> getPointAccount(YgbPerson worker, Long userId)
    {
        ensurePointGoods();
        ensurePointLedger(worker, userId);
        List<WorkerPointLedger> ledgers = workerProfileMapper.selectWorkerPointLedgerList(userId);
        List<WorkerPointGoods> goodsList = workerProfileMapper.selectWorkerPointGoodsList();
        List<WorkerPointExchange> exchanges = workerProfileMapper.selectWorkerPointExchangeList(userId);
        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal balance = BigDecimal.ZERO;
        for (WorkerPointLedger ledger : ledgers)
        {
            balance = ledger.getBalanceAfter() == null ? balance : ledger.getBalanceAfter();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ledgerId", ledger.getLedgerId());
            row.put("changeType", ledger.getChangeType());
            row.put("title", ledger.getTitle());
            row.put("summary", ledger.getSummary());
            row.put("scoreDelta", ledger.getScoreDelta());
            row.put("balanceAfter", ledger.getBalanceAfter());
            row.put("createTime", ledger.getCreateTime());
            rows.add(row);
        }

        List<Map<String, Object>> rewards = new ArrayList<>();
        for (WorkerPointGoods goods : goodsList)
        {
            rewards.add(reward(goods, balance));
        }

        List<Map<String, Object>> exchangeRows = new ArrayList<>();
        for (WorkerPointExchange exchange : exchanges)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("exchangeId", exchange.getExchangeId());
            row.put("goodsKey", exchange.getGoodsKey());
            row.put("goodsName", exchange.getGoodsName());
            row.put("goodsType", exchange.getGoodsType());
            row.put("scoreCost", exchange.getScoreCost());
            row.put("exchangeStatus", exchange.getExchangeStatus());
            row.put("exchangeStatusText", exchangeStatusText(exchange.getExchangeStatus()));
            row.put("deliveryRemark", exchange.getDeliveryRemark());
            row.put("createTime", exchange.getCreateTime());
            exchangeRows.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("currentPoints", balance);
        result.put("rows", rows);
        result.put("rewards", rewards);
        result.put("exchangeRows", exchangeRows);
        return result;
    }

    @Override
    public Map<String, Object> exchangePointGoods(YgbPerson worker, SysUser user, WorkerPointExchangeRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getGoodsKey()))
        {
            throw new ServiceException("兑换商品参数不完整。");
        }
        ensurePointGoods();
        ensurePointLedger(worker, user.getUserId());
        WorkerPointGoods goods = workerProfileMapper.selectWorkerPointGoodsByKey(request.getGoodsKey());
        if (goods == null || !"1".equals(goods.getStatus()))
        {
            throw new ServiceException("未找到可兑换商品。");
        }
        if (goods.getStockCount() == null || goods.getStockCount() <= 0)
        {
            throw new ServiceException("该商品已兑换完。");
        }

        BigDecimal currentPoints = currentPoints(workerProfileMapper.selectWorkerPointLedgerList(user.getUserId()));
        BigDecimal requiredScore = goods.getRequiredScore() == null ? BigDecimal.ZERO : goods.getRequiredScore();
        if (currentPoints.compareTo(requiredScore) < 0)
        {
            throw new ServiceException("当前积分不足，无法兑换。");
        }

        BigDecimal balanceAfter = currentPoints.subtract(requiredScore);
        WorkerPointExchange exchange = new WorkerPointExchange();
        exchange.setUserId(user.getUserId());
        exchange.setPersonId(worker.getPersonId());
        exchange.setPersonName(worker.getPersonName());
        exchange.setGoodsKey(goods.getGoodsKey());
        exchange.setGoodsName(goods.getGoodsName());
        exchange.setGoodsType(goods.getGoodsType());
        exchange.setScoreCost(requiredScore);
        exchange.setExchangeStatus("1");
        exchange.setDeliveryRemark(resolveDeliveryRemark(goods));
        exchange.setCreateBy(user.getUserName());
        workerProfileMapper.insertWorkerPointExchange(exchange);

        insertPointLedger(worker, user.getUserId(), "OUT", "兑换" + goods.getGoodsName(),
            "积分商城兑换成功，等待权益发放或人工核销。", requiredScore.negate(), balanceAfter);
        workerProfileMapper.updateWorkerPointGoodsStock(goods.getGoodsId(), Math.max(0, defaultInt(goods.getStockCount()) - 1),
            user.getUserName());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("exchangeId", exchange.getExchangeId());
        result.put("goodsName", exchange.getGoodsName());
        result.put("currentPoints", balanceAfter);
        result.put("exchangeStatus", exchange.getExchangeStatus());
        result.put("exchangeStatusText", exchangeStatusText(exchange.getExchangeStatus()));
        result.put("deliveryRemark", exchange.getDeliveryRemark());
        return result;
    }

    @Override
    public Map<String, Object> getInsuranceSecurity(YgbPerson worker, Long userId)
    {
        YgbAqInsurance policy = worker.getEnterpriseId() == null ? null
            : aqInsuranceMapper.selectLatestAqInsuranceByEnterpriseId(worker.getEnterpriseId());
        JSONObject payload = parseRawPayload(policy == null ? null : policy.getRawPayload());
        AqCoverageSnapshot coverageSnapshot = resolveCoverageSnapshot(policy, payload);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("personName", worker.getPersonName());
        result.put("enterpriseName", worker.getEnterpriseName());
        result.put("injuryInsuranceStatus", insuranceStatusText(worker.getInsuranceStatus()));
        result.put("injuryInsuranceStatusCode", worker.getInsuranceStatus());
        result.put("aqInsuranceStatus", policyStatusText(policy == null ? null : policy.getPolicyStatus()));
        result.put("aqInsuranceStatusCode", policy == null ? null : policy.getPolicyStatus());
        result.put("aqInsuranceCoverageAmount", coverageSnapshot.getCoverageSummary());
        result.put("aqInsuranceCoverageHint", coverageSnapshot.getCoverageHint());
        result.put("aqInsuranceCoverageSource", coverageSnapshot.getCoverageSourceText());
        result.put("aqInsuranceCoverageSyncStatus", coverageSnapshot.getCoverageSyncStatus());
        result.put("aqInsuranceCoverageSyncStatusText", coverageSnapshot.getCoverageSyncStatusText());
        result.put("aqInsuranceCoverageDetail", coverageSnapshot.getCoverageDetail());
        result.put("aqInsurancePremium", policy == null ? "-" : formatMoney(policy.getPremium()));
        result.put("aqInsurancePeriod", buildPeriodText(policy == null ? null : policy.getStartDate(),
            policy == null ? null : policy.getEndDate()));
        result.put("aqInsuranceInsurer", policy == null ? "暂未匹配保单" : nvl(policy.getInsurerName(), "待补充承保机构"));
        result.put("aqInsurancePolicyNo", policy == null ? "暂未匹配保单" : nvl(policy.getPolicyNo(), "待补充保单号"));
        result.put("aqInsuranceExpireInDays", policy == null ? null : policy.getExpireInDays());
        result.put("aqInsuranceProtectedCount", policy == null ? null : defaultInt(policy.getInsuredPersonCount()));
        result.put("aqInsuranceFundRatio", formatPercent(policy == null ? null : policy.getPreventionFundRatio()));
        result.put("aqInsuranceFundAmount", policy == null ? "-" : formatMoney(policy.getPreventionFundAmount()));
        result.put("aqInsuranceUsedFund", policy == null ? "-" : formatMoney(policy.getUsedFundAmount()));
        result.put("aqInsuranceRemainingFund", policy == null ? "-" : formatMoney(policy.getRemainingFundAmount()));
        result.put("aqInsuranceSourceStatusText", policy == null ? "暂未同步" : syncSourceStatusText(policy.getSourceStatus()));
        result.put("aqInsuranceSourceMessage", policy == null ? "当前劳动者未匹配到安责险保单。"
            : firstNonBlank(policy.getSourceMessage(), "保单数据已同步"));
        return result;
    }

    @Override
    public Map<String, Object> createUploadRecord(YgbPerson worker, SysUser user, WorkerUploadRecordSaveRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getCategoryCode()) || StringUtils.isEmpty(request.getCategoryName())
            || StringUtils.isEmpty(request.getFileUrl()))
        {
            throw new ServiceException("上传记录参数不完整。");
        }
        WorkerUploadRecord record = new WorkerUploadRecord();
        record.setUserId(user.getUserId());
        record.setPersonId(worker.getPersonId());
        record.setPersonName(worker.getPersonName());
        record.setCategoryCode(request.getCategoryCode());
        record.setCategoryName(request.getCategoryName());
        record.setFileUrl(request.getFileUrl());
        record.setFileName(request.getFileName());
        record.setOriginalFilename(request.getOriginalFilename());
        record.setFileSize(request.getFileSize());
        record.setContentType(request.getContentType());
        record.setSourceModule(StringUtils.isEmpty(request.getSourceModule()) ? "camera" : request.getSourceModule());
        record.setCreateBy(user.getUserName());
        workerProfileMapper.insertWorkerUploadRecord(record);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("uploadId", record.getUploadId());
        result.put("categoryCode", record.getCategoryCode());
        result.put("fileUrl", record.getFileUrl());
        return result;
    }

    @Override
    public Map<String, Object> getUploadRecordList(Long userId, String categoryCode)
    {
        List<WorkerUploadRecord> records = workerProfileMapper.selectWorkerUploadRecordList(userId, categoryCode);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (WorkerUploadRecord record : records)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("uploadId", record.getUploadId());
            row.put("categoryCode", record.getCategoryCode());
            row.put("categoryName", record.getCategoryName());
            row.put("fileUrl", record.getFileUrl());
            row.put("fileName", record.getFileName());
            row.put("originalFilename", record.getOriginalFilename());
            row.put("fileSize", record.getFileSize());
            row.put("contentType", record.getContentType());
            row.put("sourceModule", record.getSourceModule());
            row.put("createTime", record.getCreateTime());
            rows.add(row);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", rows.size());
        result.put("rows", rows);
        return result;
    }

    @Override
    public List<WorkerUploadRecord> listUploadRecordManageRecords(WorkerUploadRecord query)
    {
        return workerProfileMapper.selectWorkerUploadRecordManageList(query);
    }

    @Override
    public WorkerUploadRecord getUploadRecordManageDetail(Long uploadId)
    {
        WorkerUploadRecord record = workerProfileMapper.selectWorkerUploadRecordById(uploadId);
        if (record == null)
        {
            throw new ServiceException("未找到上传归档记录。");
        }
        return record;
    }

    private void fillResume(WorkerResume resume, WorkerResumeSaveRequest request)
    {
        resume.setExpectedJob(request.getExpectedJob());
        resume.setExpectedCity(request.getExpectedCity());
        resume.setExpectedSalary(request.getExpectedSalary());
        resume.setSkillTags(request.getSkillTags());
        resume.setCertificateText(normalizeCertificateText(request.getCertificateText()));
        resume.setIntro(request.getIntro());
    }

    private String realnameApplyStatusText(String status)
    {
        if (REALNAME_STATUS_APPROVED.equals(status))
        {
            return "已通过";
        }
        if (REALNAME_STATUS_REJECTED.equals(status))
        {
            return "已驳回";
        }
        return "待审核";
    }

    private String buildRealnameStatusHint(YgbPerson worker, WorkerRealnameApply latestApply)
    {
        if (isRealNameVerified(worker))
        {
            return "当前劳动者基础档案已具备实名核验要素，可继续核对证件照片与最近一次申请记录。";
        }
        if (latestApply == null)
        {
            return "当前还未提交实名认证申请，请先补充姓名、身份证、手机号和证件照片。";
        }
        if (REALNAME_STATUS_REJECTED.equals(latestApply.getApplyStatus()))
        {
            return "最近一次实名认证申请已被驳回，请根据驳回原因补充或修正材料后重新提交。";
        }
        if (REALNAME_STATUS_APPROVED.equals(latestApply.getApplyStatus()))
        {
            return "最近一次实名认证申请已通过，若页面状态未同步，请联系管理员核对劳动者档案。";
        }
        return "实名认证申请正在审核中，审核通过后会同步到劳动者档案。";
    }

    private String buildRealnameApplyRemark(YgbPerson worker, WorkerRealnameApply latestApply)
    {
        if (latestApply != null && REALNAME_STATUS_REJECTED.equals(latestApply.getApplyStatus())
            && StringUtils.isNotEmpty(latestApply.getRejectReason()))
        {
            return "重提来源：上次驳回，原因：" + latestApply.getRejectReason();
        }
        return "申请来源：劳动者端实名认证提交；档案姓名=" + firstNonBlank(worker.getPersonName(), "-");
    }

    private void normalizeRealnameSubmitRequest(WorkerRealnameSubmitRequest request)
    {
        request.setPersonName(trimToEmpty(request.getPersonName()));
        request.setMobile(trimToEmpty(request.getMobile()));
        request.setIdCard(trimToEmpty(request.getIdCard()).toUpperCase());
        request.setIdCardFrontUrl(trimToEmpty(request.getIdCardFrontUrl()));
        request.setIdCardBackUrl(trimToEmpty(request.getIdCardBackUrl()));
        request.setSelfieUrl(trimToEmpty(request.getSelfieUrl()));
        request.setSourceModule(trimToEmpty(request.getSourceModule()));
    }

    private void validateRealnameSubmitRequest(YgbPerson worker, WorkerRealnameSubmitRequest request)
    {
        if (StringUtils.isEmpty(request.getPersonName()) || StringUtils.isEmpty(request.getMobile())
            || StringUtils.isEmpty(request.getIdCard()) || StringUtils.isEmpty(request.getIdCardFrontUrl())
            || StringUtils.isEmpty(request.getIdCardBackUrl()) || StringUtils.isEmpty(request.getSelfieUrl()))
        {
            throw new ServiceException("Real-name fields and image materials are required.");
        }
        if (!REALNAME_PERSON_NAME_PATTERN.matcher(request.getPersonName()).matches())
        {
            throw new ServiceException("Person name format is invalid. Use 2-20 Chinese or English characters.");
        }
        if (!REALNAME_MOBILE_PATTERN.matcher(request.getMobile()).matches())
        {
            throw new ServiceException("Mobile format is invalid. Use an 11-digit mobile number.");
        }
        if (!REALNAME_IDCARD_PATTERN.matcher(request.getIdCard()).matches())
        {
            throw new ServiceException("ID card format is invalid.");
        }
        if (StringUtils.isNotEmpty(worker.getPersonName()) && !request.getPersonName().equals(trimToEmpty(worker.getPersonName())))
        {
            throw new ServiceException("Person name does not match the worker archive.");
        }
        if (StringUtils.isNotEmpty(worker.getMobile()) && !request.getMobile().equals(trimToEmpty(worker.getMobile())))
        {
            throw new ServiceException("Mobile does not match the worker archive.");
        }
        if (StringUtils.isNotEmpty(worker.getIdCard())
            && !request.getIdCard().equals(trimToEmpty(worker.getIdCard()).toUpperCase()))
        {
            throw new ServiceException("ID card does not match the worker archive.");
        }
    }

    private WorkerSetting initWorkerSetting(YgbPerson worker, SysUser user)
    {
        WorkerSetting setting = new WorkerSetting();
        setting.setUserId(user.getUserId());
        setting.setPersonId(worker.getPersonId());
        setting.setCreateBy(user.getUserName());
        return setting;
    }

    private void fillPushRegistration(WorkerSetting setting, WorkerPushRegisterRequest request)
    {
        if (request == null)
        {
            return;
        }
        if (StringUtils.isNotEmpty(request.getPushClientId()))
        {
            setting.setPushClientId(request.getPushClientId().trim());
        }
        if (StringUtils.isNotEmpty(request.getNotificationPermission()))
        {
            setting.setNotificationPermission(request.getNotificationPermission().trim());
        }
        if (StringUtils.isNotEmpty(request.getPushPlatform()))
        {
            setting.setPushPlatform(request.getPushPlatform().trim());
        }
    }

    private boolean isPushRegistered(WorkerSetting setting)
    {
        return setting != null && StringUtils.isNotEmpty(setting.getPushClientId());
    }

    private Map<String, Object> buildPushGatewayStatusSnapshot()
    {
        String gatewayEnabled = nvl(sysConfigService.selectConfigByKey(CONFIG_PUSH_GATEWAY_ENABLED), "false");
        String gatewayUrl = nvl(sysConfigService.selectConfigByKey(CONFIG_PUSH_GATEWAY_URL), "");
        String gatewayProvider = firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_PUSH_GATEWAY_PROVIDER), "custom");
        boolean gatewayReady = isGatewayReady(gatewayEnabled, gatewayUrl);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pushGatewayEnabled", isTrueFlag(gatewayEnabled));
        result.put("pushGatewayUrlConfigured", StringUtils.isNotEmpty(gatewayUrl));
        result.put("pushGatewayProvider", gatewayProvider);
        result.put("pushGatewayReady", gatewayReady);
        result.put("pushGatewayStatusText", resolvePushGatewayStatusText(gatewayEnabled, gatewayUrl, gatewayProvider));
        return result;
    }

    private String buildPushTraceId()
    {
        return "worker-push-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private void appendLatestPushTestRecord(Map<String, Object> result, Long userId)
    {
        WorkerPushTestRecord record = workerProfileMapper.selectLatestWorkerPushTestRecord(userId);
        List<WorkerPushTestRecord> records = workerProfileMapper.selectWorkerPushTestRecordList(userId, 5);
        if (records == null)
        {
            records = new ArrayList<>();
        }
        if (record == null)
        {
            result.put("lastServerPushTest", null);
            result.put("lastServerPushTests", new ArrayList<>());
            return;
        }
        result.put("lastServerPushTest", pushTestRecordMap(record));
        List<Map<String, Object>> history = new ArrayList<>();
        for (WorkerPushTestRecord item : records)
        {
            history.add(pushTestRecordMap(item));
        }
        result.put("lastServerPushTests", history);
    }

    private Map<String, Object> pushTestRecordMap(WorkerPushTestRecord record)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", record.getRecordId());
        result.put("traceId", nvl(record.getTraceId(), ""));
        result.put("testedAt", formatDateTime(record.getCreateTime()));
        result.put("pushClientIdMasked", nvl(record.getPushClientIdMasked(), ""));
        result.put("pushPlatform", nvl(record.getPushPlatform(), ""));
        result.put("notificationPermission", nvl(record.getNotificationPermission(), ""));
        result.put("notifyEnabled", "1".equals(record.getNotifyEnabled()));
        result.put("gatewayUrl", nvl(record.getGatewayUrl(), ""));
        result.put("provider", firstNonBlank(record.getGatewayProvider(), "custom"));
        result.put("targetPath", nvl(record.getTargetPath(), ""));
        result.put("targetQueryText", nvl(record.getTargetQueryText(), ""));
        result.put("actionLabel", nvl(record.getActionLabel(), ""));
        result.put("sourceLabel", nvl(record.getSourceLabel(), ""));
        result.put("requestBody", nvl(record.getRequestBody(), ""));
        result.put("responseBody", nvl(record.getResponseBody(), ""));
        result.put("testStatus", nvl(record.getTestStatus(), ""));
        result.put("statusMessage", nvl(record.getStatusMessage(), ""));
        result.put("testStatusText", resolvePushTestStatusText(record.getTestStatus(), record.getStatusMessage()));
        return result;
    }

    private void saveWorkerPushTestRecord(YgbPerson worker, SysUser user, WorkerSetting setting,
        Map<String, Object> gatewaySnapshot, String traceId, String targetPath, String targetQueryText,
        String actionLabel, String sourceLabel, Object requestBody, Object responseBody, String testStatus,
        String statusMessage)
    {
        WorkerPushTestRecord record = new WorkerPushTestRecord();
        record.setUserId(user.getUserId());
        record.setPersonId(worker.getPersonId());
        record.setPersonName(worker.getPersonName());
        record.setTraceId(traceId);
        record.setPushClientIdMasked(maskPushClientId(setting == null ? null : setting.getPushClientId()));
        record.setPushPlatform(setting == null ? "" : nvl(setting.getPushPlatform(), ""));
        record.setNotificationPermission(setting == null ? "" : nvl(setting.getNotificationPermission(), ""));
        record.setNotifyEnabled(setting == null ? "0" : ("1".equals(setting.getNotifyEnabled()) ? "1" : "0"));
        record.setGatewayProvider(gatewaySnapshot == null ? "custom" : String.valueOf(gatewaySnapshot.get("pushGatewayProvider")));
        record.setGatewayUrl(nvl(sysConfigService.selectConfigByKey(CONFIG_PUSH_GATEWAY_URL), ""));
        record.setTargetPath(targetPath);
        record.setTargetQueryText(targetQueryText);
        record.setActionLabel(actionLabel);
        record.setSourceLabel(sourceLabel);
        record.setRequestBody(stringifyPushTestField(requestBody));
        record.setResponseBody(stringifyPushTestField(responseBody));
        record.setTestStatus(testStatus);
        record.setStatusMessage(statusMessage);
        record.setCreateBy(user.getUserName());
        workerProfileMapper.insertWorkerPushTestRecord(record);
    }

    private Object buildPushGatewayRequestBodySnapshot(WorkerSetting setting, WorkerPushTestRequest request,
        Map<String, Object> payload, Map<String, Object> metadata)
    {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("provider", firstNonBlank(sysConfigService.selectConfigByKey(CONFIG_PUSH_GATEWAY_PROVIDER), "custom"));
        requestBody.put("clientId", setting == null ? "" : setting.getPushClientId());
        requestBody.put("title", resolvePushTitle(request));
        requestBody.put("content", resolvePushContent(request));
        requestBody.put("payload", payload);
        if (metadata != null && !metadata.isEmpty())
        {
            requestBody.put("metadata", metadata);
        }
        return requestBody;
    }

    private String stringifyPushTestField(Object value)
    {
        if (value == null)
        {
            return "";
        }
        if (value instanceof String text)
        {
            return text;
        }
        return JSON.toJSONString(value);
    }

    private String resolvePushTestStatusText(String testStatus, String statusMessage)
    {
        if ("SUCCESS".equalsIgnoreCase(testStatus))
        {
            return firstNonBlank(statusMessage, "服务端推送网关已受理本次测试请求，请在真机上确认送达和点击落页。");
        }
        if ("FAIL".equalsIgnoreCase(testStatus))
        {
            return firstNonBlank(statusMessage, "测试推送发送失败");
        }
        return firstNonBlank(statusMessage, "");
    }

    private boolean isGatewayReady(String enabled, String gatewayUrl)
    {
        return isTrueFlag(enabled) && StringUtils.isNotEmpty(gatewayUrl);
    }

    private boolean isTrueFlag(String value)
    {
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    private String maskPushClientId(String pushClientId)
    {
        if (StringUtils.isEmpty(pushClientId))
        {
            return "未登记";
        }
        String normalized = pushClientId.trim();
        if (normalized.length() <= 8)
        {
            return normalized;
        }
        return normalized.substring(0, 4) + "..." + normalized.substring(normalized.length() - 4);
    }

    private String resolvePushStatusText(WorkerSetting setting)
    {
        if (!isPushRegistered(setting))
        {
            return "未完成 APP 推送登记，请先在真机登录并上报 push 标识。";
        }
        if ("0".equals(setting.getNotifyEnabled()))
        {
            return "已完成服务端登记，但当前账号已关闭业务通知偏好。";
        }
        if ("denied".equalsIgnoreCase(setting.getNotificationPermission()))
        {
            return "已完成服务端登记，但系统通知权限处于关闭状态。";
        }
        if ("config error".equalsIgnoreCase(setting.getNotificationPermission()))
        {
            return "已完成服务端登记，但原生端通知能力尚未正确配置。";
        }
        if (StringUtils.isEmpty(setting.getPushPlatform()))
        {
            return "已完成服务端登记，待终端补充推送平台信息后继续联调。";
        }
        if ("authorized".equalsIgnoreCase(setting.getNotificationPermission()))
        {
            return "已完成服务端登记，可继续进行真机推送送达与点击落页联调。";
        }
        if ("not determined".equalsIgnoreCase(setting.getNotificationPermission()))
        {
            return "已完成服务端登记，待系统确认通知授权状态后再验证送达。";
        }
        return "已完成服务端登记，建议继续在真机上验证消息送达与点击落页。";
    }

    private String resolvePushGatewayStatusText(String enabled, String gatewayUrl, String gatewayProvider)
    {
        if (!isTrueFlag(enabled))
        {
            return "服务端推送网关未启用，发送测试推送会直接失败。";
        }
        if (StringUtils.isEmpty(gatewayUrl))
        {
            return "服务端推送网关已启用，但网关地址未配置。";
        }
        return "服务端推送网关已就绪，当前 provider=" + firstNonBlank(gatewayProvider, "custom") + "。";
    }

    private Map<String, Object> buildPushPayload(WorkerPushTestRequest request)
    {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("jumpPath", resolvePushPath(request));
        Object jumpQuery = request == null ? null : request.getJumpQuery();
        if (jumpQuery == null)
        {
            Map<String, Object> defaultQuery = new LinkedHashMap<>();
            defaultQuery.put("from", "push");
            defaultQuery.put("scene", "test");
            jumpQuery = defaultQuery;
        }
        payload.put("jumpQuery", jumpQuery);
        payload.put("actionLabel", firstNonBlank(request == null ? null : request.getActionLabel(), "查看通知"));
        payload.put("sourceLabel", firstNonBlank(request == null ? null : request.getSourceLabel(), "服务端测试推送"));
        return payload;
    }

    private String resolvePushTitle(WorkerPushTestRequest request)
    {
        return firstNonBlank(request == null ? null : request.getTitle(), "粤工保劳动者端测试推送");
    }

    private String resolvePushContent(WorkerPushTestRequest request)
    {
        return firstNonBlank(request == null ? null : request.getContent(),
            "服务端推送发送链已触发，请点击消息验证 APP 落页是否正确。");
    }

    private String resolvePushPath(WorkerPushTestRequest request)
    {
        return firstNonBlank(request == null ? null : request.getJumpPath(), "/pages/notice/list");
    }

    private Map<String, Object> resumeMap(WorkerResume resume)
    {
        List<Map<String, Object>> certificateList = parseCertificateList(resume.getCertificateText());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("resumeId", resume.getResumeId());
        result.put("personName", resume.getPersonName());
        result.put("mobile", resume.getMobile());
        result.put("jobType", resume.getJobType());
        result.put("expectedJob", resume.getExpectedJob());
        result.put("expectedCity", resume.getExpectedCity());
        result.put("expectedSalary", resume.getExpectedSalary());
        result.put("skillTags", resume.getSkillTags());
        result.put("certificateText", resume.getCertificateText());
        result.put("certificateList", certificateList);
        result.put("certificateCount", certificateList.size());
        result.put("intro", resume.getIntro());
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

        for (String segment : splitCertificateSegments(certificateText))
        {
            String value = firstNonBlank(segment == null ? null : segment.trim(), "");
            if (StringUtils.isEmpty(value))
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

    private String normalizeCertificateText(String certificateText)
    {
        List<Map<String, Object>> certificateList = parseCertificateList(certificateText);
        if (certificateList.isEmpty())
        {
            return "";
        }
        return JSON.toJSONString(certificateList);
    }

    private List<String> splitCertificateSegments(String certificateText)
    {
        List<String> rows = new ArrayList<>();
        if (StringUtils.isEmpty(certificateText))
        {
            return rows;
        }
        String normalized = certificateText.replace("；", ",")
            .replace("，", ",")
            .replace("、", ",")
            .replace("\r", "\n");
        for (String line : normalized.split("[,\n]"))
        {
            if (StringUtils.isNotEmpty(line))
            {
                rows.add(line);
            }
        }
        return rows;
    }

    private String certificateStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "证书有效";
        }
        if ("2".equals(status))
        {
            return "证书临期";
        }
        if ("3".equals(status))
        {
            return "证书过期";
        }
        return "待校验证书";
    }

    private Map<String, Object> contractSummary(YgbContract contract)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("contractId", contract.getContractId());
        row.put("contractNo", contract.getContractNo());
        row.put("title", contractTypeText(contract.getContractType()) + " " + nvl(contract.getContractNo(), ""));
        row.put("contractType", contract.getContractType());
        row.put("contractTypeText", contractTypeText(contract.getContractType()));
        row.put("contractStatus", contract.getContractStatus());
        row.put("contractStatusText", contractStatusText(contract.getContractStatus()));
        row.put("dispatchEnterpriseName", nvl(contract.getDispatchEnterpriseName(), "待同步派遣单位"));
        row.put("employerEnterpriseName", nvl(contract.getEmployerEnterpriseName(), "待同步用工单位"));
        row.put("period", buildPeriodText(contract.getStartDate(), contract.getEndDate()));
        row.put("expireInDays", expireInDays(contract.getEndDate()));
        row.put("expireText", expireText(contract.getEndDate()));
        row.put("hasFile", StringUtils.isNotEmpty(contract.getContractFileUrl()));
        return row;
    }

    private List<Map<String, Object>> buildContractPreviewSections(YgbContract contract)
    {
        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(previewSection("合同主体",
            Arrays.asList("劳动者：" + nvl(contract.getPersonName(), "待同步"),
                "派遣单位：" + nvl(contract.getDispatchEnterpriseName(), "待同步"),
                "用工单位：" + nvl(contract.getEmployerEnterpriseName(), "待同步"),
                "合同类型：" + contractTypeText(contract.getContractType()))));
        sections.add(previewSection("合同期限",
            Arrays.asList("签订日期：" + formatDate(contract.getSignDate()),
                "生效日期：" + formatDate(contract.getStartDate()),
                "终止日期：" + formatDate(contract.getEndDate()),
                "合同状态：" + contractStatusText(contract.getContractStatus()))));
        sections.add(previewSection("报酬与备案",
            Arrays.asList("月工资标准：" + (contract.getMonthlyWage() == null ? "-" : contract.getMonthlyWage().setScale(2, RoundingMode.HALF_UP).toPlainString()),
                "备案编号：" + nvl(contract.getFilingNo(), "未备案"),
                "备案时间：" + formatDateTime(contract.getFilingTime()),
                "条款校验：" + clauseCheckStatusText(contract.getClauseCheckStatus()))));
        sections.add(previewSection("留痕信息",
            Arrays.asList("OCR 状态：" + ocrStatusText(contract.getOcrStatus()),
                "区块链哈希：" + nvl(contract.getBlockchainHash(), "待补充"),
                "备注：" + nvl(contract.getRemark(), "暂无补充说明"))));
        return sections;
    }

    private Map<String, Object> previewSection(String title, List<String> lines)
    {
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("title", title);
        section.put("lines", lines);
        return section;
    }

    private Map<String, Object> help(String articleKey, String title, String category, String summary, String path,
        String actionLabel)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("articleKey", articleKey);
        row.put("title", title);
        row.put("category", category);
        row.put("summary", summary);
        row.put("path", path);
        row.put("actionLabel", actionLabel);
        return row;
    }

    private Map<String, Object> helpDetail(String articleKey, String title, String category, List<String> paragraphs,
        String path, String actionLabel)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("articleKey", articleKey);
        row.put("title", title);
        row.put("category", category);
        row.put("paragraphs", paragraphs);
        row.put("path", path);
        row.put("actionLabel", actionLabel);
        return row;
    }

    private Map<String, Object> actionCard(String key, String label, String summary, String path)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("label", label);
        row.put("summary", summary);
        row.put("path", path);
        return row;
    }

    private Map<String, Object> serviceCard(String key, String title, String subtitle, String summary, String actionLabel,
        String path, String phoneNumber)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("title", title);
        row.put("subtitle", subtitle);
        row.put("summary", summary);
        row.put("actionLabel", actionLabel);
        row.put("path", path);
        row.put("phoneNumber", phoneNumber);
        return row;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castRows(Object rows)
    {
        if (rows instanceof List<?> list)
        {
            return (List<Map<String, Object>>) list;
        }
        return new ArrayList<>();
    }

    private void ensurePointLedger(YgbPerson worker, Long userId)
    {
        if (workerProfileMapper.countPointLedger(userId) > 0)
        {
            return;
        }
        insertPointLedger(worker, userId, "IN", "完成本月培训", "完成 10 道安全培训题，奖励积分。", new BigDecimal("20"), new BigDecimal("20"));
        insertPointLedger(worker, userId, "IN", "完成课程学习", "完成工伤预防基础课程，奖励积分。", new BigDecimal("48"), new BigDecimal("68"));
        insertPointLedger(worker, userId, "IN", "参与福利活动", "满足活动条件后获得额外积分。", new BigDecimal("120"), new BigDecimal("188"));
    }

    private void ensurePointGoods()
    {
        if (workerProfileMapper.countPointGoods() > 0)
        {
            return;
        }
        insertPointGoods("coupon-88", "88 元福利券", "满足条件后由平台发放福利券，适用于劳动者活动权益。", new BigDecimal("88"),
            "coupon", 30, 1);
        insertPointGoods("lecture-priority", "法律讲座优先名额", "兑换后可优先报名下一场法律公益讲座。", new BigDecimal("120"),
            "service", 50, 2);
        insertPointGoods("training-pack", "工伤培训礼包", "兑换后可获得培训资料包与课程优先提醒。", new BigDecimal("168"),
            "package", 80, 3);
    }

    private void insertPointLedger(YgbPerson worker, Long userId, String changeType, String title, String summary,
        BigDecimal scoreDelta, BigDecimal balanceAfter)
    {
        WorkerPointLedger ledger = new WorkerPointLedger();
        ledger.setUserId(userId);
        ledger.setPersonId(worker.getPersonId());
        ledger.setPersonName(worker.getPersonName());
        ledger.setChangeType(changeType);
        ledger.setTitle(title);
        ledger.setSummary(summary);
        ledger.setScoreDelta(scoreDelta);
        ledger.setBalanceAfter(balanceAfter);
        ledger.setCreateBy("system");
        workerProfileMapper.insertWorkerPointLedger(ledger);
    }

    private void insertPointGoods(String goodsKey, String goodsName, String goodsDesc, BigDecimal requiredScore,
        String goodsType, Integer stockCount, Integer sortNum)
    {
        WorkerPointGoods goods = new WorkerPointGoods();
        goods.setGoodsKey(goodsKey);
        goods.setGoodsName(goodsName);
        goods.setGoodsDesc(goodsDesc);
        goods.setRequiredScore(requiredScore);
        goods.setGoodsType(goodsType);
        goods.setStatus("1");
        goods.setStockCount(stockCount);
        goods.setSortNum(sortNum);
        goods.setCreateBy("system");
        workerProfileMapper.insertWorkerPointGoods(goods);
    }

    private Map<String, Object> reward(WorkerPointGoods goods, BigDecimal currentPoints)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("goodsKey", goods.getGoodsKey());
        row.put("title", goods.getGoodsName());
        row.put("desc", goods.getGoodsDesc());
        row.put("score", goods.getRequiredScore());
        row.put("goodsType", goods.getGoodsType());
        row.put("stockCount", goods.getStockCount());
        row.put("canExchange", currentPoints.compareTo(nvl(goods.getRequiredScore())) >= 0 && defaultInt(goods.getStockCount()) > 0);
        row.put("disabledReason", rewardDisabledReason(goods, currentPoints));
        return row;
    }

    private String firstNonBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value))
            {
                return value;
            }
        }
        return null;
    }

    private String buildFeedbackHandleRemark(String status, String replyContent)
    {
        String label = "1".equals(status) ? "已处理" : "重新打开";
        String content = StringUtils.isEmpty(replyContent) ? "待继续跟进" : replyContent.trim();
        return "反馈处理：" + label + "；" + content;
    }

    private String trimToEmpty(String value)
    {
        return value == null ? "" : value.trim();
    }

    private Date firstNonNull(Date... values)
    {
        for (Date value : values)
        {
            if (value != null)
            {
                return value;
            }
        }
        return null;
    }

    private String insuranceStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "已参保";
        }
        if ("2".equals(status))
        {
            return "停保";
        }
        return "未参保";
    }

    private String policyStatusText(String status)
    {
        if ("0".equals(status))
        {
            return "未生效";
        }
        if ("1".equals(status))
        {
            return "有效";
        }
        if ("2".equals(status))
        {
            return "即将到期";
        }
        if ("3".equals(status))
        {
            return "已过期";
        }
        return "暂未匹配保单";
    }

    private String contractTypeText(String contractType)
    {
        if ("1".equals(contractType))
        {
            return "劳动合同";
        }
        if ("2".equals(contractType))
        {
            return "派遣协议";
        }
        if ("3".equals(contractType))
        {
            return "用工协议";
        }
        return "合同";
    }

    private String contractStatusText(String status)
    {
        if ("0".equals(status))
        {
            return "草稿";
        }
        if ("1".equals(status))
        {
            return "待备案";
        }
        if ("2".equals(status))
        {
            return "已备案";
        }
        if ("3".equals(status))
        {
            return "已驳回";
        }
        if ("4".equals(status))
        {
            return "已到期";
        }
        if ("5".equals(status))
        {
            return "已解除";
        }
        return "未知状态";
    }

    private String ocrStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "识别成功";
        }
        if ("2".equals(status))
        {
            return "识别失败";
        }
        return "未识别";
    }

    private String clauseCheckStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "校验通过";
        }
        if ("2".equals(status))
        {
            return "条款缺失";
        }
        return "未校验";
    }

    private String buildPeriodText(Date startDate, Date endDate)
    {
        if (startDate == null || endDate == null)
        {
            return "暂无有效期";
        }
        return DATE_FORMAT.format(startDate) + " 至 " + DATE_FORMAT.format(endDate);
    }

    private String formatDate(Date date)
    {
        return date == null ? "-" : DATE_FORMAT.format(date);
    }

    private String formatDateTime(Date date)
    {
        return date == null ? "-" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private Integer expireInDays(Date endDate)
    {
        if (endDate == null)
        {
            return null;
        }
        long diff = endDate.getTime() - System.currentTimeMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    private String expireText(Date endDate)
    {
        Integer days = expireInDays(endDate);
        if (days == null)
        {
            return "未设置到期日";
        }
        if (days < 0)
        {
            return "已到期";
        }
        if (days == 0)
        {
            return "今日到期";
        }
        return "还有 " + days + " 天到期";
    }

    private String formatMoney(BigDecimal value)
    {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatPercent(BigDecimal value)
    {
        return value == null ? "-" : value.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    private JSONObject parseRawPayload(String rawPayload)
    {
        if (StringUtils.isEmpty(rawPayload))
        {
            return null;
        }
        try
        {
            return JSON.parseObject(rawPayload);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private AqCoverageSnapshot resolveCoverageSnapshot(YgbAqInsurance policy, JSONObject payload)
    {
        if (policy == null)
        {
            return new AqCoverageSnapshot("暂未匹配保单", "当前未查询到安责险保单，页面仅保留工伤参保状态。", "未匹配保单",
                "MISSING_POLICY", "未匹配保单", new ArrayList<>());
        }
        String payloadText = firstNonBlank(jsonString(payload, "coverageAmount"), jsonString(payload, "coverageText"),
            jsonString(payload, "coverageSummary"), jsonString(payload, "coverageScope"), jsonString(payload, "insuredAmount"),
            jsonString(payload, "perPersonCoverage"), jsonString(payload, "coverageLimit"));
        List<Map<String, Object>> coverageDetail = buildCoverageDetail(payload);
        int insuredPersonCount = defaultInt(policy.getInsuredPersonCount());
        String coverageSummary;
        if (StringUtils.isNotEmpty(payloadText))
        {
            coverageSummary = payloadText;
        }
        else if ("0".equals(policy.getPolicyStatus()))
        {
            coverageSummary = "保单生效后预计覆盖 " + insuredPersonCount + " 人";
        }
        else if ("2".equals(policy.getPolicyStatus()))
        {
            coverageSummary = "当前覆盖 " + insuredPersonCount + " 人，保单即将到期";
        }
        else if ("3".equals(policy.getPolicyStatus()))
        {
            coverageSummary = "最近一张保单覆盖 " + insuredPersonCount + " 人，当前已过期";
        }
        else
        {
            coverageSummary = "当前覆盖 " + insuredPersonCount + " 人";
        }

        if (!coverageDetail.isEmpty())
        {
            return new AqCoverageSnapshot(coverageSummary, "当前保障范围和保额明细已由来源接口回写，页面同时展示保单状态、保费、有效期和事故预防资金。",
                "来源已回写保障范围与保额明细", "SYNCED", "已同步保额明细", coverageDetail);
        }
        if (StringUtils.isNotEmpty(payloadText))
        {
            return new AqCoverageSnapshot(coverageSummary, "当前承保范围摘要已由来源接口回写，但保额明细尚未同步。",
                "来源已回写保障摘要", "SUMMARY_ONLY", "仅同步摘要", coverageDetail);
        }
        return new AqCoverageSnapshot(coverageSummary, "当前保单尚未同步统一保额明细，页面先展示覆盖人数、保费、有效期和事故预防资金。",
            "来源未回写保额明细", "DETAIL_MISSING", "待同步保额明细", coverageDetail);
    }

    private List<Map<String, Object>> buildCoverageDetail(JSONObject payload)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        addCoverageDetail(rows, payload, "insuredAmount", "总保额");
        addCoverageDetail(rows, payload, "perPersonCoverage", "人均保额");
        addCoverageDetail(rows, payload, "deathAmount", "身故保额");
        addCoverageDetail(rows, payload, "disabilityAmount", "伤残保额");
        addCoverageDetail(rows, payload, "medicalAmount", "医疗保额");
        addCoverageDetail(rows, payload, "coverageLimit", "责任限额");
        return rows;
    }

    private void addCoverageDetail(List<Map<String, Object>> rows, JSONObject payload, String key, String label)
    {
        String value = jsonString(payload, key);
        if (StringUtils.isEmpty(value))
        {
            return;
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("label", label);
        row.put("value", value);
        rows.add(row);
    }

    private String jsonString(JSONObject payload, String key)
    {
        if (payload == null || !payload.containsKey(key))
        {
            return null;
        }
        Object value = payload.get(key);
        if (value == null)
        {
            return null;
        }
        String text = String.valueOf(value).trim();
        return StringUtils.isEmpty(text) ? null : text;
    }

    private String syncSourceStatusText(String status)
    {
        if ("SUCCESS".equalsIgnoreCase(status) || "1".equals(status))
        {
            return "同步成功";
        }
        if ("FAIL".equalsIgnoreCase(status) || "2".equals(status))
        {
            return "同步失败";
        }
        if ("PROCESSING".equalsIgnoreCase(status) || "3".equals(status))
        {
            return "处理中";
        }
        return "待同步";
    }

    private String nvl(String value, String defaultValue)
    {
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private BigDecimal currentPoints(List<WorkerPointLedger> ledgers)
    {
        BigDecimal balance = BigDecimal.ZERO;
        for (WorkerPointLedger ledger : ledgers)
        {
            balance = ledger.getBalanceAfter() == null ? balance : ledger.getBalanceAfter();
        }
        return balance;
    }

    private BigDecimal nvl(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static class AqCoverageSnapshot
    {
        private final String coverageSummary;

        private final String coverageHint;

        private final String coverageSourceText;

        private final String coverageSyncStatus;

        private final String coverageSyncStatusText;

        private final List<Map<String, Object>> coverageDetail;

        private AqCoverageSnapshot(String coverageSummary, String coverageHint, String coverageSourceText,
            String coverageSyncStatus, String coverageSyncStatusText, List<Map<String, Object>> coverageDetail)
        {
            this.coverageSummary = coverageSummary;
            this.coverageHint = coverageHint;
            this.coverageSourceText = coverageSourceText;
            this.coverageSyncStatus = coverageSyncStatus;
            this.coverageSyncStatusText = coverageSyncStatusText;
            this.coverageDetail = coverageDetail;
        }

        public String getCoverageSummary()
        {
            return coverageSummary;
        }

        public String getCoverageHint()
        {
            return coverageHint;
        }

        public String getCoverageSourceText()
        {
            return coverageSourceText;
        }

        public String getCoverageSyncStatus()
        {
            return coverageSyncStatus;
        }

        public String getCoverageSyncStatusText()
        {
            return coverageSyncStatusText;
        }

        public List<Map<String, Object>> getCoverageDetail()
        {
            return coverageDetail;
        }
    }

    private String rewardDisabledReason(WorkerPointGoods goods, BigDecimal currentPoints)
    {
        if (defaultInt(goods.getStockCount()) <= 0)
        {
            return "库存不足";
        }
        if (currentPoints.compareTo(nvl(goods.getRequiredScore())) < 0)
        {
            return "积分不足";
        }
        return "";
    }

    private String exchangeStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "已兑换";
        }
        if ("2".equals(status))
        {
            return "已发放";
        }
        return "待处理";
    }

    private String resolveDeliveryRemark(WorkerPointGoods goods)
    {
        if ("coupon".equals(goods.getGoodsType()))
        {
            return "平台将在 1 个工作日内发放福利券。";
        }
        if ("service".equals(goods.getGoodsType()))
        {
            return "平台将在下一场讲座开放时优先保留名额。";
        }
        return "兑换成功后，平台会在 1 个工作日内开通资料包下载与课程提醒，可在帮助中心或通知页查看发放结果。";
    }

    private boolean isRealNameVerified(YgbPerson worker)
    {
        return worker != null
            && StringUtils.isNotEmpty(worker.getPersonName())
            && StringUtils.isNotEmpty(worker.getIdCard())
            && StringUtils.isNotEmpty(worker.getMobile());
    }

    private String maskName(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return "";
        }
        if (value.length() <= 1)
        {
            return value;
        }
        if (value.length() == 2)
        {
            return value.charAt(0) + "*";
        }
        return value.charAt(0) + "*" + value.charAt(value.length() - 1);
    }

    private String maskMobile(String value)
    {
        if (StringUtils.isEmpty(value) || value.length() < 7)
        {
            return firstNonBlank(value, "");
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    private String maskIdCard(String value)
    {
        if (StringUtils.isEmpty(value) || value.length() < 8)
        {
            return firstNonBlank(value, "");
        }
        return value.substring(0, 4) + "**********" + value.substring(value.length() - 4);
    }
}
