package com.yuegongbao.ygb.aireport.controller;

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
import com.yuegongbao.ygb.aireport.domain.YgbAiReportSubscription;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportSubscriptionService;

@RestController
@RequestMapping("/ygb/aiReport/subscription")
public class YgbAiReportSubscriptionController extends BaseController
{
    @Autowired
    private IYgbAiReportSubscriptionService aiReportSubscriptionService;

    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAiReportSubscription subscription)
    {
        startPage();
        List<YgbAiReportSubscription> list = aiReportSubscriptionService.selectAiReportSubscriptionList(subscription);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbAiReportSubscription subscription)
    {
        return success(aiReportSubscriptionService.selectAiReportSubscriptionSummary(subscription));
    }

    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:query')")
    @GetMapping("/{subscriptionId}")
    public AjaxResult getInfo(@PathVariable Long subscriptionId)
    {
        return success(aiReportSubscriptionService.selectAiReportSubscriptionById(subscriptionId));
    }

    @Log(title = "AI报告订阅", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAiReportSubscription subscription)
    {
        List<YgbAiReportSubscription> list = aiReportSubscriptionService.selectAiReportSubscriptionList(subscription);
        ExcelUtil<YgbAiReportSubscription> util = new ExcelUtil<>(YgbAiReportSubscription.class);
        util.exportExcel(response, list, "ai_report_subscription");
    }

    @Log(title = "AI报告订阅", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:add')")
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody YgbAiReportSubscription subscription)
    {
        return toAjax(aiReportSubscriptionService.insertAiReportSubscription(subscription, getUsername()));
    }

    @Log(title = "AI报告订阅", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody YgbAiReportSubscription subscription)
    {
        return toAjax(aiReportSubscriptionService.updateAiReportSubscription(subscription, getUsername()));
    }

    @Log(title = "AI报告订阅", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:aiReportSubscription:remove')")
    @DeleteMapping("/remove/{subscriptionIds}")
    public AjaxResult remove(@PathVariable Long[] subscriptionIds)
    {
        return toAjax(aiReportSubscriptionService.deleteAiReportSubscriptionByIds(subscriptionIds, getUsername()));
    }
}
