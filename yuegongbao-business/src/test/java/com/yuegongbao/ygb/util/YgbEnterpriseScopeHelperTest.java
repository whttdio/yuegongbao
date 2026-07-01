package com.yuegongbao.ygb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.core.domain.BaseEntity;
import com.yuegongbao.common.core.domain.entity.SysRole;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.exception.ServiceException;

class YgbEnterpriseScopeHelperTest
{
    private final YgbEnterpriseScopeHelper helper = new YgbEnterpriseScopeHelper();

    @BeforeEach
    void setUp()
    {
        SysUser user = new SysUser();
        user.setUserId(1006L);
        user.setEnterpriseId(1001L);
        SysRole role = new SysRole();
        role.setRoleKey("ygb_enterprise_admin");
        role.setStatus(UserConstants.ROLE_NORMAL);
        role.setDataScope("3");
        user.setRoles(List.of(role));

        LoginUser loginUser = new LoginUser(1006L, 118L, user, null);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void buildSingleEnterpriseScopeSql()
    {
        BaseEntity query = new BaseEntity();
        helper.applyEnterpriseDataScope(query, EnterpriseScopeMode.SINGLE, "p.enterprise_id");
        assertEquals(" AND p.enterprise_id = 1001 ", query.getParams().get("enterpriseDataScope"));
    }

    @Test
    void buildDualOrEnterpriseScopeSql()
    {
        BaseEntity query = new BaseEntity();
        helper.applyEnterpriseDataScope(query, EnterpriseScopeMode.DUAL_OR,
            "c.dispatch_enterprise_id", "c.employer_enterprise_id");
        assertEquals(" AND (c.dispatch_enterprise_id = 1001 OR c.employer_enterprise_id = 1001) ",
            query.getParams().get("enterpriseDataScope"));
    }

    @Test
    void assertEnterpriseAuthorizedRejectsForeignEnterprise()
    {
        ServiceException ex = assertThrows(ServiceException.class, () -> helper.assertEnterpriseAuthorized(1002L));
        assertTrue(ex.getMessage().contains("无权访问该企业数据"));
    }

    @Test
    void validateEnterpriseLoginAccountRejectsMissingEnterpriseBinding()
    {
        SysUser user = new SysUser();
        user.setStatus(UserConstants.NORMAL);
        user.setRoles(List.of(buildEnterpriseRole()));
        ServiceException ex = assertThrows(ServiceException.class, () -> helper.validateEnterpriseLoginAccount(user));
        assertTrue(ex.getMessage().contains("未绑定企业"));
    }

    @Test
    void validateEnterpriseLoginAccountRejectsMissingEnterpriseRole()
    {
        SysUser user = new SysUser();
        user.setStatus(UserConstants.NORMAL);
        user.setEnterpriseId(1001L);
        ServiceException ex = assertThrows(ServiceException.class, () -> helper.validateEnterpriseLoginAccount(user));
        assertTrue(ex.getMessage().contains("无企业端登录权限"));
    }

    @Test
    void validateEnterpriseLoginAccountAcceptsEnterpriseAdmin()
    {
        SysUser user = new SysUser();
        user.setStatus(UserConstants.NORMAL);
        user.setEnterpriseId(1001L);
        user.setRoles(List.of(buildEnterpriseRole()));
        helper.validateEnterpriseLoginAccount(user);
    }

    private SysRole buildEnterpriseRole()
    {
        SysRole role = new SysRole();
        role.setRoleKey("ygb_enterprise_admin");
        role.setStatus(UserConstants.ROLE_NORMAL);
        return role;
    }

    @Test
    void assertDualEnterpriseAuthorizedAcceptsEitherSide()
    {
        helper.assertDualEnterpriseAuthorized(1002L, 1001L);
        helper.assertDualEnterpriseAuthorized(1001L, 1002L);
    }
}
