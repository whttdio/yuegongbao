package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredList;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbTaxCompareMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbUninsuredListMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbUninsuredListServiceImplTest
{
    @Mock
    private YgbUninsuredListMapper uninsuredListMapper;

    @Mock
    private YgbTaxCompareMapper taxCompareMapper;

    @Mock
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbUninsuredListServiceImpl service;

    @Test
    void generateBuildsOnlyUninsuredRowsFromTaxMinusSocial()
    {
        YgbTaxCompare insured = taxRecord(1L, "P1", new BigDecimal("6000"));
        YgbTaxCompare uninsured = taxRecord(2L, "P2", null);
        when(taxCompareMapper.selectTaxCompareList(any(YgbTaxCompare.class))).thenReturn(List.of(insured, uninsured));
        when(socialPaymentMapper.selectSocialPaymentList(any(YgbSocialPayment.class)))
            .thenReturn(List.of(socialPayment(1L)));

        int rows = service.generate("2026-05", 10L, "tester");

        assertEquals(1, rows);
        verify(uninsuredListMapper).deleteByScope("2026-05", 10L);

        ArgumentCaptor<YgbUninsuredList> uninsuredCaptor = ArgumentCaptor.forClass(YgbUninsuredList.class);
        verify(uninsuredListMapper).insertUninsured(uninsuredCaptor.capture());
        YgbUninsuredList record = uninsuredCaptor.getValue();
        assertEquals("UNINS-202605", record.getBatchNo());
        assertEquals(Long.valueOf(2L), record.getPersonId());
        assertEquals(BigDecimal.ZERO, record.getSalaryAmount());
        assertEquals("0", record.getDisposalStatus());
        assertEquals("1", record.getWarningStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("UNINSURED", warningCaptor.getValue().getWarnType());
        assertEquals(Long.valueOf(2L), warningCaptor.getValue().getTargetObjectId());
        assertTrue(warningCaptor.getValue().getContent().contains("P2"));
    }

    @Test
    void handleUpdatesDisposalStatusAndRemark()
    {
        YgbUninsuredList record = new YgbUninsuredList();
        record.setListId(8L);
        when(uninsuredListMapper.selectUninsuredById(8L)).thenReturn(record);
        when(uninsuredListMapper.updateUninsuredHandle(any(YgbUninsuredList.class))).thenReturn(1);

        int rows = service.handle(8L, "3", "DONE", "tester");

        assertEquals(1, rows);

        ArgumentCaptor<YgbUninsuredList> updateCaptor = ArgumentCaptor.forClass(YgbUninsuredList.class);
        verify(uninsuredListMapper).updateUninsuredHandle(updateCaptor.capture());
        assertEquals("3", updateCaptor.getValue().getDisposalStatus());
        assertEquals("DONE", updateCaptor.getValue().getRemark());
        assertEquals("tester", updateCaptor.getValue().getUpdateBy());
    }

    private YgbTaxCompare taxRecord(Long personId, String personName, BigDecimal salaryAmount)
    {
        YgbTaxCompare record = new YgbTaxCompare();
        record.setEnterpriseId(10L);
        record.setEnterpriseName("ENT-10");
        record.setPersonId(personId);
        record.setPersonName(personName);
        record.setIdCard("ID-" + personId);
        record.setRegionCode("440100");
        record.setSalaryAmount(salaryAmount);
        return record;
    }

    private YgbSocialPayment socialPayment(Long personId)
    {
        YgbSocialPayment payment = new YgbSocialPayment();
        payment.setPersonId(personId);
        return payment;
    }
}
