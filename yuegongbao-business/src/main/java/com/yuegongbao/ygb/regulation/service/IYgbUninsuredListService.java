package com.yuegongbao.ygb.regulation.service;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredList;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredListSummary;

public interface IYgbUninsuredListService
{
    public List<YgbUninsuredList> selectUninsuredList(YgbUninsuredList uninsuredList);
    public YgbUninsuredListSummary selectUninsuredSummary(YgbUninsuredList uninsuredList);

    public int generate(String statMonth, Long enterpriseId, String operator);

    public int handle(Long listId, String disposalStatus, String remark, String operator);
}

