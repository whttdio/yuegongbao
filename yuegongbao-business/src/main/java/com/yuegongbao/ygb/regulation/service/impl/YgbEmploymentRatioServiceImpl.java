package com.yuegongbao.ygb.regulation.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatioSummary;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.domain.vo.YgbWarningCreateRequest;
import com.yuegongbao.ygb.compliance.mapper.YgbContractMapper;
import com.yuegongbao.ygb.regulation.mapper.YgbEmploymentRatioMapper;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.regulation.service.IYgbEmploymentRatioService;
import com.yuegongbao.ygb.util.YgbRiskCalculator;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbEmploymentRatioServiceImpl implements IYgbEmploymentRatioService
{
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    @Autowired
    private YgbEmploymentRatioMapper employmentRatioMapper;

    @Autowired
    private YgbContractMapper contractMapper;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private IYgbWarningService warningService;

    @Override
    public List<YgbEmploymentRatio> selectEmploymentRatioList(YgbEmploymentRatio employmentRatio)
    {
        return employmentRatioMapper.selectEmploymentRatioList(employmentRatio);
    }

    @Override
    public YgbEmploymentRatioSummary selectEmploymentRatioSummary(YgbEmploymentRatio employmentRatio)
    {
        List<YgbEmploymentRatio> list = selectEmploymentRatioList(employmentRatio);
        YgbEmploymentRatioSummary summary = new YgbEmploymentRatioSummary();
        summary.setTotalCount(list.size());

        int normalCount = 0;
        int yellowCount = 0;
        int redCount = 0;
        int warnedCount = 0;
        for (YgbEmploymentRatio item : list)
        {
            if ("0".equals(item.getWarningLevel()))
            {
                normalCount++;
            }
            else if ("1".equals(item.getWarningLevel()))
            {
                yellowCount++;
            }
            else if ("2".equals(item.getWarningLevel()))
            {
                redCount++;
            }
            if ("1".equals(item.getWarningStatus()))
            {
                warnedCount++;
            }
        }

        summary.setNormalCount(normalCount);
        summary.setYellowCount(yellowCount);
        summary.setRedCount(redCount);
        summary.setWarnedCount(warnedCount);
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int calculate(String statMonth, Long employerEnterpriseId, String operator)
    {
        validateMonth(statMonth);
        YearMonth yearMonth = YearMonth.parse(statMonth);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        YgbContract query = new YgbContract();
        query.setEmployerEnterpriseId(employerEnterpriseId);
        List<YgbContract> contractList = contractMapper.selectContractList(query);

        Map<Long, YgbPerson> personMap = new HashMap<>();
        for (YgbPerson person : personMapper.selectPersonList(new YgbPerson()))
        {
            personMap.put(person.getPersonId(), person);
        }

        Map<Long, YgbEmploymentRatio> resultMap = new HashMap<>();
        for (YgbContract contract : contractList)
        {
            if (!isActiveInMonth(contract, monthStart, monthEnd))
            {
                continue;
            }

            YgbPerson person = personMap.get(contract.getPersonId());
            if (person == null)
            {
                continue;
            }

            YgbEmploymentRatio ratio = resultMap.computeIfAbsent(contract.getEmployerEnterpriseId(), key -> {
                YgbEmploymentRatio record = new YgbEmploymentRatio();
                record.setStatMonth(statMonth);
                record.setEmployerEnterpriseId(contract.getEmployerEnterpriseId());
                record.setEmployerEnterpriseName(contract.getEmployerEnterpriseName());
                record.setRegionCode(contract.getRegionCode());
                record.setDispatchCount(0);
                record.setFormalCount(0);
                return record;
            });

            if ("1".equals(person.getWorkerType()))
            {
                ratio.setDispatchCount(ratio.getDispatchCount() + 1);
            }
            else if ("2".equals(person.getWorkerType()))
            {
                ratio.setFormalCount(ratio.getFormalCount() + 1);
            }
        }

        employmentRatioMapper.deleteByScope(statMonth, employerEnterpriseId);
        int rows = 0;
        for (YgbEmploymentRatio ratio : resultMap.values())
        {
            ratio.setRatioValue(YgbRiskCalculator.calculateEmploymentRatio(ratio.getDispatchCount(),
                ratio.getFormalCount()));
            ratio.setWarningLevel(YgbRiskCalculator.calculateEmploymentWarningLevel(ratio.getRatioValue()));
            ratio.setWarningStatus("0".equals(ratio.getWarningLevel()) ? "0" : "1");
            ratio.setRemark("派遣工与正式工比例按月统计。");
            ratio.setCreateBy(operator);
            employmentRatioMapper.insertEmploymentRatio(ratio);
            rows++;

            if (!"0".equals(ratio.getWarningLevel()))
            {
                YgbWarningCreateRequest warning = new YgbWarningCreateRequest();
                warning.setWarnLevel("2".equals(ratio.getWarningLevel()) ? "3" : "2");
                warning.setWarnType("EMPLOYMENT_RATIO");
                warning.setSourceModule("SPECIAL");
                warning.setTargetObjectId(ratio.getEmployerEnterpriseId());
                warning.setTargetType("1");
                warning.setEnterpriseId(ratio.getEmployerEnterpriseId());
                warning.setEnterpriseName(ratio.getEmployerEnterpriseName());
                warning.setRegionCode(ratio.getRegionCode());
                warning.setContent("用工比例预警，当前派遣比例：" + ratio.getRatioValue() + "%");
                warningService.createWarningIfAbsent(warning, operator);
            }
        }
        return rows;
    }

    private boolean isActiveInMonth(YgbContract contract, LocalDate monthStart, LocalDate monthEnd)
    {
        LocalDate start = contract.getStartDate() == null ? null
            : contract.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = contract.getEndDate() == null ? null
            : contract.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean afterStart = start == null || !start.isAfter(monthEnd);
        boolean beforeEnd = end == null || !end.isBefore(monthStart);
        return afterStart && beforeEnd;
    }

    private void validateMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth) || !MONTH_PATTERN.matcher(statMonth).matches())
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM。");
        }
    }
}
