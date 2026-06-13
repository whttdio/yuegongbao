package com.yuegongbao.ygb.integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.ygb.aqins.service.IYgbAqInsuranceService;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;
import com.yuegongbao.ygb.domain.vo.YgbBankCallbackRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAiEventRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceHeartbeatRequest;
import com.yuegongbao.ygb.domain.vo.YgbMonthScopeRequest;
import com.yuegongbao.ygb.newform.service.IYgbNewformWorkerService;
import com.yuegongbao.ygb.occupation.service.IYgbOccupationMonitorService;
import com.yuegongbao.ygb.regulation.service.IYgbSocialPaymentService;
import com.yuegongbao.ygb.regulation.service.IYgbTaxCompareService;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;

@RestController
@RequestMapping("/ygb/stub")
public class YgbStubController extends BaseController
{
    @Autowired
    private IYgbSocialPaymentService socialPaymentService;

    @Autowired
    private IYgbTaxCompareService taxCompareService;

    @Autowired
    private IYgbDeviceService deviceService;

    @Autowired
    private IYgbSalaryBatchService salaryBatchService;

    @Autowired
    private IYgbAqInsuranceService aqInsuranceService;

    @Autowired
    private IYgbNewformWorkerService newformWorkerService;

    @Autowired
    private IYgbOccupationMonitorService occupationMonitorService;

    @PostMapping("/social/sync")
    public AjaxResult socialSync(@RequestBody YgbMonthScopeRequest request)
    {
        int rows = socialPaymentService.syncSocialPayment(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("Stub social sync completed, rows: " + rows);
    }

    @PostMapping("/tax/sync")
    public AjaxResult taxSync(@RequestBody YgbMonthScopeRequest request)
    {
        int rows = taxCompareService.syncTaxCompare(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("Stub tax sync completed, rows: " + rows);
    }

    @PostMapping("/aqins/sync")
    public AjaxResult aqInsuranceSync(@RequestBody YgbMonthScopeRequest request)
    {
        int rows = aqInsuranceService.syncAqInsurance(request.getStatMonth(), request.getEnterpriseId(), getUsername());
        return success("Stub aq insurance sync completed, rows: " + rows);
    }

    @PostMapping("/newform/sync")
    public AjaxResult newformSync(@RequestBody YgbMonthScopeRequest request)
    {
        int rows = newformWorkerService.syncNewformWorker(request.getStatMonth(), request.getEnterpriseId(),
            getUsername());
        return success("Stub newform sync completed, rows: " + rows);
    }

    @PostMapping("/occupation/sync")
    public AjaxResult occupationSync(@RequestBody YgbMonthScopeRequest request)
    {
        int rows = occupationMonitorService.syncOccupationMonitor(request.getStatMonth(), getUsername());
        return success("Stub occupation sync completed, rows: " + rows);
    }

    @PostMapping("/bank/callback")
    public AjaxResult bankCallback(@RequestBody YgbBankCallbackRequest request)
    {
        return AjaxResult.success("Stub bank callback accepted.",
            salaryBatchService.handleBankCallback(request, getUsername()));
    }

    @PostMapping("/device/heartbeat")
    public AjaxResult deviceHeartbeat(@RequestBody YgbDeviceHeartbeatRequest request)
    {
        return AjaxResult.success("Stub device heartbeat accepted.", deviceService.heartbeat(request, getUsername()));
    }

    @PostMapping("/device/ai-event")
    public AjaxResult deviceAiEvent(@RequestBody YgbDeviceAiEventRequest request)
    {
        return AjaxResult.success("Stub AI event accepted.", deviceService.aiEvent(request, getUsername()));
    }

    @PostMapping("/device/authorize")
    public AjaxResult deviceAuthorize(@RequestBody YgbDeviceAuthorizeRequest request)
    {
        return AjaxResult.success("Stub device authorize accepted.", deviceService.authorize(request, getUsername()));
    }
}
