package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.compliance.domain.ContractBatchActionRequest;
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

    public List<YgbContract> selectContractExpiryList(YgbContract contract);

    public List<YgbContract> selectContractUnfiledList(YgbContract contract);

    public YgbContractSummary selectContractSummary(YgbContract contract);

    public YgbContractSummary selectContractExpirySummary(YgbContract contract);

    public YgbContractSummary selectContractUnfiledSummary(YgbContract contract);

    public List<YgbContract> selectContractOptions();

    public YgbContract selectContractById(Long contractId);

    public boolean checkContractNoUnique(YgbContract contract);

    public int insertContract(YgbContract contract);

    public int updateContract(YgbContract contract);

    public int deleteContractByIds(Long[] contractIds, String updateBy);

    public Map<String, Object> batchRemindExpiry(ContractBatchActionRequest request, String operator);

    public Map<String, Object> batchWarnUnfiled(ContractBatchActionRequest request, String operator);
}

