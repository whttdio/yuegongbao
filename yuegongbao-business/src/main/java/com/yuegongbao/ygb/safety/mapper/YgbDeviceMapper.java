package com.yuegongbao.ygb.safety.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.safety.domain.YgbDevice;

public interface YgbDeviceMapper
{
    public List<YgbDevice> selectDeviceList(YgbDevice device);

    public YgbDevice selectDeviceById(Long deviceId);

    public YgbDevice selectDeviceByCode(String deviceCode);

    public int insertDevice(YgbDevice device);

    public int updateDevice(YgbDevice device);

    public int deleteDeviceByIds(@Param("deviceIds") Long[] deviceIds, @Param("updateBy") String updateBy);

    public int updateDeviceStatus(@Param("deviceId") Long deviceId, @Param("deviceStatus") String deviceStatus,
        @Param("updateBy") String updateBy);

    public int updateAuthStatus(@Param("deviceId") Long deviceId, @Param("authStatus") String authStatus,
        @Param("updateBy") String updateBy);

    public int updateHeartbeat(@Param("deviceId") Long deviceId, @Param("deviceStatus") String deviceStatus,
        @Param("updateBy") String updateBy);
}
