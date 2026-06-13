package com.yuegongbao.ygb.regulation.service;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingSummary;
import com.yuegongbao.ygb.domain.vo.YgbFakeOutsourcingAnalyzeRequest;

public interface IYgbFakeOutsourcingService
{
    public List<YgbFakeOutsourcingRecord> selectFakeOutsourcingList(YgbFakeOutsourcingRecord fakeOutsourcingRecord);

    public YgbFakeOutsourcingSummary selectFakeOutsourcingSummary(YgbFakeOutsourcingRecord fakeOutsourcingRecord);

    public Long analyze(YgbFakeOutsourcingAnalyzeRequest request, String operator);
}

