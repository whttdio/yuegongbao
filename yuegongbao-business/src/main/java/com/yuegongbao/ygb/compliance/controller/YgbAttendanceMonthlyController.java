package com.yuegongbao.ygb.compliance.controller;

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
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceMonthlyService;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly;

@RestController
@RequestMapping("/ygb/attendance/monthly")
public class YgbAttendanceMonthlyController extends BaseController
{
    @Autowired
    private IYgbAttendanceMonthlyService attendanceMonthlyService;

    @PreAuthorize("@ss.hasPermi('ygb:attendanceMonthly:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAttendanceMonthly attendanceMonthly)
    {
        startPage();
        List<YgbAttendanceMonthly> list = attendanceMonthlyService.selectAttendanceMonthlyList(attendanceMonthly);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceMonthly:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbAttendanceMonthly attendanceMonthly)
    {
        return success(attendanceMonthlyService.selectAttendanceMonthlySummary(attendanceMonthly));
    }

    @Log(title = "考勤归集", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:attendanceMonthly:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAttendanceMonthly attendanceMonthly)
    {
        List<YgbAttendanceMonthly> list = attendanceMonthlyService.selectAttendanceMonthlyList(attendanceMonthly);
        ExcelUtil<YgbAttendanceMonthly> util = new ExcelUtil<>(YgbAttendanceMonthly.class);
        util.exportExcel(response, list, "考勤归集");
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceMonthly:aggregate')")
    @Log(title = "考勤归集", businessType = BusinessType.OTHER)
    @PostMapping("/aggregate")
    public AjaxResult aggregate(@Validated @RequestBody YgbAttendanceMonthly attendanceMonthly)
    {
        int rows = attendanceMonthlyService.aggregateMonthly(attendanceMonthly.getStatMonth(),
            attendanceMonthly.getDispatchEnterpriseId(), getUsername());
        return success("考勤归集完成，本次生成 " + rows + " 条月度记录。");
    }
}
