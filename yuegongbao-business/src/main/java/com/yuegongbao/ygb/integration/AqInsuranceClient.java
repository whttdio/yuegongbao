package com.yuegongbao.ygb.integration;

import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbAqInsuranceStubItem;

/**
 * 安责险接口。
 *
 * @author yuegongbao
 */
public interface AqInsuranceClient
{
    List<YgbAqInsuranceStubItem> pullMonthlyPolicies(String statMonth);
}
