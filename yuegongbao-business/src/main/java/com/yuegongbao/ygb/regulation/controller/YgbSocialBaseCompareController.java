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
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;
import com.yuegongbao.ygb.domain.vo.YgbMonthScopeRequest;
import com.yuegongbao.ygb.regulation.service.IYgbSocialBaseCompareService;

@RestController
@RequestMapping("/ygb/social/compare")
public class YgbSocialBaseCompareController extends BaseController
{
    @Autowired
    private IYgbSocialBaseCompareService socialBaseCompareService;

    @PreAuthorize("@ss.hasPermi('ygb:socialBaseCompare:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbSocialBaseCompare socialBaseCompare)
    {
        startPage();
        List<YgbSocialBaseCompare> list = socialBaseCompareService.selectSocialBaseCompareList(socialBaseCompare);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:socialBaseCompare:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbSocialBaseCompare socialBaseCompare)
    {
        return success(socialBaseCompareService.selectSocialBaseCompareSummary(socialBaseCompare));
    }

    @Log(title = "社保基数比对", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:socialBaseCompare:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbSocialBaseCompare socialBaseCompare)
    {
        List<YgbSocialBaseCompare> list = socialBaseCompareService.selectSocialBaseCompareList(socialBaseCompare);
        ExcelUtil<YgbSocialBaseCompare> util = new ExcelUtil<>(YgbSocialBaseCompare.class);
        util.exportExcel(response, list, "社保基数比对");
    }

    @Log(title = "社保基数比对", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:socialBaseCompare:compare')")
    @PostMapping("/execute")
    public AjaxResult compare(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = socialBaseCompareService.compare(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("社保基数比对完成，本次处理 " + rows + " 条记录。");
    }
}
