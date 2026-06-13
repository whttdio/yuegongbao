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
import com.yuegongbao.ygb.domain.vo.YgbNewformWorkerStubItem;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.integration.NewformClient;

/**
 * 新业态平台 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubNewformClient extends AbstractYgbStubClient implements NewformClient
{
    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Override
    public List<YgbNewformWorkerStubItem> pullWorkerRecords(String statMonth)
    {
        YgbPerson query = new YgbPerson();
        query.setEmploymentStatus("0");
        query.setWorkerType("4");
        return personMapper.selectPersonList(query).stream()
            .map(person -> buildItem(statMonth, person))
            .collect(Collectors.toList());
    }

    private YgbNewformWorkerStubItem buildItem(String statMonth, YgbPerson person)
    {
        YgbNewformWorkerStubItem item = new YgbNewformWorkerStubItem();
        item.setEnterpriseId(person.getEnterpriseId());
        item.setEnterpriseName(person.getEnterpriseName());
        item.setPersonId(person.getPersonId());
        item.setPersonName(person.getPersonName());
        item.setIdCard(person.getIdCard());
        item.setRegionCode(person.getRegionCode());
        item.setPlatformName(resolvePlatformName(person.getEnterpriseId()));
        item.setEmploymentType(resolveEmploymentType(person.getJobType()));
        item.setInsuranceStatus(person.getInsuranceStatus());
        item.setInjuryInsuranceStatus(resolveInjuryInsuranceStatus(person));
        item.setMonthlyIncome(resolveIncome(person.getPersonId(), statMonth));

        Map<String, Object> payload = new HashMap<>();
        payload.put("statMonth", statMonth);
        payload.put("personId", person.getPersonId());
        payload.put("platformName", item.getPlatformName());
        fillStubMeta(item, "SUCCESS", "新业态平台 Stub 拉取成功", payload);
        return item;
    }

    private BigDecimal resolveIncome(Long personId, String statMonth)
    {
        BigDecimal income = salaryDetailMapper.selectLatestNetAmountByPerson(personId, statMonth);
        if (income == null || income.compareTo(BigDecimal.ZERO) <= 0)
        {
            income = new BigDecimal("6800.00")
                .add(new BigDecimal(personId % 5).multiply(new BigDecimal("450.00")));
        }
        return income.setScale(2, RoundingMode.HALF_UP);
    }

    private String resolvePlatformName(Long enterpriseId)
    {
        long seed = enterpriseId == null ? 0L : enterpriseId % 3L;
        if (seed == 1L)
        {
            return "粤运配送平台";
        }
        if (seed == 2L)
        {
            return "南粤灵工平台";
        }
        return "湾区众包平台";
    }

    private String resolveEmploymentType(String jobType)
    {
        if (jobType != null && jobType.contains("配送"))
        {
            return "配送骑手";
        }
        if (jobType != null && (jobType.contains("司机") || jobType.contains("驾驶")))
        {
            return "网约司机";
        }
        return "平台零工";
    }

    private String resolveInjuryInsuranceStatus(YgbPerson person)
    {
        if ("1".equals(person.getInsuranceStatus()))
        {
            return "1";
        }
        if ("2".equals(person.getInsuranceStatus()))
        {
            return person.getPersonId() % 2 == 0 ? "2" : "0";
        }
        return person.getPersonId() % 2 == 0 ? "1" : "0";
    }
}
