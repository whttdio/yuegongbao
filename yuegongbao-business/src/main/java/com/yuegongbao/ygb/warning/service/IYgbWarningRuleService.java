package com.yuegongbao.ygb.warning.service;

import java.util.List;
import com.yuegongbao.ygb.warning.domain.YgbWarningRule;
import com.yuegongbao.ygb.warning.domain.YgbWarningRuleSummary;

/**
 * 预警规则服务接口。
 * @author yuegongbao
 */
public interface IYgbWarningRuleService
{
    List<YgbWarningRule> selectWarningRuleList(YgbWarningRule warningRule);

    YgbWarningRuleSummary selectWarningRuleSummary(YgbWarningRule warningRule);

    YgbWarningRule selectWarningRuleById(Long ruleId);

    int insertWarningRule(YgbWarningRule warningRule);

    int updateWarningRule(YgbWarningRule warningRule);

    int deleteWarningRuleByIds(Long[] ruleIds, String updateBy);
}

