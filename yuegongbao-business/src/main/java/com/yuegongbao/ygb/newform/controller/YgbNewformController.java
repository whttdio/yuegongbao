package com.yuegongbao.ygb.newform.controller;

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
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.newform.domain.YgbNewformPlatformStat;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.service.IYgbNewformWorkerService;

@RestController
@RequestMapping("/ygb/newform")
public class YgbNewformController extends BaseController
{
    private static final String TRAINING_RECORD_TYPE = "NEWFORM_TRAINING";

    @Autowired
    private IYgbNewformWorkerService newformWorkerService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasPermi('ygb:newformPlatform:list')")
    @GetMapping("/platform/list")
    public TableDataInfo platformList(YgbNewformWorker query)
    {
        startPage();
        List<YgbNewformPlatformStat> list = newformWorkerService.selectNewformPlatformList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformPlatform:list')")
    @GetMapping("/platform/summary")
    public AjaxResult platformSummary(YgbNewformWorker query)
    {
        return success(newformWorkerService.selectNewformPlatformSummary(query));
    }

    @Log(title = "Newform Platform", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:newformPlatform:export')")
    @PostMapping("/platform/export")
    public void exportPlatform(HttpServletResponse response, YgbNewformWorker query)
    {
        ExcelUtil<YgbNewformPlatformStat> util = new ExcelUtil<>(YgbNewformPlatformStat.class);
        util.exportExcel(response, newformWorkerService.selectNewformPlatformList(query), "newform_platform");
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformInjuryMonitor:list')")
    @GetMapping("/injuryMonitor/list")
    public TableDataInfo injuryMonitorList(YgbNewformWorker query)
    {
        startPage();
        List<YgbNewformPlatformStat> list = newformWorkerService.selectNewformInjuryMonitorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformInjuryMonitor:list')")
    @GetMapping("/injuryMonitor/summary")
    public AjaxResult injuryMonitorSummary(YgbNewformWorker query)
    {
        return success(newformWorkerService.selectNewformInjuryMonitorSummary(query));
    }

    @Log(title = "Newform Injury Monitor", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:newformInjuryMonitor:export')")
    @PostMapping("/injuryMonitor/export")
    public void exportInjuryMonitor(HttpServletResponse response, YgbNewformWorker query)
    {
        ExcelUtil<YgbNewformPlatformStat> util = new ExcelUtil<>(YgbNewformPlatformStat.class);
        util.exportExcel(response, newformWorkerService.selectNewformInjuryMonitorList(query), "newform_injury_monitor");
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:list')")
    @GetMapping("/training/list")
    public TableDataInfo trainingList(YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedTrainingQuery(query)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:list')")
    @GetMapping("/training/summary")
    public AjaxResult trainingSummary(YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedTrainingQuery(query)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:query')")
    @GetMapping("/training/{recordId}")
    public AjaxResult getTraining(@PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        if (!TRAINING_RECORD_TYPE.equals(record.getRecordType()))
        {
            return error("Record type does not match newform training");
        }
        return success(record);
    }

    @Log(title = "Newform Training", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:export')")
    @PostMapping("/training/export")
    public void exportTraining(HttpServletResponse response, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, moduleRecordService.selectModuleRecordList(typedTrainingQuery(query)), "newform_training");
    }

    @Log(title = "Newform Training", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:add')")
    @PostMapping("/training")
    public AjaxResult addTraining(@Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedTrainingQuery(record);
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "Newform Training", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:edit')")
    @PutMapping("/training")
    public AjaxResult editTraining(@Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedTrainingQuery(record);
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "Newform Training", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:newformTraining:remove')")
    @DeleteMapping("/training/{recordIds}")
    public AjaxResult removeTraining(@PathVariable Long[] recordIds)
    {
        return toAjax(moduleRecordService.deleteModuleRecordByIds(recordIds, getUsername()));
    }

    private YgbModuleRecord typedTrainingQuery(YgbModuleRecord query)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType(TRAINING_RECORD_TYPE);
        return typedQuery;
    }
}
