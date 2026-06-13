package com.yuegongbao.ygb.compliance.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryDetailService;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;

@RestController
@RequestMapping("/ygb/salary/detail")
public class YgbSalaryDetailController extends BaseController
{
    @Autowired
    private IYgbSalaryDetailService salaryDetailService;

    @PreAuthorize("@ss.hasPermi('ygb:salaryDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbSalaryDetail salaryDetail)
    {
        startPage();
        List<YgbSalaryDetail> list = salaryDetailService.selectSalaryDetailList(salaryDetail);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryDetail:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbSalaryDetail salaryDetail)
    {
        return success(salaryDetailService.selectSalaryDetailSummary(salaryDetail));
    }

    @Log(title = "工资明细", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:salaryDetail:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbSalaryDetail salaryDetail)
    {
        List<YgbSalaryDetail> list = salaryDetailService.selectSalaryDetailList(salaryDetail);
        ExcelUtil<YgbSalaryDetail> util = new ExcelUtil<>(YgbSalaryDetail.class);
        util.exportExcel(response, list, "工资明细");
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryDetail:query')")
    @GetMapping("/{detailId}")
    public AjaxResult getInfo(@PathVariable Long detailId)
    {
        return success(salaryDetailService.selectSalaryDetailById(detailId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryDetail:edit')")
    @Log(title = "工资明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbSalaryDetail salaryDetail)
    {
        salaryDetail.setUpdateBy(getUsername());
        return toAjax(salaryDetailService.updateSalaryDetail(salaryDetail));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryDetail:remove')")
    @Log(title = "工资明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{detailIds}")
    public AjaxResult remove(@PathVariable Long[] detailIds)
    {
        return toAjax(salaryDetailService.deleteSalaryDetailByIds(detailIds, getUsername()));
    }
}
