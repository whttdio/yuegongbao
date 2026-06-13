package com.yuegongbao.ygb.aqins.service;

import java.util.List;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary;

public interface IYgbAqInsuranceService
{
    List<YgbAqInsurance> selectAqInsuranceList(YgbAqInsurance aqInsurance);

    YgbAqInsuranceSummary selectAqInsuranceSummary(YgbAqInsurance aqInsurance);

    YgbAqInsurance selectAqInsuranceById(Long policyId);

    int syncAqInsurance(String statMonth, Long enterpriseId, String operator);
}
