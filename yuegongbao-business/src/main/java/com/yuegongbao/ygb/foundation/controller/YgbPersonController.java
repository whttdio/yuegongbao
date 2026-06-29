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
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.service.IYgbPersonService;

@RestController
@RequestMapping("/ygb/person")
public class YgbPersonController extends BaseController
{
    private static final String CERTIFICATE_RECORD_TYPE = "PERSON_CERTIFICATE";
    private static final String BLACKLIST_RECORD_TYPE = "PERSON_BLACKLIST";
    private static final String TRAINING_RECORD_TYPE = "PERSON_TRAINING";
    private static final String HIGH_RISK_POST_RECORD_TYPE = "PERSON_HIGH_RISK_POST";
    private static final String RISK_POST_RECORD_TYPE = "PERSON_RISK_POST";
    private static final String EXPERT_RECORD_TYPE = "PERSON_EXPERT";

    @Autowired
    private IYgbPersonService personService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasAnyPermi('ygb:person:list,ygb:contract:add,ygb:contract:edit')")
    @GetMapping("/list")
    public TableDataInfo list(YgbPerson person)
    {
        startPage();
        List<YgbPerson> list = personService.selectPersonList(person);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:person:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbPerson person)
    {
        return success(personService.selectPersonSummary(person));
    }

    @Log(title = "Person", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:person:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbPerson person)
    {
        List<YgbPerson> list = personService.selectPersonList(person);
        ExcelUtil<YgbPerson> util = new ExcelUtil<>(YgbPerson.class);
        util.exportExcel(response, list, "person");
    }

    @PreAuthorize("@ss.hasPermi('ygb:person:query')")
    @GetMapping("/{personId}")
    public AjaxResult getInfo(@PathVariable Long personId)
    {
        return success(personService.selectPersonById(personId));
    }

    @GetMapping("/optionselect")
    public AjaxResult optionselect(Long enterpriseId)
    {
        return success(personService.selectPersonOptions(enterpriseId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:person:add')")
    @Log(title = "Person", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbPerson person)
    {
        if (!personService.checkPersonIdCardUnique(person))
        {
            return error("Add person failed: id card already exists");
        }
        person.setCreateBy(getUsername());
        return toAjax(personService.insertPerson(person));
    }

    @PreAuthorize("@ss.hasPermi('ygb:person:edit')")
    @Log(title = "Person", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbPerson person)
    {
        if (!personService.checkPersonIdCardUnique(person))
        {
            return error("Update person failed: id card already exists");
        }
        person.setUpdateBy(getUsername());
        return toAjax(personService.updatePerson(person));
    }

    @PreAuthorize("@ss.hasPermi('ygb:person:remove')")
    @Log(title = "Person", businessType = BusinessType.DELETE)
    @DeleteMapping("/{personIds}")
    public AjaxResult remove(@PathVariable Long[] personIds)
    {
        return toAjax(personService.deletePersonByIds(personIds, getUsername()));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:list,ygb:personBlacklist:list,ygb:personTraining:list,ygb:personHighRiskPost:list,ygb:personRiskPost:list,ygb:personExpert:list')")
    @GetMapping("/{submodule}/list")
    public TableDataInfo listSubmodule(@PathVariable String submodule, YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, resolveRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:list,ygb:personBlacklist:list,ygb:personTraining:list,ygb:personHighRiskPost:list,ygb:personRiskPost:list,ygb:personExpert:list')")
    @GetMapping("/{submodule}/summary")
    public AjaxResult summarySubmodule(@PathVariable String submodule, YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, resolveRecordType(submodule))));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:query,ygb:personBlacklist:query,ygb:personTraining:query,ygb:personHighRiskPost:query,ygb:personRiskPost:query,ygb:personExpert:query')")
    @GetMapping("/{submodule}/{recordId}")
    public AjaxResult getSubmodule(@PathVariable String submodule, @PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        String recordType = resolveRecordType(submodule);
        if (record == null || !recordType.equals(record.getRecordType()))
        {
            throw new ServiceException("Record type does not match current person submodule");
        }
        return success(record);
    }

    @Log(title = "Person Submodule", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:export,ygb:personBlacklist:export,ygb:personTraining:export,ygb:personHighRiskPost:export,ygb:personRiskPost:export,ygb:personExpert:export')")
    @PostMapping("/{submodule}/export")
    public void exportSubmodule(HttpServletResponse response, @PathVariable String submodule, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, moduleRecordService.selectModuleRecordList(typedQuery(query, resolveRecordType(submodule))),
            "person_" + submodule);
    }

    @Log(title = "Person Submodule", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:add,ygb:personBlacklist:add,ygb:personTraining:add,ygb:personHighRiskPost:add,ygb:personRiskPost:add,ygb:personExpert:add')")
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

    @Log(title = "Person Submodule", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:edit,ygb:personBlacklist:edit,ygb:personTraining:edit,ygb:personHighRiskPost:edit,ygb:personRiskPost:edit,ygb:personExpert:edit')")
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

    @Log(title = "Person Submodule", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:personCertificate:remove,ygb:personBlacklist:remove,ygb:personTraining:remove,ygb:personHighRiskPost:remove,ygb:personRiskPost:remove,ygb:personExpert:remove')")
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
        if ("certificate".equals(submodule))
        {
            return CERTIFICATE_RECORD_TYPE;
        }
        if ("blacklist".equals(submodule))
        {
            return BLACKLIST_RECORD_TYPE;
        }
        if ("training".equals(submodule))
        {
            return TRAINING_RECORD_TYPE;
        }
        if ("highRiskPost".equals(submodule))
        {
            return HIGH_RISK_POST_RECORD_TYPE;
        }
        if ("riskPost".equals(submodule))
        {
            return RISK_POST_RECORD_TYPE;
        }
        if ("expert".equals(submodule))
        {
            return EXPERT_RECORD_TYPE;
        }
        throw new ServiceException("Unsupported person submodule: " + submodule);
    }
}
