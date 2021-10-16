/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: LicenceApplyBorrowService
 * @Description: 证照借用服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LicenceApplyBorrowService {

    public void queryMyBorrowLicenceList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertBorrowLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryBorrowLicenceMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryBorrowLicenceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowLicenceMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editBorrowLicenceToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowLicenceToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateBorrowLicenceMationToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
