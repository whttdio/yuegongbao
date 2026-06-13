package com.yuegongbao.ygb.regulation.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.domain.vo.YgbTaxRecordStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.TaxClient;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbTaxCompareMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbTaxCompareServiceImplTest
{
    @Mock
    private YgbTaxCompareMapper taxCompareMapper;

    @Mock
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Mock
    private TaxClient taxClient;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbTaxCompareServiceImpl service;

    @Test
    void syncTaxCompareFiltersByEnterpriseAndPersistsStubMeta()
    {
        YgbTaxRecordStubItem matched = record(10L, 1L, "P1", new BigDecimal("4000"), "SER-1");
        YgbTaxRecordStubItem ignored = record(20L, 2L, "P2", new BigDecimal("3000"), "SER-2");
        when(taxClient.pullMonthlyRecords("2026-05")).thenReturn(List.of(matched, ignored));
        when(salaryDetailMapper.selectLatestNetAmountByPerson(1L, "2026-05"))
            .thenReturn(new BigDecimal("5000"));

        int rows = service.syncTaxCompare("2026-05", 10L, "tester");

        assertEquals(1, rows);
        verify(taxCompareMapper).deleteByScope("2026-05", 10L);
        verify(salaryDetailMapper, never()).selectLatestNetAmountByPerson(2L, "2026-05");

        ArgumentCaptor<YgbTaxCompare> compareCaptor = ArgumentCaptor.forClass(YgbTaxCompare.class);
        verify(taxCompareMapper).insertTaxCompare(compareCaptor.capture());
        YgbTaxCompare compare = compareCaptor.getValue();
        assertEquals("2", compare.getCompareResult());
        assertEquals("1", compare.getWarningStatus());
        assertEquals(new BigDecimal("25.00"), compare.getDiffRatio());
        assertEquals("SER-1", compare.getSourceSerialNo());
        assertEquals("FAIL", compare.getSourceStatus());
        assertEquals("DECLARED_DIFF", compare.getSourceMessage());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("TAX_COMPARE", warningCaptor.getValue().getWarnType());
        assertEquals(Long.valueOf(1L), warningCaptor.getValue().getTargetObjectId());
        assertTrue(warningCaptor.getValue().getContent().contains("P1"));
    }

    private YgbTaxRecordStubItem record(Long enterpriseId, Long personId, String personName, BigDecimal declaredAmount,
        String serialNo)
    {
        YgbTaxRecordStubItem item = new YgbTaxRecordStubItem();
        item.setEnterpriseId(enterpriseId);
        item.setEnterpriseName("ENT-" + enterpriseId);
        item.setPersonId(personId);
        item.setPersonName(personName);
        item.setIdCard("ID-" + personId);
        item.setRegionCode("440100");
        item.setDeclaredAmount(declaredAmount);
        item.setExternalSerialNo(serialNo);
        item.setSourceStatus("FAIL");
        item.setSourceMessage("DECLARED_DIFF");
        item.setCallbackTime(new Date());
        item.setRawPayload("{\"serial\":\"" + serialNo + "\"}");
        return item;
    }
}
