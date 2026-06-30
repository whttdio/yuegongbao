package com.yuegongbao.ygb.aspect;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.ygb.util.EnterpriseScopeMode;
import com.yuegongbao.ygb.util.YgbEnterpriseScopeHelper;

@Aspect
@Component
public class YgbEnterpriseScopeAspect
{
    private static final Map<String, EnterpriseScopeBinding> ENTERPRISE_COLUMNS = new HashMap<>();

    static
    {
        bind("YgbPerson", EnterpriseScopeMode.SINGLE, "p.enterprise_id");
        bind("YgbEnterprise", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbContract", EnterpriseScopeMode.DUAL_OR, "c.dispatch_enterprise_id", "c.employer_enterprise_id");
        bind("YgbAttendanceRaw", EnterpriseScopeMode.DUAL_OR, "a.dispatch_enterprise_id", "a.employer_enterprise_id");
        bind("YgbAttendanceMonthly", EnterpriseScopeMode.DUAL_OR, "m.dispatch_enterprise_id", "m.employer_enterprise_id");
        bind("YgbSalaryDetail", EnterpriseScopeMode.DUAL_OR, "d.dispatch_enterprise_id", "d.employer_enterprise_id");
        bind("YgbSalaryBatch", EnterpriseScopeMode.SINGLE, "b.dispatch_enterprise_id");
        bind("YgbSalaryArrears", EnterpriseScopeMode.SINGLE, "b.dispatch_enterprise_id");
        bind("YgbDevice", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbDeviceEvent", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbInjuryEvent", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbPreventionProject", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbSocialPayment", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbSocialBaseCompare", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbTaxCompare", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbUninsuredList", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbEmploymentRatio", EnterpriseScopeMode.SINGLE, "employer_enterprise_id");
        bind("YgbFakeOutsourcingRecord", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbWarning", EnterpriseScopeMode.SINGLE, "w.enterprise_id");
        bind("YgbCreditScore", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbAqInsurance", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbPreventionFund", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbHeightWorkReport", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbModuleRecord", EnterpriseScopeMode.SINGLE, "enterprise_id");
        bind("YgbNewformWorker", EnterpriseScopeMode.SINGLE, "enterprise_id");
    }

    @Autowired
    private YgbEnterpriseScopeHelper enterpriseScopeHelper;

    @Before("execution(* com.yuegongbao.ygb..service.impl.*ServiceImpl.select*List(..))"
        + " || execution(* com.yuegongbao.ygb..service.impl.*ServiceImpl.select*Summary(..))")
    public void applyEnterpriseScope(JoinPoint joinPoint)
    {
        if (!enterpriseScopeHelper.isEnterpriseScopedUser())
        {
            return;
        }
        if (joinPoint.getArgs() == null || joinPoint.getArgs().length == 0)
        {
            return;
        }
        Object query = joinPoint.getArgs()[0];
        if (!(query instanceof BaseEntity))
        {
            return;
        }
        EnterpriseScopeBinding binding = ENTERPRISE_COLUMNS.get(query.getClass().getSimpleName());
        if (binding == null)
        {
            return;
        }
        enterpriseScopeHelper.applyEnterpriseDataScope((BaseEntity) query, binding.mode, binding.columns);
    }

    public static boolean hasEnterpriseScopeBinding(String entitySimpleName)
    {
        return ENTERPRISE_COLUMNS.containsKey(entitySimpleName);
    }

    private static void bind(String entityName, EnterpriseScopeMode mode, String... columns)
    {
        ENTERPRISE_COLUMNS.put(entityName, new EnterpriseScopeBinding(mode, columns));
    }

    private static final class EnterpriseScopeBinding
    {
        private final EnterpriseScopeMode mode;
        private final String[] columns;

        private EnterpriseScopeBinding(EnterpriseScopeMode mode, String... columns)
        {
            this.mode = mode;
            this.columns = columns;
        }
    }
}
