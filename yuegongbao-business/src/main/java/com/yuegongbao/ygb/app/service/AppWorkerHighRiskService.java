package com.yuegongbao.ygb.app.service;

import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.app.domain.vo.AppWorkerHighRiskActionRequest;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface AppWorkerHighRiskService
{
    Map<String, Object> getUnlockDashboard(SysUser user, YgbPerson worker);

    Map<String, Object> verifyScan(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> verifyFace(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> submitUnlock(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> getOutworkDraft(SysUser user, YgbPerson worker);

    Map<String, Object> saveOutworkAttachment(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> submitOutworkApply(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> getCertificates(SysUser user, YgbPerson worker);

    Map<String, Object> submitCertificateRenew(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> submitCertificateUpload(SysUser user, YgbPerson worker, AppWorkerHighRiskActionRequest request);

    Map<String, Object> getWorkRecords(SysUser user, YgbPerson worker);
}
