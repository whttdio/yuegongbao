package com.yuegongbao.ygb.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class YgbRegionScopeHelperTest
{
    private final YgbRegionScopeHelper helper = new YgbRegionScopeHelper();

    @Test
    void isRegionAllowedAcceptsPrefixMatch()
    {
        assertTrue(helper.isRegionAllowed("440106", java.util.List.of("440106")));
        assertTrue(helper.isRegionAllowed("440106001", java.util.List.of("440106")));
        assertFalse(helper.isRegionAllowed("440305", java.util.List.of("440106")));
    }

    @Test
    void isRegionAllowedTreatsProvinceCodeAsUnrestricted()
    {
        assertTrue(helper.isRegionAllowed("440305", java.util.List.of("440000")));
        assertTrue(helper.isRegionAllowed(null, java.util.List.of("440106")));
        assertTrue(helper.isRegionAllowed("440106", java.util.List.of()));
    }
}
