package com.yuegongbao.ygb.regulation.service;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary;

public interface IYgbSocialPaymentService
{
    public List<YgbSocialPayment> selectSocialPaymentList(YgbSocialPayment socialPayment);

    public YgbSocialPaymentSummary selectSocialPaymentSummary(YgbSocialPayment socialPayment);

    public YgbSocialPayment selectSocialPaymentById(Long paymentId);

    public int syncSocialPayment(String statMonth, Long enterpriseId, String operator);
}

