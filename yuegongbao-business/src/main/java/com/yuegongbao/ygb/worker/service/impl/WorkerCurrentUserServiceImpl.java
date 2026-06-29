package com.yuegongbao.ygb.worker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.mapper.YgbPersonMapper;
import com.yuegongbao.ygb.worker.service.WorkerCurrentUserService;

@Service
public class WorkerCurrentUserServiceImpl implements WorkerCurrentUserService
{
    @Autowired
    private YgbPersonMapper personMapper;

    @Override
    public SysUser getCurrentSysUser()
    {
        return SecurityUtils.getLoginUser().getUser();
    }

    @Override
    public YgbPerson getCurrentWorker()
    {
        YgbPerson worker = resolveCurrentWorker();
        if (worker == null)
        {
            throw new ServiceException("当前账号未绑定劳动者档案，请联系管理员。");
        }
        return worker;
    }

    @Override
    public YgbPerson getCurrentWorkerIfPresent()
    {
        return resolveCurrentWorker();
    }

    private YgbPerson resolveCurrentWorker()
    {
        SysUser user = getCurrentSysUser();
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
}
