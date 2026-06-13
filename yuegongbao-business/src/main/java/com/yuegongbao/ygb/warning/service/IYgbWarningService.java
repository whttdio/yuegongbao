package com.yuegongbao.ygb.warning.service;

import java.util.List;
import com.yuegongbao.ygb.warning.domain.YgbWarningAnalysisSummary;
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.warning.domain.YgbWarningHandleLog;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;

/**
 * 预警工单服务接口。
 * @author yuegongbao
 */
public interface IYgbWarningService
{
    List<YgbWarning> selectWarningList(YgbWarning warning);

    YgbWarningSummary selectWarningSummary(YgbWarning warning);

    YgbWarningAnalysisSummary selectWarningAnalysis(YgbWarning warning);

    YgbWarning selectWarningById(Long warnId);

    List<YgbWarningHandleLog> selectWarningHandleLogs(Long warnId);

    Long createWarning(YgbWarningCreateRequest request, String operator);

    Long createWarningIfAbsent(YgbWarningCreateRequest request, String operator);

    int handleWarning(Long warnId, String action, String opinion, String attachmentUrls, String operator);
}

