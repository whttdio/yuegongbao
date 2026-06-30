package com.yuegongbao.ygb.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.common.core.domain.entity.SysRole;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;

@Component
public class YgbEnterpriseScopeHelper
{
    private static final String ENTERPRISE_DATA_SCOPE = "enterpriseDataScope";

    private static final Set<String> ENTERPRISE_ROLE_KEYS = new HashSet<>(Arrays.asList(
        "ygb_enterprise_admin",
        "ygb_enterprise_operator"));

    public void applyEnterpriseDataScope(BaseEntity query, EnterpriseScopeMode mode, String... columns)
    {
        if (query == null)
        {
            return;
        }
        sanitizeEnterpriseQueryParams(query);
        query.getParams().put(ENTERPRISE_DATA_SCOPE, buildEnterpriseDataScope(mode, columns));
    }

    public boolean isEnterpriseScopedUser()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return false;
        }
        SysUser user = loginUser.getUser();
        if (user.isAdmin() || hasAllDataScope(user))
        {
            return false;
        }
        if (user.getEnterpriseId() == null)
        {
            return false;
        }
        return hasEnterpriseRole(user);
    }

    public Long resolveScopedEnterpriseId()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return null;
        }
        return loginUser.getUser().getEnterpriseId();
    }

    public void assertEnterpriseAuthorized(Long enterpriseId)
    {
        if (!isEnterpriseScopedUser())
        {
            return;
        }
        Long scopedEnterpriseId = resolveScopedEnterpriseId();
        if (scopedEnterpriseId == null)
        {
            throw new ServiceException("当前用户未配置企业权限");
        }
        if (enterpriseId == null || !scopedEnterpriseId.equals(enterpriseId))
        {
            throw new ServiceException("无权访问该企业数据");
        }
    }

    public void assertDualEnterpriseAuthorized(Long dispatchEnterpriseId, Long employerEnterpriseId)
    {
        if (!isEnterpriseScopedUser())
        {
            return;
        }
        Long scopedEnterpriseId = resolveScopedEnterpriseId();
        if (scopedEnterpriseId == null)
        {
            throw new ServiceException("当前用户未配置企业权限");
        }
        boolean allowed = scopedEnterpriseId.equals(dispatchEnterpriseId) || scopedEnterpriseId.equals(employerEnterpriseId);
        if (!allowed)
        {
            throw new ServiceException("无权访问该企业数据");
        }
    }

    public void assertEntityEnterpriseAllowed(Object entity)
    {
        if (!isEnterpriseScopedUser() || entity == null)
        {
            return;
        }
        Long dispatchEnterpriseId = invokeLongGetter(entity, "getDispatchEnterpriseId");
        Long employerEnterpriseId = invokeLongGetter(entity, "getEmployerEnterpriseId");
        if (dispatchEnterpriseId != null || employerEnterpriseId != null)
        {
            assertDualEnterpriseAuthorized(dispatchEnterpriseId, employerEnterpriseId);
            return;
        }
        Long enterpriseId = invokeLongGetter(entity, "getEnterpriseId");
        if (enterpriseId != null)
        {
            assertEnterpriseAuthorized(enterpriseId);
        }
    }

    private String buildEnterpriseDataScope(EnterpriseScopeMode mode, String... columns)
    {
        if (!isEnterpriseScopedUser())
        {
            return "";
        }
        Long enterpriseId = resolveScopedEnterpriseId();
        if (enterpriseId == null)
        {
            return " AND 1 = 0 ";
        }
        if (columns == null || columns.length == 0)
        {
            return " AND 1 = 0 ";
        }
        if (mode == EnterpriseScopeMode.DUAL_OR && columns.length >= 2)
        {
            return " AND (" + columns[0] + " = " + enterpriseId + " OR " + columns[1] + " = " + enterpriseId + ") ";
        }
        return " AND " + columns[0] + " = " + enterpriseId + " ";
    }

    private void sanitizeEnterpriseQueryParams(BaseEntity query)
    {
        if (!isEnterpriseScopedUser())
        {
            return;
        }
        clearEnterpriseIdField(query, "setEnterpriseId");
        clearEnterpriseIdField(query, "setDispatchEnterpriseId");
        clearEnterpriseIdField(query, "setEmployerEnterpriseId");
    }

    private void clearEnterpriseIdField(BaseEntity query, String setterName)
    {
        try
        {
            Method setter = query.getClass().getMethod(setterName, Long.class);
            setter.invoke(query, new Object[] { null });
        }
        catch (ReflectiveOperationException ignored)
        {
            // entity may not expose this filter field
        }
    }

    private Long invokeLongGetter(Object entity, String getterName)
    {
        try
        {
            Method getter = entity.getClass().getMethod(getterName);
            Object value = getter.invoke(entity);
            return value instanceof Long ? (Long) value : null;
        }
        catch (ReflectiveOperationException ignored)
        {
            return null;
        }
    }

    private boolean hasEnterpriseRole(SysUser user)
    {
        if (user.getRoles() == null)
        {
            return false;
        }
        for (SysRole role : user.getRoles())
        {
            if (UserConstants.ROLE_NORMAL.equals(role.getStatus())
                && StringUtils.isNotEmpty(role.getRoleKey())
                && ENTERPRISE_ROLE_KEYS.contains(role.getRoleKey()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean hasAllDataScope(SysUser user)
    {
        if (user.getRoles() == null)
        {
            return false;
        }
        for (SysRole role : user.getRoles())
        {
            if (UserConstants.ROLE_NORMAL.equals(role.getStatus())
                && Constants.Dept.DATA_SCOPE_ALL.equals(role.getDataScope()))
            {
                return true;
            }
        }
        return false;
    }
}
