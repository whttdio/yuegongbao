package com.yuegongbao.ygb.compliance.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.compliance.mapper.YgbAttendanceMonthlyMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryBatchMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.domain.vo.YgbBankCallbackRequest;
import com.yuegongbao.ygb.domain.vo.YgbBankPaymentResponse;
import com.yuegongbao.ygb.domain.vo.YgbSalaryPaymentItem;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.integration.BankClient;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbSalaryBatchServiceImplTest
{
    @Mock
    private YgbSalaryBatchMapper salaryBatchMapper;

    @Mock
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Mock
    private YgbAttendanceMonthlyMapper attendanceMonthlyMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private YgbContractMapper contractMapper;

    @Mock
    private BankClient bankClient;

    @Mock
    private WorkerMessageService workerMessageService;

    @InjectMocks
    private YgbSalaryBatchServiceImpl service;

    @Test
    void submitBatchPushesPendingDetailsToBankAndMarksBatchProcessing()
    {
        YgbSalaryBatch batch = batch(9L, "BATCH-01", "4", new BigDecimal("2000.00"));
        YgbSalaryBatch processingBatch = batch(9L, "BATCH-01", "5", new BigDecimal("2000.00"));
        YgbSalaryDetail detail = detail(100L, 9L, "6222020000000001", "0", new BigDecimal("1200.00"));
        YgbBankPaymentResponse response = new YgbBankPaymentResponse();
        response.setSourceStatus("SUCCESS");
        response.setSourceMessage("OK");

        when(salaryBatchMapper.selectSalaryBatchById(9L)).thenReturn(batch, processingBatch);
        when(salaryDetailMapper.selectSalaryDetailList(any(YgbSalaryDetail.class))).thenReturn(List.of(detail));
        when(bankClient.submitPayment(eq("BATCH-01"), eq(new BigDecimal("1200.00")), anyList())).thenReturn(response);

        int rows = service.submitBatch(9L, "tester");

        assertEquals(1, rows);
        verify(salaryDetailMapper).updatePayStatusByBatchId(9L, "1", "tester");
        verify(salaryBatchMapper).updateBatchPaid(9L, "5", "tester");
        verify(attendanceMonthlyMapper).updateSummaryStatusByScope(batch.getStatMonth(), batch.getDispatchEnterpriseId(),
            "4", "tester");
        verify(workerMessageService).notifySalaryBatchSubmitted(eq(batch), anyList(), eq("tester"));

        ArgumentCaptor<List<YgbSalaryPaymentItem>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(bankClient).submitPayment(eq("BATCH-01"), eq(new BigDecimal("1200.00")), itemsCaptor.capture());
        assertEquals(1, itemsCaptor.getValue().size());
        assertEquals("6222020000000001", itemsCaptor.getValue().get(0).getBankCardNo());

        verify(salaryBatchMapper).updateBatchAmounts(9L, 1, new BigDecimal("1200.00"), new BigDecimal("0.00"), "5",
            "tester");
    }

    @Test
    void handleBankCallbackGeneratesDefaultSuccessResultsAndClosesBatch()
    {
        YgbSalaryBatch batch = batch(9L, "BATCH-01", "5", new BigDecimal("2000.00"));
        YgbSalaryBatch paidBatch = batch(9L, "BATCH-01", "6", new BigDecimal("2000.00"));
        YgbSalaryDetail processingDetail = detail(100L, 9L, "6222020000000001", "1", new BigDecimal("1200.00"));
        YgbSalaryDetail paidDetail = detail(100L, 9L, "6222020000000001", "2", new BigDecimal("1200.00"));

        when(salaryBatchMapper.selectSalaryBatchByBatchNo("BATCH-01")).thenReturn(batch);
        when(salaryDetailMapper.selectSalaryDetailList(any(YgbSalaryDetail.class)))
            .thenReturn(List.of(processingDetail), List.of(paidDetail));
        when(salaryDetailMapper.updatePayResultByBatchAndCardNo(9L, "6222020000000001", "2", "", "tester"))
            .thenReturn(1);
        when(salaryBatchMapper.selectSalaryBatchById(9L)).thenReturn(paidBatch);

        YgbBankCallbackRequest request = new YgbBankCallbackRequest();
        request.setBatchNo("BATCH-01");

        Map<String, Object> result = service.handleBankCallback(request, "tester");

        assertEquals(1, result.get("successCount"));
        assertEquals(0, result.get("failCount"));
        assertEquals("6", result.get("batchStatus"));
        verify(salaryBatchMapper).updateBatchPaid(9L, "6", "tester");
        verify(salaryBatchMapper).updateBatchAmounts(9L, 1, new BigDecimal("1200.00"), new BigDecimal("1200.00"),
            "6", "tester");
        assertTrue(((Integer) result.get("unmatchedCount")) == 0);
    }

    private YgbSalaryBatch batch(Long batchId, String batchNo, String batchStatus, BigDecimal accountReceivedAmount)
    {
        YgbSalaryBatch batch = new YgbSalaryBatch();
        batch.setBatchId(batchId);
        batch.setBatchNo(batchNo);
        batch.setBatchStatus(batchStatus);
        batch.setStatMonth("2026-05");
        batch.setDispatchEnterpriseId(10L);
        batch.setAccountReceivedAmount(accountReceivedAmount);
        return batch;
    }

    private YgbSalaryDetail detail(Long detailId, Long batchId, String bankCardNo, String payStatus,
        BigDecimal netAmount)
    {
        YgbSalaryDetail detail = new YgbSalaryDetail();
        detail.setDetailId(detailId);
        detail.setBatchId(batchId);
        detail.setBankAccountNo(bankCardNo);
        detail.setPayStatus(payStatus);
        detail.setPayableAmount(netAmount);
        detail.setNetAmount(netAmount);
        detail.setAttCheck("1");
        detail.setPersonName("P" + detailId);
        return detail;
    }
}
