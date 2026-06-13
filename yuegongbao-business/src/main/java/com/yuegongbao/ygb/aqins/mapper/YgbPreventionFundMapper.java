package com.yuegongbao.ygb.aqins.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;

public interface YgbPreventionFundMapper
{
    List<YgbPreventionFund> selectPreventionFundList(YgbPreventionFund preventionFund);

    YgbPreventionFund selectPreventionFundById(Long fundId);

    YgbPreventionFund selectPreventionFundByScope(@Param("policyId") Long policyId, @Param("statMonth") String statMonth);

    int insertPreventionFund(YgbPreventionFund preventionFund);

    int updatePreventionFund(YgbPreventionFund preventionFund);
}
