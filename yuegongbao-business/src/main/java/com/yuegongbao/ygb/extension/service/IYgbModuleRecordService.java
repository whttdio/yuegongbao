package com.yuegongbao.ygb.extension.service;

import java.util.List;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecordSummary;

public interface IYgbModuleRecordService
{
    List<YgbModuleRecord> selectModuleRecordList(YgbModuleRecord query);

    YgbModuleRecordSummary selectModuleRecordSummary(YgbModuleRecord query);

    YgbModuleRecord selectModuleRecordById(Long recordId);

    int insertModuleRecord(YgbModuleRecord record, String operator);

    int updateModuleRecord(YgbModuleRecord record, String operator);

    int deleteModuleRecordByIds(Long[] recordIds, String operator);
}
