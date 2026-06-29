package com.yuegongbao.ygb.aireport.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.aireport.domain.YgbAiReport;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboard;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportGenerateRequest;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportService;

@RestController
@RequestMapping("/ygb/aiReport")
public class YgbAiReportController extends BaseController
{
    @Autowired
    private IYgbAiReportService aiReportService;

    @PreAuthorize("@ss.hasPermi('ygb:aiReport:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAiReport report)
    {
        startPage();
        List<YgbAiReport> list = aiReportService.selectAiReportList(report);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReport:query')")
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable Long reportId)
    {
        AjaxResult result = success(aiReportService.selectAiReportById(reportId));
        result.put("items", aiReportService.selectAiReportItems(reportId));
        return result;
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReport:list')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard(YgbAiReport report, Long activeReportId)
    {
        YgbAiReportDashboard dashboard = aiReportService.selectDashboard(report, activeReportId);
        return success(dashboard);
    }

    @Log(title = "AI监测报告", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:aiReport:generate')")
    @PostMapping("/generate")
    public AjaxResult generate(@Validated @RequestBody YgbAiReportGenerateRequest request)
    {
        Long reportId = aiReportService.generateReport(request, getUsername());
        AjaxResult result = success("AI监测报告生成完成");
        result.put("reportId", reportId);
        return result;
    }

    @Log(title = "AI监测报告", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReport:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAiReport report)
    {
        exportReport(response, report);
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReport:export')")
    @GetMapping("/download")
    public void download(HttpServletResponse response, YgbAiReport report)
    {
        exportReport(response, report);
    }

    private void exportReport(HttpServletResponse response, YgbAiReport report)
    {
        List<YgbAiReport> list = aiReportService.selectAiReportList(report);
        ExcelUtil<YgbAiReport> util = new ExcelUtil<>(YgbAiReport.class);
        util.exportExcel(response, list, "AI监测报告");
    }
}
