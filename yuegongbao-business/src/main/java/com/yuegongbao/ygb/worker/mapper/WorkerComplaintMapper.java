package com.yuegongbao.ygb.worker.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;

public interface WorkerComplaintMapper
{
    int insertWorkerComplaint(WorkerComplaint complaint);

    List<WorkerComplaint> selectWorkerComplaintList(@Param("userId") Long userId, @Param("status") String status);

    List<WorkerComplaint> selectWorkerComplaintManageList(WorkerComplaint query);

    WorkerComplaint selectWorkerComplaintById(@Param("complaintId") Long complaintId, @Param("userId") Long userId);

    WorkerComplaint selectWorkerComplaintByIdForManage(@Param("complaintId") Long complaintId);

    int updateWorkerComplaintHandle(WorkerComplaint complaint);

    int updateWorkerComplaintSyncResult(WorkerComplaint complaint);
}
