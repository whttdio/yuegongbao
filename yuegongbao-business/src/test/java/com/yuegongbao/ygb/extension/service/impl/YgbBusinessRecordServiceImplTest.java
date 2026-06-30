package com.yuegongbao.ygb.extension.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.extension.domain.YgbBusinessRecord;
import com.yuegongbao.ygb.extension.mapper.YgbBusinessRecordMapper;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbBusinessRecordServiceImplTest
{
    @Mock
    private YgbBusinessRecordMapper businessRecordMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private YgbPersonMapper personMapper;

    @Mock
    private YgbRegionScopeHelper regionScopeHelper;

    @Mock
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @InjectMocks
    private YgbBusinessRecordServiceImpl service;

    @Test
    void insertBusinessRecordBackfillsEnterpriseAndPersonSnapshot()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise(1001L, "测试企业", "440100"));
        when(personMapper.selectPersonById(2001L)).thenReturn(person(2001L, 1001L, "测试人员"));

        YgbBusinessRecord record = baseRecord();
        record.setEnterpriseId(1001L);
        record.setPersonId(2001L);

        service.insertBusinessRecord("socialSupplement", record, "tester");

        ArgumentCaptor<YgbBusinessRecord> recordCaptor = ArgumentCaptor.forClass(YgbBusinessRecord.class);
        verify(businessRecordMapper).insertBusinessRecord(eq("ygb_br_social_supplement"), recordCaptor.capture());
        assertEquals("socialSupplement", recordCaptor.getValue().getModuleCode());
        assertEquals("测试企业", recordCaptor.getValue().getEnterpriseName());
        assertEquals("440100", recordCaptor.getValue().getRegionCode());
        assertEquals("测试人员", recordCaptor.getValue().getPersonName());
    }

    @Test
    void insertPlatformRecordAllowsNoEnterpriseButResolvesAuthorizedRegion()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(regionScopeHelper.resolveAuthorizedRegionCode(null)).thenReturn("440100");

        YgbBusinessRecord record = baseRecord();
        record.setEnterpriseId(null);
        record.setRegionCode(null);

        service.insertBusinessRecord("platformDocument", record, "tester");

        ArgumentCaptor<YgbBusinessRecord> recordCaptor = ArgumentCaptor.forClass(YgbBusinessRecord.class);
        verify(businessRecordMapper).insertBusinessRecord(eq("ygb_br_platform_document"), recordCaptor.capture());
        assertEquals("440100", recordCaptor.getValue().getRegionCode());
    }

    @Test
    void insertOccupationRecordsRouteToDedicatedTables()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise(1001L, "test enterprise", "440100"));

        YgbBusinessRecord prevention = baseRecord();
        prevention.setBusinessName("occupation prevention");
        service.insertBusinessRecord("occupationPrevention", prevention, "tester");

        YgbBusinessRecord healthArchive = baseRecord();
        healthArchive.setBusinessName("occupation health archive");
        service.insertBusinessRecord("occupationHealthArchive", healthArchive, "tester");

        verify(businessRecordMapper).insertBusinessRecord(eq("ygb_br_occupation_prevention"), eq(prevention));
        verify(businessRecordMapper).insertBusinessRecord(eq("ygb_br_occupation_health_archive"), eq(healthArchive));
        assertEquals("occupationPrevention", prevention.getModuleCode());
        assertEquals("occupationHealthArchive", healthArchive.getModuleCode());
    }

    @Test
    void insertBusinessRecordRejectsInvalidBusinessValues()
    {
        YgbBusinessRecord invalidMonthRecord = baseRecord();
        invalidMonthRecord.setStatMonth("202606");

        assertThrows(ServiceException.class,
            () -> service.insertBusinessRecord("socialSupplement", invalidMonthRecord, "tester"));

        YgbBusinessRecord negativeAmountRecord = baseRecord();
        negativeAmountRecord.setAmount(new BigDecimal("-1.00"));
        assertThrows(ServiceException.class,
            () -> service.insertBusinessRecord("socialSupplement", negativeAmountRecord, "tester"));

        YgbBusinessRecord invalidStatusRecord = baseRecord();
        invalidStatusRecord.setWorkflowStatus("finished");
        assertThrows(ServiceException.class,
            () -> service.insertBusinessRecord("socialSupplement", invalidStatusRecord, "tester"));

        verify(businessRecordMapper, never()).insertBusinessRecord(any(), any());
    }

    @Test
    void insertBusinessRecordRejectsPersonFromAnotherEnterprise()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise(1001L, "测试企业", "440100"));
        when(personMapper.selectPersonById(2001L)).thenReturn(person(2001L, 1002L, "错企人员"));

        YgbBusinessRecord record = baseRecord();
        record.setEnterpriseId(1001L);
        record.setPersonId(2001L);

        assertThrows(ServiceException.class, () -> service.insertBusinessRecord("socialSupplement", record, "tester"));
        verify(businessRecordMapper, never()).insertBusinessRecord(any(), any());
    }

    @Test
    void insertBusinessRecordRejectsEndBeforeStart()
    {
        YgbBusinessRecord record = baseRecord();
        record.setStartTime(new Date(2000L));
        record.setEndTime(new Date(1000L));

        assertThrows(ServiceException.class, () -> service.insertBusinessRecord("socialSupplement", record, "tester"));
        verify(businessRecordMapper, never()).insertBusinessRecord(any(), any());
    }

    private YgbBusinessRecord baseRecord()
    {
        YgbBusinessRecord record = new YgbBusinessRecord();
        record.setBusinessName("补缴跟踪");
        record.setStatMonth("2026-06");
        record.setWorkflowStatus("pending");
        record.setStatus("0");
        record.setRiskLevel("1");
        record.setEnterpriseId(1001L);
        record.setAmount(BigDecimal.ZERO);
        record.setQuantity(BigDecimal.ONE);
        return record;
    }

    private YgbEnterprise enterprise(Long enterpriseId, String enterpriseName, String regionCode)
    {
        YgbEnterprise enterprise = new YgbEnterprise();
        enterprise.setEnterpriseId(enterpriseId);
        enterprise.setEnterpriseName(enterpriseName);
        enterprise.setRegionCode(regionCode);
        return enterprise;
    }

    private YgbPerson person(Long personId, Long enterpriseId, String personName)
    {
        YgbPerson person = new YgbPerson();
        person.setPersonId(personId);
        person.setEnterpriseId(enterpriseId);
        person.setPersonName(personName);
        return person;
    }
}
