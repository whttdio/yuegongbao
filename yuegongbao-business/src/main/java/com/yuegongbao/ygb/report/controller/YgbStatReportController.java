package com.yuegongbao.ygb.report.controller;

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
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.report.domain.YgbStatReport;
import com.yuegongbao.ygb.report.domain.YgbStatReportGenerateRequest;
import com.yuegongbao.ygb.report.service.IYgbStatReportService;

@RestController
@RequestMapping("/ygb/report")
public class YgbStatReportController extends BaseController
{
    private static final String QUERY_EXPR =
        "@ss.hasAnyPermi('ygb:statReport:injury:query,ygb:statReport:warning:query,ygb:statReport:salary:query,"
            + "ygb:statReport:socialTax:query,ygb:statReport:employment:query,ygb:statReport:attendance:query,"
            + "ygb:statReport:social:query,ygb:statReport:tax:query,ygb:statReport:aqInsurance:query,"
            + "ygb:statReport:newform:query,ygb:statReport:occupation:query,ygb:statReport:union:query,"
            + "ygb:statReport:custom:query,ygb:statReport:device:query,ygb:statReport:expansion:query,"
            + "ygb:statReport:rectification:query,"
            + "azb:statReport:injury:query,azb:statReport:warning:query,azb:statReport:salary:query,"
            + "azb:statReport:socialTax:query,azb:statReport:employment:query,azb:statReport:attendance:query,"
            + "azb:statReport:social:query,azb:statReport:tax:query,azb:statReport:aqInsurance:query,"
            + "azb:statReport:newform:query,azb:statReport:occupation:query,azb:statReport:union:query,"
            + "azb:statReport:custom:query,azb:statReport:device:query,azb:statReport:expansion:query,"
            + "azb:statReport:rectification:query')";

    private static final String GENERATE_EXPR =
        "@ss.hasAnyPermi('ygb:statReport:injury:generate,ygb:statReport:warning:generate,ygb:statReport:salary:generate,"
            + "ygb:statReport:socialTax:generate,ygb:statReport:employment:generate,ygb:statReport:attendance:generate,"
            + "ygb:statReport:social:generate,ygb:statReport:tax:generate,ygb:statReport:aqInsurance:generate,"
            + "ygb:statReport:newform:generate,ygb:statReport:occupation:generate,ygb:statReport:union:generate,"
            + "ygb:statReport:custom:generate,ygb:statReport:device:generate,ygb:statReport:expansion:generate,"
            + "ygb:statReport:rectification:generate,"
            + "azb:statReport:injury:generate,azb:statReport:warning:generate,azb:statReport:salary:generate,"
            + "azb:statReport:socialTax:generate,azb:statReport:employment:generate,azb:statReport:attendance:generate,"
            + "azb:statReport:social:generate,azb:statReport:tax:generate,azb:statReport:aqInsurance:generate,"
            + "azb:statReport:newform:generate,azb:statReport:occupation:generate,azb:statReport:union:generate,"
            + "azb:statReport:custom:generate,azb:statReport:device:generate,azb:statReport:expansion:generate,"
            + "azb:statReport:rectification:generate')";

    private static final String EXPORT_EXPR =
        "@ss.hasAnyPermi('ygb:statReport:injury:export,ygb:statReport:warning:export,ygb:statReport:salary:export,"
            + "ygb:statReport:socialTax:export,ygb:statReport:employment:export,ygb:statReport:attendance:export,"
            + "ygb:statReport:social:export,ygb:statReport:tax:export,ygb:statReport:aqInsurance:export,"
            + "ygb:statReport:newform:export,ygb:statReport:occupation:export,ygb:statReport:union:export,"
            + "ygb:statReport:custom:export,ygb:statReport:device:export,ygb:statReport:expansion:export,"
            + "ygb:statReport:rectification:export,"
            + "azb:statReport:injury:export,azb:statReport:warning:export,azb:statReport:salary:export,"
            + "azb:statReport:socialTax:export,azb:statReport:employment:export,azb:statReport:attendance:export,"
            + "azb:statReport:social:export,azb:statReport:tax:export,azb:statReport:aqInsurance:export,"
            + "azb:statReport:newform:export,azb:statReport:occupation:export,azb:statReport:union:export,"
            + "azb:statReport:custom:export,azb:statReport:device:export,azb:statReport:expansion:export,"
            + "azb:statReport:rectification:export')";

    @Autowired
    private IYgbStatReportService statReportService;

