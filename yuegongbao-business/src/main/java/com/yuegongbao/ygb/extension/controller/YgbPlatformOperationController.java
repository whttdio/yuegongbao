package com.yuegongbao.ygb.extension.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.framework.web.domain.Server;
import com.yuegongbao.system.domain.SysLogininfor;
import com.yuegongbao.system.domain.SysOperLog;
import com.yuegongbao.system.mapper.SysLogininforMapper;
import com.yuegongbao.system.mapper.SysOperLogMapper;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecordSummary;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.worker.domain.WorkerJobPost;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.mapper.WorkerJobMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.IWorkerJobPostService;

@RestController
@RequestMapping
public class YgbPlatformOperationController extends BaseController
{
    private static final String OP_ENTERPRISE_REVIEW = "OP_ENTERPRISE_REVIEW";
    private static final String OP_MESSAGE = "OP_MESSAGE";
    private static final String PLATFORM_DOCUMENT = "PLATFORM_DOCUMENT";
    private static final String PLATFORM_EXCHANGE = "PLATFORM_EXCHANGE";
    private static final String PLATFORM_SECURITY_AUDIT = "PLATFORM_SECURITY_AUDIT";
    private static final String PLATFORM_BACKUP = "PLATFORM_BACKUP";

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @Autowired
    private WorkerJobMapper workerJobMapper;

    @Autowired
    private IWorkerJobPostService workerJobPostService;

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Autowired
    private SysLogininforMapper sysLogininforMapper;

    @PreAuthorize("@ss.hasPermi('ygb:operation:overview')")
    @GetMapping("/ygb/operation/overview")
    public AjaxResult operationOverview()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("enterpriseReviewSummary", typedSummary(OP_ENTERPRISE_REVIEW));
        result.put("messageSummary", typedSummary(OP_MESSAGE));
        result.put("jobCount", workerJobMapper.selectAdminJobPostList(new WorkerJobPost()).size());
        result.put("resumeCount", workerProfileMapper.selectAdminWorkerResumeList(new WorkerResume()).size());
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:list,ygb:operationMessage:list')")
    @GetMapping("/ygb/operation/{submodule}/list")
    public TableDataInfo listOperationModule(@PathVariable String submodule, YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, resolveOperationRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:list,ygb:operationMessage:list')")
    @GetMapping("/ygb/operation/{submodule}/summary")
    public AjaxResult summaryOperationModule(@PathVariable String submodule, YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, resolveOperationRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:query,ygb:operationMessage:query')")
    @GetMapping("/ygb/operation/{submodule}/{recordId}")
    public AjaxResult getOperationModule(@PathVariable String submodule, @PathVariable Long recordId)
    {
        return success(typedDetail(recordId, resolveOperationRecordType(submodule)));
    }

    @Log(title = "Operation Module", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:export,ygb:operationMessage:export')")
    @PostMapping("/ygb/operation/{submodule}/export")
    public void exportOperationModule(HttpServletResponse response, @PathVariable String submodule, YgbModuleRecord query)
    {
        exportTyped(response, typedQuery(query, resolveOperationRecordType(submodule)), "operation_" + submodule);
    }

    @Log(title = "Operation Module", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:add,ygb:operationMessage:add')")
    @PostMapping("/ygb/operation/{submodule}")
    public AjaxResult addOperationModule(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord(record, resolveOperationRecordType(submodule)), getUsername()));
    }

    @Log(title = "Operation Module", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:edit,ygb:operationMessage:edit')")
    @PutMapping("/ygb/operation/{submodule}")
    public AjaxResult editOperationModule(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord(record, resolveOperationRecordType(submodule)), getUsername()));
    }

    @Log(title = "Operation Module", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:operationEnterpriseReview:remove,ygb:operationMessage:remove')")
    @DeleteMapping("/ygb/operation/{submodule}/{recordIds}")
    public AjaxResult removeOperationModule(@PathVariable String submodule, @PathVariable Long[] recordIds)
    {
        resolveOperationRecordType(submodule);
        return toAjax(moduleRecordService.deleteModuleRecordByIds(recordIds, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('ygb:operationRecruitStats:list')")
    @GetMapping("/ygb/operation/recruitStats/list")
    public TableDataInfo listRecruitStats(YgbModuleRecord query)
    {
        startPage();
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType("OPERATION_RECRUIT_STATS");
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery));
    }

    @PreAuthorize("@ss.hasPermi('ygb:operationRecruitStats:list')")
    @GetMapping("/ygb/operation/recruitStats/summary")
    public AjaxResult summaryRecruitStats(YgbModuleRecord query)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType("OPERATION_RECRUIT_STATS");
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery));
    }

    @Log(title = "Operation Recruit Stats", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:operationRecruitStats:export')")
    @PostMapping("/ygb/operation/recruitStats/export")
    public void exportRecruitStats(HttpServletResponse response, YgbModuleRecord query)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType("OPERATION_RECRUIT_STATS");
        exportTyped(response, typedQuery, "operation_recruit_stats");
    }

