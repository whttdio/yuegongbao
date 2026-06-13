package com.yuegongbao.ygb.integration;

import java.math.BigDecimal;
import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbBankPaymentResponse;
import com.yuegongbao.ygb.domain.vo.YgbSalaryPaymentItem;

/**
 * 银行代发接口。
 *
 * @author yuegongbao
 */
public interface BankClient
{
    YgbBankPaymentResponse submitPayment(String batchNo, BigDecimal totalAmount, List<YgbSalaryPaymentItem> details);
}
