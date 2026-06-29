package com.yuegongbao.ygb.integration.stub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService;
import com.yuegongbao.ygb.domain.vo.YgbAqInsuranceStubItem;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.integration.AqInsuranceClient;

/**
 * 安责险接口 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubAqInsuranceClient extends AbstractYgbStubClient implements AqInsuranceClient
{
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private IYgbEnterpriseService enterpriseService;

    @Autowired
    private YgbPersonMapper personMapper;

    @Override
    public List<YgbAqInsuranceStubItem> pullMonthlyPolicies(String statMonth)
    {
        YearMonth month = YearMonth.parse(statMonth, MONTH_FORMATTER);
        return enterpriseService.selectEnterpriseOptions().stream()
            .filter(enterprise -> !"4".equals(enterprise.getEnterpriseType()))
            .map(enterprise -> buildPolicy(month, enterprise))
            .collect(Collectors.toList());
    }

    private YgbAqInsuranceStubItem buildPolicy(YearMonth month, YgbEnterprise enterprise)
    {
        YgbPerson query = new YgbPerson();
        query.setEnterpriseId(enterprise.getEnterpriseId());
        query.setEmploymentStatus("0");
        int insuredPersonCount = Math.max(1, personMapper.selectPersonList(query).size());

        int statusSeed = Math.toIntExact(enterprise.getEnterpriseId() % 4);
        String policyStatus = switch (statusSeed)
        {
            case 2 -> "2";
            case 3 -> "3";
            case 0 -> "0";
            default -> "1";
        };

        LocalDate startDate = month.atDay(1).minusMonths(1);
        LocalDate endDate = switch (policyStatus)
        {
            case "0" -> month.atEndOfMonth().plusDays(45);
            case "2" -> month.atEndOfMonth().plusDays(20);
            case "3" -> month.atEndOfMonth().minusDays(10);
            default -> month.atEndOfMonth().plusDays(180);
        };

        BigDecimal premium = new BigDecimal(insuredPersonCount)
            .multiply(new BigDecimal(statusSeed == 3 ? "260.00" : "220.00"))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal preventionFundRatio = switch (policyStatus)
        {
            case "2" -> new BigDecimal("16.00");
            case "3" -> new BigDecimal("12.00");
            default -> new BigDecimal("15.00");
        };

        YgbAqInsuranceStubItem item = new YgbAqInsuranceStubItem();
        item.setEnterpriseId(enterprise.getEnterpriseId());
        item.setEnterpriseName(enterprise.getEnterpriseName());
        item.setRegionCode(enterprise.getRegionCode());
        item.setInsurerName(resolveInsurerName(enterprise.getEnterpriseId()));
        item.setPolicyNo("AQ" + month.format(DateTimeFormatter.ofPattern("yyyyMM")) + enterprise.getEnterpriseId());
        item.setPremium(premium);
        item.setStartDate(java.sql.Date.valueOf(startDate));
        item.setEndDate(java.sql.Date.valueOf(endDate));
        item.setPolicyStatus(policyStatus);
        item.setInsuredPersonCount(insuredPersonCount);
        item.setPreventionFundRatio(preventionFundRatio);
        item.setPreventionFundAmount(premium.multiply(preventionFundRatio)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));

        Map<String, Object> payload = new HashMap<>();
        payload.put("statMonth", month.toString());
        payload.put("enterpriseId", enterprise.getEnterpriseId());
        payload.put("status", policyStatus);
        payload.put("insuredPersonCount", insuredPersonCount);
        payload.put("premium", premium);
        payload.put("preventionFundRatio", preventionFundRatio);
        payload.put("coverageSummary", buildCoverageSummary(policyStatus, insuredPersonCount));
        fillStubMeta(item, "SUCCESS", "安责险保单 Stub 同步成功", payload);
        return item;
    }

    private String buildCoverageSummary(String policyStatus, int insuredPersonCount)
    {
        return switch (policyStatus)
        {
            case "0" -> "保单生效后预计覆盖 " + insuredPersonCount + " 人";
            case "2" -> "当前覆盖 " + insuredPersonCount + " 人，保单即将到期";
            case "3" -> "最近一张保单覆盖 " + insuredPersonCount + " 人，当前已过期";
            default -> "当前覆盖 " + insuredPersonCount + " 人";
        };
    }

    private String resolveInsurerName(Long enterpriseId)
    {
        int seed = Math.toIntExact(enterpriseId % 3);
        return switch (seed)
        {
            case 1 -> "广东人保财险";
            case 2 -> "平安财险广东分公司";
            default -> "太平洋财险广东分公司";
        };
    }
}
