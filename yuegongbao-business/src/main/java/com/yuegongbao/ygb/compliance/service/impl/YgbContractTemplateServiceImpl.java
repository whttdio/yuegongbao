package com.yuegongbao.ygb.compliance.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.ContractTemplateReviewRequest;
import com.yuegongbao.ygb.compliance.domain.YgbContractActionLog;
import com.yuegongbao.ygb.compliance.domain.YgbContractTemplate;
import com.yuegongbao.ygb.compliance.mapper.YgbContractActionLogMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbContractTemplateMapper;
import com.yuegongbao.ygb.compliance.service.IYgbContractTemplateService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbContractTemplateServiceImpl implements IYgbContractTemplateService
{
    @Autowired
    private YgbContractTemplateMapper contractTemplateMapper;

    @Autowired
    private YgbContractActionLogMapper contractActionLogMapper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbContractTemplate> selectContractTemplateList(YgbContractTemplate template)
    {
        return contractTemplateMapper.selectContractTemplateList(template);
    }

    @Override
    public List<YgbContractTemplate> selectEnabledTemplateOptions(String templateType, String regionCode)
    {
        return contractTemplateMapper.selectEnabledTemplateOptions(templateType,
            StringUtils.defaultIfEmpty(regionCode, YgbRegionHelper.defaultDashboardRegion(null)));
    }

    @Override
    public YgbContractTemplate selectContractTemplateById(Long templateId)
    {
        YgbContractTemplate template = contractTemplateMapper.selectContractTemplateById(templateId);
        if (template != null)
        {
            regionScopeHelper.assertEntityRegionAllowed(template);
        }
        return template;
    }

    @Override
    public boolean checkTemplateCodeUnique(YgbContractTemplate template)
    {
        Long templateId = template.getTemplateId() == null ? -1L : template.getTemplateId();
        YgbContractTemplate info = contractTemplateMapper.checkTemplateCodeUnique(template.getTemplateCode());
        if (info != null && info.getTemplateId().longValue() != templateId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertContractTemplate(YgbContractTemplate template)
    {
        fillDefaultFields(template);
        template.setReviewStatus("pending");
        template.setStatus("0");
        template.setCreateTime(new Date());
        return contractTemplateMapper.insertContractTemplate(template);
    }

    @Override
    public int updateContractTemplate(YgbContractTemplate template)
    {
        requireContractTemplateAllowed(template.getTemplateId());
        fillDefaultFields(template);
        template.setReviewStatus("pending");
        template.setStatus("0");
        template.setReviewBy("");
        template.setReviewTime(null);
        template.setReviewRemark("");
        template.setUpdateTime(new Date());
        return contractTemplateMapper.updateContractTemplate(template);
    }

    @Override
    public int updateTemplateStatus(Long templateId, String status, String updateBy)
    {
        if (StringUtils.isEmpty(status))
        {
            throw new ServiceException("模板状态不能为空");
        }
        YgbContractTemplate template = requireContractTemplateAllowed(templateId);
        if ("1".equals(status) && !"approved".equals(template.getReviewStatus()))
        {
            throw new ServiceException("只有审核通过的合同模板才能启用");
        }
        return contractTemplateMapper.updateTemplateStatus(templateId, status, updateBy);
    }

    @Override
    public int submitTemplateReview(Long templateId, String updateBy, String portalScope)
    {
        YgbContractTemplate template = requireContractTemplateAllowed(templateId);
        if ("pending".equals(template.getReviewStatus()))
        {
            throw new ServiceException("模板已处于待审核状态，无需重复提交");
        }
        int rows = contractTemplateMapper.submitTemplateReview(templateId, updateBy);
        insertTemplateActionLog(templateId, "template_submit", "pending", "模板已提交审核。", portalScope, updateBy, null);
        return rows;
    }

    @Override
    public int reviewTemplate(Long templateId, ContractTemplateReviewRequest request, String reviewBy)
    {
        YgbContractTemplate template = requireContractTemplateAllowed(templateId);
        if (request == null || StringUtils.isEmpty(request.getReviewStatus()))
        {
            throw new ServiceException("审核状态不能为空");
        }
        if (!"approved".equals(request.getReviewStatus()) && !"rejected".equals(request.getReviewStatus()))
        {
            throw new ServiceException("审核状态只能为通过或驳回");
        }
        if (!"pending".equals(template.getReviewStatus()))
        {
            throw new ServiceException("只有待审核合同模板才能审核");
        }
        int rows = contractTemplateMapper.reviewTemplate(templateId, request.getReviewStatus(),
            request.getReviewRemark(), reviewBy);
        insertTemplateActionLog(templateId, "template_review", request.getReviewStatus(),
            "approved".equals(request.getReviewStatus()) ? "模板审核通过。" : "模板审核驳回。",
            request.getPortalScope(), reviewBy, request.getReviewRemark());
        return rows;
    }

    @Override
    public int deleteContractTemplateByIds(Long[] templateIds, String updateBy)
    {
        for (Long templateId : templateIds)
        {
            requireContractTemplateAllowed(templateId);
        }
        return contractTemplateMapper.deleteContractTemplateByIds(templateIds, updateBy);
    }

    private YgbContractTemplate requireContractTemplateAllowed(Long templateId)
    {
        if (templateId == null)
        {
            throw new ServiceException("模板ID不能为空");
        }
        YgbContractTemplate template = contractTemplateMapper.selectContractTemplateById(templateId);
        if (template == null)
        {
            throw new ServiceException("合同模板不存在");
        }
        regionScopeHelper.assertEntityRegionAllowed(template);
        return template;
    }

    private void fillDefaultFields(YgbContractTemplate template)
    {
        if (template == null)
        {
            throw new ServiceException("合同模板不能为空");
        }
        if (StringUtils.isEmpty(template.getTemplateCode()))
        {
            throw new ServiceException("模板编码不能为空");
        }
        if (StringUtils.isEmpty(template.getTemplateName()))
        {
            throw new ServiceException("模板名称不能为空");
        }
        if (StringUtils.isEmpty(template.getTemplateVersion()))
        {
            template.setTemplateVersion("V1.0");
        }
        if (StringUtils.isEmpty(template.getReviewStatus()))
        {
            template.setReviewStatus("pending");
        }
        if (StringUtils.isEmpty(template.getStatus()))
        {
            template.setStatus("approved".equals(template.getReviewStatus()) ? "1" : "0");
        }
        if (StringUtils.isEmpty(template.getRegionCode()))
        {
            template.setRegionCode(YgbRegionHelper.defaultDashboardRegion(null));
        }
    }

    private void insertTemplateActionLog(Long templateId, String actionType, String actionStatus, String actionResult,
        String portalScope, String operator, String remark)
    {
        YgbContractActionLog log = new YgbContractActionLog();
        log.setTemplateId(templateId);
        log.setActionType(actionType);
        log.setActionStatus(actionStatus);
        log.setActionResult(actionResult);
        log.setPortalScope(StringUtils.defaultIfEmpty(portalScope, "ygb"));
        log.setActionTime(new Date());
        log.setRemark(remark);
        log.setCreateBy(operator);
        contractActionLogMapper.insertContractActionLog(log);
    }
}
