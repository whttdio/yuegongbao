package com.yuegongbao.ygb.extension.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecordSummary;
import com.yuegongbao.ygb.extension.mapper.YgbModuleRecordMapper;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbModuleRecordServiceImpl implements IYgbModuleRecordService
{
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final Set<String> VALID_WORKFLOW_STATUS = Set.of(
        "draft", "pending", "processing", "closed", "rejected", "overdue", "approved", "done");

    private static final Set<String> VALID_STATUS = Set.of("0", "1", "closed", "done", "processed");

    @Autowired
    private YgbModuleRecordMapper moduleRecordMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbModuleRecord> selectModuleRecordList(YgbModuleRecord query)
    {
        YgbModuleRecord scopedQuery = query == null ? new YgbModuleRecord() : query;
        applyListScope(scopedQuery);
        return moduleRecordMapper.selectModuleRecordList(scopedQuery);
    }

    @Override
    public YgbModuleRecordSummary selectModuleRecordSummary(YgbModuleRecord query)
    {
        List<YgbModuleRecord> list = selectModuleRecordList(query);
        YgbModuleRecordSummary summary = new YgbModuleRecordSummary();
        int enabledCount = 0;
        int pendingCount = 0;
        int processingCount = 0;
        int closedCount = 0;
        int overdueCount = 0;
        for (YgbModuleRecord item : list)
        {
            if ("0".equals(item.getStatus()))
            {
                enabledCount++;
            }
            if ("pending".equals(item.getWorkflowStatus()))
            {
                pendingCount++;
            }
            if ("processing".equals(item.getWorkflowStatus()))
            {
                processingCount++;
            }
            if ("closed".equals(item.getWorkflowStatus()))
            {
                closedCount++;
            }
            if ("overdue".equals(item.getWorkflowStatus()))
            {
                overdueCount++;
            }
        }
        summary.setTotalCount(list.size());
        summary.setEnabledCount(enabledCount);
        summary.setPendingCount(pendingCount);
        summary.setProcessingCount(processingCount);
        summary.setClosedCount(closedCount);
        summary.setOverdueCount(overdueCount);
        return summary;
    }

    @Override
    public YgbModuleRecord selectModuleRecordById(Long recordId)
    {
        YgbModuleRecord record = moduleRecordMapper.selectModuleRecordById(recordId);
        if (record == null)
        {
            throw new ServiceException("扩展记录不存在");
        }
        dataScopeGuard.assertEntityAllowed(record);
        return record;
    }

    @Override
    public int insertModuleRecord(YgbModuleRecord record, String operator)
    {
        applyScopedEnterprise(record);
        normalizeRecord(record);
        dataScopeGuard.assertEntityAllowed(record);
        record.setCreateBy(operator);
        return moduleRecordMapper.insertModuleRecord(record);
    }

    @Override
    public int updateModuleRecord(YgbModuleRecord record, String operator)
    {
        selectModuleRecordById(record.getRecordId());
        applyScopedEnterprise(record);
        normalizeRecord(record);
        dataScopeGuard.assertEntityAllowed(record);
        record.setUpdateBy(operator);
        return moduleRecordMapper.updateModuleRecord(record);
    }

    @Override
    public int deleteModuleRecordByIds(Long[] recordIds, String operator)
    {
        for (Long recordId : recordIds)
        {
            selectModuleRecordById(recordId);
        }
        return moduleRecordMapper.deleteModuleRecordByIds(recordIds, operator);
    }

    private void normalizeRecord(YgbModuleRecord record)
    {
        if (record == null)
        {
            throw new ServiceException("扩展记录不能为空");
        }
        if (StringUtils.isEmpty(record.getRecordType()))
        {
            throw new ServiceException("记录类型不能为空");
        }
        if (StringUtils.isEmpty(record.getRecordName()))
        {
            throw new ServiceException("记录名称不能为空");
        }
        normalizeStatMonth(record);
        validateStatusFields(record);
        validatePayloadJson(record);
        fillMasterDataSnapshot(record);
        if (StringUtils.isEmpty(record.getStatus()))
        {
            record.setStatus("0");
        }
        if (StringUtils.isEmpty(record.getWorkflowStatus()))
        {
            record.setWorkflowStatus("draft");
        }
        if (record.getSortOrder() == null)
        {
            record.setSortOrder(0);
        }
    }

    private void normalizeStatMonth(YgbModuleRecord record)
    {
        if (StringUtils.isEmpty(record.getStatMonth()))
        {
            record.setStatMonth(LocalDate.now().format(MONTH_FORMATTER));
            return;
        }
        if (!record.getStatMonth().matches("^\\d{4}-\\d{2}$"))
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM");
        }
    }

    private void validateStatusFields(YgbModuleRecord record)
    {
        if (StringUtils.isNotEmpty(record.getWorkflowStatus()) && !VALID_WORKFLOW_STATUS.contains(record.getWorkflowStatus()))
        {
            throw new ServiceException("流程状态不合法");
        }
        if (StringUtils.isNotEmpty(record.getStatus()) && !VALID_STATUS.contains(record.getStatus()))
        {
            throw new ServiceException("启停状态不合法");
        }
        if (record.getSortOrder() != null && record.getSortOrder() < 0)
        {
            throw new ServiceException("排序不能小于 0");
        }
    }

    private void validatePayloadJson(YgbModuleRecord record)
    {
        if (StringUtils.isEmpty(record.getPayloadJson()))
        {
            return;
        }
        try
        {
            com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(record.getPayloadJson());
        }
        catch (Exception ex)
        {
            throw new ServiceException("扩展JSON格式不合法");
        }
    }

    private void fillMasterDataSnapshot(YgbModuleRecord record)
    {
        if (record.getEnterpriseId() != null)
        {
            YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(record.getEnterpriseId());
            if (enterprise == null)
            {
                throw new ServiceException("关联企业不存在");
            }
            record.setEnterpriseName(enterprise.getEnterpriseName());
            record.setRegionCode(enterprise.getRegionCode());
        }
        else if (StringUtils.isEmpty(record.getRegionCode()))
        {
            record.setRegionCode(resolveAuthorizedRegionCode(null));
        }

        if (record.getPersonId() == null)
        {
            return;
        }
        YgbPerson person = personMapper.selectPersonById(record.getPersonId());
        if (person == null)
        {
            throw new ServiceException("关联人员不存在");
        }
        if (record.getEnterpriseId() != null && person.getEnterpriseId() != null
            && !record.getEnterpriseId().equals(person.getEnterpriseId()))
        {
            throw new ServiceException("关联人员与企业不一致");
        }
        if (record.getEnterpriseId() == null && person.getEnterpriseId() != null)
        {
            YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(person.getEnterpriseId());
            if (enterprise == null)
            {
                throw new ServiceException("关联人员所属企业不存在");
            }
            record.setEnterpriseId(enterprise.getEnterpriseId());
            record.setEnterpriseName(enterprise.getEnterpriseName());
            record.setRegionCode(enterprise.getRegionCode());
        }
        record.setPersonName(person.getPersonName());
    }

    private String resolveAuthorizedRegionCode(String regionCode)
    {
        if (regionScopeHelper == null)
        {
            return StringUtils.defaultIfEmpty(regionCode, "440000");
        }
        return regionScopeHelper.resolveAuthorizedRegionCode(regionCode);
    }

    private void applyScopedEnterprise(YgbModuleRecord record)
    {
        if (!enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            return;
        }
        Long scopedEnterpriseId = enterpriseScopeHelper.resolveScopedEnterpriseId();
        if (scopedEnterpriseId == null)
        {
            throw new ServiceException("当前用户未配置企业权限");
        }
        record.setEnterpriseId(scopedEnterpriseId);
    }

    private void applyListScope(YgbModuleRecord query)
    {
        if (regionScopeHelper != null)
        {
            regionScopeHelper.applyRegionDataScope(query, "region_code");
        }
        if (enterpriseScopeHelper != null)
        {
            enterpriseScopeHelper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "enterprise_id");
        }
    }
}
