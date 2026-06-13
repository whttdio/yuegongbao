package com.yuegongbao.ygb.worker.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerActivityJoin;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerMessageHandleRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest;

public interface WorkerMessageService
{
    Map<String, Object> createComplaint(YgbPerson worker, SysUser user, WorkerComplaintCreateRequest request);

    Map<String, Object> listComplaints(Long userId, String status);

    List<WorkerComplaint> listComplaintManageRecords(WorkerComplaint query);

    Map<String, Object> getComplaintDetail(Long userId, Long complaintId);

    WorkerComplaint getComplaintManageDetail(Long complaintId);

    Map<String, Object> updateComplaintHandle(Long complaintId, WorkerMessageHandleRequest request, String operator);

    Map<String, Object> createLegalConsult(YgbPerson worker, SysUser user, WorkerLegalConsultCreateRequest request);

    Map<String, Object> listLegalConsults(Long userId, String status);

    List<WorkerLegalConsult> listLegalConsultManageRecords(WorkerLegalConsult query);

    Map<String, Object> getLegalConsultDetail(Long userId, Long consultId);

    WorkerLegalConsult getLegalConsultManageDetail(Long consultId);

    Map<String, Object> updateLegalConsultHandle(Long consultId, WorkerMessageHandleRequest request, String operator);

    boolean notifyActivityHandleUpdated(WorkerActivityJoin before, WorkerActivityJoin after, String operator,
        String statusMessage);

    int notifySalaryBatchSubmitted(YgbSalaryBatch batch, List<YgbSalaryDetail> detailList, String operator);

    boolean notifyTrainingLockReminder(YgbPerson worker, Long userId, WorkerTrainingProgress progress, String source);

    boolean notifyInsuranceArrearsReminder(YgbPerson worker, Long userId, String insuranceStatus,
        String latestArrearsMonth, String source);

    boolean notifyCertificateReminder(YgbPerson worker, Long userId, WorkerResume resume, String source);

    Map<String, Object> getNoticeList(Long userId, int pageNum, int pageSize);

    Map<String, Object> getNoticeDetail(Long userId, Long noticeId);

    void markNoticeRead(Long userId, Long noticeId);

    int getUnreadNoticeCount(Long userId);

    Map<String, Object> getHotline();

    Map<String, Object> getLegalArticleList();

    Map<String, Object> getLegalArticleDetail(String articleKey);

    Map<String, Object> getLegalFaqList(String keyword);

    Map<String, Object> getLegalFaqDetail(String faqKey);

    Map<String, Object> getUnionServiceHome();

    Map<String, Object> getUnionCaseList();

    Map<String, Object> getUnionNoticeList();

    Map<String, Object> getUnionCaseDetail(String caseKey);

    Map<String, Object> getUnionNoticeDetail(String noticeKey);

    Map<String, Object> getUnionContractList();

    Map<String, Object> getUnionContractDetail(String contractKey);
}
