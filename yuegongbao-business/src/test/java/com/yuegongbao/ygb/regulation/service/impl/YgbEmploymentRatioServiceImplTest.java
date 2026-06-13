package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbEmploymentRatioMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbEmploymentRatioServiceImplTest
{
    @Mock
    private YgbEmploymentRatioMapper employmentRatioMapper;

    @Mock
    private YgbContractMapper contractMapper;

    @Mock
    private YgbPersonMapper personMapper;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbEmploymentRatioServiceImpl service;

    @Test
    void calculateCountsOnlyActiveDispatchAndFormalWorkers()
    {
        when(contractMapper.selectContractList(any(YgbContract.class))).thenReturn(List.of(
            contract(1L, "2026-01-01", "2026-12-31"),
            contract(2L, "2026-01-01", "2026-12-31"),
            contract(3L, "2026-01-01", "2026-12-31"),
            contract(4L, "2026-01-01", "2026-12-31"),
            contract(5L, "2026-01-01", "2026-12-31"),
            contract(6L, "2026-01-01", "2026-12-31"),
            contract(7L, "2026-06-01", "2026-12-31"),
            contract(8L, "2026-01-01", "2026-12-31")));
        when(personMapper.selectPersonList(any(YgbPerson.class))).thenReturn(List.of(
            person(1L, "1"),
            person(2L, "2"),
            person(3L, "2"),
            person(4L, "2"),
            person(5L, "2"),
            person(6L, "2"),
            person(7L, "1"),
            person(8L, "3")));

        int rows = service.calculate("2026-05", 10L, "tester");

        assertEquals(1, rows);
        verify(employmentRatioMapper).deleteByScope("2026-05", 10L);

        ArgumentCaptor<YgbEmploymentRatio> ratioCaptor = ArgumentCaptor.forClass(YgbEmploymentRatio.class);
        verify(employmentRatioMapper).insertEmploymentRatio(ratioCaptor.capture());
        YgbEmploymentRatio ratio = ratioCaptor.getValue();
        assertEquals(Integer.valueOf(1), ratio.getDispatchCount());
        assertEquals(Integer.valueOf(5), ratio.getFormalCount());
        assertEquals(new BigDecimal("16.67"), ratio.getRatioValue());
        assertEquals("2", ratio.getWarningLevel());
        assertEquals("1", ratio.getWarningStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("EMPLOYMENT_RATIO", warningCaptor.getValue().getWarnType());
        assertEquals("3", warningCaptor.getValue().getWarnLevel());
        assertEquals(Long.valueOf(10L), warningCaptor.getValue().getTargetObjectId());
    }

    private YgbContract contract(Long personId, String startDate, String endDate)
    {
        YgbContract contract = new YgbContract();
        contract.setEmployerEnterpriseId(10L);
        contract.setEmployerEnterpriseName("ENT-10");
        contract.setRegionCode("440100");
        contract.setPersonId(personId);
        contract.setStartDate(java.util.Date.from(LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault())
            .toInstant()));
        contract.setEndDate(java.util.Date.from(LocalDate.parse(endDate).atStartOfDay(ZoneId.systemDefault())
            .toInstant()));
        return contract;
    }

    private YgbPerson person(Long personId, String workerType)
    {
        YgbPerson person = new YgbPerson();
        person.setPersonId(personId);
        person.setWorkerType(workerType);
        return person;
    }
}