    @PreAuthorize("@ss.hasPermi('ygb:operationJobReview:list')")
    @GetMapping("/ygb/operation/jobReview/list")
    public TableDataInfo listJobReview(WorkerJobPost query)
    {
        startPage();
        return getDataTable(workerJobPostService.selectJobPostList(query));
    }

    @PreAuthorize("@ss.hasPermi('ygb:operationJobReview:query')")
    @GetMapping("/ygb/operation/jobReview/{jobId}")
    public AjaxResult getJobReview(@PathVariable Long jobId)
    {
        return success(workerJobPostService.selectJobPostById(jobId));
    }

    @Log(title = "Operation Job Review", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:operationJobReview:edit')")
    @PutMapping("/ygb/operation/jobReview/{jobId}/review")
    public AjaxResult reviewJobReview(@PathVariable Long jobId, @RequestBody Map<String, String> request)
    {
        String status = request == null ? null : request.get("status");
        String opinion = request == null ? null : request.get("opinion");
        return toAjax(workerJobPostService.reviewJobPost(jobId, status, opinion, getUsername()));
    }

    @Log(title = "Operation Job Review", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:operationJobReview:export')")
    @PostMapping("/ygb/operation/jobReview/export")
    public void exportJobReview(HttpServletResponse response, WorkerJobPost query)
    {
        ExcelUtil<WorkerJobPost> util = new ExcelUtil<>(WorkerJobPost.class);
        util.exportExcel(response, workerJobMapper.selectAdminJobPostList(query), "operation_job_review");
    }

    @PreAuthorize("@ss.hasPermi('ygb:operationResume:list')")
    @GetMapping("/ygb/operation/resume/list")
    public TableDataInfo listResume(WorkerResume query)
    {
        startPage();
        return getDataTable(workerProfileMapper.selectAdminWorkerResumeList(query));
    }

    @PreAuthorize("@ss.hasPermi('ygb:operationResume:query')")
    @GetMapping("/ygb/operation/resume/{resumeId}")
    public AjaxResult getResume(@PathVariable Long resumeId)
    {
        WorkerResume resume = workerProfileMapper.selectWorkerResumeById(resumeId);
        if (resume == null)
        {
            throw new ServiceException("Resume record not found");
        }
        return success(resume);
    }

    @Log(title = "Operation Resume", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:operationResume:export')")
    @PostMapping("/ygb/operation/resume/export")
    public void exportResume(HttpServletResponse response, WorkerResume query)
    {
        ExcelUtil<WorkerResume> util = new ExcelUtil<>(WorkerResume.class);
        util.exportExcel(response, workerProfileMapper.selectAdminWorkerResumeList(query), "operation_resume");
    }

    @Log(title = "Operation Resume", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:operationResume:edit')")
    @PutMapping("/ygb/operation/resume/{resumeId}/mark")
    public AjaxResult markResume(@PathVariable Long resumeId, @RequestBody Map<String, String> request)
    {
        WorkerResume resume = workerProfileMapper.selectWorkerResumeById(resumeId);
        if (resume == null)
        {
            throw new ServiceException("Resume record not found");
        }
        String mark = request == null ? null : request.get("mark");
        String opinion = request == null ? null : request.get("opinion");
        if (!"quality".equals(mark) && !"incomplete".equals(mark) && !"blocked".equals(mark))
        {
            throw new ServiceException("Unsupported resume mark");
        }
        resume.setRemark(buildResumeRemark(mark, opinion));
        resume.setUpdateBy(getUsername());
        return toAjax(workerProfileMapper.updateWorkerResumeRemark(resume));
    }

    @PreAuthorize("@ss.hasPermi('ygb:platformRuntime:query')")
    @GetMapping("/ygb/platform/runtime/summary")
    public AjaxResult runtimeSummary()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        try
        {
            Server server = new Server();
            server.copyTo();
            result.put("server", server);
        }
        catch (Exception ex)
        {
            result.put("serverError", ex.getMessage());
        }

        List<SysOperLog> operLogs = sysOperLogMapper.selectOperLogList(new SysOperLog());
        List<SysLogininfor> loginInfos = sysLogininforMapper.selectLogininforList(new SysLogininfor());
        int errorOperLogCount = 0;
        int loginFailCount = 0;
        for (SysOperLog operLog : operLogs)
        {
            if (operLog.getStatus() != null && operLog.getStatus().intValue() == 1)
            {
                errorOperLogCount++;
            }
        }
        for (SysLogininfor logininfor : loginInfos)
        {
            if ("1".equals(logininfor.getStatus()))
            {
                loginFailCount++;
            }
        }
        result.put("operLogCount", operLogs.size());
        result.put("errorOperLogCount", errorOperLogCount);
        result.put("loginInfoCount", loginInfos.size());
        result.put("loginFailCount", loginFailCount);
        result.put("documentSummary", typedSummary(PLATFORM_DOCUMENT));
        result.put("exchangeSummary", typedSummary(PLATFORM_EXCHANGE));
        result.put("securityAuditSummary", typedSummary(PLATFORM_SECURITY_AUDIT));
        result.put("backupSummary", typedSummary(PLATFORM_BACKUP));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:list,ygb:platformExchange:list,ygb:platformSecurityAudit:list,ygb:platformBackup:list')")
    @GetMapping("/ygb/platform/{submodule}/list")
    public TableDataInfo listPlatformModule(@PathVariable String submodule, YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, resolvePlatformRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:list,ygb:platformExchange:list,ygb:platformSecurityAudit:list,ygb:platformBackup:list')")
    @GetMapping("/ygb/platform/{submodule}/summary")
    public AjaxResult summaryPlatformModule(@PathVariable String submodule, YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, resolvePlatformRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:query,ygb:platformExchange:query,ygb:platformSecurityAudit:query,ygb:platformBackup:query')")
    @GetMapping("/ygb/platform/{submodule}/{recordId}")
    public AjaxResult getPlatformModule(@PathVariable String submodule, @PathVariable Long recordId)
    {
        return success(typedDetail(recordId, resolvePlatformRecordType(submodule)));
    }

    @Log(title = "Platform Module", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:export,ygb:platformExchange:export,ygb:platformSecurityAudit:export,ygb:platformBackup:export')")
    @PostMapping("/ygb/platform/{submodule}/export")
    public void exportPlatformModule(HttpServletResponse response, @PathVariable String submodule, YgbModuleRecord query)
    {
        exportTyped(response, typedQuery(query, resolvePlatformRecordType(submodule)), "platform_" + submodule);
    }

    @Log(title = "Platform Module", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:add,ygb:platformExchange:add,ygb:platformSecurityAudit:add,ygb:platformBackup:add')")
    @PostMapping("/ygb/platform/{submodule}")
    public AjaxResult addPlatformModule(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord(record, resolvePlatformRecordType(submodule)), getUsername()));
    }

    @Log(title = "Platform Module", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:edit,ygb:platformExchange:edit,ygb:platformSecurityAudit:edit,ygb:platformBackup:edit')")
    @PutMapping("/ygb/platform/{submodule}")
    public AjaxResult editPlatformModule(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord(record, resolvePlatformRecordType(submodule)), getUsername()));
    }

    @Log(title = "Platform Module", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:platformDocument:remove,ygb:platformExchange:remove,ygb:platformSecurityAudit:remove,ygb:platformBackup:remove')")
    @DeleteMapping("/ygb/platform/{submodule}/{recordIds}")
    public AjaxResult removePlatformModule(@PathVariable String submodule, @PathVariable Long[] recordIds)
    {
        resolvePlatformRecordType(submodule);
        return toAjax(moduleRecordService.deleteModuleRecordByIds(recordIds, getUsername()));
    }

    private YgbModuleRecord typedQuery(YgbModuleRecord query, String recordType)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType(recordType);
        return typedQuery;
    }

    private YgbModuleRecord typedRecord(YgbModuleRecord record, String recordType)
    {
        YgbModuleRecord typedRecord = record == null ? new YgbModuleRecord() : record;
        typedRecord.setRecordType(recordType);
        return typedRecord;
    }

    private YgbModuleRecord typedDetail(Long recordId, String recordType)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        if (!recordType.equals(record.getRecordType()))
        {
            throw new ServiceException("Record type does not match current module");
        }
        return record;
    }

    private void exportTyped(HttpServletResponse response, YgbModuleRecord query, String fileName)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, moduleRecordService.selectModuleRecordList(query), fileName);
    }

    private YgbModuleRecordSummary typedSummary(String recordType)
    {
        YgbModuleRecord query = new YgbModuleRecord();
        query.setRecordType(recordType);
        return moduleRecordService.selectModuleRecordSummary(query);
    }

    private String buildResumeRemark(String mark, String opinion)
    {
        String label = "quality".equals(mark) ? "优质简历" : ("incomplete".equals(mark) ? "待补充" : "暂不推荐");
        String text = opinion == null || opinion.isBlank() ? "运营标记：" + label : opinion.trim();
        return "运营标记：" + label + "；" + text;
    }

    private String resolveOperationRecordType(String submodule)
    {
        if ("enterpriseReview".equals(submodule))
        {
            return OP_ENTERPRISE_REVIEW;
        }
        if ("message".equals(submodule))
        {
            return OP_MESSAGE;
        }
        throw new ServiceException("Unsupported operation submodule: " + submodule);
    }

    private String resolvePlatformRecordType(String submodule)
    {
        if ("document".equals(submodule))
        {
            return PLATFORM_DOCUMENT;
        }
        if ("exchange".equals(submodule))
        {
            return PLATFORM_EXCHANGE;
        }
        if ("securityAudit".equals(submodule))
        {
            return PLATFORM_SECURITY_AUDIT;
        }
        if ("backup".equals(submodule))
        {
            return PLATFORM_BACKUP;
        }
        throw new ServiceException("Unsupported platform submodule: " + submodule);
    }
}
