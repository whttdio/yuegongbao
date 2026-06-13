package com.yuegongbao.ygb.safety.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEvent;

public interface YgbInjuryEventMapper
{
    public List<YgbInjuryEvent> selectInjuryEventList(YgbInjuryEvent injuryEvent);

    public YgbInjuryEvent selectInjuryEventById(Long eventId);

    public int insertInjuryEvent(YgbInjuryEvent injuryEvent);

    public int updateInjuryEvent(YgbInjuryEvent injuryEvent);

    public int deleteInjuryEventByIds(@Param("eventIds") Long[] eventIds, @Param("updateBy") String updateBy);
}
