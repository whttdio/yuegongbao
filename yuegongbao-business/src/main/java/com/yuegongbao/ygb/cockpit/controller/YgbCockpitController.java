package com.yuegongbao.ygb.cockpit.controller;

import java.util.List;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;
import com.yuegongbao.ygb.cockpit.service.IYgbCockpitService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ygb/cockpit")
public class YgbCockpitController extends BaseController
{
    @Autowired
    private IYgbCockpitService cockpitService;

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/indicators")
    public AjaxResult indicators(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate)
    {
        return success(cockpitService.getIndicators(regionCode, statDate));
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/trend")
    public AjaxResult trend(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate, @RequestParam(required = false) Integer days)
    {
        return success(cockpitService.listTrend(regionCode, statDate, days));
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/distribution")
    public AjaxResult distribution(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statMonth)
    {
        return success(cockpitService.listWarningDistribution(regionCode, statMonth));
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/map")
    public AjaxResult map(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate)
    {
        return success(cockpitService.getMapFeatures(regionCode, statDate));
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/ygb/dashboard")
    public AjaxResult ygbDashboard(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate, @RequestParam(required = false) String statMonth,
        @RequestParam(required = false) Integer days)
    {
        YgbWorkbenchDashboard dashboard = cockpitService.getYgbDashboard(regionCode, statDate, statMonth, days);
        return success(dashboard);
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/azb/dashboard")
    public AjaxResult azbDashboard(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate, @RequestParam(required = false) String statMonth,
        @RequestParam(required = false) Integer days)
    {
        YgbAzbCockpitDashboard dashboard = cockpitService.getAzbDashboard(regionCode, statDate, statMonth, days);
        return success(dashboard);
    }

    @Log(title = "Cockpit", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:cockpit:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate, @RequestParam(required = false) Integer days)
    {
        List<YgbCockpitSnapshot> list = cockpitService.listTrend(regionCode, statDate, days);
        ExcelUtil<YgbCockpitSnapshot> util = new ExcelUtil<>(YgbCockpitSnapshot.class);
        util.exportExcel(response, list, "cockpit_trend");
    }
}
