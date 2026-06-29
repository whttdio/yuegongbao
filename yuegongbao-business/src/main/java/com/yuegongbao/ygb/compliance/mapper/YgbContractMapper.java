package com.yuegongbao.ygb.compliance.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbContract;

/**
 * 合同备案Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbContractMapper
{
    public List<YgbContract> selectContractList(YgbContract contract);

    public List<YgbContract> selectContractExpiryList(YgbContract contract);

    public List<YgbContract> selectContractUnfiledList(YgbContract contract);

    public List<YgbContract> selectContractOptions();

    public YgbContract selectContractById(Long contractId);

    public YgbContract selectEffectiveContractByPersonId(@Param("personId") Long personId, @Param("workDate") java.util.Date workDate);

    public YgbContract checkContractNoUnique(String contractNo);

    public int insertContract(YgbContract contract);

    public int updateContract(YgbContract contract);

    public int deleteContractByIds(@Param("contractIds") Long[] contractIds, @Param("updateBy") String updateBy);
}
