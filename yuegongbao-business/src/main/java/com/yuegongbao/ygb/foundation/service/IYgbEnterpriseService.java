package com.yuegongbao.ygb.foundation.service;

import java.util.List;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;

/**
 * 企业主数据服务接口。
 * @author yuegongbao
 */
public interface IYgbEnterpriseService
{
    public List<YgbEnterprise> selectEnterpriseList(YgbEnterprise enterprise);

    public YgbEnterpriseSummary selectEnterpriseSummary(YgbEnterprise enterprise);

    public List<YgbEnterprise> selectEnterpriseOptions();

    public YgbEnterprise selectEnterpriseById(Long enterpriseId);

    public boolean checkEnterpriseNameUnique(YgbEnterprise enterprise);

    public boolean checkEnterpriseCodeUnique(YgbEnterprise enterprise);

    public int insertEnterprise(YgbEnterprise enterprise);

    public int updateEnterprise(YgbEnterprise enterprise);

    public int deleteEnterpriseByIds(Long[] enterpriseIds, String updateBy);
}

