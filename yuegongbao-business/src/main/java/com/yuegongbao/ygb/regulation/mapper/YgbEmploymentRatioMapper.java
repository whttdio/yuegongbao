package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;

public interface YgbEmploymentRatioMapper
{
    public List<YgbEmploymentRatio> selectEmploymentRatioList(YgbEmploymentRatio employmentRatio);

    public int insertEmploymentRatio(YgbEmploymentRatio employmentRatio);

    public int deleteByScope(@Param("statMonth") String statMonth,
        @Param("employerEnterpriseId") Long employerEnterpriseId);
}
