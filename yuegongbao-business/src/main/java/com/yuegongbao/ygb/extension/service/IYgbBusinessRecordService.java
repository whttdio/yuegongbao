package com.yuegongbao.ygb.extension.service;

import java.util.List;
import com.yuegongbao.ygb.extension.domain.YgbBusinessRecord;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecordSummary;

public interface IYgbBusinessRecordService
{
    List<YgbBusinessRecord> selectBusinessRecordList(String moduleCode, YgbBusinessRecord query);

    YgbModuleRecordSummary selectBusinessRecordSummary(String moduleCode, YgbBusinessRecord query);

    YgbBusinessRecord selectBusinessRecordById(String moduleCode, Long businessId);

    int insertBusinessRecord(String moduleCode, YgbBusinessRecord record, String operator);

    int updateBusinessRecord(String moduleCode, YgbBusinessRecord record, String operator);

    int deleteBusinessRecordByIds(String moduleCode, Long[] businessIds, String operator);
}
