package com.yuegongbao.framework.aspectj;

import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.yuegongbao.common.annotation.DataScope;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.common.core.domain.entity.SysRole;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.core.text.Convert;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.framework.security.context.PermissionContextHolder;

@Aspect
@Component
public class DataScopeAspect
{
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser))
        {
            return;
        }

        SysUser currentUser = loginUser.getUser();
        if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
        {
            String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), PermissionContextHolder.getContext());
            dataScopeFilter(joinPoint, currentUser, controllerDataScope.userAlias(), controllerDataScope.deptAlias(),
                controllerDataScope.userField(), controllerDataScope.deptField(), permission);
        }
    }

    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String userAlias, String deptAlias,
        String userField, String deptField, String permission)
    {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<String>();
        List<String> scopeCustomIds = new ArrayList<String>();
        user.getRoles().forEach(role -> {
            if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(role.getDataScope()) && StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL)
                && (StringUtils.isEmpty(permission) || StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission))))
            {
                scopeCustomIds.add(Convert.toStr(role.getRoleId()));
            }
        });

        for (SysRole role : user.getRoles())
        {
            String dataScope = role.getDataScope();
            if (conditions.contains(dataScope) || StringUtils.equals(role.getStatus(), UserConstants.ROLE_DISABLE))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(permission) && !StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission)))
            {
                continue;
            }
            if (Constants.Dept.DATA_SCOPE_ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            }
            else if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                if (scopeCustomIds.size() > 1)
                {
                    sqlString.append(StringUtils.format(" OR {}.{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ",
                        deptAlias, deptField, String.join(",", scopeCustomIds)));
                }
                else
                {
                    sqlString.append(StringUtils.format(" OR {}.{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ",
                        deptAlias, deptField, role.getRoleId()));
                }
            }
            else if (Constants.Dept.DATA_SCOPE_DEPT.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.{} = {} ", deptAlias, deptField, user.getDeptId()));
            }
            else if (Constants.Dept.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.{} IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                    deptAlias, deptField, user.getDeptId(), user.getDeptId()));
            }
            else if (Constants.Dept.DATA_SCOPE_SELF.equals(dataScope))
            {
                if (StringUtils.isNotBlank(userAlias))
                {
                    sqlString.append(StringUtils.format(" OR {}.{} = {} ", userAlias, userField, user.getUserId()));
                }
                else
                {
                    sqlString.append(StringUtils.format(" OR {}.{} = 0 ", deptAlias, deptField));
                }
            }
            conditions.add(dataScope);
        }

        if (StringUtils.isEmpty(conditions))
        {
            sqlString.append(StringUtils.format(" OR {}.{} = 0 ", deptAlias, deptField));
        }

        if (StringUtils.isNotBlank(sqlString.toString()))
        {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}