    @PreAuthorize(QUERY_EXPR)
    @GetMapping("/list")
    public TableDataInfo list(YgbStatReport report)
    {
        checkReportPermission(report == null ? null : report.getReportCode(), "query");
        startPage();
        List<YgbStatReport> list = statReportService.selectStatReportList(report);
        return getDataTable(list);
    }

    @PreAuthorize(QUERY_EXPR)
    @GetMapping("/summary")
    public AjaxResult summary(YgbStatReport report)
    {
        checkReportPermission(report == null ? null : report.getReportCode(), "query");
        return success(statReportService.selectStatReportSummary(report));
    }

    @PreAuthorize(QUERY_EXPR)
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable Long reportId)
    {
        YgbStatReport report = statReportService.selectStatReportById(reportId);
        checkReportPermission(report.getReportCode(), "query");
        AjaxResult result = success(report);
        result.put("items", statReportService.selectStatReportItems(reportId));
        return result;
    }

    @Log(title = "统计报表", businessType = BusinessType.OTHER)
    @PreAuthorize(GENERATE_EXPR)
    @PostMapping("/generate")
    public AjaxResult generate(@Validated @RequestBody YgbStatReportGenerateRequest request)
    {
        checkReportPermission(request == null ? null : request.getReportCode(), "generate");
        Long reportId = statReportService.generateReport(request, getUsername());
        AjaxResult result = success("统计报表生成完成");
        result.put("reportId", reportId);
        return result;
    }

    @Log(title = "统计报表", businessType = BusinessType.EXPORT)
    @PreAuthorize(EXPORT_EXPR)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbStatReport report)
    {
        exportReport(response, report);
    }

    @PreAuthorize(EXPORT_EXPR)
    @GetMapping("/download")
    public void download(HttpServletResponse response, YgbStatReport report)
    {
        exportReport(response, report);
    }

    private void exportReport(HttpServletResponse response, YgbStatReport report)
    {
        checkReportPermission(report == null ? null : report.getReportCode(), "export");
        List<YgbStatReport> list = statReportService.selectStatReportList(report);
        ExcelUtil<YgbStatReport> util = new ExcelUtil<>(YgbStatReport.class);
        util.exportExcel(response, list, "统计报表");
    }

    private void checkReportPermission(String reportCode, String action)
    {
        if (StringUtils.isEmpty(reportCode))
        {
            throw new ServiceException("月报类型不能为空");
        }
        if (!hasTypedPermission(reportCode, action))
        {
            throw new ServiceException("无权访问当前月报");
        }
    }

    private boolean hasTypedPermission(String reportCode, String action)
    {
        String suffix = resolvePermissionSuffix(reportCode);
        return SecurityUtils.hasPermi("ygb:statReport:" + suffix + ":" + action)
            || SecurityUtils.hasPermi("azb:statReport:" + suffix + ":" + action);
    }

    private String resolvePermissionSuffix(String reportCode)
    {
        String normalizedReportCode = reportCode.trim().toUpperCase();
        if ("INJURY_RATE".equals(normalizedReportCode) || "INJURY".equals(normalizedReportCode))
        {
            return "injury";
        }
        if ("WARNING_OVERVIEW".equals(normalizedReportCode))
        {
            return "warning";
        }
        if ("SALARY_PAYMENT".equals(normalizedReportCode) || "SALARY".equals(normalizedReportCode))
        {
            return "salary";
        }
        if ("EMPLOYMENT".equals(normalizedReportCode))
        {
            return "employment";
        }
        if ("ATTENDANCE".equals(normalizedReportCode))
        {
            return "attendance";
        }
        if ("SOCIAL".equals(normalizedReportCode))
        {
            return "social";
        }
        if ("TAX".equals(normalizedReportCode))
        {
            return "tax";
        }
        if ("SOCIAL_TAX".equals(normalizedReportCode))
        {
            return "socialTax";
        }
        if ("AQ_INSURANCE".equals(normalizedReportCode))
        {
            return "aqInsurance";
        }
        if ("NEWFORM".equals(normalizedReportCode))
        {
            return "newform";
        }
        if ("OCCUPATION".equals(normalizedReportCode))
        {
            return "occupation";
        }
        if ("UNION_SUPERVISION".equals(normalizedReportCode))
        {
            return "union";
        }
        if ("CUSTOM".equals(normalizedReportCode))
        {
            return "custom";
        }
        if ("DEVICE_STATS".equals(normalizedReportCode))
        {
            return "device";
        }
        if ("EXPANSION_REDUCTION".equals(normalizedReportCode))
        {
            return "expansion";
        }
        if ("SPECIAL_RECTIFICATION".equals(normalizedReportCode))
        {
            return "rectification";
        }
        throw new ServiceException("不支持的月报类型");
    }
}
