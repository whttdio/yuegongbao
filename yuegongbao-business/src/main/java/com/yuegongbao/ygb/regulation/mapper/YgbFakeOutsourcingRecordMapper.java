package com.yuegongbao.ygb.regulation.mapper;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord;

public interface YgbFakeOutsourcingRecordMapper
{
    public List<YgbFakeOutsourcingRecord> selectFakeOutsourcingList(YgbFakeOutsourcingRecord fakeOutsourcingRecord);

    public int insertFakeOutsourcingRecord(YgbFakeOutsourcingRecord fakeOutsourcingRecord);
}
