package com.yuegongbao.ygb.regulation.service.impl;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary;
import com.yuegongbao.ygb.domain.vo.YgbSocialPaymentStubItem;
import com.yuegongbao.ygb.integration.SocialClient;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.regulation.service.IYgbSocialPaymentService;

@Service
public class YgbSocialPaymentServiceImpl implements IYgbSocialPaymentService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Autowired
    private SocialClient socialClient;

    @Override
    public List<YgbSocialPayment> selectSocialPaymentList(YgbSocialPayment socialPayment)
    {
        return socialPaymentMapper.selectSocialPaymentList(socialPayment);
    }

    @Override
    public YgbSocialPaymentSummary selectSocialPaymentSummary(YgbSocialPayment socialPayment)
    {
        List<YgbSocialPayment> list = selectSocialPaymentList(socialPayment);
        YgbSocialPaymentSummary summary = new YgbSocialPaymentSummary();
        summary.setTotalCount(list.size());

        int normalCount = 0;
        int unpaidCount = 0;
        int overdueCount = 0;
        for (YgbSocialPayment item : list)
        {
            if ("1".equals(item.getPaymentStatus()))
            {
                normalCount++;
            }
            if ("0".equals(item.getPaymentStatus()))
            {
                unpaidCount++;
            }
            if ("2".equals(item.getPaymentStatus()))
            {
                overdueCount++;
            }
        }

        summary.setNormalCount(normalCount);
        summary.setUnpaidCount(unpaidCount);
        summary.setOverdueCount(overdueCount);
        return summary;
    }

    @Override
    public YgbSocialPayment selectSocialPaymentById(Long paymentId)
    {
        return socialPaymentMapper.selectSocialPaymentById(paymentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncSocialPayment(String statMonth, Long enterpriseId, String operator)
    {
        validateMonth(statMonth);
        socialPaymentMapper.deleteByScope(statMonth, enterpriseId);
        List<YgbSocialPaymentStubItem> items = socialClient.pullMonthlyPayments(statMonth);
        int rows = 0;
        for (YgbSocialPaymentStubItem item : items)
        {
            if (enterpriseId != null && !enterpriseId.equals(item.getEnterpriseId()))
            {
                continue;
            }

            YgbSocialPayment socialPayment = new YgbSocialPayment();
            socialPayment.setStatMonth(statMonth);
            socialPayment.setEnterpriseId(item.getEnterpriseId());
            socialPayment.setEnterpriseName(item.getEnterpriseName());
            socialPayment.setPersonId(item.getPersonId());
            socialPayment.setPersonName(item.getPersonName());
            socialPayment.setIdCard(item.getIdCard());
            socialPayment.setRegionCode(item.getRegionCode());
            socialPayment.setBaseAmount(item.getBaseAmount());
            socialPayment.setPaidAmount(item.getPaidAmount());
            socialPayment.setPaymentStatus(item.getPaymentStatus());
            socialPayment.setSourceSerialNo(item.getExternalSerialNo());
            socialPayment.setSourceStatus(item.getSourceStatus());
            socialPayment.setSourceMessage(item.getSourceMessage());
            socialPayment.setCallbackTime(item.getCallbackTime());
            socialPayment.setRawPayload(item.getRawPayload());
            socialPayment.setCreateBy(operator);
            socialPaymentMapper.insertSocialPayment(socialPayment);
            rows++;
        }
        return rows;
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }
}
