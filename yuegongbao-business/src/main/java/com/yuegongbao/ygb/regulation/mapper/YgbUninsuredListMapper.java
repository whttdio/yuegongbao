package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredList;

public interface YgbUninsuredListMapper
{
    public List<YgbUninsuredList> selectUninsuredList(YgbUninsuredList uninsuredList);

    public YgbUninsuredList selectUninsuredById(Long listId);

    public int insertUninsured(YgbUninsuredList uninsuredList);

    public int updateUninsuredHandle(YgbUninsuredList uninsuredList);

    public int deleteByScope(YgbUninsuredList uninsuredList);
}
