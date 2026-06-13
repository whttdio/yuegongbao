package com.yuegongbao.ygb.aireport.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportSubscription;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportSubscriptionSummary;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportSubscriptionMapper;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportSubscriptionService;
import com.yuegongbao.ygb.util.YgbRegionHelper;

@Service
public class YgbAiReportSubscriptionServiceImpl implements IYgbAiReportSubscriptionService
{
    @Autowired
    private YgbAiReportSubscriptionMapper aiReportSubscriptionMapper;

    @Override
    public List<YgbAiReportSubscription> selectAiReportSubscriptionList(YgbAiReportSubscription subscription)
    {
        List<YgbAiReportSubscription> list = aiReportSubscriptionMapper.selectAiReportSubscriptionList(subscription);
        list.forEach(this::hydrateRegionName);
        return list;
    }

    @Override
    public YgbAiReportSubscriptionSummary selectAiReportSubscriptionSummary(YgbAiReportSubscription subscription)
    {
        List<YgbAiReportSubscription> list = selectAiReportSubscriptionList(subscription);
        YgbAiReportSubscriptionSummary summary = new YgbAiReportSubscriptionSummary();
        summary.setTotalCount(list.size());

        int activeCount = 0;
        int monthlyCount = 0;
        int multiChannelCount = 0;
        for (YgbAiReportSubscription item : list)
        {
            if ("1".equals(item.getStatus()))
            {
                activeCount++;
            }
            if ("MONTHLY".equals(item.getCycleType()))
            {
                monthlyCount++;
            }
            if (StringUtils.isNotEmpty(item.getReceiveType()) && item.getReceiveType().contains(","))
            {
                multiChannelCount++;
            }
        }

        summary.setActiveCount(activeCount);
        summary.setMonthlyCount(monthlyCount);
        summary.setMultiChannelCount(multiChannelCount);
        return summary;
    }

    @Override
    public YgbAiReportSubscription selectAiReportSubscriptionById(Long subscriptionId)
    {
        YgbAiReportSubscription subscription = aiReportSubscriptionMapper.selectAiReportSubscriptionById(subscriptionId);
        if (subscription != null)
        {
            hydrateRegionName(subscription);
        }
        return subscription;
    }

    @Override
    public int insertAiReportSubscription(YgbAiReportSubscription subscription, String operator)
    {
        fillDefaults(subscription);
        subscription.setCreateBy(operator);
        subscription.setCreateTime(new Date());
        subscription.setUpdateBy(operator);
        subscription.setUpdateTime(new Date());
        return aiReportSubscriptionMapper.insertAiReportSubscription(subscription);
    }

    @Override
    public int updateAiReportSubscription(YgbAiReportSubscription subscription, String operator)
    {
        if (subscription == null || subscription.getSubscriptionId() == null)
        {
            throw new ServiceException("Subscription ID cannot be empty");
        }
        fillDefaults(subscription);
        subscription.setUpdateBy(operator);
        subscription.setUpdateTime(new Date());
        return aiReportSubscriptionMapper.updateAiReportSubscription(subscription);
    }

    @Override
    public int deleteAiReportSubscriptionByIds(Long[] subscriptionIds, String operator)
    {
        return aiReportSubscriptionMapper.deleteAiReportSubscriptionByIds(subscriptionIds, operator);
    }

    private void fillDefaults(YgbAiReportSubscription subscription)
    {
        if (subscription == null)
        {
            throw new ServiceException("AI report subscription cannot be null");
        }
        if (StringUtils.isEmpty(subscription.getSubscriptionName()))
        {
            throw new ServiceException("Subscription name cannot be empty");
        }
        if (StringUtils.isEmpty(subscription.getReportType()))
        {
            subscription.setReportType("MONTHLY");
        }
        if (StringUtils.isEmpty(subscription.getRegionCode()))
        {
            subscription.setRegionCode(YgbRegionHelper.defaultDashboardRegion(null));
        }
        if (StringUtils.isEmpty(subscription.getCycleType()))
        {
            subscription.setCycleType("MONTHLY");
        }
        if (StringUtils.isEmpty(subscription.getReceiveType()))
        {
            subscription.setReceiveType("INTERNAL");
        }
        if (StringUtils.isEmpty(subscription.getVersionScope()))
        {
            subscription.setVersionScope("current");
        }
        if (StringUtils.isEmpty(subscription.getStatus()))
        {
            subscription.setStatus("1");
        }
        if (StringUtils.isEmpty(subscription.getSourceMode()))
        {
            subscription.setSourceMode("manual");
        }
    }

    private void hydrateRegionName(YgbAiReportSubscription subscription)
    {
        subscription.setRegionName(YgbRegionHelper.resolveRegionName(subscription.getRegionCode()));
    }
}
