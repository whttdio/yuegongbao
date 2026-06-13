package com.yuegongbao.ygb.aireport.service.impl;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbAiReportConfigServiceImplTest
{
    @Mock
    private YgbAiReportMapper aiReportMapper;

    @InjectMocks
    private YgbAiReportConfigServiceImpl service;

    @Test
    void selectCurrentConfigFallsBackToDefaultStub()
    {
        when(aiReportMapper.selectActiveConfigByRegion(eq("440305"), any(Date.class))).thenReturn(null);
        when(aiReportMapper.selectActiveConfigByRegion(eq("440000"), any(Date.class))).thenReturn(null);

        YgbAiReportConfig config = service.selectCurrentConfig("440305", new Date());

        assertEquals("DEFAULT-STUB", config.getVersion());
        assertNotNull(config.getDimensionWeights());
        assertEquals("深圳市南山区", config.getRegionName());
    }

    @Test
    void saveConfigActivatesCurrentRegionVersion()
    {
        doAnswer(invocation -> {
            YgbAiReportConfig config = invocation.getArgument(0);
            config.setConfigId(93099L);
            return 1;
        }).when(aiReportMapper).insertAiReportConfig(any(YgbAiReportConfig.class));

        YgbAiReportConfig config = new YgbAiReportConfig();
        config.setRegionCode("440000");
        config.setVersion("V202606");
        config.setConfigStatus("1");
        config.setEffectiveDate(new Date());
        config.setDimensionWeights("{\"A\":25}");
        config.setTargetValues("{\"contractRate\":100}");

        service.saveAiReportConfig(config, "tester");

        ArgumentCaptor<YgbAiReportConfig> captor = ArgumentCaptor.forClass(YgbAiReportConfig.class);
        verify(aiReportMapper).insertAiReportConfig(captor.capture());
        assertEquals("stub", captor.getValue().getSourceMode());
        verify(aiReportMapper).deactivateConfigsByRegion("440000", 93099L, "tester");
    }
}
