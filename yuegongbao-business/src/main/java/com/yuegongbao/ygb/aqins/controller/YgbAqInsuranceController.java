package com.yuegongbao.ygb.aqins.controller;

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
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.service.IYgbAqInsuranceService;
import com.yuegongbao.ygb.domain.vo.YgbMonthScopeRequest;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;

@RestController
@RequestMapping("/ygb/aqInsurance")
public class YgbAqInsuranceController extends BaseController
{
    private static final String CLAIM_RECORD_TYPE = "AQ_CLAIM_MONITOR";

    @Autowired
    private IYgbAqInsuranceService aqInsuranceService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasPermi('ygb:aqInsurance:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAqInsurance aqInsurance)
    {
        startPage();
        List<YgbAqInsurance> list = aqInsuranceService.selectAqInsuranceList(aqInsurance);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:aqInsurance:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbAqInsurance aqInsurance)
    {
        return success(aqInsuranceService.selectAqInsuranceSummary(aqInsurance));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aqInsurance:query')")
    @GetMapping("/{policyId}")
    public AjaxResult getInfo(@PathVariable Long policyId)
    {
        return success(aqInsuranceService.selectAqInsuranceById(policyId));
    }

    @Log(title = "安责险投保监管", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:aqInsurance:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAqInsurance aqInsurance)
    {
        List<YgbAqInsurance> list = aqInsuranceService.selectAqInsuranceList(aqInsurance);
        ExcelUtil<YgbAqInsurance> util = new ExcelUtil<>(YgbAqInsurance.class);
        util.exportExcel(response, list, "aq_insurance");
    }

    @Log(title = "安责险投保监管", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:aqInsurance:sync')")
    @PostMapping("/sync")
    public AjaxResult sync(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = aqInsuranceService.syncAqInsurance(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("安责险保单数据同步完成，本次写入 " + rows + " 条记录。");
    }

    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:list')")
    @GetMapping("/claim/list")
    public TableDataInfo claimList(YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedClaimQuery(query)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:list')")
    @GetMapping("/claim/summary")
    public AjaxResult claimSummary(YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedClaimQuery(query)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:query')")
    @GetMapping("/claim/{recordId}")
    public AjaxResult getClaim(@PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        if (!CLAIM_RECORD_TYPE.equals(record.getRecordType()))
        {
            return error("当前记录不属于赔付率监控对象");
        }
        return success(record);
    }

    @Log(title = "安责险赔付率监控", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:export')")
    @PostMapping("/claim/export")
    public void exportClaim(HttpServletResponse response, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, moduleRecordService.selectModuleRecordList(typedClaimQuery(query)), "aq_insurance_claim");
    }

    @Log(title = "安责险赔付率监控", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:add')")
    @PostMapping("/claim")
    public AjaxResult addClaim(@Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedClaimQuery(record);
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "安责险赔付率监控", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:edit')")
    @PutMapping("/claim")
    public AjaxResult editClaim(@Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedClaimQuery(record);
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "安责险赔付率监控", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:aqInsuranceClaim:remove')")
    @DeleteMapping("/claim/{recordIds}")
    public AjaxResult removeClaim(@PathVariable Long[] recordIds)
    {
        return toAjax(moduleRecordService.deleteModuleRecordByIds(recordIds, getUsername()));
    }

    private YgbModuleRecord typedClaimQuery(YgbModuleRecord query)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType(CLAIM_RECORD_TYPE);
        return typedQuery;
    }
}
