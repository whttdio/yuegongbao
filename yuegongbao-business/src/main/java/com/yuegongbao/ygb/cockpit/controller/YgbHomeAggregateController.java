package com.yuegongbao.ygb.cockpit.controller;

import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard;
import com.yuegongbao.ygb.cockpit.service.IYgbCockpitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ygb/home")
public class YgbHomeAggregateController extends BaseController
{
    @Autowired
    private IYgbCockpitService cockpitService;

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/ygb/aggregate")
    public AjaxResult ygbAggregate(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate, @RequestParam(required = false) String statMonth,
        @RequestParam(required = false) Integer days)
    {
        YgbWorkbenchDashboard dashboard = cockpitService.getYgbHomeAggregate(regionCode, statDate, statMonth, days);
        return success(dashboard);
    }

    @PreAuthorize("@ss.hasPermi('ygb:cockpit:list')")
    @GetMapping("/azb/aggregate")
    public AjaxResult azbAggregate(@RequestParam(required = false) String regionCode,
        @RequestParam(required = false) String statDate, @RequestParam(required = false) String statMonth,
        @RequestParam(required = false) Integer days)
    {
        YgbAzbCockpitDashboard dashboard = cockpitService.getAzbHomeAggregate(regionCode, statDate, statMonth, days);
        return success(dashboard);
    }
}
