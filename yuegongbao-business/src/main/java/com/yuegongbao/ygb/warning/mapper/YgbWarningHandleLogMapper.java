package com.yuegongbao.ygb.warning.mapper;

import java.util.List;
import com.yuegongbao.ygb.warning.domain.YgbWarningHandleLog;

/**
 * 预警处置日志 Mapper。
 *
 * @author yuegongbao
 */
public interface YgbWarningHandleLogMapper
{
    int insertWarningHandleLog(YgbWarningHandleLog handleLog);

    List<YgbWarningHandleLog> selectWarningHandleLogList(Long warnId);
}
