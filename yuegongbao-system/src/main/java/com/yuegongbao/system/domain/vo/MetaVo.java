package com.yuegongbao.system.domain.vo;

import com.yuegongbao.common.utils.StringUtils;

/**
 * 路由显示信息
 *
 * @author yuegongbao
 */
public class MetaVo
{
    /** 标题 */
    private String title;

    /** 图标 */
    private String icon;

    /** 是否缓存 */
    private boolean noCache;

    /** 内链地址 */
    private String link;

    /** 前端归属：ygb / azb / both */
    private String portalScope;

    public MetaVo()
    {
    }

    public MetaVo(String title, String icon)
    {
        this.title = title;
        this.icon = icon;
    }

    public MetaVo(String title, String icon, boolean noCache)
    {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }

    public MetaVo(String title, String icon, String link)
    {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }

    public MetaVo(String title, String icon, boolean noCache, String link)
    {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
        if (StringUtils.ishttp(link))
        {
            this.link = link;
        }
    }

    public boolean isNoCache()
    {
        return noCache;
    }

    public void setNoCache(boolean noCache)
    {
        this.noCache = noCache;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getPortalScope()
    {
        return portalScope;
    }

    public void setPortalScope(String portalScope)
    {
        this.portalScope = portalScope;
    }
}
