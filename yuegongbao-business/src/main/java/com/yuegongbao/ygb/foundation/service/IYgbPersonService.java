package com.yuegongbao.ygb.foundation.service;

import java.util.List;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.domain.YgbPersonSummary;

/**
 * 人员主数据服务接口。
 * @author yuegongbao
 */
public interface IYgbPersonService
{
    public List<YgbPerson> selectPersonList(YgbPerson person);
    public YgbPersonSummary selectPersonSummary(YgbPerson person);

    public List<YgbPerson> selectPersonOptions(Long enterpriseId);

    public YgbPerson selectPersonById(Long personId);

    public boolean checkPersonIdCardUnique(YgbPerson person);

    public int insertPerson(YgbPerson person);

    public int updatePerson(YgbPerson person);

    public int deletePersonByIds(Long[] personIds, String updateBy);
}

