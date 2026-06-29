package com.yuegongbao.ygb.aqins.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.aqins.mapper.YgbPreventionFundMapper;
import com.yuegongbao.ygb.domain.vo.YgbAqInsuranceStubItem;
import com.yuegongbao.ygb.integration.AqInsuranceClient;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbAqInsuranceServiceImplTest
{
    @Mock
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Mock
    private YgbPreventionFundMapper preventionFundMapper;

    @Mock
    private AqInsuranceClient aqInsuranceClient;

    @Mock
    private IYgbWarningService warningService;

    @Mock
    private YgbRegionScopeHelper regionScopeHelper;

    @Mock
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @InjectMocks
    private YgbAqInsuranceServiceImpl service;

    @Test
    void syncCreatesExpiringPolicyAndFundSummary()
    {
        YgbAqInsuranceStubItem item = new YgbAqInsuranceStubItem();
        item.setEnterpriseId(1002L);
        item.setEnterpriseName("深圳鹏城机电工程有限公司");
        item.setRegionCode("440305");
        item.setInsurerName("平安财险广东分公司");
        item.setPolicyNo("AQ2026061002");
        item.setPremium(new BigDecimal("1200.00"));
        item.setStartDate(java.sql.Date.valueOf("2026-05-01"));
        item.setEndDate(java.sql.Date.valueOf("2026-06-20"));
        item.setPolicyStatus("2");
        item.setInsuredPersonCount(6);
        item.setPreventionFundRatio(new BigDecimal("15.00"));
        item.setPreventionFundAmount(new BigDecimal("180.00"));
        item.setExternalSerialNo("SERIAL001");
        item.setSourceStatus("SUCCESS");
        item.setSourceMessage("ok");
        item.setCallbackTime(new Date());
        item.setRawPayload("{}");

        when(aqInsuranceClient.pullMonthlyPolicies("2026-06")).thenReturn(List.of(item));
        when(aqInsuranceMapper.selectAqInsuranceByScope("2026-06", 1002L)).thenReturn(null);
        when(preventionFundMapper.selectPreventionFundByScope(any(), eq("2026-06"))).thenReturn(null);
        doAnswer(invocation -> {
            YgbAqInsurance record = invocation.getArgument(0);
            record.setPolicyId(96001L);
            return 1;
        }).when(aqInsuranceMapper).insertAqInsurance(any(YgbAqInsurance.class));

        int rows = service.syncAqInsurance("2026-06", null, "tester");

        assertEquals(1, rows);
        ArgumentCaptor<YgbPreventionFund> fundCaptor = ArgumentCaptor.forClass(YgbPreventionFund.class);
        verify(preventionFundMapper).insertPreventionFund(fundCaptor.capture());
        assertEquals(new BigDecimal("180.00"), fundCaptor.getValue().getAccruedAmount());
        assertEquals("1", fundCaptor.getValue().getFundStatus());
        verify(warningService).createWarningIfAbsent(any(), eq("tester"));
    }

    @Test
    void syncSkipsOtherEnterpriseWhenEnterpriseFilterProvided()
    {
        YgbAqInsuranceStubItem item = new YgbAqInsuranceStubItem();
        item.setEnterpriseId(1002L);
        when(aqInsuranceClient.pullMonthlyPolicies("2026-06")).thenReturn(List.of(item));

        int rows = service.syncAqInsurance("2026-06", 1001L, "tester");

        assertEquals(0, rows);
        verify(aqInsuranceMapper, never()).insertAqInsurance(any());
    }
}
