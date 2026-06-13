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
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;

@RestController
@RequestMapping("/ygb/salary/batch")
public class YgbSalaryBatchController extends BaseController
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

    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return success(salaryBatchService.selectSalaryBatchOptions());
    }

    @Log(title = "工资批次", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbSalaryBatch salaryBatch)
    {
        List<YgbSalaryBatch> list = salaryBatchService.selectSalaryBatchList(salaryBatch);
        ExcelUtil<YgbSalaryBatch> util = new ExcelUtil<>(YgbSalaryBatch.class);
        util.exportExcel(response, list, "工资批次");
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:query')")
    @GetMapping("/{batchId}")
    public AjaxResult getInfo(@PathVariable Long batchId)
    {
        return success(salaryBatchService.selectSalaryBatchById(batchId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:add')")
    @Log(title = "工资批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbSalaryBatch salaryBatch)
    {
        if (!salaryBatchService.checkBatchNoUnique(salaryBatch))
        {
            return error("批次编号已存在: " + salaryBatch.getBatchNo());
        }
        salaryBatch.setCreateBy(getUsername());
        return toAjax(salaryBatchService.insertSalaryBatch(salaryBatch));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:edit')")
    @Log(title = "工资批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbSalaryBatch salaryBatch)
    {
        if (!salaryBatchService.checkBatchNoUnique(salaryBatch))
        {
            return error("批次编号已存在: " + salaryBatch.getBatchNo());
        }
        salaryBatch.setUpdateBy(getUsername());
        return toAjax(salaryBatchService.updateSalaryBatch(salaryBatch));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:remove')")
    @Log(title = "工资批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds)
    {
        return toAjax(salaryBatchService.deleteSalaryBatchByIds(batchIds, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:generate')")
    @Log(title = "工资批次", businessType = BusinessType.OTHER)
    @PostMapping("/generate/{batchId}")
    public AjaxResult generate(@PathVariable Long batchId)
    {
        int rows = salaryBatchService.generateSalaryDetail(batchId, getUsername());
        return success("工资明细生成完成，本次生成 " + rows + " 条记录。");
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:account')")
    @Log(title = "工资到账确认", businessType = BusinessType.OTHER)
    @PostMapping("/account/{batchId}")
    public AjaxResult confirmAccount(@PathVariable Long batchId, @RequestBody YgbSalaryBatch salaryBatch)
    {
        return toAjax(salaryBatchService.confirmAccount(batchId, salaryBatch.getAccountReceivedAmount(),
            salaryBatch.getBankSerialNo(), getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('ygb:salaryBatch:submit')")
    @Log(title = "工资发放提交", businessType = BusinessType.OTHER)
    @PostMapping("/submit/{batchId}")
    public AjaxResult submit(@PathVariable Long batchId)
    {
        int rows = salaryBatchService.submitBatch(batchId, getUsername());
        return success("已提交银行代发，本次推送 " + rows + " 条工资明细，待银行回调确认。");
    }
}
