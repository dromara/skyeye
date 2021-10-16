/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetArticlesTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetArticlesTypeController
 * @Description: 用品分类控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:05
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetArticlesTypeController {

    @Autowired
    private AssetArticlesTypeService assetArticlesTypeService;

    /**
     *
     * @Title: queryAssetArticlesTypeList
     * @Description: 获取用品类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/queryAssetArticlesTypeList")
    @ResponseBody
    public void queryAssetArticlesTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.queryAssetArticlesTypeList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssetArticlesType
     * @Description: 添加用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/insertAssetArticlesType")
    @ResponseBody
    public void insertAssetArticlesType(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.insertAssetArticlesType(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteAssetArticlesTypeById
     * @Description: 删除用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/deleteAssetArticlesTypeById")
    @ResponseBody
    public void deleteAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.deleteAssetArticlesTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateUpAssetArticlesTypeById
     * @Description: 上线用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/updateUpAssetArticlesTypeById")
    @ResponseBody
    public void updateUpAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.updateUpAssetArticlesTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: updateDownAssetArticlesTypeById
     * @Description: 下线用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/updateDownAssetArticlesTypeById")
    @ResponseBody
    public void updateDownAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.updateDownAssetArticlesTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: selectAssetArticlesTypeById
     * @Description: 通过id查找对应的用品类别信息用以编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/selectAssetArticlesTypeById")
    @ResponseBody
    public void selectAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.selectAssetArticlesTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesTypeById
     * @Description: 通过id编辑对应的用品类别信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/editAssetArticlesTypeById")
    @ResponseBody
    public void editAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.editAssetArticlesTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesTypeOrderUpById
     * @Description: 用品类别上移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/editAssetArticlesTypeOrderUpById")
    @ResponseBody
    public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.editAssetArticlesTypeOrderUpById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetArticlesTypeOrderDownById
     * @Description: 用品类别下移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/editAssetArticlesTypeOrderDownById")
    @ResponseBody
    public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.editAssetArticlesTypeOrderDownById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetArticlesTypeUpStateList
     * @Description: 获取已经上线的用品类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetArticlesTypeController/queryAssetArticlesTypeUpStateList")
    @ResponseBody
    public void queryAssetArticlesTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetArticlesTypeService.queryAssetArticlesTypeUpStateList(inputObject, outputObject);
    }

}
