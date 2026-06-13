package com.yuegongbao.ygb.warning.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.warning.domain.YgbWarningRule;

/**
 * 预警规则 Mapper。
 *
 * @author yuegongbao
 */
public interface YgbWarningRuleMapper
{
    List<YgbWarningRule> selectWarningRuleList(YgbWarningRule warningRule);

    YgbWarningRule selectWarningRuleById(Long ruleId);

    int insertWarningRule(YgbWarningRule warningRule);

    int updateWarningRule(YgbWarningRule warningRule);

    int deleteWarningRuleByIds(@Param("ruleIds") Long[] ruleIds, @Param("updateBy") String updateBy);
}
