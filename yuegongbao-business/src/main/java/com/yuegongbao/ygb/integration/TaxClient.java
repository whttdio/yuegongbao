package com.yuegongbao.ygb.integration;

import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbTaxRecordStubItem;

/**
 * 税务接口。
 *
 * @author yuegongbao
 */
public interface TaxClient
{
    List<YgbTaxRecordStubItem> pullMonthlyRecords(String statMonth);
}
