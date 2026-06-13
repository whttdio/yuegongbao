package com.yuegongbao.ygb.extension.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecordSummary;
import com.yuegongbao.ygb.extension.mapper.YgbModuleRecordMapper;
import com.yuegongbao.ygb.extension.service.IYgbModuleRecordService;

@Service
public class YgbModuleRecordServiceImpl implements IYgbModuleRecordService
{
    @Autowired
    private YgbModuleRecordMapper moduleRecordMapper;

    @Override
    public List<YgbModuleRecord> selectModuleRecordList(YgbModuleRecord query)
    {
        return moduleRecordMapper.selectModuleRecordList(query);
    }

    @Override
    public YgbModuleRecordSummary selectModuleRecordSummary(YgbModuleRecord query)
    {
        List<YgbModuleRecord> list = selectModuleRecordList(query);
        YgbModuleRecordSummary summary = new YgbModuleRecordSummary();
        int enabledCount = 0;
        int pendingCount = 0;
        int processingCount = 0;
        int closedCount = 0;
        int overdueCount = 0;
        for (YgbModuleRecord item : list)
        {
            if ("0".equals(item.getStatus()))
            {
                enabledCount++;
            }
            if ("pending".equals(item.getWorkflowStatus()))
            {
                pendingCount++;
            }
            if ("processing".equals(item.getWorkflowStatus()))
            {
                processingCount++;
            }
            if ("closed".equals(item.getWorkflowStatus()))
            {
                closedCount++;
            }
            if ("overdue".equals(item.getWorkflowStatus()))
            {
                overdueCount++;
            }
        }
        summary.setTotalCount(list.size());
        summary.setEnabledCount(enabledCount);
        summary.setPendingCount(pendingCount);
        summary.setProcessingCount(processingCount);
        summary.setClosedCount(closedCount);
        summary.setOverdueCount(overdueCount);
        return summary;
    }

    @Override
    public YgbModuleRecord selectModuleRecordById(Long recordId)
    {
        YgbModuleRecord record = moduleRecordMapper.selectModuleRecordById(recordId);
        if (record == null)
        {
            throw new ServiceException("扩展记录不存在");
        }
        return record;
    }

    @Override
    public int insertModuleRecord(YgbModuleRecord record, String operator)
    {
        normalizeRecord(record);
        record.setCreateBy(operator);
        return moduleRecordMapper.insertModuleRecord(record);
    }

    @Override
    public int updateModuleRecord(YgbModuleRecord record, String operator)
    {
        normalizeRecord(record);
        record.setUpdateBy(operator);
        return moduleRecordMapper.updateModuleRecord(record);
    }

    @Override
    public int deleteModuleRecordByIds(Long[] recordIds, String operator)
    {
        return moduleRecordMapper.deleteModuleRecordByIds(recordIds, operator);
    }

    private void normalizeRecord(YgbModuleRecord record)
    {
        if (StringUtils.isEmpty(record.getRecordType()))
        {
            throw new ServiceException("记录类型不能为空");
        }
        if (StringUtils.isEmpty(record.getRecordName()))
        {
            throw new ServiceException("记录名称不能为空");
        }
        if (StringUtils.isEmpty(record.getStatus()))
        {
            record.setStatus("0");
        }
        if (StringUtils.isEmpty(record.getWorkflowStatus()))
        {
            record.setWorkflowStatus("draft");
        }
        if (record.getSortOrder() == null)
        {
            record.setSortOrder(0);
        }
    }
}
