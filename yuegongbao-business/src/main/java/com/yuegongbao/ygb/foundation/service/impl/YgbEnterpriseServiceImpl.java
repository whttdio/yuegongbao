package com.yuegongbao.ygb.foundation.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
import com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;

@Service
public class YgbEnterpriseServiceImpl implements IYgbEnterpriseService
{
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
    public List<YgbEnterprise> selectEnterpriseList(YgbEnterprise enterprise)
    {
        normalizeRegionQuery(enterprise);
        return enterpriseMapper.selectEnterpriseList(enterprise);
    }

    @Override
    public YgbEnterpriseSummary selectEnterpriseSummary(YgbEnterprise enterprise)
    {
        List<YgbEnterprise> list = selectEnterpriseList(enterprise);
        YgbEnterpriseSummary summary = new YgbEnterpriseSummary();
        summary.setTotalCount(list.size());

        int normalCount = 0;
        int syncErrorCount = 0;
        int employerCount = 0;
        int dispatchCount = 0;
        int serviceOrgCount = 0;
        int disabledCount = 0;
        for (YgbEnterprise item : list)
        {
            if ("0".equals(item.getStatus()))
            {
                normalCount++;
            }
            if ("2".equals(item.getSyncStatus()))
            {
                syncErrorCount++;
            }
            if ("2".equals(item.getEnterpriseType()))
            {
                employerCount++;
            }
            if ("1".equals(item.getEnterpriseType()))
            {
                dispatchCount++;
            }
            if ("3".equals(item.getEnterpriseType()))
            {
                serviceOrgCount++;
            }
            if ("1".equals(item.getStatus()))
            {
                disabledCount++;
            }
        }

        summary.setNormalCount(normalCount);
        summary.setSyncErrorCount(syncErrorCount);
        summary.setEmployerCount(employerCount);
        summary.setDispatchCount(dispatchCount);
        summary.setServiceOrgCount(serviceOrgCount);
        summary.setDisabledCount(disabledCount);
        summary.setYgbExplanation(buildYgbExplanation(enterprise, summary));
        summary.setAzbExplanation(buildAzbExplanation(enterprise, summary));
        return summary;
    }

    @Override
    public List<YgbEnterprise> selectEnterpriseOptions()
    {
        YgbEnterprise query = new YgbEnterprise();
        applyEnterpriseOptionScope(query);
        return enterpriseMapper.selectEnterpriseOptions(query);
    }

