package com.yuegongbao.ygb.compliance.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryArrears;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryArrearsSummary;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryArrearsMapper;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryArrearsService;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbSalaryArrearsServiceImpl implements IYgbSalaryArrearsService
{
    @Autowired
    private YgbSalaryArrearsMapper salaryArrearsMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Override
    public List<YgbSalaryArrears> selectSalaryArrearsList(YgbSalaryArrears arrears)
    {
        return salaryArrearsMapper.selectSalaryArrearsList(arrears);
    }

    @Override
    public YgbSalaryArrearsSummary selectSalaryArrearsSummary(YgbSalaryArrears arrears)
    {
        List<YgbSalaryArrears> list = selectSalaryArrearsList(arrears);
        YgbSalaryArrearsSummary summary = new YgbSalaryArrearsSummary();
        BigDecimal totalArrearsAmount = BigDecimal.ZERO;
        int unhandledCount = 0;
        int processingCount = 0;
        int handledCount = 0;
        int overdueCount = 0;
        for (YgbSalaryArrears item : list)
        {
            totalArrearsAmount = totalArrearsAmount.add(item.getArrearsAmount() == null ? BigDecimal.ZERO : item.getArrearsAmount());
            String handleStatus = item.getHandleStatus();
            if ("pending".equals(handleStatus) || "0".equals(handleStatus))
            {
                unhandledCount++;
            }
            else if ("processing".equals(handleStatus) || "1".equals(handleStatus))
            {
                processingCount++;
            }
            else if ("closed".equals(handleStatus) || "handled".equals(handleStatus) || "2".equals(handleStatus))
            {
                handledCount++;
            }
            if (item.getOverdueDays() != null && item.getOverdueDays() > 0)
            {
                overdueCount++;
            }
        }
        summary.setTotalArrears(list.size());
        summary.setTotalArrearsAmount(totalArrearsAmount);
        summary.setUnhandledCount(unhandledCount);
        summary.setProcessingCount(processingCount);
        summary.setHandledCount(handledCount);
        summary.setOverdueCount(overdueCount);
        return summary;
    }

    @Override
    public YgbSalaryArrears selectSalaryArrearsByBatchId(Long batchId)
    {
        return salaryArrearsMapper.selectSalaryArrearsByBatchId(batchId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleSalaryArrears(YgbSalaryArrears arrears, String operator)
    {
        if (arrears == null || arrears.getBatchId() == null)
        {
            throw new ServiceException("拖欠批次不能为空");
        }
        YgbSalaryArrears current = salaryArrearsMapper.selectSalaryArrearsByBatchId(arrears.getBatchId());
        if (current == null)
        {
            throw new ServiceException("拖欠对象不存在");
        }
        arrears.setArrearsId(current.getArrearsId());
        arrears.setBatchNo(current.getBatchNo());
        arrears.setDispatchEnterpriseId(current.getDispatchEnterpriseId());
        arrears.setDispatchEnterpriseName(current.getDispatchEnterpriseName());
        arrears.setRegionCode(current.getRegionCode());
        arrears.setFollowUser(operator);
        arrears.setFollowTime(new Date());
        if (StringUtils.isEmpty(arrears.getHandleStatus()))
        {
            arrears.setHandleStatus("processing");
        }
        if (current.getWarningId() == null && calculateRisk(current).compareTo(BigDecimal.ZERO) > 0)
        {
            YgbWarningCreateRequest request = new YgbWarningCreateRequest();
            request.setWarnLevel(current.getOverdueDays() != null && current.getOverdueDays() > 15 ? "3" : "2");
            request.setWarnType("SALARY_ARREARS");
            request.setSourceModule("SALARY");
            request.setTargetObjectId(current.getBatchId());
            request.setTargetType("1");
            request.setEnterpriseId(current.getDispatchEnterpriseId());
            request.setEnterpriseName(current.getDispatchEnterpriseName());
            request.setRegionCode(current.getRegionCode());
            request.setContent("工资批次" + current.getBatchNo() + "存在拖欠风险，请尽快处置");
            Long warningId = warningService.createWarningIfAbsent(request, operator);
            arrears.setWarningId(warningId);
        }
        else
        {
            arrears.setWarningId(current.getWarningId());
        }
        return salaryArrearsMapper.insertOrUpdateHandle(arrears);
    }

    @Override
    public Integer countArrearsEnterprise(String regionCode, String statMonth)
    {
        return salaryArrearsMapper.countArrearsEnterprise(regionCode, statMonth);
    }

    private BigDecimal calculateRisk(YgbSalaryArrears current)
    {
        BigDecimal arrearsAmount = current.getArrearsAmount() == null ? BigDecimal.ZERO : current.getArrearsAmount();
        BigDecimal accountGap = current.getAccountGapAmount() == null ? BigDecimal.ZERO : current.getAccountGapAmount();
        return arrearsAmount.max(accountGap);
    }
}
