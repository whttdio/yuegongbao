package com.yuegongbao.ygb.worker.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.DateUtils;
import com.yuegongbao.common.utils.uuid.IdUtils;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.mapper.YgbAttendanceRawMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceRawService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.vo.WorkerAttendanceCheckRequest;
import com.yuegongbao.ygb.worker.service.WorkerAttendanceService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@Service
public class WorkerAttendanceServiceImpl implements WorkerAttendanceService
{
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private IYgbAttendanceRawService attendanceRawService;

    @Autowired
    private YgbAttendanceRawMapper attendanceRawMapper;

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private WorkerTrainingService workerTrainingService;

    @Override
    public Map<String, Object> checkIn(YgbPerson worker, String username, WorkerAttendanceCheckRequest request)
    {
        validateTraining(worker);
        LocalDate today = LocalDate.now();
        YgbAttendanceRaw existing = attendanceRawMapper.selectAttendanceRawByPersonAndDate(worker.getPersonId(),
            DateUtils.toDate(today));
        if (existing != null && existing.getClockInTime() != null)
        {
            return buildAttendanceResponse(existing, "CHECK_IN", isLate(existing.getClockInTime()));
        }

        YgbContract contract = resolveCurrentContract(worker.getPersonId(), today);
        YgbAttendanceRaw attendance = existing == null ? new YgbAttendanceRaw() : existing;
        attendance.setContractId(contract.getContractId());
        attendance.setAttendanceDate(DateUtils.toDate(today));
        attendance.setClockInTime(new Date());
        attendance.setShiftName("白班");
        attendance.setAttendanceHours(BigDecimal.ZERO);
        attendance.setOvertimeHours(BigDecimal.ZERO);
        attendance.setSourceType("3");
        attendance.setCollectStatus("0");
        attendance.setAttCheck("1");
        attendance.setDeviceCode(request == null ? null : request.getDeviceCode());
        attendance.setAnomalyRemark(mergeRemark(buildRemark(request), buildRequestExtras(request)));
        attendance.setAttendanceStatus(resolveAttendanceStatus(attendance.getClockInTime(), attendance.getClockOutTime()));
        if (existing == null)
        {
            attendance.setAttendanceNo(buildAttendanceNo(worker.getPersonId(), "IN"));
            attendance.setCreateBy(username);
            attendanceRawService.insertAttendanceRaw(attendance);
        }
        else
        {
            attendance.setUpdateBy(username);
            attendanceRawService.updateAttendanceRaw(attendance);
        }
        YgbAttendanceRaw saved = attendanceRawMapper.selectAttendanceRawByPersonAndDate(worker.getPersonId(),
            DateUtils.toDate(today));
        return buildAttendanceResponse(saved, "CHECK_IN", isLate(saved.getClockInTime()));
    }

    @Override
    public Map<String, Object> checkOut(YgbPerson worker, String username, WorkerAttendanceCheckRequest request)
    {
        validateTraining(worker);
        LocalDate today = LocalDate.now();
        YgbAttendanceRaw attendance = attendanceRawMapper.selectAttendanceRawByPersonAndDate(worker.getPersonId(),
            DateUtils.toDate(today));
        if (attendance == null || attendance.getClockInTime() == null)
        {
            throw new ServiceException("今日尚无上班打卡记录，不能直接下班打卡。");
        }
        if (attendance.getClockOutTime() != null)
        {
            return buildAttendanceResponse(attendance, "CHECK_OUT", isLate(attendance.getClockInTime()));
        }

        attendance.setClockOutTime(new Date());
        attendance.setAttendanceHours(calculateHours(attendance.getClockInTime(), attendance.getClockOutTime()));
        attendance.setAttendanceStatus(resolveAttendanceStatus(attendance.getClockInTime(), attendance.getClockOutTime()));
        attendance.setAnomalyRemark(mergeRemark(buildRemark(request), buildRequestExtras(request)));
        attendance.setUpdateBy(username);
        attendanceRawService.updateAttendanceRaw(attendance);

        YgbAttendanceRaw saved = attendanceRawMapper.selectAttendanceRawByPersonAndDate(worker.getPersonId(),
            DateUtils.toDate(today));
        return buildAttendanceResponse(saved, "CHECK_OUT", isLate(saved.getClockInTime()));
    }

