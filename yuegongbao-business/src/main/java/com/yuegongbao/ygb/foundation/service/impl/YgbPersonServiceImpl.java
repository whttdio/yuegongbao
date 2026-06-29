package com.yuegongbao.ygb.foundation.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.domain.YgbPersonSummary;
import com.yuegongbao.ygb.foundation.service.IYgbPersonService;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionHelper;

@Service
public class YgbPersonServiceImpl implements IYgbPersonService
{
    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Override
    public List<YgbPerson> selectPersonList(YgbPerson person)
    {
        normalizeRegionQuery(person);
        return personMapper.selectPersonList(person);
    }

    @Override
    public YgbPersonSummary selectPersonSummary(YgbPerson person)
    {
        normalizeRegionQuery(person);
        List<YgbPerson> list = personMapper.selectPersonList(person);
        YgbPersonSummary summary = new YgbPersonSummary();
        summary.setTotalCount(list.size());

        int onPostCount = 0;
        int certRiskCount = 0;
        int uninsuredCount = 0;
        int dispatchWorkerCount = 0;
        int outsourcingWorkerCount = 0;
        Set<Long> enterpriseIds = list.stream().map(YgbPerson::getEnterpriseId).filter(item -> item != null)
            .collect(Collectors.toSet());
        for (YgbPerson item : list)
        {
            if ("0".equals(item.getEmploymentStatus()))
            {
                onPostCount++;
            }
            if ("2".equals(item.getCertStatus()) || "3".equals(item.getCertStatus()))
            {
                certRiskCount++;
            }
            if ("0".equals(item.getInsuranceStatus()))
            {
                uninsuredCount++;
            }
            if ("1".equals(item.getWorkerType()))
            {
                dispatchWorkerCount++;
            }
            if ("3".equals(item.getWorkerType()))
            {
                outsourcingWorkerCount++;
            }
        }

        summary.setOnPostCount(onPostCount);
        summary.setCertRiskCount(certRiskCount);
        summary.setUninsuredCount(uninsuredCount);
        summary.setEnterpriseCount(enterpriseIds.size());
        summary.setDispatchWorkerCount(dispatchWorkerCount);
        summary.setOutsourcingWorkerCount(outsourcingWorkerCount);
        summary.setYgbExplanation(buildYgbExplanation(person, summary));
        summary.setAzbExplanation(buildAzbExplanation(person, summary));
        return summary;
    }

    @Override
    public List<YgbPerson> selectPersonOptions(Long enterpriseId)
    {
        return personMapper.selectPersonOptions(enterpriseId);
    }

    @Override
    public YgbPerson selectPersonById(Long personId)
    {
        YgbPerson person = personMapper.selectPersonById(personId);
        if (person != null)
        {
            dataScopeGuard.assertEntityAllowed(person);
        }
        return person;
    }

    @Override
    public boolean checkPersonIdCardUnique(YgbPerson person)
    {
        Long personId = StringUtils.isNull(person.getPersonId()) ? -1L : person.getPersonId();
        YgbPerson info = personMapper.checkPersonIdCardUnique(person.getIdCard());
        if (StringUtils.isNotNull(info) && info.getPersonId().longValue() != personId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertPerson(YgbPerson person)
    {
        applyScopedEnterprise(person);
        fillEnterpriseSnapshot(person);
        dataScopeGuard.assertEntityAllowed(person);
        return personMapper.insertPerson(person);
    }

    @Override
    public int updatePerson(YgbPerson person)
    {
        applyScopedEnterprise(person);
        fillEnterpriseSnapshot(person);
        dataScopeGuard.assertEntityAllowed(person);
        return personMapper.updatePerson(person);
    }

    @Override
    public int deletePersonByIds(Long[] personIds, String updateBy)
    {
        if (personIds != null)
        {
            for (Long personId : personIds)
            {
                YgbPerson person = personMapper.selectPersonById(personId);
                if (person != null)
                {
                    dataScopeGuard.assertEntityAllowed(person);
                }
            }
        }
        return personMapper.deletePersonByIds(personIds, updateBy);
    }

    private void applyScopedEnterprise(YgbPerson person)
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
        person.setEnterpriseId(scopedEnterpriseId);
    }

    private void normalizeRegionQuery(YgbPerson person)
    {
        if (person == null || StringUtils.isEmpty(person.getRegionCode()))
        {
            return;
        }
        person.setRegionCode(YgbRegionHelper.toRegionPrefix(person.getRegionCode()));
    }

    private void fillEnterpriseSnapshot(YgbPerson person)
    {
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(person.getEnterpriseId());
        if (StringUtils.isNull(enterprise))
        {
            throw new ServiceException("Selected enterprise does not exist.");
        }
        person.setRegionCode(enterprise.getRegionCode());
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbPerson query, YgbPersonSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("onPost", "在岗人员", summary.getOnPostCount(), "持续完整",
            "先固定在岗人员范围，再承接合同、考勤和工资办理链路。", "person", "person",
            "530.1 人员主数据解释", baseQuery));
        list.add(explanationItem("certRisk", "证书风险", summary.getCertRiskCount(), 0,
            "证书临期或过期会阻断后续授权、报备和重点岗位办理，应优先回看风险对象。", "person", "person",
            "530.1 人员主数据解释", baseQuery));
        list.add(explanationItem("uninsured", "未参保人员", summary.getUninsuredCount(), 0,
            "未参保对象会影响工资、工伤和扩面减损链路，应先锁定对象再进入后续办理。", "person", "person",
            "530.1 人员主数据解释", baseQuery));
        list.add(explanationItem("dispatch", "派遣结构", summary.getDispatchWorkerCount(), "持续核实",
            "派遣用工结构决定合同备案和工资监管承接方式，应优先固定人员类型口径。", "person", "person",
            "530.1 人员主数据解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbPerson query, YgbPersonSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("certRisk", "证书风险对象", summary.getCertRiskCount(), 0,
            "证书异常会直接影响现场准入和设备授权，应优先复核。", "person", "person",
            "6.1 人员对象解释", baseQuery));
        list.add(explanationItem("uninsured", "未参保对象", summary.getUninsuredCount(), 0,
            "未参保对象会影响事故治理和协同处置口径，应先确认真实原因。", "person", "person",
            "6.1 人员对象解释", baseQuery));
        list.add(explanationItem("onPost", "在岗范围", summary.getOnPostCount(), "持续核实",
            "治理链应先明确当前在岗范围，避免离岗对象继续进入现场协同链路。", "person", "person",
            "6.1 人员对象解释", baseQuery));
        list.add(explanationItem("dispatch", "派遣作业人群", summary.getDispatchWorkerCount(), "持续核实",
            "派遣对象更容易出现跨主体归属偏差，应同步回看企业和现场授权链。", "person", "person",
            "6.1 人员对象解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbPerson query)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (query == null)
        {
            return map;
        }
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getRegionCode()))
        {
            map.put("regionCode", query.getRegionCode());
        }
        if (StringUtils.isNotEmpty(query.getWorkerType()))
        {
            map.put("workerType", query.getWorkerType());
        }
        if (StringUtils.isNotEmpty(query.getCertStatus()))
        {
            map.put("certStatus", query.getCertStatus());
        }
        if (StringUtils.isNotEmpty(query.getInsuranceStatus()))
        {
            map.put("insuranceStatus", query.getInsuranceStatus());
        }
        if (StringUtils.isNotEmpty(query.getEmploymentStatus()))
        {
            map.put("employmentStatus", query.getEmploymentStatus());
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
}
