package com.yuegongbao.ygb.app.service;

import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.app.domain.vo.AppEnterpriseActionRequest;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface AppEnterpriseService
{
    Map<String, Object> getHomeDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> getWorkbenchDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> getPeopleLedger(SysUser user, YgbPerson worker);

    Map<String, Object> submitPeopleAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> exportPeopleLedger(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> getDeviceLedger(SysUser user, YgbPerson worker);

    Map<String, Object> submitDeviceAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> exportDeviceLedger(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> getSalaryDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> submitSalaryConfirm(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> importSalaryDraft(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> getOperationApprovalDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> submitOperationApproval(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> exportOperationLedger(SysUser user, YgbPerson worker);

    Map<String, Object> getInsuranceDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> submitInsuranceAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> getTrainingDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> saveTrainingPlanDraft(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> submitTrainingAction(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> getJobPublishDraft(SysUser user, YgbPerson worker);

    Map<String, Object> saveJobPublishDraft(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);

    Map<String, Object> submitJobPublish(SysUser user, YgbPerson worker, AppEnterpriseActionRequest request);
}
