package com.yuegongbao.ygb.warning.controller;

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
import com.yuegongbao.ygb.warning.domain.YgbWarningRule;
import com.yuegongbao.ygb.warning.service.IYgbWarningRuleService;

@RestController
@RequestMapping("/ygb/warning/rule")
public class YgbWarningRuleController extends BaseController
{
    @Autowired
    private IYgbWarningRuleService warningRuleService;

    @PreAuthorize("@ss.hasPermi('ygb:warningRule:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbWarningRule warningRule)
    {
        startPage();
        List<YgbWarningRule> list = warningRuleService.selectWarningRuleList(warningRule);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:warningRule:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbWarningRule warningRule)
    {
        return success(warningRuleService.selectWarningRuleSummary(warningRule));
    }

    @PreAuthorize("@ss.hasPermi('ygb:warningRule:query')")
    @GetMapping("/{ruleId}")
    public AjaxResult getInfo(@PathVariable Long ruleId)
    {
        return success(warningRuleService.selectWarningRuleById(ruleId));
    }

    @Log(title = "预警规则", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:warningRule:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbWarningRule warningRule)
    {
        List<YgbWarningRule> list = warningRuleService.selectWarningRuleList(warningRule);
        ExcelUtil<YgbWarningRule> util = new ExcelUtil<>(YgbWarningRule.class);
        util.exportExcel(response, list, "预警规则");
    }

    @Log(title = "预警规则", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:warningRule:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbWarningRule warningRule)
    {
        warningRule.setCreateBy(getUsername());
        return toAjax(warningRuleService.insertWarningRule(warningRule));
    }

    @Log(title = "预警规则", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:warningRule:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbWarningRule warningRule)
    {
        warningRule.setUpdateBy(getUsername());
        return toAjax(warningRuleService.updateWarningRule(warningRule));
    }

    @Log(title = "预警规则", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:warningRule:remove')")
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(warningRuleService.deleteWarningRuleByIds(ruleIds, getUsername()));
    }
}
