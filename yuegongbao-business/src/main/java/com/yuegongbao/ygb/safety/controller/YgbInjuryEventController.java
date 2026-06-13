package com.yuegongbao.ygb.safety.controller;

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
import com.yuegongbao.ygb.domain.vo.YgbInjuryStatusRequest;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEvent;
import com.yuegongbao.ygb.safety.service.IYgbInjuryEventService;

@RestController
@RequestMapping("/ygb/injury/event")
public class YgbInjuryEventController extends BaseController
{
    @Autowired
    private IYgbInjuryEventService injuryEventService;

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbInjuryEvent injuryEvent)
    {
        startPage();
        List<YgbInjuryEvent> list = injuryEventService.selectInjuryEventList(injuryEvent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbInjuryEvent injuryEvent)
    {
        return success(injuryEventService.selectInjuryEventSummary(injuryEvent));
    }

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:list')")
    @GetMapping("/analysis")
    public AjaxResult analysis(YgbInjuryEvent injuryEvent)
    {
        return success(injuryEventService.selectInjuryEventAnalysis(injuryEvent));
    }

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:list')")
    @GetMapping("/monitor")
    public AjaxResult monitor(YgbInjuryEvent injuryEvent)
    {
        return success(injuryEventService.selectInjuryEventMonitor(injuryEvent));
    }

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:list')")
    @GetMapping("/recognitionAssist/list")
    public TableDataInfo recognitionAssistList(YgbInjuryEvent injuryEvent)
    {
        startPage();
        List<YgbModuleRecord> list = injuryEventService.selectRecognitionAssistList(injuryEvent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:query')")
    @GetMapping("/recognitionAssist/{eventId}")
    public AjaxResult recognitionAssistDetail(@PathVariable Long eventId)
    {
        return success(injuryEventService.selectRecognitionAssistById(eventId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:query')")
    @GetMapping("/{eventId}")
    public AjaxResult getInfo(@PathVariable Long eventId)
    {
        return success(injuryEventService.selectInjuryEventById(eventId));
    }

    @Log(title = "工伤事件", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbInjuryEvent injuryEvent)
    {
        List<YgbInjuryEvent> list = injuryEventService.selectInjuryEventList(injuryEvent);
        ExcelUtil<YgbInjuryEvent> util = new ExcelUtil<>(YgbInjuryEvent.class);
        util.exportExcel(response, list, "工伤事件");
    }

    @Log(title = "工伤认定辅助", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:export')")
    @PostMapping("/recognitionAssist/export")
    public void exportRecognitionAssist(HttpServletResponse response, YgbInjuryEvent injuryEvent)
    {
        List<YgbModuleRecord> list = injuryEventService.selectRecognitionAssistList(injuryEvent);
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, list, "工伤认定辅助");
    }

    @Log(title = "工伤事件", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbInjuryEvent injuryEvent)
    {
        injuryEvent.setCreateBy(getUsername());
        return toAjax(injuryEventService.insertInjuryEvent(injuryEvent));
    }

    @Log(title = "工伤事件", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbInjuryEvent injuryEvent)
    {
        injuryEvent.setUpdateBy(getUsername());
        return toAjax(injuryEventService.updateInjuryEvent(injuryEvent));
    }

    @Log(title = "工伤事件状态流转", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:flow')")
    @PostMapping("/status/{eventId}")
    public AjaxResult updateStatus(@PathVariable Long eventId, @RequestBody YgbInjuryStatusRequest request)
    {
        return toAjax(injuryEventService.updateStatus(eventId, request.getInjuryStatus(), request.getApprovalResult(),
            getUsername()));
    }

    @Log(title = "工伤事件", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:injuryEvent:remove')")
    @DeleteMapping("/{eventIds}")
    public AjaxResult remove(@PathVariable Long[] eventIds)
    {
        return toAjax(injuryEventService.deleteInjuryEventByIds(eventIds, getUsername()));
    }
}
