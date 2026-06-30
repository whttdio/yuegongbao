package com.yuegongbao.ygb.compliance.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;

/**
 * 工资监管 - 银行代发结果监控
 */
@RestController
@RequestMapping("/ygb/salary/bank")
public class YgbSalaryBankController extends BaseController
{
    @Autowired
    private IYgbSalaryBatchService salaryBatchService;

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbSalaryBatch salaryBatch)
    {
        startPage();
        List<YgbSalaryBatch> list = salaryBatchService.selectSalaryBatchList(salaryBatch);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbSalaryBatch salaryBatch)
    {
        return success(salaryBatchService.selectSalaryBatchSummary(salaryBatch));
    }

    @Log(title = "银行代发结果监控", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbSalaryBatch salaryBatch)
    {
        List<YgbSalaryBatch> list = salaryBatchService.selectSalaryBatchList(salaryBatch);
        ExcelUtil<YgbSalaryBatch> util = new ExcelUtil<>(YgbSalaryBatch.class);
        util.exportExcel(response, list, "银行代发结果监控");
    }
}
