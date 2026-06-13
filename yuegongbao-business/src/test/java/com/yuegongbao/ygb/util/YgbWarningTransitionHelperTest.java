package com.yuegongbao.ygb.util;

import org.junit.jupiter.api.Test;
import com.yuegongbao.common.exception.ServiceException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YgbWarningTransitionHelperTest
{
    @Test
    void transitSupportsExpectedHappyPath()
    {
        assertEquals("1", YgbWarningTransitionHelper.transit("0", "PROCESS"));
        assertEquals("2", YgbWarningTransitionHelper.transit("1", "CLOSE"));
        assertEquals("3", YgbWarningTransitionHelper.transit("0", "MISREPORT"));
        assertEquals("4", YgbWarningTransitionHelper.transit("1", "UPGRADE"));
    }

    @Test
    void transitRejectsInvalidStateAndUnknownAction()
    {
        assertThrows(ServiceException.class, () -> YgbWarningTransitionHelper.transit("2", "PROCESS"));
        assertThrows(ServiceException.class, () -> YgbWarningTransitionHelper.transit("0", "UNKNOWN"));
    }
}
