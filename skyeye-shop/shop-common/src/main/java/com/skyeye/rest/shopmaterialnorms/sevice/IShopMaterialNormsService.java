/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shopmaterialnorms.sevice;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IShopMaterialNormsService
 * @Description: ERP商城购物车信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:32
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface IShopMaterialNormsService {
    List<Map<String, Object>> queryShopMaterialByNormsIdList(String normsIds);

    List<Map<String, Object>> queryShopMaterialByMaterialIdList(String materialIds);

    /**
     * 根据id批量获取商城商品信息
     *
     * @param ids 商城商品materialId与storeId的关系id
     * @return
     */
    List<Map<String, Object>> queryShopMaterialByIds(List<String> ids);

    List<Map<String, Object>> queryAllShopMaterialListForChoose();

}
