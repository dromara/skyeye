/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.store.entity.ProductTransferLink;

import java.util.List;

/**
 * @ClassName: ProductTransferLinkService
 * @Description: 门店产品调拨明细服务接口
 */
public interface ProductTransferLinkService extends SkyeyeBusinessService<ProductTransferLink> {

    void saveLinkList(String pId, List<ProductTransferLink> beans);

    List<ProductTransferLink> selectByPId(String pId);

    void deleteByPId(String pId);

}
