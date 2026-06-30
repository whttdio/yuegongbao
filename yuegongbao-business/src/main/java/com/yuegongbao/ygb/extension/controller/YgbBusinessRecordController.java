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
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.extension.domain.YgbBusinessRecord;
import com.yuegongbao.ygb.extension.service.IYgbBusinessRecordService;

@RestController
@RequestMapping("/ygb")
public class YgbBusinessRecordController extends BaseController
{
    @Autowired
    private IYgbBusinessRecordService businessRecordService;

    @PreAuthorize("@ss.hasAnyPermi('ygb:' + #module + ':list,ygb:' + #module + ':query')")
    @GetMapping("/{module}/list")
    public TableDataInfo list(@PathVariable String module, YgbBusinessRecord query)
    {
        startPage();
        return getDataTable(businessRecordService.selectBusinessRecordList(module, query));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:' + #module + ':list,ygb:' + #module + ':query')")
    @GetMapping("/{module}/summary")
    public AjaxResult summary(@PathVariable String module, YgbBusinessRecord query)
    {
        return success(businessRecordService.selectBusinessRecordSummary(module, query));
    }

    @PreAuthorize("@ss.hasPermi('ygb:' + #module + ':query')")
    @GetMapping("/{module}/{businessId}")
    public AjaxResult detail(@PathVariable String module, @PathVariable Long businessId)
    {
        return success(businessRecordService.selectBusinessRecordById(module, businessId));
    }

    @Log(title = "YGB Business Record", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:' + #module + ':export')")
    @PostMapping("/{module}/export")
    public void export(HttpServletResponse response, @PathVariable String module, YgbBusinessRecord query)
    {
        List<YgbBusinessRecord> list = businessRecordService.selectBusinessRecordList(module, query);
        ExcelUtil<YgbBusinessRecord> util = new ExcelUtil<>(YgbBusinessRecord.class);
        util.exportExcel(response, list, module);
    }

    @Log(title = "YGB Business Record", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:' + #module + ':add')")
    @PostMapping("/{module}")
    public AjaxResult add(@PathVariable String module, @Validated @RequestBody YgbBusinessRecord record)
    {
        return toAjax(businessRecordService.insertBusinessRecord(module, record, getUsername()));
    }

    @Log(title = "YGB Business Record", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:' + #module + ':edit')")
    @PutMapping("/{module}")
    public AjaxResult edit(@PathVariable String module, @Validated @RequestBody YgbBusinessRecord record)
    {
        return toAjax(businessRecordService.updateBusinessRecord(module, record, getUsername()));
    }

    @Log(title = "YGB Business Record", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:' + #module + ':remove')")
    @DeleteMapping("/{module}/{businessIds}")
    public AjaxResult remove(@PathVariable String module, @PathVariable Long[] businessIds)
    {
        return toAjax(businessRecordService.deleteBusinessRecordByIds(module, businessIds, getUsername()));
    }
}
