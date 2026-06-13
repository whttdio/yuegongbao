package com.yuegongbao.ygb.framework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.ygb.framework.service.IYgbModuleCatalogService;

@RestController
@RequestMapping("/ygb/framework")
public class YgbFrameworkController extends BaseController
{
    @Autowired
    private IYgbModuleCatalogService moduleCatalogService;

    @PreAuthorize("@ss.hasPermi('ygb:framework:list')")
    @GetMapping("/modules")
    public AjaxResult modules()
    {
        return success(moduleCatalogService.listModules());
    }
}
