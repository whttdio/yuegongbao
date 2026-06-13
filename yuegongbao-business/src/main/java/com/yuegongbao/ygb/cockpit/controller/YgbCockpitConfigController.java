package com.yuegongbao.ygb.cockpit.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitConfig;
import com.yuegongbao.ygb.cockpit.service.IYgbCockpitConfigService;

@RestController
@RequestMapping("/ygb/cockpit/config")
public class YgbCockpitConfigController extends BaseController
{
    @Autowired
    private IYgbCockpitConfigService cockpitConfigService;

    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbCockpitConfig config)
    {
        startPage();
        List<YgbCockpitConfig> list = cockpitConfigService.selectCockpitConfigList(config);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbCockpitConfig config)
    {
        return success(cockpitConfigService.selectCockpitConfigSummary(config));
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:query')")
    @GetMapping("/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(cockpitConfigService.selectCockpitConfigById(configId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:query')")
    @GetMapping("/current")
    public AjaxResult current(@RequestParam(required = false) String regionCode)
    {
        return success(cockpitConfigService.selectCurrentConfig(regionCode));
    }

    @Log(title = "驾驶舱配置", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbCockpitConfig config)
    {
        List<YgbCockpitConfig> list = cockpitConfigService.selectCockpitConfigList(config);
        ExcelUtil<YgbCockpitConfig> util = new ExcelUtil<>(YgbCockpitConfig.class);
        util.exportExcel(response, list, "cockpit_config");
    }

    @Log(title = "驾驶舱配置", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:add')")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody YgbCockpitConfig config)
    {
        if (!cockpitConfigService.checkConfigCodeUnique(config))
        {
            return error("配置编码已存在: " + config.getConfigCode());
        }
        config.setCreateBy(getUsername());
        config.setUpdateBy(getUsername());
        return toAjax(cockpitConfigService.insertCockpitConfig(config));
    }

    @Log(title = "驾驶舱配置", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody YgbCockpitConfig config)
    {
        if (!cockpitConfigService.checkConfigCodeUnique(config))
        {
            return error("配置编码已存在: " + config.getConfigCode());
        }
        config.setUpdateBy(getUsername());
        return toAjax(cockpitConfigService.updateCockpitConfig(config));
    }

    @Log(title = "驾驶舱配置", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:cockpitConfig:remove')")
    @DeleteMapping("/remove/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(cockpitConfigService.deleteCockpitConfigByIds(configIds, getUsername()));
    }
}
