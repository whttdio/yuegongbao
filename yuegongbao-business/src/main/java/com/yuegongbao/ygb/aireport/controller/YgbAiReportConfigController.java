package com.yuegongbao.ygb.aireport.controller;

import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportConfigService;

@RestController
@RequestMapping("/ygb/aiReport/config")
public class YgbAiReportConfigController extends BaseController
{
    @Autowired
    private IYgbAiReportConfigService aiReportConfigService;

    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAiReportConfig config)
    {
        startPage();
        List<YgbAiReportConfig> list = aiReportConfigService.selectAiReportConfigList(config);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbAiReportConfig config)
    {
        return success(aiReportConfigService.selectAiReportConfigSummary(config));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:query')")
    @GetMapping("/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(aiReportConfigService.selectAiReportConfigById(configId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:query')")
    @GetMapping("/current")
    public AjaxResult current(String regionCode, @DateTimeFormat(pattern = "yyyy-MM-dd") Date effectiveDate)
    {
        return success(aiReportConfigService.selectCurrentConfig(regionCode, effectiveDate));
    }

    @Log(title = "AI评分模型", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAiReportConfig config)
    {
        List<YgbAiReportConfig> list = aiReportConfigService.selectAiReportConfigList(config);
        ExcelUtil<YgbAiReportConfig> util = new ExcelUtil<>(YgbAiReportConfig.class);
        util.exportExcel(response, list, "AI评分模型");
    }

    @Log(title = "AI评分模型", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbAiReportConfig config)
    {
        return toAjax(aiReportConfigService.saveAiReportConfig(config, getUsername()));
    }

    @Log(title = "AI评分模型", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbAiReportConfig config)
    {
        return toAjax(aiReportConfigService.saveAiReportConfig(config, getUsername()));
    }

    @Log(title = "AI评分模型", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportConfig:activate')")
    @PutMapping("/{configId}/activate")
    public AjaxResult activate(@PathVariable Long configId)
    {
        return toAjax(aiReportConfigService.activateConfig(configId, getUsername()));
    }
}
