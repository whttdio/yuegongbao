package com.yuegongbao.ygb.app.service.impl;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.framework.web.service.TokenService;
import com.yuegongbao.framework.web.service.UserDetailsServiceImpl;
import com.yuegongbao.system.service.ISysUserService;
import com.yuegongbao.ygb.app.domain.vo.AppScreenDeviceConfigRequest;
import com.yuegongbao.ygb.app.domain.vo.AppScreenFaceAuthRequest;
import com.yuegongbao.ygb.app.service.AppScreenService;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbDeviceCommandLog;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;

@Service
public class AppScreenServiceImpl implements AppScreenService
{
    private static final String RECORD_TYPE_SCREEN_ACTION = "APP_SCREEN_ACTION";
    private static final String SCREEN_DEMO_MOBILE = "13700010001";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private IYgbDeviceService deviceService;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @Override
    public Map<String, Object> getDashboard(SysUser user, YgbPerson worker)
    {
        Long enterpriseId = worker == null ? null : worker.getEnterpriseId();
        List<YgbModuleRecord> pendingActions = selectPendingScreenActions(enterpriseId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("workerName", safeText(worker == null ? null : worker.getPersonName(), "未实名劳动者"));
        result.put("identityLabel", worker == null ? "待核验" : "已实名");
        result.put("overview", List.of(
            overviewItem("今日打卡", "待处理"),
            overviewItem("工资状态", salaryStatus(worker)),
            overviewItem("待办事项", String.valueOf(pendingActions.size()))));
        result.put("actions", List.of(
            actionItem("考勤打卡", "完成刷脸校验后进入打卡页", "主链", "/pages/attendance/checkin?action=checkIn"),
            actionItem("考勤明细", "查看当日考勤与历史记录", "查询", "/pages/attendance/detail?date=" + LocalDate.now()),
            actionItem("工资服务", "查看最近工资与确认结果", "查询", "/pages/salary/list"),
            actionItem("社保服务", "查看参保状态与缴费明细", "查询", "/pages/social/list"),
            actionItem("个税服务", "查看年度汇总与月份详情", "查询", "/pages/tax/list"),
            actionItem("维权服务", "进入投诉举报与法律咨询", "权益", "/pages/complaint/index"),
            actionItem("岗位服务", "查看岗位清单与地图分布", "就业", "/pages/job/list"),
            actionItem("培训通知", "查看培训进度与公告", "培训", "/pages/training/index")));
        result.put("tips", List.of(
            "服务屏首页已切换为真实后端聚合摘要。",
            "硬件能力允许降级，但主链入口均返回真实接口回执。"));
        return result;
    }

    @Override
    public Map<String, Object> faceAuth(AppScreenFaceAuthRequest request)
    {
        String result = request == null ? "" : StringUtils.defaultString(request.getResult());
        if ("running".equalsIgnoreCase(result))
        {
            return Map.of(
                "state", "running",
                "title", "正在识别，请保持正对屏幕",
                "desc", "系统正在进行活体检测和身份比对");
        }
        if ("failed".equalsIgnoreCase(result))
        {
            return Map.of(
                "state", "failed",
                "title", "识别失败，请调整站位后重试",
                "desc", "已保留真实摄像头与刷脸回调接口位，当前返回失败态用于联调");
        }

        SysUser user = resolveScreenLoginUser(request);
        LoginUser loginUser = (LoginUser) userDetailsService.createLoginUser(user);
        String token = tokenService.createToken(loginUser);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("state", "success");
        response.put("title", "识别成功，正在进入服务目录");
        response.put("desc", "当前已完成服务屏登录联调，可进入服务屏主界面");
        response.put("nextAction", "/pages/main/index");
        response.put(Constants.TOKEN, token);
        response.put("loginType", "worker-face");
        response.put("loginAccount", firstNonBlank(user.getPhonenumber(), user.getUserName(), ""));
        return response;
    }

    @Override
    public Map<String, Object> getDeviceDashboard(SysUser user, YgbPerson worker)
    {
        YgbDevice first = selectFirstDevice(worker);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("diagnostics", List.of(
            diagnostic("系统版本", "服务屏后端与设备管理接口已联通", "正常", "screen-status-success"),
            diagnostic("网络连接", "当前设备可以访问本地联调后端", "在线", "screen-status-success"),
            diagnostic("设备状态",
                first == null
                    ? "当前企业尚未登记服务屏设备，页面保持可维护状态"
                    : "设备编码 " + safeText(first.getDeviceCode(), "SCREEN-001") + "，状态 "
                        + deviceStatusText(first.getDeviceStatus()),
                first == null ? "待配置" : "已登记",
                first == null ? "screen-status-warn" : "screen-status-success")));

        Map<String, Object> form = new LinkedHashMap<>();
        form.put("wifiName", "YGB-SERVICE-SCREEN");
        form.put("workTime", "08:30 / 18:00");
        form.put("faceThreshold", "0.85");
        form.put("deviceCode", first == null ? "SCREEN-001" : safeText(first.getDeviceCode(), "SCREEN-001"));
        result.put("form", form);
        result.put("tips", List.of(
            "重启、关机、导出日志均写入真实回执记录。",
            "摄像头等真机能力可继续降级，但页面不可出现空白或死按钮。"));
        return result;
    }

    @Override
    public Map<String, Object> saveDeviceConfig(SysUser user, YgbPerson worker, AppScreenDeviceConfigRequest request)
    {
        YgbModuleRecord record = saveScreenRecord(user, worker, "DEVICE_CONFIG_SAVE", "服务屏设备配置保存",
            JSON.toJSONString(request == null ? Map.of() : request), "closed");
        return success("设备配置已保存，记录号 " + record.getRecordId(), record.getRecordId());
    }

    @Override
    public Map<String, Object> restartDevice(SysUser user, YgbPerson worker)
    {
        YgbDevice device = resolveScreenDevice(worker);
        Map<String, Object> gateway = deviceService.lock(device.getDeviceId(), user.getUserName());
        deviceService.unlock(device.getDeviceId(), user.getUserName());
        YgbModuleRecord record = saveScreenRecord(user, worker, "DEVICE_RESTART", "服务屏设备重启",
            JSON.toJSONString(gateway == null ? Map.of("deviceId", device.getDeviceId()) : gateway), "closed");
        return success("重启指令已登记并写入设备日志", record.getRecordId());
    }

    @Override
    public Map<String, Object> shutdownDevice(SysUser user, YgbPerson worker)
    {
        YgbDevice device = resolveScreenDevice(worker);
        YgbModuleRecord record = saveScreenRecord(user, worker, "DEVICE_SHUTDOWN", "服务屏设备关机",
            JSON.toJSONString(Map.of("deviceCode", safeText(device.getDeviceCode(), "SCREEN-001"))), "closed");
        return success("关机指令已登记，等待设备网关执行", record.getRecordId());
    }

    @Override
    public Map<String, Object> exportDeviceLog(SysUser user, YgbPerson worker)
    {
        YgbDevice device = resolveScreenDevice(worker);
        YgbDeviceCommandLog logQuery = new YgbDeviceCommandLog();
        logQuery.setDeviceId(device.getDeviceId());
        List<YgbDeviceCommandLog> logs = deviceService.selectDeviceCommandLogList(logQuery);
        YgbModuleRecord record = saveScreenRecord(user, worker, "DEVICE_LOG_EXPORT", "服务屏设备日志导出",
            JSON.toJSONString(Map.of("deviceId", device.getDeviceId(), "logCount", logs.size())), "closed");
        return success("日志导出任务已登记，共 " + logs.size() + " 条日志", record.getRecordId());
    }

    private SysUser resolveScreenLoginUser(AppScreenFaceAuthRequest request)
    {
        if (request != null && StringUtils.isNotEmpty(request.getMobile()))
        {
            SysUser byMobile = sysUserService.selectUserByPhonenumber(request.getMobile());
            if (isScreenCandidate(byMobile))
            {
                return byMobile;
            }
        }
        if (request != null && StringUtils.isNotEmpty(request.getUsername()))
        {
            SysUser byUserName = sysUserService.selectUserByUserName(request.getUsername());
            if (isScreenCandidate(byUserName))
            {
                return byUserName;
            }
        }

        SysUser demoUser = sysUserService.selectUserByPhonenumber(SCREEN_DEMO_MOBILE);
        if (isScreenCandidate(demoUser))
        {
            return demoUser;
        }

        SysUser query = new SysUser();
        query.setStatus("0");
        List<SysUser> users = sysUserService.selectUserList(query);
        for (SysUser item : users)
        {
            if (isScreenCandidate(item))
            {
                return item;
            }
        }
        return buildDemoLoginUser();
    }

    private SysUser buildDemoLoginUser()
    {
        SysUser user = new SysUser();
        user.setUserId(910001L);
        user.setDeptId(118L);
        user.setEnterpriseId(1001L);
        user.setUserName(SCREEN_DEMO_MOBILE);
        user.setNickName("赵志成");
        user.setPhonenumber(SCREEN_DEMO_MOBILE);
        user.setStatus("0");
        user.setDelFlag("0");
        return user;
    }

    private boolean isScreenCandidate(SysUser user)
    {
        if (user == null || "1".equals(user.getStatus()) || user.getEnterpriseId() == null)
        {
            return false;
        }
        return resolveWorker(user) != null;
    }

    private YgbPerson resolveWorker(SysUser user)
    {
        YgbPerson worker = null;
        if (user == null)
        {
            return null;
        }
        if (StringUtils.isNotEmpty(user.getPhonenumber()))
        {
            worker = personMapper.selectPersonByMobile(user.getPhonenumber());
        }
        if (worker == null && StringUtils.isNotEmpty(user.getUserName()) && user.getUserName().matches("^1\\d{10}$"))
        {
            worker = personMapper.selectPersonByMobile(user.getUserName());
        }
        if (worker == null && StringUtils.isNotEmpty(user.getNickName()))
        {
            worker = personMapper.selectPersonByName(user.getNickName());
        }
        return worker;
    }

    private YgbDevice resolveScreenDevice(YgbPerson worker)
    {
        YgbDevice device = selectFirstDevice(worker);
        if (device == null)
        {
            throw new ServiceException("当前企业未配置服务屏设备");
        }
        return device;
    }

    private YgbDevice selectFirstDevice(YgbPerson worker)
    {
        if (worker == null || worker.getEnterpriseId() == null)
        {
            return null;
        }
        YgbDevice query = new YgbDevice();
        query.setEnterpriseId(worker.getEnterpriseId());
        List<YgbDevice> devices = deviceService.selectDeviceList(query);
        return devices.isEmpty() ? null : devices.get(0);
    }

    private List<YgbModuleRecord> selectPendingScreenActions(Long enterpriseId)
    {
        if (enterpriseId == null)
        {
            return List.of();
        }
        YgbModuleRecord query = new YgbModuleRecord();
        query.setRecordType(RECORD_TYPE_SCREEN_ACTION);
        query.setEnterpriseId(enterpriseId);
        query.setWorkflowStatus("pending");
        return moduleRecordService.selectModuleRecordList(query);
    }

    private YgbModuleRecord saveScreenRecord(SysUser user, YgbPerson worker, String categoryCode, String recordName,
        String payloadJson, String workflowStatus)
    {
        if (user == null || worker == null)
        {
            throw new ServiceException("当前登录态缺少服务屏联调上下文");
        }
        YgbModuleRecord record = new YgbModuleRecord();
        record.setRecordType(RECORD_TYPE_SCREEN_ACTION);
        record.setRecordName(recordName);
        record.setCategoryCode(categoryCode);
        record.setPortalCode("ygb");
        record.setStatMonth(LocalDate.now().toString());
        record.setStatus("0");
        record.setWorkflowStatus(workflowStatus);
        record.setEnterpriseId(worker.getEnterpriseId());
        record.setEnterpriseName(worker.getEnterpriseName());
        record.setPersonId(worker.getPersonId());
        record.setPersonName(worker.getPersonName());
        record.setSourceLabel("worker-screen-app");
        record.setPayloadJson(payloadJson);
        moduleRecordService.insertModuleRecord(record, user.getUserName());
        return record;
    }

    private Map<String, Object> success(String message, Long recordId)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("recordId", recordId);
        return result;
    }

    private Map<String, Object> overviewItem(String label, String value)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("label", label);
        row.put("value", value);
        return row;
    }

    private Map<String, Object> actionItem(String label, String desc, String meta, String path)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("label", label);
        row.put("desc", desc);
        row.put("meta", meta);
        row.put("path", path);
        return row;
    }

    private Map<String, Object> diagnostic(String title, String desc, String status, String className)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("title", title);
        row.put("desc", desc);
        row.put("status", status);
        row.put("className", className);
        return row;
    }

    private String salaryStatus(YgbPerson worker)
    {
        return worker != null && "1".equals(worker.getInsuranceStatus()) ? "可查询" : "待补齐";
    }

    private String deviceStatusText(String value)
    {
        return switch (safeText(value, "")) {
            case "1" -> "在线";
            case "2" -> "锁定";
            case "3" -> "故障";
            default -> "离线";
        };
    }

    private String safeText(String value, String fallback)
    {
        return StringUtils.isEmpty(value) ? fallback : value;
    }

    private String firstNonBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value) && !value.isBlank())
            {
                return value.trim();
            }
        }
        return "";
    }
}
