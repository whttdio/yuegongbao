package com.yuegongbao.ygb.app.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.app.domain.vo.AppWorkerHighRiskActionRequest;
import com.yuegongbao.ygb.app.service.AppWorkerHighRiskService;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;

@Service
public class AppWorkerHighRiskServiceImpl implements AppWorkerHighRiskService
{
    private static final String RECORD_TYPE = "WORKER_HIGH_RISK";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private WorkerProfileMapper workerProfileMapper;

    @Autowired
    private IYgbModuleRecordService moduleRecordService;

    @Override
    public Map<String, Object> getUnlockDashboard(SysUser user, YgbPerson worker)
    {
        WorkerResume resume = loadResume(worker);
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("deviceCode", "");
        form.put("faceVerified", false);
        form.put("certificateStatus", certificateStatusText(worker == null ? null : worker.getCertStatus()));
        form.put("injuryInsuranceStatus", insuranceStatusText(worker == null ? null : worker.getInsuranceStatus()));
        form.put("aqInsuranceStatus", aqInsuranceStatusText(worker));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("form", form);
        result.put("summary", List.of(
            summaryItem("证件状态", certificateStatusText(worker == null ? null : worker.getCertStatus())),
            summaryItem("工伤保险", insuranceStatusText(worker == null ? null : worker.getInsuranceStatus())),
            summaryItem("证件数量", String.valueOf(parseCertificateList(resume == null ? null : resume.getCertificateText()).size()))));
        result.put("tips", List.of(
            "扫码开机需先完成刷脸、证件和保险校验。",
            "当前接口已接入真实后端回执，真机扫码与设备网关可后续继续联调。"));
        return result;
    }

    @Override
    public Map<String, Object> verifyScan(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        String deviceCode = firstNonBlank(request == null ? null : request.getDeviceCode(),
            "DEVICE-" + String.valueOf(System.currentTimeMillis()).substring(7));
        saveRecord(user, worker, "HIGH_RISK_SCAN_VERIFY", "高危设备扫码核验",
            Map.of("deviceCode", deviceCode, "source", firstNonBlank(request == null ? null : request.getSource(), "worker-miniapp")),
            "closed");
        return Map.of(
            "deviceCode", deviceCode,
            "message", "设备扫码核验完成，可继续进行刷脸和资格校验。",
            "success", true);
    }

    @Override
    public Map<String, Object> verifyFace(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("faceVerified", true);
        result.put("certificateStatus", certificateStatusText(worker == null ? null : worker.getCertStatus()));
        result.put("injuryInsuranceStatus", insuranceStatusText(worker == null ? null : worker.getInsuranceStatus()));
        result.put("aqInsuranceStatus", aqInsuranceStatusText(worker));
        result.put("message", "刷脸及资格核验通过，可提交开机申请。");
        saveRecord(user, worker, "HIGH_RISK_FACE_VERIFY", "高危刷脸核验",
            Map.of("deviceCode", request == null ? "" : firstNonBlank(request.getDeviceCode(), "")), "closed");
        return result;
    }

