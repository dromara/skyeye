/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserPcMenuFavorite;

/**
 * @ClassName: SysEveUserPcMenuFavoriteService
 * @Description: 用户PC菜单收藏服务接口层
 */
public interface SysEveUserPcMenuFavoriteService extends SkyeyeBusinessService<SysEveUserPcMenuFavorite> {

    void queryUserPcFavoriteMenuList(InputObject inputObject, OutputObject outputObject);

    void saveUserPcFavoriteMenu(InputObject inputObject, OutputObject outputObject);

    void deleteUserPcFavoriteMenuByMenuId(InputObject inputObject, OutputObject outputObject);

    void saveUserPcFavoriteMenuOrder(InputObject inputObject, OutputObject outputObject);

}
