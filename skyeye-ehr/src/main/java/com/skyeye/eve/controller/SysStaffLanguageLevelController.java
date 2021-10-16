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
import com.skyeye.eve.service.SysStaffLanguageLevelService;

@Controller
public class SysStaffLanguageLevelController {

    @Autowired
    private SysStaffLanguageLevelService sysStaffLanguageLevelService;

    /**
     * 查询语种等级列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/querySysStaffLanguageLevelList")
    @ResponseBody
    public void querySysStaffLanguageLevelList(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.querySysStaffLanguageLevelList(inputObject, outputObject);
    }

    /**
     * 新增语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/insertSysStaffLanguageLevelMation")
    @ResponseBody
    public void insertSysStaffLanguageLevelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.insertSysStaffLanguageLevelMation(inputObject, outputObject);
    }

    /**
     * 修改语种等级时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/querySysStaffLanguageLevelMationToEditById")
    @ResponseBody
    public void querySysStaffLanguageLevelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.querySysStaffLanguageLevelMationToEditById(inputObject, outputObject);
    }

    /**
     * 修改语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/editSysStaffLanguageLevelMationById")
    @ResponseBody
    public void editSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.editSysStaffLanguageLevelMationById(inputObject, outputObject);
    }

    /**
     * 禁用语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/downSysStaffLanguageLevelMationById")
    @ResponseBody
    public void downSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.downSysStaffLanguageLevelMationById(inputObject, outputObject);
    }

    /**
     * 启用语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/upSysStaffLanguageLevelMationById")
    @ResponseBody
    public void upSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.upSysStaffLanguageLevelMationById(inputObject, outputObject);
    }

    /**
     * 删除语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/deleteSysStaffLanguageLevelMationById")
    @ResponseBody
    public void deleteSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.deleteSysStaffLanguageLevelMationById(inputObject, outputObject);
    }

    /**
     * 获取所有启用语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SysStaffLanguageLevelController/querySysStaffLanguageLevelUpMation")
    @ResponseBody
    public void querySysStaffLanguageLevelUpMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        sysStaffLanguageLevelService.querySysStaffLanguageLevelUpMation(inputObject, outputObject);
    }

}
