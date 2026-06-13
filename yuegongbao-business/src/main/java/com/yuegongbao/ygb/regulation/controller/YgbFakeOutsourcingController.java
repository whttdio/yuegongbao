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
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord;
import com.yuegongbao.ygb.domain.vo.YgbFakeOutsourcingAnalyzeRequest;
import com.yuegongbao.ygb.regulation.service.IYgbFakeOutsourcingService;

@RestController
@RequestMapping("/ygb/special/fakeOutsourcing")
public class YgbFakeOutsourcingController extends BaseController
{
    @Autowired
    private IYgbFakeOutsourcingService fakeOutsourcingService;

    @PreAuthorize("@ss.hasPermi('ygb:fakeOutsourcing:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbFakeOutsourcingRecord fakeOutsourcingRecord)
    {
        startPage();
        List<YgbFakeOutsourcingRecord> list = fakeOutsourcingService.selectFakeOutsourcingList(fakeOutsourcingRecord);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:fakeOutsourcing:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbFakeOutsourcingRecord fakeOutsourcingRecord)
    {
        return success(fakeOutsourcingService.selectFakeOutsourcingSummary(fakeOutsourcingRecord));
    }

    @Log(title = "假外包分析", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:fakeOutsourcing:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbFakeOutsourcingRecord fakeOutsourcingRecord)
    {
        List<YgbFakeOutsourcingRecord> list = fakeOutsourcingService.selectFakeOutsourcingList(fakeOutsourcingRecord);
        ExcelUtil<YgbFakeOutsourcingRecord> util = new ExcelUtil<>(YgbFakeOutsourcingRecord.class);
        util.exportExcel(response, list, "假外包分析");
    }

    @Log(title = "假外包分析", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:fakeOutsourcing:analyze')")
    @PostMapping("/analyze")
    public AjaxResult analyze(@Validated @RequestBody YgbFakeOutsourcingAnalyzeRequest request)
    {
        Long recordId = fakeOutsourcingService.analyze(request, getUsername());
        return AjaxResult.success("假外包分析完成。", recordId);
    }
}
