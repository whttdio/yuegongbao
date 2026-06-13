package com.yuegongbao.ygb.techdefense.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportWorker;

public interface YgbHeightWorkReportWorkerMapper
{
    List<YgbHeightWorkReportWorker> selectWorkersByReportId(Long reportId);

    int insertHeightWorkReportWorkers(@Param("reportId") Long reportId,
        @Param("workers") List<YgbHeightWorkReportWorker> workers);

    int deleteWorkersByReportId(Long reportId);
}
