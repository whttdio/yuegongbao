package com.yuegongbao.ygb.techdefense.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.common.utils.uuid.IdUtils;
import com.yuegongbao.ygb.domain.vo.YgbCertCheckResult;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.integration.EmergencyCertClient;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportWorker;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.techdefense.domain.vo.YgbHeightWorkFinishRequest;
import com.yuegongbao.ygb.techdefense.mapper.YgbHeightWorkReportMapper;
import com.yuegongbao.ygb.techdefense.mapper.YgbHeightWorkReportWorkerMapper;
import com.yuegongbao.ygb.techdefense.service.IYgbHeightWorkReportService;

@Service
public class YgbHeightWorkReportServiceImpl implements IYgbHeightWorkReportService
{
    private static final int IMPORT_LIMIT = 100;

    @Autowired
    private YgbHeightWorkReportMapper reportMapper;

    @Autowired
    private YgbHeightWorkReportWorkerMapper workerMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private EmergencyCertClient emergencyCertClient;

    @Override
    public List<YgbHeightWorkReport> selectHeightWorkReportList(YgbHeightWorkReport query)
    {
        List<YgbHeightWorkReport> list = reportMapper.selectHeightWorkReportList(query);
        list.forEach(this::fillComputedFields);
        return list;
    }

    @Override
    public YgbHeightWorkReportSummary selectHeightWorkReportSummary(YgbHeightWorkReport query)
    {
        List<YgbHeightWorkReport> list = selectHeightWorkReportList(query);
        YgbHeightWorkReportSummary summary = new YgbHeightWorkReportSummary();
        summary.setTotalCount(list.size());

        int activeCount = 0;
        int finishedCount = 0;
        int invalidWorkerCount = 0;
        int importedCount = 0;
        int allInvalidCount = 0;
        int partialInvalidCount = 0;
        for (YgbHeightWorkReport item : list)
        {
            if ("0".equals(item.getReportStatus()))
            {
                activeCount++;
            }
            if ("1".equals(item.getReportStatus()))
            {
                finishedCount++;
            }
            invalidWorkerCount += defaultInt(item.getCertInvalidCount());
            if ("2".equals(item.getCertValidStatus()))
            {
                allInvalidCount++;
            }
            if ("1".equals(item.getCertValidStatus()))
            {
                partialInvalidCount++;
            }
            String sourceMode = StringUtils.upperCase(item.getSourceMode());
            if ("3".equals(item.getReporterType()) || (StringUtils.isNotEmpty(sourceMode) && !"PC".equals(sourceMode)))
            {
                importedCount++;
            }
        }

        summary.setActiveCount(activeCount);
        summary.setFinishedCount(finishedCount);
        summary.setInvalidWorkerCount(invalidWorkerCount);
        summary.setImportedCount(importedCount);
        summary.setAllInvalidCount(allInvalidCount);
        summary.setPartialInvalidCount(partialInvalidCount);
        summary.setYgbExplanation(buildYgbExplanation(query, summary));
        summary.setAzbExplanation(buildAzbExplanation(query, summary));
        return summary;
    }

