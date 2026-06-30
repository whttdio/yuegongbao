package com.yuegongbao.ygb.app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.service.ISysUserService;
import com.yuegongbao.ygb.app.domain.vo.AppEnterpriseActionRequest;
import com.yuegongbao.ygb.app.service.AppEnterpriseService;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;
import com.yuegongbao.ygb.credit.service.IYgbCreditScoreService;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.domain.YgbPersonSummary;
import com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService;
import com.yuegongbao.ygb.foundation.service.IYgbPersonService;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.IWorkerJobPostService;

@Service
public class AppEnterpriseServiceImpl implements AppEnterpriseService
{
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String RECORD_TYPE_ENTERPRISE_ACTION = "APP_ENTERPRISE_ACTION";
    private static final String RECORD_TYPE_ENTERPRISE_EXPORT = "APP_ENTERPRISE_EXPORT";
    private static final String STATUS_NORMAL = "0";

    @Autowired
    private IYgbEnterpriseService enterpriseService;

    @Autowired
    private IYgbPersonService personService;

    @Autowired
    private IYgbDeviceService deviceService;

    @Autowired
    private IYgbSalaryBatchService salaryBatchService;

    @Autowired
    private IYgbCreditScoreService creditScoreService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @Autowired
    private WorkerJobMapper workerJobMapper;

    @Autowired
    private IWorkerJobPostService workerJobPostService;

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Autowired
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public Map<String, Object> getHomeDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbEnterprise enterprise = enterpriseId == null ? null : enterpriseService.selectEnterpriseById(enterpriseId);
        YgbCreditScore creditScore = enterpriseId == null ? null : creditScoreService.selectLatestCreditScore(enterpriseId);
        YgbPersonSummary personSummary = personService.selectPersonSummary(buildPersonQuery(enterpriseId));
        YgbDeviceSummary deviceSummary = deviceService.selectDeviceSummary(buildDeviceQuery(enterpriseId));
        YgbSalaryBatchSummary salarySummary = salaryBatchService.selectSalaryBatchSummary(buildSalaryQuery(enterpriseId));
        List<YgbModuleRecord> pendingActions = selectActionRecords(enterpriseId, "pending");
        List<WorkerJobPost> jobPosts = selectEnterpriseJobs(enterpriseId);

