package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;

public interface YgbEmploymentRatioMapper
{
    public List<YgbEmploymentRatio> selectEmploymentRatioList(YgbEmploymentRatio employmentRatio);

    public int insertEmploymentRatio(YgbEmploymentRatio employmentRatio);

    public int deleteByScope(YgbEmploymentRatio employmentRatio);
}
