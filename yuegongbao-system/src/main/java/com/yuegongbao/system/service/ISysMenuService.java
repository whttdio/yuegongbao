package com.yuegongbao.system.service;

import java.util.List;
import java.util.Set;
import com.yuegongbao.common.core.domain.TreeSelect;
import com.yuegongbao.common.core.domain.entity.SysMenu;
import com.yuegongbao.system.domain.vo.RouterVo;

/**
 * 菜单业务层
 *
 * @author yuegongbao
 */
public interface ISysMenuService
{
    List<SysMenu> selectMenuList(Long userId);

    List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    Set<String> selectMenuPermsByUserId(Long userId);

    Set<String> selectMenuPermsByUserId(Long userId, String portalCode);

    Set<String> selectMenuPermsByRoleId(Long roleId);

    Set<String> selectMenuPermsByRoleId(Long roleId, String portalCode);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId, String portalCode);

    List<Long> selectMenuListByRoleId(Long roleId);

    List<Long> selectMenuListByRoleId(Long roleId, String menuPortalScope);

    List<RouterVo> buildMenus(List<SysMenu> menus);

    List<SysMenu> filterMenuTreeByPortalScope(List<SysMenu> menus, String portalCode);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    SysMenu selectMenuById(Long menuId);

    boolean hasChildByMenuId(Long menuId);

    boolean checkMenuExistRole(Long menuId);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    void updateMenuSort(String[] menuIds, String[] orderNums);

    int deleteMenuById(Long menuId);

    boolean checkMenuNameUnique(SysMenu menu);

    boolean checkRouteConfigUnique(SysMenu menu);
}
