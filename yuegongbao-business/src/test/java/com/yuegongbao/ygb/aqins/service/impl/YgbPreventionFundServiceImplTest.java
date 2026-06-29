package com.yuegongbao.ygb.aqins.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.mapper.YgbAqInsuranceMapper;
import com.yuegongbao.ygb.aqins.mapper.YgbPreventionFundMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbPreventionFundServiceImplTest
{
    @Mock
    private YgbPreventionFundMapper preventionFundMapper;

    @Mock
    private YgbAqInsuranceMapper aqInsuranceMapper;

    @Mock
    private IYgbWarningService warningService;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @InjectMocks
    private YgbPreventionFundServiceImpl service;

    @Test
    void updateFundRecalculatesRemainingAndTriggersLowBalanceWarning()
    {
        YgbPreventionFund current = new YgbPreventionFund();
        current.setFundId(97001L);
        current.setPolicyId(96001L);
        current.setEnterpriseId(1002L);
        current.setEnterpriseName("深圳鹏城机电工程有限公司");
        current.setRegionCode("440305");
        current.setAccruedAmount(new BigDecimal("180.00"));
        current.setUsedAmount(BigDecimal.ZERO);
        current.setRemainingAmount(new BigDecimal("180.00"));

        YgbAqInsurance policy = new YgbAqInsurance();
        policy.setPolicyId(96001L);

        when(preventionFundMapper.selectPreventionFundById(97001L)).thenReturn(current);
        when(aqInsuranceMapper.selectAqInsuranceById(96001L)).thenReturn(policy);
        when(preventionFundMapper.updatePreventionFund(any(YgbPreventionFund.class))).thenReturn(1);

        YgbPreventionFund update = new YgbPreventionFund();
        update.setFundId(97001L);
        update.setUsedAmount(new BigDecimal("170.00"));
        update.setUsagePurpose("焊工安全培训");
        update.setEvidenceUrl("stub://fund/train");
        update.setLastSettleTime(new Date());

        int rows = service.updatePreventionFund(update, "tester");

        assertEquals(1, rows);
        verify(aqInsuranceMapper).updateFundAmounts(96001L, new BigDecimal("170.00"), new BigDecimal("10.00"), "tester");
        verify(warningService).createWarningIfAbsent(any(), eq("tester"));
    }
}
