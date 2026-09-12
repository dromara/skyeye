/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MaterialNormsService
 * @Description: ERP商品规格参数服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/21 15:38
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MaterialNormsService extends SkyeyeBusinessService<MaterialNorms> {

    void saveMaterialNorms(String userId, Material material);

    /**
     * 根据商品id删除规格信息
     *
     * @param materialId 商品id
     */
    void deleteMaterialNormsByMaterialId(String materialId);

    /**
     * 根据产品id批量获取产品规格单位信息
     *
     * @param depotId     仓库id
     * @param materialIds 商品id
     * @return
     */
    Map<String, List<MaterialNorms>> queryMaterialNormsList(String depotId, String... materialIds);

    MaterialNorms queryMaterialNorms(String normsId, String depotId);

    /**
     * 根据产品id获取规格信息(包含规格所属单位信息)
     *
     * @param materialId 商品id
     * @return
     */
    List<MaterialNorms> queryNormsUnitListByMaterialId(String materialId);

    void calcDepotStock(MaterialNorms materialNorms, String depotId);

    void queryNormsListByMaterialId(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取所有启用规格列表（所属商品须启用且未删除，无入参）
     */
    void queryAllNormsList(InputObject inputObject, OutputObject outputObject);
}
