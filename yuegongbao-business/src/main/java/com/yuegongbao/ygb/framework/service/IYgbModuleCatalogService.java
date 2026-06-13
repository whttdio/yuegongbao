package com.yuegongbao.ygb.framework.service;

import java.util.List;
import com.yuegongbao.ygb.domain.vo.YgbModuleInfo;

/**
 * 粤工保模块目录服务。
 * @author yuegongbao
 */
public interface IYgbModuleCatalogService
{
    /**
     * 查询初版模块目录。
     *
     * @return 模块列表
     */
    List<YgbModuleInfo> listModules();
}

