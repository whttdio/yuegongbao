package com.yuegongbao.ygb.aspect;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Aspect
@Component
public class YgbRegionScopeAspect
{
    private static final Map<String, String> REGION_COLUMNS = new HashMap<>();

    static
    {
        REGION_COLUMNS.put("YgbPerson", "p.region_code");
        REGION_COLUMNS.put("YgbContract", "c.region_code");
        REGION_COLUMNS.put("YgbAttendanceRaw", "a.region_code");
        REGION_COLUMNS.put("YgbAttendanceMonthly", "m.region_code");
        REGION_COLUMNS.put("YgbSalaryBatch", "b.region_code");
        REGION_COLUMNS.put("YgbSalaryDetail", "d.region_code");
        REGION_COLUMNS.put("YgbWarning", "w.region_code");
        REGION_COLUMNS.put("YgbHeightWorkReport", "e.region_code");
        REGION_COLUMNS.put("YgbStatReport", "r.region_code");
    }

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

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
            regionScopeHelper.applyRegionDataScope(baseEntity, resolveRegionColumn(baseEntity));
        }
    }

    private String resolveRegionColumn(BaseEntity query)
    {
        return REGION_COLUMNS.getOrDefault(query.getClass().getSimpleName(), "region_code");
    }
}
