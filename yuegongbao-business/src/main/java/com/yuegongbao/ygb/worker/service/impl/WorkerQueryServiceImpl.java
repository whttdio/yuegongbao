package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryBatchMapper;
import com.yuegongbao.ygb.compliance.mapper.YgbSalaryDetailMapper;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.regulation.mapper.YgbSocialPaymentMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbTaxCompareMapper;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.service.WorkerQueryService;
import com.yuegongbao.ygb.worker.service.WorkerTrainingService;

@Service
public class WorkerQueryServiceImpl implements WorkerQueryService
{
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");

    @Autowired
    private YgbSalaryDetailMapper salaryDetailMapper;

    @Autowired
    private YgbSalaryBatchMapper salaryBatchMapper;

    @Autowired
    private YgbSocialPaymentMapper socialPaymentMapper;

    @Autowired
    private YgbTaxCompareMapper taxCompareMapper;

    @Autowired
    private WorkerTrainingService workerTrainingService;

    @Override
    public Map<String, Object> getSalaryList(YgbPerson worker, String year)
    {
        WorkerTrainingProgress progress = workerTrainingService.getProgress(worker);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("locked", progress.isLockSalary());
        result.put("lockReason", progress.isLockSalary() ? buildTrainingLockReason(progress) : null);
        if (progress.isLockSalary())
        {
            result.put("months", new ArrayList<>());
            return result;
        }

        List<YgbSalaryDetail> rows = salaryDetailMapper.selectWorkerSalaryList(worker.getPersonId(), normalizeYear(year));
        List<Map<String, Object>> months = new ArrayList<>();
        for (YgbSalaryDetail item : rows)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("salaryMonth", item.getStatMonth());
            row.put("payStatus", item.getPayStatus());
            row.put("payStatusText", resolveSalaryStatusText(item.getPayStatus()));
            row.put("realAmount", defaultAmount(item.getNetAmount()));
            row.put("shouldAmount", defaultAmount(item.getPayableAmount()));
            row.put("deductionAmount", defaultAmount(item.getDeductionAmount()));
            row.put("attendanceDays", item.getAttendanceDays());
            months.add(row);
        }
        result.put("months", months);
        return result;
    }

    @Override
    public Map<String, Object> getSalaryDetail(YgbPerson worker, String month)
    {
        WorkerTrainingProgress progress = workerTrainingService.getProgress(worker);
        if (progress.isLockSalary())
        {
            throw new ServiceException(buildTrainingLockReason(progress));
        }

        String statMonth = normalizeMonth(month);
        YgbSalaryDetail item = salaryDetailMapper.selectWorkerSalaryDetail(worker.getPersonId(), statMonth);
        if (item == null)
        {
            throw new ServiceException("未找到该月份工资记录。");
        }
        YgbSalaryBatch batch = item.getBatchId() == null ? null : salaryBatchMapper.selectSalaryBatchById(item.getBatchId());

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("salaryMonth", item.getStatMonth());
        detail.put("enterpriseName", firstNonBlank(item.getEmployerEnterpriseName(), item.getDispatchEnterpriseName(),
            worker.getEnterpriseName()));
        detail.put("shouldAmount", defaultAmount(item.getPayableAmount()));
        detail.put("realAmount", defaultAmount(item.getNetAmount()));
        detail.put("baseAmount", defaultAmount(item.getPayableAmount()));
        detail.put("overtimeAmount", BigDecimal.ZERO);
        detail.put("allowanceAmount", BigDecimal.ZERO);
        detail.put("deductionAmount", defaultAmount(item.getDeductionAmount()));
        detail.put("socialSecurityAmount", BigDecimal.ZERO);
        detail.put("taxAmount", BigDecimal.ZERO);
        detail.put("attendanceDays", item.getAttendanceDays());
        detail.put("totalHours", item.getTotalHours());
        detail.put("contractNo", item.getContractNo());
        detail.put("batchNo", item.getBatchNo());
        detail.put("bankAccountName", firstNonBlank(item.getBankAccountName(), worker.getPersonName()));
        detail.put("bankAccountNo", item.getBankAccountNo());
        detail.put("bankAccountNoMasked", maskBankAccountNo(item.getBankAccountNo()));
        detail.put("bankSerialNo", batch == null ? null : batch.getBankSerialNo());
        detail.put("paidTime", batch == null ? null : batch.getPaidTime());
        detail.put("payStatus", item.getPayStatus());
        detail.put("payStatusText", resolveSalaryStatusText(item.getPayStatus()));
        detail.put("failReason", item.getFailReason());
        detail.put("deductionItems", buildDeductionItems(item));
        detail.put("remark", item.getRemark());
        return detail;
    }

    @Override
    public Map<String, Object> getSocialSecurityList(YgbPerson worker, String year)
    {
        String queryYear = normalizeYear(year);
        List<YgbSocialPayment> rows = socialPaymentMapper.selectWorkerSocialPaymentList(worker.getPersonId(), queryYear);
        List<Map<String, Object>> records = new ArrayList<>();
        BigDecimal cumulativeBaseAmount = BigDecimal.ZERO;
        BigDecimal cumulativePersonalAmount = BigDecimal.ZERO;
        BigDecimal cumulativeCompanyAmount = BigDecimal.ZERO;
        BigDecimal cumulativePaidAmount = BigDecimal.ZERO;
        int normalMonths = 0;
        int arrearsMonths = 0;
        for (YgbSocialPayment item : rows)
        {
            SocialAmountSplit split = resolveSocialAmountSplit(item);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("insuredMonth", item.getStatMonth());
            row.put("insuredStatus", item.getPaymentStatus());
            row.put("insuredStatusText", resolveSocialStatusText(item.getPaymentStatus()));
            row.put("personalAmount", split.getPersonalAmount());
            row.put("personalAmountText", split.getPersonalAmount() == null ? split.getExplainText() : null);
            row.put("companyAmount", split.getCompanyAmount());
            row.put("paidAmount", split.getPaidAmount());
            row.put("baseAmount", split.getBaseAmount());
            row.put("sourceStatus", item.getSourceStatus());
            row.put("sourceStatusText", resolveSourceStatusText(item.getSourceStatus()));
            records.add(row);

            cumulativeBaseAmount = cumulativeBaseAmount.add(split.getBaseAmount());
            cumulativePersonalAmount = cumulativePersonalAmount.add(defaultAmount(split.getPersonalAmount()));
            cumulativeCompanyAmount = cumulativeCompanyAmount.add(defaultAmount(split.getCompanyAmount()));
            cumulativePaidAmount = cumulativePaidAmount.add(split.getPaidAmount());
            if ("1".equals(item.getPaymentStatus()))
            {
                normalMonths++;
            }
            if ("2".equals(item.getPaymentStatus()))
            {
                arrearsMonths++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("year", queryYear);
        summary.put("totalMonths", rows.size());
        summary.put("normalMonths", normalMonths);
        summary.put("arrearsMonths", arrearsMonths);
        summary.put("cumulativeBaseAmount", cumulativeBaseAmount);
        summary.put("cumulativePersonalAmount", cumulativePersonalAmount);
        summary.put("cumulativeCompanyAmount", cumulativeCompanyAmount);
        summary.put("cumulativePaidAmount", cumulativePaidAmount);
        summary.put("amountHint", "优先展示来源已回写的个人、单位和总额拆分；若来源仅回写基数或总额，页面会明确提示未同步项，不再按固定比例推导。");
        result.put("summary", summary);
        result.put("records", records);
        return result;
    }

    @Override
    public Map<String, Object> getSocialSecurityDetail(YgbPerson worker, String month)
    {
        YgbSocialPayment item = socialPaymentMapper.selectWorkerSocialPaymentDetail(worker.getPersonId(), normalizeMonth(month));
        if (item == null)
        {
            throw new ServiceException("未找到该月份社保记录。");
        }

        SocialAmountSplit split = resolveSocialAmountSplit(item);
        List<Map<String, Object>> insuranceItems = new ArrayList<>();
        insuranceItems.add(insuranceItem("社保缴费", split.getBaseAmount(), split.getPersonalAmount(),
            split.getCompanyAmount(), split.getPaidAmount(), split.getExplainText()));

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("insuredMonth", item.getStatMonth());
        detail.put("enterpriseName", firstNonBlank(item.getEnterpriseName(), worker.getEnterpriseName()));
        detail.put("insuranceItems", insuranceItems);
        detail.put("status", item.getPaymentStatus());
        detail.put("statusText", resolveSocialStatusText(item.getPaymentStatus()));
        detail.put("sourceSerialNo", item.getSourceSerialNo());
        detail.put("sourceStatus", item.getSourceStatus());
        detail.put("sourceStatusText", resolveSourceStatusText(item.getSourceStatus()));
        detail.put("callbackTime", item.getCallbackTime());
        detail.put("amountHint", "当前优先展示来源已回写的金额拆分；若来源未提供个人或单位缴费明细，页面将按未同步处理。");
        detail.put("remark", firstNonBlank(item.getRemark(), item.getSourceMessage()));
        return detail;
    }

    @Override
    public Map<String, Object> getTaxList(YgbPerson worker, String year)
    {
        String queryYear = normalizeYear(year);
        List<YgbTaxCompare> rows = taxCompareMapper.selectWorkerTaxCompareList(worker.getPersonId(), queryYear);
        List<Map<String, Object>> records = new ArrayList<>();
        BigDecimal cumulativeIncome = BigDecimal.ZERO;
        BigDecimal cumulativeSalary = BigDecimal.ZERO;
        int abnormalMonths = 0;
        int warningMonths = 0;
        for (YgbTaxCompare item : rows)
        {
            TaxAmountSnapshot amountSnapshot = resolveTaxAmountSnapshot(item);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("taxMonth", item.getStatMonth());
            row.put("incomeAmount", amountSnapshot.getIncomeAmount());
            row.put("taxAmount", amountSnapshot.getTaxAmount());
            row.put("taxAmountText", amountSnapshot.getTaxAmountText());
            row.put("declareStatus", item.getCompareResult());
            row.put("declareStatusText", resolveTaxStatusText(item.getCompareResult()));
            row.put("salaryAmount", defaultAmount(item.getSalaryAmount()));
            row.put("warningStatus", item.getWarningStatus());
            row.put("warningStatusText", resolveWarningStatusText(item.getWarningStatus()));
            row.put("diffRatio", defaultAmount(item.getDiffRatio()));
            row.put("amountHint", amountSnapshot.getAmountHint());
            records.add(row);

            cumulativeIncome = cumulativeIncome.add(defaultAmount(amountSnapshot.getIncomeAmount()));
            cumulativeSalary = cumulativeSalary.add(defaultAmount(item.getSalaryAmount()));
            if ("2".equals(item.getCompareResult()))
            {
                abnormalMonths++;
            }
            if ("1".equals(item.getWarningStatus()))
            {
                warningMonths++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("year", queryYear);
        summary.put("cumulativeIncome", cumulativeIncome);
        summary.put("cumulativeTax", null);
        summary.put("cumulativeTaxText", "来源暂未回写累计实缴税额");
        summary.put("cumulativeSalary", cumulativeSalary);
        summary.put("totalMonths", rows.size());
        summary.put("abnormalMonths", abnormalMonths);
        summary.put("warningMonths", warningMonths);
        summary.put("amountHint", "优先展示来源已回写的申报收入和实缴税额；若来源只返回申报收入，税额将显示未同步。");
        result.put("summary", summary);
        result.put("records", records);
        return result;
    }

    @Override
    public Map<String, Object> getTaxDetail(YgbPerson worker, String month)
    {
        YgbTaxCompare item = taxCompareMapper.selectWorkerTaxCompareDetail(worker.getPersonId(), normalizeMonth(month));
        if (item == null)
        {
            throw new ServiceException("未找到该月份个税记录。");
        }
        List<YgbTaxCompare> yearRows = taxCompareMapper.selectWorkerTaxCompareList(worker.getPersonId(), item.getStatMonth().substring(0, 4));
        BigDecimal cumulativeIncome = BigDecimal.ZERO;
        BigDecimal cumulativeSalary = BigDecimal.ZERO;
        for (YgbTaxCompare yearRow : yearRows)
        {
            if (StringUtils.isEmpty(yearRow.getStatMonth()) || yearRow.getStatMonth().compareTo(item.getStatMonth()) > 0)
            {
                continue;
            }
            TaxAmountSnapshot yearAmountSnapshot = resolveTaxAmountSnapshot(yearRow);
            cumulativeIncome = cumulativeIncome.add(defaultAmount(yearAmountSnapshot.getIncomeAmount()));
            cumulativeSalary = cumulativeSalary.add(defaultAmount(yearRow.getSalaryAmount()));
        }
        TaxAmountSnapshot amountSnapshot = resolveTaxAmountSnapshot(item);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("taxMonth", item.getStatMonth());
        detail.put("incomeType", "工资薪金");
        detail.put("incomeAmount", amountSnapshot.getIncomeAmount());
        detail.put("taxableAmount", amountSnapshot.getTaxableAmount());
        detail.put("taxAmount", amountSnapshot.getTaxAmount());
        detail.put("taxAmountText", amountSnapshot.getTaxAmountText());
        detail.put("cumulativeIncome", cumulativeIncome);
        detail.put("cumulativeTax", null);
        detail.put("cumulativeTaxText", "来源暂未回写累计实缴税额");
        detail.put("cumulativeSalary", cumulativeSalary);
        detail.put("salaryAmount", defaultAmount(item.getSalaryAmount()));
        detail.put("diffRatio", defaultAmount(item.getDiffRatio()));
        detail.put("compareResult", item.getCompareResult());
        detail.put("compareResultText", resolveTaxStatusText(item.getCompareResult()));
        detail.put("warningStatus", item.getWarningStatus());
        detail.put("warningStatusText", resolveWarningStatusText(item.getWarningStatus()));
        detail.put("sourceSerialNo", item.getSourceSerialNo());
        detail.put("sourceStatus", item.getSourceStatus());
        detail.put("sourceStatusText", resolveSourceStatusText(item.getSourceStatus()));
        detail.put("callbackTime", item.getCallbackTime());
        detail.put("amountHint", amountSnapshot.getAmountHint());
        detail.put("remark", firstNonBlank(item.getRemark(), item.getSourceMessage()));
        return detail;
    }

    private Map<String, Object> insuranceItem(String itemName, BigDecimal baseAmount, BigDecimal personalAmount,
        BigDecimal companyAmount, BigDecimal paidAmount, String explainText)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("itemName", itemName);
        item.put("baseAmount", baseAmount);
        item.put("personalAmount", personalAmount);
        item.put("personalAmountText", personalAmount == null ? explainText : null);
        item.put("companyAmount", companyAmount);
        item.put("paidAmount", paidAmount);
        return item;
    }

    private SocialAmountSplit resolveSocialAmountSplit(YgbSocialPayment item)
    {
        JSONObject payload = parseRawPayload(item.getRawPayload());
        BigDecimal baseAmount = firstPositiveAmount(payload, "baseAmount", item.getBaseAmount());
        BigDecimal personalAmount = firstPositiveAmount(payload, "personalAmount", null);
        BigDecimal companyAmount = firstPositiveAmount(payload, "companyAmount", null);
        BigDecimal paidAmount = firstPositiveAmount(payload, "paidAmount", item.getPaidAmount());
        String explainText = null;

        if (personalAmount != null && companyAmount != null)
        {
            if (paidAmount == null || BigDecimal.ZERO.compareTo(paidAmount) >= 0)
            {
                paidAmount = personalAmount.add(companyAmount).setScale(2, RoundingMode.HALF_UP);
            }
            return new SocialAmountSplit(defaultAmount(baseAmount), personalAmount, companyAmount, defaultAmount(paidAmount),
                "来源已回写拆分金额");
        }

        if (paidAmount != null)
        {
            explainText = "来源仅回写缴费总额，个人/单位拆分暂未同步";
            return new SocialAmountSplit(defaultAmount(baseAmount), null, null, defaultAmount(paidAmount), explainText);
        }

        return new SocialAmountSplit(defaultAmount(baseAmount), null, null, null, "来源暂未回写缴费金额拆分");
    }

    private JSONObject parseRawPayload(String rawPayload)
    {
        if (StringUtils.isEmpty(rawPayload))
        {
            return null;
        }
        try
        {
            return JSON.parseObject(rawPayload);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private BigDecimal firstPositiveAmount(JSONObject payload, String key, BigDecimal fallback)
    {
        BigDecimal amount = jsonAmount(payload, key);
        if (amount != null)
        {
            return amount;
        }
        return fallback;
    }

    private BigDecimal jsonAmount(JSONObject payload, String key)
    {
        if (payload == null || !payload.containsKey(key))
        {
            return null;
        }
        Object value = payload.get(key);
        if (value == null)
        {
            return null;
        }
        try
        {
            return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private TaxAmountSnapshot resolveTaxAmountSnapshot(YgbTaxCompare item)
    {
        JSONObject payload = parseRawPayload(item == null ? null : item.getRawPayload());
        BigDecimal incomeAmount = firstPositiveAmount(payload, "declaredAmount",
            firstPositiveAmount(payload, "declared", item == null ? null : item.getDeclaredAmount()));
        BigDecimal taxableAmount = firstPositiveAmount(payload, "taxableAmount",
            firstPositiveAmount(payload, "taxableIncome", incomeAmount));
        BigDecimal taxAmount = firstPositiveAmount(payload, "taxAmount",
            firstPositiveAmount(payload, "paidTax", firstPositiveAmount(payload, "withholdingTax", null)));

        String taxAmountText = taxAmount == null ? "来源暂未回写实缴税额" : null;
        String amountHint = taxAmount == null
            ? "当前仅展示来源已回写的申报收入，实缴税额暂未同步。"
            : "当前展示来源已回写的申报收入与实缴税额。";
        return new TaxAmountSnapshot(defaultAmount(incomeAmount), defaultAmount(taxableAmount), taxAmount, taxAmountText, amountHint);
    }

    private List<Map<String, Object>> buildDeductionItems(YgbSalaryDetail item)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (defaultAmount(item.getDeductionAmount()).compareTo(BigDecimal.ZERO) > 0)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("itemName", "工资扣减");
            row.put("amount", defaultAmount(item.getDeductionAmount()));
            row.put("summary", firstNonBlank(item.getRemark(), "本月存在工资扣减，请结合企业说明核对。"));
            rows.add(row);
        }
        return rows;
    }

    private String normalizeYear(String year)
    {
        if (StringUtils.isNotEmpty(year))
        {
            return year;
        }
        return LocalDate.now().format(YEAR_FORMATTER);
    }

    private String normalizeMonth(String month)
    {
        if (StringUtils.isEmpty(month))
        {
            return LocalDate.now().withDayOfMonth(1).toString().substring(0, 7);
        }
        return month;
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String buildTrainingLockReason(WorkerTrainingProgress progress)
    {
        return "您本月安全培训未完成（" + progress.getCompleted() + "/" + progress.getTotal() + "），请先完成培训";
    }

    private String resolveSalaryStatusText(String status)
    {
        if ("2".equals(status))
        {
            return "已发放";
        }
        if ("1".equals(status))
        {
            return "发放中";
        }
        if ("3".equals(status))
        {
            return "发放失败";
        }
        return "待发放";
    }

    private String resolveSocialStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "正常";
        }
        if ("2".equals(status))
        {
            return "欠费";
        }
        return "未缴费";
    }

    private String resolveTaxStatusText(String status)
    {
        return "1".equals(status) ? "正常" : "异常";
    }

    private String resolveSourceStatusText(String status)
    {
        if ("1".equals(status))
        {
            return "同步成功";
        }
        if ("2".equals(status))
        {
            return "同步失败";
        }
        if ("3".equals(status))
        {
            return "处理中";
        }
        return "待同步";
    }

    private String resolveWarningStatusText(String status)
    {
        return "1".equals(status) ? "已预警" : "未预警";
    }

    private String maskBankAccountNo(String bankAccountNo)
    {
        if (StringUtils.isEmpty(bankAccountNo) || bankAccountNo.length() < 8)
        {
            return bankAccountNo;
        }
        return bankAccountNo.substring(0, 4) + " **** **** " + bankAccountNo.substring(bankAccountNo.length() - 4);
    }

    private String firstNonBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value))
            {
                return value;
            }
        }
        return null;
    }

    private static class SocialAmountSplit
    {
        private final BigDecimal baseAmount;

        private final BigDecimal personalAmount;

        private final BigDecimal companyAmount;

        private final BigDecimal paidAmount;

        private final String explainText;

        private SocialAmountSplit(BigDecimal baseAmount, BigDecimal personalAmount, BigDecimal companyAmount,
            BigDecimal paidAmount, String explainText)
        {
            this.baseAmount = baseAmount;
            this.personalAmount = personalAmount;
            this.companyAmount = companyAmount;
            this.paidAmount = paidAmount;
            this.explainText = explainText;
        }

        public BigDecimal getBaseAmount()
        {
            return baseAmount;
        }

        public BigDecimal getPersonalAmount()
        {
            return personalAmount;
        }

        public BigDecimal getCompanyAmount()
        {
            return companyAmount;
        }

        public BigDecimal getPaidAmount()
        {
            return paidAmount;
        }

        public String getExplainText()
        {
            return explainText;
        }
    }

    private static class TaxAmountSnapshot
    {
        private final BigDecimal incomeAmount;

        private final BigDecimal taxableAmount;

        private final BigDecimal taxAmount;

        private final String taxAmountText;

        private final String amountHint;

        private TaxAmountSnapshot(BigDecimal incomeAmount, BigDecimal taxableAmount, BigDecimal taxAmount,
            String taxAmountText, String amountHint)
        {
            this.incomeAmount = incomeAmount;
            this.taxableAmount = taxableAmount;
            this.taxAmount = taxAmount;
            this.taxAmountText = taxAmountText;
            this.amountHint = amountHint;
        }

        public BigDecimal getIncomeAmount()
        {
            return incomeAmount;
        }

        public BigDecimal getTaxableAmount()
        {
            return taxableAmount;
        }

        public BigDecimal getTaxAmount()
        {
            return taxAmount;
        }

        public String getTaxAmountText()
        {
            return taxAmountText;
        }

        public String getAmountHint()
        {
            return amountHint;
        }
    }
}