        Map<String, Object> hero = new LinkedHashMap<>();
        hero.put("healthScore", formatScore(creditScore == null ? null : creditScore.getTotalScore(), "92"));
        hero.put("warningCount", String.valueOf(deviceSummary == null ? 0 : safeInt(deviceSummary.getLockedOrFaultCount())
            + safeInt(deviceSummary.getAuthDeniedCount())));
        hero.put("taskCount", String.valueOf(pendingActions.size() + safeInt(salarySummary == null ? 0 : salarySummary.getPendingSubmitCount())));
        hero.put("todoCount", String.valueOf(pendingActions.size() + safeInt(personSummary == null ? 0 : personSummary.getCertRiskCount())));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hero", hero);
        result.put("quickEntries", List.of(
            quickEntry("salary", "工资确认", "薪", "gold", "/pages/enterprise/salary-confirm"),
            quickEntry("approval", "作业审批", "批", "teal", "/pages/enterprise/operation-approval"),
            quickEntry("job", "岗位发布", "招", "blue", "/pages/enterprise/job-publish"),
            quickEntry("warning", "设备台账", "设", "rose", "/pages/enterprise/device-ledger")));
        result.put("dataCards", List.of(
            dataCard("人员在岗", String.valueOf(safeInt(personSummary == null ? 0 : personSummary.getOnPostCount())),
                safeText(enterprise == null ? null : enterprise.getEnterpriseName(), "当前企业")),
            dataCard("设备在线率", buildRate(safeInt(deviceSummary == null ? 0 : deviceSummary.getOnlineCount()),
                safeInt(deviceSummary == null ? 0 : deviceSummary.getTotalCount())),
                "在线 " + safeInt(deviceSummary == null ? 0 : deviceSummary.getOnlineCount()) + " / "
                    + safeInt(deviceSummary == null ? 0 : deviceSummary.getTotalCount())),
            dataCard("参保覆盖", buildInsuranceRate(personSummary),
                "漏保 " + safeInt(personSummary == null ? 0 : personSummary.getUninsuredCount()) + " 人待核验"),
            dataCard("培训待办", String.valueOf(countActionByCategory(pendingActions, "training")),
                "月度培训计划待处理"),
            dataCard("工资待确认", String.valueOf(safeInt(salarySummary == null ? 0 : salarySummary.getPendingSubmitCount())),
                "待提交批次"),
            dataCard("招聘岗位", String.valueOf(jobPosts.size()), "企业岗位库")));
        result.put("warnings", buildHomeWarnings(personSummary, deviceSummary, salarySummary, pendingActions));
        return result;
    }

    @Override
    public Map<String, Object> getWorkbenchDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sections", List.of(
            section("人员管理", "P0", List.of(
                quickEntry("people", "员工花名册", "人", "teal", "/pages/enterprise/people-manage"),
                quickEntry("entry", "入离职登记", "岗", "blue", "/pages/enterprise/people-manage"),
                quickEntry("cert", "证件管理", "证", "gold", "/pages/enterprise/people-manage"),
                quickEntry("insurance", "参保核验", "保", "green", "/pages/enterprise/insurance-manage"))),
            section("设备与作业", "P0", List.of(
                quickEntry("device", "设备台账", "设", "cyan", "/pages/enterprise/device-ledger"),
                quickEntry("repair", "故障报修", "修", "orange", "/pages/enterprise/device-ledger"),
                quickEntry("approval", "作业审批", "批", "rose", "/pages/enterprise/operation-approval"),
                quickEntry("iot", "物联卡缴费", "卡", "indigo", "/pages/enterprise/device-ledger"))),
            section("工资与招聘", "P1", List.of(
                quickEntry("salary", "工资确认", "薪", "gold", "/pages/enterprise/salary-confirm"),
                quickEntry("salary-import", "工资导入", "导", "teal", "/pages/enterprise/salary-confirm"),
                quickEntry("job", "岗位发布", "招", "blue", "/pages/enterprise/job-publish"),
                quickEntry("resume", "简历库", "简", "violet", "/pages/enterprise/job-publish"))),
            section("保险与培训", "P1", List.of(
                quickEntry("injury", "工伤保险", "伤", "green", "/pages/enterprise/insurance-manage"),
                quickEntry("aq", "安责险投保", "险", "rose", "/pages/enterprise/insurance-manage"),
                quickEntry("plan", "培训计划", "训", "orange", "/pages/enterprise/training-manage"),
                quickEntry("exam", "考试证书", "证", "cyan", "/pages/enterprise/training-manage")))));
        result.put("enterpriseId", enterpriseId);
        return result;
    }

    @Override
    public Map<String, Object> getPeopleLedger(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        List<YgbPerson> list = personService.selectPersonList(buildPersonQuery(enterpriseId));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbPerson item : list)
        {
            rows.add(personRow(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("filters", List.of(
            filter("all", "全部"),
            filter("active", "在职"),
            filter("certificateDue", "临期证件"),
            filter("insurancePending", "待参保")));
        result.put("activeFilter", "all");
        result.put("list", rows);
        return result;
    }

    @Override
    public Map<String, Object> submitPeopleAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveActionRecord(user, enterpriseId, "PERSON_ACTION",
            firstNonBlank(request == null ? null : request.getActionType(), "person-action"),
            "企业人员处理", request, request == null ? null : request.getPersonIds(), "processing");
        return successResult("已提交 " + humanAction(request == null ? null : request.getActionType(), "人员处理")
            + "，共 " + countIds(request == null ? null : request.getPersonIds()) + " 人。", record.getRecordId());
    }

    @Override
    public Map<String, Object> exportPeopleLedger(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveExportRecord(user, enterpriseId, "PERSON_EXPORT", "企业人员花名册导出", request);
        return successResult("人员花名册导出任务已登记。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getDeviceLedger(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        List<YgbDevice> list = deviceService.selectDeviceList(buildDeviceQuery(enterpriseId));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbDevice item : list)
        {
            rows.add(deviceRow(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", rows);
        return result;
    }

    @Override
    public Map<String, Object> submitDeviceAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveActionRecord(user, enterpriseId, "DEVICE_ACTION",
            firstNonBlank(request == null ? null : request.getActionType(), "device-action"),
            "企业设备处理", request, request == null ? null : request.getDeviceIds(), "processing");
        return successResult("已提交 " + humanAction(request == null ? null : request.getActionType(), "设备处理")
            + "，共 " + countIds(request == null ? null : request.getDeviceIds()) + " 台设备。", record.getRecordId());
    }

    @Override
    public Map<String, Object> exportDeviceLedger(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveExportRecord(user, enterpriseId, "DEVICE_EXPORT", "企业设备台账导出", request);
        return successResult("设备台账导出任务已登记。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getSalaryDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbSalaryBatch query = buildSalaryQuery(enterpriseId);
        List<YgbSalaryBatch> list = salaryBatchService.selectSalaryBatchList(query);
        YgbSalaryBatchSummary summary = salaryBatchService.selectSalaryBatchSummary(query);

        List<Map<String, Object>> batchRows = new ArrayList<>();
        for (YgbSalaryBatch item : list)
        {
            batchRows.add(salaryBatchRow(item));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", List.of(
            summaryItem("待确认批次", String.valueOf(summary.getPendingGenerateCount() + summary.getPendingSubmitCount())),
            summaryItem("待发人数", String.valueOf(summary.getTotalPersonCount())),
            summaryItem("待发总额", formatCurrency(summary.getAccountGapAmount().compareTo(BigDecimal.ZERO) > 0
                ? summary.getAccountGapAmount() : summary.getRegulatorAccountBalance()))));
        result.put("batches", batchRows);
        return result;
    }

    @Override
    public Map<String, Object> submitSalaryConfirm(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long[] batchIds = request == null ? null : request.getBatchIds();
        if (batchIds == null || batchIds.length == 0)
        {
            throw new ServiceException("请先选择工资批次。");
        }

        int confirmedCount = 0;
        for (Long batchId : batchIds)
        {
            YgbSalaryBatch batch = salaryBatchService.selectSalaryBatchById(batchId);
            if (batch == null)
            {
                continue;
            }
            if (!"1".equals(batch.getAccountStatus()))
            {
                BigDecimal received = batch.getTotalPayableAmount() == null || batch.getTotalPayableAmount().compareTo(BigDecimal.ZERO) <= 0
                    ? batch.getTotalPaidAmount() : batch.getTotalPayableAmount();
                if (received == null || received.compareTo(BigDecimal.ZERO) <= 0)
                {
                    received = new BigDecimal("1.00");
                }
                salaryBatchService.confirmAccount(batchId, received, "APP-" + batch.getBatchNo(), user.getUserName());
                batch = salaryBatchService.selectSalaryBatchById(batchId);
            }
            try
            {
                salaryBatchService.submitBatch(batchId, user.getUserName());
            }
            catch (ServiceException ex)
            {
                saveActionRecord(user, resolveEnterpriseId(user, worker), "SALARY_CONFIRM_FAIL", batch.getBatchNo(),
                    "工资确认失败", Map.of("batchId", batchId, "message", ex.getMessage()), new Long[] { batchId }, "pending");
                throw ex;
            }
            confirmedCount++;
        }
        return successResult("已完成 " + confirmedCount + " 个工资批次确认提交。", null);
    }

    @Override
    public Map<String, Object> importSalaryDraft(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveActionRecord(user, enterpriseId, "SALARY_IMPORT",
            firstNonBlank(request == null ? null : request.getBatchMonth(), currentMonth()),
            "工资导入草稿", request, request == null ? null : request.getBatchIds(), "draft");
        return successResult("工资明细导入登记成功，后续可在管理端继续完善。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getOperationApprovalDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        List<YgbModuleRecord> records = selectRecordsByType(enterpriseId, "WORKER_OUTWORK_APPLY");
        if (records.isEmpty())
        {
            records = selectActionRecords(enterpriseId, "pending");
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbModuleRecord item : records)
        {
            rows.add(operationRow(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", rows);
        return result;
    }

    @Override
    public Map<String, Object> submitOperationApproval(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        Long[] approvalIds = request == null ? null : request.getApprovalIds();
        if (approvalIds == null || approvalIds.length == 0)
        {
            throw new ServiceException("请先选择作业审批记录。");
        }
        String workflowStatus = "approved".equalsIgnoreCase(request.getDecision()) ? "closed" : "processing";
        for (Long approvalId : approvalIds)
        {
            YgbModuleRecord target = moduleRecordService.selectModuleRecordById(approvalId);
            if (target == null)
            {
                continue;
            }
            target.setWorkflowStatus(workflowStatus);
            target.setRemark(limitText("企业端审批：" + firstNonBlank(request.getDecision(), "pending"), 500));
            moduleRecordService.updateModuleRecord(target, user.getUserName());
        }
        YgbModuleRecord audit = saveActionRecord(user, enterpriseId, "OPERATION_APPROVAL",
            firstNonBlank(request.getDecision(), "pending"), "企业作业审批", request, approvalIds, workflowStatus);
        return successResult("已提交 " + approvalIds.length + " 条作业审批结果。", audit.getRecordId());
    }

    @Override
    public Map<String, Object> exportOperationLedger(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveExportRecord(user, enterpriseId, "OPERATION_EXPORT", "作业审批台账导出", Map.of());
        return successResult("作业审批台账导出任务已登记。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getInsuranceDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbPersonSummary personSummary = personService.selectPersonSummary(buildPersonQuery(enterpriseId));
        YgbAqInsurance policy = enterpriseId == null ? null : aqInsuranceMapper.selectLatestAqInsuranceByEnterpriseId(enterpriseId);

        List<Map<String, Object>> summary = new ArrayList<>();
        summary.add(summaryItem("工伤保险参保率", buildInsuranceRate(personSummary)));
        summary.add(summaryItem("安责险有效期", policy == null || policy.getEndDate() == null ? "待同步"
            : DateTimeFormatter.ofPattern("yyyy-MM-dd").format(policy.getEndDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate())));
        summary.add(summaryItem("待补缴人数", String.valueOf(safeInt(personSummary == null ? 0 : personSummary.getUninsuredCount()))));
        summary.add(summaryItem("待续保项目", policy != null && "2".equals(policy.getPolicyStatus()) ? "1" : "0"));

        List<Map<String, Object>> actions = new ArrayList<>();
        if (safeInt(personSummary == null ? 0 : personSummary.getUninsuredCount()) > 0)
        {
            actions.add(actionRow(1L, "补缴人员清单",
                "当前仍有 " + safeInt(personSummary.getUninsuredCount()) + " 人待核验参保状态。", "补缴办理", "worker-tag--warning"));
        }
        if (policy != null)
        {
            actions.add(actionRow(2L, "安责险续保提醒",
                "保单号 " + safeText(policy.getPolicyNo(), "待同步") + "，状态 " + insurancePolicyStatusText(policy.getPolicyStatus()) + "。",
                "续保跟进", "worker-tag--info"));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("actions", actions);
        return result;
    }

    @Override
    public Map<String, Object> submitInsuranceAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveActionRecord(user, enterpriseId, "INSURANCE_ACTION",
            firstNonBlank(request == null ? null : request.getActionType(), "insurance-action"),
            "企业保险处理", request, null, "processing");
        return successResult("保险办理请求已登记。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getTrainingDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        List<YgbModuleRecord> plans = selectRecordsByType(enterpriseId, "PREVENTION_TRAINING");
        if (plans.isEmpty())
        {
            plans = selectActionRecords(enterpriseId, "pending");
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YgbModuleRecord item : plans)
        {
            rows.add(trainingPlanRow(item));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", List.of(
            summaryItem("本月计划", String.valueOf(plans.size())),
            summaryItem("未完成人员", String.valueOf(safeInt(personService.selectPersonSummary(buildPersonQuery(enterpriseId)).getCertRiskCount()))),
            summaryItem("完成率", buildTrainingCompletionRate(plans))));
        result.put("plans", rows);
        return result;
    }

    @Override
    public Map<String, Object> saveTrainingPlanDraft(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveActionRecord(user, enterpriseId, "PREVENTION_TRAINING",
            firstNonBlank(request == null ? null : request.getTitle(), "月度培训计划"),
            "培训计划草稿", request, request == null ? null : request.getPlanIds(), "draft");
        return successResult("培训计划草稿已保存。", record.getRecordId());
    }

    @Override
    public Map<String, Object> submitTrainingAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbModuleRecord record = saveActionRecord(user, enterpriseId, "TRAINING_ACTION",
            firstNonBlank(request == null ? null : request.getActionType(), "training-action"),
            "企业培训处理", request, request == null ? null : request.getPlanIds(), "processing");
        return successResult("培训处理请求已提交。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getJobPublishDraft(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        List<WorkerJobPost> jobs = selectEnterpriseJobs(enterpriseId);
        WorkerJobPost latest = jobs.isEmpty() ? null : jobs.get(0);
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("title", latest == null ? "" : safeText(latest.getTitle(), ""));
        form.put("location", latest == null ? "" : safeText(latest.getWorkAddress(), ""));
        form.put("salary", latest == null ? "" : safeText(latest.getSalaryText(), ""));
        form.put("requirement", latest == null ? "" : safeText(latest.getRequirementText(), ""));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("form", form);
        return result;
    }

    @Override
    public Map<String, Object> saveJobPublishDraft(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbEnterprise enterprise = enterpriseId == null ? null : enterpriseService.selectEnterpriseById(enterpriseId);
        WorkerJobPost job = buildJobPost(enterpriseId, enterprise, request, "1");
        workerJobPostService.insertJobPost(job);
        return successResult("岗位草稿已保存：" + safeText(job.getTitle(), "未命名岗位"), job.getJobId());
    }

    @Override
    public Map<String, Object> submitJobPublish(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request)
    {
        Long enterpriseId = resolveEnterpriseId(user, worker);
        YgbEnterprise enterprise = enterpriseId == null ? null : enterpriseService.selectEnterpriseById(enterpriseId);
        WorkerJobPost job = buildJobPost(enterpriseId, enterprise, request, "1");
        workerJobPostService.insertJobPost(job);
        return successResult("岗位发布已提交审核：" + safeText(job.getTitle(), "未命名岗位"), job.getJobId());
    }

    private WorkerJobPost buildJobPost(Long enterpriseId, YgbEnterprise enterprise, AppEnterpriseActionRequest request, String status)
    {
        WorkerJobPost job = new WorkerJobPost();
        job.setEnterpriseId(enterpriseId);
        job.setEnterpriseName(enterprise == null ? "" : enterprise.getEnterpriseName());
        job.setTitle(firstNonBlank(request == null ? null : request.getTitle(), "未命名岗位"));
        job.setJobType(resolveJobType(request == null ? null : request.getRequirement()));
        job.setWorkAddress(firstNonBlank(request == null ? null : request.getLocation(), enterprise == null ? null : enterprise.getAddress(), ""));
        job.setSalaryText(firstNonBlank(request == null ? null : request.getSalary(), "面议"));
        job.setSalaryMin(parseSalaryBound(job.getSalaryText(), true));
        job.setSalaryMax(parseSalaryBound(job.getSalaryText(), false));
        job.setRecruitCount(3);
        job.setDescription(firstNonBlank(request == null ? null : request.getRequirement(), "企业移动端提交岗位。"));
        job.setRequirementText(firstNonBlank(request == null ? null : request.getRequirement(), "持证上岗，服从现场管理。"));
        job.setStatus(status);
        job.setPublishTime(new Date());
        job.setCreateBy("app-enterprise");
        return job;
    }

    private List<Map<String, Object>> buildHomeWarnings(YgbPersonSummary personSummary, YgbDeviceSummary deviceSummary,
        YgbSalaryBatchSummary salarySummary, List<YgbModuleRecord> pendingActions)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (safeInt(personSummary == null ? 0 : personSummary.getCertRiskCount()) > 0)
        {
            rows.add(warningRow(1L, "高危岗位证件临期",
                safeInt(personSummary.getCertRiskCount()) + " 名人员证件需要复核。", "高", "worker-tag--danger"));
        }
        if (safeInt(deviceSummary == null ? 0 : deviceSummary.getLockedOrFaultCount()) > 0
            || safeInt(deviceSummary == null ? 0 : deviceSummary.getAuthDeniedCount()) > 0)
        {
            rows.add(warningRow(2L, "设备状态异常",
                "锁定/故障 " + safeInt(deviceSummary == null ? 0 : deviceSummary.getLockedOrFaultCount())
                    + " 台，授权拒绝 " + safeInt(deviceSummary == null ? 0 : deviceSummary.getAuthDeniedCount()) + " 台。",
                "中", "worker-tag--warning"));
        }
        if (safeInt(salarySummary == null ? 0 : salarySummary.getPendingSubmitCount()) > 0)
        {
            rows.add(warningRow(3L, "工资批次待提交",
                "当前仍有 " + safeInt(salarySummary.getPendingSubmitCount()) + " 批待代发。", "中", "worker-tag--info"));
        }
        if (rows.isEmpty() && !pendingActions.isEmpty())
        {
            rows.add(warningRow(4L, "企业待办提醒", "当前有 " + pendingActions.size() + " 条企业办理待跟进。", "低", "worker-tag--info"));
        }
        return rows;
    }

    private Map<String, Object> successResult(String message, Object recordId)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", message);
        if (recordId != null)
        {
            result.put("recordId", recordId);
        }
        return result;
    }

    private YgbPerson buildPersonQuery(Long enterpriseId)
    {
        YgbPerson query = new YgbPerson();
        query.setEnterpriseId(enterpriseId);
        return query;
    }

    private YgbDevice buildDeviceQuery(Long enterpriseId)
    {
        YgbDevice query = new YgbDevice();
        query.setEnterpriseId(enterpriseId);
        return query;
    }

    private YgbSalaryBatch buildSalaryQuery(Long enterpriseId)
    {
        YgbSalaryBatch query = new YgbSalaryBatch();
        query.setDispatchEnterpriseId(enterpriseId);
        return query;
    }

    private List<WorkerJobPost> selectEnterpriseJobs(Long enterpriseId)
    {
        WorkerJobPost query = new WorkerJobPost();
        query.setEnterpriseId(enterpriseId);
        return workerJobMapper.selectAdminJobPostList(query);
    }

    private Map<String, Object> personRow(YgbPerson item)
    {
        String certText = certStatusText(item.getCertStatus());
        String insuranceText = insuranceStatusText(item.getInsuranceStatus());
        String statusText = employmentStatusText(item.getEmploymentStatus());
        String tag = "正常";
        String className = "worker-tag--success";
        if ("2".equals(item.getCertStatus()))
        {
            tag = "临期";
            className = "worker-tag--warning";
        }
        else if ("0".equals(item.getInsuranceStatus()) || "3".equals(item.getCertStatus()))
        {
            tag = "待处理";
            className = "worker-tag--danger";
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getPersonId());
        row.put("name", safeText(item.getPersonName(), "未命名人员"));
        row.put("job", safeText(item.getJobType(), "待同步"));
        row.put("status", statusText);
        row.put("certificate", certText);
        row.put("insurance", insuranceText);
        row.put("tag", tag);
        row.put("className", className);
        return row;
    }

    private Map<String, Object> deviceRow(YgbDevice item)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getDeviceId());
        row.put("name", safeText(item.getDeviceName(), safeText(item.getDeviceCode(), "未命名设备")));
        row.put("code", safeText(item.getDeviceCode(), "待同步"));
        row.put("location", safeText(item.getInstallLocation(), "待同步"));
        row.put("status", deviceStatusText(item.getDeviceStatus()));
        row.put("className", deviceStatusClass(item.getDeviceStatus()));
        return row;
    }

    private Map<String, Object> salaryBatchRow(YgbSalaryBatch item)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getBatchId());
        row.put("month", safeText(item.getStatMonth(), currentMonth()));
        row.put("title", safeText(item.getBatchNo(), "工资批次"));
        row.put("people", safeInt(item.getTotalPersonCount()));
        row.put("amount", formatCurrency(item.getTotalPayableAmount()));
        row.put("status", salaryBatchStatusText(item.getBatchStatus()));
        row.put("className", salaryBatchClass(item.getBatchStatus()));
        return row;
    }

    private Map<String, Object> operationRow(YgbModuleRecord item)
    {
        Map<String, Object> payload = parsePayload(item.getPayloadJson());
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getRecordId());
        row.put("title", safeText(item.getRecordName(), "待审批作业"));
        row.put("location", safeText(stringValue(payload.get("location")), safeText(item.getEnterpriseName(), "待同步")));
        row.put("time", safeText(item.getStatMonth(), currentMonth()));
        row.put("guardian", safeText(item.getPersonName(), "企业管理员"));
        row.put("status", workflowStatusText(item.getWorkflowStatus()));
        return row;
    }

    private Map<String, Object> trainingPlanRow(YgbModuleRecord item)
    {
        Map<String, Object> payload = parsePayload(item.getPayloadJson());
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getRecordId());
        row.put("title", safeText(item.getRecordName(), "培训计划"));
        row.put("desc", safeText(stringValue(payload.get("summary")), "企业培训计划待同步。"));
        row.put("progress", trainingProgressText(item.getWorkflowStatus()));
        return row;
    }

    private Map<String, Object> quickEntry(String key, String label, String glyph, String tone, String path)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("label", label);
        row.put("glyph", glyph);
        row.put("icon", glyph);
        row.put("tone", tone);
        row.put("color", tone);
        row.put("path", path);
        return row;
    }

    private Map<String, Object> dataCard(String label, String value, String desc)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("label", label);
        row.put("value", value);
        row.put("desc", desc);
        row.put("note", desc);
        return row;
    }

    private Map<String, Object> filter(String key, String label)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("key", key);
        row.put("label", label);
        return row;
    }

    private Map<String, Object> section(String title, String tag, List<Map<String, Object>> items)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("title", title);
        row.put("tag", tag);
        row.put("items", items);
        row.put("entries", items);
        return row;
    }

    private Map<String, Object> summaryItem(String label, String value)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("label", label);
        row.put("value", value);
        return row;
    }

    private Map<String, Object> actionRow(Long id, String title, String desc, String type, String className)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", id);
        row.put("title", title);
        row.put("desc", desc);
        row.put("type", type);
        row.put("className", className);
        return row;
    }

    private Map<String, Object> warningRow(Long id, String title, String desc, String level, String levelClass)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", id);
        row.put("title", title);
        row.put("desc", desc);
        row.put("level", level);
        row.put("levelClass", levelClass);
        return row;
    }

    private YgbModuleRecord saveActionRecord(SysUser user, Long enterpriseId, String categoryCode, String recordName,
        String sourceLabel, Object payload, Long[] relatedIds, String workflowStatus)
    {
        YgbModuleRecord record = new YgbModuleRecord();
        record.setRecordType(RECORD_TYPE_ENTERPRISE_ACTION);
        record.setRecordName(safeText(recordName, sourceLabel));
        record.setCategoryCode(categoryCode);
        record.setStatMonth(currentMonth());
        record.setPortalCode("ygb");
        record.setWorkflowStatus(firstNonBlank(workflowStatus, "draft"));
        record.setStatus(STATUS_NORMAL);
        record.setEnterpriseId(enterpriseId);
        record.setPersonName(user.getNickName());
        record.setRelatedCode(joinIds(relatedIds));
        record.setSourceLabel(sourceLabel);
        record.setPayloadJson(JSON.toJSONString(payload == null ? Map.of() : payload));
        moduleRecordService.insertModuleRecord(record, user.getUserName());
        return record;
    }

    private YgbModuleRecord saveExportRecord(SysUser user, Long enterpriseId, String categoryCode, String sourceLabel, Object payload)
    {
        YgbModuleRecord record = new YgbModuleRecord();
        record.setRecordType(RECORD_TYPE_ENTERPRISE_EXPORT);
        record.setRecordName(sourceLabel);
        record.setCategoryCode(categoryCode);
        record.setStatMonth(currentMonth());
        record.setPortalCode("ygb");
        record.setWorkflowStatus("closed");
        record.setStatus(STATUS_NORMAL);
        record.setEnterpriseId(enterpriseId);
        record.setPersonName(user.getNickName());
        record.setSourceLabel(sourceLabel);
        record.setPayloadJson(JSON.toJSONString(payload == null ? Map.of() : payload));
        moduleRecordService.insertModuleRecord(record, user.getUserName());
        return record;
    }

    private List<YgbModuleRecord> selectActionRecords(Long enterpriseId, String workflowStatus)
    {
        YgbModuleRecord query = new YgbModuleRecord();
        query.setRecordType(RECORD_TYPE_ENTERPRISE_ACTION);
        query.setEnterpriseId(enterpriseId);
        query.setWorkflowStatus(workflowStatus);
        return moduleRecordService.selectModuleRecordList(query);
    }

    private List<YgbModuleRecord> selectRecordsByType(Long enterpriseId, String categoryCode)
    {
        YgbModuleRecord query = new YgbModuleRecord();
        query.setEnterpriseId(enterpriseId);
        query.setCategoryCode(categoryCode);
        return moduleRecordService.selectModuleRecordList(query);
    }

    private Long resolveEnterpriseId(SysUser user, YgbPerson worker)
    {
        if (user != null && user.getEnterpriseId() != null)
        {
            return user.getEnterpriseId();
        }
        if (worker != null && worker.getEnterpriseId() != null)
        {
            return worker.getEnterpriseId();
        }
        SysUser resolved = resolveUserByWorker(worker);
        if (resolved != null && resolved.getEnterpriseId() != null)
        {
            return resolved.getEnterpriseId();
        }
        throw new ServiceException("当前账号未绑定企业权限。");
    }

    private SysUser resolveUserByWorker(YgbPerson worker)
    {
        if (worker == null)
        {
            return null;
        }
        if (StringUtils.isNotEmpty(worker.getMobile()))
        {
            SysUser user = sysUserService.selectUserByPhonenumber(worker.getMobile());
            if (user != null)
            {
                return user;
            }
        }
        if (StringUtils.isNotEmpty(worker.getPersonName()))
        {
            SysUser query = new SysUser();
            query.setNickName(worker.getPersonName());
            List<SysUser> users = sysUserService.selectUserList(query);
            if (users != null && !users.isEmpty())
            {
                return users.get(0);
            }
        }
        return null;
    }

    private String buildInsuranceRate(YgbPersonSummary summary)
    {
        int total = safeInt(summary == null ? 0 : summary.getTotalCount());
        int uninsured = safeInt(summary == null ? 0 : summary.getUninsuredCount());
        int insured = Math.max(total - uninsured, 0);
        return buildRate(insured, total);
    }

    private String buildTrainingCompletionRate(List<YgbModuleRecord> plans)
    {
        if (plans == null || plans.isEmpty())
        {
            return "0%";
        }
        int completed = 0;
        for (YgbModuleRecord item : plans)
        {
            if ("closed".equals(item.getWorkflowStatus()))
            {
                completed++;
            }
        }
        return buildRate(completed, plans.size());
    }

    private String buildRate(int numerator, int denominator)
    {
        if (denominator <= 0)
        {
            return "0%";
        }
        BigDecimal rate = BigDecimal.valueOf(numerator).multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(denominator), 0, RoundingMode.HALF_UP);
        return rate + "%";
    }

    private int countActionByCategory(List<YgbModuleRecord> rows, String categoryCode)
    {
        int count = 0;
        for (YgbModuleRecord item : rows)
        {
            if (item != null && categoryCode.equalsIgnoreCase(safeText(item.getCategoryCode(), "")))
            {
                count++;
            }
        }
        return count;
    }

    private String deviceStatusText(String value)
    {
        return switch (safeText(value, "")) {
            case "1" -> "在线";
            case "2" -> "锁定";
            case "3" -> "故障";
            default -> "离线";
        };
    }

    private String deviceStatusClass(String value)
    {
        return switch (safeText(value, "")) {
            case "1" -> "worker-tag--success";
            case "2" -> "worker-tag--warning";
            case "3" -> "worker-tag--danger";
            default -> "worker-tag--danger";
        };
    }

    private String employmentStatusText(String value)
    {
        return "0".equals(value) ? "在职" : "待入场";
    }

    private String certStatusText(String value)
    {
        return switch (safeText(value, "")) {
            case "1" -> "高危证件有效";
            case "2" -> "7 天内到期";
            case "3" -> "证件已过期";
            default -> "待补齐";
        };
    }

    private String insuranceStatusText(String value)
    {
        return "1".equals(value) ? "已参保" : "待参保";
    }

    private String salaryBatchStatusText(String value)
    {
        return switch (safeText(value, "")) {
            case "1" -> "待确认";
            case "3" -> "待生成";
            case "4" -> "待提交";
            case "5" -> "代发中";
            case "6" -> "已发放";
            case "7" -> "发放失败";
            default -> "草稿";
        };
    }

    private String salaryBatchClass(String value)
    {
        return switch (safeText(value, "")) {
            case "6" -> "worker-tag--success";
            case "5" -> "worker-tag--info";
            case "7" -> "worker-tag--danger";
            default -> "worker-tag--warning";
        };
    }

    private String insurancePolicyStatusText(String value)
    {
        return switch (safeText(value, "")) {
            case "1" -> "有效";
            case "2" -> "即将到期";
            case "3" -> "已过期";
            default -> "未生效";
        };
    }

    private String workflowStatusText(String value)
    {
        return switch (safeText(value, "")) {
            case "closed" -> "已办结";
            case "processing" -> "处理中";
            case "approved" -> "已通过";
            default -> "待审批";
        };
    }

    private String trainingProgressText(String workflowStatus)
    {
        return switch (safeText(workflowStatus, "")) {
            case "closed" -> "100%";
            case "processing" -> "60%";
            case "pending" -> "20%";
            default -> "40%";
        };
    }

    private String resolveJobType(String requirement)
    {
        if (StringUtils.isEmpty(requirement))
        {
            return "综合岗位";
        }
        if (requirement.contains("高空"))
        {
            return "高空作业";
        }
        if (requirement.contains("焊"))
        {
            return "焊工";
        }
        return "综合岗位";
    }

    private BigDecimal parseSalaryBound(String salaryText, boolean min)
    {
        if (StringUtils.isEmpty(salaryText))
        {
            return null;
        }
        String sanitized = salaryText.replaceAll("[^0-9\\-]", "");
        if (StringUtils.isEmpty(sanitized))
        {
            return null;
        }
        String[] parts = sanitized.split("-");
        try
        {
            if (parts.length == 1)
            {
                return new BigDecimal(parts[0]);
            }
            return new BigDecimal(min ? parts[0] : parts[parts.length - 1]);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    private Map<String, Object> parsePayload(String payloadJson)
    {
        if (StringUtils.isEmpty(payloadJson))
        {
            return Map.of();
        }
        try
        {
            Map<String, Object> parsed = JSON.parseObject(payloadJson);
            return parsed == null ? Map.of() : parsed;
        }
        catch (Exception ex)
        {
            return Map.of();
        }
    }

    private String stringValue(Object value)
    {
        return value == null ? "" : String.valueOf(value);
    }

    private String humanAction(String actionType, String fallback)
    {
        return switch (safeText(actionType, "")) {
            case "certificate-remind" -> "证件提醒";
            case "insurance-check" -> "参保核验";
            case "authorize" -> "设备授权";
            case "repair" -> "故障报修";
            case "renew" -> "续保处理";
            case "remind" -> "培训督办";
            case "publish-exam" -> "考试补发";
            default -> fallback;
        };
    }

    private String currentMonth()
    {
        return MONTH_FORMATTER.format(LocalDate.now());
    }

    private String joinIds(Long[] ids)
    {
        if (ids == null || ids.length == 0)
        {
            return "";
        }
        return Arrays.stream(ids).map(String::valueOf).reduce((left, right) -> left + "," + right).orElse("");
    }

    private int countIds(Long[] ids)
    {
        return ids == null ? 0 : ids.length;
    }

    private int safeInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private int safeInt(int value)
    {
        return value;
    }

    private String formatScore(BigDecimal score, String fallback)
    {
        return score == null ? fallback : score.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatCurrency(BigDecimal amount)
    {
        BigDecimal value = amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
        return "¥" + value.toPlainString();
    }

    private String limitText(String text, int maxLength)
    {
        String safe = safeText(text, "");
        if (safe.length() <= maxLength)
        {
            return safe;
        }
        return safe.substring(0, maxLength);
    }

    private String safeText(String value, String fallback)
    {
        return StringUtils.isEmpty(value) ? fallback : value;
    }

    private String firstNonBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value) && !value.isBlank())
            {
                return value.trim();
            }
        }
        return "";
    }
}
