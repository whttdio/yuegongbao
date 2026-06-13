package com.yuegongbao.ygb.warning.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;

/**
 * 预警规则对象 t_warning_rule。
 *
 * @author yuegongbao
 */
public class YgbWarningRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "规则ID")
    private Long ruleId;

    @Excel(name = "规则名称")
    private String ruleName;

    @Excel(name = "级别", readConverterExp = "1=提示,2=黄警,3=红警")
    private String warnLevel;

    @Excel(name = "来源模块")
    private String sourceModule;

    @Excel(name = "规则表达式", width = 30)
    private String conditionText;

    @Excel(name = "推送目标", width = 20)
    private String pushTargets;

    @Excel(name = "超时时长")
    private Integer timeoutMinutes;

    @Excel(name = "升级级别")
    private String upgradeLevel;

    @Excel(name = "状态", readConverterExp = "0=停用,1=启用")
    private String ruleStatus;

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    @NotBlank(message = "规则名称不能为空")
    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }

    public String getWarnLevel()
    {
        return warnLevel;
    }

    public void setWarnLevel(String warnLevel)
    {
        this.warnLevel = warnLevel;
    }

    public String getSourceModule()
    {
        return sourceModule;
    }

    public void setSourceModule(String sourceModule)
    {
        this.sourceModule = sourceModule;
    }

    public String getConditionText()
    {
        return conditionText;
    }

    public void setConditionText(String conditionText)
    {
        this.conditionText = conditionText;
    }

    public String getPushTargets()
    {
        return pushTargets;
    }

    public void setPushTargets(String pushTargets)
    {
        this.pushTargets = pushTargets;
    }

    public Integer getTimeoutMinutes()
    {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(Integer timeoutMinutes)
    {
        this.timeoutMinutes = timeoutMinutes;
    }

    public String getUpgradeLevel()
    {
        return upgradeLevel;
    }

    public void setUpgradeLevel(String upgradeLevel)
    {
        this.upgradeLevel = upgradeLevel;
    }

    public String getRuleStatus()
    {
        return ruleStatus;
    }

    public void setRuleStatus(String ruleStatus)
    {
        this.ruleStatus = ruleStatus;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("ruleId", getRuleId())
            .append("ruleName", getRuleName())
            .append("warnLevel", getWarnLevel())
            .append("sourceModule", getSourceModule())
            .append("conditionText", getConditionText())
            .append("pushTargets", getPushTargets())
            .append("timeoutMinutes", getTimeoutMinutes())
            .append("upgradeLevel", getUpgradeLevel())
            .append("ruleStatus", getRuleStatus())
            .append("remark", getRemark())
            .toString();
    }
}
