package com.yuegongbao.ygb.techdefense.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;
import com.yuegongbao.ygb.techdefense.domain.vo.YgbHeightWorkFinishRequest;
import com.yuegongbao.ygb.techdefense.service.IYgbHeightWorkReportService;

@RestController
@RequestMapping("/ygb/heightWork/report")
public class YgbHeightWorkReportController extends BaseController
{
    @Autowired
    private IYgbHeightWorkReportService heightWorkReportService;

    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:query')")
    @GetMapping("/list")
    public TableDataInfo list(YgbHeightWorkReport query)
    {
        startPage();
        List<YgbHeightWorkReport> list = heightWorkReportService.selectHeightWorkReportList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:query')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbHeightWorkReport query)
    {
        return success(heightWorkReportService.selectHeightWorkReportSummary(query));
    }

    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:query')")
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable Long reportId)
    {
        return success(heightWorkReportService.selectHeightWorkReportById(reportId));
    }

    @Log(title = "高处作业申报报备", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbHeightWorkReport report)
    {
        return toAjax(heightWorkReportService.insertHeightWorkReport(report, getUsername()));
    }

    @Log(title = "高处作业申报报备", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbHeightWorkReport report)
    {
        return toAjax(heightWorkReportService.updateHeightWorkReport(report, getUsername()));
    }

    @Log(title = "高处作业申报报备", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:finish')")
    @PostMapping("/finish/{reportId}")
    public AjaxResult finish(@PathVariable Long reportId, @Validated @RequestBody YgbHeightWorkFinishRequest request)
    {
        return toAjax(heightWorkReportService.finishHeightWorkReport(reportId, request, getUsername()));
    }

    @Log(title = "高处作业申报报备", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbHeightWorkReport query)
    {
        List<YgbHeightWorkReport> list = heightWorkReportService.selectHeightWorkReportList(query);
        ExcelUtil<YgbHeightWorkReport> util = new ExcelUtil<>(YgbHeightWorkReport.class);
        util.exportExcel(response, list, "高处作业申报报备");
    }

    @Log(title = "高处作业申报报备", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:import')")
    @PostMapping("/import")
    public AjaxResult importData(@RequestBody List<YgbHeightWorkReport> reports)
    {
        int rows = heightWorkReportService.importHeightWorkReports(reports, getUsername());
        return success("高处作业报备占位导入完成，本次处理 " + rows + " 条记录");
    }

    @PreAuthorize("@ss.hasPermi('ygb:heightWorkReport:voucher')")
    @GetMapping("/voucher/{reportId}")
    public AjaxResult voucher(@PathVariable Long reportId)
    {
        Map<String, Object> voucher = heightWorkReportService.buildVoucher(reportId);
        return success(voucher);
    }
}
