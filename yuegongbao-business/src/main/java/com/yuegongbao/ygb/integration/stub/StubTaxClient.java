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
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.domain.vo.YgbTaxRecordStubItem;
import com.yuegongbao.ygb.integration.TaxClient;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;

/**
 * 税务接口 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubTaxClient extends AbstractYgbStubClient implements TaxClient
{
    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Override
    public List<YgbTaxRecordStubItem> pullMonthlyRecords(String statMonth)
    {
        YgbPerson query = new YgbPerson();
        query.setEmploymentStatus("0");
        return personMapper.selectPersonList(query).stream().map(person -> buildRecord(statMonth, person))
            .collect(Collectors.toList());
    }

    private YgbTaxRecordStubItem buildRecord(String statMonth, YgbPerson person)
    {
        YgbTaxRecordStubItem item = new YgbTaxRecordStubItem();
        item.setEnterpriseId(person.getEnterpriseId());
        item.setEnterpriseName(person.getEnterpriseName());
        item.setPersonId(person.getPersonId());
        item.setPersonName(person.getPersonName());
        item.setIdCard(person.getIdCard());
        item.setRegionCode(person.getRegionCode());
        BigDecimal salary = salaryDetailMapper.selectLatestNetAmountByPerson(person.getPersonId(), statMonth);
        if (salary == null || BigDecimal.ZERO.compareTo(salary) >= 0)
        {
            salary = new BigDecimal("6000.00");
        }
        BigDecimal declared = salary.multiply(person.getPersonId() % 3 == 0 ? new BigDecimal("0.85") : new BigDecimal("1.02"))
            .setScale(2, RoundingMode.HALF_UP);
        item.setDeclaredAmount(declared);
        Map<String, Object> payload = new HashMap<>();
        payload.put("statMonth", statMonth);
        payload.put("personId", person.getPersonId());
        fillStubMeta(item, "SUCCESS", "个税申报 Stub 拉取成功", payload);
        return item;
    }
}
