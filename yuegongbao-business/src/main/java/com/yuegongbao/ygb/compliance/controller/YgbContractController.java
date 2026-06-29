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
import com.yuegongbao.ygb.compliance.domain.ContractBatchActionRequest;
import com.yuegongbao.ygb.compliance.service.IYgbContractService;
import com.yuegongbao.ygb.compliance.domain.YgbContract;

@RestController
@RequestMapping("/ygb/contract")
public class YgbContractController extends BaseController
{
    @Autowired
    private IYgbContractService contractService;

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:list,azb:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbContract contract)
    {
        startPage();
        List<YgbContract> list = contractService.selectContractList(contract);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:list,azb:contract:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbContract contract)
    {
        return success(contractService.selectContractSummary(contract));
    }

    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return success(contractService.selectContractOptions());
    }

    @Log(title = "合同备案", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:export,azb:contract:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbContract contract)
    {
        List<YgbContract> list = contractService.selectContractList(contract);
        ExcelUtil<YgbContract> util = new ExcelUtil<>(YgbContract.class);
        util.exportExcel(response, list, "合同备案");
    }

    @PreAuthorize("@ss.hasPermi('ygb:contract:add')")
    @Log(title = "合同备案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbContract contract)
    {
        if (!contractService.checkContractNoUnique(contract))
        {
            return error("合同编号已存在: " + contract.getContractNo());
        }
        contract.setCreateBy(getUsername());
        return toAjax(contractService.insertContract(contract));
    }

    @PreAuthorize("@ss.hasPermi('ygb:contract:edit')")
    @Log(title = "合同备案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbContract contract)
    {
        if (!contractService.checkContractNoUnique(contract))
        {
            return error("合同编号已存在: " + contract.getContractNo());
        }
        contract.setUpdateBy(getUsername());
        return toAjax(contractService.updateContract(contract));
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:list,azb:contract:list')")
    @GetMapping("/expiry/list")
    public TableDataInfo expiryList(YgbContract contract)
    {
        startPage();
        List<YgbContract> list = contractService.selectContractExpiryList(contract);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:list,azb:contract:list')")
    @GetMapping("/expiry/summary")
    public AjaxResult expirySummary(YgbContract contract)
    {
        return success(contractService.selectContractExpirySummary(contract));
    }

    @Log(title = "合同到期提醒", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:remind,azb:contract:remind')")
    @PostMapping("/expiry/remind")
    public AjaxResult remindExpiry(@RequestBody ContractBatchActionRequest request)
    {
        return success(contractService.batchRemindExpiry(request, getUsername()));
    }

    @Log(title = "合同到期提醒", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:export,azb:contract:export')")
    @PostMapping("/expiry/export")
    public void expiryExport(HttpServletResponse response, YgbContract contract)
    {
        List<YgbContract> list = contractService.selectContractExpiryList(contract);
        ExcelUtil<YgbContract> util = new ExcelUtil<>(YgbContract.class);
        util.exportExcel(response, list, "合同到期提醒");
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:list,azb:contract:list')")
    @GetMapping("/unfiled/list")
    public TableDataInfo unfiledList(YgbContract contract)
    {
        startPage();
        List<YgbContract> list = contractService.selectContractUnfiledList(contract);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:list,azb:contract:list')")
    @GetMapping("/unfiled/summary")
    public AjaxResult unfiledSummary(YgbContract contract)
    {
        return success(contractService.selectContractUnfiledSummary(contract));
    }

    @Log(title = "未备案合同预警", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:warn,azb:contract:warn')")
    @PostMapping("/unfiled/warn")
    public AjaxResult warnUnfiled(@RequestBody ContractBatchActionRequest request)
    {
        return success(contractService.batchWarnUnfiled(request, getUsername()));
    }

    @Log(title = "未备案合同预警", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:export,azb:contract:export')")
    @PostMapping("/unfiled/export")
    public void unfiledExport(HttpServletResponse response, YgbContract contract)
    {
        List<YgbContract> list = contractService.selectContractUnfiledList(contract);
        ExcelUtil<YgbContract> util = new ExcelUtil<>(YgbContract.class);
        util.exportExcel(response, list, "未备案合同预警");
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contract:query,azb:contract:query')")
    @GetMapping("/{contractId:\\d+}")
    public AjaxResult getInfo(@PathVariable Long contractId)
    {
        return success(contractService.selectContractById(contractId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:contract:remove')")
    @Log(title = "合同备案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contractIds:[0-9,]+}")
    public AjaxResult remove(@PathVariable Long[] contractIds)
    {
        return toAjax(contractService.deleteContractByIds(contractIds, getUsername()));
    }
}
