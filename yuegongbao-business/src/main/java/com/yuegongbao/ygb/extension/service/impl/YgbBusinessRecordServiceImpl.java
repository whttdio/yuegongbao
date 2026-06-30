package com.yuegongbao.ygb.extension.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.extension.domain.YgbBusinessRecord;
import com.yuegongbao.ygb.extension.domain.YgbBusinessRecordModule;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecordSummary;
import com.yuegongbao.ygb.extension.mapper.YgbBusinessRecordMapper;
import com.yuegongbao.ygb.extension.service.IYgbBusinessRecordService;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbBusinessRecordServiceImpl implements IYgbBusinessRecordService
{
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final Set<String> VALID_WORKFLOW_STATUS = Set.of(
        "draft", "pending", "processing", "closed", "rejected", "overdue");

    private static final Set<String> VALID_STATUS = Set.of("0", "1");

    private static final Set<String> VALID_RISK_LEVEL = Set.of("0", "1", "2", "3");

    private static final Set<YgbBusinessRecordModule> ENTERPRISE_OPTIONAL_MODULES = Set.of(
        YgbBusinessRecordModule.CREDIT_RULE,
        YgbBusinessRecordModule.OPERATION_JOB_CATEGORY,
        YgbBusinessRecordModule.OPERATION_MAINTENANCE_STATS,
        YgbBusinessRecordModule.OPERATION_RECRUIT_STATS,
        YgbBusinessRecordModule.PLATFORM_DOCUMENT,
        YgbBusinessRecordModule.PLATFORM_EXCHANGE,
        YgbBusinessRecordModule.PLATFORM_SECURITY_AUDIT,
        YgbBusinessRecordModule.PLATFORM_BACKUP);

    @Autowired
    private YgbBusinessRecordMapper businessRecordMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbBusinessRecord> selectBusinessRecordList(String moduleCode, YgbBusinessRecord query)
    {
        YgbBusinessRecordModule module = YgbBusinessRecordModule.resolve(moduleCode);
        YgbBusinessRecord scopedQuery = query == null ? new YgbBusinessRecord() : query;
        scopedQuery.setModuleCode(module.getModuleCode());
        regionScopeHelper.applyRegionDataScope(scopedQuery, "region_code");
        enterpriseScopeHelper.applyEnterpriseDataScope(scopedQuery, EnterpriseScopeMode.SINGLE, "enterprise_id");
        return businessRecordMapper.selectBusinessRecordList(module.getTableName(), scopedQuery);
    }

    @Override
    public YgbModuleRecordSummary selectBusinessRecordSummary(String moduleCode, YgbBusinessRecord query)
    {
        List<YgbBusinessRecord> list = selectBusinessRecordList(moduleCode, query);
        YgbModuleRecordSummary summary = new YgbModuleRecordSummary();
        int enabledCount = 0;
        int pendingCount = 0;
        int processingCount = 0;
        int closedCount = 0;
        int overdueCount = 0;
        for (YgbBusinessRecord item : list)
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
    public YgbBusinessRecord selectBusinessRecordById(String moduleCode, Long businessId)
    {
        YgbBusinessRecordModule module = YgbBusinessRecordModule.resolve(moduleCode);
        YgbBusinessRecord record = businessRecordMapper.selectBusinessRecordById(module.getTableName(), businessId);
        if (record == null)
        {
            throw new ServiceException("业务记录不存在");
        }
        dataScopeGuard.assertEntityAllowed(record);
        return record;
    }

    @Override
    public int insertBusinessRecord(String moduleCode, YgbBusinessRecord record, String operator)
    {
        YgbBusinessRecordModule module = YgbBusinessRecordModule.resolve(moduleCode);
        applyScopedEnterprise(record);
        normalizeRecord(record, module);
        dataScopeGuard.assertEntityAllowed(record);
        record.setCreateBy(operator);
        return businessRecordMapper.insertBusinessRecord(module.getTableName(), record);
    }

    @Override
    public int updateBusinessRecord(String moduleCode, YgbBusinessRecord record, String operator)
    {
        YgbBusinessRecordModule module = YgbBusinessRecordModule.resolve(moduleCode);
        if (record.getBusinessId() == null)
        {
            throw new ServiceException("业务记录ID不能为空");
        }
        assertBusinessRecordAllowed(module, record.getBusinessId());
        applyScopedEnterprise(record);
        normalizeRecord(record, module);
        dataScopeGuard.assertEntityAllowed(record);
        record.setUpdateBy(operator);
        return businessRecordMapper.updateBusinessRecord(module.getTableName(), record);
    }

    @Override
    public int deleteBusinessRecordByIds(String moduleCode, Long[] businessIds, String operator)
    {
        YgbBusinessRecordModule module = YgbBusinessRecordModule.resolve(moduleCode);
        if (businessIds == null || businessIds.length == 0)
        {
            throw new ServiceException("请选择要删除的业务记录");
        }
        for (Long businessId : businessIds)
        {
            assertBusinessRecordAllowed(module, businessId);
        }
        return businessRecordMapper.deleteBusinessRecordByIds(module.getTableName(), businessIds, operator);
    }

    private void assertBusinessRecordAllowed(YgbBusinessRecordModule module, Long businessId)
    {
        if (businessId == null)
        {
            throw new ServiceException("业务记录ID不能为空");
        }
        YgbBusinessRecord existing = businessRecordMapper.selectBusinessRecordById(module.getTableName(), businessId);
        if (existing == null)
        {
            throw new ServiceException("业务记录不存在");
        }
        dataScopeGuard.assertEntityAllowed(existing);
    }

    private void normalizeRecord(YgbBusinessRecord record, YgbBusinessRecordModule module)
    {
        if (record == null)
        {
            throw new ServiceException("业务记录不能为空");
        }
        record.setModuleCode(module.getModuleCode());
        if (StringUtils.isEmpty(record.getBusinessName()))
        {
            throw new ServiceException("业务名称不能为空");
        }
        normalizeStatMonth(record);
        validateStatusFields(record);
        validateNumericFields(record);
        validateTimeRange(record);
        fillMasterDataSnapshot(record, module);
        if (StringUtils.isEmpty(record.getBusinessNo()))
        {
            record.setBusinessNo(module.getModuleCode() + "-" + System.currentTimeMillis());
        }
        if (StringUtils.isEmpty(record.getPortalCode()))
        {
            record.setPortalCode("ygb");
        }
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

    private void normalizeStatMonth(YgbBusinessRecord record)
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

    private void validateStatusFields(YgbBusinessRecord record)
    {
        if (StringUtils.isNotEmpty(record.getWorkflowStatus()) && !VALID_WORKFLOW_STATUS.contains(record.getWorkflowStatus()))
        {
            throw new ServiceException("流程状态不合法");
        }
        if (StringUtils.isNotEmpty(record.getStatus()) && !VALID_STATUS.contains(record.getStatus()))
        {
            throw new ServiceException("启停状态不合法");
        }
        if (StringUtils.isNotEmpty(record.getRiskLevel()) && !VALID_RISK_LEVEL.contains(record.getRiskLevel()))
        {
            throw new ServiceException("风险等级不合法");
        }
    }

    private void validateNumericFields(YgbBusinessRecord record)
    {
        if (isNegative(record.getAmount()))
        {
            throw new ServiceException("金额不能小于 0");
        }
        if (isNegative(record.getQuantity()))
        {
            throw new ServiceException("数量不能小于 0");
        }
    }

    private boolean isNegative(BigDecimal value)
    {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    private void validateTimeRange(YgbBusinessRecord record)
    {
        if (record.getStartTime() != null && record.getEndTime() != null
            && record.getEndTime().before(record.getStartTime()))
        {
            throw new ServiceException("结束时间不能早于开始时间");
        }
    }

    private void fillMasterDataSnapshot(YgbBusinessRecord record, YgbBusinessRecordModule module)
    {
        if (record.getEnterpriseId() == null)
        {
            if (!ENTERPRISE_OPTIONAL_MODULES.contains(module))
            {
                throw new ServiceException("请选择关联企业");
            }
            if (StringUtils.isEmpty(record.getRegionCode()))
            {
                record.setRegionCode(resolveAuthorizedRegionCode(null));
            }
            return;
        }
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(record.getEnterpriseId());
        if (enterprise == null)
        {
            throw new ServiceException("关联企业不存在");
        }
        record.setEnterpriseName(enterprise.getEnterpriseName());
        record.setRegionCode(enterprise.getRegionCode());

        if (record.getPersonId() == null)
        {
            return;
        }
        YgbPerson person = personMapper.selectPersonById(record.getPersonId());
        if (person == null)
        {
            throw new ServiceException("关联人员不存在");
        }
        if (person.getEnterpriseId() != null && !person.getEnterpriseId().equals(record.getEnterpriseId()))
        {
            throw new ServiceException("关联人员与企业不一致");
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

    private void applyScopedEnterprise(YgbBusinessRecord record)
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
}
