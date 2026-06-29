package com.yuegongbao.ygb.compliance.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.compliance.mapper.YgbAttendanceMonthlyMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryBatchMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryArrearsService;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;
import com.yuegongbao.ygb.domain.vo.YgbBankCallbackRequest;
import com.yuegongbao.ygb.domain.vo.YgbBankCallbackResultItem;
import com.yuegongbao.ygb.domain.vo.YgbBankPaymentResponse;
import com.yuegongbao.ygb.domain.vo.YgbSalaryPaymentItem;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.integration.BankClient;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;

@Service
public class YgbSalaryBatchServiceImpl implements IYgbSalaryBatchService
{
    private static final BigDecimal MONTH_STANDARD_HOURS = new BigDecimal("174");
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private YgbSalaryBatchMapper salaryBatchMapper;

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Autowired
    private YgbAttendanceMonthlyMapper attendanceMonthlyMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private BankClient bankClient;

    @Autowired
    private WorkerMessageService workerMessageService;

    @Autowired
    private IYgbSalaryArrearsService salaryArrearsService;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbSalaryBatch> selectSalaryBatchList(YgbSalaryBatch salaryBatch)
    {
        return salaryBatchMapper.selectSalaryBatchList(salaryBatch);
    }

    @Override
    public YgbSalaryBatchSummary selectSalaryBatchSummary(YgbSalaryBatch salaryBatch)
    {
        List<YgbSalaryBatch> list = selectSalaryBatchList(salaryBatch);
        YgbSalaryBatchSummary summary = new YgbSalaryBatchSummary();
        summary.setTotalCount(list.size());

        int pendingGenerateCount = 0;
        int pendingSubmitCount = 0;
        int payingCount = 0;
        int paidCount = 0;
        int failedCount = 0;
        int awaitingAccountCount = 0;
        int totalPersonCount = 0;
        BigDecimal regulatorAccountBalance = ZERO;
        BigDecimal accountGapAmount = ZERO;
        for (YgbSalaryBatch item : list)
        {
            if ("3".equals(item.getBatchStatus()))
            {
                pendingGenerateCount++;
            }
            if ("4".equals(item.getBatchStatus()))
            {
                pendingSubmitCount++;
            }
            if ("5".equals(item.getBatchStatus()))
            {
                payingCount++;
            }
            if ("6".equals(item.getBatchStatus()))
            {
                paidCount++;
            }
            if ("7".equals(item.getBatchStatus()))
            {
                failedCount++;
            }
            if (!"1".equals(item.getAccountStatus()))
            {
                awaitingAccountCount++;
            }
            totalPersonCount += defaultInt(item.getTotalPersonCount());
            regulatorAccountBalance = regulatorAccountBalance.add(nvl(item.getAccountReceivedAmount()))
                .subtract(nvl(item.getTotalPaidAmount()));
            BigDecimal gap = nvl(item.getTotalPayableAmount()).subtract(nvl(item.getAccountReceivedAmount()));
            if (gap.compareTo(BigDecimal.ZERO) > 0)
            {
                accountGapAmount = accountGapAmount.add(gap);
            }
        }

        summary.setPendingGenerateCount(pendingGenerateCount);
        summary.setPendingSubmitCount(pendingSubmitCount);
        summary.setPayingCount(payingCount);
        summary.setPaidCount(paidCount);
        summary.setFailedCount(failedCount);
        summary.setAwaitingAccountCount(awaitingAccountCount);
        summary.setTotalPersonCount(totalPersonCount);
        summary.setFailedBatchCount(failedCount);
        summary.setArrearsEnterpriseCount(defaultInt(salaryArrearsService.countArrearsEnterprise(
            salaryBatch == null ? null : salaryBatch.getRegionCode(), salaryBatch == null ? null : salaryBatch.getStatMonth())));
        summary.setRegulatorAccountBalance(regulatorAccountBalance);
        summary.setAccountGapAmount(accountGapAmount);
        return summary;
    }

    @Override
    public List<YgbSalaryBatch> selectSalaryBatchOptions()
    {
        return salaryBatchMapper.selectSalaryBatchOptions();
    }

