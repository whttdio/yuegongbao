package com.yuegongbao.ygb.techdefense.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.domain.vo.YgbCertCheckResult;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.integration.EmergencyCertClient;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportWorker;
import com.yuegongbao.ygb.techdefense.domain.vo.YgbHeightWorkFinishRequest;
import com.yuegongbao.ygb.techdefense.mapper.YgbHeightWorkReportMapper;
import com.yuegongbao.ygb.techdefense.mapper.YgbHeightWorkReportWorkerMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbHeightWorkReportServiceImplTest
{
    @Mock
    private YgbHeightWorkReportMapper reportMapper;

    @Mock
    private YgbHeightWorkReportWorkerMapper workerMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private EmergencyCertClient emergencyCertClient;

    @InjectMocks
    private YgbHeightWorkReportServiceImpl service;

    @Test
    void insertHeightWorkReportKeepsInvalidCertAsWarningOnly()
    {
        when(enterpriseMapper.selectEnterpriseById(1002L)).thenReturn(enterprise());
        when(emergencyCertClient.checkValidity("440305199001010019", "GC-001")).thenReturn(cert(false, "证书已过期"));
        doAnswer(invocation -> {
            YgbHeightWorkReport report = invocation.getArgument(0);
            report.setReportId(102001L);
            return 1;
        }).when(reportMapper).insertHeightWorkReport(any(YgbHeightWorkReport.class));

        int rows = service.insertHeightWorkReport(report("440305199001010019"), "tester");

        assertEquals(1, rows);
        ArgumentCaptor<YgbHeightWorkReport> reportCaptor = ArgumentCaptor.forClass(YgbHeightWorkReport.class);
        verify(reportMapper).insertHeightWorkReport(reportCaptor.capture());
        assertEquals("2", reportCaptor.getValue().getCertValidStatus());
        assertEquals(1, reportCaptor.getValue().getCertInvalidCount());
        verify(workerMapper).insertHeightWorkReportWorkers(org.mockito.ArgumentMatchers.eq(102001L), any());
    }

    @Test
    void updateFinishedReportIsRejected()
    {
        YgbHeightWorkReport finished = new YgbHeightWorkReport();
        finished.setReportId(102001L);
        finished.setReportStatus("1");
        when(reportMapper.selectHeightWorkReportById(102001L)).thenReturn(finished);

        ServiceException ex = assertThrows(ServiceException.class,
            () -> service.updateHeightWorkReport(report("440305199001010018"), "tester"));

        assertTrue(ex.getMessage().contains("已结束"));
        verify(reportMapper, never()).updateHeightWorkReport(any());
    }

    @Test
    void importRejectsBatchOverLimit()
    {
        List<YgbHeightWorkReport> reports = java.util.stream.IntStream.range(0, 101)
            .mapToObj(index -> report("440305199001010018"))
            .toList();

        ServiceException ex = assertThrows(ServiceException.class,
            () -> service.importHeightWorkReports(reports, "tester"));

        assertTrue(ex.getMessage().contains("最多100条"));
    }

    @Test
    void finishHeightWorkReportUpdatesStatus()
    {
        YgbHeightWorkReport origin = report("440305199001010018");
        origin.setReportId(102001L);
        origin.setReportStatus("0");
        when(reportMapper.selectHeightWorkReportById(102001L)).thenReturn(origin);

        YgbHeightWorkFinishRequest request = new YgbHeightWorkFinishRequest();
        request.setActualEndTime(new Date(origin.getEndTime().getTime() + 3600000L));
        request.setEndPhotoUrl("/profile/upload/finish.png");

        service.finishHeightWorkReport(102001L, request, "tester");

        ArgumentCaptor<YgbHeightWorkReport> captor = ArgumentCaptor.forClass(YgbHeightWorkReport.class);
        verify(reportMapper).finishHeightWorkReport(captor.capture());
        assertEquals("1", captor.getValue().getReportStatus());
    }

    private YgbHeightWorkReport report(String idCard)
    {
        YgbHeightWorkReport report = new YgbHeightWorkReport();
        report.setReportId(102001L);
        report.setEnterpriseId(1002L);
        report.setEnterpriseName("深圳鹏城机电工程有限公司");
        report.setReporterType("1");
        report.setApplicantName("李工");
        report.setApplicantPhone("13800000001");
        report.setWorkLocation("南山区科技园 18 号楼");
        report.setLongitude(new BigDecimal("113.952700"));
        report.setLatitude(new BigDecimal("22.540300"));
        report.setStartTime(new Date(1764566400000L));
        report.setEndTime(new Date(1764573600000L));
        report.setWorkHeightM(12);
        report.setGuardianName("张监护");
        report.setGuardianPhone("13800000002");
        report.setSafetyMeasures(List.of("安全带", "安全网", "监护人"));
        report.setWorkerList(List.of(worker(idCard)));
        return report;
    }

    private YgbHeightWorkReportWorker worker(String idCard)
    {
        YgbHeightWorkReportWorker worker = new YgbHeightWorkReportWorker();
        worker.setWorkerName("陈高空");
        worker.setIdCard(idCard);
        worker.setCertNo("GC-001");
        worker.setCertPhotoUrl("/profile/upload/cert.png");
        return worker;
    }

    private YgbEnterprise enterprise()
    {
        YgbEnterprise enterprise = new YgbEnterprise();
        enterprise.setEnterpriseId(1002L);
        enterprise.setEnterpriseName("深圳鹏城机电工程有限公司");
        enterprise.setRegionCode("440305");
        return enterprise;
    }

    private YgbCertCheckResult cert(boolean valid, String message)
    {
        YgbCertCheckResult result = new YgbCertCheckResult();
        result.setValid(valid);
        result.setCertStatus(valid ? "VALID" : "EXPIRED");
        result.setSourceMessage(message);
        return result;
    }
}
