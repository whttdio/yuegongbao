package com.yuegongbao.ygb.integration;

import com.yuegongbao.ygb.domain.vo.YgbUnionSyncResponse;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;

/**
 * 工会法律援助系统联动接口。
 *
 * @author yuegongbao
 */
public interface UnionAidClient
{
    YgbUnionSyncResponse submitComplaint(WorkerComplaint complaint);

    YgbUnionSyncResponse submitLegalConsult(WorkerLegalConsult consult);
}
