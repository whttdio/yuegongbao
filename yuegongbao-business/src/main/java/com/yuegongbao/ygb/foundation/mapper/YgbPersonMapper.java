package com.yuegongbao.ygb.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

/**
 * 人员主数据Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbPersonMapper
{
    public List<YgbPerson> selectPersonList(YgbPerson person);

    public List<YgbPerson> selectPersonOptions(@Param("enterpriseId") Long enterpriseId);

    public YgbPerson selectPersonById(Long personId);

    public YgbPerson selectPersonByMobile(String mobile);

    public YgbPerson selectPersonByName(String personName);

    public YgbPerson checkPersonIdCardUnique(String idCard);

    public int countPersonByEnterpriseId(Long enterpriseId);

    public int insertPerson(YgbPerson person);

    public int updatePerson(YgbPerson person);

    public int deletePersonByIds(@Param("personIds") Long[] personIds, @Param("updateBy") String updateBy);
}
