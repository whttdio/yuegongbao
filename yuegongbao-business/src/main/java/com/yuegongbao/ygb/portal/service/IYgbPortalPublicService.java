package com.yuegongbao.ygb.portal.service;

import java.util.List;
import java.util.Map;

public interface IYgbPortalPublicService
{
    Map<String, Object> getPortalHome(String portalCode);

    List<Map<String, Object>> searchPortalContent(String portalCode, String keyword);

    List<Map<String, Object>> listPortalJobs(String keyword, String location, String salaryText, Integer limit);

    Map<String, Object> getPortalContentDetail(Long contentId);

    Map<String, Object> getPortalJobDetail(Long jobId);
}
