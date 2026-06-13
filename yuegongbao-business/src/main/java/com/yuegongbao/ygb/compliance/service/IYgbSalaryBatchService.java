package com.yuegongbao.ygb.compliance.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary;
import com.yuegongbao.ygb.domain.vo.YgbBankCallbackRequest;

/**
 * 工资批次服务接口。
 *
 * @author yuegongbao
 */
public interface IYgbSalaryBatchService
{
    public List<YgbSalaryBatch> selectSalaryBatchList(YgbSalaryBatch salaryBatch);

    public YgbSalaryBatchSummary selectSalaryBatchSummary(YgbSalaryBatch salaryBatch);

    public List<YgbSalaryBatch> selectSalaryBatchOptions();

    public YgbSalaryBatch selectSalaryBatchById(Long batchId);

    public boolean checkBatchNoUnique(YgbSalaryBatch salaryBatch);

    public int insertSalaryBatch(YgbSalaryBatch salaryBatch);

    public int updateSalaryBatch(YgbSalaryBatch salaryBatch);

    public int deleteSalaryBatchByIds(Long[] batchIds, String updateBy);

    public int generateSalaryDetail(Long batchId, String updateBy);

    public int confirmAccount(Long batchId, BigDecimal accountReceivedAmount, String bankSerialNo, String updateBy);

    public int submitBatch(Long batchId, String updateBy);

    public Map<String, Object> handleBankCallback(YgbBankCallbackRequest request, String updateBy);

    public void refreshBatchAmounts(Long batchId, String updateBy);
}

