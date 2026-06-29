package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialBaseCompareMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbSocialBaseCompareServiceImplTest
{
    @Mock
    private YgbSocialBaseCompareMapper socialBaseCompareMapper;

    @Mock
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Mock
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Mock
    private IYgbWarningService warningService;

    @Mock
    private YgbRegionScopeHelper regionScopeHelper;

    @Mock
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @InjectMocks
    private YgbSocialBaseCompareServiceImpl service;

    @Test
    void comparePersistsAbnormalRowsAndCreatesWarning()
    {
        YgbSocialPayment normal = payment(1L, "P1", new BigDecimal("5000"));
        YgbSocialPayment abnormal = payment(2L, "P2", new BigDecimal("5000"));
        when(socialPaymentMapper.selectSocialPaymentList(any(YgbSocialPayment.class)))
            .thenReturn(List.of(normal, abnormal));
        when(salaryDetailMapper.selectLatestNetAmountByPerson(1L, "2026-05"))
            .thenReturn(new BigDecimal("6000"));
        when(salaryDetailMapper.selectLatestNetAmountByPerson(2L, "2026-05"))
            .thenReturn(new BigDecimal("6500"));

        int rows = service.compare("2026-05", 10L, "tester");

        assertEquals(2, rows);
        ArgumentCaptor<YgbSocialBaseCompare> deleteScopeCaptor = ArgumentCaptor.forClass(YgbSocialBaseCompare.class);
        verify(socialBaseCompareMapper).deleteByScope(deleteScopeCaptor.capture());
        assertEquals("2026-05", deleteScopeCaptor.getValue().getStatMonth());
        assertEquals(Long.valueOf(10L), deleteScopeCaptor.getValue().getEnterpriseId());

        ArgumentCaptor<YgbSocialBaseCompare> compareCaptor = ArgumentCaptor.forClass(YgbSocialBaseCompare.class);
        verify(socialBaseCompareMapper, times(2)).insertSocialBaseCompare(compareCaptor.capture());
        List<YgbSocialBaseCompare> compares = compareCaptor.getAllValues();

        assertEquals(new BigDecimal("20.00"), compares.get(0).getDiffRatio());
        assertEquals("1", compares.get(0).getCompareResult());
        assertEquals("0", compares.get(0).getWarningStatus());

        assertEquals(new BigDecimal("30.00"), compares.get(1).getDiffRatio());
        assertEquals("2", compares.get(1).getCompareResult());
        assertEquals("1", compares.get(1).getWarningStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("SOCIAL_BASE_COMPARE", warningCaptor.getValue().getWarnType());
        assertEquals(Long.valueOf(2L), warningCaptor.getValue().getTargetObjectId());
        assertTrue(warningCaptor.getValue().getContent().contains("P2"));
    }

    @Test
    void compareRejectsInvalidMonth()
    {
        assertThrows(ServiceException.class, () -> service.compare("202605", 10L, "tester"));

        verifyNoInteractions(socialPaymentMapper, socialBaseCompareMapper, salaryDetailMapper, warningService);
    }

    private YgbSocialPayment payment(Long personId, String personName, BigDecimal baseAmount)
    {
        YgbSocialPayment payment = new YgbSocialPayment();
        payment.setEnterpriseId(10L);
        payment.setEnterpriseName("ENT-10");
        payment.setPersonId(personId);
        payment.setPersonName(personName);
        payment.setIdCard("ID-" + personId);
        payment.setRegionCode("440100");
        payment.setBaseAmount(baseAmount);
        return payment;
    }
}
