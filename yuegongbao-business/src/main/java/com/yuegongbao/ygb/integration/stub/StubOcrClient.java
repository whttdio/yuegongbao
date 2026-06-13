package com.yuegongbao.ygb.integration.stub;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.domain.vo.YgbOcrExtractResponse;
import com.yuegongbao.ygb.integration.OcrClient;

/**
 * OCR 识别 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubOcrClient extends AbstractYgbStubClient implements OcrClient
{
    @Override
    public YgbOcrExtractResponse extractContract(String contractFileUrl)
    {
        YgbOcrExtractResponse response = new YgbOcrExtractResponse();
        response.setContractNo("OCR-STUB-" + System.currentTimeMillis());
        response.setPersonName("OCR示例人员");
        response.setIdCard("440100199001011234");
        response.setMonthlyWage(new BigDecimal("6500.00"));
        Map<String, Object> payload = new HashMap<>();
        payload.put("contractFileUrl", contractFileUrl);
        fillStubMeta(response, "SUCCESS", "OCR Stub 提取完成", payload);
        return response;
    }
}
