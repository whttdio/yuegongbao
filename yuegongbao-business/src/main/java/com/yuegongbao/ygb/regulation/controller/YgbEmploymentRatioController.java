package com.yuegongbao.ygb.regulation.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;
import com.yuegongbao.ygb.domain.vo.YgbEmploymentRatioCalcRequest;
import com.yuegongbao.ygb.regulation.service.IYgbEmploymentRatioService;

@RestController
@RequestMapping("/ygb/special/employmentRatio")
public class YgbEmploymentRatioController extends BaseController
{
    @Autowired
    private IYgbEmploymentRatioService employmentRatioService;

    @PreAuthorize("@ss.hasPermi('ygb:employmentRatio:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbEmploymentRatio employmentRatio)
    {
        startPage();
        List<YgbEmploymentRatio> list = employmentRatioService.selectEmploymentRatioList(employmentRatio);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:employmentRatio:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbEmploymentRatio employmentRatio)
    {
        return success(employmentRatioService.selectEmploymentRatioSummary(employmentRatio));
    }

    @Log(title = "用工比例监控", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:employmentRatio:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbEmploymentRatio employmentRatio)
    {
        List<YgbEmploymentRatio> list = employmentRatioService.selectEmploymentRatioList(employmentRatio);
        ExcelUtil<YgbEmploymentRatio> util = new ExcelUtil<>(YgbEmploymentRatio.class);
        util.exportExcel(response, list, "用工比例监控");
    }

    @Log(title = "用工比例监控", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:employmentRatio:calculate')")
    @PostMapping("/calculate")
    public AjaxResult calculate(@Validated @RequestBody YgbEmploymentRatioCalcRequest request)
    {
        int rows = employmentRatioService.calculate(request.getStatMonth(), request.getEmployerEnterpriseId(),
            getUsername());
        return success("用工比例计算完成，本次生成 " + rows + " 条记录。");
    }
}
