package com.yuegongbao.ygb.util;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeDecision;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YgbRiskCalculatorTest
{
    @Test
    void diffRatioReturnsHundredWhenRightIsZeroAndLeftPositive()
    {
        BigDecimal ratio = YgbRiskCalculator.diffRatio(new BigDecimal("5000"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("100.00"), ratio);
    }

    @Test
    void socialAndTaxThresholdChecksUseConfiguredBoundaries()
    {
        assertTrue(YgbRiskCalculator.isSocialBaseNormal(new BigDecimal("6000"), new BigDecimal("5000")));
        assertFalse(YgbRiskCalculator.isSocialBaseNormal(new BigDecimal("6100"), new BigDecimal("5000")));

        assertTrue(YgbRiskCalculator.isTaxNormal(new BigDecimal("5500"), new BigDecimal("5000")));
        assertFalse(YgbRiskCalculator.isTaxNormal(new BigDecimal("5600"), new BigDecimal("5000")));
    }

    @Test
    void employmentRatioProducesYellowAndRedWarnings()
    {
        assertEquals(new BigDecimal("11.11"), YgbRiskCalculator.calculateEmploymentRatio(1, 8));
        assertEquals("1", YgbRiskCalculator.calculateEmploymentWarningLevel(new BigDecimal("10.01")));
        assertEquals("2", YgbRiskCalculator.calculateEmploymentWarningLevel(new BigDecimal("15.01")));
        assertEquals("0", YgbRiskCalculator.calculateEmploymentWarningLevel(new BigDecimal("10.00")));
    }

    @Test
    void fakeOutsourcingScoreClampsInputAndFlagsLowScore()
    {
        assertEquals(56, YgbRiskCalculator.calculateFakeOutsourcingScore(40, 50, 60, 75));
        assertEquals(47, YgbRiskCalculator.calculateFakeOutsourcingScore(120, -10, 50, 40));
        assertTrue(YgbRiskCalculator.isSuspectedFakeOutsourcing(59));
        assertFalse(YgbRiskCalculator.isSuspectedFakeOutsourcing(60));
    }

    @Test
    void deviceAuthorizeDecisionRespectsCertBeforeInsurance()
    {
        YgbDeviceAuthorizeDecision certDenied = YgbRiskCalculator.decideDeviceAuthorize(false, true, "证件失效", "");
        assertFalse(certDenied.isAuthorized());
        assertEquals("证件失效", certDenied.getReason());

        YgbDeviceAuthorizeDecision insuranceDenied = YgbRiskCalculator.decideDeviceAuthorize(true, false, "", "未参保");
        assertFalse(insuranceDenied.isAuthorized());
        assertEquals("未参保", insuranceDenied.getReason());

        YgbDeviceAuthorizeDecision pass = YgbRiskCalculator.decideDeviceAuthorize(true, true, "", "");
        assertTrue(pass.isAuthorized());
        assertEquals("授权通过", pass.getReason());
    }
}
