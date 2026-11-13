/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.document.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.document.entity.Document;
import com.skyeye.doc.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentController
 * @Description: 文档管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/24 11:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "文档管理", tags = "文档管理", modelName = "文档管理")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @ApiOperation(id = "queryAllDocumentByList", value = "获取文档/目录信息", method = "POST", allUse = "1")
    @RequestMapping("/post/DocumentController/queryAllDocumentByList")
    public void queryAllDocumentByList(InputObject inputObject, OutputObject outputObject) {
        documentService.queryAllDocumentByList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDocument", value = "添加/编辑文档/目录信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Document.class)
    @RequestMapping("/post/DocumentController/writeDocument")
    public void writeDocument(InputObject inputObject, OutputObject outputObject) {
        documentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDocumentById", value = "据ID查询文档/目录信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DocumentController/queryDocumentById")
    public void queryDocumentById(InputObject inputObject, OutputObject outputObject) {
        documentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDocumentById", value = "根据id删除文档/目录等级", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DocumentLevelController/deleteDocumentById")
    public void deleteDocumentById(InputObject inputObject, OutputObject outputObject) {
        documentService.deleteDocumentById(inputObject, outputObject);
    }

    @ApiOperation(id = "moveDocument", value = "移动文档/目录到指定父目录", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "要移动的文档/目录主键id", required = "required"),
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "目标父目录id，根目录传 0", required = "required")})
    @RequestMapping("/post/DocumentController/moveDocument")
    public void moveDocument(InputObject inputObject, OutputObject outputObject) {
        documentService.moveDocument(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEnabledDocumentByList", value = "获取所有启动的文档/目录信息", method = "POST", allUse = "2")
    @RequestMapping("/post/DocumentController/queryAllEnabledDocumentByList")
    public void queryAllEnabledDocumentByList(InputObject inputObject, OutputObject outputObject) {
        documentService.queryAllEnabledDocumentByList(inputObject, outputObject);
    }

}
