package com.yuegongbao.ygb.integration.stub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.domain.vo.YgbInsuranceCheckResult;
import com.yuegongbao.ygb.domain.vo.YgbSocialPaymentStubItem;
import com.yuegongbao.ygb.integration.SocialClient;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;

/**
 * 社保接口 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubSocialClient extends AbstractYgbStubClient implements SocialClient
{
    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Override
    public YgbInsuranceCheckResult checkInsurance(String idCard)
    {
        YgbInsuranceCheckResult response = new YgbInsuranceCheckResult();
        boolean insured = StringUtils.isNotEmpty(idCard) && !idCard.endsWith("8");
        response.setInsured(insured);
        response.setBaseAmount(insured ? new BigDecimal("5200.00") : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        Map<String, Object> payload = new HashMap<>();
        payload.put("idCard", idCard);
        fillStubMeta(response, insured ? "SUCCESS" : "FAIL", insured ? "社保参保正常" : "未查询到参保记录", payload);
        return response;
    }

    @Override
    public List<YgbSocialPaymentStubItem> pullMonthlyPayments(String statMonth)
    {
        YgbPerson query = new YgbPerson();
        query.setEmploymentStatus("0");
        return personMapper.selectPersonList(query).stream().map(person -> buildPaymentItem(statMonth, person))
            .collect(Collectors.toList());
    }

    private YgbSocialPaymentStubItem buildPaymentItem(String statMonth, YgbPerson person)
    {
        YgbSocialPaymentStubItem item = new YgbSocialPaymentStubItem();
        item.setEnterpriseId(person.getEnterpriseId());
        item.setEnterpriseName(person.getEnterpriseName());
        item.setPersonId(person.getPersonId());
        item.setPersonName(person.getPersonName());
        item.setIdCard(person.getIdCard());
        item.setRegionCode(person.getRegionCode());
        BigDecimal salary = salaryDetailMapper.selectLatestNetAmountByPerson(person.getPersonId(), statMonth);
        if (salary == null || BigDecimal.ZERO.compareTo(salary) >= 0)
        {
            salary = new BigDecimal("5500.00");
        }
        BigDecimal baseAmount = salary.multiply(person.getPersonId() % 2 == 0 ? new BigDecimal("0.95") : new BigDecimal("0.70"))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal personalAmount = baseAmount.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal companyAmount = baseAmount.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal paidAmount = personalAmount.add(companyAmount).setScale(2, RoundingMode.HALF_UP);
        item.setBaseAmount(baseAmount);
        item.setPaidAmount(paidAmount);
        item.setPaymentStatus("1");
        Map<String, Object> payload = new HashMap<>();
        payload.put("statMonth", statMonth);
        payload.put("personId", person.getPersonId());
        payload.put("baseAmount", baseAmount);
        payload.put("personalAmount", personalAmount);
        payload.put("companyAmount", companyAmount);
        payload.put("paidAmount", paidAmount);
        fillStubMeta(item, "SUCCESS", "社保缴费 Stub 拉取成功", payload);
        return item;
    }
}
