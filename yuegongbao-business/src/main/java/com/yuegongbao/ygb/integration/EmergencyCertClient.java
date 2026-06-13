package com.yuegongbao.ygb.integration;

import com.yuegongbao.ygb.domain.vo.YgbCertCheckResult;

/**
 * 应急特种作业证接口。
 *
 * @author yuegongbao
 */
public interface EmergencyCertClient
{
    YgbCertCheckResult checkValidity(String idCard, String certNo);
}
