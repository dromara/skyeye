/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shopmaterialnorms.sevice.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.rest.shopmaterialnorms.rest.IShopMaterialNormsRest;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: IShopMaterialNormsServiceImpl
 * @Description: ERP商城购物车信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:32
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class IShopMaterialNormsServiceImpl extends IServiceImpl implements IShopMaterialNormsService {

    @Autowired
    private IShopMaterialNormsRest iShopMaterialNormsRest;

    @Override
    public List<Map<String, Object>> queryShopMaterialByNormsIdList(String normsIds) {
        ResultEntity resultEntity = ExecuteFeignClient.get(() -> iShopMaterialNormsRest.queryShopMaterialByNormsIdList(normsIds));
        List<Map<String, Object>> rows = resultEntity.getRows();
        return rows;
    }

    @Override
    public List<Map<String, Object>> queryShopMaterialByMaterialIdList(String materialIds) {
        ResultEntity resultEntity = ExecuteFeignClient.get(() -> iShopMaterialNormsRest.queryShopMaterialByMaterialIdList(materialIds));
        List<Map<String, Object>> rows = resultEntity.getRows();
        return rows;
    }

    @Override
    public List<Map<String, Object>> queryShopMaterialByIds(List<String> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        ids = ids.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        String joinIds = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
        ResultEntity resultEntity = ExecuteFeignClient.get(() -> iShopMaterialNormsRest.queryShopMaterialByIds(joinIds));
        List<Map<String, Object>> rows = resultEntity.getRows();
        return rows;
    }

    @Override
    public List<Map<String, Object>> queryAllShopMaterialListForChoose() {
        ResultEntity resultEntity = ExecuteFeignClient.get(() -> iShopMaterialNormsRest.queryAllShopMaterialListForChoose());
        List<Map<String, Object>> rows = resultEntity.getRows();
        return rows;
    }

}
