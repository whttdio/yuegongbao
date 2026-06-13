package com.yuegongbao.system.domain.vo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 路由配置信息
 *
 * @author yuegongbao
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo
{
    /** 路由名字 */
    private String name;

    /** 路由地址 */
    private String path;

    /** 是否隐藏路由 */
    private boolean hidden;

    /** 重定向地址 */
    private String redirect;

    /** 组件地址 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否总是显示 */
    private Boolean alwaysShow;

    /** 前端归属：ygb / azb / both */
    private String portalScope;

    /** 其他元素 */
    private MetaVo meta;

    /** 子路由 */
    private List<RouterVo> children;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public String getRedirect()
    {
        return redirect;
    }

    public void setRedirect(String redirect)
    {
        this.redirect = redirect;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent(String component)
    {
        this.component = component;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public Boolean getAlwaysShow()
    {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow)
    {
        this.alwaysShow = alwaysShow;
    }

    public String getPortalScope()
    {
        return portalScope;
    }

    public void setPortalScope(String portalScope)
    {
        this.portalScope = portalScope;
    }

    public MetaVo getMeta()
    {
        return meta;
    }

    public void setMeta(MetaVo meta)
    {
        this.meta = meta;
    }

    public List<RouterVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<RouterVo> children)
    {
        this.children = children;
    }
}
