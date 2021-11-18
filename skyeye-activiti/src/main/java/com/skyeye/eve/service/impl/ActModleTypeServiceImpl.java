/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActModelDao;
import com.skyeye.eve.dao.ActModleTypeDao;
import com.skyeye.eve.dao.DsFormPageDao;
import com.skyeye.eve.service.ActModleTypeService;
import com.skyeye.jedis.JedisClientService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ActModleTypeServiceImpl
 * @Description: 工作流任务配置类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/4 23:20
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ActModleTypeServiceImpl implements ActModleTypeService {

	@Autowired
	private ActModleTypeDao actModleTypeDao;

	@Autowired
	private ActModelDao actModelDao;
	
	@Autowired
	public JedisClientService jedisClient;

	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired  
	private TaskService taskService;//任务服务类。可以从这个类中获取任务的信息
	
	@Autowired  
	private RuntimeService runtimeService;

	@Autowired
	private DsFormPageDao dsFormPageDao;

	public static enum ActModelTypeState{
		START_NEW(1, "新建"),
		START_UP(2, "上线"),
		START_DOWN(3, "下线"),
		START_DELETE(4, "删除");
		private int state;
		private String name;
		ActModelTypeState(int state, String name){
			this.state = state;
			this.name = name;
		}
		public int getState() {
			return state;
		}

		public String getName() {
			return name;
		}
	}

	public static enum ActModelState{
		START_NEW(1, "新建"),
		START_UP(2, "上线"),
		START_DOWN(3, "下线"),
		START_DELETE(4, "删除");
		private int state;
		private String name;
		ActModelState(int state, String name){
			this.state = state;
			this.name = name;
		}
		public int getState() {
			return state;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * 
	     * @Title: insertActModleTypeMation
	     * @Description: 新增申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(map.get("title").toString().length() > 6){
			outputObject.setreturnMessage("申诉类型名称不能超过6个字符，请更换");
		}else{
			Map<String, Object> bean = actModleTypeDao.queryActModleTypeByTitle(map);
			if(bean != null && !bean.isEmpty()){
				outputObject.setreturnMessage("该申诉类型名称已存在，请更换");
			}else{
				Map<String, Object> itemCount = actModleTypeDao.queryCountOrderby(map);
				Map<String, Object> user = inputObject.getLogParams();
				int thisOrderBy = Integer.parseInt(itemCount.get("orderBy").toString()) + 1;
				map.put("orderBy", thisOrderBy);
				map.put("id", ToolUtil.getSurFaceId());
				map.put("state", "1");//默认新建
				map.put("createId", user.get("id"));
				map.put("createTime", DateUtil.getTimeAndToString());
				actModleTypeDao.insertActModleTypeMation(map);
				outputObject.setBean(map);
			}
		}
	}

	/**
	 * 
	     * @Title: selectAllActModleTypeMation
	     * @Description: 遍历所有的申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = actModleTypeDao.selectAllActModleTypeMation(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertActModleByTypeId
	     * @Description: 新增申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertActModleByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = actModelDao.queryActModelMationByNameOrUrl(map);
		if(bean == null || bean.isEmpty()){
			Map<String, Object> itemCount = actModelDao.queryCountOrderbyInModle(map);
			Map<String, Object> user = inputObject.getLogParams();
			int thisOrderBy = Integer.parseInt(itemCount.get("orderBy").toString()) + 1;
			map.put("orderBy", thisOrderBy);
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", ActModelState.START_NEW.getState());//默认新建
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			actModelDao.insertActModle(map);
		}else{
			outputObject.setreturnMessage("名称和指定页面不能重复存在。");
		}
	}

	/**
	 * 
	     * @Title: editActModleTypeNameById
	     * @Description: 编辑申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActModleTypeNameById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModleTypeDao.queryActModleTypeMationById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelTypeState.START_NEW.getState() == state || ActModelTypeState.START_DOWN.getState() == state){
			// 新建或者下线可以编辑
			actModleTypeDao.editActModleTypeNameById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: deleteActModleTypeById
	     * @Description: 删除申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModleTypeDao.queryActModleTypeMationById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelTypeState.START_NEW.getState() == state || ActModelTypeState.START_DOWN.getState() == state){
			// 新建或者下线可以删除
			actModleTypeDao.editActModleTypeStateById(id, ActModelTypeState.START_DELETE.getState());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: deleteActModleById
	     * @Description: 移除申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteActModleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModelDao.queryActModleDetailsById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelState.START_NEW.getState() == state || ActModelState.START_DOWN.getState() == state){
			// 新建或者下线可以移除
			actModelDao.editActModleStateById(id, ActModelState.START_DELETE.getState());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectActModleTypeMation
	     * @Description: 申请类型实体列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = actModelDao.selectActModleTypeMation(map);
		for(Map<String, Object> bean : beans){
			List<ProcessDefinition> pros = repositoryService.createProcessDefinitionQuery().processDefinitionKey(bean.get("actId").toString()).latestVersion().list();
			if(!pros.isEmpty()){
				Model model = repositoryService.createModelQuery().deploymentId(pros.get(0).getDeploymentId()).singleResult();
				if(model != null){
					bean.put("actId", model.getName());
				}else{
					bean.put("actId", "该流程已被删除");
				}
			}else{
				bean.put("actId", "错误流程");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: updateUpActModleById
	     * @Description: 上线发起申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpActModleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModelDao.queryActModleDetailsById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelState.START_NEW.getState() == state || ActModelState.START_DOWN.getState() == state){
			// 新建或者下线可以上线
			actModelDao.editActModleStateById(id, ActModelState.START_UP.getState());
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownActModleById
	     * @Description: 下线发起申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownActModleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModelDao.queryActModleDetailsById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelState.START_UP.getState() == state){
			// 上线状态可以下线
			actModelDao.editActModleStateById(id, ActModelState.START_DOWN.getState());
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectActModleMationById
	     * @Description: 通过id查找对应的发起申请类型实体用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectActModleMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModelDao.queryActModleDetailsById(id);
		List<ProcessDefinition> pros = repositoryService.createProcessDefinitionQuery().processDefinitionKey(bean.get("actId").toString()).latestVersion().list();
		if(!pros.isEmpty()){
			Model model = repositoryService.createModelQuery().deploymentId(pros.get(0).getDeploymentId()).singleResult();
			if(model != null){
				bean.put("actId", model.getName());
			}else{
				bean.put("actId", "错误流程");
			}
		}else{
			bean.put("actId", "错误流程");
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editActModleMationById
	     * @Description: 通过id编辑对应的发起申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActModleMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModelDao.queryActModleDetailsById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelState.START_NEW.getState() == state || ActModelState.START_DOWN.getState() == state){
			// 新建或者下线可以编辑
			bean = actModelDao.queryActModelMationByNameOrUrlAndId(map);
			if(bean == null || bean.isEmpty()){
				actModelDao.editActModleMationById(map);
			}else{
				outputObject.setreturnMessage("名称和指定页面不能重复存在。");
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editActModleMationOrderNumUpById
	     * @Description: 发起申请类型实体上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActModleMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = actModelDao.queryActModleUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型实体已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			actModelDao.editActModleMationOrderNumUpById(map);
			actModelDao.editActModleMationOrderNumUpById(bean);
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}
	}
	
	/**
	 * 
	     * @Title: editActModleMationOrderNumDownById
	     * @Description: 发起申请类型实体下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActModleMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = actModelDao.queryActModleDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型实体已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			actModelDao.editActModleMationOrderNumUpById(map);
			actModelDao.editActModleMationOrderNumUpById(bean);
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}
	}

	/**
	 * 
	     * @Title: updateUpActModleTypeById
	     * @Description: 上线发起申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModleTypeDao.queryActModleTypeMationById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelTypeState.START_NEW.getState() == state || ActModelTypeState.START_DOWN.getState() == state){
			// 新建或者下线可以上线
			actModleTypeDao.editActModleTypeStateById(id, ActModelTypeState.START_UP.getState());
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownActModleTypeById
	     * @Description: 下线发起申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModleTypeDao.queryActModleTypeMationById(id);
		int state = Integer.parseInt(bean.get("state").toString());
		if(ActModelTypeState.START_UP.getState() == state){
			// 上线状态可以下线
			actModleTypeDao.editActModleTypeStateById(id, ActModelTypeState.START_DOWN.getState());
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: editActModleTypeOrderNumUpById
	     * @Description: 发起申请类型上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActModleTypeOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = actModleTypeDao.queryActModleTypeUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			actModleTypeDao.editActModleTypeMationOrderNumUpById(map);
			actModleTypeDao.editActModleTypeMationOrderNumUpById(bean);
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}
	}

	/**
	 * 
	     * @Title: editActModleTypeOrderNumDownById
	     * @Description: 发起申请类型下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActModleTypeOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = actModleTypeDao.queryActModleTypeDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			actModleTypeDao.editActModleTypeMationOrderNumUpById(map);
			actModleTypeDao.editActModleTypeMationOrderNumUpById(bean);
			jedisClient.del(Constants.ACT_MODLE_UP_STATE_LIST);//删除上线发起申请类型实体的redis
		}
	}
	
	/**
	 * 
	     * @Title: queryActModleUpStateByUpStateType
	     * @Description: 获取上线的申请类型下的上线的类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryActModleUpStateByUpStateType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.ACT_MODLE_UP_STATE_LIST))){
			beans = actModleTypeDao.queryActModleUpStateByUpStateType(map);
			for (Map<String, Object> bean : beans){
				List<Map<String, Object>> items = actModelDao.queryActModleUpStateByUpStateTypeId(bean);
				bean.put("child", items);
			}
			jedisClient.set(Constants.ACT_MODLE_UP_STATE_LIST, JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.ACT_MODLE_UP_STATE_LIST), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryAllDsForm
	     * @Description: 获取动态表单用于新增申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllDsForm(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = actModleTypeDao.queryAllDsForm(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: queryDsFormMationToEdit
	     * @Description: 获取动态表单内容用于编辑申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		List<Map<String, Object>> beans = dsFormPageDao.queryDsFormPageDataListBySequenceId(Arrays.asList(id));
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: editDsFormMationBySequenceId
	     * @Description: 编辑申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editDsFormMationBySequenceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String str = map.get("jsonStr").toString();//前端传来的数据json串
		String taskId = map.get("taskId").toString();//任务id
		String processInstanceId = map.get("processInstanceId").toString();//流程id
		if(ToolUtil.isJson(str)){
			List<Map<String, Object>> json = JSONUtil.toList(str, null);
			Map<String, Object> params = (Map<String, Object>) taskService.getVariable(taskId, "baseTask");
			String contentId = "";
			Map<String, Object> cenBean;
		    for(int i = 0; i < json.size(); i++){
				Map<String, Object> jObject = json.get(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
		    	actModleTypeDao.editDsFormMationBySequenceId(jObject);
		    	contentId = jObject.get("contentId").toString();
		    	System.out.println(contentId);
				if(params.containsKey(contentId)){
					cenBean = (Map<String, Object>) params.get(contentId);
					cenBean.put("text", jObject.get("text"));
					cenBean.put("value", jObject.get("value"));
					params.put(contentId, cenBean);
				}
		    }
			runtimeService.setVariable(processInstanceId, "baseTask", params);
		}
	}

	/**
	 * 通过id获取流程任务配置详情
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryActModleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = actModelDao.queryActModleDetailsById(id);
		if(bean != null && !bean.isEmpty()){
			List<ProcessDefinition> pros = repositoryService.createProcessDefinitionQuery().processDefinitionKey(bean.get("actId").toString()).latestVersion().list();
			if(!pros.isEmpty()){
				Model model = repositoryService.createModelQuery().deploymentId(pros.get(0).getDeploymentId()).singleResult();
				if(model != null){
					bean.put("actName", model.getName());
				}else{
					bean.put("actName", "该流程已被删除");
				}
			}else{
				bean.put("actName", "错误流程");
			}
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该流程配置信息不存在.");
		}
	}

	/**
	 * 获取热门的流程任务配置列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryHotActModleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = actModelDao.queryHotActModleDetailsById();
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

}
