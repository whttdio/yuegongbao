package com.yuegongbao.ygb.aireport.service;

import java.util.List;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportSubscription;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportSubscriptionSummary;

public interface IYgbAiReportSubscriptionService
{
    List<YgbAiReportSubscription> selectAiReportSubscriptionList(YgbAiReportSubscription subscription);

    YgbAiReportSubscriptionSummary selectAiReportSubscriptionSummary(YgbAiReportSubscription subscription);

    YgbAiReportSubscription selectAiReportSubscriptionById(Long subscriptionId);

    int insertAiReportSubscription(YgbAiReportSubscription subscription, String operator);

    int updateAiReportSubscription(YgbAiReportSubscription subscription, String operator);

    int deleteAiReportSubscriptionByIds(Long[] subscriptionIds, String operator);
}
