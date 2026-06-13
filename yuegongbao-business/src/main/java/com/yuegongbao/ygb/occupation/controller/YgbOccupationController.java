package com.yuegongbao.ygb.occupation.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@RestController
@RequestMapping("/ygb/occupation")
public class YgbOccupationController extends BaseController
{
    private static final String PREVENTION_RECORD_TYPE = "OCCUPATION_PREVENTION";
    private static final String HEALTH_ARCHIVE_RECORD_TYPE = "OCCUPATION_HEALTH_ARCHIVE";

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasPermi('ygb:occupationPrevention:list')")
    @GetMapping("/prevention/list")
    public TableDataInfo preventionList(YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, PREVENTION_RECORD_TYPE)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:occupationPrevention:list')")
    @GetMapping("/prevention/summary")
    public AjaxResult preventionSummary(YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, PREVENTION_RECORD_TYPE)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:occupationPrevention:query')")
    @GetMapping("/prevention/{recordId}")
    public AjaxResult getPrevention(@PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        if (!PREVENTION_RECORD_TYPE.equals(record.getRecordType()))
        {
            return error("Record type does not match occupation prevention");
        }
        return success(record);
    }

    @Log(title = "Occupation Prevention", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:occupationPrevention:export')")
    @PostMapping("/prevention/export")
    public void exportPrevention(HttpServletResponse response, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, moduleRecordService.selectModuleRecordList(typedQuery(query, PREVENTION_RECORD_TYPE)),
            "occupation_prevention");
    }

    @PreAuthorize("@ss.hasPermi('ygb:occupationHealthArchive:list')")
    @GetMapping("/healthArchive/list")
    public TableDataInfo healthArchiveList(YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedQuery(query, HEALTH_ARCHIVE_RECORD_TYPE)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:occupationHealthArchive:list')")
    @GetMapping("/healthArchive/summary")
    public AjaxResult healthArchiveSummary(YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedQuery(query, HEALTH_ARCHIVE_RECORD_TYPE)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:occupationHealthArchive:query')")
    @GetMapping("/healthArchive/{recordId}")
    public AjaxResult getHealthArchive(@PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        if (!HEALTH_ARCHIVE_RECORD_TYPE.equals(record.getRecordType()))
        {
            return error("Record type does not match occupation health archive");
        }
        return success(record);
    }

    @Log(title = "Occupation Health Archive", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:occupationHealthArchive:export')")
    @PostMapping("/healthArchive/export")
    public void exportHealthArchive(HttpServletResponse response, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response,
            moduleRecordService.selectModuleRecordList(typedQuery(query, HEALTH_ARCHIVE_RECORD_TYPE)),
            "occupation_health_archive");
    }

    private YgbModuleRecord typedQuery(YgbModuleRecord query, String recordType)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType(recordType);
        return typedQuery;
    }
}
