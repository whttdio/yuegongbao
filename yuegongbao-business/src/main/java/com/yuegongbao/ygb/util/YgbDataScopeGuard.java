package com.yuegongbao.ygb.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YgbDataScopeGuard
{
    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    public void assertEntityAllowed(Object entity)
    {
        regionScopeHelper.assertEntityRegionAllowed(entity);
        enterpriseScopeHelper.assertEntityEnterpriseAllowed(entity);
    }
}
