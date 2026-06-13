package com.yuegongbao.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.core.domain.entity.SysRole;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.service.ISysMenuService;
import com.yuegongbao.system.service.ISysRoleService;
import com.yuegongbao.system.service.ISysUserService;

/**
 * 用户权限处理
 *
 * @author yuegongbao
 */
@Component
public class SysPermissionService
{
    private static final String PORTAL_SCOPE_BOTH = "both";
    private static final String PORTAL_CODE_YGB = "ygb";
    private static final String PORTAL_CODE_AZB = "azb";

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysUserService userService;

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        if (user.isAdmin())
        {
            roles.add(Constants.SUPER_ADMIN);
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        return getMenuPermission(user, null);
    }

    public Set<String> getMenuPermission(SysUser user, String portalCode)
    {
        Set<String> perms = new HashSet<String>();
        String effectivePortalCode = resolvePortalCode(user, portalCode);
        if (user.isAdmin())
        {
            perms.add(Constants.ALL_PERMISSION);
        }
        else
        {
            List<SysRole> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                for (SysRole role : roles)
                {
                    if (StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && !role.isAdmin())
                    {
                        Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId(), effectivePortalCode);
                        role.setPermissions(rolePerms);
                        perms.addAll(rolePerms);
                    }
                }
            }
            else
            {
                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId(), effectivePortalCode));
            }
        }
        return perms;
    }

    public SysUser refreshUserRoles(SysUser user)
    {
        if (user == null || user.getUserId() == null || user.isAdmin())
        {
            return user;
        }
        SysUser refreshedUser = userService.selectUserById(user.getUserId());
        if (refreshedUser != null)
        {
            user.setRoles(refreshedUser.getRoles());
        }
        return user;
    }

    public Set<String> getAllowedPortalCodes(SysUser user)
    {
        Set<String> allowedPortalCodes = new HashSet<String>();
        if (user == null)
        {
            allowedPortalCodes.add(PORTAL_CODE_YGB);
            return allowedPortalCodes;
        }
        if (user.isAdmin())
        {
            allowedPortalCodes.add(PORTAL_CODE_YGB);
            allowedPortalCodes.add(PORTAL_CODE_AZB);
            return allowedPortalCodes;
        }
        List<SysRole> roles = user.getRoles();
        if (CollectionUtils.isEmpty(roles))
        {
            allowedPortalCodes.add(PORTAL_CODE_YGB);
            return allowedPortalCodes;
        }
        for (SysRole role : roles)
        {
            if (role == null || !StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL))
            {
                continue;
            }
            mergeAllowedPortalCodes(allowedPortalCodes, role.getAllowedPortalScope());
        }
        if (allowedPortalCodes.isEmpty())
        {
            allowedPortalCodes.add(PORTAL_CODE_YGB);
        }
        return allowedPortalCodes;
    }

    public String resolveDefaultPortalCode(SysUser user, String fallbackPortalCode)
    {
        Set<String> allowedPortalCodes = getAllowedPortalCodes(user);
        if (user != null && !CollectionUtils.isEmpty(user.getRoles()))
        {
            for (SysRole role : user.getRoles())
            {
                if (role == null || !StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL))
                {
                    continue;
                }
                String defaultPortalCode = normalizePortalCode(role.getDefaultPortalCode());
                if (allowedPortalCodes.contains(defaultPortalCode))
                {
                    return defaultPortalCode;
                }
            }
        }
        String normalizedFallbackPortalCode = normalizePortalCode(fallbackPortalCode);
        if (allowedPortalCodes.contains(normalizedFallbackPortalCode))
        {
            return normalizedFallbackPortalCode;
        }
        if (allowedPortalCodes.contains(PORTAL_CODE_YGB))
        {
            return PORTAL_CODE_YGB;
        }
        return allowedPortalCodes.iterator().next();
    }

    public String resolvePortalCode(SysUser user, String requestedPortalCode)
    {
        Set<String> allowedPortalCodes = getAllowedPortalCodes(user);
        String normalizedRequestedPortalCode = normalizePortalCode(requestedPortalCode);
        if (allowedPortalCodes.contains(normalizedRequestedPortalCode))
        {
            return normalizedRequestedPortalCode;
        }
        return resolveDefaultPortalCode(user, requestedPortalCode);
    }

    private void mergeAllowedPortalCodes(Set<String> allowedPortalCodes, String portalScope)
    {
        String normalizedPortalScope = normalizePortalCode(portalScope);
        if (PORTAL_CODE_YGB.equals(normalizedPortalScope) || PORTAL_CODE_AZB.equals(normalizedPortalScope))
        {
            allowedPortalCodes.add(normalizedPortalScope);
            return;
        }
        if (StringUtils.isEmpty(normalizedPortalScope) || PORTAL_SCOPE_BOTH.equals(normalizedPortalScope)
                || "common".equals(normalizedPortalScope))
        {
            allowedPortalCodes.add(PORTAL_CODE_YGB);
            allowedPortalCodes.add(PORTAL_CODE_AZB);
        }
    }

    private String normalizePortalCode(String portalCode)
    {
        return StringUtils.defaultString(portalCode).trim().toLowerCase();
    }
}
