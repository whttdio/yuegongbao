package com.yuegongbao.ygb.safety.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.annotation.Log;
import com.yuegongbao.common.core.controller.BaseController;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.page.TableDataInfo;
import com.yuegongbao.common.enums.BusinessType;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.poi.ExcelUtil;
import com.yuegongbao.ygb.domain.vo.YgbDeviceBatchActionRequest;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceCommandLog;
import com.yuegongbao.ygb.safety.domain.YgbDeviceEvent;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAiEventRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceAuthorizeRequest;
import com.yuegongbao.ygb.domain.vo.YgbDeviceHeartbeatRequest;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;

@RestController
@RequestMapping("/ygb/device")
public class YgbDeviceController extends BaseController
{
    private static final String DEVICE_GEOFENCE_RECORD_TYPE = "DEVICE_GEOFENCE";

    @Autowired
    private IYgbDeviceService deviceService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgbDevice device)
    {
        startPage();
        List<YgbDevice> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/summary")
    public AjaxResult summary(YgbDevice device)
    {
        return success(deviceService.selectDeviceSummary(device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/subledger/list")
    public TableDataInfo subledgerList(String viewCode, YgbDevice device)
    {
        startPage();
        return getDataTable(deviceService.selectDeviceSubledgerList(viewCode, device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/subledger/summary")
    public AjaxResult subledgerSummary(String viewCode, YgbDevice device)
    {
        return success(deviceService.selectDeviceSubledgerSummary(viewCode, device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:query')")
    @GetMapping("/{deviceId}")
    public AjaxResult getInfo(@PathVariable Long deviceId)
    {
        return success(deviceService.selectDeviceById(deviceId));
    }

    @Log(title = "设备台账", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:device:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgbDevice device)
    {
        List<YgbDevice> list = deviceService.selectDeviceList(device);
        ExcelUtil<YgbDevice> util = new ExcelUtil<>(YgbDevice.class);
        util.exportExcel(response, list, "设备台账");
    }

    @Log(title = "设备台账", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:device:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YgbDevice device)
    {
        device.setCreateBy(getUsername());
        return toAjax(deviceService.insertDevice(device));
    }

    @Log(title = "设备台账", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:device:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YgbDevice device)
    {
        device.setUpdateBy(getUsername());
        return toAjax(deviceService.updateDevice(device));
    }

    @Log(title = "设备台账", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:device:remove')")
    @DeleteMapping("/{deviceIds}")
    public AjaxResult remove(@PathVariable Long[] deviceIds)
    {
        return toAjax(deviceService.deleteDeviceByIds(deviceIds, getUsername()));
    }

    @GetMapping("/commandLog/list")
    public TableDataInfo commandLogList(YgbDeviceCommandLog commandLog)
    {
        startPage();
        List<YgbDeviceCommandLog> list = deviceService.selectDeviceCommandLogList(commandLog);
        return getDataTable(list);
    }

    @GetMapping("/event/list")
    public TableDataInfo eventList(YgbDeviceEvent deviceEvent)
    {
        startPage();
        List<YgbDeviceEvent> list = deviceService.selectDeviceEventList(deviceEvent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/iotCard/list")
    public TableDataInfo iotCardList(YgbDevice device)
    {
        startPage();
        return getDataTable(deviceService.selectIotCardList(device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/iotCard/summary")
    public AjaxResult iotCardSummary(YgbDevice device)
    {
        return success(deviceService.selectIotCardSummary(device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:query')")
    @GetMapping("/iotCard/{deviceId}")
    public AjaxResult getIotCard(@PathVariable Long deviceId)
    {
        return success(deviceService.selectDeviceById(deviceId));
    }

    @Log(title = "Iot Card", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:device:export')")
    @PostMapping("/iotCard/export")
    public void exportIotCard(HttpServletResponse response, YgbDevice device)
    {
        ExcelUtil<YgbDevice> util = new ExcelUtil<>(YgbDevice.class);
        util.exportExcel(response, deviceService.selectIotCardList(device), "device_iot_card");
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/chipInventory/list")
    public TableDataInfo chipInventoryList(YgbDevice device)
    {
        startPage();
        return getDataTable(deviceService.selectChipInventoryList(device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/chipInventory/summary")
    public AjaxResult chipInventorySummary(YgbDevice device)
    {
        return success(deviceService.selectChipInventorySummary(device));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:query')")
    @GetMapping("/chipInventory/{deviceId}")
    public AjaxResult getChipInventory(@PathVariable Long deviceId)
    {
        return success(deviceService.selectDeviceById(deviceId));
    }

    @Log(title = "Chip Inventory", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:device:export')")
    @PostMapping("/chipInventory/export")
    public void exportChipInventory(HttpServletResponse response, YgbDevice device)
    {
        ExcelUtil<YgbDevice> util = new ExcelUtil<>(YgbDevice.class);
        util.exportExcel(response, deviceService.selectChipInventoryList(device), "device_chip_inventory");
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/uninstallAlert/list")
    public TableDataInfo uninstallAlertList(YgbDeviceEvent query)
    {
        startPage();
        return getDataTable(deviceService.selectUninstallAlertList(query));
    }

    @Log(title = "Device Uninstall Alert", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:device:export')")
    @PostMapping("/uninstallAlert/export")
    public void exportUninstallAlert(HttpServletResponse response, YgbDeviceEvent query)
    {
        ExcelUtil<YgbDeviceEvent> util = new ExcelUtil<>(YgbDeviceEvent.class);
        util.exportExcel(response, deviceService.selectUninstallAlertList(query), "device_uninstall_alert");
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/geofence/list")
    public TableDataInfo geofenceList(YgbModuleRecord query)
    {
        startPage();
        return getDataTable(moduleRecordService.selectModuleRecordList(typedGeofenceQuery(query)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:list')")
    @GetMapping("/geofence/summary")
    public AjaxResult geofenceSummary(YgbModuleRecord query)
    {
        return success(moduleRecordService.selectModuleRecordSummary(typedGeofenceQuery(query)));
    }

    @PreAuthorize("@ss.hasPermi('ygb:device:query')")
    @GetMapping("/geofence/{recordId}")
    public AjaxResult getGeofence(@PathVariable Long recordId)
    {
        YgbModuleRecord record = moduleRecordService.selectModuleRecordById(recordId);
        if (record == null || !DEVICE_GEOFENCE_RECORD_TYPE.equals(record.getRecordType()))
        {
            throw new ServiceException("Record type does not match device geofence");
        }
        return success(record);
    }

    @Log(title = "Device Geofence", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('ygb:device:export')")
    @PostMapping("/geofence/export")
    public void exportGeofence(HttpServletResponse response, YgbModuleRecord query)
    {
        ExcelUtil<YgbModuleRecord> util = new ExcelUtil<>(YgbModuleRecord.class);
        util.exportExcel(response, moduleRecordService.selectModuleRecordList(typedGeofenceQuery(query)), "device_geofence");
    }

    @Log(title = "Device Geofence", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('ygb:device:add')")
    @PostMapping("/geofence")
    public AjaxResult addGeofence(@Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedGeofenceQuery(record);
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.insertModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "Device Geofence", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('ygb:device:edit')")
    @PutMapping("/geofence")
    public AjaxResult editGeofence(@Validated @RequestBody YgbModuleRecord record)
    {
        YgbModuleRecord typedRecord = typedGeofenceQuery(record);
        if (typedRecord.getPortalCode() == null || typedRecord.getPortalCode().isEmpty())
        {
            typedRecord.setPortalCode("ygb");
        }
        return toAjax(moduleRecordService.updateModuleRecord(typedRecord, getUsername()));
    }

    @Log(title = "Device Geofence", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('ygb:device:remove')")
    @DeleteMapping("/geofence/{recordIds}")
    public AjaxResult removeGeofence(@PathVariable Long[] recordIds)
    {
        return toAjax(moduleRecordService.deleteModuleRecordByIds(recordIds, getUsername()));
    }

    @Log(title = "设备锁机", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:lock')")
    @PostMapping("/lock/{deviceId}")
    public AjaxResult lock(@PathVariable Long deviceId)
    {
        Map<String, Object> result = deviceService.lock(deviceId, getUsername());
        return AjaxResult.success("设备锁机完成。", result);
    }

    @Log(title = "设备解锁", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:unlock')")
    @PostMapping("/unlock/{deviceId}")
    public AjaxResult unlock(@PathVariable Long deviceId)
    {
        Map<String, Object> result = deviceService.unlock(deviceId, getUsername());
        return AjaxResult.success("设备解锁完成。", result);
    }

    @Log(title = "设备授权", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:authorize')")
    @PostMapping("/authorize")
    public AjaxResult authorize(@Validated @RequestBody YgbDeviceAuthorizeRequest request)
    {
        Map<String, Object> result = deviceService.authorize(request, getUsername());
        return AjaxResult.success("设备授权处理完成。", result);
    }

    @Log(title = "设备心跳", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:heartbeat')")
    @PostMapping("/heartbeat")
    public AjaxResult heartbeat(@RequestBody YgbDeviceHeartbeatRequest request)
    {
        Map<String, Object> result = deviceService.heartbeat(request, getUsername());
        return AjaxResult.success("模拟心跳完成。", result);
    }

    @Log(title = "设备AI事件", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:aiEvent')")
    @PostMapping("/aiEvent")
    public AjaxResult aiEvent(@RequestBody YgbDeviceAiEventRequest request)
    {
        Map<String, Object> result = deviceService.aiEvent(request, getUsername());
        return AjaxResult.success("模拟 AI 事件完成。", result);
    }
    @Log(title = "Device Batch Lock", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:lock')")
    @PostMapping("/batch/lock")
    public AjaxResult batchLock(@Validated @RequestBody YgbDeviceBatchActionRequest request)
    {
        return AjaxResult.success("Batch lock complete", deviceService.batchLock(request, getUsername()));
    }

    @Log(title = "Device Batch Unlock", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:unlock')")
    @PostMapping("/batch/unlock")
    public AjaxResult batchUnlock(@Validated @RequestBody YgbDeviceBatchActionRequest request)
    {
        return AjaxResult.success("Batch unlock complete", deviceService.batchUnlock(request, getUsername()));
    }

    @Log(title = "Device Batch Authorize", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('ygb:device:authorize')")
    @PostMapping("/batch/authorize")
    public AjaxResult batchAuthorize(@Validated @RequestBody YgbDeviceBatchActionRequest request)
    {
        return AjaxResult.success("Batch authorize complete", deviceService.batchAuthorize(request, getUsername()));
    }

    private YgbModuleRecord typedGeofenceQuery(YgbModuleRecord query)
    {
        YgbModuleRecord typedQuery = query == null ? new YgbModuleRecord() : query;
        typedQuery.setRecordType(DEVICE_GEOFENCE_RECORD_TYPE);
        return typedQuery;
    }
}
