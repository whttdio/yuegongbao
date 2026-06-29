package com.yuegongbao.ygb.newform.mapper;

import java.util.List;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.domain.YgbNewformPlatformStat;

public interface YgbNewformWorkerMapper
{
    List<YgbNewformWorker> selectNewformWorkerList(YgbNewformWorker query);

    List<YgbNewformPlatformStat> selectNewformPlatformList(YgbNewformWorker query);

    List<YgbNewformPlatformStat> selectNewformInjuryMonitorList(YgbNewformWorker query);

    int insertNewformWorker(YgbNewformWorker worker);

    int deleteByScope(YgbNewformWorker query);
}
