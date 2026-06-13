package com.yuegongbao.ygb.worker.service;

import java.util.Map;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;

public interface WorkerQueryService
{
    Map<String, Object> getSalaryList(YgbPerson worker, String year);

    Map<String, Object> getSalaryDetail(YgbPerson worker, String month);

    Map<String, Object> getSocialSecurityList(YgbPerson worker, String year);

    Map<String, Object> getSocialSecurityDetail(YgbPerson worker, String month);

    Map<String, Object> getTaxList(YgbPerson worker, String year);

    Map<String, Object> getTaxDetail(YgbPerson worker, String month);
}
