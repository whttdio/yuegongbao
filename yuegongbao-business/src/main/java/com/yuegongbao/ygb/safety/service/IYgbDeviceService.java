package com.yuegongbao.ygb.safety.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.domain.vo.YgbDeviceBatchActionRequest;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceCommandLog;
import com.yuegongbao.ygb.safety.domain.YgbDeviceEvent;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAiEventRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceHeartbeatRequest;

public interface IYgbDeviceService
{
    public List<YgbDevice> selectDeviceList(YgbDevice device);

    public YgbDeviceSummary selectDeviceSummary(YgbDevice device);

    public YgbDevice selectDeviceById(Long deviceId);

    public List<YgbDevice> selectDeviceSubledgerList(String viewCode, YgbDevice device);

    public YgbDeviceSummary selectDeviceSubledgerSummary(String viewCode, YgbDevice device);

    public List<YgbDevice> selectIotCardList(YgbDevice device);

    public YgbDeviceSummary selectIotCardSummary(YgbDevice device);

    public List<YgbDevice> selectChipInventoryList(YgbDevice device);

    public YgbDeviceSummary selectChipInventorySummary(YgbDevice device);

    public List<YgbDeviceEvent> selectUninstallAlertList(YgbDeviceEvent query);

    public int insertDevice(YgbDevice device);

    public int updateDevice(YgbDevice device);

    public int deleteDeviceByIds(Long[] deviceIds, String updateBy);

    public List<YgbDeviceCommandLog> selectDeviceCommandLogList(YgbDeviceCommandLog commandLog);

    public List<YgbDeviceEvent> selectDeviceEventList(YgbDeviceEvent deviceEvent);

    public Map<String, Object> lock(Long deviceId, String operator);

    public Map<String, Object> unlock(Long deviceId, String operator);

    public Map<String, Object> authorize(YgbDeviceAuthorizeRequest request, String operator);

    public Map<String, Object> heartbeat(YgbDeviceHeartbeatRequest request, String operator);

    public Map<String, Object> aiEvent(YgbDeviceAiEventRequest request, String operator);

    public Map<String, Object> batchLock(YgbDeviceBatchActionRequest request, String operator);

    public Map<String, Object> batchUnlock(YgbDeviceBatchActionRequest request, String operator);

    public Map<String, Object> batchAuthorize(YgbDeviceBatchActionRequest request, String operator);
}

