package com.yuegongbao.ygb.portal.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;

public interface IYgbPortalContentService
{
    YgbPortalContent selectPortalContentById(Long contentId);

    List<YgbPortalContent> selectPortalContentList(YgbPortalContent query);

    int insertPortalContent(YgbPortalContent content);

    int updatePortalContent(YgbPortalContent content);

    int deletePortalContentByIds(Long[] contentIds, String updateBy);
}
