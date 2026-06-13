package com.yuegongbao.ygb.worker.service;

import java.util.Map;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface WorkerHomeService
{
    Map<String, Object> getHomeView(YgbPerson worker, Long userId);

    Map<String, Object> getProfile(YgbPerson worker, Long userId, String userName, String nickName);

    Map<String, Object> getWorkbenchView(YgbPerson worker, Long userId, String userName, String nickName);
}
