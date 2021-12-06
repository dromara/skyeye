/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.factory;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.impl.ActivitiService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActModelDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.exception.CustomException;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActivitiFactory
 * @Description: 工作流工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/9 20:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public abstract class ActivitiFactory {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected SysEnclosureDao sysEnclosureDao;

    protected ActivitiModelService activitiModelService;

    protected ActModelDao actModelDao;

    protected ActivitiService activitiService;

    protected RuntimeService runtimeService;

    protected String key;

    protected InputObject inputObject;

    protected OutputObject outputObject;

    protected ActivitiFactory(InputObject inputObject, OutputObject outputObject, String key) {
        this.inputObject = inputObject;
        this.outputObject = outputObject;
        this.key = key;
        this.initObject();
    }

    protected ActivitiFactory(String key) {
        this.key = key;
        this.initObject();
    }

    /**
     * 初始化数据
     */
    protected void initObject(){
        this.activitiModelService = SpringUtils.getBean(ActivitiModelService.class);
        this.actModelDao = SpringUtils.getBean(ActModelDao.class);
        this.activitiService = SpringUtils.getBean(ActivitiService.class);
        this.runtimeService = SpringUtils.getBean(RuntimeService.class);
        this.sysEnclosureDao = SpringUtils.getBean(SysEnclosureDao.class);
        this.initChildObject();
    }

    protected abstract void initChildObject();

    public String getActModelTitle() throws Exception {
        Map<String, Object> actModelMation = this.getActModelMation();
        if(actModelMation == null || actModelMation.isEmpty()){
            return StringUtils.EMPTY;
        }
        if(actModelMation.containsKey("title")){
            return actModelMation.get("title").toString();
        }
        return StringUtils.EMPTY;
    }

    private Map<String, Object> getActModelMation() throws Exception {
        return actModelDao.queryActModelMationByPageUrl(key);
    }

    /**
     * 获取和工作流相关操作的列表
     * @desc editRow -1：什么都不能做； 1：编辑|提交审批|作废； 2：编辑|撤销
     *
     * @throws Exception
     */
    public List<Map<String, Object>> queryWithActivitiList() throws Exception{
        Map<String, Object> inputParams = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        inputParams.put("userId", userId);
        Page pages = PageHelper.startPage(Integer.parseInt(inputParams.get("page").toString()), Integer.parseInt(inputParams.get("limit").toString()));
        List<Map<String, Object>> beans = this.queryWithActivitiListRunSql(inputParams);
        String taskType = this.getActModelTitle();
        for(Map<String, Object> bean : beans){
            this.setDataOtherMation(userId, taskType, bean);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
        return beans;
    }

    public void setDataOtherMation(String userId, String taskType, Map<String, Object> bean) throws Exception {
        Integer state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
            || ActivitiConstants.ActivitiState.IN_EXAMINE.getState() == state
            || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
            || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            bean.put("editRow", 1);
        }else{
            bean.put("editRow", -1);
        }
        bean.put("taskType", taskType);
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 设置指定bean的是否可以编辑的参数
        this.setDataStateEditRowWhenInExamine(bean, state, userId);
    }

    public void setDataStateEditRowWhenInExamine(Map<String, Object> bean, Integer state, String userId) throws Exception {
        String processInstanceId = bean.get("processInstanceId").toString();
        if(ActivitiConstants.ActivitiState.IN_EXAMINE.getState() == state
            && !ToolUtil.isBlank(processInstanceId)){
            // 审核中
            activitiModelService.setWhetherEditByProcessInstanceId(bean, processInstanceId, userId);
        }
    }

    /**
     * 获取和工作流相关操作的列表时获取数据
     *
     * @param inputParams 前台传递的入参
     * @return 结果
     * @throws Exception
     */
    protected abstract List<Map<String, Object>> queryWithActivitiListRunSql(Map<String, Object> inputParams) throws Exception;

    /**
     * 提交数据到工作流
     *
     * @param id 需要提交到工作流的主单据id
     * @param approvalId 审批人id
     * @throws Exception
     */
    public void submitToActivi(String id, String approvalId) throws Exception{
        // 1.获取数据
        Map<String, Object> data = this.submitToActiviGetDate(id);
        judgeSubmitActiviti(data);
        // 2.根据页面获取该页面配置的工作流key,如果actModel没有配置，则无法提交审批
        Map<String, Object> actModel = getActModelMation();
        if(actModel != null && !actModel.isEmpty()){
            // 3.转换成json数据
            Map<String, Object> json = new HashMap<>();
            json.put("keyName", actModel.get("actKey"));
            json.put("jsonStr", this.transform(data));
            // 4.请求工作流接口获取数据
            Map<String, Object> user = inputObject.getLogParams();
            activitiModelService.editActivitiModelToStartProcessByMap(json, user, id, approvalId);
            if("0".equals(json.get("code").toString())){
                // 请求成功后返回流程实例id
                String processInId = json.get("message").toString();
                this.submitToActiviSuccess(id, processInId);
            }else{
                throw new CustomException(json.get("message").toString());
            }
        }else{
            throw new CustomException("该功能暂未配置工作流，请联系管理员进行配置。");
        }
    }

    /**
     * 提交数据到工作流时获取数据
     *
     * @param id 需要提交到工作流的主单据id
     * @return
     * @throws Exception
     */
    protected abstract Map<String, Object> submitToActiviGetDate(String id) throws Exception;

    /**
     * 提交数据到工作流后成功的回调函数
     *
     * @param id 需要提交到工作流的主单据id
     * @param processInId 提交成功后的流程实例id
     * @throws Exception
     */
    protected abstract void submitToActiviSuccess(String id, String processInId) throws Exception;

    /**
     * 将数据转化成工作流展示的对象
     *
     * @param bean 数据
     * @return
     * @throws Exception
     */
    private Map<String, Object> transform(Map<String, Object> bean) throws Exception {
        String transformClassPath = ActivitiConstants.ActivitiObjectType.getTransformClassPath(key);
        Class<?> clazz = Class.forName(transformClassPath);
        // 要执行的方法
        Method doing = clazz.getMethod("transform", Map.class);
        Constructor<?> constructor = clazz.getConstructor();
        Object instance = constructor.newInstance();
        return (Map<String, Object>) doing.invoke(instance, bean);
    }

    /**
     * 判断是否已经提交到工作流
     * @param bean 实体信息
     * @return
     * @throws Exception
     */
    protected void judgeSubmitActiviti(Map<String, Object> bean) throws Exception {
        if(bean != null && !bean.isEmpty()){
            int state = Integer.parseInt(bean.get("state").toString());
            if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
                // 草稿，审核不通过，撤销的可以提交审批
            }else{
                throw new CustomException("该数据状态已改变，请刷新页面");
            }
        }else{
            throw new CustomException("该数据不存在，请刷新页面");
        }
    }

    /**
     * 撤销工作流
     *
     * @return 订单数据
     * @throws Exception
     */
    public void revokeActivi() throws Exception{
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        // 在工作流中撤销申请，map必须包含processInstanceId和userId
        activitiService.editDsFormContentToRevokeByProcessInstanceId(map);
        if("0".equals(map.get("code").toString())){
            // 撤销成功
            logger.info("revokeActivi success, processInstanceId is {}", processInstanceId);
            this.revokeActiviSuccess(map);
        }else{
            logger.warn("revokeActivi failed, processInstanceId is {}", processInstanceId);
            outputObject.setreturnMessage(map.get("message").toString());
        }
    }

    public void revokeActivi(String processInstanceId, String createId) throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("processInstanceId", processInstanceId);
        map.put("userId", createId);
        // 在工作流中撤销申请，map必须包含processInstanceId和userId
        activitiService.editDsFormContentToRevokeByProcessInstanceId(map);
        if("0".equals(map.get("code").toString())){
            // 撤销成功
            logger.info("revokeActivi success, processInstanceId is {}", processInstanceId);
            this.revokeActiviSuccess(map);
        }else{
            logger.warn("revokeActivi failed, processInstanceId is {}", processInstanceId);
        }
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    protected abstract void revokeActiviSuccess(Map<String, Object> map) throws Exception;

    /**
     * 在工作流中编辑申请
     *
     * @param id 需要提交到工作流的主单据id
     * @throws Exception
     */
    public void editApplyMationInActiviti(String id) throws Exception{
        Map<String, Object> data = this.submitToActiviGetDate(id);
        Map<String, Object> mm = this.transform(data);
        mm.put("createName", data.get("userName"));
        runtimeService.setVariable(data.get("processInstanceId").toString(), "baseTask", mm);
    }

}
