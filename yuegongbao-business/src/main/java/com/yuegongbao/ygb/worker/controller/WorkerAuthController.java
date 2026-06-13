package com.yuegongbao.ygb.worker.controller;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yuegongbao.common.constant.CacheConstants;
import com.yuegongbao.common.constant.Constants;
import com.yuegongbao.common.core.domain.AjaxResult;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.core.domain.model.LoginBody;
import com.yuegongbao.common.core.domain.model.LoginUser;
import com.yuegongbao.common.core.redis.RedisCache;
import com.yuegongbao.common.enums.UserStatus;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.framework.security.context.AuthenticationContextHolder;
import com.yuegongbao.framework.web.service.SysLoginService;
import com.yuegongbao.framework.web.service.TokenService;
import com.yuegongbao.framework.web.service.UserDetailsServiceImpl;
import com.yuegongbao.system.service.ISysUserService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.worker.domain.vo.WorkerSmsCodeRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerSmsLoginRequest;
import com.yuegongbao.ygb.worker.service.WorkerSmsGatewayService;

@RestController
@RequestMapping("/app/worker/auth")
public class WorkerAuthController
{
    private static final Integer SMS_CODE_EXPIRE_SECONDS = 300;

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private YgbPersonMapper personMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private WorkerSmsGatewayService workerSmsGatewayService;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getUsername()) || StringUtils.isEmpty(loginBody.getPassword()))
        {
            throw new ServiceException("请输入账号和密码。");
        }

        SysUser user = sysUserService.selectUserByUserName(loginBody.getUsername());
        if (user == null)
        {
            throw new ServiceException("劳动者账号不存在。");
        }
        checkWorkerAccount(user);
        if (resolveWorker(user) == null)
        {
            throw new ServiceException("当前账号未绑定劳动者档案，请联系管理员。");
        }

        AjaxResult ajax = AjaxResult.success();
        LoginUser loginUser = authenticatePasswordLogin(loginBody.getUsername(), loginBody.getPassword());
        loginService.recordLoginInfo(loginUser.getUserId());
        String token = tokenService.createToken(loginUser);
        ajax.put(Constants.TOKEN, token);
        ajax.put("loginType", "worker-password");
        return ajax;
    }

    @PostMapping("/send-sms-code")
    public AjaxResult sendSmsCode(@RequestBody WorkerSmsCodeRequest request)
    {
        if (request == null || !isMobile(request.getMobile()))
        {
            throw new ServiceException("请输入正确的手机号。");
        }

        SysUser user = sysUserService.selectUserByPhonenumber(request.getMobile());
        if (user == null)
        {
            throw new ServiceException("该手机号未绑定劳动者账号。");
        }
        checkWorkerAccount(user);
        if (resolveWorker(user) == null)
        {
            throw new ServiceException("当前手机号未绑定劳动者档案，请联系管理员。");
        }

        String code = buildSmsCode();
        workerSmsGatewayService.sendLoginCode(request.getMobile(), code);
        redisCache.setCacheObject(buildSmsCodeKey(request.getMobile()), code, SMS_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("mobile", request.getMobile());
        ajax.put("expireSeconds", SMS_CODE_EXPIRE_SECONDS);
        ajax.put("mockSent", false);
        ajax.put("message", "验证码已发送，请注意查收手机短信。");
        return ajax;
    }

    @PostMapping("/sms-login")
    public AjaxResult smsLogin(@RequestBody WorkerSmsLoginRequest request)
    {
        if (request == null || !isMobile(request.getMobile()) || StringUtils.isEmpty(request.getCode()))
        {
            throw new ServiceException("请输入手机号和验证码。");
        }

        SysUser user = sysUserService.selectUserByPhonenumber(request.getMobile());
        if (user == null)
        {
            throw new ServiceException("该手机号未绑定劳动者账号。");
        }
        checkWorkerAccount(user);
        if (resolveWorker(user) == null)
        {
            throw new ServiceException("当前手机号未绑定劳动者档案，请联系管理员。");
        }

        String cacheKey = buildSmsCodeKey(request.getMobile());
        String cachedCode = redisCache.getCacheObject(cacheKey);
        if (StringUtils.isEmpty(cachedCode))
        {
            throw new ServiceException("验证码已过期，请重新获取。");
        }
        if (!request.getCode().equals(cachedCode))
        {
            throw new ServiceException("验证码不正确。");
        }
        redisCache.deleteObject(cacheKey);

        LoginUser loginUser = (LoginUser) userDetailsService.createLoginUser(user);
        loginService.recordLoginInfo(user.getUserId());
        String token = tokenService.createToken(loginUser);

        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        ajax.put("loginType", "worker-sms");
        return ajax;
    }

    private YgbPerson resolveWorker(SysUser user)
    {
        YgbPerson worker = null;
        if (StringUtils.isNotEmpty(user.getPhonenumber()))
        {
            worker = personMapper.selectPersonByMobile(user.getPhonenumber());
        }
        if (worker == null && StringUtils.isNotEmpty(user.getUserName()) && user.getUserName().matches("^1\\d{10}$"))
        {
            worker = personMapper.selectPersonByMobile(user.getUserName());
        }
        if (worker == null && StringUtils.isNotEmpty(user.getNickName()))
        {
            worker = personMapper.selectPersonByName(user.getNickName());
        }
        return worker;
    }

    private LoginUser authenticatePasswordLogin(String username, String password)
    {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationContextHolder.setContext(authenticationToken);
        try
        {
            return (LoginUser) userDetailsService.loadUserByUsername(username);
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
    }

    private boolean isMobile(String mobile)
    {
        return StringUtils.isNotEmpty(mobile) && mobile.matches("^1\\d{10}$");
    }

    private void checkWorkerAccount(SysUser user)
    {
        if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            throw new ServiceException("当前劳动者账号已停用。");
        }
    }

    private String buildSmsCode()
    {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private String buildSmsCodeKey(String mobile)
    {
        return CacheConstants.WORKER_SMS_CODE_KEY + mobile;
    }
}
