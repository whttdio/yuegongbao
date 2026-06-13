package com.yuegongbao.ygb.integration.stub;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.domain.vo.YgbCertCheckResult;
import com.yuegongbao.ygb.integration.EmergencyCertClient;

/**
 * 特种证校验 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubEmergencyCertClient extends AbstractYgbStubClient implements EmergencyCertClient
{
    @Override
    public YgbCertCheckResult checkValidity(String idCard, String certNo)
    {
        YgbCertCheckResult response = new YgbCertCheckResult();
        boolean valid = StringUtils.isNotEmpty(idCard) && !idCard.endsWith("9");
        response.setValid(valid);
        response.setCertStatus(valid ? "VALID" : "EXPIRED");
        Map<String, Object> payload = new HashMap<>();
        payload.put("idCard", idCard);
        payload.put("certNo", certNo);
        fillStubMeta(response, valid ? "SUCCESS" : "FAIL", valid ? "证件校验通过" : "证件已过期", payload);
        return response;
    }
}
