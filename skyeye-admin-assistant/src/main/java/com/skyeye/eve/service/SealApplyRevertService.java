/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: SealApplyRevertService
 * @Description: 印章归还申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealApplyRevertService {

    public void queryMyRevertSealList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertRevertSealMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryRevertSealMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryRevertSealMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateRevertSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateRevertSealMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editRevertSealToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateRevertSealToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateRevertSealToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
