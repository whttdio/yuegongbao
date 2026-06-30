package com.yuegongbao.ygb.app.service;

import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.app.domain.vo.AppScreenDeviceConfigRequest;
import com.yuegongbao.ygb.app.domain.vo.AppScreenFaceAuthRequest;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface AppScreenService
{
    Map<String, Object> getDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> faceAuth(AppScreenFaceAuthRequest request);

    Map<String, Object> getDeviceDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> saveDeviceConfig(SysUser user, YgbPerson worker, AppScreenDeviceConfigRequest request);

    Map<String, Object> restartDevice(SysUser user, YgbPerson worker);

    Map<String, Object> shutdownDevice(SysUser user, YgbPerson worker);

    Map<String, Object> exportDeviceLog(SysUser user, YgbPerson worker);
}
