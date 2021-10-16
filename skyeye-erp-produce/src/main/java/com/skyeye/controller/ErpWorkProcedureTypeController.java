/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpWorkProcedureTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErpWorkProcedureTypeController {

    @Autowired
    private ErpWorkProcedureTypeService erpWorkProcedureTypeService;

    /**
     * 查询工序类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/queryErpWorkProcedureTypeList")
    @ResponseBody
    public void queryErpWorkProcedureTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.queryErpWorkProcedureTypeList(inputObject, outputObject);
    }

    /**
     * 新增工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/insertErpWorkProcedureTypeMation")
    @ResponseBody
    public void insertErpWorkProcedureTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.insertErpWorkProcedureTypeMation(inputObject, outputObject);
    }

    /**
     * 修改工序类别时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/queryErpWorkProcedureTypeMationToEditById")
    @ResponseBody
    public void queryErpWorkProcedureTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.queryErpWorkProcedureTypeMationToEditById(inputObject, outputObject);
    }

    /**
     * 修改工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/editErpWorkProcedureTypeMationById")
    @ResponseBody
    public void editErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.editErpWorkProcedureTypeMationById(inputObject, outputObject);
    }

    /**
     * 禁用工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/downErpWorkProcedureTypeMationById")
    @ResponseBody
    public void downErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.downErpWorkProcedureTypeMationById(inputObject, outputObject);
    }

    /**
     * 启用工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/upErpWorkProcedureTypeMationById")
    @ResponseBody
    public void upErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.upErpWorkProcedureTypeMationById(inputObject, outputObject);
    }

    /**
     * 删除工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/deleteErpWorkProcedureTypeMationById")
    @ResponseBody
    public void deleteErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.deleteErpWorkProcedureTypeMationById(inputObject, outputObject);
    }

    /**
     * 获取所有启用工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpWorkProcedureTypeController/queryErpWorkProcedureTypeUpMation")
    @ResponseBody
    public void queryErpWorkProcedureTypeUpMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        erpWorkProcedureTypeService.queryErpWorkProcedureTypeUpMation(inputObject, outputObject);
    }

}
