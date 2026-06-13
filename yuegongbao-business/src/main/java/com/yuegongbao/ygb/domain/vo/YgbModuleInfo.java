package com.yuegongbao.ygb.domain.vo;

public class YgbModuleInfo
{
    private String code;

    private String name;

    private String description;

    private Integer priority;

    private String categoryCode;

    private String categoryName;

    private Long menuRootId;

    private String backendPackage;

    private String futureServiceName;

    private String splitLevel;

    public YgbModuleInfo()
    {
    }

    public YgbModuleInfo(String code, String name, String description, Integer priority)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public YgbModuleInfo(String code, String name, String description, Integer priority, String categoryCode,
        String categoryName, Long menuRootId, String backendPackage, String futureServiceName, String splitLevel)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.menuRootId = menuRootId;
        this.backendPackage = backendPackage;
        this.futureServiceName = futureServiceName;
        this.splitLevel = splitLevel;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    public String getCategoryCode()
    {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode)
    {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public Long getMenuRootId()
    {
        return menuRootId;
    }

    public void setMenuRootId(Long menuRootId)
    {
        this.menuRootId = menuRootId;
    }

    public String getBackendPackage()
    {
        return backendPackage;
    }

    public void setBackendPackage(String backendPackage)
    {
        this.backendPackage = backendPackage;
    }

    public String getFutureServiceName()
    {
        return futureServiceName;
    }

    public void setFutureServiceName(String futureServiceName)
    {
        this.futureServiceName = futureServiceName;
    }

    public String getSplitLevel()
    {
        return splitLevel;
    }

    public void setSplitLevel(String splitLevel)
    {
        this.splitLevel = splitLevel;
    }
}
