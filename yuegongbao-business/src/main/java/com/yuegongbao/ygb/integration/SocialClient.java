package com.yuegongbao.ygb.integration;

import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbInsuranceCheckResult;
import com.yuegongbao.ygb.domain.vo.YgbSocialPaymentStubItem;

/**
 * 人社社保接口。
 *
 * @author yuegongbao
 */
public interface SocialClient
{
    YgbInsuranceCheckResult checkInsurance(String idCard);

    List<YgbSocialPaymentStubItem> pullMonthlyPayments(String statMonth);
}
