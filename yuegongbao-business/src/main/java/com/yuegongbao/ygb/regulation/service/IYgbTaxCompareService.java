package com.yuegongbao.ygb.regulation.service;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompareSummary;

public interface IYgbTaxCompareService
{
    public List<YgbTaxCompare> selectTaxCompareList(YgbTaxCompare taxCompare);

    public YgbTaxCompareSummary selectTaxCompareSummary(YgbTaxCompare taxCompare);

    public int syncTaxCompare(String statMonth, Long enterpriseId, String operator);

    public int compare(String statMonth, Long enterpriseId, String operator);
}

