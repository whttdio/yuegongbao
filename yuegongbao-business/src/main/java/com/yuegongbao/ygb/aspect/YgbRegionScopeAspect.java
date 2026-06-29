package com.yuegongbao.ygb.aspect;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Aspect
@Component
public class YgbRegionScopeAspect
{
    private static final Map<String, String> REGION_COLUMNS = new HashMap<>();

    static
    {
        REGION_COLUMNS.put("YgbPerson", "p.region_code");
        REGION_COLUMNS.put("YgbEnterprise", "region_code");
        REGION_COLUMNS.put("YgbContract", "c.region_code");
        REGION_COLUMNS.put("YgbAttendanceRaw", "a.region_code");
        REGION_COLUMNS.put("YgbAttendanceMonthly", "m.region_code");
        REGION_COLUMNS.put("YgbSalaryBatch", "b.region_code");
        REGION_COLUMNS.put("YgbSalaryDetail", "d.region_code");
        REGION_COLUMNS.put("YgbSalaryArrears", "b.region_code");
        REGION_COLUMNS.put("YgbContractTemplate", "region_code");
        REGION_COLUMNS.put("YgbWarning", "w.region_code");
        REGION_COLUMNS.put("YgbHeightWorkReport", "e.region_code");
        REGION_COLUMNS.put("YgbStatReport", "r.region_code");
        REGION_COLUMNS.put("YgbDevice", "region_code");
        REGION_COLUMNS.put("YgbDeviceEvent", "region_code");
        REGION_COLUMNS.put("YgbInjuryEvent", "region_code");
        REGION_COLUMNS.put("YgbPreventionProject", "region_code");
        REGION_COLUMNS.put("YgbSocialPayment", "region_code");
        REGION_COLUMNS.put("YgbSocialBaseCompare", "region_code");
        REGION_COLUMNS.put("YgbTaxCompare", "region_code");
        REGION_COLUMNS.put("YgbUninsuredList", "region_code");
        REGION_COLUMNS.put("YgbEmploymentRatio", "region_code");
        REGION_COLUMNS.put("YgbFakeOutsourcingRecord", "region_code");
        REGION_COLUMNS.put("YgbCreditScore", "region_code");
        REGION_COLUMNS.put("YgbAqInsurance", "region_code");
        REGION_COLUMNS.put("YgbPreventionFund", "region_code");
        REGION_COLUMNS.put("YgbOccupationMonitor", "region_code");
        REGION_COLUMNS.put("YgbAiReport", "region_code");
        REGION_COLUMNS.put("YgbAiReportTask", "region_code");
        REGION_COLUMNS.put("YgbAiReportSubscription", "region_code");
        REGION_COLUMNS.put("YgbCockpitConfig", "region_code");
        REGION_COLUMNS.put("YgbModuleRecord", "region_code");
        REGION_COLUMNS.put("YgbNewformWorker", "region_code");
    }

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Before("execution(* com.yuegongbao.ygb..service.impl.*ServiceImpl.select*List(..))"
        + " || execution(* com.yuegongbao.ygb..service.impl.*ServiceImpl.select*Summary(..))")
    public void applyRegionScope(JoinPoint joinPoint)
    {
        if (joinPoint.getArgs() == null || joinPoint.getArgs().length == 0)
        {
            return;
        }
        Object query = joinPoint.getArgs()[0];
        if (query instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) query;
            if (enterpriseScopeHelper.isEnterpriseScopedUser()
                && YgbEnterpriseScopeAspect.hasEnterpriseScopeBinding(baseEntity.getClass().getSimpleName()))
            {
                return;
            }
            regionScopeHelper.applyRegionDataScope(baseEntity, resolveRegionColumn(baseEntity));
        }
    }

    private String resolveRegionColumn(BaseEntity query)
    {
        return REGION_COLUMNS.getOrDefault(query.getClass().getSimpleName(), "region_code");
    }
}
