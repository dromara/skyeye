/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.service;

import com.skyeye.accessory.entity.ServiceUserStock;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ServiceUserStockService
 * @Description: 用户配件申领单审核通过后的库存信息服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/13 22:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ServiceUserStockService extends SkyeyeBusinessService<ServiceUserStock> {

    /**
     * 修改用户拥有的商品规格库存
     *
     * @param userId     用户id
     * @param materialId 商品id
     * @param normsId    规格id
     * @param operNumber 变化数量
     * @param type       出入库类型，{@link com.skyeye.accessory.classenum.UserStockPutOutType}
     */
    void editMaterialNormsUserStock(String userId, String materialId, String normsId, String operNumber, int type);

    void queryMyPartsNumByNormsId(InputObject inputObject, OutputObject outputObject);

    void queryUserStockByNormsIds(InputObject inputObject, OutputObject outputObject);

    void editMaterialNormsUserStockForFeign(InputObject inputObject, OutputObject outputObject);

    ServiceUserStock queryUserStock(String userId, String normsId);

    Map<String, ServiceUserStock> queryUserStock(String userId, List<String> normsIds);

    void updateStock(String userId, String normsId, String stock);

}
