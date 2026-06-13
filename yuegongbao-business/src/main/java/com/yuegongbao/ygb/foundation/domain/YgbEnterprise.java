package com.yuegongbao.ygb.foundation.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.annotation.Excel.ColumnType;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 企业主数据对象 t_enterprise
 *
 * @author yuegongbao
 */
public class YgbEnterprise extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 企业ID */
    @Excel(name = "企业ID", cellType = ColumnType.NUMERIC)
    private Long enterpriseId;

    /** 企业名称 */
    @Excel(name = "企业名称")
    private String enterpriseName;

    /** 统一社会信用代码 */
    @Excel(name = "统一社会信用代码")
    private String enterpriseCode;

    /** 区域编码 */
    @Excel(name = "区域编码")
    private String regionCode;

    /** 企业类型 */
    @Excel(name = "企业类型", readConverterExp = "1=派遣单位,2=用工单位,3=服务机构,4=监管单位")
    private String enterpriseType;

    /** 法定代表人 */
    @Excel(name = "法定代表人")
    private String legalPerson;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 企业地址 */
    @Excel(name = "企业地址", width = 30)
    private String address;

    /** 成立日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date establishedDate;

    /** 对接同步状态 */
    @Excel(name = "同步状态", readConverterExp = "0=未同步,1=已同步,2=同步异常")
    private String syncStatus;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    @NotBlank(message = "企业名称不能为空")
    @Size(max = 100, message = "企业名称长度不能超过100个字符")
    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    @NotBlank(message = "统一社会信用代码不能为空")
    @Size(max = 18, message = "统一社会信用代码长度不能超过18个字符")
    public String getEnterpriseCode()
    {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode)
    {
        this.enterpriseCode = enterpriseCode;
    }

    @NotBlank(message = "区域编码不能为空")
    @Size(max = 6, message = "区域编码长度不能超过6个字符")
    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    @NotBlank(message = "企业类型不能为空")
    public String getEnterpriseType()
    {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType)
    {
        this.enterpriseType = enterpriseType;
    }

    @Size(max = 30, message = "法定代表人长度不能超过30个字符")
    public String getLegalPerson()
    {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson)
    {
        this.legalPerson = legalPerson;
    }

    @Size(max = 30, message = "联系人长度不能超过30个字符")
    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    @Pattern(regexp = "^$|^1\\d{10}$", message = "联系电话格式不正确")
    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    @Size(max = 200, message = "企业地址长度不能超过200个字符")
    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Date getEstablishedDate()
    {
        return establishedDate;
    }

    public void setEstablishedDate(Date establishedDate)
    {
        this.establishedDate = establishedDate;
    }

    public String getSyncStatus()
    {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("enterpriseCode", getEnterpriseCode())
            .append("regionCode", getRegionCode())
            .append("enterpriseType", getEnterpriseType())
            .append("legalPerson", getLegalPerson())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("address", getAddress())
            .append("establishedDate", getEstablishedDate())
            .append("syncStatus", getSyncStatus())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
