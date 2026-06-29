package com.yuegongbao.ygb.safety.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.safety.domain.YgbPreventionProject;
import com.yuegongbao.ygb.safety.domain.YgbPreventionProjectSummary;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.safety.mapper.YgbPreventionProjectMapper;
import com.yuegongbao.ygb.safety.service.IYgbPreventionProjectService;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;

@Service
public class YgbPreventionProjectServiceImpl implements IYgbPreventionProjectService
{
    @Autowired
    private YgbPreventionProjectMapper preventionProjectMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbDataScopeGuard dataScopeGuard;

    @Override
    public List<YgbPreventionProject> selectPreventionProjectList(YgbPreventionProject preventionProject)
    {
        return preventionProjectMapper.selectPreventionProjectList(preventionProject);
    }

    @Override
    public YgbPreventionProjectSummary selectPreventionProjectSummary(YgbPreventionProject preventionProject)
    {
        List<YgbPreventionProject> list = selectPreventionProjectList(preventionProject);
        YgbPreventionProjectSummary summary = new YgbPreventionProjectSummary();
        summary.setTotalCount(list.size());

        BigDecimal totalBudget = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        int activeProjectCount = 0;
        int acceptancePendingCount = 0;
        int lowScoreCount = 0;
        int trainingAiProjectCount = 0;
        int highBudgetCount = 0;
        for (YgbPreventionProject item : list)
        {
            totalBudget = totalBudget.add(defaultAmount(item.getBudgetAmount()));
            if ("1".equals(item.getProjectStatus()) || "2".equals(item.getProjectStatus()) || "3".equals(item.getProjectStatus()))
            {
                activeProjectCount++;
            }
            if ("3".equals(item.getProjectStatus()))
            {
                acceptancePendingCount++;
            }
            if (item.getEvaluationScore() != null && item.getEvaluationScore() > 0 && item.getEvaluationScore() < 80)
            {
                lowScoreCount++;
            }
            if ("2".equals(item.getProjectType()) || "3".equals(item.getProjectType()))
            {
                trainingAiProjectCount++;
            }
            if (defaultAmount(item.getBudgetAmount()).compareTo(new BigDecimal("100000")) >= 0)
            {
                highBudgetCount++;
            }
        }

        summary.setTotalBudget(totalBudget);
        summary.setActiveProjectCount(activeProjectCount);
        summary.setAcceptancePendingCount(acceptancePendingCount);
        summary.setLowScoreCount(lowScoreCount);
        summary.setTrainingAiProjectCount(trainingAiProjectCount);
        summary.setHighBudgetCount(highBudgetCount);
        summary.setYgbExplanation(buildYgbExplanation(preventionProject, summary));
        summary.setAzbExplanation(buildAzbExplanation(preventionProject, summary));
        return summary;
    }

    @Override
    public YgbPreventionProject selectPreventionProjectById(Long projectId)
    {
        YgbPreventionProject preventionProject = preventionProjectMapper.selectPreventionProjectById(projectId);
        if (preventionProject != null)
        {
            dataScopeGuard.assertEntityAllowed(preventionProject);
        }
        return preventionProject;
    }

    @Override
    public int insertPreventionProject(YgbPreventionProject preventionProject)
    {
        fillSnapshot(preventionProject);
        normalizeProjectForSave(preventionProject);
        if (!"0".equals(preventionProject.getProjectStatus()))
        {
            throw new ServiceException("新增预防项目只能从申报状态开始。");
        }
        dataScopeGuard.assertEntityAllowed(preventionProject);
        return preventionProjectMapper.insertPreventionProject(preventionProject);
    }

    @Override
    public int updatePreventionProject(YgbPreventionProject preventionProject)
    {
        YgbPreventionProject previous = requirePreventionProjectAllowed(preventionProject.getProjectId());
        fillSnapshot(preventionProject);
        normalizeProjectForSave(preventionProject);
        if (StringUtils.isEmpty(preventionProject.getProjectStatus()))
        {
            preventionProject.setProjectStatus(previous.getProjectStatus());
        }
        validateProjectStatusTransition(previous, preventionProject);
        dataScopeGuard.assertEntityAllowed(preventionProject);
        return preventionProjectMapper.updatePreventionProject(preventionProject);
    }

