package com.yuegongbao.ygb.regulation.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.domain.vo.YgbMonthScopeRequest;
import com.yuegongbao.ygb.regulation.service.IYgbTaxCompareService;

@RestController
@RequestMapping("/ygb/tax/compare")
public class YgbTaxCompareController extends BaseController
{
    @Autowired
    private IYgbTaxCompareService taxCompareService;

    @PreAuthorize("@ss.hasPermi('ygb:taxCompare:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbTaxCompare taxCompare)
    {
        startPage();
        List<YgbTaxCompare> list = taxCompareService.selectTaxCompareList(taxCompare);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:taxCompare:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbTaxCompare taxCompare)
    {
        return success(taxCompareService.selectTaxCompareSummary(taxCompare));
    }

    @Log(title = "税务比对", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:taxCompare:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbTaxCompare taxCompare)
    {
        List<YgbTaxCompare> list = taxCompareService.selectTaxCompareList(taxCompare);
        ExcelUtil<YgbTaxCompare> util = new ExcelUtil<>(YgbTaxCompare.class);
        util.exportExcel(response, list, "税务比对");
    }

    @Log(title = "税务比对", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:taxCompare:sync')")
    @PostMapping("/sync")
    public AjaxResult sync(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = taxCompareService.syncTaxCompare(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("税务数据同步完成，本次处理 " + rows + " 条记录。");
    }

    @Log(title = "税务比对", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:taxCompare:compare')")
    @PostMapping("/execute")
    public AjaxResult compare(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = taxCompareService.compare(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("个税比对完成，本次处理 " + rows + " 条记录。");
    }
}
