package com.yuegongbao.ygb.safety.mapper;

import java.util.List;
import com.yuegongbao.ygb.safety.domain.YgbDeviceCommandLog;

public interface YgbDeviceCommandLogMapper
{
    public List<YgbDeviceCommandLog> selectDeviceCommandLogList(YgbDeviceCommandLog commandLog);

    public int insertDeviceCommandLog(YgbDeviceCommandLog commandLog);
}
