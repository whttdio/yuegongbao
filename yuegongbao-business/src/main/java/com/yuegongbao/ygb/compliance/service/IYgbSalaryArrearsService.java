package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryArrears;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryArrearsSummary;

public interface IYgbSalaryArrearsService
{
    List<YgbSalaryArrears> selectSalaryArrearsList(YgbSalaryArrears arrears);

    YgbSalaryArrearsSummary selectSalaryArrearsSummary(YgbSalaryArrears arrears);

    YgbSalaryArrears selectSalaryArrearsByBatchId(Long batchId);

    int handleSalaryArrears(YgbSalaryArrears arrears, String operator);

    Integer countArrearsEnterprise(String regionCode, String statMonth);
}
