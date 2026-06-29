package com.yuegongbao.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysMenu;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginBody;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.core.text.Convert;
import com.yuegongbao.common.utils.DateUtils;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.framework.web.service.SysLoginService;
import com.yuegongbao.framework.web.service.SysPermissionService;
import com.yuegongbao.framework.web.service.TokenService;
import com.yuegongbao.system.service.ISysConfigService;
import com.yuegongbao.system.service.ISysMenuService;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

/**
 * Login controller.
 *
 * @author yuegongbao
 */
@RestController
public class SysLoginController
{
    private static final String PORTAL_HEADER = "X-Portal-Code";

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @GetMapping("getInfo")
    public AjaxResult getInfo(HttpServletRequest request)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        permissionService.refreshUserRoles(user);
        String portalCode = request.getHeader(PORTAL_HEADER);
        String effectivePortalCode = permissionService.resolvePortalCode(user, portalCode);
        Set<String> allowedPortalCodes = permissionService.getAllowedPortalCodes(user);
        String defaultPortalCode = permissionService.resolveDefaultPortalCode(user, effectivePortalCode);
        Set<String> roles = permissionService.getRolePermission(user);
        Set<String> permissions = permissionService.getMenuPermission(user, effectivePortalCode);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("allowedPortalCodes", allowedPortalCodes);
        ajax.put("allowedRegionCodes", regionScopeHelper.selectAllowedRegionCodes());
        ajax.put("enterpriseId", user.getEnterpriseId());
        ajax.put("defaultPortalCode", defaultPortalCode);
        ajax.put("effectivePortalCode", effectivePortalCode);
        ajax.put("pwdChrtype", getSysAccountChrtype());
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    @GetMapping("getRouters")
    public AjaxResult getRouters(HttpServletRequest request)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        permissionService.refreshUserRoles(user);
        Long userId = user.getUserId();
        String portalCode = permissionService.resolvePortalCode(user, request.getHeader(PORTAL_HEADER));
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId, portalCode);
        List<SysMenu> scopedMenus = menuService.filterMenuTreeByPortalScope(menus, portalCode);
        return AjaxResult.success(menuService.buildMenus(scopedMenus));
    }

    public String getSysAccountChrtype()
    {
        return Convert.toStr(configService.selectConfigByKey("sys.account.chrtype"), "0");
    }

    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
