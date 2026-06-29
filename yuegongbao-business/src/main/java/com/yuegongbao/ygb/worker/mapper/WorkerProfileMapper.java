package com.yuegongbao.ygb.worker.mapper;

import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.worker.domain.WorkerFeedback;
import com.yuegongbao.ygb.worker.domain.WorkerPointExchange;
import com.yuegongbao.ygb.worker.domain.WorkerPointGoods;
import com.yuegongbao.ygb.worker.domain.WorkerPointLedger;
import com.yuegongbao.ygb.worker.domain.WorkerPushTestRecord;
import com.yuegongbao.ygb.worker.domain.WorkerRealnameApply;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.WorkerSetting;
import com.yuegongbao.ygb.worker.domain.WorkerUploadRecord;

public interface WorkerProfileMapper
{
    WorkerResume selectWorkerResume(@Param("userId") Long userId);

    WorkerResume selectWorkerResumeByPersonId(@Param("personId") Long personId);

    java.util.List<WorkerResume> selectAdminWorkerResumeList(WorkerResume query);

    WorkerResume selectWorkerResumeById(@Param("resumeId") Long resumeId);

    int insertWorkerResume(WorkerResume resume);

    int updateWorkerResume(WorkerResume resume);

    int updateWorkerResumeRemark(WorkerResume resume);

    int insertWorkerFeedback(WorkerFeedback feedback);

    WorkerRealnameApply selectLatestWorkerRealnameApply(@Param("userId") Long userId);

    int insertWorkerRealnameApply(WorkerRealnameApply apply);

    java.util.List<WorkerFeedback> selectWorkerFeedbackManageList(WorkerFeedback query);

    WorkerFeedback selectWorkerFeedbackById(@Param("feedbackId") Long feedbackId);

    int updateWorkerFeedbackHandle(WorkerFeedback feedback);

    WorkerSetting selectWorkerSetting(@Param("userId") Long userId);

    WorkerSetting selectWorkerSettingByPersonId(@Param("personId") Long personId);

    int insertWorkerSetting(WorkerSetting setting);

    int updateWorkerSetting(WorkerSetting setting);

    int countPointLedger(@Param("userId") Long userId);

    int insertWorkerPointLedger(WorkerPointLedger ledger);

    java.util.List<WorkerPointLedger> selectWorkerPointLedgerList(@Param("userId") Long userId);

    int countPointGoods();

    int insertWorkerPointGoods(WorkerPointGoods goods);

    java.util.List<WorkerPointGoods> selectWorkerPointGoodsList();

    WorkerPointGoods selectWorkerPointGoodsByKey(@Param("goodsKey") String goodsKey);

    int updateWorkerPointGoodsStock(@Param("goodsId") Long goodsId, @Param("stockCount") Integer stockCount,
        @Param("updateBy") String updateBy);

    int insertWorkerPointExchange(WorkerPointExchange exchange);

    java.util.List<WorkerPointExchange> selectWorkerPointExchangeList(@Param("userId") Long userId);

    int insertWorkerUploadRecord(WorkerUploadRecord record);

    java.util.List<WorkerUploadRecord> selectWorkerUploadRecordList(@Param("userId") Long userId,
        @Param("categoryCode") String categoryCode);

    java.util.List<WorkerUploadRecord> selectWorkerUploadRecordManageList(WorkerUploadRecord query);

    WorkerUploadRecord selectWorkerUploadRecordById(@Param("uploadId") Long uploadId);

    int insertWorkerPushTestRecord(WorkerPushTestRecord record);

    WorkerPushTestRecord selectLatestWorkerPushTestRecord(@Param("userId") Long userId);

    java.util.List<WorkerPushTestRecord> selectWorkerPushTestRecordList(@Param("userId") Long userId,
        @Param("limit") Integer limit);
}
