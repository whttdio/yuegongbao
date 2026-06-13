package com.yuegongbao.ygb.util;

import java.util.Map;
import com.yuegongbao.common.utils.StringUtils;

public final class YgbRegionHelper
{
    private static final Map<String, String> REGION_NAME_MAP = Map.of(
        "440000", "广东省",
        "440106", "广州市天河区",
        "440305", "深圳市南山区",
        "440606", "佛山市顺德区");

    private YgbRegionHelper()
    {
    }

    public static String defaultDashboardRegion(String regionCode)
    {
        return StringUtils.isEmpty(regionCode) ? "440000" : regionCode;
    }

    public static String toRegionPrefix(String regionCode)
    {
        if (StringUtils.isEmpty(regionCode))
        {
            return null;
        }
        if (regionCode.endsWith("0000"))
        {
            return regionCode.substring(0, 2);
        }
        if (regionCode.endsWith("00"))
        {
            return regionCode.substring(0, 4);
        }
        return regionCode;
    }

    public static String resolveRegionName(String regionCode)
    {
        if (StringUtils.isEmpty(regionCode))
        {
            return "全省";
        }
        return REGION_NAME_MAP.getOrDefault(regionCode, regionCode);
    }
}
