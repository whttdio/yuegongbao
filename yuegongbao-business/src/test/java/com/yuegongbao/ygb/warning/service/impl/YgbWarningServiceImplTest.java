package com.yuegongbao.ygb.warning.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.warning.domain.YgbWarningHandleLog;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.warning.mapper.YgbWarningHandleLogMapper;
import com.yuegongbao.ygb.warning.mapper.YgbWarningMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbWarningServiceImplTest
{
    @Mock
    private YgbWarningMapper warningMapper;

    @Mock
    private YgbWarningHandleLogMapper warningHandleLogMapper;

    @InjectMocks
    private YgbWarningServiceImpl service;

    @Test
    void createWarningDefaultsLevelAndReturnsGeneratedId()
    {
        when(warningMapper.insertWarning(any(YgbWarning.class))).thenAnswer(invocation -> {
            YgbWarning warning = invocation.getArgument(0);
            warning.setWarnId(99L);
            return 1;
        });

        Long warnId = service.createWarning(createRequest(), "tester");

        assertEquals(99L, warnId);

        ArgumentCaptor<YgbWarning> warningCaptor = ArgumentCaptor.forClass(YgbWarning.class);
        verify(warningMapper).insertWarning(warningCaptor.capture());
        YgbWarning warning = warningCaptor.getValue();
        assertEquals("2", warning.getWarnLevel());
        assertEquals("0", warning.getWarnStatus());
        assertEquals("tester", warning.getCreateBy());
        assertNotNull(warning.getCreateTime());
    }

    @Test
    void createWarningIfAbsentReturnsZeroWhenDuplicateExists()
    {
        YgbWarningCreateRequest request = createRequest();
        when(warningMapper.countActiveWarning("SOCIAL", "SOCIAL_BASE_COMPARE", 1L)).thenReturn(1);

        Long warnId = service.createWarningIfAbsent(request, "tester");

        assertEquals(0L, warnId);
        verify(warningMapper, never()).insertWarning(any(YgbWarning.class));
    }

    @Test
    void handleWarningClosesTicketAndWritesHandleLog()
    {
        YgbWarning warning = new YgbWarning();
        warning.setWarnId(88L);
        warning.setWarnStatus("0");
        when(warningMapper.selectWarningById(88L)).thenReturn(warning);
        when(warningHandleLogMapper.insertWarningHandleLog(any(YgbWarningHandleLog.class))).thenReturn(1);

        int rows = service.handleWarning(88L, "CLOSE", "done", "a.png", "tester");

        assertEquals(1, rows);
        verify(warningMapper).updateWarningStatus(88L, "2", "tester", "tester", true);

        ArgumentCaptor<YgbWarningHandleLog> logCaptor = ArgumentCaptor.forClass(YgbWarningHandleLog.class);
        verify(warningHandleLogMapper).insertWarningHandleLog(logCaptor.capture());
        YgbWarningHandleLog handleLog = logCaptor.getValue();
        assertEquals("CLOSE", handleLog.getActionType());
        assertEquals("0", handleLog.getBeforeStatus());
        assertEquals("2", handleLog.getAfterStatus());
        assertEquals("tester", handleLog.getHandlerName());
        assertEquals("done", handleLog.getOpinion());
        assertEquals("a.png", handleLog.getAttachmentUrls());
    }

    private YgbWarningCreateRequest createRequest()
    {
        YgbWarningCreateRequest request = new YgbWarningCreateRequest();
        request.setWarnType("SOCIAL_BASE_COMPARE");
        request.setSourceModule("SOCIAL");
        request.setTargetObjectId(1L);
        request.setTargetType("3");
        request.setEnterpriseId(10L);
        request.setEnterpriseName("ENT-10");
        request.setRegionCode("440100");
        request.setContent("ALERT");
        return request;
    }
}
