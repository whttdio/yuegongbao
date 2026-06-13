package com.yuegongbao.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.annotation.DataScope;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.core.domain.entity.SysRole;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.common.utils.spring.SpringUtils;
import com.yuegongbao.system.domain.SysRoleDept;
import com.yuegongbao.system.domain.SysRoleMenu;
import com.yuegongbao.system.domain.SysUserRole;
import com.yuegongbao.system.mapper.SysRoleDeptMapper;
import com.yuegongbao.system.mapper.SysRoleMapper;
import com.yuegongbao.system.mapper.SysRoleMenuMapper;
import com.yuegongbao.system.mapper.SysUserRoleMapper;
import com.yuegongbao.system.service.ISysRoleService;

/**
 * Role service implementation.
 *
 * <p>This version keeps the original RuoYi-style behavior while adding
 * portal-aware role menu persistence for the YGB/AZB dual-frontend setup.</p>
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService
{
    private static final String PORTAL_SCOPE_BOTH = "both";
    private static final String PORTAL_CODE_YGB = "ygb";
    private static final String PORTAL_CODE_AZB = "azb";

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    @Override
    @DataScope(deptAlias = "d")
    public List<SysRole> selectRoleList(SysRole role)
    {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId)
    {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles)
        {
            for (SysRole userRole : userRoles)
            {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue())
                {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId)
    {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms)
        {
            if (StringUtils.isNotNull(perm))
            {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysRole> selectRoleAll()
    {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId)
    {
        return roleMapper.selectRoleListByUserId(userId);
    }

    @Override
    public SysRole selectRoleById(Long roleId)
    {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void checkRoleAllowed(SysRole role)
    {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    @Override
    public void checkRoleDataScope(Long... roleIds)
    {
        if (!SecurityUtils.isAdmin())
        {
            for (Long roleId : roleIds)
            {
                SysRole role = new SysRole();
                role.setRoleId(roleId);
                List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
                if (StringUtils.isEmpty(roles))
                {
                    throw new ServiceException("没有权限访问角色数据");
                }
            }
        }
    }

    @Override
    public int countUserRoleByRoleId(Long roleId)
    {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    @Transactional
    public int insertRole(SysRole role)
    {
        normalizePortalSettings(role);
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    @Override
    @Transactional
    public int updateRole(SysRole role)
    {
        normalizePortalSettings(role);
        roleMapper.updateRole(role);
        roleMenuMapper.deleteRoleMenuByRoleIdAndPortalScope(role.getRoleId(), normalizeMenuPortalScope(role));
        return insertRoleMenu(role);
    }

    @Override
    public int updateRoleStatus(SysRole role)
    {
        return roleMapper.updateRole(role);
    }

    @Override
    @Transactional
    public int authDataScope(SysRole role)
    {
        roleMapper.updateRole(role);
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        return insertRoleDept(role);
    }

    public int insertRoleMenu(SysRole role)
    {
        int rows = 1;
        Long[] menuIds = role.getMenuIds();
        if (menuIds == null || menuIds.length == 0)
        {
            return rows;
        }

        String menuPortalScope = normalizeMenuPortalScope(role);
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        for (Long menuId : menuIds)
        {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            rm.setPortalScope(menuPortalScope);
            roleMenuList.add(rm);
        }

        if (!roleMenuList.isEmpty())
        {
            rows = roleMenuMapper.batchRoleMenu(roleMenuList);
        }
        return rows;
    }

    private void normalizePortalSettings(SysRole role)
    {
        if (role == null)
        {
            return;
        }
        String allowedPortalScope = normalizeAllowedPortalScope(role.getAllowedPortalScope());
        String defaultPortalCode = normalizeDefaultPortalCode(role.getDefaultPortalCode(), allowedPortalScope);
        role.setAllowedPortalScope(allowedPortalScope);
        role.setDefaultPortalCode(defaultPortalCode);
        role.setMenuPortalScope(normalizeMenuPortalScope(role));
    }

    private String normalizeAllowedPortalScope(String allowedPortalScope)
    {
        String normalized = StringUtils.defaultString(allowedPortalScope).trim().toLowerCase();
        if (PORTAL_CODE_YGB.equals(normalized) || PORTAL_CODE_AZB.equals(normalized))
        {
            return normalized;
        }
        return PORTAL_SCOPE_BOTH;
    }

    private String normalizeDefaultPortalCode(String defaultPortalCode, String allowedPortalScope)
    {
        String normalized = StringUtils.defaultString(defaultPortalCode).trim().toLowerCase();
        if (PORTAL_CODE_AZB.equals(allowedPortalScope))
        {
            return PORTAL_CODE_AZB;
        }
        if (PORTAL_CODE_YGB.equals(allowedPortalScope))
        {
            return PORTAL_CODE_YGB;
        }
        return PORTAL_CODE_AZB.equals(normalized) ? PORTAL_CODE_AZB : PORTAL_CODE_YGB;
    }

    private String normalizeMenuPortalScope(SysRole role)
    {
        String allowedPortalScope = normalizeAllowedPortalScope(role.getAllowedPortalScope());
        String requestedMenuPortalScope = StringUtils.defaultString(role.getMenuPortalScope()).trim().toLowerCase();
        if (PORTAL_CODE_YGB.equals(allowedPortalScope) || PORTAL_CODE_AZB.equals(allowedPortalScope))
        {
            return allowedPortalScope;
        }
        if (PORTAL_CODE_YGB.equals(requestedMenuPortalScope) || PORTAL_CODE_AZB.equals(requestedMenuPortalScope))
        {
            return requestedMenuPortalScope;
        }
        return PORTAL_SCOPE_BOTH;
    }

    public int insertRoleDept(SysRole role)
    {
        int rows = 1;
        List<SysRoleDept> list = new ArrayList<>();
        for (Long deptId : role.getDeptIds())
        {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (!list.isEmpty())
        {
            rows = roleDeptMapper.batchRoleDept(list);
        }
        return rows;
    }

    @Override
    @Transactional
    public int deleteRoleById(Long roleId)
    {
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        roleDeptMapper.deleteRoleDeptByRoleId(roleId);
        return roleMapper.deleteRoleById(roleId);
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds)
    {
        for (Long roleId : roleIds)
        {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        roleMenuMapper.deleteRoleMenu(roleIds);
        roleDeptMapper.deleteRoleDept(roleIds);
        return roleMapper.deleteRoleByIds(roleIds);
    }

    @Override
    public int deleteAuthUser(SysUserRole userRole)
    {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds)
    {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds)
    {
        List<SysUserRole> list = new ArrayList<>();
        for (Long userId : userIds)
        {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
}
