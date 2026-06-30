package com.yuegongbao.ygb.extension.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.extension.domain.YgbBusinessRecord;

public interface YgbBusinessRecordMapper
{
    List<YgbBusinessRecord> selectBusinessRecordList(@Param("tableName") String tableName,
        @Param("query") YgbBusinessRecord query);

    YgbBusinessRecord selectBusinessRecordById(@Param("tableName") String tableName,
        @Param("businessId") Long businessId);

    int insertBusinessRecord(@Param("tableName") String tableName, @Param("record") YgbBusinessRecord record);

    int updateBusinessRecord(@Param("tableName") String tableName, @Param("record") YgbBusinessRecord record);

    int deleteBusinessRecordByIds(@Param("tableName") String tableName, @Param("businessIds") Long[] businessIds,
        @Param("updateBy") String updateBy);
}
