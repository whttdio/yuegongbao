package com.yuegongbao.ygb.foundation.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.annotation.Excel.ColumnType;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 人员主数据对象 t_person
 *
 * @author yuegongbao
 */
public class YgbPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 人员ID */
    @Excel(name = "人员ID", cellType = ColumnType.NUMERIC)
    private Long personId;

    /** 所属企业ID */
    private Long enterpriseId;

    /** 所属企业名称 */
    @Excel(name = "所属企业")
    private String enterpriseName;

    /** 区域编码 */
    @Excel(name = "区域编码")
    private String regionCode;

    /** 姓名 */
    @Excel(name = "姓名")
    private String personName;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String mobile;

    /** 用工类型 */
    @Excel(name = "用工类型", readConverterExp = "1=派遣工,2=正式工,3=外包工,4=新业态人员")
    private String workerType;

    /** 工种 */
    @Excel(name = "工种")
    private String jobType;

    /** 持证状态 */
    @Excel(name = "持证状态", readConverterExp = "0=未校验,1=有效,2=临期,3=过期")
    private String certStatus;

    /** 参保状态 */
    @Excel(name = "参保状态", readConverterExp = "0=未参保,1=已参保,2=停保")
    private String insuranceStatus;

    /** 在岗状态 */
    @Excel(name = "在岗状态", readConverterExp = "0=在岗,1=离岗")
    private String employmentStatus;

    /** 入场日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入场日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date entryDate;

    /** 离场日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "离场日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date leaveDate;

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    @NotNull(message = "所属企业不能为空")
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    @NotBlank(message = "姓名不能为空")
    @Size(max = 30, message = "姓名长度不能超过30个字符")
    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[0-9Xx]{15,18}$", message = "身份证号格式不正确")
    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    @Pattern(regexp = "^$|^1\\d{10}$", message = "联系电话格式不正确")
    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    @NotBlank(message = "用工类型不能为空")
    public String getWorkerType()
    {
        return workerType;
    }

    public void setWorkerType(String workerType)
    {
        this.workerType = workerType;
    }

    @Size(max = 50, message = "工种长度不能超过50个字符")
    public String getJobType()
    {
        return jobType;
    }

    public void setJobType(String jobType)
    {
        this.jobType = jobType;
    }

    public String getCertStatus()
    {
        return certStatus;
    }

    public void setCertStatus(String certStatus)
    {
        this.certStatus = certStatus;
    }

    public String getInsuranceStatus()
    {
        return insuranceStatus;
    }

    public void setInsuranceStatus(String insuranceStatus)
    {
        this.insuranceStatus = insuranceStatus;
    }

    public String getEmploymentStatus()
    {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus)
    {
        this.employmentStatus = employmentStatus;
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public void setEntryDate(Date entryDate)
    {
        this.entryDate = entryDate;
    }

    public Date getLeaveDate()
    {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate)
    {
        this.leaveDate = leaveDate;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("personId", getPersonId())
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("regionCode", getRegionCode())
            .append("personName", getPersonName())
            .append("idCard", getIdCard())
            .append("mobile", getMobile())
            .append("workerType", getWorkerType())
            .append("jobType", getJobType())
            .append("certStatus", getCertStatus())
            .append("insuranceStatus", getInsuranceStatus())
            .append("employmentStatus", getEmploymentStatus())
            .append("entryDate", getEntryDate())
            .append("leaveDate", getLeaveDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