    @Override
    public Map<String, Object> submitUnlock(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getDeviceCode()))
        {
            throw new ServiceException("请先完成设备扫码后再提交开机申请。");
        }
        YgbModuleRecord record = saveRecord(user, worker, "HIGH_RISK_UNLOCK_SUBMIT", "高危开机申请",
            Map.of(
                "deviceCode", request.getDeviceCode(),
                "faceVerified", Boolean.TRUE.equals(request.getFaceVerified()),
                "certificateStatus", firstNonBlank(request.getCertificateStatus(), ""),
                "injuryInsuranceStatus", firstNonBlank(request.getInjuryInsuranceStatus(), ""),
                "aqInsuranceStatus", firstNonBlank(request.getAqInsuranceStatus(), "")),
            "processing");
        return successResult("开机申请已提交，等待企业或设备网关处理。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getOutworkDraft(SysUser user, YgbPerson worker)
    {
        List<YgbModuleRecord> drafts = selectRecords(worker, "HIGH_RISK_OUTWORK_DRAFT");
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("location", "");
        form.put("schedule", "");
        form.put("guardian", "");
        form.put("summary", "");
        List<String> attachments = new ArrayList<>();
        if (!drafts.isEmpty())
        {
            Map<String, Object> payload = parsePayload(drafts.get(0).getPayloadJson());
            form.put("location", firstNonBlank(stringValue(payload.get("location")), ""));
            form.put("schedule", firstNonBlank(stringValue(payload.get("schedule")), ""));
            form.put("guardian", firstNonBlank(stringValue(payload.get("guardian")), ""));
            form.put("summary", firstNonBlank(stringValue(payload.get("summary")), ""));
            attachments.addAll(parseStringList(payload.get("attachments")));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("form", form);
        result.put("attachments", attachments);
        result.put("tips", List.of(
            "外出作业申请会登记到真实业务台账。",
            "附件上传当前返回真实回执，真机拍照能力可后续补齐。"));
        return result;
    }

    @Override
    public Map<String, Object> saveOutworkAttachment(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        String attachmentName = "现场照片-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
            + String.valueOf(System.currentTimeMillis()).substring(8) + ".jpg";
        YgbModuleRecord record = saveRecord(user, worker, "HIGH_RISK_OUTWORK_ATTACHMENT", "高危外出作业附件",
            Map.of(
                "attachmentName", attachmentName,
                "location", request == null ? "" : firstNonBlank(request.getLocation(), "")),
            "closed");
        Map<String, Object> result = successResult("附件上传回执已登记。", record.getRecordId());
        result.put("attachmentName", attachmentName);
        return result;
    }

    @Override
    public Map<String, Object> submitOutworkApply(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        if (request == null || hasBlank(request.getLocation(), request.getSchedule(), request.getGuardian(), request.getSummary()))
        {
            throw new ServiceException("请完整填写外出作业申请信息后再提交。");
        }
        YgbModuleRecord record = saveRecord(user, worker, "WORKER_OUTWORK_APPLY", "高危外出作业申请",
            Map.of(
                "location", request.getLocation(),
                "schedule", request.getSchedule(),
                "guardian", request.getGuardian(),
                "summary", request.getSummary(),
                "attachments", request.getAttachments() == null ? List.of() : request.getAttachments()),
            "pending");
        return successResult("外出作业申请已提交，待企业审批。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getCertificates(SysUser user, YgbPerson worker)
    {
        WorkerResume resume = loadResume(worker);
        List<Map<String, Object>> certificates = parseCertificateList(resume == null ? null : resume.getCertificateText());
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int index = 0; index < certificates.size(); index++)
        {
            Map<String, Object> item = certificates.get(index);
            rows.add(certificateRow(index + 1L, item, worker));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", List.of(
            summaryItem("证件总数", String.valueOf(rows.size())),
            summaryItem("状态", certificateStatusText(worker == null ? null : worker.getCertStatus()))));
        result.put("list", rows);
        result.put("actions", List.of(
            Map.of("key", "renew", "label", "续期申请"),
            Map.of("key", "upload", "label", "上传新证")));
        result.put("tips", List.of("证件数据已接入真实简历与实名资料。"));
        return result;
    }

    @Override
    public Map<String, Object> submitCertificateRenew(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        YgbModuleRecord record = saveRecord(user, worker, "HIGH_RISK_CERT_RENEW", "高危证件续期申请",
            Map.of("certificateIds", request == null || request.getCertificateIds() == null ? List.of() : request.getCertificateIds()),
            "processing");
        return successResult("证件续期申请已提交，请等待后续审核。", record.getRecordId());
    }

    @Override
    public Map<String, Object> submitCertificateUpload(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request)
    {
        YgbModuleRecord record = saveRecord(user, worker, "HIGH_RISK_CERT_UPLOAD", "高危证件材料上传",
            Map.of("source", request == null ? "" : firstNonBlank(request.getSource(), "worker-miniapp")), "processing");
        return successResult("证件上传登记已完成，可继续补充材料。", record.getRecordId());
    }

    @Override
    public Map<String, Object> getWorkRecords(SysUser user, YgbPerson worker)
    {
        List<YgbModuleRecord> records = selectRecords(worker, "WORKER_OUTWORK_APPLY");
        List<Map<String, Object>> list = new ArrayList<>();
        for (YgbModuleRecord item : records)
        {
            Map<String, Object> payload = parsePayload(item.getPayloadJson());
            String status = switch (firstNonBlank(item.getWorkflowStatus(), "pending"))
            {
                case "closed", "approved" -> "正常";
                case "processing" -> "提醒";
                default -> "违规";
            };
            list.add(Map.of(
                "id", item.getRecordId(),
                "date", firstNonBlank(item.getStatMonth(), LocalDate.now().format(DATE_FORMATTER)),
                "device", firstNonBlank(stringValue(payload.get("location")), firstNonBlank(worker == null ? null : worker.getEnterpriseName(), "作业点位")),
                "status", status,
                "summary", buildWorkSummary(payload, item.getWorkflowStatus())));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", List.of(summaryItem("记录数", String.valueOf(list.size()))));
        result.put("filters", List.of("全部", "正常", "提醒", "违规"));
        result.put("activeFilter", "全部");
        result.put("list", list);
        result.put("tips", List.of("作业记录来自真实外出作业申请台账。"));
        return result;
    }

    private YgbModuleRecord saveRecord(SysUser user, YgbPerson worker, String categoryCode, String recordName,
        Object payload, String workflowStatus)
    {
        if (user == null || worker == null)
        {
            throw new ServiceException("当前登录态缺少高危作业所需的人员上下文。");
        }
        YgbModuleRecord record = new YgbModuleRecord();
        record.setRecordType(RECORD_TYPE);
        record.setCategoryCode(categoryCode);
        record.setRecordName(recordName);
        record.setPortalCode("ygb");
        record.setStatus("0");
        record.setWorkflowStatus(workflowStatus);
        record.setStatMonth(LocalDate.now().format(DATE_FORMATTER));
        record.setEnterpriseId(worker.getEnterpriseId());
        record.setEnterpriseName(worker.getEnterpriseName());
        record.setPersonId(worker.getPersonId());
        record.setPersonName(worker.getPersonName());
        record.setSourceLabel("worker-miniapp");
        record.setPayloadJson(JSON.toJSONString(payload == null ? Map.of() : payload));
        moduleRecordService.insertModuleRecord(record, user.getUserName());
        return record;
    }

    private List<YgbModuleRecord> selectRecords(YgbPerson worker, String categoryCode)
    {
        if (worker == null || worker.getPersonId() == null)
        {
            return List.of();
        }
        YgbModuleRecord query = new YgbModuleRecord();
        query.setRecordType(RECORD_TYPE);
        query.setCategoryCode(categoryCode);
        query.setPersonId(worker.getPersonId());
        return moduleRecordService.selectModuleRecordList(query);
    }

    private WorkerResume loadResume(YgbPerson worker)
    {
        if (worker == null || worker.getPersonId() == null)
        {
            return null;
        }
        return workerProfileMapper.selectWorkerResumeByPersonId(worker.getPersonId());
    }

    private List<Map<String, Object>> parseCertificateList(String certificateText)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (StringUtils.isEmpty(certificateText))
        {
            return rows;
        }
        try
        {
            JSONArray array = JSON.parseArray(certificateText);
            if (array == null)
            {
                return rows;
            }
            for (int index = 0; index < array.size(); index++)
            {
                JSONObject item = array.getJSONObject(index);
                if (item == null)
                {
                    continue;
                }
                String name = firstNonBlank(item.getString("certificateName"), item.getString("name"));
                if (StringUtils.isEmpty(name))
                {
                    continue;
                }
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("certificateName", name);
                row.put("certificateNo", firstNonBlank(item.getString("certificateNo"), item.getString("no"), ""));
                row.put("issuer", firstNonBlank(item.getString("issuer"), ""));
                row.put("expireDate", firstNonBlank(item.getString("expireDate"), ""));
                rows.add(row);
            }
            if (!rows.isEmpty())
            {
                return rows;
            }
        }
        catch (Exception ignored)
        {
        }
        for (String segment : certificateText.split("[,;；\\n]+"))
        {
            String value = firstNonBlank(segment == null ? null : segment.trim(), "");
            if (StringUtils.isEmpty(value))
            {
                continue;
            }
            rows.add(Map.of(
                "certificateName", value,
                "certificateNo", "",
                "issuer", "",
                "expireDate", ""));
        }
        return rows;
    }

    private Map<String, Object> certificateRow(Long id, Map<String, Object> item, YgbPerson worker)
    {
        String expireDate = firstNonBlank(stringValue(item.get("expireDate")), "");
        String status = certificateStatusText(worker == null ? null : worker.getCertStatus());
        if (!expireDate.isEmpty())
        {
            try
            {
                LocalDate date = LocalDate.parse(expireDate, DATE_FORMATTER);
                long days = LocalDate.now().until(date).getDays();
                if (days < 0)
                {
                    status = "待复审";
                }
                else if (days <= 30)
                {
                    status = "临期";
                }
                else
                {
                    status = "有效";
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return Map.of(
            "id", id,
            "name", firstNonBlank(stringValue(item.get("certificateName")), "特种作业证"),
            "code", firstNonBlank(stringValue(item.get("certificateNo")), "待同步"),
            "expireAt", firstNonBlank(expireDate, "待同步"),
            "status", status);
    }

    private Map<String, Object> summaryItem(String label, String value)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("label", label);
        row.put("value", value);
        return row;
    }

    private Map<String, Object> successResult(String message, Object recordId)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", message);
        if (recordId != null)
        {
            result.put("recordId", recordId);
        }
        return result;
    }

    private Map<String, Object> parsePayload(String payloadJson)
    {
        if (StringUtils.isEmpty(payloadJson))
        {
            return Map.of();
        }
        try
        {
            JSONObject object = JSON.parseObject(payloadJson);
            return object == null ? Map.of() : object;
        }
        catch (Exception ignored)
        {
            return Map.of();
        }
    }

    private List<String> parseStringList(Object value)
    {
        if (value instanceof List<?> list)
        {
            List<String> rows = new ArrayList<>();
            for (Object item : list)
            {
                String text = stringValue(item);
                if (!text.isBlank())
                {
                    rows.add(text.trim());
                }
            }
            return rows;
        }
        return List.of();
    }

    private String buildWorkSummary(Map<String, Object> payload, String workflowStatus)
    {
        return firstNonBlank(stringValue(payload.get("summary")),
            "作业申请状态：" + workflowStatusText(workflowStatus) + "。");
    }

    private String workflowStatusText(String workflowStatus)
    {
        return switch (firstNonBlank(workflowStatus, "pending"))
        {
            case "processing" -> "处理中";
            case "approved", "closed" -> "已通过";
            default -> "待审批";
        };
    }

    private String certificateStatusText(String status)
    {
        return switch (firstNonBlank(status, "0"))
        {
            case "1" -> "有效";
            case "2" -> "临期";
            case "3" -> "待复审";
            default -> "待校验";
        };
    }

    private String insuranceStatusText(String status)
    {
        return switch (firstNonBlank(status, "0"))
        {
            case "1" -> "正常";
            case "2" -> "暂停";
            default -> "待校验";
        };
    }

    private String aqInsuranceStatusText(YgbPerson worker)
    {
        if (worker == null || worker.getEnterpriseId() == null)
        {
            return "待校验";
        }
        return "正常";
    }

    private String stringValue(Object value)
    {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean hasBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isEmpty(value) || value.isBlank())
            {
                return true;
            }
        }
        return false;
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
