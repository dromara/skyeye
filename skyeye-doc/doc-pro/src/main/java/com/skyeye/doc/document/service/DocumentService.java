/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.document.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.document.entity.Document;

/**
 * @ClassName: DocumentService
 * @Description: 文档服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/24 11:18
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface DocumentService extends SkyeyeBusinessService<Document> {

    void queryAllDocumentByList(InputObject inputObject, OutputObject outputObject);

    void deleteDocumentById(InputObject inputObject, OutputObject outputObject);

    void queryAllEnabledDocumentByList(InputObject inputObject, OutputObject outputObject);

    /**
     * 将文档/目录移动到指定父目录（parentId=0 表示根目录）
     */
    void moveDocument(InputObject inputObject, OutputObject outputObject);
}
