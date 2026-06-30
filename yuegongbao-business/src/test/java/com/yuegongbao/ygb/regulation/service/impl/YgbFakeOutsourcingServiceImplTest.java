package com.yuegongbao.ygb.regulation.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.domain.vo.YgbFakeOutsourcingAnalyzeRequest;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord;
import com.yuegongbao.ygb.regulation.mapper.YgbFakeOutsourcingRecordMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbFakeOutsourcingServiceImplTest
{
    @Mock
    private YgbFakeOutsourcingRecordMapper fakeOutsourcingRecordMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private IYgbWarningService warningService;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @InjectMocks
    private YgbFakeOutsourcingServiceImpl service;

    @Test
    void analyzePersistsSuspectedRecordAndCreatesWarning()
    {
        YgbEnterprise enterprise = new YgbEnterprise();
        enterprise.setEnterpriseId(1001L);
        enterprise.setEnterpriseName("ENT-1001");
        enterprise.setRegionCode("440100");
        when(enterpriseMapper.selectEnterpriseById(1001L)).thenReturn(enterprise);

        YgbFakeOutsourcingAnalyzeRequest request = new YgbFakeOutsourcingAnalyzeRequest();
        request.setStatMonth("2026-06");
        request.setEnterpriseId(1001L);
        request.setAttendanceScore(40);
        request.setScheduleScore(45);
        request.setRewardScore(50);
        request.setTrainingScore(55);

        service.analyze(request, "tester");

        verify(dataScopeGuard).assertEntityAllowed(enterprise);

        ArgumentCaptor<YgbFakeOutsourcingRecord> recordCaptor =
            ArgumentCaptor.forClass(YgbFakeOutsourcingRecord.class);
        verify(fakeOutsourcingRecordMapper).insertFakeOutsourcingRecord(recordCaptor.capture());
        YgbFakeOutsourcingRecord record = recordCaptor.getValue();
        assertEquals("2026-06", record.getStatMonth());
        assertEquals(Long.valueOf(1001L), record.getEnterpriseId());
        assertEquals("1", record.getSuspectedFlag());
        assertEquals("1", record.getWarningStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor =
            ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("FAKE_OUTSOURCING", warningCaptor.getValue().getWarnType());
        assertEquals(Long.valueOf(1001L), warningCaptor.getValue().getEnterpriseId());
    }
}
