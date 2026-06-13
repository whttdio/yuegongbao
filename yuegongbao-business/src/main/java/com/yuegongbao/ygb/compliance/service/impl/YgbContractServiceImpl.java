package com.yuegongbao.ygb.compliance.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.service.IYgbContractService;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.domain.YgbContractSummary;
import com.yuegongbao.ygb.domain.vo.YgbBlockchainStoreResponse;
import com.yuegongbao.ygb.domain.vo.YgbOcrExtractResponse;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.integration.BlockchainClient;
import com.yuegongbao.ygb.integration.OcrClient;

@Service
public class YgbContractServiceImpl implements IYgbContractService
{
    private static final Logger log = LoggerFactory.getLogger(YgbContractServiceImpl.class);

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private YgbEnterpriseMapper enterpriseMapper;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private OcrClient ocrClient;

    @Autowired
    private BlockchainClient blockchainClient;

    @Override
    public List<YgbContract> selectContractList(YgbContract contract)
    {
        return contractMapper.selectContractList(contract);
    }

    @Override
    public YgbContractSummary selectContractSummary(YgbContract contract)
    {
        List<YgbContract> list = selectContractList(contract);
        YgbContractSummary summary = new YgbContractSummary();
        summary.setTotalCount(list.size());

        int filedCount = 0;
        int pendingCount = 0;
        int rejectedCount = 0;
        int expiredCount = 0;
        int expiringSoonCount = 0;
        int unfiledCount = 0;
        int ocrFailedCount = 0;
        int clauseMissingCount = 0;
        for (YgbContract item : list)
        {
            if ("2".equals(item.getContractStatus()))
            {
                filedCount++;
            }
            if ("0".equals(item.getContractStatus()) || "1".equals(item.getContractStatus()))
            {
                pendingCount++;
                unfiledCount++;
            }
            if ("3".equals(item.getContractStatus()))
            {
                rejectedCount++;
            }
            if ("4".equals(item.getContractStatus()) || "5".equals(item.getContractStatus()))
            {
                expiredCount++;
            }
            if ("2".equals(item.getOcrStatus()))
            {
                ocrFailedCount++;
            }
            if ("2".equals(item.getClauseCheckStatus()))
            {
                clauseMissingCount++;
            }
            if (isContractExpiringSoon(item))
            {
                expiringSoonCount++;
            }
        }

        summary.setFiledCount(filedCount);
        summary.setPendingCount(pendingCount);
        summary.setUnfiledCount(unfiledCount);
        summary.setRejectedCount(rejectedCount);
        summary.setExpiredCount(expiredCount);
        summary.setExpiringSoonCount(expiringSoonCount);
        summary.setOcrFailedCount(ocrFailedCount);
        summary.setClauseMissingCount(clauseMissingCount);
        summary.setYgbExplanation(buildYgbExplanationV2(contract, summary));
        return summary;
    }

    @Override
    public List<YgbContract> selectContractOptions()
    {
        return contractMapper.selectContractOptions();
    }

    @Override
    public YgbContract selectContractById(Long contractId)
    {
        return contractMapper.selectContractById(contractId);
    }

