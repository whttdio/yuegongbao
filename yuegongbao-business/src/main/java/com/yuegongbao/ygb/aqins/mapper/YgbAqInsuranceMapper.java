package com.yuegongbao.ygb.aqins.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;

public interface YgbAqInsuranceMapper
{
    List<YgbAqInsurance> selectAqInsuranceList(YgbAqInsurance aqInsurance);

    YgbAqInsurance selectAqInsuranceById(Long policyId);

    YgbAqInsurance selectAqInsuranceByScope(@Param("statMonth") String statMonth, @Param("enterpriseId") Long enterpriseId);

    YgbAqInsurance selectLatestAqInsuranceByEnterpriseId(@Param("enterpriseId") Long enterpriseId);

    int insertAqInsurance(YgbAqInsurance aqInsurance);

    int updateAqInsurance(YgbAqInsurance aqInsurance);

    int updateFundAmounts(@Param("policyId") Long policyId, @Param("usedFundAmount") java.math.BigDecimal usedFundAmount,
        @Param("remainingFundAmount") java.math.BigDecimal remainingFundAmount, @Param("updateBy") String updateBy);
}
