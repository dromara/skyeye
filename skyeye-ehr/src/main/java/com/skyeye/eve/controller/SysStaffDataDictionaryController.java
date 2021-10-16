/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysStaffDataDictionaryService;

@Controller
public class SysStaffDataDictionaryController {

    @Autowired
    private SysStaffDataDictionaryService sysStaffDataDictionaryService;

    /**
     * 查询员工基础信息相关的数据字典列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/querySysStaffDataDictionaryList")
    @ResponseBody
    public void querySysStaffDataDictionaryList(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.querySysStaffDataDictionaryList(inputObject, outputObject);
    }

    /**
     * 新增员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/insertSysStaffDataDictionaryMation")
    @ResponseBody
    public void insertSysStaffDataDictionaryMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.insertSysStaffDataDictionaryMation(inputObject, outputObject);
    }

    /**
     * 修改员工基础信息相关的数据字典时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/querySysStaffDataDictionaryMationToEditById")
    @ResponseBody
    public void querySysStaffDataDictionaryMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.querySysStaffDataDictionaryMationToEditById(inputObject, outputObject);
    }

    /**
     * 修改员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/editSysStaffDataDictionaryMationById")
    @ResponseBody
    public void editSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.editSysStaffDataDictionaryMationById(inputObject, outputObject);
    }

    /**
     * 禁用员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/downSysStaffDataDictionaryMationById")
    @ResponseBody
    public void downSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.downSysStaffDataDictionaryMationById(inputObject, outputObject);
    }

    /**
     * 启用员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/upSysStaffDataDictionaryMationById")
    @ResponseBody
    public void upSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.upSysStaffDataDictionaryMationById(inputObject, outputObject);
    }

    /**
     * 删除员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/deleteSysStaffDataDictionaryMationById")
    @ResponseBody
    public void deleteSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.deleteSysStaffDataDictionaryMationById(inputObject, outputObject);
    }

    /**
     * 获取所有启用员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffDataDictionaryController/querySysStaffDataDictionaryUpMation")
    @ResponseBody
    public void querySysStaffDataDictionaryUpMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffDataDictionaryService.querySysStaffDataDictionaryUpMation(inputObject, outputObject);
    }

}
