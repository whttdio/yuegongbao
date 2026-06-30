package com.yuegongbao.ygb.extension.service.impl;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.mapper.YgbModuleRecordMapper;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbModuleRecordServiceImplTest
{
    @Mock
    private YgbModuleRecordMapper moduleRecordMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private YgbPersonMapper personMapper;

    @Mock
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Mock
    private YgbRegionScopeHelper regionScopeHelper;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @InjectMocks
    private YgbModuleRecordServiceImpl service;

    @Test
    void selectModuleRecordListAppliesRegionAndEnterpriseScope()
    {
        doAnswer(invocation -> {
            YgbModuleRecord query = invocation.getArgument(0);
            query.getParams().put("regionDataScope", " AND region_code LIKE '4401%' ");
            return null;
        }).when(regionScopeHelper).applyRegionDataScope(any(YgbModuleRecord.class), eq("region_code"));
        doAnswer(invocation -> {
            YgbModuleRecord query = invocation.getArgument(0);
            query.getParams().put("enterpriseDataScope", " AND enterprise_id = 1001 ");
            return null;
        }).when(enterpriseScopeHelper).applyEnterpriseDataScope(any(YgbModuleRecord.class),
            eq(EnterpriseScopeMode.SINGLE), eq("enterprise_id"));
        when(moduleRecordMapper.selectModuleRecordList(any(YgbModuleRecord.class))).thenReturn(List.of());

        service.selectModuleRecordList(new YgbModuleRecord());

        ArgumentCaptor<YgbModuleRecord> queryCaptor = ArgumentCaptor.forClass(YgbModuleRecord.class);
        verify(moduleRecordMapper).selectModuleRecordList(queryCaptor.capture());
        assertEquals(" AND region_code LIKE '4401%' ", queryCaptor.getValue().getParams().get("regionDataScope"));
        assertEquals(" AND enterprise_id = 1001 ", queryCaptor.getValue().getParams().get("enterpriseDataScope"));
    }

    @Test
    void insertModuleRecordBackfillsEnterpriseAndPersonSnapshot()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise(1001L, "测试企业", "440100"));
        when(personMapper.selectPersonById(2001L)).thenReturn(person(2001L, 1001L, "测试人员"));

        YgbModuleRecord record = baseRecord();
        record.setEnterpriseId(1001L);
        record.setPersonId(2001L);

        service.insertModuleRecord(record, "tester");

        ArgumentCaptor<YgbModuleRecord> recordCaptor = ArgumentCaptor.forClass(YgbModuleRecord.class);
        verify(moduleRecordMapper).insertModuleRecord(recordCaptor.capture());
        assertEquals("测试企业", recordCaptor.getValue().getEnterpriseName());
        assertEquals("440100", recordCaptor.getValue().getRegionCode());
        assertEquals("测试人员", recordCaptor.getValue().getPersonName());
    }

    @Test
    void insertModuleRecordAllowsNoEnterpriseWithAuthorizedRegion()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(regionScopeHelper.resolveAuthorizedRegionCode(null)).thenReturn("440100");

        YgbModuleRecord record = baseRecord();
        record.setEnterpriseId(null);
        record.setRegionCode(null);

        service.insertModuleRecord(record, "tester");

        ArgumentCaptor<YgbModuleRecord> recordCaptor = ArgumentCaptor.forClass(YgbModuleRecord.class);
        verify(moduleRecordMapper).insertModuleRecord(recordCaptor.capture());
        assertEquals("440100", recordCaptor.getValue().getRegionCode());
    }

    @Test
    void insertModuleRecordRejectsInvalidStatusMonthSortAndJson()
    {
        YgbModuleRecord invalidMonth = baseRecord();
        invalidMonth.setStatMonth("202606");
        assertThrows(ServiceException.class, () -> service.insertModuleRecord(invalidMonth, "tester"));

        YgbModuleRecord invalidStatus = baseRecord();
        invalidStatus.setWorkflowStatus("finished");
        assertThrows(ServiceException.class, () -> service.insertModuleRecord(invalidStatus, "tester"));

        YgbModuleRecord invalidSort = baseRecord();
        invalidSort.setSortOrder(-1);
        assertThrows(ServiceException.class, () -> service.insertModuleRecord(invalidSort, "tester"));

        YgbModuleRecord invalidJson = baseRecord();
        invalidJson.setPayloadJson("{bad json");
        assertThrows(ServiceException.class, () -> service.insertModuleRecord(invalidJson, "tester"));

        verify(moduleRecordMapper, never()).insertModuleRecord(any());
    }

    @Test
    void insertModuleRecordRejectsPersonFromAnotherEnterprise()
    {
        when(enterpriseScopeHelper.isEnterpriseScopedUser()).thenReturn(false);
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise(1001L, "测试企业", "440100"));
        when(personMapper.selectPersonById(2001L)).thenReturn(person(2001L, 1002L, "错企人员"));

        YgbModuleRecord record = baseRecord();
        record.setEnterpriseId(1001L);
        record.setPersonId(2001L);

        assertThrows(ServiceException.class, () -> service.insertModuleRecord(record, "tester"));
        verify(moduleRecordMapper, never()).insertModuleRecord(any());
    }

    private YgbModuleRecord baseRecord()
    {
        YgbModuleRecord record = new YgbModuleRecord();
        record.setRecordType("NEWFORM_TRAINING");
        record.setRecordName("培训计划");
        record.setStatMonth("2026-06");
        record.setWorkflowStatus("pending");
        record.setStatus("0");
        record.setSortOrder(0);
        record.setPayloadJson("{\"channel\":\"pc\"}");
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
