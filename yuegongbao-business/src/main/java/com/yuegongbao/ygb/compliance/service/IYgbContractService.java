package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.domain.YgbContractSummary;

/**
 * 合同备案服务接口。
 *
 * @author yuegongbao
 */
public interface IYgbContractService
{
    public List<YgbContract> selectContractList(YgbContract contract);

    public YgbContractSummary selectContractSummary(YgbContract contract);

    public List<YgbContract> selectContractOptions();

    public YgbContract selectContractById(Long contractId);

    public boolean checkContractNoUnique(YgbContract contract);

    public int insertContract(YgbContract contract);

    public int updateContract(YgbContract contract);

    public int deleteContractByIds(Long[] contractIds, String updateBy);
}

