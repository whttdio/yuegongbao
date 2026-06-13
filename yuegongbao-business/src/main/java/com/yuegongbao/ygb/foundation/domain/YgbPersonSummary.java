package com.yuegongbao.ygb.foundation.domain;

import java.util.List;
import java.util.Map;

public class YgbPersonSummary
{
    private Integer totalCount;
    private Integer onPostCount;
    private Integer certRiskCount;
    private Integer uninsuredCount;
    private Integer enterpriseCount;
    private Integer dispatchWorkerCount;
    private Integer outsourcingWorkerCount;
    private List<Map<String, Object>> ygbExplanation;
    private List<Map<String, Object>> azbExplanation;

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getOnPostCount() { return onPostCount; }
    public void setOnPostCount(Integer onPostCount) { this.onPostCount = onPostCount; }
    public Integer getCertRiskCount() { return certRiskCount; }
    public void setCertRiskCount(Integer certRiskCount) { this.certRiskCount = certRiskCount; }
    public Integer getUninsuredCount() { return uninsuredCount; }
    public void setUninsuredCount(Integer uninsuredCount) { this.uninsuredCount = uninsuredCount; }
    public Integer getEnterpriseCount() { return enterpriseCount; }
    public void setEnterpriseCount(Integer enterpriseCount) { this.enterpriseCount = enterpriseCount; }
    public Integer getDispatchWorkerCount() { return dispatchWorkerCount; }
    public void setDispatchWorkerCount(Integer dispatchWorkerCount) { this.dispatchWorkerCount = dispatchWorkerCount; }
    public Integer getOutsourcingWorkerCount() { return outsourcingWorkerCount; }
    public void setOutsourcingWorkerCount(Integer outsourcingWorkerCount) { this.outsourcingWorkerCount = outsourcingWorkerCount; }
    public List<Map<String, Object>> getYgbExplanation() { return ygbExplanation; }
    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation) { this.ygbExplanation = ygbExplanation; }
    public List<Map<String, Object>> getAzbExplanation() { return azbExplanation; }
    public void setAzbExplanation(List<Map<String, Object>> azbExplanation) { this.azbExplanation = azbExplanation; }
}
