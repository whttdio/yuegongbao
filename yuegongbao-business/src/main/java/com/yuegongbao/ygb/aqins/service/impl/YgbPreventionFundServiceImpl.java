package com.yuegongbao.ygb.aqins.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFundSummary;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.aqins.mapper.YgbPreventionFundMapper;
import com.yuegongbao.ygb.aqins.service.IYgbPreventionFundService;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbPreventionFundServiceImpl implements IYgbPreventionFundService
{
    @Autowired
    private YgbPreventionFundMapper preventionFundMapper;

    @Autowired
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Override
    public List<YgbPreventionFund> selectPreventionFundList(YgbPreventionFund preventionFund)
    {
        return preventionFundMapper.selectPreventionFundList(preventionFund);
    }

    @Override
    public YgbPreventionFundSummary selectPreventionFundSummary(YgbPreventionFund preventionFund)
    {
        List<YgbPreventionFund> list = selectPreventionFundList(preventionFund);
        YgbPreventionFundSummary summary = new YgbPreventionFundSummary();
        summary.setTotalCount(list.size());

        BigDecimal remainingAmountTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        int lowBalanceCount = 0;
        int inUseCount = 0;
        int nonStubCount = 0;
        int missingEvidenceCount = 0;
        for (YgbPreventionFund item : list)
        {
            remainingAmountTotal = remainingAmountTotal.add(defaultAmount(item.getRemainingAmount()));
            if (isLowBalance(item.getAccruedAmount(), item.getRemainingAmount()))
            {
                lowBalanceCount++;
            }
            if ("2".equals(item.getFundStatus()))
            {
                inUseCount++;
            }
            String sourceMode = StringUtils.trimToEmpty(item.getSourceMode()).toLowerCase();
            if (StringUtils.isNotEmpty(sourceMode) && !sourceMode.contains("stub"))
            {
                nonStubCount++;
            }
            if (StringUtils.isEmpty(item.getEvidenceUrl()))
            {
                missingEvidenceCount++;
            }
        }

        summary.setRemainingAmountTotal(remainingAmountTotal);
        summary.setLowBalanceCount(lowBalanceCount);
        summary.setInUseCount(inUseCount);
        summary.setNonStubCount(nonStubCount);
        summary.setMissingEvidenceCount(missingEvidenceCount);
        summary.setYgbExplanation(buildYgbExplanation(preventionFund, summary));
        summary.setAzbExplanation(buildAzbExplanation(preventionFund, summary));
        return summary;
    }

    @Override
    public YgbPreventionFund selectPreventionFundById(Long fundId)
    {
        return preventionFundMapper.selectPreventionFundById(fundId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePreventionFund(YgbPreventionFund preventionFund, String operator)
    {
        YgbPreventionFund current = preventionFundMapper.selectPreventionFundById(preventionFund.getFundId());
        if (current == null)
        {
            throw new ServiceException("预防资金池记录不存在。");
        }
        YgbAqInsurance policy = aqInsuranceMapper.selectAqInsuranceById(current.getPolicyId());
        if (policy == null)
        {
            throw new ServiceException("关联安责险保单不存在。");
        }

        BigDecimal accruedAmount = current.getAccruedAmount() == null ? BigDecimal.ZERO : current.getAccruedAmount();
        BigDecimal usedAmount = preventionFund.getUsedAmount() == null ? BigDecimal.ZERO : preventionFund.getUsedAmount();
        if (usedAmount.compareTo(accruedAmount) > 0)
        {
            throw new ServiceException("已使用金额不能大于计提金额。");
        }

        BigDecimal remainingAmount = accruedAmount.subtract(usedAmount).setScale(2, RoundingMode.HALF_UP);
        current.setUsedAmount(usedAmount.setScale(2, RoundingMode.HALF_UP));
        current.setRemainingAmount(remainingAmount);
        current.setFundStatus(YgbAqInsuranceServiceImpl.resolveFundStatus(current.getUsedAmount(), remainingAmount));
        current.setUsagePurpose(preventionFund.getUsagePurpose());
        current.setEvidenceUrl(preventionFund.getEvidenceUrl());
        current.setLastSettleTime(preventionFund.getLastSettleTime() == null ? new Date() : preventionFund.getLastSettleTime());
        current.setUpdateBy(operator);

        int rows = preventionFundMapper.updatePreventionFund(current);
        aqInsuranceMapper.updateFundAmounts(current.getPolicyId(), current.getUsedAmount(), current.getRemainingAmount(), operator);

        if (accruedAmount.compareTo(BigDecimal.ZERO) > 0
            && current.getRemainingAmount().divide(accruedAmount, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.10")) <= 0)
        {
            YgbWarningCreateRequest request = new YgbWarningCreateRequest();
            request.setWarnLevel("2");
            request.setWarnType("AQ_PREVENTION_FUND_LOW");
            request.setSourceModule("AQINS");
            request.setTargetObjectId(current.getEnterpriseId());
            request.setTargetType("1");
            request.setEnterpriseId(current.getEnterpriseId());
            request.setEnterpriseName(current.getEnterpriseName());
            request.setRegionCode(current.getRegionCode());
            request.setContent("安责险事故预防资金余额偏低，企业：" + current.getEnterpriseName()
                + "，可用余额：" + current.getRemainingAmount());
            warningService.createWarningIfAbsent(request, operator);
        }
        return rows;
    }

    private BigDecimal defaultAmount(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbPreventionFund query, YgbPreventionFundSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("lowBalance", "低余额预防费", summary.getLowBalanceCount(), 0,
            "低余额资金池应优先回看月度使用、留痕和企业后续预防服务安排，避免资金链条中断。", "preventionFund",
            "preventionFund", "530.1 预防费办理解释", baseQuery));
        list.add(explanationItem("inUse", "使用中资金池", summary.getInUseCount(), 0,
            "使用中的资金池需要补齐用途、结算时间和证据附件，确保办理链可回写可归档。", "preventionFund",
            "preventionFund", "530.1 预防费办理解释", baseQuery));
        list.add(explanationItem("missingEvidence", "缺少凭证记录", summary.getMissingEvidenceCount(), 0,
            "缺凭证会直接削弱资金使用可追溯性，应优先补齐证据链。", "preventionFund", "preventionFund",
            "530.1 预防费办理解释", baseQuery));
        list.add(explanationItem("nonStub", "非 Stub 来源记录", summary.getNonStubCount(), 0,
            "非 Stub 来源记录适合作为办理链回写复核入口，确保数据来源与资金状态一致。", "preventionFund",
            "preventionFund", "530.1 预防费办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbPreventionFund query, YgbPreventionFundSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("lowBalance", "低余额协同风险", summary.getLowBalanceCount(), 0,
            "低余额意味着后续事故预防和治理服务投入空间受限，应优先进入监管复核链路。", "preventionFund",
            "preventionFund", "6.1 预防费治理解释", baseQuery));
        list.add(explanationItem("inUse", "使用中资金池", summary.getInUseCount(), 0,
            "使用中的资金池适合作为治理协同跟踪入口，复核资金是否真正转化为治理动作。", "preventionFund",
            "preventionFund", "6.1 预防费治理解释", baseQuery));
        list.add(explanationItem("missingEvidence", "证据缺口", summary.getMissingEvidenceCount(), 0,
            "证据缺口会削弱区域治理可追溯性，应优先补强留痕。", "preventionFund", "preventionFund",
            "6.1 预防费治理解释", baseQuery));
        list.add(explanationItem("nonStub", "协同来源记录", summary.getNonStubCount(), 0,
            "协同来源记录可用于判断资金池是否已进入真实治理链，不应停留在台账层。", "preventionFund",
            "preventionFund", "6.1 预防费治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbPreventionFund query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getStatMonth()))
        {
            map.put("statMonth", query.getStatMonth());
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getEnterpriseName()))
        {
            map.put("enterpriseName", query.getEnterpriseName());
        }
        if (StringUtils.isNotEmpty(query.getFundStatus()))
        {
            map.put("fundStatus", query.getFundStatus());
        }
        return map;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> baseQuery)
    {
        Map<String, Object> defaultQuery = new LinkedHashMap<>(baseQuery);
        defaultQuery.put("focusKey", focusKey);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", focusKey);
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }

    private boolean isLowBalance(BigDecimal accruedAmount, BigDecimal remainingAmount)
    {
        BigDecimal accrued = accruedAmount == null ? BigDecimal.ZERO : accruedAmount;
        BigDecimal remaining = remainingAmount == null ? BigDecimal.ZERO : remainingAmount;
        if (accrued.compareTo(BigDecimal.ZERO) <= 0)
        {
            return false;
        }
        return remaining.divide(accrued, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.10")) <= 0;
    }
}
