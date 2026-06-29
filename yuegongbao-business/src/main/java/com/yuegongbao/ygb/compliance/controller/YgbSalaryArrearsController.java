package com.yuegongbao.ygb.compliance.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.yuegongbao.ygb.compliance.domain.YgbSalaryArrears;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryArrearsService;

@RestController
@RequestMapping("/ygb/salary/batch/arrears")
public class YgbSalaryArrearsController extends BaseController
{
    @Autowired
    private IYgbSalaryArrearsService salaryArrearsService;

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatchArrears:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbSalaryArrears arrears)
    {
        startPage();
        List<YgbSalaryArrears> list = salaryArrearsService.selectSalaryArrearsList(arrears);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatchArrears:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbSalaryArrears arrears)
    {
        return success(salaryArrearsService.selectSalaryArrearsSummary(arrears));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatchArrears:query')")
    @GetMapping("/{batchId}")
    public AjaxResult getInfo(@PathVariable Long batchId)
    {
        return success(salaryArrearsService.selectSalaryArrearsByBatchId(batchId));
    }

    @Log(title = "工资拖欠预警", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:salaryBatchArrears:handle')")
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody YgbSalaryArrears arrears)
    {
        return toAjax(salaryArrearsService.handleSalaryArrears(arrears, getUsername()));
    }

    @Log(title = "工资拖欠预警", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:salaryBatchArrears:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbSalaryArrears arrears)
    {
        List<YgbSalaryArrears> list = salaryArrearsService.selectSalaryArrearsList(arrears);
        ExcelUtil<YgbSalaryArrears> util = new ExcelUtil<>(YgbSalaryArrears.class);
        util.exportExcel(response, list, "salary_arrears");
    }
}
