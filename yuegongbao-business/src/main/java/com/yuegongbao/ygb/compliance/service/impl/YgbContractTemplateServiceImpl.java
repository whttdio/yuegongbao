package com.yuegongbao.ygb.compliance.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbContractTemplate;
import com.yuegongbao.ygb.compliance.mapper.YgbContractTemplateMapper;
import com.yuegongbao.ygb.compliance.service.IYgbContractTemplateService;
import com.yuegongbao.ygb.util.YgbRegionHelper;

@Service
public class YgbContractTemplateServiceImpl implements IYgbContractTemplateService
{
    @Autowired
    private YgbContractTemplateMapper contractTemplateMapper;

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
        return contractTemplateMapper.selectContractTemplateById(templateId);
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
        template.setCreateTime(new Date());
        return contractTemplateMapper.insertContractTemplate(template);
    }

    @Override
    public int updateContractTemplate(YgbContractTemplate template)
    {
        fillDefaultFields(template);
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
        return contractTemplateMapper.updateTemplateStatus(templateId, status, updateBy);
    }

    @Override
    public int deleteContractTemplateByIds(Long[] templateIds, String updateBy)
    {
        return contractTemplateMapper.deleteContractTemplateByIds(templateIds, updateBy);
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
            template.setReviewStatus("approved");
        }
        if (StringUtils.isEmpty(template.getStatus()))
        {
            template.setStatus("1");
        }
        if (StringUtils.isEmpty(template.getRegionCode()))
        {
            template.setRegionCode(YgbRegionHelper.defaultDashboardRegion(null));
        }
    }
}
