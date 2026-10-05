/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.books.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.books.entity.SetOfBooks;

/**
 * @ClassName: IfsSetOfBooksService
 * @Description: 账套管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 12:30
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsSetOfBooksService extends SkyeyeBusinessService<SetOfBooks> {

    /**
     * 业务事件/通用模板未指定账套时，落到当前租户启用账套。
     * 已指定则原样返回。
     */
    String resolveSetOfBooksId(String setOfBooksId, String voucherDate);

}