    @Override
    public YgbSalaryBatch selectSalaryBatchById(Long batchId)
    {
        YgbSalaryBatch batch = salaryBatchMapper.selectSalaryBatchById(batchId);
        if (batch != null)
        {
            dataScopeGuard.assertEntityAllowed(batch);
        }
        return batch;
    }

    @Override
    public boolean checkBatchNoUnique(YgbSalaryBatch salaryBatch)
    {
        Long batchId = StringUtils.isNull(salaryBatch.getBatchId()) ? -1L : salaryBatch.getBatchId();
        YgbSalaryBatch info = salaryBatchMapper.checkBatchNoUnique(salaryBatch.getBatchNo());
        if (StringUtils.isNotNull(info) && info.getBatchId().longValue() != batchId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertSalaryBatch(YgbSalaryBatch salaryBatch)
    {
        fillBatchSnapshot(salaryBatch);
        if (StringUtils.isEmpty(salaryBatch.getBatchStatus()))
        {
            salaryBatch.setBatchStatus("1");
        }
        if (StringUtils.isEmpty(salaryBatch.getAccountStatus()))
        {
            salaryBatch.setAccountStatus("0");
        }
        if (salaryBatch.getTotalPersonCount() == null)
        {
            salaryBatch.setTotalPersonCount(0);
        }
        if (salaryBatch.getTotalPayableAmount() == null)
        {
            salaryBatch.setTotalPayableAmount(ZERO);
        }
        if (salaryBatch.getTotalPaidAmount() == null)
        {
            salaryBatch.setTotalPaidAmount(ZERO);
        }
        if (salaryBatch.getAccountReceivedAmount() == null)
        {
            salaryBatch.setAccountReceivedAmount(ZERO);
        }
        return salaryBatchMapper.insertSalaryBatch(salaryBatch);
    }

    @Override
    public int updateSalaryBatch(YgbSalaryBatch salaryBatch)
    {
        requireBatch(salaryBatch.getBatchId());
        fillBatchSnapshot(salaryBatch);
        return salaryBatchMapper.updateSalaryBatch(salaryBatch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalaryBatchByIds(Long[] batchIds, String updateBy)
    {
        for (Long batchId : batchIds)
        {
            requireBatch(batchId);
            salaryDetailMapper.deleteByBatchId(batchId);
        }
        return salaryBatchMapper.deleteSalaryBatchByIds(batchIds, updateBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateSalaryDetail(Long batchId, String updateBy)
    {
        YgbSalaryBatch batch = requireBatch(batchId);
        if (!"1".equals(batch.getAccountStatus()))
        {
            throw new ServiceException("监管账户尚未到账，不能生成工资明细。");
        }

        YgbAttendanceMonthly query = new YgbAttendanceMonthly();
        query.setStatMonth(batch.getStatMonth());
        query.setDispatchEnterpriseId(batch.getDispatchEnterpriseId());
        List<YgbAttendanceMonthly> monthlyList = attendanceMonthlyMapper.selectAttendanceMonthlyList(query);
        if (monthlyList.isEmpty())
        {
            throw new ServiceException("未找到当前批次对应的考勤归集记录。");
        }

        salaryDetailMapper.deleteByBatchId(batchId);
        int rows = 0;
        for (YgbSalaryDetail detail : buildSalaryDetails(batch, monthlyList))
        {
            salaryDetailMapper.insertSalaryDetail(detail);
            rows++;
        }

        attendanceMonthlyMapper.updateSummaryStatusByScope(batch.getStatMonth(), batch.getDispatchEnterpriseId(), "3",
            updateBy);
        refreshBatchAmounts(batchId, updateBy);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmAccount(Long batchId, BigDecimal accountReceivedAmount, String bankSerialNo, String updateBy)
    {
        if (accountReceivedAmount == null || accountReceivedAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("到账金额必须大于 0。");
        }

        YgbSalaryBatch batch = requireBatch(batchId);
        if ("6".equals(batch.getBatchStatus()))
        {
            throw new ServiceException("已发放批次不允许再次确认到账。");
        }

        return salaryBatchMapper.updateBatchAccount(batchId, accountReceivedAmount, bankSerialNo, "3", updateBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitBatch(Long batchId, String updateBy)
    {
        YgbSalaryBatch batch = requireBatch(batchId);
        List<YgbSalaryDetail> detailList = loadDetailList(batchId);
        if (detailList.isEmpty())
        {
            throw new ServiceException("当前批次没有可发放的工资明细。");
        }

        BigDecimal pendingAmount = ZERO;
        int pendingCount = 0;
        for (YgbSalaryDetail detail : detailList)
        {
            if ("0".equals(detail.getPayStatus()))
            {
                pendingCount++;
                pendingAmount = pendingAmount.add(nvl(detail.getNetAmount()));
            }
        }

        if (pendingCount == 0)
        {
            throw new ServiceException("当前批次没有待发放的工资明细。");
        }
        if (nvl(batch.getAccountReceivedAmount()).compareTo(pendingAmount) < 0)
        {
            throw new ServiceException("监管账户到账金额不足，不能提交代发。");
        }

        YgbBankPaymentResponse paymentResponse = bankClient.submitPayment(batch.getBatchNo(), pendingAmount,
            buildPaymentItems(detailList));
        if (paymentResponse == null || !"SUCCESS".equalsIgnoreCase(paymentResponse.getSourceStatus()))
        {
            throw new ServiceException("银行代发提交失败："
                + (paymentResponse == null ? "未返回处理结果。" : paymentResponse.getSourceMessage()));
        }

        salaryDetailMapper.updatePayStatusByBatchId(batchId, "1", updateBy);
        salaryBatchMapper.updateBatchPaid(batchId, "5", updateBy);
        attendanceMonthlyMapper.updateSummaryStatusByScope(batch.getStatMonth(), batch.getDispatchEnterpriseId(), "4",
            updateBy);
        workerMessageService.notifySalaryBatchSubmitted(batch, detailList, updateBy);
        refreshBatchAmounts(batchId, updateBy);
        return pendingCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleBankCallback(YgbBankCallbackRequest request, String updateBy)
    {
        if (request == null || StringUtils.isEmpty(request.getBatchNo()))
        {
            throw new ServiceException("银行回调批次号不能为空。");
        }

        YgbSalaryBatch batch = salaryBatchMapper.selectSalaryBatchByBatchNo(request.getBatchNo());
        if (StringUtils.isNull(batch))
        {
            throw new ServiceException("未找到对应的工资批次。");
        }
        dataScopeGuard.assertEntityAllowed(batch);

        List<YgbSalaryDetail> currentDetails = loadDetailList(batch.getBatchId());
        List<YgbBankCallbackResultItem> callbackResults = resolveCallbackResults(request, currentDetails);
        int successCount = 0;
        int failCount = 0;
        int unmatchedCount = 0;

        for (YgbBankCallbackResultItem result : callbackResults)
        {
            boolean success = isSuccessStatus(result.getStatus());
            int rows = salaryDetailMapper.updatePayResultByBatchAndCardNo(batch.getBatchId(), result.getCardNo(),
                success ? "2" : "3", success ? "" : buildFailReason(result), updateBy);
            if (rows <= 0)
            {
                unmatchedCount++;
                continue;
            }
            if (success)
            {
                successCount++;
            }
            else
            {
                failCount++;
            }
        }

        if (successCount + failCount <= 0)
        {
            throw new ServiceException("银行回调未匹配到任何处理中工资明细。");
        }

        salaryBatchMapper.updateBatchPaid(batch.getBatchId(), failCount > 0 ? "7" : "6", updateBy);
        refreshBatchAmounts(batch.getBatchId(), updateBy);

        Map<String, Object> result = new HashMap<>();
        result.put("batchId", batch.getBatchId());
        result.put("batchNo", batch.getBatchNo());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("unmatchedCount", unmatchedCount);
        result.put("batchStatus", failCount > 0 ? "7" : "6");
        return result;
    }

    @Override
    public void refreshBatchAmounts(Long batchId, String updateBy)
    {
        List<YgbSalaryDetail> detailList = loadDetailList(batchId);
        int totalPersonCount = 0;
        BigDecimal totalPayableAmount = ZERO;
        BigDecimal totalPaidAmount = ZERO;

        for (YgbSalaryDetail detail : detailList)
        {
            totalPersonCount++;
            totalPayableAmount = totalPayableAmount.add(nvl(detail.getPayableAmount()));
            if ("2".equals(detail.getPayStatus()))
            {
                totalPaidAmount = totalPaidAmount.add(nvl(detail.getNetAmount()));
            }
        }

        YgbSalaryBatch batch = requireBatch(batchId);
        String batchStatus = batch.getBatchStatus();
        if (!detailList.isEmpty() && !"5".equals(batchStatus) && !"6".equals(batchStatus) && !"7".equals(batchStatus))
        {
            batchStatus = "4";
        }

        salaryBatchMapper.updateBatchAmounts(batchId, totalPersonCount, totalPayableAmount, totalPaidAmount,
            batchStatus, updateBy);
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value.intValue();
    }

    private void fillBatchSnapshot(YgbSalaryBatch salaryBatch)
    {
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(salaryBatch.getDispatchEnterpriseId());
        if (StringUtils.isNull(enterprise))
        {
            throw new ServiceException("派遣单位不存在。");
        }
        salaryBatch.setDispatchEnterpriseName(enterprise.getEnterpriseName());
        salaryBatch.setRegionCode(enterprise.getRegionCode());
    }

    private YgbSalaryBatch requireBatch(Long batchId)
    {
        if (batchId == null)
        {
            throw new ServiceException("工资批次ID不能为空。");
        }
        YgbSalaryBatch batch = salaryBatchMapper.selectSalaryBatchById(batchId);
        if (StringUtils.isNull(batch))
        {
            throw new ServiceException("工资批次不存在。");
        }
        dataScopeGuard.assertEntityAllowed(batch);
        return batch;
    }

    private List<YgbSalaryDetail> loadDetailList(Long batchId)
    {
        YgbSalaryDetail query = new YgbSalaryDetail();
        query.setBatchId(batchId);
        return salaryDetailMapper.selectSalaryDetailList(query);
    }

    private List<YgbSalaryDetail> buildSalaryDetails(YgbSalaryBatch batch, List<YgbAttendanceMonthly> monthlyList)
    {
        List<YgbSalaryDetail> details = new ArrayList<>();
        for (YgbAttendanceMonthly monthly : monthlyList)
        {
            YgbContract contract = contractMapper.selectContractById(monthly.getContractId());
            if (StringUtils.isNull(contract))
            {
                continue;
            }

            YgbSalaryDetail detail = new YgbSalaryDetail();
            detail.setBatchId(batch.getBatchId());
            detail.setBatchNo(batch.getBatchNo());
            detail.setStatMonth(batch.getStatMonth());
            detail.setMonthlyId(monthly.getMonthlyId());
            detail.setContractId(monthly.getContractId());
            detail.setContractNo(monthly.getContractNo());
            detail.setDispatchEnterpriseId(monthly.getDispatchEnterpriseId());
            detail.setDispatchEnterpriseName(monthly.getDispatchEnterpriseName());
            detail.setEmployerEnterpriseId(monthly.getEmployerEnterpriseId());
            detail.setEmployerEnterpriseName(monthly.getEmployerEnterpriseName());
            detail.setPersonId(monthly.getPersonId());
            detail.setPersonName(monthly.getPersonName());
            detail.setIdCard(monthly.getIdCard());
            detail.setRegionCode(monthly.getRegionCode());
            detail.setAttendanceDays(monthly.getAttendanceDays());
            detail.setTotalHours(nvl(monthly.getTotalHours()));
            detail.setAttCheck(monthly.getAttCheck());

            BigDecimal monthlyWage = nvl(contract.getMonthlyWage());
            BigDecimal payableAmount = ZERO;
            if (monthlyWage.compareTo(BigDecimal.ZERO) > 0 && nvl(monthly.getTotalHours()).compareTo(BigDecimal.ZERO) > 0)
            {
                payableAmount = monthlyWage.divide(MONTH_STANDARD_HOURS, 8, RoundingMode.HALF_UP)
                    .multiply(nvl(monthly.getTotalHours()))
                    .setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal deductionAmount = monthlyWage.subtract(payableAmount);
            if (deductionAmount.compareTo(BigDecimal.ZERO) < 0)
            {
                deductionAmount = ZERO;
            }

            BigDecimal netAmount = "1".equals(monthly.getAttCheck()) ? payableAmount : ZERO;
            detail.setPayableAmount(payableAmount);
            detail.setDeductionAmount(deductionAmount);
            detail.setNetAmount(netAmount);
            detail.setBankAccountName(monthly.getPersonName());
            detail.setBankAccountNo(mockBankAccount(monthly.getIdCard(), monthly.getPersonId()));
            if ("1".equals(monthly.getAttCheck()))
            {
                detail.setPayStatus("0");
                detail.setFailReason("");
            }
            else
            {
                detail.setPayStatus("3");
                detail.setFailReason("考勤校验未通过。");
            }
            detail.setCreateBy(batch.getUpdateBy() == null ? batch.getCreateBy() : batch.getUpdateBy());
            detail.setRemark(monthly.getRemark());
            details.add(detail);
        }
        return details;
    }

    private List<YgbSalaryPaymentItem> buildPaymentItems(List<YgbSalaryDetail> detailList)
    {
        List<YgbSalaryPaymentItem> items = new ArrayList<>();
        for (YgbSalaryDetail detail : detailList)
        {
            if (!"0".equals(detail.getPayStatus()))
            {
                continue;
            }

            YgbSalaryPaymentItem item = new YgbSalaryPaymentItem();
            item.setPersonName(detail.getPersonName());
            item.setBankCardNo(detail.getBankAccountNo());
            item.setAmount(nvl(detail.getNetAmount()));
            item.setAttCheck(detail.getAttCheck());
            items.add(item);
        }
        return items;
    }

    private List<YgbBankCallbackResultItem> resolveCallbackResults(YgbBankCallbackRequest request,
        List<YgbSalaryDetail> detailList)
    {
        if (request.getResults() != null && !request.getResults().isEmpty())
        {
            return request.getResults();
        }

        List<YgbBankCallbackResultItem> results = new ArrayList<>();
        for (YgbSalaryDetail detail : detailList)
        {
            if (!"1".equals(detail.getPayStatus()))
            {
                continue;
            }

            YgbBankCallbackResultItem item = new YgbBankCallbackResultItem();
            item.setCardNo(detail.getBankAccountNo());
            item.setStatus("SUCCESS");
            item.setReason("");
            item.setTxnId("STUB-CBK-" + detail.getDetailId());
            results.add(item);
        }

        if (results.isEmpty())
        {
            throw new ServiceException("当前批次没有待回调的处理中工资明细。");
        }
        return results;
    }

    private boolean isSuccessStatus(String status)
    {
        if (StringUtils.isEmpty(status))
        {
            return true;
        }
        String normalized = status.trim().toUpperCase();
        return "1".equals(normalized) || "SUCCESS".equals(normalized) || "PAID".equals(normalized)
            || "S".equals(normalized);
    }

    private String buildFailReason(YgbBankCallbackResultItem result)
    {
        return StringUtils.isEmpty(result.getReason()) ? "银行代发失败。" : result.getReason();
    }

    private BigDecimal nvl(BigDecimal value)
    {
        return value == null ? ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String mockBankAccount(String idCard, Long personId)
    {
        String tail = idCard;
        if (StringUtils.isEmpty(tail))
        {
            tail = String.valueOf(personId == null ? 0L : personId);
        }
        tail = tail.replaceAll("[^0-9]", "");
        if (tail.length() > 10)
        {
            tail = tail.substring(tail.length() - 10);
        }
        return "622202" + StringUtils.padl(tail, 10, '0');
    }
}
