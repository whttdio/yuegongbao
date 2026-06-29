package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialBaseCompareMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.regulation.service.IYgbSocialBaseCompareService;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.util.YgbRiskCalculator;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbSocialBaseCompareServiceImpl implements IYgbSocialBaseCompareService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbSocialBaseCompareMapper socialBaseCompareMapper;

    @Autowired
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Override
    public List<YgbSocialBaseCompare> selectSocialBaseCompareList(YgbSocialBaseCompare socialBaseCompare)
    {
        return socialBaseCompareMapper.selectSocialBaseCompareList(socialBaseCompare);
    }

    @Override
    public YgbSocialBaseCompareSummary selectSocialBaseCompareSummary(YgbSocialBaseCompare socialBaseCompare)
    {
        List<YgbSocialBaseCompare> list = selectSocialBaseCompareList(socialBaseCompare);
        YgbSocialBaseCompareSummary summary = new YgbSocialBaseCompareSummary();
        summary.setTotalCount(list.size());

        int normalCount = 0;
        int abnormalCount = 0;
        int warnedCount = 0;
        for (YgbSocialBaseCompare item : list)
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
    public int compare(String statMonth, Long enterpriseId, String operator)
    {
        validateMonth(statMonth);
        YgbSocialPayment query = new YgbSocialPayment();
        query.setStatMonth(statMonth);
        query.setEnterpriseId(enterpriseId);
        applyScope(query);
        List<YgbSocialPayment> paymentList = socialPaymentMapper.selectSocialPaymentList(query);

        YgbSocialBaseCompare deleteScope = new YgbSocialBaseCompare();
        deleteScope.setStatMonth(statMonth);
        deleteScope.setEnterpriseId(enterpriseId);
        applyScope(deleteScope);
        socialBaseCompareMapper.deleteByScope(deleteScope);

        int rows = 0;
        for (YgbSocialPayment payment : paymentList)
        {
            BigDecimal salaryAmount = salaryDetailMapper.selectLatestNetAmountByPerson(payment.getPersonId(), statMonth);
            if (salaryAmount == null)
            {
                salaryAmount = BigDecimal.ZERO;
            }

            BigDecimal diffRatio = YgbRiskCalculator.diffRatio(salaryAmount, payment.getBaseAmount());
            boolean normal = YgbRiskCalculator.isSocialBaseNormal(salaryAmount, payment.getBaseAmount());

            YgbSocialBaseCompare compare = new YgbSocialBaseCompare();
            compare.setStatMonth(statMonth);
            compare.setEnterpriseId(payment.getEnterpriseId());
            compare.setEnterpriseName(payment.getEnterpriseName());
            compare.setPersonId(payment.getPersonId());
            compare.setPersonName(payment.getPersonName());
            compare.setIdCard(payment.getIdCard());
            compare.setRegionCode(payment.getRegionCode());
            compare.setSalaryAmount(salaryAmount);
            compare.setSocialBaseAmount(payment.getBaseAmount());
            compare.setDiffRatio(diffRatio);
            compare.setCompareResult(normal ? "1" : "2");
            compare.setWarningStatus(normal ? "0" : "1");
            compare.setRemark(normal ? "工资与社保基数差异正常。" : "工资与社保基数差异超过 20%。");
            compare.setCreateBy(operator);
            socialBaseCompareMapper.insertSocialBaseCompare(compare);
            rows++;

            if (!normal)
            {
                YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
                warning.setWarnLevel("2");
                warning.setWarnType("SOCIAL_BASE_COMPARE");
                warning.setSourceModule("SOCIAL");
                warning.setTargetObjectId(payment.getPersonId());
                warning.setTargetType("3");
                warning.setEnterpriseId(payment.getEnterpriseId());
                warning.setEnterpriseName(payment.getEnterpriseName());
                warning.setRegionCode(payment.getRegionCode());
                warning.setContent("社保基数比对异常，差异超过 20%，人员：" + payment.getPersonName());
                warningService.createWarningIfAbsent(warning, operator);
            }
        }
        return rows;
    }

    private void applyScope(com.yuegongbao.common.core.domain.BaseEntity query)
    {
        if (enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            enterpriseScopeHelper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "enterprise_id");
            return;
        }
        regionScopeHelper.applyRegionDataScope(query, "region_code");
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }
}
