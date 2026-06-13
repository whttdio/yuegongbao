package com.yuegongbao.ygb.aireport.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportSubscription;

public interface YgbAiReportSubscriptionMapper
{
    List<YgbAiReportSubscription> selectAiReportSubscriptionList(YgbAiReportSubscription subscription);

    YgbAiReportSubscription selectAiReportSubscriptionById(Long subscriptionId);

    int insertAiReportSubscription(YgbAiReportSubscription subscription);

    int updateAiReportSubscription(YgbAiReportSubscription subscription);

    int deleteAiReportSubscriptionByIds(@Param("subscriptionIds") Long[] subscriptionIds, @Param("updateBy") String updateBy);
}
