package com.yuegongbao.ygb.warning.controller;

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
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.domain.vo.YgbWarningHandleRequest;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@RestController
@RequestMapping("/ygb/warning")
public class YgbWarningController extends BaseController
{
    @Autowired
    private IYgbWarningService warningService;

    @PreAuthorize("@ss.hasPermi('ygb:warning:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbWarning warning)
    {
        startPage();
        List<YgbWarning> list = warningService.selectWarningList(warning);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:warning:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbWarning warning)
    {
        return success(warningService.selectWarningSummary(warning));
    }

    @PreAuthorize("@ss.hasPermi('ygb:warning:list')")
    @GetMapping("/analysis")
    public AjaxResult analysis(YgbWarning warning)
    {
        return success(warningService.selectWarningAnalysis(warning));
    }

    @PreAuthorize("@ss.hasPermi('ygb:warning:query')")
    @GetMapping("/{warnId}")
    public AjaxResult getInfo(@PathVariable Long warnId)
    {
        AjaxResult result = success(warningService.selectWarningById(warnId));
        result.put("logs", warningService.selectWarningHandleLogs(warnId));
        return result;
    }

    @Log(title = "预警中心", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:warning:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbWarning warning)
    {
        List<YgbWarning> list = warningService.selectWarningList(warning);
        ExcelUtil<YgbWarning> util = new ExcelUtil<>(YgbWarning.class);
        util.exportExcel(response, list, "预警中心");
    }

    @Log(title = "预警处置", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:warning:handle')")
    @PostMapping("/handle/{warnId}")
    public AjaxResult handle(@PathVariable Long warnId, @Validated @RequestBody YgbWarningHandleRequest request)
    {
        return toAjax(warningService.handleWarning(warnId, request.getAction(), request.getOpinion(),
            request.getAttachmentUrls(), getUsername()));
    }
}
