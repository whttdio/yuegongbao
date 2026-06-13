package com.yuegongbao.ygb.foundation.controller;

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
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService;

@RestController
@RequestMapping("/ygb/enterprise")
public class YgbEnterpriseController extends BaseController
{
    private static final String HIGH_RISK_RECORD_TYPE = "ENTERPRISE_HIGH_RISK";
    private static final String RELATION_RECORD_TYPE = "ENTERPRISE_RELATION";
    private static final String UNION_RECORD_TYPE = "ENTERPRISE_UNION";

    @Autowired
    private IYgbEnterpriseService enterpriseService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasPermi('ygb:enterprise:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbEnterprise enterprise)
    {
        startPage();
        List<YgbEnterprise> list = enterpriseService.selectEnterpriseList(enterprise);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:enterprise:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbEnterprise enterprise)
    {
        return success(enterpriseService.selectEnterpriseSummary(enterprise));
    }

    @Log(title = "Enterprise", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:enterprise:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbEnterprise enterprise)
    {
        List<YgbEnterprise> list = enterpriseService.selectEnterpriseList(enterprise);
        ExcelUtil<YgbEnterprise> util = new ExcelUtil<>(YgbEnterprise.class);
        util.exportExcel(response, list, "enterprise");
    }

    @PreAuthorize("@ss.hasPermi('ygb:enterprise:query')")
    @GetMapping("/{enterpriseId}")
    public AjaxResult getInfo(@PathVariable Long enterpriseId)
    {
        return success(enterpriseService.selectEnterpriseById(enterpriseId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:enterprise:add')")
    @Log(title = "Enterprise", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbEnterprise enterprise)
    {
        if (!enterpriseService.checkEnterpriseNameUnique(enterprise))
        {
            return error("Add enterprise failed: name already exists");
        }
        if (!enterpriseService.checkEnterpriseCodeUnique(enterprise))
        {
            return error("Add enterprise failed: credit code already exists");
        }
        enterprise.setCreateBy(getUsername());
        return toAjax(enterpriseService.insertEnterprise(enterprise));
    }

    @PreAuthorize("@ss.hasPermi('ygb:enterprise:edit')")
    @Log(title = "Enterprise", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbEnterprise enterprise)
    {
        if (!enterpriseService.checkEnterpriseNameUnique(enterprise))
        {
            return error("Update enterprise failed: name already exists");
        }
        if (!enterpriseService.checkEnterpriseCodeUnique(enterprise))
        {
            return error("Update enterprise failed: credit code already exists");
        }
        enterprise.setUpdateBy(getUsername());
        return toAjax(enterpriseService.updateEnterprise(enterprise));
    }

    @PreAuthorize("@ss.hasPermi('ygb:enterprise:remove')")
    @Log(title = "Enterprise", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enterpriseIds}")
    public AjaxResult remove(@PathVariable Long[] enterpriseIds)
    {
        return toAjax(enterpriseService.deleteEnterpriseByIds(enterpriseIds, getUsername()));
    }

    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return success(enterpriseService.selectEnterpriseOptions());
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:list,ygb:enterpriseRelation:list,ygb:enterpriseUnion:list')")
    @GetMapping("/{submodule}/list")
    public TableDataInfo listSubmodule(@PathVariable String submodule, YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, resolveRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:list,ygb:enterpriseRelation:list,ygb:enterpriseUnion:list')")
    @GetMapping("/{submodule}/summary")
    public AjaxResult summarySubmodule(@PathVariable String submodule, YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, resolveRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:query,ygb:enterpriseRelation:query,ygb:enterpriseUnion:query')")
    @GetMapping("/{submodule}/{recordId}")
    public AjaxResult getSubmodule(@PathVariable String submodule, @PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        String recordType = resolveRecordType(submodule);
        if (record == null || !recordType.equals(record.getRecordType()))
        {
            throw new ServiceException("Record type does not match current enterprise submodule");
        }
        return success(record);
    }

    @Log(title = "Enterprise Submodule", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:export,ygb:enterpriseRelation:export,ygb:enterpriseUnion:export')")
    @PostMapping("/{submodule}/export")
    public void exportSubmodule(HttpServletResponse response, @PathVariable String submodule, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response,
            moduleRecordService.selectModuleRecordList(typedQuery(query, resolveRecordType(submodule))),
            "enterprise_" + submodule);
    }

    @Log(title = "Enterprise Submodule", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:add,ygb:enterpriseRelation:add,ygb:enterpriseUnion:add')")
    @PostMapping("/{submodule}")
    public AjaxResult addSubmodule(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedRecord(record, resolveRecordType(submodule));
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "Enterprise Submodule", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:edit,ygb:enterpriseRelation:edit,ygb:enterpriseUnion:edit')")
    @PutMapping("/{submodule}")
    public AjaxResult editSubmodule(@PathVariable String submodule, @Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedRecord(record, resolveRecordType(submodule));
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "Enterprise Submodule", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:enterpriseHighRisk:remove,ygb:enterpriseRelation:remove,ygb:enterpriseUnion:remove')")
    @DeleteMapping("/{submodule}/{recordIds}")
    public AjaxResult removeSubmodule(@PathVariable String submodule, @PathVariable Long[] recordIds)
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
        if ("highRisk".equals(submodule))
        {
            return HIGH_RISK_RECORD_TYPE;
        }
        if ("relation".equals(submodule))
        {
            return RELATION_RECORD_TYPE;
        }
        if ("union".equals(submodule))
        {
            return UNION_RECORD_TYPE;
        }
        throw new ServiceException("Unsupported enterprise submodule: " + submodule);
    }
}
