package com.yuegongbao.ygb.portal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.service.IYgbPortalContentService;

@RestController
@RequestMapping("/ygb/portal/content")
public class YgbPortalContentController extends BaseController
{
    @Autowired
    private IYgbPortalContentService portalContentService;

    @PreAuthorize("@ss.hasPermi('ygb:portalContent:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbPortalContent query)
    {
        startPage();
        List<YgbPortalContent> list = portalContentService.selectPortalContentList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:portalContent:query')")
    @GetMapping("/{contentId}")
    public AjaxResult getInfo(@PathVariable Long contentId)
    {
        return success(portalContentService.selectPortalContentById(contentId));
    }

    @Log(title = "门户内容", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:portalContent:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbPortalContent content)
    {
        content.setCreateBy(getUsername());
        return toAjax(portalContentService.insertPortalContent(content));
    }

    @Log(title = "门户内容", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:portalContent:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbPortalContent content)
    {
        content.setUpdateBy(getUsername());
        return toAjax(portalContentService.updatePortalContent(content));
    }

    @Log(title = "门户内容", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:portalContent:remove')")
    @DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds)
    {
        return toAjax(portalContentService.deletePortalContentByIds(contentIds, getUsername()));
    }
}
