/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetTypeService
 * @Description: 资产类型管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/17 23:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetTypeService {

    public void selectAllAssettypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssettypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteAssettypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssettypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssettypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void selectAllAssettypeToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssettypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssettypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssettypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssettypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
