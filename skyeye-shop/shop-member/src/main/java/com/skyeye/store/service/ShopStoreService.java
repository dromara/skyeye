/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopStore;

/**
 * @ClassName: StoreService
 * @Description: 门店管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 12:35
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ShopStoreService extends SkyeyeBusinessService<ShopStore> {

    void queryStoreListByParams(InputObject inputObject, OutputObject outputObject);

    void queryStoreOnlineById(InputObject inputObject, OutputObject outputObject);

    void saveStoreOnlineMation(InputObject inputObject, OutputObject outputObject);

    void queryStoreOnlineMationPointDay(InputObject inputObject, OutputObject outputObject);

    void queryStoreListFoServer(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询当前会员的个人门店列表（tenant=shop + createId=会员id）
     */
    void queryMyPersonalStoreList(InputObject inputObject, OutputObject outputObject);
}
