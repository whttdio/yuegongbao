package com.yuegongbao.ygb.newform.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.domain.vo.YgbNewformWorkerStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.NewformClient;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.mapper.YgbNewformWorkerMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbNewformWorkerServiceImplTest
{
    @Mock
    private YgbNewformWorkerMapper newformWorkerMapper;

    @Mock
    private NewformClient newformClient;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbNewformWorkerServiceImpl service;

    @Test
    void syncNewformWorkerCreatesWarningWhenInjuryInsuranceMissing()
    {
        YgbNewformWorkerStubItem ok = item(1002L, 10008L, "邓雅琪", "1");
        YgbNewformWorkerStubItem abnormal = item(1002L, 10018L, "陈梓航", "0");
        when(newformClient.pullWorkerRecords("2026-06")).thenReturn(List.of(ok, abnormal));

        int rows = service.syncNewformWorker("2026-06", 1002L, "tester");

        assertEquals(2, rows);
        verify(newformWorkerMapper).deleteByScope("2026-06", 1002L);

        ArgumentCaptor<YgbNewformWorker> captor = ArgumentCaptor.forClass(YgbNewformWorker.class);
        verify(newformWorkerMapper, org.mockito.Mockito.times(2)).insertNewformWorker(captor.capture());
        List<YgbNewformWorker> values = captor.getAllValues();
        assertEquals("0", values.get(0).getWarningStatus());
        assertEquals("1", values.get(1).getWarningStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("NEWFORM_INJURY_INSURANCE", warningCaptor.getValue().getWarnType());
        assertTrue(warningCaptor.getValue().getContent().contains("陈梓航"));
    }

    private YgbNewformWorkerStubItem item(Long enterpriseId, Long personId, String personName, String injuryStatus)
    {
        YgbNewformWorkerStubItem item = new YgbNewformWorkerStubItem();
        item.setEnterpriseId(enterpriseId);
        item.setEnterpriseName("深圳鹏城机电工程有限公司");
        item.setPersonId(personId);
        item.setPersonName(personName);
        item.setIdCard("440305199408080088");
        item.setRegionCode("440305");
        item.setPlatformName("粤运配送平台");
        item.setEmploymentType("配送骑手");
        item.setInsuranceStatus("2");
        item.setInjuryInsuranceStatus(injuryStatus);
        item.setMonthlyIncome(new BigDecimal("7420.00"));
        item.setExternalSerialNo("NF-" + personId);
        item.setSourceStatus("SUCCESS");
        item.setSourceMessage("OK");
        item.setCallbackTime(new Date());
        item.setRawPayload("{\"personId\":" + personId + "}");
        return item;
    }
}
