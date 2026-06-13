package com.yuegongbao.ygb.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeDecision;

/**
 * 粤工保风险计算工具。
 *
 * @author yuegongbao
 */
public final class YgbRiskCalculator
{
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private YgbRiskCalculator()
    {
    }

    public static BigDecimal diffRatio(BigDecimal left, BigDecimal right)
    {
        if (left == null || right == null)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (BigDecimal.ZERO.compareTo(right) == 0)
        {
            return BigDecimal.ZERO.compareTo(left) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : ONE_HUNDRED.setScale(2, RoundingMode.HALF_UP);
        }
        return left.subtract(right).abs().multiply(ONE_HUNDRED)
            .divide(right, 2, RoundingMode.HALF_UP);
    }

    public static boolean isSocialBaseNormal(BigDecimal salaryAmount, BigDecimal baseAmount)
    {
        return diffRatio(nvl(salaryAmount), nvl(baseAmount)).compareTo(new BigDecimal("20")) <= 0;
    }

    public static boolean isTaxNormal(BigDecimal salaryAmount, BigDecimal declaredAmount)
    {
        return diffRatio(nvl(salaryAmount), nvl(declaredAmount)).compareTo(new BigDecimal("10")) <= 0;
    }

    public static BigDecimal calculateEmploymentRatio(int dispatchCount, int formalCount)
    {
        int total = dispatchCount + formalCount;
        if (total <= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(dispatchCount).multiply(ONE_HUNDRED)
            .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
    }

    public static String calculateEmploymentWarningLevel(BigDecimal ratio)
    {
        BigDecimal value = nvl(ratio);
        if (value.compareTo(new BigDecimal("15")) > 0)
        {
            return "2";
        }
        if (value.compareTo(new BigDecimal("10")) > 0)
        {
            return "1";
        }
        return "0";
    }

    public static int calculateFakeOutsourcingScore(Integer attendanceScore, Integer scheduleScore, Integer rewardScore,
        Integer trainingScore)
    {
        int att = clamp(attendanceScore);
        int schedule = clamp(scheduleScore);
        int reward = clamp(rewardScore);
        int training = clamp(trainingScore);
        return (att + schedule + reward + training) / 4;
    }

    public static boolean isSuspectedFakeOutsourcing(Integer score)
    {
        return clamp(score) < 60;
    }

    public static YgbDeviceAuthorizeDecision decideDeviceAuthorize(boolean certValid, boolean insured, String certMessage,
        String insuranceMessage)
    {
        if (!certValid)
        {
            return new YgbDeviceAuthorizeDecision(false,
                StringUtils.isEmpty(certMessage) ? "证件校验不通过" : certMessage);
        }
        if (!insured)
        {
            return new YgbDeviceAuthorizeDecision(false,
                StringUtils.isEmpty(insuranceMessage) ? "工伤保险校验不通过" : insuranceMessage);
        }
        return new YgbDeviceAuthorizeDecision(true, "授权通过");
    }

    private static int clamp(Integer value)
    {
        if (value == null)
        {
            return 0;
        }
        return Math.max(0, Math.min(100, value));
    }

    private static BigDecimal nvl(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : value.setScale(2, RoundingMode.HALF_UP);
    }
}
