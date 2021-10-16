/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.AssetFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: AssetFromController
 * @Description: 资产来源控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class AssetFromController {

    @Autowired
    private AssetFromService assetFromService;

    /**
     *
     * @Title: selectAllAssetFromMation
     * @Description: 遍历所有的资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/selectAllAssetFromMation")
    @ResponseBody
    public void selectAllAssetFromMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.selectAllAssetFromMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertAssetFromMation
     * @Description: 新增资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/insertAssetFromMation")
    @ResponseBody
    public void insertAssetFromMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.insertAssetFromMation(inputObject, outputObject);
    }

    /**
     *
     * @Title: deleteAssetFromById
     * @Description: 删除资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/deleteAssetFromById")
    @ResponseBody
    public void deleteAssetFromById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.deleteAssetFromById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryAssetFromMationById
     * @Description: 查询资产来源信息用以编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/queryAssetFromMationById")
    @ResponseBody
    public void queryAssetFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.queryAssetFromMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetFromMationById
     * @Description: 编辑资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/editAssetFromMationById")
    @ResponseBody
    public void editAssetFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.editAssetFromMationById(inputObject, outputObject);
    }

    /**
     *
     * @Title: selectAllAssetFromToChoose
     * @Description: 遍历所有的资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/selectAllAssetFromToChoose")
    @ResponseBody
    public void selectAllAssetFromToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.selectAllAssetFromToChoose(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetFromUpTypeById
     * @Description: 资产来源上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/editAssetFromUpTypeById")
    @ResponseBody
    public void editAssetFromUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.editAssetFromUpTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetFromDownTypeById
     * @Description: 资产来源下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/editAssetFromDownTypeById")
    @ResponseBody
    public void editAssetFromDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.editAssetFromDownTypeById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetFromSortTopById
     * @Description: 资产来源上移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/editAssetFromSortTopById")
    @ResponseBody
    public void editAssetFromSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.editAssetFromSortTopById(inputObject, outputObject);
    }

    /**
     *
     * @Title: editAssetFromSortLowerById
     * @Description: 资产来源下移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/AssetFromController/editAssetFromSortLowerById")
    @ResponseBody
    public void editAssetFromSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
        assetFromService.editAssetFromSortLowerById(inputObject, outputObject);
    }

}
