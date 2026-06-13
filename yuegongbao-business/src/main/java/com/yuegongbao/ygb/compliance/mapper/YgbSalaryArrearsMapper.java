package com.yuegongbao.ygb.compliance.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryArrears;

public interface YgbSalaryArrearsMapper
{
    List<YgbSalaryArrears> selectSalaryArrearsList(YgbSalaryArrears arrears);

    YgbSalaryArrears selectSalaryArrearsByBatchId(Long batchId);

    int insertOrUpdateHandle(YgbSalaryArrears arrears);

    Integer countArrearsEnterprise(@Param("regionCode") String regionCode, @Param("statMonth") String statMonth);
}
