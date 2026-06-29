package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;

public interface YgbSocialBaseCompareMapper
{
    public List<YgbSocialBaseCompare> selectSocialBaseCompareList(YgbSocialBaseCompare socialBaseCompare);

    public int insertSocialBaseCompare(YgbSocialBaseCompare socialBaseCompare);

    public int deleteByScope(YgbSocialBaseCompare socialBaseCompare);
}
