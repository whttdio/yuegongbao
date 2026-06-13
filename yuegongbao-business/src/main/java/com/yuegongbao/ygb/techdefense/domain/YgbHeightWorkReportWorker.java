package com.yuegongbao.ygb.techdefense.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import jakarta.validation.constraints.NotBlank;

/**
 * 高处作业作业人员明细对象 t_height_work_report_worker
 *
 * @author yuegongbao
 */
public class YgbHeightWorkReportWorker
{
    private static final long serialVersionUID = 1L;

    private Long rowId;

    private Long reportId;

    @Excel(name = "作业人员")
    private String workerName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "高处作业证号")
    private String certNo;

    private String certPhotoUrl;

    private String certValidStatus;

    private String certValidMessage;

    private Integer sortOrder;

    public Long getRowId()
    {
        return rowId;
    }

    public void setRowId(Long rowId)
    {
        this.rowId = rowId;
    }

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    @NotBlank(message = "作业人员姓名不能为空")
    public String getWorkerName()
    {
        return workerName;
    }

    public void setWorkerName(String workerName)
    {
        this.workerName = workerName;
    }

    @NotBlank(message = "身份证号不能为空")
    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    @NotBlank(message = "高处作业证号不能为空")
    public String getCertNo()
    {
        return certNo;
    }

    public void setCertNo(String certNo)
    {
        this.certNo = certNo;
    }

    public String getCertPhotoUrl()
    {
        return certPhotoUrl;
    }

    public void setCertPhotoUrl(String certPhotoUrl)
    {
        this.certPhotoUrl = certPhotoUrl;
    }

    public String getCertValidStatus()
    {
        return certValidStatus;
    }

    public void setCertValidStatus(String certValidStatus)
    {
        this.certValidStatus = certValidStatus;
    }

    public String getCertValidMessage()
    {
        return certValidMessage;
    }

    public void setCertValidMessage(String certValidMessage)
    {
        this.certValidMessage = certValidMessage;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("rowId", getRowId())
            .append("reportId", getReportId())
            .append("workerName", getWorkerName())
            .append("idCard", getIdCard())
            .append("certNo", getCertNo())
            .append("certValidStatus", getCertValidStatus())
            .toString();
    }
}
