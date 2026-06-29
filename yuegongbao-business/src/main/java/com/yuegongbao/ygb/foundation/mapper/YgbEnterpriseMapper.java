package com.yuegongbao.ygb.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;

/**
 * 企业主数据Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbEnterpriseMapper
{
    public List<YgbEnterprise> selectEnterpriseList(YgbEnterprise enterprise);

    public List<YgbEnterprise> selectEnterpriseOptions(YgbEnterprise enterprise);

    public YgbEnterprise selectEnterpriseById(Long enterpriseId);

    public YgbEnterprise checkEnterpriseNameUnique(String enterpriseName);

    public YgbEnterprise checkEnterpriseCodeUnique(String enterpriseCode);

    public int insertEnterprise(YgbEnterprise enterprise);

    public int updateEnterprise(YgbEnterprise enterprise);

    public int deleteEnterpriseByIds(@Param("enterpriseIds") Long[] enterpriseIds, @Param("updateBy") String updateBy);
}
