package com.yuegongbao.ygb.report.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbStatReportSummary
{
    private Integer totalCount;

    private Integer generatedCount;

    private Integer draftCount;

    private Integer warningOverviewCount;

    private Integer injuryRateCount;

    private Integer socialTaxCount;

    private Integer salaryCount;

    private Integer employmentCount;

    private Integer attendanceCount;

    private Integer socialCount;

    private Integer taxCount;

    private Integer aqInsuranceCount;

    private Integer newformCount;

    private Integer occupationCount;

    private Integer unionCount;

    private Integer customCount;

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getGeneratedCount()
    {
        return generatedCount;
    }

    public void setGeneratedCount(Integer generatedCount)
    {
        this.generatedCount = generatedCount;
    }

    public Integer getDraftCount()
    {
        return draftCount;
    }

    public void setDraftCount(Integer draftCount)
    {
        this.draftCount = draftCount;
    }

    public Integer getWarningOverviewCount()
    {
        return warningOverviewCount;
    }

    public void setWarningOverviewCount(Integer warningOverviewCount)
    {
        this.warningOverviewCount = warningOverviewCount;
    }

    public Integer getInjuryRateCount()
    {
        return injuryRateCount;
    }

    public void setInjuryRateCount(Integer injuryRateCount)
    {
        this.injuryRateCount = injuryRateCount;
    }

    public Integer getSocialTaxCount()
    {
        return socialTaxCount;
    }

    public void setSocialTaxCount(Integer socialTaxCount)
    {
        this.socialTaxCount = socialTaxCount;
    }

    public Integer getSalaryCount()
    {
        return salaryCount;
    }

    public void setSalaryCount(Integer salaryCount)
    {
        this.salaryCount = salaryCount;
    }

    public Integer getEmploymentCount()
    {
        return employmentCount;
    }

    public void setEmploymentCount(Integer employmentCount)
    {
        this.employmentCount = employmentCount;
    }

    public Integer getAttendanceCount()
    {
        return attendanceCount;
    }

    public void setAttendanceCount(Integer attendanceCount)
    {
        this.attendanceCount = attendanceCount;
    }

    public Integer getSocialCount()
    {
        return socialCount;
    }

    public void setSocialCount(Integer socialCount)
    {
        this.socialCount = socialCount;
    }

    public Integer getTaxCount()
    {
        return taxCount;
    }

    public void setTaxCount(Integer taxCount)
    {
        this.taxCount = taxCount;
    }

    public Integer getAqInsuranceCount()
    {
        return aqInsuranceCount;
    }

    public void setAqInsuranceCount(Integer aqInsuranceCount)
    {
        this.aqInsuranceCount = aqInsuranceCount;
    }

    public Integer getNewformCount()
    {
        return newformCount;
    }

    public void setNewformCount(Integer newformCount)
    {
        this.newformCount = newformCount;
    }

    public Integer getOccupationCount()
    {
        return occupationCount;
    }

    public void setOccupationCount(Integer occupationCount)
    {
        this.occupationCount = occupationCount;
    }

    public Integer getUnionCount()
    {
        return unionCount;
    }

    public void setUnionCount(Integer unionCount)
    {
        this.unionCount = unionCount;
    }

    public Integer getCustomCount()
    {
        return customCount;
    }

    public void setCustomCount(Integer customCount)
    {
        this.customCount = customCount;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }

    public List<Map<String, Object>> getAzbExplanation()
    {
        return azbExplanation;
    }

    public void setAzbExplanation(List<Map<String, Object>> azbExplanation)
    {
        this.azbExplanation = azbExplanation;
    }
}
