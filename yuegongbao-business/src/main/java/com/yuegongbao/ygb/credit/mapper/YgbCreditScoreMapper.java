package com.yuegongbao.ygb.credit.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;

/**
 * 企业信用评分Mapper接口。
 *
 * @author yuegongbao
 */
public interface YgbCreditScoreMapper
{
    List<YgbCreditScore> selectCreditScoreList(YgbCreditScore creditScore);

    YgbCreditScore selectCreditScoreById(Long scoreId);

    YgbCreditScore selectCreditScoreByScope(@Param("statMonth") String statMonth, @Param("enterpriseId") Long enterpriseId);

    YgbCreditScore selectLatestCreditScoreByEnterpriseId(Long enterpriseId);

    int insertCreditScore(YgbCreditScore creditScore);

    int updateCreditScore(YgbCreditScore creditScore);

    List<YgbCreditScore> selectCreditScoreRanking(String statMonth);

    int updateRankNo(@Param("scoreId") Long scoreId, @Param("rankNo") Integer rankNo, @Param("updateBy") String updateBy);

    int countContractTotal(@Param("enterpriseId") Long enterpriseId, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countContractFiled(@Param("enterpriseId") Long enterpriseId, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countAttendanceTotal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countAttendancePassed(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countSalaryTotal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countSalarySuccess(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countSocialBaseTotal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countSocialBaseNormal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countTaxCompareTotal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countTaxCompareNormal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countUninsuredTotal(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    String selectAqPolicyStatus(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    BigDecimal selectAqRemainingFund(@Param("enterpriseId") Long enterpriseId, @Param("statMonth") String statMonth);

    int countInjuryTotal(@Param("enterpriseId") Long enterpriseId, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countInjuryOverdue(Long enterpriseId);

    int countWarningTotal(@Param("enterpriseId") Long enterpriseId, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countWarningClosed(@Param("enterpriseId") Long enterpriseId, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countActiveRedWarning(Long enterpriseId);
}
