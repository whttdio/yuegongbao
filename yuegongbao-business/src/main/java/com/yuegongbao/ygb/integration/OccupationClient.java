package com.yuegongbao.ygb.integration;

import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbOccupationMonitorStubItem;

/**
 * 卫健职业病监测接口。
 *
 * @author yuegongbao
 */
public interface OccupationClient
{
    List<YgbOccupationMonitorStubItem> pullMonitorRecords(String statMonth);
}
