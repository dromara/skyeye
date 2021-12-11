/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: SealApplyBorrowService
 * @Description: 印章借用服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealApplyBorrowService {

    public void queryMyBorrowSealList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertBorrowSealMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryBorrowSealMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryBorrowSealMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editBorrowSealToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowSealToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowSealToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
