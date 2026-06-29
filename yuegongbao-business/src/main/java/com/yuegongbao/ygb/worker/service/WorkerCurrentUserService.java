package com.yuegongbao.ygb.worker.service;

import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface WorkerCurrentUserService
{
    SysUser getCurrentSysUser();

    YgbPerson getCurrentWorker();

    YgbPerson getCurrentWorkerIfPresent();
}
