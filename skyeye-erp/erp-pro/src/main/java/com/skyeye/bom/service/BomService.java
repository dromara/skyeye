/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.bom.entity.Bom;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ErpBomService
 * @Description: bom清单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/27 14:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface BomService extends SkyeyeBusinessService<Bom> {

    void queryBomListByNormsId(InputObject inputObject, OutputObject outputObject);

    Map<String, List<Bom>> getBomListByNormsId(String... normsId);

    void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject);

    void queryBomHistoryList(InputObject inputObject, OutputObject outputObject);

    void editBomCadContentById(InputObject inputObject, OutputObject outputObject);
}
