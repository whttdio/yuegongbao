package com.yuegongbao.ygb.compliance.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryDetailService;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetailSummary;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryBatchMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;

@Service
public class YgbSalaryDetailServiceImpl implements IYgbSalaryDetailService
{
    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Autowired
    private YgbSalaryBatchMapper salaryBatchMapper;

    @Autowired
    private IYgbSalaryBatchService salaryBatchService;

    @Override
    public List<YgbSalaryDetail> selectSalaryDetailList(YgbSalaryDetail salaryDetail)
    {
        return salaryDetailMapper.selectSalaryDetailList(salaryDetail);
    }

    @Override
    public YgbSalaryDetailSummary selectSalaryDetailSummary(YgbSalaryDetail salaryDetail)
    {
        List<YgbSalaryDetail> list = selectSalaryDetailList(salaryDetail);
        YgbSalaryDetailSummary summary = new YgbSalaryDetailSummary();
        summary.setTotalCount(list.size());

        int pendingPayCount = 0;
        int payingCount = 0;
        int paidCount = 0;
        int failedCount = 0;
        int checkFailedCount = 0;
        for (YgbSalaryDetail item : list)
        {
            if ("0".equals(item.getPayStatus()))
            {
                pendingPayCount++;
            }
            if ("1".equals(item.getPayStatus()))
            {
                payingCount++;
            }
            if ("2".equals(item.getPayStatus()))
            {
                paidCount++;
            }
            if ("3".equals(item.getPayStatus()))
            {
                failedCount++;
            }
            if ("0".equals(item.getAttCheck()))
            {
                checkFailedCount++;
            }
        }

        summary.setPendingPayCount(pendingPayCount);
        summary.setPayingCount(payingCount);
        summary.setPaidCount(paidCount);
        summary.setFailedCount(failedCount);
        summary.setCheckFailedCount(checkFailedCount);
        return summary;
    }

    @Override
    public YgbSalaryDetail selectSalaryDetailById(Long detailId)
    {
        return salaryDetailMapper.selectSalaryDetailById(detailId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalaryDetail(YgbSalaryDetail salaryDetail)
    {
        YgbSalaryDetail current = salaryDetailMapper.selectSalaryDetailById(salaryDetail.getDetailId());
        if (StringUtils.isNull(current))
        {
            throw new ServiceException("工资明细不存在。");
        }

        YgbSalaryBatch batch = salaryBatchMapper.selectSalaryBatchById(current.getBatchId());
        if (StringUtils.isNull(batch))
        {
            throw new ServiceException("工资批次不存在。");
        }
        if ("6".equals(batch.getBatchStatus()))
        {
            throw new ServiceException("已发放批次不允许修改工资明细。");
        }

        BigDecimal payableAmount = salaryDetail.getPayableAmount() == null ? current.getPayableAmount()
            : salaryDetail.getPayableAmount();
        BigDecimal deductionAmount = salaryDetail.getDeductionAmount() == null ? current.getDeductionAmount()
            : salaryDetail.getDeductionAmount();
        if (payableAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("工资金额不能为负数。");
        }
        if (deductionAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("扣减金额不能为负数。");
        }
        if (deductionAmount.compareTo(payableAmount) > 0)
        {
            throw new ServiceException("扣减金额不能大于应发工资。");
        }

        BigDecimal netAmount = payableAmount.subtract(deductionAmount);
        salaryDetail.setNetAmount(netAmount);
        if ("0".equals(salaryDetail.getAttCheck()))
        {
            salaryDetail.setPayStatus("3");
            salaryDetail.setFailReason("考勤校验未通过。");
            salaryDetail.setNetAmount(BigDecimal.ZERO);
        }
        else if (StringUtils.isEmpty(salaryDetail.getPayStatus()) || "3".equals(current.getPayStatus()))
        {
            salaryDetail.setPayStatus("0");
            if (StringUtils.isEmpty(salaryDetail.getFailReason()))
            {
                salaryDetail.setFailReason("");
            }
        }

        int rows = salaryDetailMapper.updateSalaryDetail(salaryDetail);
        salaryBatchService.refreshBatchAmounts(current.getBatchId(), salaryDetail.getUpdateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalaryDetailByIds(Long[] detailIds, String updateBy)
    {
        Long batchId = null;
        for (Long detailId : detailIds)
        {
            YgbSalaryDetail detail = salaryDetailMapper.selectSalaryDetailById(detailId);
            if (StringUtils.isNotNull(detail))
            {
                batchId = detail.getBatchId();
                YgbSalaryBatch batch = salaryBatchMapper.selectSalaryBatchById(detail.getBatchId());
                if (StringUtils.isNotNull(batch) && "6".equals(batch.getBatchStatus()))
                {
                    throw new ServiceException("已发放批次不允许删除工资明细。");
                }
            }
        }

        int rows = salaryDetailMapper.deleteSalaryDetailByIds(detailIds, updateBy);
        if (batchId != null)
        {
            salaryBatchService.refreshBatchAmounts(batchId, updateBy);
        }
        return rows;
    }
}
