package com.yuegongbao.ygb.integration.stub;

import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.ygb.domain.vo.YgbUnionSyncResponse;
import com.yuegongbao.ygb.integration.UnionAidClient;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 工会法律援助 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubUnionAidClient extends AbstractYgbStubClient implements UnionAidClient
{
    @Override
    public YgbUnionSyncResponse submitComplaint(WorkerComplaint complaint)
    {
        YgbUnionSyncResponse response = new YgbUnionSyncResponse();
        response.setSuccess(true);
        response.setSyncStatus("SUCCESS");
        response.setSyncMessage("工会法律援助 Stub 已受理投诉工单。");
        response.setTicketNo("UNION-C-" + (complaint == null || complaint.getComplaintId() == null ? "NEW" : complaint.getComplaintId()));
        fillStubMeta(response, "SUCCESS", response.getSyncMessage(), complaintPayload(complaint));
        return response;
    }

    @Override
    public YgbUnionSyncResponse submitLegalConsult(WorkerLegalConsult consult)
    {
        YgbUnionSyncResponse response = new YgbUnionSyncResponse();
        response.setSuccess(true);
        response.setSyncStatus("SUCCESS");
        response.setSyncMessage("工会法律援助 Stub 已受理法律咨询工单。");
        response.setTicketNo("UNION-L-" + (consult == null || consult.getConsultId() == null ? "NEW" : consult.getConsultId()));
        fillStubMeta(response, "SUCCESS", response.getSyncMessage(), legalConsultPayload(consult));
        return response;
    }

    private JSONObject complaintPayload(WorkerComplaint complaint)
    {
        JSONObject payload = new JSONObject();
        if (complaint != null)
        {
            payload.put("complaintId", complaint.getComplaintId());
            payload.put("personId", complaint.getPersonId());
            payload.put("complaintType", complaint.getComplaintType());
            payload.put("title", complaint.getTitle());
        }
        return payload;
    }

    private JSONObject legalConsultPayload(WorkerLegalConsult consult)
    {
        JSONObject payload = new JSONObject();
        if (consult != null)
        {
            payload.put("consultId", consult.getConsultId());
            payload.put("personId", consult.getPersonId());
            payload.put("consultType", consult.getConsultType());
            payload.put("title", consult.getTitle());
        }
        return payload;
    }
}
