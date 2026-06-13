package com.yuegongbao.ygb.safety.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEvent;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEventSummary;

public interface IYgbInjuryEventService
{
    public List<YgbInjuryEvent> selectInjuryEventList(YgbInjuryEvent injuryEvent);

    public YgbInjuryEventSummary selectInjuryEventSummary(YgbInjuryEvent injuryEvent);

    public YgbInjuryEvent selectInjuryEventById(Long eventId);

    public Map<String, Object> selectInjuryEventAnalysis(YgbInjuryEvent injuryEvent);

    public Map<String, Object> selectInjuryEventMonitor(YgbInjuryEvent injuryEvent);

    public List<YgbModuleRecord> selectRecognitionAssistList(YgbInjuryEvent injuryEvent);

    public YgbModuleRecord selectRecognitionAssistById(Long eventId);

    public int insertInjuryEvent(YgbInjuryEvent injuryEvent);

    public int updateInjuryEvent(YgbInjuryEvent injuryEvent);

    public int updateStatus(Long eventId, String injuryStatus, String approvalResult, String operator);

    public int deleteInjuryEventByIds(Long[] eventIds, String updateBy);
}

