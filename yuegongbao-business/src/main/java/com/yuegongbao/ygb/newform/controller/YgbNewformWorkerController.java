package com.yuegongbao.ygb.newform.controller;

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
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.service.IYgbNewformWorkerService;

@RestController
@RequestMapping("/ygb/newform/worker")
public class YgbNewformWorkerController extends BaseController
{
    @Autowired
    private IYgbNewformWorkerService newformWorkerService;

    @PreAuthorize("@ss.hasPermi('ygb:newformWorker:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbNewformWorker query)
    {
        startPage();
        List<YgbNewformWorker> list = newformWorkerService.selectNewformWorkerList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:newformWorker:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbNewformWorker query)
    {
        return success(newformWorkerService.selectNewformWorkerSummary(query));
    }

    @Log(title = "新业态人员库", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:newformWorker:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbNewformWorker query)
    {
        List<YgbNewformWorker> list = newformWorkerService.selectNewformWorkerList(query);
        ExcelUtil<YgbNewformWorker> util = new ExcelUtil<>(YgbNewformWorker.class);
        util.exportExcel(response, list, "新业态人员库");
    }

    @Log(title = "新业态人员库", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:newformWorker:sync')")
    @PostMapping("/sync")
    public AjaxResult sync(@Validated @RequestBody YgbMonthScopeRequest request)
    {
        int rows = newformWorkerService.syncNewformWorker(request.getStatMonth(), request.getEnterpriseId(),
            getUsername());
        return success("新业态平台模拟同步完成，本次处理 " + rows + " 条记录。");
    }
}
