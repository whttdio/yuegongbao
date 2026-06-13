package com.yuegongbao.ygb.aqins.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.service.IYgbPreventionFundService;

@RestController
@RequestMapping("/ygb/preventionFund")
public class YgbPreventionFundController extends BaseController
{
    @Autowired
    private IYgbPreventionFundService preventionFundService;

    @PreAuthorize("@ss.hasPermi('ygb:preventionFund:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbPreventionFund preventionFund)
    {
        startPage();
        List<YgbPreventionFund> list = preventionFundService.selectPreventionFundList(preventionFund);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:preventionFund:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbPreventionFund preventionFund)
    {
        return success(preventionFundService.selectPreventionFundSummary(preventionFund));
    }

    @PreAuthorize("@ss.hasPermi('ygb:preventionFund:query')")
    @GetMapping("/{fundId}")
    public AjaxResult getInfo(@PathVariable Long fundId)
    {
        return success(preventionFundService.selectPreventionFundById(fundId));
    }

    @Log(title = "事故预防资金池", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:preventionFund:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbPreventionFund preventionFund)
    {
        List<YgbPreventionFund> list = preventionFundService.selectPreventionFundList(preventionFund);
        ExcelUtil<YgbPreventionFund> util = new ExcelUtil<>(YgbPreventionFund.class);
        util.exportExcel(response, list, "事故预防资金池");
    }

    @Log(title = "事故预防资金池", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:preventionFund:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbPreventionFund preventionFund)
    {
        return toAjax(preventionFundService.updatePreventionFund(preventionFund, getUsername()));
    }
}
