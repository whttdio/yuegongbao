package com.yuegongbao.ygb.credit.service;

import java.util.List;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreGenerateRequest;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary;

/**
 * 企业信用评分Service接口。
 *
 * @author yuegongbao
 */
public interface IYgbCreditScoreService
{
    List<YgbCreditScore> selectCreditScoreList(YgbCreditScore creditScore);

    YgbCreditScoreSummary selectCreditScoreSummary(YgbCreditScore creditScore);

    YgbCreditScore selectCreditScoreById(Long scoreId);

    YgbCreditScore selectLatestCreditScore(Long enterpriseId);

    int generateCreditScores(YgbCreditScoreGenerateRequest request, String operator);
}
