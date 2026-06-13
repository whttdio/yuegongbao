package com.yuegongbao.ygb.integration;

import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbNewformWorkerStubItem;

/**
 * 新业态平台接口。
 *
 * @author yuegongbao
 */
public interface NewformClient
{
    List<YgbNewformWorkerStubItem> pullWorkerRecords(String statMonth);
}
