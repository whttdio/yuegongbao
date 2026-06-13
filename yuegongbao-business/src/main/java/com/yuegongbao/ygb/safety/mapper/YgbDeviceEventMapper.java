package com.yuegongbao.ygb.safety.mapper;

import java.util.List;
import com.yuegongbao.ygb.safety.domain.YgbDeviceEvent;

public interface YgbDeviceEventMapper
{
    public List<YgbDeviceEvent> selectDeviceEventList(YgbDeviceEvent deviceEvent);

    public int insertDeviceEvent(YgbDeviceEvent deviceEvent);
}
