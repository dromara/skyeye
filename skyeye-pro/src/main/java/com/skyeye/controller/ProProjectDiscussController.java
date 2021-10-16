/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProProjectDiscussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProProjectDiscussController {

    @Autowired
    private ProProjectDiscussService proProjectDiscussService;
    
    /**
     *
     * @Title: queryProProjectDiscussList
     * @Description: 获取项目的讨论版的列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussController/queryProProjectDiscussList")
    @ResponseBody
    public void queryProProjectDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	proProjectDiscussService.queryProProjectDiscussList(inputObject, outputObject);
    }

    /**
     *
     * @Title: insertProProjectDiscuss
     * @Description: 社区发帖
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussController/insertProProjectDiscuss")
    @ResponseBody
    public void insertProProjectDiscuss(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectDiscussService.insertProProjectDiscuss(inputObject, outputObject);
    }


    /**
     *
     * @Title: insertProProjectDiscussReply
     * @Description: 新增帖子的回复贴
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussController/insertProProjectDiscussReply")
    @ResponseBody
    public void insertProProjectDiscussReply(InputObject inputObject, OutputObject outputObject) throws Exception{
        proProjectDiscussService.insertProProjectDiscussReply(inputObject, outputObject);
    }
    
    /**
     *
     * @Title: deleteProProjectDiscussById
     * @Description: 删除讨论版
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussController/deleteProProjectDiscussById")
    @ResponseBody
    public void deleteProProjectDiscussById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	proProjectDiscussService.deleteProProjectDiscussById(inputObject, outputObject);
    }

    /**
     *
     * @Title: queryProProjectDiscussMationById
     * @Description: 根据讨论版Id获取讨论版详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProProjectDiscussController/queryProProjectDiscussMationById")
    @ResponseBody
    public void queryProProjectDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        proProjectDiscussService.queryProProjectDiscussMationById(inputObject, outputObject);
    }

    /**
    *
    * @Title: queryAllDiscussList
    * @Description: 获取所有讨论板
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
   @RequestMapping("/post/ProProjectDiscussController/queryAllDiscussList")
   @ResponseBody
   public void queryAllDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception {
       proProjectDiscussService.queryAllDiscussList(inputObject, outputObject);
   }
}
