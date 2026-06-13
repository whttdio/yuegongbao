package com.yuegongbao.ygb.compliance.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;

/**
 * 工资批次Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbSalaryBatchMapper
{
    public List<YgbSalaryBatch> selectSalaryBatchList(YgbSalaryBatch salaryBatch);

    public List<YgbSalaryBatch> selectSalaryBatchOptions();

    public YgbSalaryBatch selectSalaryBatchById(Long batchId);

    public YgbSalaryBatch selectSalaryBatchByBatchNo(String batchNo);

    public YgbSalaryBatch checkBatchNoUnique(String batchNo);

    public int insertSalaryBatch(YgbSalaryBatch salaryBatch);

    public int updateSalaryBatch(YgbSalaryBatch salaryBatch);

    public int updateBatchAmounts(@Param("batchId") Long batchId, @Param("totalPersonCount") Integer totalPersonCount,
        @Param("totalPayableAmount") BigDecimal totalPayableAmount, @Param("totalPaidAmount") BigDecimal totalPaidAmount,
        @Param("batchStatus") String batchStatus, @Param("updateBy") String updateBy);

    public int updateBatchAccount(@Param("batchId") Long batchId, @Param("accountReceivedAmount") BigDecimal accountReceivedAmount,
        @Param("bankSerialNo") String bankSerialNo, @Param("batchStatus") String batchStatus, @Param("updateBy") String updateBy);

    public int updateBatchPaid(@Param("batchId") Long batchId, @Param("batchStatus") String batchStatus,
        @Param("updateBy") String updateBy);

    public int deleteSalaryBatchByIds(@Param("batchIds") Long[] batchIds, @Param("updateBy") String updateBy);
}
