package com.yuegongbao.ygb.extension.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.extension.domain.YgbModuleRecord;

public interface YgbModuleRecordMapper
{
    List<YgbModuleRecord> selectModuleRecordList(YgbModuleRecord query);

    YgbModuleRecord selectModuleRecordById(@Param("recordId") Long recordId);

    int insertModuleRecord(YgbModuleRecord record);

    int updateModuleRecord(YgbModuleRecord record);

    int deleteModuleRecordByIds(@Param("recordIds") Long[] recordIds, @Param("updateBy") String updateBy);
}
