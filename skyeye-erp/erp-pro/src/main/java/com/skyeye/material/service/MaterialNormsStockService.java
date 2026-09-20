/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.MaterialNormsStock;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MaterialNormsStockService
 * @Description: ERP商品规格初始化库存服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/21 17:47
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MaterialNormsStockService extends SkyeyeBusinessService<MaterialNormsStock> {

    /**
     * 根据商品id删除对应的初始化库存信息
     *
     * @param materialId 商品id
     */
    void deleteNormsInitStockByMaterialId(String materialId);

    /**
     * 根据规格id获取商品规格的当前库存信息
     *
     * @param normsIds 规格id集合
     * @param depotId  仓库id
     * @return
     */
    Map<String, String> queryMaterialNormsStock(List<String> normsIds, String depotId);

    /**
     * 批量获取指定规格的初始化库存信息
     *
     * @param normsIds 规格id集合
     * @return
     */
    Map<String, List<MaterialNormsStock>> queryNormsStockByNormsId(List<String> normsIds, Integer type);

    /**
     * 保存由单据操作生成的库存信息
     *
     * @param materialId 商品id
     * @param depotId    仓库id
     * @param normsId    规格id
     * @param stock      库存数量
     * @param stockType  商品规格库存类型 {@link MaterialNormsStockType}
     */
    String saveMaterialNormsStock(String materialId, String depotId, String normsId, String stock, int stockType);

    /**
     * 保存初始化库存信息
     *
     * @param materialId
     * @param normsStock
     * @param userId
     */
    void saveMaterialNormsInitStock(String materialId, List<MaterialNormsStock> normsStock, String userId);

}
