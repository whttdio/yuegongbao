package com.yuegongbao.ygb.newform.service;

import java.util.List;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.domain.YgbNewformPlatformStat;
import com.yuegongbao.ygb.newform.domain.YgbNewformPlatformSummary;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorkerSummary;

public interface IYgbNewformWorkerService
{
    List<YgbNewformWorker> selectNewformWorkerList(YgbNewformWorker query);

    YgbNewformWorkerSummary selectNewformWorkerSummary(YgbNewformWorker query);

    List<YgbNewformPlatformStat> selectNewformPlatformList(YgbNewformWorker query);

    YgbNewformPlatformSummary selectNewformPlatformSummary(YgbNewformWorker query);

    List<YgbNewformPlatformStat> selectNewformInjuryMonitorList(YgbNewformWorker query);

    YgbNewformPlatformSummary selectNewformInjuryMonitorSummary(YgbNewformWorker query);

    int syncNewformWorker(String statMonth, Long enterpriseId, String operator);
}
