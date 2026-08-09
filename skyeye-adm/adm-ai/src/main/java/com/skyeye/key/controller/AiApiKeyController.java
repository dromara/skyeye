/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.key.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai配置控制类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "AI配置", tags = "AI配置", modelName = "AI配置")
public class AiApiKeyController {

    @Autowired
    private AiApiKeyService aiApiKeyService;

    @ApiOperation(id = "writeAiApiKey", value = "新增/编辑AI配置", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AiApiKey.class)
    @RequestMapping("/post/AiApiKeyController/writeAiApiKey")
    public void writeAiApiKey(InputObject inputObject, OutputObject outputObject) {
        aiApiKeyService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiApiKey", value = "分页查询AI配置", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AiApiKeyController/queryAiApiKey")
    public void queryAiApiKey(InputObject inputObject, OutputObject outputObject) {
        aiApiKeyService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAiApiKeyById", value = "删除AI配置", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiApiKeyController/deleteAiApiKeyById")
    public void deleteAiApiKeyByIds(InputObject inputObject, OutputObject outputObject) {
        aiApiKeyService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "selectAiApiKeyById", value = "根据id获取AI配置", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiApiKeyController/selectAiApiKeyById")
    public void selectAiApiKeyById(InputObject inputObject, OutputObject outputObject) {
        aiApiKeyService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiApiKeyList", value = "获取AI配置管理信息", method = "POST", allUse = "2")
    @RequestMapping("/post/AiApiKeyController/queryAiApiKeyList")
    public void queryAiApiKeyList(InputObject inputObject, OutputObject outputObject) {
        aiApiKeyService.queryList(inputObject, outputObject);
    }
}
