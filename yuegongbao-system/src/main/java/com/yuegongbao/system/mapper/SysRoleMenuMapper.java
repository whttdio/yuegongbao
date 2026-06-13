package com.yuegongbao.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.system.domain.SysRoleMenu;

/**
 * 角色与菜单关联表 数据层
 * 
 * @author yuegongbao
 */
public interface SysRoleMenuMapper
{
    /**
     * 查询菜单使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    public int checkMenuExistRole(Long menuId);

    /**
     * 通过角色ID删除角色和菜单关联
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 閫氳繃瑙掕壊ID鍜屽墠绔寖鍥村垹闄よ彍鍗曟巿鏉?
     *
     * @param roleId 瑙掕壊ID
     * @param portalScope 鍓嶇鑼冨洿
     * @return 缁撴灉
     */
    public int deleteRoleMenuByRoleIdAndPortalScope(@Param("roleId") Long roleId, @Param("portalScope") String portalScope);

    /**
     * 批量删除角色菜单关联信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色菜单信息
     * 
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
