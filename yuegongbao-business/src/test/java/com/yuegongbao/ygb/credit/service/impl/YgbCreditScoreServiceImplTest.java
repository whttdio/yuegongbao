package com.yuegongbao.ygb.credit.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreGenerateRequest;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary;
import com.yuegongbao.ygb.credit.mapper.YgbCreditScoreMapper;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbCreditScoreServiceImplTest
{
    @Mock
    private YgbCreditScoreMapper creditScoreMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private IYgbWarningService warningService;

    @InjectMocks
    private YgbCreditScoreServiceImpl service;

    @Test
    void generateCreditScoresCreatesLowScoreWarningForPoorMetrics()
    {
        YgbEnterprise enterprise = new YgbEnterprise();
        enterprise.setEnterpriseId(1003L);
        enterprise.setEnterpriseName("佛山顺德智造服务有限公司");
        enterprise.setEnterpriseType("3");
        enterprise.setRegionCode("440606");
        when(enterpriseMapper.selectEnterpriseOptions()).thenReturn(List.of(enterprise));

        when(creditScoreMapper.countContractTotal(eq(1003L), any(), any())).thenReturn(4);
        when(creditScoreMapper.countContractFiled(eq(1003L), any(), any())).thenReturn(2);
        when(creditScoreMapper.countAttendanceTotal(1003L, "2026-06")).thenReturn(10);
        when(creditScoreMapper.countAttendancePassed(1003L, "2026-06")).thenReturn(6);
        when(creditScoreMapper.countSalaryTotal(1003L, "2026-06")).thenReturn(10);
        when(creditScoreMapper.countSalarySuccess(1003L, "2026-06")).thenReturn(6);
        when(creditScoreMapper.countSocialBaseTotal(1003L, "2026-06")).thenReturn(4);
        when(creditScoreMapper.countSocialBaseNormal(1003L, "2026-06")).thenReturn(2);
        when(creditScoreMapper.countTaxCompareTotal(1003L, "2026-06")).thenReturn(4);
        when(creditScoreMapper.countTaxCompareNormal(1003L, "2026-06")).thenReturn(2);
        when(creditScoreMapper.countUninsuredTotal(1003L, "2026-06")).thenReturn(1);
        when(creditScoreMapper.selectAqPolicyStatus(1003L, "2026-06")).thenReturn("3");
        when(creditScoreMapper.selectAqRemainingFund(1003L, "2026-06")).thenReturn(new BigDecimal("8.00"));
        when(creditScoreMapper.countInjuryTotal(eq(1003L), any(), any())).thenReturn(2);
        when(creditScoreMapper.countInjuryOverdue(1003L)).thenReturn(1);
        when(creditScoreMapper.countWarningTotal(eq(1003L), any(), any())).thenReturn(4);
        when(creditScoreMapper.countWarningClosed(eq(1003L), any(), any())).thenReturn(1);
        when(creditScoreMapper.countActiveRedWarning(1003L)).thenReturn(1);
        when(creditScoreMapper.selectCreditScoreByScope("2026-06", 1003L)).thenReturn(null);
        when(creditScoreMapper.selectCreditScoreRanking("2026-06")).thenReturn(List.of(ranking(98001L)));
        doAnswer(invocation -> {
            YgbCreditScore score = invocation.getArgument(0);
            score.setScoreId(98001L);
            return 1;
        }).when(creditScoreMapper).insertCreditScore(any(YgbCreditScore.class));

        YgbCreditScoreGenerateRequest request = new YgbCreditScoreGenerateRequest();
        request.setStatMonth("2026-06");
        request.setRegionCode("440000");

        int rows = service.generateCreditScores(request, "tester");

        assertEquals(1, rows);
        ArgumentCaptor<YgbCreditScore> captor = ArgumentCaptor.forClass(YgbCreditScore.class);
        verify(creditScoreMapper).insertCreditScore(captor.capture());
        assertTrue(captor.getValue().getTotalScore().compareTo(new BigDecimal("70")) < 0);
        assertEquals("D", captor.getValue().getCreditLevel());
        assertEquals("RED", captor.getValue().getColorCode());
        assertEquals("1", captor.getValue().getWarningStatus());
        verify(warningService).createWarningIfAbsent(any(), eq("tester"));
        verify(creditScoreMapper).updateRankNo(98001L, 1, "tester");
    }

    @Test
    void resolveLevelAndColorFollowThreshold()
    {
        assertEquals("A", YgbCreditScoreServiceImpl.resolveLevel(new BigDecimal("92.00")));
        assertEquals("GREEN", YgbCreditScoreServiceImpl.resolveColor("A"));
        assertEquals("B", YgbCreditScoreServiceImpl.resolveLevel(new BigDecimal("85.00")));
        assertEquals("GREEN", YgbCreditScoreServiceImpl.resolveColor("B"));
        assertEquals("C", YgbCreditScoreServiceImpl.resolveLevel(new BigDecimal("75.00")));
        assertEquals("YELLOW", YgbCreditScoreServiceImpl.resolveColor("C"));
        assertEquals("D", YgbCreditScoreServiceImpl.resolveLevel(new BigDecimal("60.00")));
        assertEquals("RED", YgbCreditScoreServiceImpl.resolveColor("D"));
    }

    @Test
    void selectCreditScoreSummaryBuildsDifferentPortalExplanationsForSameScope()
    {
        YgbCreditScore lowScore = new YgbCreditScore();
        lowScore.setEnterpriseId(1003L);
        lowScore.setStatMonth("2026-06");
        lowScore.setRegionCode("440606");
        lowScore.setTotalScore(new BigDecimal("66.20"));
        lowScore.setCreditLevel("D");
        lowScore.setColorCode("RED");

        YgbCreditScore midScore = new YgbCreditScore();
        midScore.setEnterpriseId(1003L);
        midScore.setStatMonth("2026-06");
        midScore.setRegionCode("440606");
        midScore.setTotalScore(new BigDecimal("76.80"));
        midScore.setCreditLevel("C");
        midScore.setColorCode("YELLOW");

        when(creditScoreMapper.selectCreditScoreList(any(YgbCreditScore.class))).thenReturn(List.of(lowScore, midScore));

        YgbCreditScore query = new YgbCreditScore();
        query.setEnterpriseId(1003L);
        query.setStatMonth("2026-06");
        query.setRegionCode("440606");

        YgbCreditScoreSummary summary = service.selectCreditScoreSummary(query);

        assertEquals(2, summary.getYgbExplanation().size());
        assertEquals(3, summary.getAzbExplanation().size());
        assertEquals("statReport", summary.getYgbExplanation().get(0).get("recommendModule"));
        assertEquals("socialBaseCompare", summary.getYgbExplanation().get(1).get("recommendModule"));
        assertEquals("creditScore", summary.getAzbExplanation().get(0).get("recommendModule"));
        assertEquals("warning", summary.getAzbExplanation().get(1).get("recommendModule"));
        assertTrue(String.valueOf(summary.getYgbExplanation().get(0).get("sourceLabel")).contains("530.1"));
        assertTrue(String.valueOf(summary.getAzbExplanation().get(0).get("sourceLabel")).contains("6.1"));
        assertEquals(1003L, castMap(summary.getYgbExplanation().get(0).get("defaultQuery")).get("enterpriseId"));
        assertEquals("2026-06", castMap(summary.getAzbExplanation().get(0).get("defaultQuery")).get("statMonth"));
    }

    private YgbCreditScore ranking(Long scoreId)
    {
        YgbCreditScore score = new YgbCreditScore();
        score.setScoreId(scoreId);
        score.setTotalScore(new BigDecimal("61.20"));
        return score;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value)
    {
        return (Map<String, Object>) value;
    }
}
