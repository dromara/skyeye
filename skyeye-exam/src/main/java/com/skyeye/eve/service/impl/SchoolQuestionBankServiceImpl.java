/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CheckType;
import com.skyeye.common.constans.QuType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SchoolQuestionBankDao;
import com.skyeye.eve.service.SchoolQuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: SchoolQuestionBankServiceImpl
 * @Description: 学校题库管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:50
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SchoolQuestionBankServiceImpl implements SchoolQuestionBankService{
	
	@Autowired
	private SchoolQuestionBankDao schoolQuestionBankDao;

	/**
	 * 
	     * @Title: querySchoolQuestionBankMationList
	     * @Description: 获取我的题库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolQuestionBankMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolQuestionBankDao.querySchoolQuestionBankMationList(map);
		beans.stream().forEach(bean -> {
			bean.put("cName", QuType.getCName(Integer.parseInt(bean.get("quType").toString())));
		});
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 编辑题目信息
	 * map-> belongId:试卷id
	 * 		 quTitle:问题标题
	 * 		 orderById:序号
	 * 		 tag:表示题目是试卷题还是题库中题
	 * 		 answerInputWidth:填空的input宽度
	 * 		 answerInputRow:填空的input行
	 * 		 contactsAttr:1关联到联系人属性  0不关联到联系人属性
	 * 		 contactsField:关联的联系人字段
	 * 		 checkType:说明的验证方式
	 * 		 hv:1水平显示 2垂直显示
	 * 		 randOrder:选项随机排列  1随机排列 0不随机排列
	 * 		 cellCount:按列显示时，列数
	 * 		 fraction:分数
	 * 		 quId:问题id------非必填
	 * @return
	 * @throws Exception 
	 */
	private String compileQuestion(Map<String, Object> question, String userId) throws Exception{
		String quId = "";
		//判断题目id是否为空，为空则新增，不为空则修改
		if(ToolUtil.isBlank(question.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			question.put("id", quId);
			question.put("quTag", 1);
			question.put("visibility", 1);
			question.put("createId", userId);
			question.put("createTime", DateUtil.getTimeAndToString());
			schoolQuestionBankDao.addQuestionMation(question);
		}else{
			quId = question.get("quId").toString();
			schoolQuestionBankDao.editQuestionMationById(question);
		}
		return quId;
	}
	
	/**
	 * 操作问题和知识点的绑定信息
	 * @param schoolKnowledgeMationList
	 * @param questionId
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void operatorQuestionAndKnowledge(String schoolKnowledgeMationList, String questionId) throws Exception{
		List<Map<String, Object>> beans = JSONUtil.toList(schoolKnowledgeMationList, null);
		List<Map<String, Object>> items = new ArrayList<>();
		beans.stream().forEach(bean->{
			if(bean.containsKey("id") && !ToolUtil.isBlank(bean.get("id").toString())){
				bean.put("questionId", questionId);
				items.add(bean);
			}
		});
		//删除之前的绑定关系
		schoolQuestionBankDao.deleteOldBindingByQuId(questionId);
		if(!items.isEmpty()){
			schoolQuestionBankDao.insertNewBinding(items);
		}
	}
	
	/**
	 * 
	    * @Title: getKnowledgeListBuQuId
	    * @Description: 根据题目id获取知识点
	    * @param quId 提米id
	    * @param @return
	    * @throws Exception    参数
	    * @return List<Map<String,Object>>    返回类型
	    * @throws
	 */
	private List<Map<String, Object>> getKnowledgeListBuQuId(String quId) throws Exception{
		List<Map<String, Object>> knowledgeList = schoolQuestionBankDao.queryQuestionKnowledgeByQuestionId(quId);
		return knowledgeList;
	}

	/**
	 * 
	     * @Title: addQuRadioMation
	     * @Description: 新增单选题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> radio = JSONUtil.toList(map.get("radioTd").toString(), null);//获取模板绑定信息
		if(radio.size() > 0){
			map.put("quType", QuType.RADIO.getIndex());
			Map<String, Object> user = inputObject.getLogParams();
			//添加问题并返回问题id
			String quId = compileQuestion(map, user.get("id").toString());
			//知识点关联
			operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
			
			List<Map<String, Object>> quRadio = new ArrayList<>();
			List<Map<String, Object>> editquRadio = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < radio.size(); i++){
				Map<String, Object> object = radio.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				bean.put("isNote", object.get("isNote"));
				bean.put("isDefaultAnswer", object.get("isDefaultAnswer"));
				if(!ToolUtil.isNumeric(object.get("checkType").toString()))
					bean.put("checkType", CheckType.valueOf(object.get("checkType").toString()).getIndex());
				else
					bean.put("checkType", object.get("checkType").toString());
				bean.put("isRequiredFill", object.get("isRequiredFill"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quRadio.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquRadio.add(bean);
				}
			}
			
			//删除要删除的问题项
			List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
			if(!deleteRowList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionRadioOptionMationList(deleteRowList);
			}
			
			if(!quRadio.isEmpty())
				schoolQuestionBankDao.addQuestionRadioMationList(quRadio);
			if(!editquRadio.isEmpty())
				schoolQuestionBankDao.editQuestionRadioMationList(editquRadio);
		}else{
			outputObject.setreturnMessage("选项不能为空");
		}
	}

	/**
	 * 
	     * @Title: deleteSchoolQuestionBankMationById
	     * @Description: 删除我的题目信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteSchoolQuestionBankMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("createId", inputObject.getLogParams().get("id"));
		schoolQuestionBankDao.deleteSchoolQuestionBankMationById(map);
	}

	/**
	 * 
	     * @Title: queryQuRadioMationToEditById
	     * @Description: 编辑单选题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuRadioMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		//获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			//获取选项
			List<Map<String, Object>> questionRadio = schoolQuestionBankDao.queryQuestionRadioListByQuestionId(question);
			question.put("questionRadio", questionRadio);
			//获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: addQuCheckBoxMation
	     * @Description: 新增多选题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> checkbox = JSONUtil.toList(map.get("checkboxTd").toString(), null);//获取模板绑定信息
		if(checkbox.size() > 0){
			map.put("quType", QuType.CHECKBOX.getIndex());
			Map<String, Object> user = inputObject.getLogParams();
			//添加问题并返回问题id
			String quId = compileQuestion(map, user.get("id").toString());
			//知识点关联
			operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
			
			List<Map<String, Object>> quCheckbox = new ArrayList<>();
			List<Map<String, Object>> editquCheckbox = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < checkbox.size(); i++){
				Map<String, Object> object = checkbox.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				bean.put("isNote", object.get("isNote"));
				bean.put("isDefaultAnswer", object.get("isDefaultAnswer"));
				if(!ToolUtil.isNumeric(object.get("checkType").toString()))
					bean.put("checkType", CheckType.valueOf(object.get("checkType").toString()).getIndex());
				else
					bean.put("checkType", object.get("checkType").toString());
				bean.put("isRequiredFill", object.get("isRequiredFill"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quCheckbox.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquCheckbox.add(bean);
				}
			}
			
			//删除要删除的问题项
			List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
			if(!deleteRowList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionCheckBoxOptionMationList(deleteRowList);
			}
			if(!quCheckbox.isEmpty())
				schoolQuestionBankDao.addQuestionCheckBoxMationList(quCheckbox);
			if(!editquCheckbox.isEmpty())
				schoolQuestionBankDao.editQuestionCheckBoxMationList(editquCheckbox);
		}else{
			outputObject.setreturnMessage("选项不能为空");
		}
	}

	/**
	 * 
	     * @Title: queryQuCheckBoxMationToEditById
	     * @Description: 编辑多选题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuCheckBoxMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		//获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			//获取选项
			List<Map<String, Object>> questionCheckBox = schoolQuestionBankDao.queryQuestionCheckBoxListByQuestionId(question);
			question.put("questionCheckBox", questionCheckBox);
			//获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: addQuFillblankMation
	     * @Description: 新增填空题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.FILLBLANK.getIndex());
		if(!ToolUtil.isNumeric(map.get("checkType").toString()))
			map.put("checkType", CheckType.valueOf(map.get("checkType").toString()).getIndex());
		Map<String, Object> user = inputObject.getLogParams();
		//添加问题并返回问题id
		String quId = compileQuestion(map, user.get("id").toString());
		//知识点关联
		operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
	}

	/**
	 * 
	     * @Title: queryQuFillblankMationToEditById
	     * @Description: 编辑填空题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		//获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			//获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: addQuScoreMation
	     * @Description: 新增评分题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> scoreTd = JSONUtil.toList(map.get("scoreTd").toString(), null);
		if(scoreTd.size() > 0){
			map.put("quType", QuType.SCORE.getIndex());
			Map<String, Object> user = inputObject.getLogParams();
			//添加问题并返回问题id
			String quId = compileQuestion(map, user.get("id").toString());
			//知识点关联
			operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
			
			List<Map<String, Object>> quScore = new ArrayList<>();
			List<Map<String, Object>> editquScore = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < scoreTd.size(); i++){
				Map<String, Object> object = scoreTd.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quScore.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquScore.add(bean);
				}
			}
			
			//删除要删除的问题项
			List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
			if(!deleteRowList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionScoreOptionMationList(deleteRowList);
			}
			if(!quScore.isEmpty())
				schoolQuestionBankDao.addQuestionScoreMationList(quScore);
			if(!editquScore.isEmpty())
				schoolQuestionBankDao.editQuestionScoreMationList(editquScore);
		}else{
			outputObject.setreturnMessage("选项不能为空");
		}
	}

	/**
	 * 
	     * @Title: queryQuScoreMationToEditById
	     * @Description: 编辑评分题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuScoreMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		//获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			//获取选项
			List<Map<String, Object>> questionScore = schoolQuestionBankDao.queryQuestionScoreListByQuestionId(question);//获取评分题
			questionScore.parallelStream().forEach(bean -> {
				String str = "";
				for(int i = 1; i <= Integer.parseInt(bean.get("paramInt02").toString()); i++){
					str += "<td>" + i + "</td>";
				}
				bean.put("showParamInt02", str);
			});
			question.put("quScores", questionScore);
			//获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: addQuOrderbyMation
	     * @Description: 新增排序题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void addQuOrderbyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> orderquTd = JSONUtil.toList(map.get("orderquTd").toString(), null);
		if(orderquTd.size() > 0){
			map.put("quType", QuType.ORDERQU.getIndex());
			Map<String, Object> user = inputObject.getLogParams();
			//添加问题并返回问题id
			String quId = compileQuestion(map, user.get("id").toString());
			//知识点关联
			operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
			
			List<Map<String, Object>> quOrderqu = new ArrayList<>();
			List<Map<String, Object>> editquOrderqu = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < orderquTd.size(); i++){
				Map<String, Object> object = orderquTd.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quOrderqu.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquOrderqu.add(bean);
				}
			}
			
			//删除要删除的问题项
			List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
			if(!deleteRowList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionOrderOptionMationList(deleteRowList);
			}
			if(!quOrderqu.isEmpty())
				schoolQuestionBankDao.addQuestionOrderquMationList(quOrderqu);
			if(!editquOrderqu.isEmpty())
				schoolQuestionBankDao.editQuestionOrderquMationList(editquOrderqu);
		}else{
			outputObject.setreturnMessage("选项不能为空");
		}
	}

	/**
	 * 
	     * @Title: queryQuOrderbyMationToEditById
	     * @Description: 编辑排序题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuOrderbyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		//获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			//获取选项
			List<Map<String, Object>> questionOrderBy = schoolQuestionBankDao.queryQuestionOrderByListByQuestionId(question);
			question.put("questionOrderBy", questionOrderBy);
			//获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: addQuMultiFillblankAddMation
	     * @Description: 新增多项填空题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void addQuMultiFillblankAddMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> multiFillblankTd = JSONUtil.toList(map.get("multiFillblankTd").toString(), null);
		if(multiFillblankTd.size() > 0){
			map.put("quType", QuType.MULTIFILLBLANK.getIndex());
			Map<String, Object> user = inputObject.getLogParams();
			//添加问题并返回问题id
			String quId = compileQuestion(map, user.get("id").toString());
			//知识点关联
			operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
			
			List<Map<String, Object>> quMultiFillblank = new ArrayList<>();
			List<Map<String, Object>> editquMultiFillblank = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < multiFillblankTd.size(); i++){
				Map<String, Object> object = multiFillblankTd.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				bean.put("isDefaultAnswer", object.get("isDefaultAnswer"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quMultiFillblank.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquMultiFillblank.add(bean);
				}
			}
			
			//删除要删除的问题项
			List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
			if(!deleteRowList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionMultiFillblankOptionMationList(deleteRowList);
			}
			if(!quMultiFillblank.isEmpty())
				schoolQuestionBankDao.addQuestionMultiFillblankMationList(quMultiFillblank);
			if(!editquMultiFillblank.isEmpty())
				schoolQuestionBankDao.editQuestionMultiFillblankMationList(editquMultiFillblank);
		}else{
			outputObject.setreturnMessage("选项不能为空");
		}
	}

	/**
	 * 
	     * @Title: queryQuMultiFillblankMationToEditById
	     * @Description: 编辑多项填空题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuMultiFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		// 获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			// 获取选项
			List<Map<String, Object>> questionMultiFillBlank = schoolQuestionBankDao.queryQuestionMultiFillBlankListByQuestionId(question);
			question.put("questionMultiFillBlank", questionMultiFillBlank);
			// 获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: addQuChenMation
	     * @Description: 新增矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> column = JSONUtil.toList(map.get("column").toString(), null);
		List<Map<String, Object>> row = JSONUtil.toList(map.get("row").toString(), null);
		if(column.size() > 0 && row.size() > 0){
			int quType = QuType.getIndex(map.get("quType").toString());
			if(-1 == quType){
				outputObject.setreturnMessage("参数值错误！");
				return;
			}else{
				map.put("quType", quType);
			}
			Map<String, Object> user = inputObject.getLogParams();
			// 添加问题并返回问题id
			String quId = compileQuestion(map, user.get("id").toString());
			// 知识点关联
			operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);
			
			List<Map<String, Object>> quColumn = new ArrayList<>();
			List<Map<String, Object>> editquColumn = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < column.size(); i++){
				Map<String, Object> object = column.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quColumn.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquColumn.add(bean);
				}
			}
			// 删除要删除的问题项
			List<String> deleteColumnList = JSONUtil.toList(map.get("deleteColumnList").toString(), null);
			if(!deleteColumnList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionColumnOptionMationList(deleteColumnList);
			}
			if(!quColumn.isEmpty())
				schoolQuestionBankDao.addQuestionColumnMationList(quColumn);
			if(!editquColumn.isEmpty())
				schoolQuestionBankDao.editQuestionColumnMationList(editquColumn);
			
			List<Map<String, Object>> quRow = new ArrayList<>();
			List<Map<String, Object>> editquRow = new ArrayList<>();
			for(int i = 0; i < row.size(); i++){
				Map<String, Object> object = row.get(i);
				bean = new HashMap<>();
				bean.put("orderById", object.get("key"));
				bean.put("optionName", object.get("optionValue"));
				if(ToolUtil.isBlank(object.get("optionId").toString())){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", DateUtil.getTimeAndToString());
					quRow.add(bean);
				}else{
					bean.put("id", object.get("optionId"));
					editquRow.add(bean);
				}
			}
			// 删除要删除的问题项
			List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
			if(!deleteRowList.isEmpty()){
				schoolQuestionBankDao.deleteQuestionRowOptionMationList(deleteRowList);
			}
			if(!quRow.isEmpty())
				schoolQuestionBankDao.addQuestionRowMationList(quRow);
			if(!editquRow.isEmpty())
				schoolQuestionBankDao.editQuestionRowMationList(editquRow);
		}else{
			outputObject.setreturnMessage("选项不能为空");
		}
	}

	/**
	 * 
	     * @Title: queryQuChenMationToEditById
	     * @Description: 编辑矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题时回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryQuChenMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String quId = map.get("id").toString();
		// 获取题目信息
		Map<String, Object> question = schoolQuestionBankDao.queryQuestionMationById(quId);
		if(question != null && !question.isEmpty()){
			String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
			// 获取选项
			List<Map<String, Object>> questionChenRow = schoolQuestionBankDao.queryQuestionChenRowListByQuestionId(question);// 获取行选项
			List<Map<String, Object>> questionChenColumn = schoolQuestionBankDao.queryQuestionChenColumnListByQuestionId(question);// 获取列选项
			for(Map<String, Object> bean : questionChenRow){
				for(Map<String, Object> item : questionChenColumn){
					item.put("rowId", bean.get("id"));
				}
				bean.put("questionChenColumn", questionChenColumn);
			}
			question.put("questionChenRow", questionChenRow);
			question.put("questionChenColumn", questionChenColumn);
			if(quType.equals(QuType.COMPCHENRADIO.getActionName())){// 如果是复合矩阵单选题， 则还有题选项
				List<Map<String, Object>> questionChenOption = schoolQuestionBankDao.queryQuestionChenOptionListByQuestionId(question);// 获取选项
				question.put("questionChenOption", questionChenOption);
			}
			// 获取知识点
			question.put("knowledgeList", getKnowledgeListBuQuId(quId));
			question.put("quTypeName", quType.toUpperCase());
			outputObject.setBean(question);
		}else{
			outputObject.setreturnMessage("该题目信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: querySchoolQuestionBankMationListToChoose
	     * @Description: 获取题库列表(包含我的私有题库以及所有公开题库)供试卷选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolQuestionBankMationListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolQuestionBankDao.querySchoolQuestionBankMationListToChoose(map);
		beans.stream().forEach(bean -> {
			bean.put("cName", QuType.getCName(Integer.parseInt(bean.get("quType").toString())));
		});
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: querySchoolQuestionBankMationListByIds
	     * @Description: 根据试题id串获取试题详细信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolQuestionBankMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = schoolQuestionBankDao.querySchoolQuestionBankMationListByIds(idsList);
			for(Map<String, Object> question : beans){
				getQuestionOptionListMation(question);
				// 知识点数量回显
				String knowledgeIds = question.containsKey("knowledgeIds") ? question.get("knowledgeIds").toString() : ",";
				question.put("questionKnowledge", knowledgeIds.split(","));
			}
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}
	
	/**
	 * 
	     * @Title: getQuestionOptionListMation
	     * @Description: 获取问题项
	     * @param question
	     * @param @return
	     * @throws Exception    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public void getQuestionOptionListMation(Map<String, Object> question) throws Exception {
		// 获取题目类型
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
		question.put("id", question.get("quInBankId"));
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
			List<Map<String, Object>> questionRadio = schoolQuestionBankDao.queryQuestionRadioListByQuestionId(question);//获取多行填空题
			questionRadio.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			question.put("questionRadio", questionRadio);
		} else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
			List<Map<String, Object>> questionCheckBox = schoolQuestionBankDao.queryQuestionCheckBoxListByQuestionId(question);//获取多选题
			questionCheckBox.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			question.put("questionCheckBox", questionCheckBox);
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
			List<Map<String, Object>> questionMultiFillBlank = schoolQuestionBankDao.queryQuestionMultiFillBlankListByQuestionId(question);//获取多行填空题
			questionMultiFillBlank.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			question.put("questionMultiFillBlank", questionMultiFillBlank);
		} else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) || quType.equals(QuType.CHENSCORE.getActionName())
				|| quType.equals(QuType.CHENFBK.getActionName()) || quType.equals(QuType.COMPCHENRADIO.getActionName())) {
			// 矩阵单选，矩阵多选，矩阵填空题，复合矩阵单选
			List<Map<String, Object>> questionChenRow = schoolQuestionBankDao.queryQuestionChenRowListByQuestionId(question);//获取行选项
			List<Map<String, Object>> questionChenColumn = schoolQuestionBankDao.queryQuestionChenColumnListByQuestionId(question);//获取列选项
			questionChenRow.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			questionChenColumn.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			for(Map<String, Object> bean : questionChenRow){
				bean.put("questionChenColumn", questionChenColumn);
			}
			question.put("questionChenRow", questionChenRow);
			question.put("questionChenColumn", questionChenColumn);
			if(quType.equals(QuType.COMPCHENRADIO.getActionName())){//如果是复合矩阵单选题， 则还有题选项
				List<Map<String, Object>> questionChenOption = schoolQuestionBankDao.queryQuestionChenOptionListByQuestionId(question);//获取选项
				question.put("questionChenOption", questionChenOption);
			}
		} else if (quType.equals(QuType.SCORE.getActionName())) {
			List<Map<String, Object>> questionScore = schoolQuestionBankDao.queryQuestionScoreListByQuestionId(question);//获取评分题
			questionScore.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			question.put("quScores", questionScore);
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {
			List<Map<String, Object>> questionOrderBy = schoolQuestionBankDao.queryQuestionOrderByListByQuestionId(question);//获取排序题
			questionOrderBy.parallelStream().forEach(bean -> {
				bean.remove("id");
			});
			question.put("questionOrderBy", questionOrderBy);
		}
		question.remove("id");
		question.put("questionLogic", new ArrayList<>());
		question.put("quTypeName", quType.toUpperCase());
	}

	/**
	 * 
	     * @Title: querySchoolQuestionBankMationAllList
	     * @Description: 获取所有公共题库以及个人私有题库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolQuestionBankMationAllList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolQuestionBankDao.querySchoolQuestionBankMationAllList(map);
		beans.stream().forEach(bean -> {
			bean.put("cName", QuType.getCName(Integer.parseInt(bean.get("quType").toString())));
		});
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
}
