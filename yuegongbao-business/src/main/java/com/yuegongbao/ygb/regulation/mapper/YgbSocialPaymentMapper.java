package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;

public interface YgbSocialPaymentMapper
{
    public List<YgbSocialPayment> selectSocialPaymentList(YgbSocialPayment socialPayment);

    public YgbSocialPayment selectSocialPaymentById(Long paymentId);

    public int insertSocialPayment(YgbSocialPayment socialPayment);

    public int deleteByScope(YgbSocialPayment socialPayment);

    public List<YgbSocialPayment> selectWorkerSocialPaymentList(@Param("personId") Long personId,
        @Param("yearPrefix") String yearPrefix);

    public YgbSocialPayment selectWorkerSocialPaymentDetail(@Param("personId") Long personId,
        @Param("statMonth") String statMonth);
}
