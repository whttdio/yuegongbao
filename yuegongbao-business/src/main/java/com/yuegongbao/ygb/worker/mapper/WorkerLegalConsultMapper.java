package com.yuegongbao.ygb.worker.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;

public interface WorkerLegalConsultMapper
{
    int insertWorkerLegalConsult(WorkerLegalConsult consult);

    List<WorkerLegalConsult> selectWorkerLegalConsultList(@Param("userId") Long userId, @Param("status") String status);

    List<WorkerLegalConsult> selectWorkerLegalConsultManageList(WorkerLegalConsult query);

    WorkerLegalConsult selectWorkerLegalConsultById(@Param("consultId") Long consultId, @Param("userId") Long userId);

    WorkerLegalConsult selectWorkerLegalConsultByIdForManage(@Param("consultId") Long consultId);

    int updateWorkerLegalConsultHandle(WorkerLegalConsult consult);

    int updateWorkerLegalConsultSyncResult(WorkerLegalConsult consult);
}
