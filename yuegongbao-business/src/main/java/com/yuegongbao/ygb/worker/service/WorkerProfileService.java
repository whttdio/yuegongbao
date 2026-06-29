package com.yuegongbao.ygb.worker.service;

import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerFeedback;
import com.yuegongbao.ygb.worker.domain.WorkerUploadRecord;
import com.yuegongbao.ygb.worker.domain.vo.WorkerFeedbackCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPointExchangeRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushRegisterRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerPushTestRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerRealnameSubmitRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerResumeSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerSettingSaveRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerUploadRecordSaveRequest;

public interface WorkerProfileService
{
    Map<String, Object> getResume(YgbPerson worker, Long userId);

    Map<String, Object> getLaborContractList(YgbPerson worker);

    Map<String, Object> getLaborContractDetail(YgbPerson worker, Long contractId);

    Map<String, Object> saveResume(YgbPerson worker, SysUser user, WorkerResumeSaveRequest request);

    Map<String, Object> getHelpList();

    Map<String, Object> getHelpDetail(String articleKey);

    Map<String, Object> getRealnameDetail(YgbPerson worker, Long userId, String userName);

    Map<String, Object> submitRealnameApply(YgbPerson worker, SysUser user, WorkerRealnameSubmitRequest request);

    Map<String, Object> createFeedback(YgbPerson worker, SysUser user, WorkerFeedbackCreateRequest request);

    java.util.List<WorkerFeedback> listFeedbackManageRecords(WorkerFeedback query);

    WorkerFeedback getFeedbackManageDetail(Long feedbackId);

    Map<String, Object> updateFeedbackHandle(Long feedbackId, WorkerMessageHandleRequest request, String operator);

    Map<String, Object> getSettings(YgbPerson worker, Long userId);

    Map<String, Object> saveSettings(YgbPerson worker, SysUser user, WorkerSettingSaveRequest request);

    Map<String, Object> registerPush(YgbPerson worker, SysUser user, WorkerPushRegisterRequest request);

    Map<String, Object> sendPushTest(YgbPerson worker, SysUser user, WorkerPushTestRequest request);

    Map<String, Object> getPointAccount(YgbPerson worker, Long userId);

    Map<String, Object> exchangePointGoods(YgbPerson worker, SysUser user, WorkerPointExchangeRequest request);

    Map<String, Object> getInsuranceSecurity(YgbPerson worker, Long userId);

    Map<String, Object> createUploadRecord(YgbPerson worker, SysUser user, WorkerUploadRecordSaveRequest request);

    Map<String, Object> getUploadRecordList(Long userId, String categoryCode);

    java.util.List<WorkerUploadRecord> listUploadRecordManageRecords(WorkerUploadRecord query);

    WorkerUploadRecord getUploadRecordManageDetail(Long uploadId);
}
