package com.yuegongbao.ygb.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.ygb.portal.service.IYgbPortalPublicService;

/**
 * 门户网站公开接口（530 官网模块，无需登录）。
 */
@RestController
@RequestMapping("/open/portal")
public class YgbPortalPublicController
{
    @Autowired
    private IYgbPortalPublicService portalPublicService;

    @GetMapping("/{portalCode}/home")
    public AjaxResult home(@PathVariable String portalCode)
    {
        return AjaxResult.success(portalPublicService.getPortalHome(portalCode));
    }

    @GetMapping("/{portalCode}/search")
    public AjaxResult search(@PathVariable String portalCode, @RequestParam String keyword)
    {
        return AjaxResult.success(portalPublicService.searchPortalContent(portalCode, keyword));
    }

    @GetMapping("/{portalCode}/jobs")
    public AjaxResult jobs(@PathVariable String portalCode,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String salary,
        @RequestParam(required = false, defaultValue = "20") Integer limit)
    {
        return AjaxResult.success(portalPublicService.listPortalJobs(keyword, location, salary, limit));
    }

    @GetMapping("/content/{contentId}")
    public AjaxResult detail(@PathVariable Long contentId)
    {
        return AjaxResult.success(portalPublicService.getPortalContentDetail(contentId));
    }

    @GetMapping("/jobs/{jobId}")
    public AjaxResult jobDetail(@PathVariable Long jobId)
    {
        return AjaxResult.success(portalPublicService.getPortalJobDetail(jobId));
    }
}
