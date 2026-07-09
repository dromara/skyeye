/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserMenuFavorite;

/**
 * @ClassName: SysEveUserMenuFavoriteService
 * @Description: 用户APP菜单收藏服务接口层
 */
public interface SysEveUserMenuFavoriteService extends SkyeyeBusinessService<SysEveUserMenuFavorite> {

    void queryUserFavoriteMenuList(InputObject inputObject, OutputObject outputObject);

    void saveUserFavoriteMenu(InputObject inputObject, OutputObject outputObject);

    void deleteUserFavoriteMenuByMenuId(InputObject inputObject, OutputObject outputObject);

    void saveUserFavoriteMenuOrder(InputObject inputObject, OutputObject outputObject);

}
