package com.yuegongbao.ygb.util;

import java.util.Map;
import com.yuegongbao.common.utils.StringUtils;

public final class YgbRegionHelper
{
    private static final Map<String, String> REGION_NAME_MAP = Map.ofEntries(
        Map.entry("440000", "广东省"),
        Map.entry("440100", "广州市"),
        Map.entry("440103", "广州市荔湾区"),
        Map.entry("440104", "广州市越秀区"),
        Map.entry("440105", "广州市海珠区"),
        Map.entry("440106", "广州市天河区"),
        Map.entry("440111", "广州市白云区"),
        Map.entry("440112", "广州市黄埔区"),
        Map.entry("440113", "广州市番禺区"),
        Map.entry("440114", "广州市花都区"),
        Map.entry("440115", "广州市南沙区"),
        Map.entry("440117", "广州市从化区"),
        Map.entry("440118", "广州市增城区"),
        Map.entry("440300", "深圳市"),
        Map.entry("440303", "深圳市罗湖区"),
        Map.entry("440304", "深圳市福田区"),
        Map.entry("440305", "深圳市南山区"),
        Map.entry("440306", "深圳市宝安区"),
        Map.entry("440307", "深圳市龙岗区"),
        Map.entry("440308", "深圳市盐田区"),
        Map.entry("440309", "深圳市龙华区"),
        Map.entry("440310", "深圳市坪山区"),
        Map.entry("440311", "深圳市光明区"),
        Map.entry("440600", "佛山市"),
        Map.entry("440604", "佛山市禅城区"),
        Map.entry("440605", "佛山市南海区"),
        Map.entry("440606", "佛山市顺德区"),
        Map.entry("440607", "佛山市三水区"),
        Map.entry("440608", "佛山市高明区"),
        Map.entry("440700", "江门市"),
        Map.entry("441900", "东莞市"),
        Map.entry("442000", "中山市"));

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
