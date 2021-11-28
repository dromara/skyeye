/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.IfsAccountSubjectDao;
import com.skyeye.service.IfsAccountSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsAccountSubjectServiceImpl
 * @Description: 会计科目管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/27 12:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IfsAccountSubjectServiceImpl implements IfsAccountSubjectService {

    @Autowired
    private IfsAccountSubjectDao ifsAccountSubjectDao;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryIfsAccountSubjectList
     * @Description: 获取会计科目管理表
     */
    @Override
    public void queryIfsAccountSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = ifsAccountSubjectDao.queryIfsAccountSubjectList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertIfsAccountSubjectMation
     * @Description: 新增会计科目管理
     */
    @Override
    @Transactional(value = "transactionManager")
    public void insertIfsAccountSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String str = judgeSimpleTitle(map);
        if (!"".equals(str)){
            outputObject.setreturnMessage(str);
        } else {
            map.put("id", ToolUtil.getSurFaceId());
            map.put("userId", inputObject.getLogParams().get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            ifsAccountSubjectDao.insertIfsAccountSubject(map);
        }
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteIfsAccountSubjectById
     * @Description: 删除会计科目管理信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void deleteIfsAccountSubjectById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        ifsAccountSubjectDao.deleteIfsAccountSubjectById(map);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectIfsAccountSubjectById
     * @Description: 通过id查找对应的会计科目管理信息
     */
    @Override
    public void selectIfsAccountSubjectById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = ifsAccountSubjectDao.queryIfsAccountSubjectToEditById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editIfsAccountSubjectMationById
     * @Description: 通过id编辑对应的会计科目管理信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void editIfsAccountSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String str = judgeSimpleTitle(map);
        if (!"".equals(str)){
            outputObject.setreturnMessage(str);
        } else {
            map.put("userId", inputObject.getLogParams().get("id"));
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            ifsAccountSubjectDao.editIfsAccountSubjectById(map);
        }
    }

    private String judgeSimpleTitle(Map<String, Object> map) throws Exception {
        String str = "";
        Map<String, Object> bean = ifsAccountSubjectDao.queryIfsAccountSubjectMationByName(map);
        if (bean == null || bean.isEmpty()) {
            bean = ifsAccountSubjectDao.queryIfsAccountSubjectMationByNum(map);
            if (bean != null && !bean.isEmpty()) {
                str = "该会计科目编号已存在，请更换";
            }
        } else {
            str = "该会计科目名称已存在，请更换";
        }
        return str;
    }
}

