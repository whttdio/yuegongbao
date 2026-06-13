package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;

public interface YgbSocialBaseCompareMapper
{
    public List<YgbSocialBaseCompare> selectSocialBaseCompareList(YgbSocialBaseCompare socialBaseCompare);

    public int insertSocialBaseCompare(YgbSocialBaseCompare socialBaseCompare);

    public int deleteByScope(@Param("statMonth") String statMonth, @Param("enterpriseId") Long enterpriseId);
}
