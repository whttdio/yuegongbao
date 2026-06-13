package com.yuegongbao.ygb.regulation.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.domain.vo.YgbMonthScopeRequest;
import com.yuegongbao.ygb.regulation.service.IYgbSocialPaymentService;

@RestController
@RequestMapping("/ygb/social/payment")
public class YgbSocialPaymentController extends BaseController
{
    @Autowired
    private IYgbSocialPaymentService socialPaymentService;

    @PreAuthorize("@ss.hasPermi('ygb:socialPayment:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbSocialPayment socialPayment)
    {
        startPage();
        List<YgbSocialPayment> list = socialPaymentService.selectSocialPaymentList(socialPayment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:socialPayment:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbSocialPayment socialPayment)
    {
        return success(socialPaymentService.selectSocialPaymentSummary(socialPayment));
    }

    @PreAuthorize("@ss.hasPermi('ygb:socialPayment:query')")
    @GetMapping("/{paymentId}")
    public AjaxResult getInfo(@PathVariable Long paymentId)
    {
        return success(socialPaymentService.selectSocialPaymentById(paymentId));
    }

    @Log(title = "社保缴费监控", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:socialPayment:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbSocialPayment socialPayment)
    {
        List<YgbSocialPayment> list = socialPaymentService.selectSocialPaymentList(socialPayment);
        ExcelUtil<YgbSocialPayment> util = new ExcelUtil<>(YgbSocialPayment.class);
        util.exportExcel(response, list, "社保缴费监控");
    }

    @Log(title = "社保缴费监控", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:socialPayment:sync')")
    @PostMapping("/sync")
    public AjaxResult sync(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = socialPaymentService.syncSocialPayment(request.getStatMonth(), request.getEnterpriseId(),
            getUsername());
        return success("模拟同步完成，本次写入 " + rows + " 条社保缴费记录。");
    }
}
