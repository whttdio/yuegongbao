package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import com.yuegongbao.ygb.compliance.domain.ContractTemplateReviewRequest;
import com.yuegongbao.ygb.compliance.domain.YgbContractTemplate;

public interface IYgbContractTemplateService
{
    List<YgbContractTemplate> selectContractTemplateList(YgbContractTemplate template);

    List<YgbContractTemplate> selectEnabledTemplateOptions(String templateType, String regionCode);

    YgbContractTemplate selectContractTemplateById(Long templateId);

    boolean checkTemplateCodeUnique(YgbContractTemplate template);

    int insertContractTemplate(YgbContractTemplate template);

    int updateContractTemplate(YgbContractTemplate template);

    int updateTemplateStatus(Long templateId, String status, String updateBy);

    int submitTemplateReview(Long templateId, String updateBy, String portalScope);

    int reviewTemplate(Long templateId, ContractTemplateReviewRequest request, String reviewBy);

    int deleteContractTemplateByIds(Long[] templateIds, String updateBy);
}
