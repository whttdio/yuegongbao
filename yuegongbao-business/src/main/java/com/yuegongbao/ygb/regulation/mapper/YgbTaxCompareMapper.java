package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;

public interface YgbTaxCompareMapper
{
    public List<YgbTaxCompare> selectTaxCompareList(YgbTaxCompare taxCompare);

    public int insertTaxCompare(YgbTaxCompare taxCompare);

    public int deleteByScope(YgbTaxCompare taxCompare);

    public List<YgbTaxCompare> selectWorkerTaxCompareList(@Param("personId") Long personId,
        @Param("yearPrefix") String yearPrefix);

    public YgbTaxCompare selectWorkerTaxCompareDetail(@Param("personId") Long personId,
        @Param("statMonth") String statMonth);
}
