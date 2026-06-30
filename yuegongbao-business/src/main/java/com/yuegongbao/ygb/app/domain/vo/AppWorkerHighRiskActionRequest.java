package com.yuegongbao.ygb.app.domain.vo;

import java.util.List;

public class AppWorkerHighRiskActionRequest
{
    private String deviceCode;

    private Boolean faceVerified;

    private String certificateStatus;

    private String injuryInsuranceStatus;

    private String aqInsuranceStatus;

    private String location;

    private String schedule;

    private String guardian;

    private String summary;

    private List<String> attachments;

    private Long[] certificateIds;

    private String source;

    public String getDeviceCode()
    {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode)
    {
        this.deviceCode = deviceCode;
    }

    public Boolean getFaceVerified()
    {
        return faceVerified;
    }

    public void setFaceVerified(Boolean faceVerified)
    {
        this.faceVerified = faceVerified;
    }

    public String getCertificateStatus()
    {
        return certificateStatus;
    }

    public void setCertificateStatus(String certificateStatus)
    {
        this.certificateStatus = certificateStatus;
    }

    public String getInjuryInsuranceStatus()
    {
        return injuryInsuranceStatus;
    }

    public void setInjuryInsuranceStatus(String injuryInsuranceStatus)
    {
        this.injuryInsuranceStatus = injuryInsuranceStatus;
    }

    public String getAqInsuranceStatus()
    {
        return aqInsuranceStatus;
    }

    public void setAqInsuranceStatus(String aqInsuranceStatus)
    {
        this.aqInsuranceStatus = aqInsuranceStatus;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getSchedule()
    {
        return schedule;
    }

    public void setSchedule(String schedule)
    {
        this.schedule = schedule;
    }

    public String getGuardian()
    {
        return guardian;
    }

    public void setGuardian(String guardian)
    {
        this.guardian = guardian;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public List<String> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<String> attachments)
    {
        this.attachments = attachments;
    }

    public Long[] getCertificateIds()
    {
        return certificateIds;
    }

    public void setCertificateIds(Long[] certificateIds)
    {
        this.certificateIds = certificateIds;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }
}
