package com.yuegongbao.ygb.safety.controller;

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
import com.yuegongbao.ygb.safety.domain.YgbPreventionProject;
import com.yuegongbao.ygb.safety.service.IYgbPreventionProjectService;

@RestController
@RequestMapping("/ygb/injury/prevention")
public class YgbPreventionProjectController extends BaseController
{
    @Autowired
    private IYgbPreventionProjectService preventionProjectService;

    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbPreventionProject preventionProject)
    {
        startPage();
        List<YgbPreventionProject> list = preventionProjectService.selectPreventionProjectList(preventionProject);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbPreventionProject preventionProject)
    {
        return success(preventionProjectService.selectPreventionProjectSummary(preventionProject));
    }

    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:query')")
    @GetMapping("/{projectId}")
    public AjaxResult getInfo(@PathVariable Long projectId)
    {
        return success(preventionProjectService.selectPreventionProjectById(projectId));
    }

    @Log(title = "预防项目", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbPreventionProject preventionProject)
    {
        List<YgbPreventionProject> list = preventionProjectService.selectPreventionProjectList(preventionProject);
        ExcelUtil<YgbPreventionProject> util = new ExcelUtil<>(YgbPreventionProject.class);
        util.exportExcel(response, list, "预防项目");
    }

    @Log(title = "预防项目", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbPreventionProject preventionProject)
    {
        preventionProject.setCreateBy(getUsername());
        return toAjax(preventionProjectService.insertPreventionProject(preventionProject));
    }

    @Log(title = "预防项目", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbPreventionProject preventionProject)
    {
        preventionProject.setUpdateBy(getUsername());
        return toAjax(preventionProjectService.updatePreventionProject(preventionProject));
    }

    @Log(title = "预防项目", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:preventionProject:remove')")
    @DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds)
    {
        return toAjax(preventionProjectService.deletePreventionProjectByIds(projectIds, getUsername()));
    }
}