    @Override
    public int deletePreventionProjectByIds(Long[] projectIds, String updateBy)
    {
        for (Long projectId : projectIds)
        {
            YgbPreventionProject preventionProject = requirePreventionProjectAllowed(projectId);
            if (!"0".equals(preventionProject.getProjectStatus()))
            {
                throw new ServiceException("已立项、实施、验收或结项的预防项目不允许删除。");
            }
        }
        return preventionProjectMapper.deletePreventionProjectByIds(projectIds, updateBy);
    }

    private YgbPreventionProject requirePreventionProjectAllowed(Long projectId)
    {
        if (projectId == null)
        {
            throw new ServiceException("预防项目ID不能为空。");
        }
        YgbPreventionProject preventionProject = preventionProjectMapper.selectPreventionProjectById(projectId);
        if (preventionProject == null)
        {
            throw new ServiceException("预防项目不存在。");
        }
        dataScopeGuard.assertEntityAllowed(preventionProject);
        return preventionProject;
    }

    private void fillSnapshot(YgbPreventionProject preventionProject)
    {
        if (preventionProject.getEnterpriseId() == null)
        {
            return;
        }
        YgbEnterprise enterprise = enterpriseMapper.selectEnterpriseById(preventionProject.getEnterpriseId());
        if (enterprise == null)
        {
            throw new ServiceException("企业不存在。");
        }
        preventionProject.setEnterpriseName(enterprise.getEnterpriseName());
        preventionProject.setRegionCode(enterprise.getRegionCode());
    }

    private void normalizeProjectForSave(YgbPreventionProject preventionProject)
    {
        if (StringUtils.isEmpty(preventionProject.getProjectStatus()))
        {
            preventionProject.setProjectStatus("0");
        }
        validateProjectStatusValue(preventionProject.getProjectStatus());
        if (preventionProject.getBudgetAmount() == null)
        {
            preventionProject.setBudgetAmount(BigDecimal.ZERO);
        }
        if (preventionProject.getActualAmount() == null)
        {
            preventionProject.setActualAmount(BigDecimal.ZERO);
        }
        if (preventionProject.getBudgetAmount().compareTo(BigDecimal.ZERO) < 0
            || preventionProject.getActualAmount().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("预防项目预算金额和实际金额不能小于 0。");
        }
        if (preventionProject.getStartDate() != null && preventionProject.getEndDate() != null
            && preventionProject.getEndDate().before(preventionProject.getStartDate()))
        {
            throw new ServiceException("预防项目结束日期不能早于开始日期。");
        }
        Integer score = preventionProject.getEvaluationScore();
        if (score != null && (score < 0 || score > 100))
        {
            throw new ServiceException("预防项目评价分必须在 0 到 100 之间。");
        }
    }

    private void validateProjectStatusTransition(YgbPreventionProject previous, YgbPreventionProject target)
    {
        String currentStatus = StringUtils.defaultIfEmpty(previous.getProjectStatus(), "0");
        String targetStatus = StringUtils.defaultIfEmpty(target.getProjectStatus(), currentStatus);
        int current = projectStatusOrder(currentStatus);
        int next = projectStatusOrder(targetStatus);
        if (next < current)
        {
            throw new ServiceException("预防项目状态不允许回退。");
        }
        if (next - current > 1)
        {
            throw new ServiceException("预防项目状态只能按申报、立项、实施、验收、结项逐步推进。");
        }
        if (next >= 1 && target.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("预防项目立项后预算金额必须大于 0。");
        }
        if (next >= 2 && (target.getStartDate() == null || target.getEndDate() == null))
        {
            throw new ServiceException("预防项目进入实施阶段前必须填写计划起止日期。");
        }
        if (next >= 3 && (target.getActualAmount() == null || target.getActualAmount().compareTo(BigDecimal.ZERO) <= 0))
        {
            throw new ServiceException("预防项目进入验收阶段前必须填写实际金额。");
        }
        if (next >= 4 && (target.getEvaluationScore() == null || StringUtils.isEmpty(target.getEvaluationReport())))
        {
            throw new ServiceException("预防项目结项前必须填写评价分和评价报告。");
        }
    }

