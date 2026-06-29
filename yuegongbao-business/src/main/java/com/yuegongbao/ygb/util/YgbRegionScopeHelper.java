package com.yuegongbao.ygb.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.common.core.domain.entity.SysDept;
import com.yuegongbao.common.core.domain.entity.SysRole;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.mapper.SysDeptMapper;

@Component
public class YgbRegionScopeHelper
{
    private static final String REGION_DATA_SCOPE = "regionDataScope";

    @Autowired
    private SysDeptMapper deptMapper;

    public void applyRegionDataScope(BaseEntity query, String regionColumn)
    {
        if (query == null)
        {
            return;
        }
        query.getParams().put(REGION_DATA_SCOPE, buildRegionDataScope(regionColumn));
    }

    public List<String> selectAllowedRegionCodes()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return List.of();
        }
        SysUser user = loginUser.getUser();
        if (user.isAdmin() || hasAllDataScope(user))
        {
            return List.of("440000");
        }

        Set<Long> deptIds = collectVisibleDeptIds(user);
        Set<String> regions = new LinkedHashSet<>();
        for (Long deptId : deptIds)
        {
            String regionCode = resolveRegionCode(deptId);
            if ("440000".equals(regionCode))
            {
                return List.of("440000");
            }
            if (StringUtils.isNotEmpty(regionCode))
            {
                regions.add(regionCode);
            }
        }
        return new ArrayList<>(regions);
    }

    public String resolveAuthorizedRegionCode(String regionCode)
    {
        String requestedRegionCode = YgbRegionHelper.defaultDashboardRegion(regionCode);
        List<String> allowedRegionCodes = selectAllowedRegionCodes();
        if (allowedRegionCodes.contains("440000"))
        {
            return requestedRegionCode;
        }
        if (allowedRegionCodes.isEmpty())
        {
            throw new ServiceException("当前用户未配置区域权限");
        }
        if (StringUtils.isEmpty(regionCode) || "440000".equals(requestedRegionCode))
        {
            return allowedRegionCodes.get(0);
        }
        if (isRegionAllowed(requestedRegionCode, allowedRegionCodes))
        {
            return requestedRegionCode;
        }
        throw new ServiceException("无权查询该区域数据");
    }

    public boolean isRegionAllowed(String regionCode, List<String> allowedRegionCodes)
    {
        if (StringUtils.isEmpty(regionCode))
        {
            return true;
        }
        if (allowedRegionCodes == null || allowedRegionCodes.isEmpty() || allowedRegionCodes.contains("440000"))
        {
            return true;
        }
        String regionPrefix = YgbRegionHelper.toRegionPrefix(regionCode);
        for (String allowedRegionCode : allowedRegionCodes)
        {
            String allowedPrefix = YgbRegionHelper.toRegionPrefix(allowedRegionCode);
            if (StringUtils.isNotEmpty(regionPrefix) && StringUtils.isNotEmpty(allowedPrefix)
                && regionPrefix.startsWith(allowedPrefix))
            {
                return true;
            }
        }
        return false;
    }

    private String buildRegionDataScope(String regionColumn)
    {
        String column = StringUtils.defaultIfEmpty(regionColumn, "region_code");
        List<String> regionCodes = selectAllowedRegionCodes();
        if (regionCodes.contains("440000"))
        {
            return "";
        }
        if (regionCodes.isEmpty())
        {
            return " AND 1 = 0 ";
        }

        StringBuilder sql = new StringBuilder(" AND (");
        for (int i = 0; i < regionCodes.size(); i++)
        {
            if (i > 0)
            {
                sql.append(" OR ");
            }
            sql.append(column).append(" LIKE '").append(YgbRegionHelper.toRegionPrefix(regionCodes.get(i))).append("%'");
        }
        sql.append(") ");
        return sql.toString();
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

    private Set<Long> collectVisibleDeptIds(SysUser user)
    {
        Set<Long> deptIds = new LinkedHashSet<>();
        if (user.getRoles() != null)
        {
            for (SysRole role : user.getRoles())
            {
                if (!UserConstants.ROLE_NORMAL.equals(role.getStatus()))
                {
                    continue;
                }
                String dataScope = role.getDataScope();
                if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(dataScope))
                {
                    addDeptAndChildren(deptIds, deptMapper.selectDeptListByRoleId(role.getRoleId(), role.isDeptCheckStrictly()));
                }
                else if (Constants.Dept.DATA_SCOPE_DEPT.equals(dataScope)
                    || Constants.Dept.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)
                    || Constants.Dept.DATA_SCOPE_SELF.equals(dataScope))
                {
                    addDeptAndChildren(deptIds, List.of(user.getDeptId()));
                }
            }
        }
        return deptIds;
    }

    private void addDeptAndChildren(Set<Long> deptIds, List<Long> sourceDeptIds)
    {
        if (sourceDeptIds == null)
        {
            return;
        }
        for (Long deptId : sourceDeptIds)
        {
            if (deptId == null)
            {
                continue;
            }
            deptIds.add(deptId);
            List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
            for (SysDept child : children)
            {
                deptIds.add(child.getDeptId());
            }
        }
    }

    public void assertRegionAuthorized(String regionCode)
    {
        if (StringUtils.isEmpty(regionCode))
        {
            return;
        }
        List<String> allowedRegionCodes = selectAllowedRegionCodes();
        if (!isRegionAllowed(regionCode, allowedRegionCodes))
        {
            throw new ServiceException("无权访问该区域数据");
        }
    }

    public void assertEntityRegionAllowed(Object entity)
    {
        if (entity == null)
        {
            return;
        }
        try
        {
            java.lang.reflect.Method getter = entity.getClass().getMethod("getRegionCode");
            Object value = getter.invoke(entity);
            if (value instanceof String)
            {
                assertRegionAuthorized((String) value);
            }
        }
        catch (ReflectiveOperationException ignored)
        {
            // entity has no region field
        }
    }

    private String resolveRegionCode(Long deptId)
    {
        if (deptId == null)
        {
            return null;
        }
        SysDept dept = deptMapper.selectDeptById(deptId);
        if (dept == null || StringUtils.isEmpty(dept.getRegionCode()))
        {
            return null;
        }
        return dept.getRegionCode();
    }
}
