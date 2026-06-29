package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompareSummary;
import com.yuegongbao.ygb.domain.vo.YgbTaxRecordStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.TaxClient;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbTaxCompareMapper;
import com.yuegongbao.ygb.regulation.service.IYgbTaxCompareService;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.util.YgbRiskCalculator;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbTaxCompareServiceImpl implements IYgbTaxCompareService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbTaxCompareMapper taxCompareMapper;

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Autowired
    private TaxClient taxClient;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Override
    public List<YgbTaxCompare> selectTaxCompareList(YgbTaxCompare taxCompare)
    {
        return taxCompareMapper.selectTaxCompareList(taxCompare);
    }

    @Override
    public YgbTaxCompareSummary selectTaxCompareSummary(YgbTaxCompare taxCompare)
    {
        List<YgbTaxCompare> list = selectTaxCompareList(taxCompare);
        YgbTaxCompareSummary summary = new YgbTaxCompareSummary();
        summary.setTotalCount(list.size());

        int normalCount = 0;
        int abnormalCount = 0;
        int warnedCount = 0;
        for (YgbTaxCompare item : list)
        {
            if ("1".equals(item.getCompareResult()))
            {
                normalCount++;
            }
            if ("2".equals(item.getCompareResult()))
            {
                abnormalCount++;
            }
            if ("1".equals(item.getWarningStatus()))
            {
                warnedCount++;
            }
        }

        summary.setNormalCount(normalCount);
        summary.setAbnormalCount(abnormalCount);
        summary.setWarnedCount(warnedCount);
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncTaxCompare(String statMonth, Long enterpriseId, String operator)
    {
        return rebuildCompareRows(statMonth, enterpriseId, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int compare(String statMonth, Long enterpriseId, String operator)
    {
        return rebuildCompareRows(statMonth, enterpriseId, operator);
    }

    private int rebuildCompareRows(String statMonth, Long enterpriseId, String operator)
    {
        validateMonth(statMonth);
        YgbTaxCompare deleteScope = buildScopedTaxQuery(statMonth, enterpriseId);
        taxCompareMapper.deleteByScope(deleteScope);
        List<YgbTaxRecordStubItem> records = taxClient.pullMonthlyRecords(statMonth);

        int rows = 0;
        for (YgbTaxRecordStubItem item : records)
        {
            if (!isItemInScope(item.getEnterpriseId(), item.getRegionCode(), enterpriseId))
            {
                continue;
            }

            BigDecimal salaryAmount = salaryDetailMapper.selectLatestNetAmountByPerson(item.getPersonId(), statMonth);
            if (salaryAmount == null)
            {
                salaryAmount = BigDecimal.ZERO;
            }

            BigDecimal diffRatio = YgbRiskCalculator.diffRatio(salaryAmount, item.getDeclaredAmount());
            boolean normal = YgbRiskCalculator.isTaxNormal(salaryAmount, item.getDeclaredAmount());

            YgbTaxCompare compare = new YgbTaxCompare();
            compare.setStatMonth(statMonth);
            compare.setEnterpriseId(item.getEnterpriseId());
            compare.setEnterpriseName(item.getEnterpriseName());
            compare.setPersonId(item.getPersonId());
            compare.setPersonName(item.getPersonName());
            compare.setIdCard(item.getIdCard());
            compare.setRegionCode(item.getRegionCode());
            compare.setSalaryAmount(salaryAmount);
            compare.setDeclaredAmount(item.getDeclaredAmount());
            compare.setDiffRatio(diffRatio);
            compare.setCompareResult(normal ? "1" : "2");
            compare.setWarningStatus(normal ? "0" : "1");
            compare.setSourceSerialNo(item.getExternalSerialNo());
            compare.setSourceStatus(item.getSourceStatus());
            compare.setSourceMessage(item.getSourceMessage());
            compare.setCallbackTime(item.getCallbackTime());
            compare.setRawPayload(item.getRawPayload());
            compare.setRemark(normal ? "工资实发与个税申报收入差异正常。" : "工资实发与个税申报收入差异超过 10%。");
            compare.setCreateBy(operator);
            taxCompareMapper.insertTaxCompare(compare);
            rows++;

            if (!normal)
            {
                YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
                warning.setWarnLevel("2");
                warning.setWarnType("TAX_COMPARE");
                warning.setSourceModule("TAX");
                warning.setTargetObjectId(item.getPersonId());
                warning.setTargetType("3");
                warning.setEnterpriseId(item.getEnterpriseId());
                warning.setEnterpriseName(item.getEnterpriseName());
                warning.setRegionCode(item.getRegionCode());
                warning.setContent("个税比对异常，差异超过 10%，人员：" + item.getPersonName());
                warningService.createWarningIfAbsent(warning, operator);
            }
        }
        return rows;
    }

    private YgbTaxCompare buildScopedTaxQuery(String statMonth, Long enterpriseId)
    {
        YgbTaxCompare query = new YgbTaxCompare();
        query.setStatMonth(statMonth);
        query.setEnterpriseId(enterpriseId);
        applyTaxScope(query);
        return query;
    }

    private void applyTaxScope(YgbTaxCompare query)
    {
        if (enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            enterpriseScopeHelper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "enterprise_id");
            return;
        }
        regionScopeHelper.applyRegionDataScope(query, "region_code");
    }

    private boolean isItemInScope(Long itemEnterpriseId, String itemRegionCode, Long requestedEnterpriseId)
    {
        if (requestedEnterpriseId != null && !requestedEnterpriseId.equals(itemEnterpriseId))
        {
            return false;
        }
        try
        {
            regionScopeHelper.assertRegionAuthorized(itemRegionCode);
            enterpriseScopeHelper.assertEnterpriseAuthorized(itemEnterpriseId);
            return true;
        }
        catch (ServiceException e)
        {
            return false;
        }
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }
}