    @Override
    public YgbHeightWorkReport selectHeightWorkReportById(Long reportId)
    {
        YgbHeightWorkReport report = reportMapper.selectHeightWorkReportById(reportId);
        if (report == null)
        {
            return null;
        }
        fillComputedFields(report);
        report.setWorkerList(workerMapper.selectWorkersByReportId(reportId));
        return report;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHeightWorkReport(YgbHeightWorkReport report, String operator)
    {
        normalizeReport(report, false, false, operator);
        report.setReportNo(generateReportNo());
        report.setVoucherToken(generateVoucherToken());
        report.setReportStatus("0");
        report.setCreateBy(operator);
        int rows = reportMapper.insertHeightWorkReport(report);
        saveWorkers(report);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHeightWorkReport(YgbHeightWorkReport report, String operator)
    {
        YgbHeightWorkReport origin = requireReport(report.getReportId());
        ensureEditable(origin);
        normalizeReport(report, true, false, operator);
        report.setUpdateBy(operator);
        int rows = reportMapper.updateHeightWorkReport(report);
        workerMapper.deleteWorkersByReportId(report.getReportId());
        saveWorkers(report);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int finishHeightWorkReport(Long reportId, YgbHeightWorkFinishRequest request, String operator)
    {
        YgbHeightWorkReport origin = requireReport(reportId);
        ensureEditable(origin);
        if (request.getActualEndTime().before(origin.getStartTime()))
        {
            throw new ServiceException("实际结束时间不能早于计划开始时间");
        }
        YgbHeightWorkReport report = new YgbHeightWorkReport();
        report.setReportId(reportId);
        report.setActualEndTime(request.getActualEndTime());
        report.setEndPhotoUrl(request.getEndPhotoUrl());
        report.setReportStatus("1");
        report.setUpdateBy(operator);
        return reportMapper.finishHeightWorkReport(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importHeightWorkReports(List<YgbHeightWorkReport> reports, String operator)
    {
        if (reports == null || reports.isEmpty())
        {
            throw new ServiceException("导入报备记录不能为空");
        }
        if (reports.size() > IMPORT_LIMIT)
        {
            throw new ServiceException("单次导入最多100条报备记录");
        }

        int rows = 0;
        for (YgbHeightWorkReport report : reports)
        {
            report.setReporterType(StringUtils.defaultIfEmpty(report.getReporterType(), "3"));
            report.setSourceMode("IMPORT");
            report.setSourcePlatform(StringUtils.defaultIfEmpty(report.getSourcePlatform(), "THIRD_PARTY"));
            normalizeReport(report, false, true, operator);
            report.setReportNo(StringUtils.defaultIfEmpty(report.getReportNo(), generateReportNo()));
            report.setVoucherToken(StringUtils.defaultIfEmpty(report.getVoucherToken(), generateVoucherToken()));
            report.setReportStatus(StringUtils.defaultIfEmpty(report.getReportStatus(), "0"));
            report.setCreateBy(operator);
            reportMapper.insertHeightWorkReport(report);
            saveWorkers(report);
            rows++;
        }
        return rows;
    }

    @Override
    public Map<String, Object> buildVoucher(Long reportId)
    {
        YgbHeightWorkReport report = selectHeightWorkReportById(reportId);
        if (report == null)
        {
            throw new ServiceException("报备记录不存在");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reportId", report.getReportId());
        result.put("reportNo", report.getReportNo());
        result.put("voucherToken", report.getVoucherToken());
        result.put("enterpriseName", report.getEnterpriseName());
        result.put("workLocation", report.getWorkLocation());
        result.put("applicantName", report.getApplicantName());
        result.put("guardianName", report.getGuardianName());
        result.put("startTime", report.getStartTime());
        result.put("endTime", report.getEndTime());
        result.put("reportStatus", report.getReportStatus());
        result.put("qrText", "YGB-HWR|" + report.getReportNo() + "|" + report.getVoucherToken());
        result.put("voucherUrl", "/ygb/heightWork/report/voucher/" + report.getReportId());
        result.put("generatedTime", new Date());
        return result;
    }

    private void saveWorkers(YgbHeightWorkReport report)
    {
        workerMapper.insertHeightWorkReportWorkers(report.getReportId(), report.getWorkerList());
    }

    private void normalizeReport(YgbHeightWorkReport report, boolean update, boolean imported, String operator)
    {
        if (report == null)
        {
            throw new ServiceException("报备信息不能为空");
        }
        if (update && report.getReportId() == null)
        {
            throw new ServiceException("报备ID不能为空");
        }
        if (report.getStartTime() == null || report.getEndTime() == null)
        {
            throw new ServiceException("计划开始时间和结束时间不能为空");
        }
        if (!report.getEndTime().after(report.getStartTime()))
        {
            throw new ServiceException("计划结束时间必须晚于计划开始时间");
        }
        if (report.getWorkerList() == null || report.getWorkerList().isEmpty())
        {
            throw new ServiceException("作业人员清单不能为空");
        }
        if (report.getWorkHeightM() == null || report.getWorkHeightM() < 2)
        {
            throw new ServiceException("高处作业高度不能低于2米");
        }

        fillEnterpriseSnapshot(report);
        report.setWorkerCount(report.getWorkerList().size());
        report.setSafetyMeasuresJson(JSON.toJSONString(report.getSafetyMeasures() == null ? List.of() : report.getSafetyMeasures()));
        applyCertChecks(report, operator);

        if (!imported)
        {
            report.setSourceMode(StringUtils.defaultIfEmpty(report.getSourceMode(), "PC"));
            report.setSourcePlatform(StringUtils.defaultIfEmpty(report.getSourcePlatform(), "YGB_PC"));
            report.setSourceSerialNo(StringUtils.defaultIfEmpty(report.getSourceSerialNo(), ""));
        }
    }

    private void fillEnterpriseSnapshot(YgbHeightWorkReport report)
    {
        if (report.getEnterpriseId() != null && report.getEnterpriseId() > 0)
        {
            YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(report.getEnterpriseId());
            if (enterprise == null)
            {
                throw new ServiceException("企业不存在");
            }
            report.setEnterpriseName(enterprise.getEnterpriseName());
            report.setRegionCode(enterprise.getRegionCode());
            if ("2".equals(report.getReporterType()) && StringUtils.isEmpty(report.getApplicantName()))
            {
                report.setApplicantName(enterprise.getEnterpriseName());
            }
            return;
        }

        report.setEnterpriseId(0L);
        report.setRegionCode(StringUtils.defaultString(report.getRegionCode()));
    }

    private void applyCertChecks(YgbHeightWorkReport report, String operator)
    {
        int validCount = 0;
        int invalidCount = 0;
        List<YgbHeightWorkReportWorker> normalized = new ArrayList<>();
        int index = 1;
        for (YgbHeightWorkReportWorker worker : report.getWorkerList())
        {
            worker.setSortOrder(index++);
            YgbCertCheckResult certCheck = emergencyCertClient.checkValidity(worker.getIdCard(), worker.getCertNo());
            boolean valid = certCheck != null && certCheck.isValid();
            worker.setCertValidStatus(valid ? "0" : "1");
            worker.setCertValidMessage(certCheck == null ? "证书核验失败" : certCheck.getSourceMessage());
            normalized.add(worker);
            if (valid)
            {
                validCount++;
            }
            else
            {
                invalidCount++;
            }
        }

        report.setWorkerList(normalized);
        report.setCertValidCount(validCount);
        report.setCertInvalidCount(invalidCount);
        if (invalidCount == 0)
        {
            report.setCertValidStatus("0");
        }
        else if (validCount == 0)
        {
            report.setCertValidStatus("2");
        }
        else
        {
            report.setCertValidStatus("1");
        }
    }

    private void fillComputedFields(YgbHeightWorkReport report)
    {
        if (StringUtils.isNotEmpty(report.getSafetyMeasuresJson()))
        {
            report.setSafetyMeasures(JSON.parseArray(report.getSafetyMeasuresJson(), String.class));
        }
        else
        {
            report.setSafetyMeasures(List.of());
        }
    }

    private YgbHeightWorkReport requireReport(Long reportId)
    {
        YgbHeightWorkReport report = reportMapper.selectHeightWorkReportById(reportId);
        if (report == null)
        {
            throw new ServiceException("报备记录不存在");
        }
        return report;
    }

    private void ensureEditable(YgbHeightWorkReport report)
    {
        if ("1".equals(report.getReportStatus()))
        {
            throw new ServiceException("已结束的报备记录不可修改");
        }
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private String generateReportNo()
    {
        return "HWR" + System.currentTimeMillis() + IdUtils.fastSimpleUUID().substring(0, 6).toUpperCase();
    }

    private String generateVoucherToken()
    {
        return IdUtils.fastSimpleUUID();
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbHeightWorkReport query, YgbHeightWorkReportSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("active", "Active Reports", summary.getActiveCount(), 0,
            "Prioritize reports that still need finish registration before the business chain can close.",
            "heightWorkReport", "heightWorkReport", "530.1 Height Work Handling", baseQuery));
        list.add(explanationItem("invalid", "Invalid Certificates", summary.getInvalidWorkerCount(), 0,
            "Certificate exceptions should be resolved first to avoid blocking downstream authorization and closure.",
            "heightWorkReport", "heightWorkReport", "530.1 Height Work Handling", baseQuery));
        list.add(explanationItem("allInvalid", "All Invalid Reports", summary.getAllInvalidCount(), 0,
            "Escalate reports where every certificate is invalid before continuing work registration.",
            "heightWorkReport", "heightWorkReport", "530.1 Height Work Handling", baseQuery));
        list.add(explanationItem("imported", "Imported Sources", summary.getImportedCount(), 0,
            "Imported or third-party sourced reports should be verified first for source responsibility and evidence retention.",
            "heightWorkReport", "heightWorkReport", "530.1 Height Work Handling", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbHeightWorkReport query, YgbHeightWorkReportSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("active", "进行中高危作业", summary.getActiveCount(), 0,
            "6.1 口径优先锁定仍在途的高危作业对象，先压降现场作业风险。", "heightWorkReport",
            "heightWorkReport", "6.1 高处作业治理解释", baseQuery));
        list.add(explanationItem("invalid", "异常证书人次", summary.getInvalidWorkerCount(), 0,
            "证书异常说明现场准入链路存在明显风险，应优先联动复核。", "heightWorkReport",
            "heightWorkReport", "6.1 高处作业治理解释", baseQuery));
        list.add(explanationItem("allInvalid", "全部失效报备", summary.getAllInvalidCount(), 0,
            "全部失效报备更适合优先升级核查，避免高危对象持续暴露。", "heightWorkReport",
            "heightWorkReport", "6.1 高处作业治理解释", baseQuery));
        list.add(explanationItem("imported", "导入与第三方来源", summary.getImportedCount(), 0,
            "第三方来源对象需要优先核对来源平台和结束责任，保证治理链稳定。", "heightWorkReport",
            "heightWorkReport", "6.1 高处作业治理解释", baseQuery));
        list.add(explanationItem("partial", "部分失效报备", summary.getPartialInvalidCount(), 0,
            "部分失效报备说明现场仍有待补证人员，需要继续跟进处置。", "heightWorkReport",
            "heightWorkReport", "6.1 高处作业治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbHeightWorkReport query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (StringUtils.isNotEmpty(query.getReportStatus()))
        {
            map.put("reportStatus", query.getReportStatus());
        }
        if (StringUtils.isNotEmpty(query.getCertValidStatus()))
        {
            map.put("certValidStatus", query.getCertValidStatus());
        }
        return map;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> baseQuery)
    {
        Map<String, Object> defaultQuery = new LinkedHashMap<>(baseQuery);
        defaultQuery.put("focusKey", focusKey);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", focusKey);
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }
}