    private void applyEnterpriseOptionScope(YgbEnterprise query)
    {
        if (enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            enterpriseScopeHelper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "enterprise_id");
            return;
        }
        regionScopeHelper.applyRegionDataScope(query, "region_code");
    }

    @Override
    public YgbEnterprise selectEnterpriseById(Long enterpriseId)
    {
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(enterpriseId);
        if (enterprise != null)
        {
            dataScopeGuard.assertEntityAllowed(enterprise);
        }
        return enterprise;
    }

    @Override
    public boolean checkEnterpriseNameUnique(YgbEnterprise enterprise)
    {
        Long enterpriseId = StringUtils.isNull(enterprise.getEnterpriseId()) ? -1L : enterprise.getEnterpriseId();
        YgbEnterprise info = enterpriseMapper.checkEnterpriseNameUnique(enterprise.getEnterpriseName());
        if (StringUtils.isNotNull(info) && info.getEnterpriseId().longValue() != enterpriseId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public boolean checkEnterpriseCodeUnique(YgbEnterprise enterprise)
    {
        Long enterpriseId = StringUtils.isNull(enterprise.getEnterpriseId()) ? -1L : enterprise.getEnterpriseId();
        YgbEnterprise info = enterpriseMapper.checkEnterpriseCodeUnique(enterprise.getEnterpriseCode());
        if (StringUtils.isNotNull(info) && info.getEnterpriseId().longValue() != enterpriseId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertEnterprise(YgbEnterprise enterprise)
    {
        return enterpriseMapper.insertEnterprise(enterprise);
    }

    @Override
    public int updateEnterprise(YgbEnterprise enterprise)
    {
        YgbEnterprise existing = selectEnterpriseById(enterprise.getEnterpriseId());
        if (StringUtils.isNull(existing))
        {
            throw new ServiceException("Enterprise does not exist.");
        }
        dataScopeGuard.assertEntityAllowed(enterprise);
        return enterpriseMapper.updateEnterprise(enterprise);
    }

    @Override
    public int deleteEnterpriseByIds(Long[] enterpriseIds, String updateBy)
    {
        for (Long enterpriseId : enterpriseIds)
        {
            YgbEnterprise enterprise = selectEnterpriseById(enterpriseId);
            if (StringUtils.isNull(enterprise))
            {
                continue;
            }
            if (personMapper.countPersonByEnterpriseId(enterpriseId) > 0)
            {
                throw new ServiceException(String.format(
                    "Enterprise [%s] still has linked persons and cannot be deleted.", enterprise.getEnterpriseName()));
            }
        }
        return enterpriseMapper.deleteEnterpriseByIds(enterpriseIds, updateBy);
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbEnterprise query, YgbEnterpriseSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("dispatch", "派遣单位覆盖", summary.getDispatchCount(), "持续完整",
            "先校准派遣单位主体，保证后续人员归属、合同备案和考勤链路承接稳定。", "enterprise", "enterprise",
            "530.1 企业主数据解释", baseQuery));
        list.add(explanationItem("employer", "用工单位覆盖", summary.getEmployerCount(), "持续完整",
            "用工单位主体影响工资、社保税务和扩面减损解释顺序，应优先固定办理口径。", "enterprise", "enterprise",
            "530.1 企业主数据解释", baseQuery));
        list.add(explanationItem("syncError", "同步异常", summary.getSyncErrorCount(), 0,
            "同步异常会阻断企业主数据向后续办理模块透传，应优先回到企业台账修正。", "enterprise", "enterprise",
            "530.1 企业主数据解释", baseQuery));
        list.add(explanationItem("disabled", "停用企业", summary.getDisabledCount(), 0,
            "停用主体应先核查是否仍挂载人员、合同或办理记录，避免企业办理链落到失效主体。", "enterprise", "enterprise",
            "530.1 企业主数据解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbEnterprise query, YgbEnterpriseSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("syncError", "同步异常企业", summary.getSyncErrorCount(), 0,
            "同步异常会持续污染区域治理对象和下游协同台账，应优先修正归属和同步链路。", "enterprise", "enterprise",
            "6.1 企业对象解释", baseQuery));
        list.add(explanationItem("employer", "重点用工主体", summary.getEmployerCount(), "持续核实",
            "用工单位是现场治理主对象，应先核定对象范围再联动设备、保险和预警。", "enterprise", "enterprise",
            "6.1 企业对象解释", baseQuery));
        list.add(explanationItem("dispatch", "派遣协同主体", summary.getDispatchCount(), "持续核实",
            "派遣主体影响人员归属和现场准入链，应与治理对象一并复核。", "enterprise", "enterprise",
            "6.1 企业对象解释", baseQuery));
        list.add(explanationItem("disabled", "停用对象", summary.getDisabledCount(), 0,
            "停用主体残留在治理台账中会影响区域态势，应优先清退和校正。", "enterprise", "enterprise",
            "6.1 企业对象解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbEnterprise query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (StringUtils.isNotEmpty(query.getEnterpriseType()))
        {
            map.put("enterpriseType", query.getEnterpriseType());
        }
        if (StringUtils.isNotEmpty(query.getSyncStatus()))
        {
            map.put("syncStatus", query.getSyncStatus());
        }
        if (StringUtils.isNotEmpty(query.getStatus()))
        {
            map.put("status", query.getStatus());
        }
        return map;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> baseQuery)
    {
        Map<String, Object> defaultQuery = new LinkedHashMap<>(baseQuery);
        defaultQuery.put("focusKey", focusKey);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", focusKey);
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }

    private void normalizeRegionQuery(YgbEnterprise enterprise)
    {
        if (enterprise == null || StringUtils.isEmpty(enterprise.getRegionCode()))
        {
            return;
        }
        enterprise.setRegionCode(YgbRegionHelper.toRegionPrefix(enterprise.getRegionCode()));
    }
}
