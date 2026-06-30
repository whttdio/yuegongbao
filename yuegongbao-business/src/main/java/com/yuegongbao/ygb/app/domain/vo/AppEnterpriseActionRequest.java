package com.yuegongbao.ygb.app.domain.vo;

public class AppEnterpriseActionRequest
{
    private String actionType;

    private Long[] personIds;

    private Long[] deviceIds;

    private Long[] batchIds;

    private Long[] approvalIds;

    private Long[] planIds;

    private String title;

    private String location;

    private String salary;

    private String requirement;

    private String source;

    private String batchMonth;

    private String decision;

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public Long[] getPersonIds()
    {
        return personIds;
    }

    public void setPersonIds(Long[] personIds)
    {
        this.personIds = personIds;
    }

    public Long[] getDeviceIds()
    {
        return deviceIds;
    }

    public void setDeviceIds(Long[] deviceIds)
    {
        this.deviceIds = deviceIds;
    }

    public Long[] getBatchIds()
    {
        return batchIds;
    }

    public void setBatchIds(Long[] batchIds)
    {
        this.batchIds = batchIds;
    }

    public Long[] getApprovalIds()
    {
        return approvalIds;
    }

    public void setApprovalIds(Long[] approvalIds)
    {
        this.approvalIds = approvalIds;
    }

    public Long[] getPlanIds()
    {
        return planIds;
    }

    public void setPlanIds(Long[] planIds)
    {
        this.planIds = planIds;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getSalary()
    {
        return salary;
    }

    public void setSalary(String salary)
    {
        this.salary = salary;
    }

    public String getRequirement()
    {
        return requirement;
    }

    public void setRequirement(String requirement)
    {
        this.requirement = requirement;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getBatchMonth()
    {
        return batchMonth;
    }

    public void setBatchMonth(String batchMonth)
    {
        this.batchMonth = batchMonth;
    }

    public String getDecision()
    {
        return decision;
    }

    public void setDecision(String decision)
    {
        this.decision = decision;
    }
}
