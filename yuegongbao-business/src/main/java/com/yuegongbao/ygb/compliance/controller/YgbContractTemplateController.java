package com.yuegongbao.ygb.compliance.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.compliance.domain.ContractTemplateReviewRequest;
import com.yuegongbao.ygb.compliance.domain.YgbContractTemplate;
import com.yuegongbao.ygb.compliance.service.IYgbContractTemplateService;

@RestController
@RequestMapping("/ygb/contract/template")
public class YgbContractTemplateController extends BaseController
{
    @Autowired
    private IYgbContractTemplateService contractTemplateService;

    @PreAuthorize("@ss.hasAnyPermi('ygb:contractTemplate:list,azb:contractTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbContractTemplate template)
    {
        startPage();
        List<YgbContractTemplate> list = contractTemplateService.selectContractTemplateList(template);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyPermi('ygb:contractTemplate:query,azb:contractTemplate:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable Long templateId)
    {
        return success(contractTemplateService.selectContractTemplateById(templateId));
    }

    @GetMapping("/optionselect")
    public AjaxResult optionselect(@RequestParam(required = false) String templateType,
        @RequestParam(required = false) String regionCode)
    {
        return success(contractTemplateService.selectEnabledTemplateOptions(templateType, regionCode));
    }

    @Log(title = "合同模板库", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contractTemplate:export,azb:contractTemplate:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbContractTemplate template)
    {
        List<YgbContractTemplate> list = contractTemplateService.selectContractTemplateList(template);
        ExcelUtil<YgbContractTemplate> util = new ExcelUtil<>(YgbContractTemplate.class);
        util.exportExcel(response, list, "contract_template");
    }

    @Log(title = "合同模板库", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:contractTemplate:add')")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody YgbContractTemplate template)
    {
        if (!contractTemplateService.checkTemplateCodeUnique(template))
        {
            return error("模板编码已存在: " + template.getTemplateCode());
        }
        template.setCreateBy(getUsername());
        return toAjax(contractTemplateService.insertContractTemplate(template));
    }

    @Log(title = "合同模板库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:contractTemplate:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody YgbContractTemplate template)
    {
        if (!contractTemplateService.checkTemplateCodeUnique(template))
        {
            return error("模板编码已存在: " + template.getTemplateCode());
        }
        template.setUpdateBy(getUsername());
        return toAjax(contractTemplateService.updateContractTemplate(template));
    }

    @Log(title = "合同模板库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:contractTemplate:status')")
    @PutMapping("/status/{templateId}")
    public AjaxResult changeStatus(@PathVariable Long templateId, @RequestBody YgbContractTemplate template)
    {
        return toAjax(contractTemplateService.updateTemplateStatus(templateId, template.getStatus(), getUsername()));
    }

    @Log(title = "合同模板库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:contractTemplate:submit')")
    @PostMapping("/{templateId}/submit")
    public AjaxResult submit(@PathVariable Long templateId, @RequestBody(required = false) ContractTemplateReviewRequest request)
    {
        String portalScope = request == null ? "ygb" : request.getPortalScope();
        return toAjax(contractTemplateService.submitTemplateReview(templateId, getUsername(), portalScope));
    }

    @Log(title = "合同模板库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasAnyPermi('ygb:contractTemplate:review,azb:contractTemplate:review')")
    @PostMapping("/{templateId}/review")
    public AjaxResult review(@PathVariable Long templateId, @RequestBody ContractTemplateReviewRequest request)
    {
        return toAjax(contractTemplateService.reviewTemplate(templateId, request, getUsername()));
    }

    @Log(title = "合同模板库", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:contractTemplate:remove')")
    @DeleteMapping("/remove/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(contractTemplateService.deleteContractTemplateByIds(templateIds, getUsername()));
    }
}
