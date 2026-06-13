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
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawOverview;
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceRawService;

@RestController
@RequestMapping("/ygb/attendance/raw")
public class YgbAttendanceRawController extends BaseController
{
    @Autowired
    private IYgbAttendanceRawService attendanceRawService;

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbAttendanceRaw attendanceRaw)
    {
        startPage();
        List<YgbAttendanceRaw> list = attendanceRawService.selectAttendanceRawList(attendanceRaw);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbAttendanceRaw attendanceRaw)
    {
        return success(attendanceRawService.selectAttendanceRawSummary(attendanceRaw));
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:list')")
    @GetMapping("/overview")
    public AjaxResult overview(YgbAttendanceRaw attendanceRaw)
    {
        YgbAttendanceRawOverview overview = attendanceRawService.selectAttendanceRawOverview(attendanceRaw);
        return success(overview);
    }

    @Log(title = "考勤上报", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbAttendanceRaw attendanceRaw)
    {
        List<YgbAttendanceRaw> list = attendanceRawService.selectAttendanceRawList(attendanceRaw);
        ExcelUtil<YgbAttendanceRaw> util = new ExcelUtil<>(YgbAttendanceRaw.class);
        util.exportExcel(response, list, "考勤上报");
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:query')")
    @GetMapping("/{attendanceId}")
    public AjaxResult getInfo(@PathVariable Long attendanceId)
    {
        return success(attendanceRawService.selectAttendanceRawById(attendanceId));
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:add')")
    @Log(title = "考勤上报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbAttendanceRaw attendanceRaw)
    {
        if (!attendanceRawService.checkAttendanceNoUnique(attendanceRaw))
        {
            return error("考勤编号已存在 " + attendanceRaw.getAttendanceNo());
        }
        attendanceRaw.setCreateBy(getUsername());
        return toAjax(attendanceRawService.insertAttendanceRaw(attendanceRaw));
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:edit')")
    @Log(title = "考勤上报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbAttendanceRaw attendanceRaw)
    {
        if (!attendanceRawService.checkAttendanceNoUnique(attendanceRaw))
        {
            return error("考勤编号已存在 " + attendanceRaw.getAttendanceNo());
        }
        attendanceRaw.setUpdateBy(getUsername());
        return toAjax(attendanceRawService.updateAttendanceRaw(attendanceRaw));
    }

    @PreAuthorize("@ss.hasPermi('ygb:attendanceRaw:remove')")
    @Log(title = "考勤上报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{attendanceIds}")
    public AjaxResult remove(@PathVariable Long[] attendanceIds)
    {
        return toAjax(attendanceRawService.deleteAttendanceRawByIds(attendanceIds, getUsername()));
    }
}
