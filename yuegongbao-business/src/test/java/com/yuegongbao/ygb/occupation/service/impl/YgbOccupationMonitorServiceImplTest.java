package com.yuegongbao.ygb.occupation.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.domain.vo.YgbOccupationMonitorStubItem;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.integration.OccupationClient;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;
import com.yuegongbao.ygb.occupation.mapper.YgbOccupationMonitorMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbOccupationMonitorServiceImplTest
{
    @Mock
    private YgbOccupationMonitorMapper occupationMonitorMapper;

    @Mock
    private OccupationClient occupationClient;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbOccupationMonitorServiceImpl service;

    @Test
    void syncOccupationMonitorCreatesWarningsForHighRiskRows()
    {
        when(occupationClient.pullMonitorRecords("2026-06")).thenReturn(List.of(
            item("440106", "建筑施工", new BigDecimal("1.80"), 2),
            item("440305", "制造加工", new BigDecimal("4.20"), 5)));

        int rows = service.syncOccupationMonitor("2026-06", "tester");

        assertEquals(2, rows);
        verify(occupationMonitorMapper).deleteByScope("2026-06", null);

        ArgumentCaptor<YgbOccupationMonitor> captor = ArgumentCaptor.forClass(YgbOccupationMonitor.class);
        verify(occupationMonitorMapper, org.mockito.Mockito.times(2)).insertOccupationMonitor(captor.capture());
        List<YgbOccupationMonitor> values = captor.getAllValues();
        assertEquals("0", values.get(0).getWarningLevel());
        assertEquals("2", values.get(1).getWarningLevel());
        assertEquals("1", values.get(1).getWarningStatus());

        ArgumentCaptor<YgbWarningCreateRequest> warningCaptor = ArgumentCaptor.forClass(YgbWarningCreateRequest.class);
        verify(warningService).createWarningIfAbsent(warningCaptor.capture(), eq("tester"));
        assertEquals("OCCUPATION_MONITOR", warningCaptor.getValue().getWarnType());
        assertEquals("3", warningCaptor.getValue().getWarnLevel());
    }

    @Test
    void resolveWarningLevelUsesIncidenceAndCaseThresholds()
    {
        assertEquals("0", YgbOccupationMonitorServiceImpl.resolveWarningLevel(new BigDecimal("1.20"), 1));
        assertEquals("1", YgbOccupationMonitorServiceImpl.resolveWarningLevel(new BigDecimal("2.10"), 2));
        assertEquals("2", YgbOccupationMonitorServiceImpl.resolveWarningLevel(new BigDecimal("2.40"), 5));
    }

    private YgbOccupationMonitorStubItem item(String regionCode, String industryType, BigDecimal incidenceRate,
        Integer caseCount)
    {
        YgbOccupationMonitorStubItem item = new YgbOccupationMonitorStubItem();
        item.setStatMonth("2026-06");
        item.setRegionCode(regionCode);
        item.setIndustryType(industryType);
        item.setEnterpriseCount(12);
        item.setWorkerCount(860);
        item.setCaseCount(caseCount);
        item.setHighRiskEnterpriseCount(3);
        item.setIncidenceRate(incidenceRate);
        item.setSourceChannel("卫健 Stub");
        item.setExternalSerialNo("OC-" + regionCode);
        item.setSourceStatus("SUCCESS");
        item.setSourceMessage("OK");
        item.setCallbackTime(new Date());
        item.setRawPayload("{\"regionCode\":\"" + regionCode + "\"}");
        return item;
    }
}
