package com.yuegongbao.ygb.portal.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.portal.service.IYgbPortalContentService;

@Service
public class YgbPortalContentServiceImpl implements IYgbPortalContentService
{
    @Autowired
    private YgbPortalContentMapper portalContentMapper;

    @Override
    public YgbPortalContent selectPortalContentById(Long contentId)
    {
        return portalContentMapper.selectPortalContentById(contentId);
    }

    @Override
    public List<YgbPortalContent> selectPortalContentList(YgbPortalContent query)
    {
        return portalContentMapper.selectPortalContentList(query);
    }

    @Override
    public int insertPortalContent(YgbPortalContent content)
    {
        return portalContentMapper.insertPortalContent(content);
    }

    @Override
    public int updatePortalContent(YgbPortalContent content)
    {
        return portalContentMapper.updatePortalContent(content);
    }

    @Override
    public int deletePortalContentByIds(Long[] contentIds, String updateBy)
    {
        return portalContentMapper.deletePortalContentByIds(contentIds, updateBy);
    }
}
