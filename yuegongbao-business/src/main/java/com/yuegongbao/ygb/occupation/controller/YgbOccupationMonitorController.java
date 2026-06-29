package com.yuegongbao.ygb.occupation.controller;

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
import com.yuegongbao.ygb.domain.vo.YgbMonthScopeRequest;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;
import com.yuegongbao.ygb.occupation.service.IYgbOccupationMonitorService;

@RestController
@RequestMapping("/ygb/occupation/monitor")
public class YgbOccupationMonitorController extends BaseController
{
    @Autowired
    private IYgbOccupationMonitorService occupationMonitorService;

    @PreAuthorize("@ss.hasPermi('ygb:occupationMonitor:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbOccupationMonitor query)
    {
        startPage();
        List<YgbOccupationMonitor> list = occupationMonitorService.selectOccupationMonitorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:occupationMonitor:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbOccupationMonitor query)
    {
        return success(occupationMonitorService.selectOccupationMonitorSummary(query));
    }

    @Log(title = "职业病监测", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:occupationMonitor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbOccupationMonitor query)
    {
        List<YgbOccupationMonitor> list = occupationMonitorService.selectOccupationMonitorList(query);
        ExcelUtil<YgbOccupationMonitor> util = new ExcelUtil<>(YgbOccupationMonitor.class);
        util.exportExcel(response, list, "职业病监测");
    }

    @Log(title = "职业病监测", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:occupationMonitor:sync')")
    @PostMapping("/sync")
    public AjaxResult sync(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = occupationMonitorService.syncOccupationMonitor(request.getStatMonth(), getUsername());
        return success("职业病监测数据同步完成，本次处理 " + rows + " 条记录。");
    }
}
