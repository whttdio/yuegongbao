package com.yuegongbao.ygb.credit.controller;

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
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreGenerateRequest;
import com.yuegongbao.ygb.credit.service.IYgbCreditScoreService;

/**
 * 企业信用评分Controller。
 *
 * @author yuegongbao
 */
@RestController
@RequestMapping("/ygb/credit/score")
public class YgbCreditScoreController extends BaseController
{
    @Autowired
    private IYgbCreditScoreService creditScoreService;

    @PreAuthorize("@ss.hasPermi('ygb:creditScore:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbCreditScore creditScore)
    {
        startPage();
        List<YgbCreditScore> list = creditScoreService.selectCreditScoreList(creditScore);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:creditScore:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbCreditScore creditScore)
    {
        return success(creditScoreService.selectCreditScoreSummary(creditScore));
    }

    @PreAuthorize("@ss.hasPermi('ygb:creditScore:query')")
    @GetMapping("/{scoreId}")
    public AjaxResult getInfo(@PathVariable Long scoreId)
    {
        YgbCreditScore score = creditScoreService.selectCreditScoreById(scoreId);
        AjaxResult result = success(score);
        if (StringUtils.isNotEmpty(score.getFactorJson()))
        {
            result.put("factors", JSON.parseObject(score.getFactorJson()));
        }
        return result;
    }

    @PreAuthorize("@ss.hasPermi('ygb:creditScore:query')")
    @GetMapping("/enterprise/{enterpriseId}")
    public AjaxResult getEnterpriseScore(@PathVariable Long enterpriseId)
    {
        YgbCreditScore score = creditScoreService.selectLatestCreditScore(enterpriseId);
        AjaxResult result = success(score);
        if (score != null && StringUtils.isNotEmpty(score.getFactorJson()))
        {
            result.put("factors", JSON.parseObject(score.getFactorJson()));
        }
        return result;
    }

    @Log(title = "企业信用评分", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:creditScore:generate')")
    @PostMapping("/generate")
    public AjaxResult generate(@Validated @RequestBody YgbCreditScoreGenerateRequest request)
    {
        int rows = creditScoreService.generateCreditScores(request, getUsername());
        return success("企业信用评分生成完成，本次写入 " + rows + " 条记录。");
    }

    @Log(title = "企业信用评分", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:creditScore:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbCreditScore creditScore)
    {
        List<YgbCreditScore> list = creditScoreService.selectCreditScoreList(creditScore);
        ExcelUtil<YgbCreditScore> util = new ExcelUtil<>(YgbCreditScore.class);
        util.exportExcel(response, list, "企业信用评分");
    }
}
