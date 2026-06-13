package com.yuegongbao.ygb.regulation.service;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompareSummary;

public interface IYgbSocialBaseCompareService
{
    public List<YgbSocialBaseCompare> selectSocialBaseCompareList(YgbSocialBaseCompare socialBaseCompare);

    public YgbSocialBaseCompareSummary selectSocialBaseCompareSummary(YgbSocialBaseCompare socialBaseCompare);

    public int compare(String statMonth, Long enterpriseId, String operator);
}

