package com.yuegongbao.ygb.aqins.service;

import java.util.List;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFundSummary;

public interface IYgbPreventionFundService
{
    List<YgbPreventionFund> selectPreventionFundList(YgbPreventionFund preventionFund);

    YgbPreventionFundSummary selectPreventionFundSummary(YgbPreventionFund preventionFund);

    YgbPreventionFund selectPreventionFundById(Long fundId);

    int updatePreventionFund(YgbPreventionFund preventionFund, String operator);
}