    @Override
    public boolean checkContractNoUnique(YgbContract contract)
    {
        Long contractId = StringUtils.isNull(contract.getContractId()) ? -1L : contract.getContractId();
        YgbContract info = contractMapper.checkContractNoUnique(contract.getContractNo());
        if (StringUtils.isNotNull(info) && info.getContractId().longValue() != contractId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertContract(YgbContract contract)
    {
        fillSnapshots(contract);
        syncContractEvidence(contract, null);
        return contractMapper.insertContract(contract);
    }

    @Override
    public int updateContract(YgbContract contract)
    {
        YgbContract previous = contract.getContractId() == null ? null : contractMapper.selectContractById(contract.getContractId());
        fillSnapshots(contract);
        syncContractEvidence(contract, previous);
        return contractMapper.updateContract(contract);
    }

    @Override
    public int deleteContractByIds(Long[] contractIds, String updateBy)
    {
        return contractMapper.deleteContractByIds(contractIds, updateBy);
    }

    private void fillSnapshots(YgbContract contract)
    {
        YgbEnterprise dispatchEnterprise = enterpriseMapper.selectEnterpriseById(contract.getDispatchEnterpriseId());
        if (StringUtils.isNull(dispatchEnterprise))
        {
            throw new ServiceException("派遣单位不存在。");
        }

        YgbEnterprise employerEnterprise = enterpriseMapper.selectEnterpriseById(contract.getEmployerEnterpriseId());
        if (StringUtils.isNull(employerEnterprise))
        {
            throw new ServiceException("用工单位不存在。");
        }

        if (dispatchEnterprise.getEnterpriseId().longValue() == employerEnterprise.getEnterpriseId().longValue())
        {
            throw new ServiceException("派遣单位和用工单位不能相同。");
        }

        YgbPerson person = personMapper.selectPersonById(contract.getPersonId());
        if (StringUtils.isNull(person))
        {
            throw new ServiceException("合同关联人员不存在。");
        }

        contract.setDispatchEnterpriseName(dispatchEnterprise.getEnterpriseName());
        contract.setEmployerEnterpriseName(employerEnterprise.getEnterpriseName());
        contract.setPersonName(person.getPersonName());
        contract.setIdCard(person.getIdCard());
        contract.setRegionCode(person.getRegionCode());
    }

    private void syncContractEvidence(YgbContract contract, YgbContract previous)
    {
        if (contract == null || StringUtils.isEmpty(contract.getContractFileUrl()))
        {
            return;
        }

        boolean fileChanged = previous != null
            && !StringUtils.equals(contract.getContractFileUrl(), previous.getContractFileUrl());
        boolean needOcr = shouldSyncOcr(contract, previous, fileChanged);
        boolean needBlockchain = shouldSyncBlockchain(contract, previous, fileChanged);

        if (needOcr)
        {
            trySyncOcr(contract);
        }
        else if (StringUtils.isEmpty(contract.getOcrStatus()) && previous != null)
        {
            contract.setOcrStatus(previous.getOcrStatus());
        }

        if (needBlockchain)
        {
            trySyncBlockchain(contract);
        }
        else if (StringUtils.isEmpty(contract.getBlockchainHash()) && previous != null)
        {
            contract.setBlockchainHash(previous.getBlockchainHash());
        }
    }

    private boolean shouldSyncOcr(YgbContract contract, YgbContract previous, boolean fileChanged)
    {
        if (StringUtils.isEmpty(contract.getContractFileUrl()))
        {
            return false;
        }
        if (previous == null)
        {
            return true;
        }
        if (fileChanged)
        {
            return true;
        }
        if (StringUtils.isEmpty(contract.getOcrStatus()))
        {
            return StringUtils.isEmpty(previous.getOcrStatus()) || "0".equals(previous.getOcrStatus()) || "2".equals(previous.getOcrStatus());
        }
        return "0".equals(contract.getOcrStatus()) || "2".equals(contract.getOcrStatus());
    }

    private boolean shouldSyncBlockchain(YgbContract contract, YgbContract previous, boolean fileChanged)
    {
        if (StringUtils.isEmpty(contract.getContractFileUrl()))
        {
            return false;
        }
        if (previous == null)
        {
            return StringUtils.isEmpty(contract.getBlockchainHash());
        }
        if (fileChanged)
        {
            return true;
        }
        if (StringUtils.isNotEmpty(contract.getBlockchainHash()))
        {
            return false;
        }
        return StringUtils.isEmpty(previous.getBlockchainHash());
    }

    private void trySyncOcr(YgbContract contract)
    {
        try
        {
            YgbOcrExtractResponse response = ocrClient.extractContract(contract.getContractFileUrl());
            if (response != null && "SUCCESS".equalsIgnoreCase(response.getSourceStatus()))
            {
                contract.setOcrStatus("1");
                if (StringUtils.isEmpty(contract.getContractNo()) && StringUtils.isNotEmpty(response.getContractNo()))
                {
                    contract.setContractNo(response.getContractNo());
                }
                if (contract.getMonthlyWage() == null && response.getMonthlyWage() != null)
                {
                    contract.setMonthlyWage(response.getMonthlyWage());
                }
                return;
            }
            contract.setOcrStatus("2");
        }
        catch (Exception ex)
        {
            contract.setOcrStatus("2");
            log.warn("Contract OCR sync failed, contractNo={}, fileUrl={}, message={}",
                contract.getContractNo(), contract.getContractFileUrl(), ex.getMessage());
        }
    }

    private void trySyncBlockchain(YgbContract contract)
    {
        try
        {
            YgbBlockchainStoreResponse response = blockchainClient.storeContract(contract.getContractNo(),
                contract.getContractFileUrl());
            if (response != null && "SUCCESS".equalsIgnoreCase(response.getSourceStatus())
                && StringUtils.isNotEmpty(response.getHashValue()))
            {
                contract.setBlockchainHash(response.getHashValue());
            }
        }
        catch (Exception ex)
        {
            log.warn("Contract blockchain sync failed, contractNo={}, fileUrl={}, message={}",
                contract.getContractNo(), contract.getContractFileUrl(), ex.getMessage());
        }
    }

    private boolean isContractExpiringSoon(YgbContract contract)
    {
        if (contract == null || contract.getEndDate() == null)
        {
            return false;
        }
        if ("4".equals(contract.getContractStatus()) || "5".equals(contract.getContractStatus()))
        {
            return false;
        }

        Date now = new Date();
        if (contract.getEndDate().before(now))
        {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        return !contract.getEndDate().after(calendar.getTime());
    }

    private List<Map<String, Object>> buildYgbExplanationV2(YgbContract contract, YgbContractSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(contract);
        list.add(explanationItem("unfiled", "未备案预警", summary.getUnfiledCount(), 0,
            "草稿和待备案合同应优先补齐备案编号、备案时间和主体字段，避免合同主链路在备案环节积压。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("expiringSoon", "合同到期提醒", summary.getExpiringSoonCount(), 0,
            "建议优先梳理 30 天内到期合同，提前确认续签、归档或替换安排，避免影响后续用工与工资链路。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("rejected", "驳回合同回查", summary.getRejectedCount(), 0,
            "驳回对象应先回查主体关系、期限与工资标准，再重新进入备案闭环。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("ocrFailed", "OCR 异常合同", summary.getOcrFailedCount(), 0,
            "应优先处置 OCR 识别失败对象，避免关键信息缺失继续传导到考勤和工资环节。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("clauseMissingOrExpired", "条款缺失与已到期合同",
            summary.getClauseMissingCount() + summary.getExpiredCount(), 0,
            "条款缺失和已到期合同适合集中补录、续签和归档，减少后续办理风险。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        return list;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbContract contract, YgbContractSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> baseQuery = buildExplanationQuery(contract);
        list.add(explanationItem("pending", "待备案合同", summary.getPendingCount(), 0,
            "优先补齐合同备案编号、备案时间和主体字段，避免办理主链在备案环节积压。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("rejected", "驳回合同回查", summary.getRejectedCount(), 0,
            "驳回对象应先回查主体关系、期限与工资标准，再重新进入备案闭环。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("ocrFailed", "OCR 异常合同", summary.getOcrFailedCount(), 0,
            "先处理 OCR 识别失败对象，避免关键信息缺失继续传导到考勤和工资链路。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        list.add(explanationItem("clauseMissingOrExpired", "条款缺失与到期合同",
            summary.getClauseMissingCount() + summary.getExpiredCount(), 0,
            "条款缺失和到期合同更适合集中补录、续签和归档，减少后续办理风险。", "contract", "contract",
            "530.1 合同备案办理解释", baseQuery));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbContract contract)
    {
        Map<String, Object> query = new LinkedHashMap<>();
        if (contract == null)
        {
            return query;
        }
        if (contract.getDispatchEnterpriseId() != null)
        {
            query.put("dispatchEnterpriseId", contract.getDispatchEnterpriseId());
        }
        if (contract.getPersonId() != null)
        {
            query.put("personId", contract.getPersonId());
        }
        if (StringUtils.isNotEmpty(contract.getContractStatus()))
        {
            query.put("contractStatus", contract.getContractStatus());
        }
        if (StringUtils.isNotEmpty(contract.getOcrStatus()))
        {
            query.put("ocrStatus", contract.getOcrStatus());
        }
        return query;
    }

    private Map<String, Object> explanationItem(String focusKey, String dimensionName, Object currentValue,
        Object targetValue, String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> baseQuery)
    {
        Map<String, Object> defaultQuery = new LinkedHashMap<>(baseQuery);
        defaultQuery.put("focusKey", focusKey);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", focusKey);
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }
}