    @Override
    public Map<String, Object> getMonthlyView(YgbPerson worker, String month)
    {
        String queryMonth = (month == null || month.isBlank()) ? LocalDate.now().format(MONTH_FORMATTER) : month;
        List<YgbAttendanceRaw> records = attendanceRawMapper.selectAttendanceRawByPersonAndMonth(worker.getPersonId(), queryMonth);
        List<Map<String, Object>> days = new ArrayList<>();
        for (YgbAttendanceRaw item : records)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("attendanceId", item.getAttendanceId());
            row.put("date", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, item.getAttendanceDate()));
            row.put("status", item.getAttendanceStatus());
            row.put("clockInTime", item.getClockInTime() == null ? null : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, item.getClockInTime()));
            row.put("clockOutTime", item.getClockOutTime() == null ? null : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, item.getClockOutTime()));
            row.put("sourceType", item.getSourceType());
            days.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("month", queryMonth);
        result.put("days", days);
        result.put("summary", attendanceRawService.selectAttendanceRawSummary(buildWorkerQuery(worker.getPersonId())));
        return result;
    }

    @Override
    public Map<String, Object> getDayDetail(YgbPerson worker, String date)
    {
        if (date == null || date.isBlank())
        {
            throw new ServiceException("日期不能为空。");
        }
        LocalDate queryDate = LocalDate.parse(date, DATE_FORMATTER);
        YgbAttendanceRaw item = attendanceRawMapper.selectAttendanceRawByPersonAndDate(worker.getPersonId(),
            DateUtils.toDate(queryDate));
        if (item == null)
        {
            throw new ServiceException("当天暂无考勤记录。");
        }
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("attendanceId", item.getAttendanceId());
        detail.put("attendanceNo", item.getAttendanceNo());
        detail.put("date", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, item.getAttendanceDate()));
        detail.put("clockInTime", item.getClockInTime() == null ? null : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, item.getClockInTime()));
        detail.put("clockOutTime", item.getClockOutTime() == null ? null : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, item.getClockOutTime()));
        detail.put("attendanceStatus", item.getAttendanceStatus());
        detail.put("sourceType", item.getSourceType());
        detail.put("attendanceHours", item.getAttendanceHours());
        detail.put("anomalyRemark", item.getAnomalyRemark());
        return detail;
    }

    private void validateTraining(YgbPerson worker)
    {
        WorkerTrainingProgress progress = workerTrainingService.getProgress(worker);
        if (progress.isNeedComplete())
        {
            throw new ServiceException("请先完成本月安全培训后再打卡。");
        }
    }

    private YgbContract resolveCurrentContract(Long personId, LocalDate workDate)
    {
        YgbContract contract = contractMapper.selectEffectiveContractByPersonId(personId, DateUtils.toDate(workDate));
        if (contract == null)
        {
            throw new ServiceException("未找到当前有效合同，无法提交打卡。");
        }
        return contract;
    }

    private YgbAttendanceRaw buildWorkerQuery(Long personId)
    {
        YgbAttendanceRaw query = new YgbAttendanceRaw();
        query.setPersonId(personId);
        return query;
    }

    private String buildRequestExtras(WorkerAttendanceCheckRequest request)
    {
        if (request == null)
        {
            return null;
        }
        List<String> parts = new ArrayList<>();
        if (request.getFaceImageUrl() != null && !request.getFaceImageUrl().isBlank())
        {
            parts.add("faceImageUrl=" + request.getFaceImageUrl());
        }
        if (request.getDeviceCode() != null && !request.getDeviceCode().isBlank())
        {
            parts.add("deviceCode=" + request.getDeviceCode());
        }
        return parts.isEmpty() ? null : String.join("; ", parts);
    }

    private String mergeRemark(String baseRemark, String extraRemark)
    {
        if (baseRemark == null || baseRemark.isBlank())
        {
            return extraRemark;
        }
        if (extraRemark == null || extraRemark.isBlank())
        {
            return baseRemark;
        }
        return baseRemark + "; " + extraRemark;
    }

    private String buildAttendanceNo(Long personId, String type)
    {
        return "APP-" + type + "-" + DateUtils.dateTimeNow() + "-" + personId + "-" + IdUtils.fastSimpleUUID().substring(0, 6);
    }

    private String buildRemark(WorkerAttendanceCheckRequest request)
    {
        if (request == null)
        {
            return null;
        }
        List<String> parts = new ArrayList<>();
        if (request.getAddress() != null && !request.getAddress().isBlank())
        {
            parts.add("地址：" + request.getAddress());
        }
        if (request.getLatitude() != null && request.getLongitude() != null)
        {
            parts.add("坐标：" + request.getLatitude() + "," + request.getLongitude());
        }
        return parts.isEmpty() ? null : String.join("；", parts);
    }

    private String resolveAttendanceStatus(Date clockInTime, Date clockOutTime)
    {
        boolean late = isLate(clockInTime);
        boolean early = isEarly(clockOutTime);
        if (late && early)
        {
            return "5";
        }
        if (late)
        {
            return "2";
        }
        if (early)
        {
            return "3";
        }
        return "1";
    }

    private boolean isLate(Date clockInTime)
    {
        if (clockInTime == null)
        {
            return false;
        }
        LocalTime time = LocalDateTime.ofInstant(clockInTime.toInstant(), ZoneId.systemDefault()).toLocalTime();
        return time.isAfter(LocalTime.of(9, 0));
    }

    private boolean isEarly(Date clockOutTime)
    {
        if (clockOutTime == null)
        {
            return false;
        }
        LocalTime time = LocalDateTime.ofInstant(clockOutTime.toInstant(), ZoneId.systemDefault()).toLocalTime();
        return time.isBefore(LocalTime.of(18, 0));
    }

    private BigDecimal calculateHours(Date start, Date end)
    {
        if (start == null || end == null)
        {
            return BigDecimal.ZERO;
        }
        long minutes = Duration.between(start.toInstant(), end.toInstant()).toMinutes();
        if (minutes < 0)
        {
            minutes = 0;
        }
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> buildAttendanceResponse(YgbAttendanceRaw item, String type, boolean lateFlag)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", item.getAttendanceId());
        result.put("type", type);
        result.put("time", "CHECK_IN".equals(type)
            ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, item.getClockInTime())
            : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, item.getClockOutTime()));
        result.put("lateFlag", lateFlag);
        result.put("attendanceStatus", item.getAttendanceStatus());
        result.put("attendanceHours", item.getAttendanceHours());
        return result;
    }
}
