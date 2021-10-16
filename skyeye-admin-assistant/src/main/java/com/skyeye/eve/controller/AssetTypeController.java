/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetTypeService;

/**
 * @ClassName: AssetTypeController
 * @Description: 资产类型管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/17 23:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetTypeController {
    
    @Autowired
    private AssetTypeService assetTypeService;

    /**
     *
     * @Title: selectAllAssettypeMation
     * @Description: 遍历所有的资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/selectAllAssettypeMation")
    @ResponseBody
    public void selectAllAssettypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.selectAllAssettypeMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssettypeMation
     * @Description: 新增资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/insertAssettypeMation")
    @ResponseBody
    public void insertAssettypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.insertAssettypeMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteAssettypeById
     * @Description: 删除资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/deleteAssettypeById")
    @ResponseBody
    public void deleteAssettypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.deleteAssettypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssettypeMationById
     * @Description: 查询资产类型信息用以编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/queryAssettypeMationById")
    @ResponseBody
    public void queryAssettypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.queryAssettypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssettypeMationById
     * @Description: 编辑资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/editAssettypeMationById")
    @ResponseBody
    public void editAssettypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.editAssettypeMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: selectAllAssettypeToChoose
     * @Description: 遍历所有的资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/selectAllAssettypeToChoose")
    @ResponseBody
    public void selectAllAssettypeToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.selectAllAssettypeToChoose(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssettypeUpTypeById
     * @Description: 资产类型上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/editAssettypeUpTypeById")
    @ResponseBody
    public void editAssettypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.editAssettypeUpTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssettypeDownTypeById
     * @Description: 资产类型下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/editAssettypeDownTypeById")
    @ResponseBody
    public void editAssettypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.editAssettypeDownTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssettypeSortTopById
     * @Description: 资产类型上移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/editAssettypeSortTopById")
    @ResponseBody
    public void editAssettypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.editAssettypeSortTopById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssettypeSortLowerById
     * @Description: 资产类型下移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetTypeController/editAssettypeSortLowerById")
    @ResponseBody
    public void editAssettypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetTypeService.editAssettypeSortLowerById(inputObject, outputObject);
    }
    
}
