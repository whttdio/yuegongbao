package com.yuegongbao.ygb.integration.stub;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.domain.vo.YgbBankPaymentResponse;
import com.yuegongbao.ygb.domain.vo.YgbSalaryPaymentItem;
import com.yuegongbao.ygb.integration.BankClient;

/**
 * 银行代发 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubBankClient extends AbstractYgbStubClient implements BankClient
{
    @Override
    public YgbBankPaymentResponse submitPayment(String batchNo, BigDecimal totalAmount, List<YgbSalaryPaymentItem> details)
    {
        YgbBankPaymentResponse response = new YgbBankPaymentResponse();
        response.setBatchNo(batchNo);
        response.setPaymentStatus("processing");
        Map<String, Object> payload = new HashMap<>();
        payload.put("batchNo", batchNo);
        payload.put("totalAmount", totalAmount);
        payload.put("detailCount", details == null ? 0 : details.size());
        fillStubMeta(response, "SUCCESS", "银行代发 Stub 已受理", payload);
        return response;
    }
}
