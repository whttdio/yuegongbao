package com.yuegongbao.ygb.regulation.controller;

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
import com.yuegongbao.ygb.domain.vo.YgbUninsuredHandleRequest;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredList;
import com.yuegongbao.ygb.regulation.service.IYgbUninsuredListService;

/**
 * 扩面减损 - 催缴跟踪
 */
@RestController
@RequestMapping("/ygb/expansion/collection")
public class YgbExpansionCollectionController extends BaseController
{
    @Autowired
    private IYgbUninsuredListService uninsuredListService;

    @PreAuthorize("@ss.hasPermi('ygb:uninsuredList:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbUninsuredList uninsuredList)
    {
        startPage();
        List<YgbUninsuredList> list = uninsuredListService.selectUninsuredList(uninsuredList);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:uninsuredList:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbUninsuredList uninsuredList)
    {
        return success(uninsuredListService.selectUninsuredSummary(uninsuredList));
    }

    @Log(title = "催缴跟踪", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:uninsuredList:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbUninsuredList uninsuredList)
    {
        List<YgbUninsuredList> list = uninsuredListService.selectUninsuredList(uninsuredList);
        ExcelUtil<YgbUninsuredList> util = new ExcelUtil<>(YgbUninsuredList.class);
        util.exportExcel(response, list, "催缴跟踪");
    }

    @Log(title = "催缴跟踪", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:uninsuredList:handle')")
    @PostMapping("/handle/{listId}")
    public AjaxResult handle(@PathVariable Long listId, @RequestBody @Validated YgbUninsuredHandleRequest request)
    {
        return toAjax(uninsuredListService.handle(listId, request.getDisposalStatus(), request.getRemark(), getUsername()));
    }
}
