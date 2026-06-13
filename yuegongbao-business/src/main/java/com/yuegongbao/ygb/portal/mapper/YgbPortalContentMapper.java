package com.yuegongbao.ygb.portal.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;

public interface YgbPortalContentMapper
{
    YgbPortalContent selectPortalContentById(Long contentId);

    List<YgbPortalContent> selectPortalContentList(YgbPortalContent query);

    List<YgbPortalContent> selectPublishedPortalContentList(@Param("portalCode") String portalCode,
        @Param("sectionCode") String sectionCode, @Param("categoryCode") String categoryCode,
        @Param("keyword") String keyword, @Param("limit") Integer limit);

    int insertPortalContent(YgbPortalContent content);

    int updatePortalContent(YgbPortalContent content);

    int deletePortalContentByIds(@Param("contentIds") Long[] contentIds, @Param("updateBy") String updateBy);
}