    private void validateProjectStatusValue(String projectStatus)
    {
        projectStatusOrder(projectStatus);
    }

    private int projectStatusOrder(String projectStatus)
    {
        if ("0".equals(projectStatus) || "1".equals(projectStatus) || "2".equals(projectStatus)
            || "3".equals(projectStatus) || "4".equals(projectStatus))
        {
            return Integer.parseInt(projectStatus);
        }
        throw new ServiceException("预防项目状态值不合法。");
    }

    private BigDecimal defaultAmount(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbPreventionProject query,
        YgbPreventionProjectSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("active", "实施中项目", summary.getActiveProjectCount(), 0,
            "实施中项目适合作为办理链持续跟踪入口，确保项目预算和执行进度同步回写。", "preventionProject",
            "preventionProject", "530.1 预防项目办理解释", baseQuery));
        list.add(explanationItem("acceptance", "待验收项目", summary.getAcceptancePendingCount(), 0,
            "待验收项目需要优先补齐验收结果和归档材料，避免项目长期悬停。", "preventionProject", "preventionProject",
            "530.1 预防项目办理解释", baseQuery));
        list.add(explanationItem("lowScore", "低评分项目", summary.getLowScoreCount(), 80,
            "低评分项目应优先回到项目执行与结果复盘环节，明确整改和优化动作。", "preventionProject",
            "preventionProject", "530.1 预防项目办理解释", baseQuery));
        list.add(explanationItem("trainingAi", "培训与 AI 项目", summary.getTrainingAiProjectCount(), "持续推进",
            "培训与 AI 项目更贴近企业事故预防投入主链，应优先复核实施与留痕。", "preventionProject",
            "preventionProject", "530.1 预防项目办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbPreventionProject query,
        YgbPreventionProjectSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(query);
        list.add(explanationItem("highBudget", "高预算治理项目", summary.getHighBudgetCount(), "重点复核",
            "高预算项目更适合作为区域治理成效和投入产出复核入口。", "preventionProject", "preventionProject",
            "6.1 预防项目治理解释", baseQuery));
        list.add(explanationItem("acceptance", "待验收治理项目", summary.getAcceptancePendingCount(), 0,
            "待验收项目会影响治理结果落地判断，应优先进入监管复盘链路。", "preventionProject", "preventionProject",
            "6.1 预防项目治理解释", baseQuery));
        list.add(explanationItem("lowScore", "低评分治理项目", summary.getLowScoreCount(), 80,
            "低评分项目说明治理效果不稳，应优先分析原因并压实后续措施。", "preventionProject", "preventionProject",
            "6.1 预防项目治理解释", baseQuery));
        list.add(explanationItem("trainingAi", "培训与 AI 治理项目", summary.getTrainingAiProjectCount(), "持续跟踪",
            "培训与 AI 项目更适合作为治理手段覆盖度的观察入口。", "preventionProject", "preventionProject",
            "6.1 预防项目治理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbPreventionProject query)
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
        if (query.getEnterpriseId() != null)
        {
            map.put("enterpriseId", query.getEnterpriseId());
        }
        if (StringUtils.isNotEmpty(query.getEnterpriseName()))
        {
            map.put("enterpriseName", query.getEnterpriseName());
        }
        if (StringUtils.isNotEmpty(query.getProjectType()))
        {
            map.put("projectType", query.getProjectType());
        }
        if (StringUtils.isNotEmpty(query.getProjectStatus()))
        {
            map.put("projectStatus", query.getProjectStatus());
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
