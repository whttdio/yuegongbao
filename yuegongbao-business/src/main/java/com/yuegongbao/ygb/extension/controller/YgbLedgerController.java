package com.yuegongbao.ygb.extension.controller;

import java.util.List;
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
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;

@RestController
@RequestMapping("/ygb/ledger")
public class YgbLedgerController extends BaseController
{
    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasAnyPermi('ygb:' + #submodule + ':list,ygb:' + #submodule + ':query')")
    @GetMapping("/{submodule}/list")
    public TableDataInfo list(@PathVariable String submodule, YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, resolveRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:' + #submodule + ':list,ygb:' + #submodule + ':query')")
    @GetMapping("/{submodule}/summary")
    public AjaxResult summary(@PathVariable String submodule, YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, resolveRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasPermi('ygb:' + #submodule + ':query')")
    @GetMapping("/{submodule}/{recordId}")
    public AjaxResult detail(@PathVariable String submodule, @PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        String recordType = resolveRecordType(submodule);
        if (record == null || !recordType.equals(record.getRecordType()))
        {
            throw new ServiceException("Record type does not match current ledger submodule");
        }
        return success(record);
    }

    @Log(title = "YGB Ledger", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:' + #submodule + ':export')")
    @PostMapping("/{submodule}/export")
    public void export(HttpServletResponse response, @PathVariable String submodule, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response,
            moduleRecordService.selectModuleRecordList(typedQuery(query, resolveRecordType(submodule))),
            "ledger_" + submodule);
    }

    @Log(title = "YGB Ledger", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:' + #submodule + ':add')")
    @PostMapping("/{submodule}")
    public AjaxResult add(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedRecord(record, resolveRecordType(submodule));
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "YGB Ledger", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:' + #submodule + ':edit')")
    @PutMapping("/{submodule}")
    public AjaxResult edit(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedRecord(record, resolveRecordType(submodule));
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "YGB Ledger", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:' + #submodule + ':remove')")
    @DeleteMapping("/{submodule}/{recordIds}")
    public AjaxResult remove(@PathVariable String submodule, @PathVariable Long[] recordIds)
    {
        resolveRecordType(submodule);
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

    private String resolveRecordType(String submodule)
    {
        return switch (submodule)
        {
            case "socialSupplement" -> "SOCIAL_SUPPLEMENT";
            case "socialEnrollment" -> "SOCIAL_ENROLLMENT";
            case "taxInvoice" -> "TAX_INVOICE";
            case "taxFundFlow" -> "TAX_FUND_FLOW";
            case "taxRecovery" -> "TAX_RECOVERY";
            case "threeNaturePost" -> "THREE_NATURE_POST";
            case "specialRectification" -> "SPECIAL_RECTIFICATION";
            case "expansionSubsidy" -> "EXPANSION_SUBSIDY";
            case "expansionEvaluation" -> "EXPANSION_EVALUATION";
            case "creditRule" -> "CREDIT_RULE";
            case "creditRepair" -> "CREDIT_REPAIR";
            case "creditSanction" -> "CREDIT_SANCTION";
            case "injuryPersonMonitor" -> "INJURY_PERSON_MONITOR";
            case "injuryEmployerMonitor" -> "INJURY_EMPLOYER_MONITOR";
            case "injuryRegionMonitor" -> "INJURY_REGION_MONITOR";
            case "injuryOccHazardMonitor" -> "INJURY_OCC_HAZARD_MONITOR";
            case "injuryNewformHazardMonitor" -> "INJURY_NEWFORM_HAZARD_MONITOR";
            case "injuryAccidentWarning" -> "INJURY_ACCIDENT_WARNING";
            case "deviceInstallOrder" -> "DEVICE_INSTALL_ORDER";
            case "deviceRepairOrder" -> "DEVICE_REPAIR_ORDER";
            case "deviceInspectPlan" -> "DEVICE_INSPECT_PLAN";
            case "operationChipDispatch" -> "OPERATION_CHIP_DISPATCH";
            case "operationMaintenanceStats" -> "OPERATION_MAINTENANCE_STATS";
            case "preventionPublicity" -> "PREVENTION_PUBLICITY";
            case "preventionTraining" -> "PREVENTION_TRAINING";
            case "preventionAi" -> "PREVENTION_AI";
            case "operationJobCategory" -> "OPERATION_JOB_CATEGORY";
            case "operationRecruitStats" -> "OPERATION_RECRUIT_STATS";
            case "unionOrg" -> "UNION_ORG";
            case "unionSupervision" -> "UNION_SUPERVISION";
            case "unionLegalAid" -> "UNION_LEGAL_AID";
            case "unionNegotiation" -> "UNION_NEGOTIATION";
            case "unionPreventionSupervision" -> "UNION_PREVENTION_SUPERVISION";
            default -> throw new ServiceException("Unsupported ledger submodule: " + submodule);
        };
    }
}
