package com.yuegongbao.ygb.compliance.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbContractTemplate;

public interface YgbContractTemplateMapper
{
    List<YgbContractTemplate> selectContractTemplateList(YgbContractTemplate template);

    List<YgbContractTemplate> selectEnabledTemplateOptions(@Param("templateType") String templateType,
        @Param("regionCode") String regionCode);

    YgbContractTemplate selectContractTemplateById(Long templateId);

    YgbContractTemplate checkTemplateCodeUnique(String templateCode);

    int insertContractTemplate(YgbContractTemplate template);

    int updateContractTemplate(YgbContractTemplate template);

    int updateTemplateStatus(@Param("templateId") Long templateId, @Param("status") String status,
        @Param("updateBy") String updateBy);

    int deleteContractTemplateByIds(@Param("templateIds") Long[] templateIds, @Param("updateBy") String updateBy);
}
