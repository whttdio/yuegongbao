package com.yuegongbao.ygb.util;

import com.yuegongbao.common.exception.ServiceException;

/**
 * 预警状态流转工具。
 *
 * 状态：0待处理 1处理中 2已办结 3误报 4已升级
 *
 * @author yuegongbao
 */
public final class YgbWarningTransitionHelper
{
    private YgbWarningTransitionHelper()
    {
    }

    public static String transit(String currentStatus, String action)
    {
        String current = currentStatus == null ? "0" : currentStatus;
        String normalized = action == null ? "" : action.trim().toUpperCase();
        switch (normalized)
        {
            case "PROCESS":
                requireState(current, "0", "1", "4");
                return "1";
            case "CLOSE":
                requireState(current, "0", "1", "4");
                return "2";
            case "MISREPORT":
                requireState(current, "0", "1");
                return "3";
            case "UPGRADE":
                requireState(current, "0", "1");
                return "4";
            default:
                throw new ServiceException("不支持的预警处置动作");
        }
    }

    private static void requireState(String current, String... allowStates)
    {
        for (String allowState : allowStates)
        {
            if (allowState.equals(current))
            {
                return;
            }
        }
        throw new ServiceException("当前预警状态不允许执行该操作");
    }
}
