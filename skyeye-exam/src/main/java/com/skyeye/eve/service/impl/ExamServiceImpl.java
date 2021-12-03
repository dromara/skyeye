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
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ExamDao;
import com.skyeye.eve.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ExamServiceImpl
 * @Description: 考试管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:50
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ExamServiceImpl implements ExamService{
	
	@Autowired
	private ExamDao examDao;
	
	/**
	 * 
	     * @Title: queryExamList
	     * @Description: 获取所有试卷列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = examDao.queryExamList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: queryMyExamList
	     * @Description: 获取我的试卷列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMyExamList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = examDao.queryMyExamList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertExamMation
	     * @Description: 新增试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertExamMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		
		// 获取当前年级所属的年份
		Map<String, Object> gradeYear = examDao.queryGradeNowYearByGradeId(map);
		if(gradeYear != null && !gradeYear.isEmpty()){
			String examId = ToolUtil.getSurFaceId();
			
			// 试卷信息
			map.put("id", examId);
			map.put("sId", ToolUtil.randomStr(6, 12));// 用于短链接的ID
			map.put("surveyModel", 1);// 试卷所属的试卷模块   1试卷模块
			map.put("createId", user.get("id"));
			map.put("sessionYear", gradeYear.get("sessionYear"));// 哪一届，比如2013
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("surveyNote", "遵守考场纪律，维护知识尊严，谨记做人原则，守望诚信功德。");
			
			// 处理试卷与班级的绑定信息
			String[] propertyIds = map.get("propertyIds").toString().split(",");
			List<Map<String, Object>> beans = new ArrayList<>();
			Map<String, Object> bean = null;
			for(String str : propertyIds){
				if(!ToolUtil.isBlank(str)){
					bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("examId", examId);
					bean.put("classId", str);
					beans.add(bean);
				}
			}
			if(!beans.isEmpty()){
				// 新增试卷信息
				examDao.insertExamMation(map);
				// 新增绑定信息
				examDao.insertExamClassMation(beans);
			}else{
				outputObject.setreturnMessage("请选择考试班级。");
			}
		}else{
			outputObject.setreturnMessage("该年级还未曾分班。");
		}
	}
	
	/**
	 * 
	     * @Title: queryExamMationById
	     * @Description: 获取试卷题目信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = examDao.queryExamMationById(map);//获取试卷信息
		if(surveyMation != null && !surveyMation.isEmpty()){
			// 获取试卷中的题
			List<Map<String, Object>> questions = examDao.queryQuestionListByBelongId(map);
			List<Map<String, Object>> questionLeftList = new ArrayList<>();
			Map<String, Object> questionItem;
			for(Map<String, Object> question : questions){
				questionItem = new HashMap<>();
				questionItem.put("id", question.get("id"));
				questionItem.put("quTitle", question.get("quTitle"));
				questionItem.put("quType", question.get("quType"));
				questionItem.put("fraction", question.get("fraction"));
				questionLeftList.add(questionItem);
				getQuestionOptionListMation(question);
				// 知识点数量回显
				String knowledgeIds = question.containsKey("knowledgeIds") ? question.get("knowledgeIds").toString() : ",";
				if(ToolUtil.isBlank(knowledgeIds)){
					question.put("questionKnowledge", 0);
				}else{
					question.put("questionKnowledge", knowledgeIds.split(",").length);
				}
			}
			surveyMation.put("questionLeftList", questionLeftList);
			outputObject.setBean(surveyMation);
			outputObject.setBeans(questions);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
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
		//获取题目类型
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
			List<Map<String, Object>> questionRadio = examDao.queryQuestionRadioListByQuestionId(question);//获取多行填空题
			question.put("questionRadio", questionRadio);
		} else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
			List<Map<String, Object>> questionCheckBox = examDao.queryQuestionCheckBoxListByQuestionId(question);//获取多选题
			question.put("questionCheckBox", questionCheckBox);
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
			List<Map<String, Object>> questionMultiFillBlank = examDao.queryQuestionMultiFillBlankListByQuestionId(question);//获取多行填空题
			question.put("questionMultiFillBlank", questionMultiFillBlank);
		} else if (quType.equals(QuType.BIGQU.getActionName())) {
			List<Map<String, Object>> childQuestions = examDao.queryChildQuestionListByBelongId(question);//获取试卷中的题
			for(Map<String, Object> item : childQuestions){
				getQuestionOptionListMation(item);
			}
			question.put("option", childQuestions);
		} else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) || quType.equals(QuType.CHENSCORE.getActionName())
				|| quType.equals(QuType.CHENFBK.getActionName()) || quType.equals(QuType.COMPCHENRADIO.getActionName())) {// 矩阵单选，矩阵多选，矩阵填空题，复合矩阵单选
			List<Map<String, Object>> questionChenRow = examDao.queryQuestionChenRowListByQuestionId(question);//获取行选项
			List<Map<String, Object>> questionChenColumn = examDao.queryQuestionChenColumnListByQuestionId(question);//获取列选项
			for(Map<String, Object> bean : questionChenRow){
				for(Map<String, Object> item : questionChenColumn){
					item.put("rowId", bean.get("id"));
				}
				bean.put("questionChenColumn", questionChenColumn);
			}
			question.put("questionChenRow", questionChenRow);
			question.put("questionChenColumn", questionChenColumn);
			if(quType.equals(QuType.COMPCHENRADIO.getActionName())){//如果是复合矩阵单选题， 则还有题选项
				List<Map<String, Object>> questionChenOption = examDao.queryQuestionChenOptionListByQuestionId(question);//获取选项
				question.put("questionChenOption", questionChenOption);
			}
		} else if (quType.equals(QuType.SCORE.getActionName())) {
			List<Map<String, Object>> questionScore = examDao.queryQuestionScoreListByQuestionId(question);//获取评分题
			question.put("quScores", questionScore);
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {
			List<Map<String, Object>> questionOrderBy = examDao.queryQuestionOrderByListByQuestionId(question);//获取排序题
			question.put("questionOrderBy", questionOrderBy);
		}
		List<Map<String, Object>> questionLogic = examDao.queryQuestionLogicListByQuestionId(question);// 获取逻辑信息
		question.put("questionLogic", questionLogic);
		question.put("quTypeName", quType.toUpperCase());
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
	 * 		 logic:逻辑设置json串
	 * 		 fraction:分数
	 * 		 quId:问题id------非必填
	 * @return
	 * @throws Exception 
	 */
	public String compileQuestion(Map<String, Object> question) throws Exception{
		String quId = "";
		// 判断题目id是否为空，为空则新增，不为空则修改
		if(ToolUtil.isBlank(question.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			question.put("id", quId);
			question.put("quTag", 1);
			question.put("visibility", 1);
			// 试题类型，0.默认没有，1.视频，2.音频，3.图片
			question.put("fileType", question.containsKey("fileType") ? question.get("fileType") : 0);
			// 是否允许拍照/上传图片选中，1.是，2.否
			question.put("whetherUpload", question.containsKey("whetherUpload") ? question.get("whetherUpload") : 2);
			question.put("createTime", DateUtil.getTimeAndToString());
			examDao.addQuestionMation(question);
		}else{
			quId = question.get("quId").toString();
			examDao.editQuestionMationById(question);
		}
		return quId;
	}
	
	/**
	 * 设置问题逻辑
	 * @param logicStr
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> setLogics(String quId, String logicStr, String userId) throws Exception{
		//设置问题逻辑
		List<Map<String, Object>> array = JSONUtil.toList(logicStr, null);
		//返回前台的逻辑对象
		List<Map<String, Object>> quLogics = new ArrayList<>();
		if(array.size() > 0){
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> bean;
			for(int i = 0; i < array.size(); i++){
				Map<String, Object> object = array.get(i);
				bean = new HashMap<>();
				bean.put("cgQuItemId", object.get("cgQuItemId"));
				bean.put("skQuId", object.get("skQuId"));
				bean.put("logicType", object.get("logicType"));
				bean.put("title", object.get("key"));
				if(object.containsKey("geLe")){
					bean.put("geLe", object.get("geLe"));
				}
				if(object.containsKey("scoreNum")){
					bean.put("scoreNum", object.get("scoreNum"));
				}
				if(ToolUtil.isBlank(object.get("quLogicId").toString())){
					bean.put("ckQuId", quId);
					bean.put("visibility", object.get("visibility"));
					bean.put("createId", userId);
					bean.put("createTime", DateUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.get("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				examDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				examDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			return quLogics;
		}
		return quLogics;
	}
	
	/**
	 * 
	     * @Title: addQuFillblankMation
	     * @Description: 添加填空题
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
		String checkType = map.get("checkType").toString();
		if("undefined".equals(checkType)){
			map.put("checkType", "");
			checkType = "";
		}
		if(!ToolUtil.isBlank(checkType) && !ToolUtil.isNumeric(checkType))
			map.put("checkType", CheckType.valueOf(map.get("checkType").toString()).getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuScoreMation
	     * @Description: 添加评分题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.SCORE.getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);

		List<Map<String, Object>> score = JSONUtil.toList(map.get("scoreTd").toString(), null);//获取模板绑定信息
		if(score.size() > 0){
			List<Map<String, Object>> quScore = new ArrayList<>();
			List<Map<String, Object>> editquScore = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> bean;
			for(int i = 0; i < score.size(); i++){
				Map<String, Object> object = score.get(i);
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
			if(!quScore.isEmpty())
				examDao.addQuestionScoreMationList(quScore);
			if(!editquScore.isEmpty())
				examDao.editQuestionScoreMationList(editquScore);
			quScore.addAll(editquScore);
			map.put("quItems", quScore);
		}
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuOrderquMation
	     * @Description: 添加排序题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuOrderquMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.ORDERQU.getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);

		List<Map<String, Object>> orderqu = JSONUtil.toList(map.get("orderquTd").toString(), null);//获取模板绑定信息
		if(orderqu.size() > 0){
			List<Map<String, Object>> quOrderqu = new ArrayList<>();
			List<Map<String, Object>> editquOrderqu = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> bean;
			for(int i = 0; i < orderqu.size(); i++){
				Map<String, Object> object = orderqu.get(i);
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
			if(!quOrderqu.isEmpty())
				examDao.addQuestionOrderquMationList(quOrderqu);
			if(!editquOrderqu.isEmpty())
				examDao.editQuestionOrderquMationList(editquOrderqu);
			quOrderqu.addAll(editquOrderqu);
			map.put("quItems", quOrderqu);
		}
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuPagetagMation
	     * @Description: 添加分页标记
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuPagetagMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.PAGETAG.getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuRadioMation
	     * @Description: 添加单选题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.RADIO.getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);

		List<Map<String, Object>> radio = JSONUtil.toList(map.get("radioTd").toString(), null);//获取模板绑定信息
		if(radio.size() > 0){
			List<Map<String, Object>> quRadio = new ArrayList<>();
			List<Map<String, Object>> editquRadio = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
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
			if(!quRadio.isEmpty())
				examDao.addQuestionRadioMationList(quRadio);
			if(!editquRadio.isEmpty())
				examDao.editQuestionRadioMationList(editquRadio);
			quRadio.addAll(editquRadio);
			map.put("quItems", quRadio);
		}
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuCheckBoxMation
	     * @Description: 添加多选题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.CHECKBOX.getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);

		List<Map<String, Object>> checkbox = JSONUtil.toList(map.get("checkboxTd").toString(), null);//获取模板绑定信息
		if(checkbox.size() > 0){
			List<Map<String, Object>> quCheckbox = new ArrayList<>();
			List<Map<String, Object>> editquCheckbox = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
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
			if(!quCheckbox.isEmpty())
				examDao.addQuestionCheckBoxMationList(quCheckbox);
			if(!editquCheckbox.isEmpty())
				examDao.editQuestionCheckBoxMationList(editquCheckbox);
			quCheckbox.addAll(editquCheckbox);
			map.put("quItems", quCheckbox);
		}
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuMultiFillblankMation
	     * @Description: 添加多选填空题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuMultiFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.MULTIFILLBLANK.getIndex());
		//添加问题并返回问题id
		String quId = compileQuestion(map);

		List<Map<String, Object>> multiFillblank = JSONUtil.toList(map.get("multiFillblankTd").toString(), null);//获取模板绑定信息
		if(multiFillblank.size() > 0){
			List<Map<String, Object>> quMultiFillblank = new ArrayList<>();
			List<Map<String, Object>> editquMultiFillblank = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> bean;
			for(int i = 0; i < multiFillblank.size(); i++){
				Map<String, Object> object = multiFillblank.get(i);
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
			if(!quMultiFillblank.isEmpty())
				examDao.addQuestionMultiFillblankMationList(quMultiFillblank);
			if(!editquMultiFillblank.isEmpty())
				examDao.editQuestionMultiFillblankMationList(editquMultiFillblank);
			quMultiFillblank.addAll(editquMultiFillblank);
			map.put("quItems", quMultiFillblank);
		}
		
		// 设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuParagraphMation
	     * @Description: 添加段落
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuParagraphMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.PARAGRAPH.getIndex());
		// 添加问题并返回问题id
		String quId = compileQuestion(map);
		
		// 设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuChenMation
	     * @Description: 添加矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		int quType = QuType.getIndex(map.get("quType").toString());
		if(-1 == quType){
			outputObject.setreturnMessage("参数值错误！");
			return;
		}else{
			map.put("quType", quType);
		}
		// 添加问题并返回问题id
		String quId = compileQuestion(map);
		// 获取模板绑定信息
		List<Map<String, Object>> column = JSONUtil.toList(map.get("column").toString(), null);
		if(column.size() > 0){
			List<Map<String, Object>> quColumn = new ArrayList<>();
			List<Map<String, Object>> editquColumn = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
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
			if(!quColumn.isEmpty())
				examDao.addQuestionColumnMationList(quColumn);
			if(!editquColumn.isEmpty())
				examDao.editQuestionColumnMationList(editquColumn);
			quColumn.addAll(editquColumn);
			map.put("quColumnItems", quColumn);
		}

		List<Map<String, Object>> row = JSONUtil.toList(map.get("row").toString(), null);//获取模板绑定信息
		if(row.size() > 0){
			List<Map<String, Object>> quRow = new ArrayList<>();
			List<Map<String, Object>> editquRow = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> bean;
			for(int i = 0; i < row.size(); i++){
				Map<String, Object>object = row.get(i);
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
			if(!quRow.isEmpty())
				examDao.addQuestionRowMationList(quRow);
			if(!editquRow.isEmpty())
				examDao.editQuestionRowMationList(editquRow);
			quRow.addAll(editquRow);
			map.put("quRowItems", quRow);
		}
		
		//设置问题逻辑
		map.put("quLogics", setLogics(quId, map.get("logic").toString(), inputObject.getLogParams().get("id").toString()));
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: deleteQuestionMationById
	     * @Description: 删除问题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> question = examDao.queryQuestionMationById(map);
		if(question != null){
			if(question.get("surveyState").toString().equals("0")){//设计状态
				String tableName = QuType.getTableName(Integer.parseInt(question.get("quType").toString()));
				if(!ToolUtil.isBlank(tableName)){
					String[] questionId = QuType.getQuestionId(Integer.parseInt(question.get("quType").toString())).split(",");
					String[] str = tableName.split(",");
					for(int i = 0; i < str.length; i++){
						map.put("tableName", str[i]);
						map.put("key", questionId[i]);
						examDao.deleteQuestionOptionMationByQuId(map);
					}
				}
				examDao.deleteQuestionMationById(map);//执行物理删除问题
			}else{//执行中或者结束
				examDao.deleteLogicQuestionMationById(map);//执行逻辑删除问题
			}
			examDao.updateQuestionOrderByIdByQuId(question);//修改该问题排序后面的序号减1
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionChenColumnMationById
	     * @Description: 删除矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题列选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionChenColumnMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionChenColumnById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionChenColumnMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionChenColumnMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionChenRowMationById
	     * @Description: 删除矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题行选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionChenRowMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionChenRowById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionChenRowMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionChenRowMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionRadioOptionMationById
	     * @Description: 删除单选题选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionRadioOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionRadioOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionRadioOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionRadioOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionChedkBoxOptionMationById
	     * @Description: 删除多选题选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionChedkBoxOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionChedkBoxOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionChedkBoxOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionChedkBoxOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionScoreOptionMationById
	     * @Description: 删除评分题选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionScoreOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionScoreOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionScoreOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionScoreOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionOrderOptionMationById
	     * @Description: 删除排序选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionOrderOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionOrderOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionOrderOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionOrderOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionMultiFillblankOptionMationById
	     * @Description: 删除多项填空题选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteQuestionMultiFillblankOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = examDao.queryQuestionMultiFillblankOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				examDao.deleteQuestionMultiFillblankOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				examDao.deleteLogicQuestionMultiFillblankOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: editExamStateToReleaseById
	     * @Description: 发布试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editExamStateToReleaseById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = examDao.queryExamMationById(map);//获取试卷信息
		if(surveyMation != null && !surveyMation.isEmpty()){
			if(!surveyMation.containsKey("className") || ToolUtil.isBlank(surveyMation.get("className").toString())){
				outputObject.setreturnMessage("该试卷未指定班级，无法发布试卷。");
				return;
			}
			if("0".equals(surveyMation.get("surveyState").toString())){//设计状态可以发布试卷
				List<Map<String, Object>> questions = examDao.queryQuestionListByBelongId(map);//获取试卷中的题
				if(!questions.isEmpty() && questions.size() > 0){
					//开始事件
					map.put("startTime", DateUtil.getTimeAndToString());
					//总分数
					int fraction = 0;
					//题目总数
					int questionNum = 0;
					int questionType;
					for(Map<String, Object> question : questions){
						questionType = Integer.parseInt(question.get("quType").toString());
						if(questionType != 16 && questionType != 17){
							fraction += Integer.parseInt(question.get("fraction").toString());
							questionNum++;
						}
					}
					map.put("questionNum", questionNum);
					map.put("fraction", fraction);
					//修改试卷信息
					examDao.editExamStateToReleaseById(map);
				}else{
					outputObject.setreturnMessage("该试卷没有调查项，无法发布试卷。");
				}
			}else{
				outputObject.setreturnMessage("该试卷已发布，请刷新数据。");
			}
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: queryExamMationByIdToHTML
	     * @Description: 获取试卷题目信息用来生成html页面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamMationByIdToHTML(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = examDao.queryExamMationById(map);//获取试卷信息
		if(surveyMation != null && !surveyMation.isEmpty()){
			List<Map<String, Object>> questions = examDao.queryQuestionListByBelongId(map);//获取试卷中的题
			int pageNo = 1;
			for(Map<String, Object> question : questions){
				String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
				if(quType.equals(QuType.PAGETAG.getActionName())){
					pageNo++;
				}
			}
			for(Map<String, Object> question : questions){
				question.put("pageNo", pageNo);
				getQuestionOptionListMation(question);
			}
			
			surveyMation.put("pageNo", pageNo);
			outputObject.setBean(surveyMation);
			outputObject.setBeans(questions);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: deleteExamMationById
	     * @Description: 删除试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteExamMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		examDao.deleteExamMationById(map);//删除试卷
	}

	/**
	 * 
	     * @Title: queryExamFxMationById
	     * @Description: 分析报告试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamFxMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = examDao.queryExamMationById(map);//获取试卷信息
		if(surveyMation != null && !surveyMation.isEmpty()){
			List<Map<String, Object>> questions = examDao.queryQuestionListByBelongId(map);//获取试卷中的题
			for(Map<String, Object> question : questions){
				question.put("quTypeName", QuType.getCName(Integer.parseInt(question.get("quType").toString())));
				getQuestionOptionListMation(question);
				getQuestionOptionReportListMation(question);
			}
			
			outputObject.setBean(surveyMation);
			outputObject.setBeans(questions);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
		}
	}
	
	/**
	 * 
	     * @Title: getQuestionOptionReportListMation
	     * @Description: 统计获取数量
	     * @param question
	     * @param @return
	     * @throws Exception    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public Map<String, Object> getQuestionOptionReportListMation(Map<String, Object> question) throws Exception {
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));//获取题目类型
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {//单选  复合单选
			List<Map<String, Object>> beans = examDao.queryRadioGroupStat(question);
			List<Map<String, Object>> radios = (List<Map<String, Object>>) question.get("questionRadio");
			int count = 0;
			for(Map<String, Object> radio : radios){
				radio.put("anCount", 0);
				for(Map<String, Object> bean : beans){
					if(bean.get("quItemId").toString().equals(radio.get("id").toString())){
						radio.put("anCount", bean.get("count"));
					}
				}
				count += Integer.parseInt(radio.get("anCount").toString());
			}
			for(Map<String, Object> radio : radios){
				radio.put("anAllCount", count);
			}
		} else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {//多选 复合多选
			List<Map<String, Object>> beans = examDao.queryCheckBoxGroupStat(question);
			List<Map<String, Object>> checkBoxs = (List<Map<String, Object>>) question.get("questionCheckBox");
			int count = 0;
			for(Map<String, Object> checkBox : checkBoxs){
				checkBox.put("anCount", 0);
				for(Map<String, Object> bean : beans){
					if(bean.get("quItemId").toString().equals(checkBox.get("id").toString())){
						checkBox.put("anCount", bean.get("count"));
					}
				}
				count += Integer.parseInt(checkBox.get("anCount").toString());
			}
			for(Map<String, Object> checkBox : checkBoxs){
				checkBox.put("anAllCount", count);
			}
		} else if (quType.equals(QuType.FILLBLANK.getActionName())) {//填空题
			Map<String, Object> bean = examDao.queryFillBlankGroupStat(question);
			question.put("rowContent", bean.get("emptyCount"));
			question.put("optionContent", bean.get("blankCount"));
			question.put("anCount", bean.get("blankCount"));
		} else if (quType.equals(QuType.ANSWER.getActionName())) {//多行填空题
			Map<String, Object> bean = examDao.queryAnswerGroupStat(question);
			question.put("rowContent", bean.get("emptyCount"));
			question.put("optionContent", bean.get("blankCount"));
			question.put("anCount", bean.get("blankCount"));
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {//组合填空
			List<Map<String, Object>> beans = examDao.queryMultiFillBlankGroupStat(question);
			List<Map<String, Object>> multiFillBlanks = (List<Map<String, Object>>) question.get("questionMultiFillBlank");
			int count = 0;
			for(Map<String, Object> multiFillBlank : multiFillBlanks){
				multiFillBlank.put("anCount", 0);
				for(Map<String, Object> bean : beans){
					if(bean.get("quItemId").toString().equals(multiFillBlank.get("id").toString())){
						multiFillBlank.put("anCount", bean.get("count"));
					}
				}
				count += Integer.parseInt(multiFillBlank.get("anCount").toString());
			}
			for(Map<String, Object> multiFillBlank : multiFillBlanks){
				multiFillBlank.put("anAllCount", count);
			}
		} else if (quType.equals(QuType.ENUMQU.getActionName())) {//枚举题
			List<Map<String, Object>> beans = examDao.queryEnumQuGroupStat(question);
			if(beans.isEmpty())
				question.put("anCount", 0);
			else
				question.put("anCount", beans.size());
		} else if (quType.equals(QuType.CHENRADIO.getActionName())){//矩阵单选题
			List<Map<String, Object>> beans = examDao.queryChenRadioGroupStat(question);
			List<Map<String, Object>> rows = (List<Map<String, Object>>) question.get("questionChenRow");
			int count = 0;
			for(Map<String, Object> row : rows){
				row.put("anCount", 0);
				for(Map<String, Object> bean : beans){
					if(bean.get("quRowId").toString().equals(row.get("id").toString())){
						row.put("anCount", Integer.parseInt(row.get("anCount").toString()) + Integer.parseInt(bean.get("count").toString()));
					}
				}
				count += Integer.parseInt(row.get("anCount").toString());
			}
			for(Map<String, Object> bean : beans){
				bean.put("anAllCount", count);
				for(Map<String, Object> row : rows){
					if(row.get("id").toString().equals(bean.get("quRowId").toString()))
						bean.put("anCount", row.get("anCount").toString());
				}
			}
			question.put("anChenRadios", beans);
		} else if (quType.equals(QuType.CHENFBK.getActionName())){//矩阵填空题
			List<Map<String, Object>> beans = examDao.queryChenFbkGroupStat(question);
			List<Map<String, Object>> rows = (List<Map<String, Object>>) question.get("questionChenRow");
			int count = 0;
			for(Map<String, Object> row : rows){
				row.put("anCount", 0);
				for(Map<String, Object> bean : beans){
					if(bean.get("quRowId").toString().equals(row.get("id").toString())){
						row.put("anCount", Integer.parseInt(row.get("anCount").toString()) + Integer.parseInt(bean.get("count").toString()));
					}
				}
				count += Integer.parseInt(row.get("anCount").toString());
			}
			for(Map<String, Object> bean : beans){
				bean.put("anAllCount", count);
				for(Map<String, Object> row : rows){
					if(row.get("id").toString().equals(bean.get("quRowId").toString()))
						bean.put("anCount", row.get("anCount").toString());
				}
			}
			question.put("anChenFbks", beans);
		} else if(quType.equals(QuType.CHENCHECKBOX.getActionName())){//矩阵多选题
			List<Map<String, Object>> beans = examDao.queryChenCheckBoxGroupStat(question);
			List<Map<String, Object>> rows = (List<Map<String, Object>>) question.get("questionChenRow");
			int count = 0;
			for(Map<String, Object> row : rows){
				row.put("anCount", 0);
				for(Map<String, Object> bean : beans){
					if(bean.get("quRowId").toString().equals(row.get("id").toString())){
						row.put("anCount", Integer.parseInt(row.get("anCount").toString()) + Integer.parseInt(bean.get("count").toString()));
					}
				}
				count += Integer.parseInt(row.get("anCount").toString());
			}
			for(Map<String, Object> bean : beans){
				bean.put("anAllCount", count);
				for(Map<String, Object> row : rows){
					if(row.get("id").toString().equals(bean.get("quRowId").toString()))
						bean.put("anCount", row.get("anCount").toString());
				}
			}
			question.put("anChenCheckboxs", beans);
		} else if(quType.equals(QuType.CHENSCORE.getActionName())){//矩阵评分题
			List<Map<String, Object>> beans = examDao.queryChenScoreGroupStat(question);
			question.put("anChenScores", beans);
		}else if (quType.equals(QuType.SCORE.getActionName())) {//评分题
			List<Map<String, Object>> beans = examDao.queryScoreGroupStat(question);
			List<Map<String, Object>> scores = (List<Map<String, Object>>) question.get("quScores");
			int count = 0;
			for(Map<String, Object> score : scores){
				score.put("anCount", 0);
				score.put("avgScore", "0.00");
				for(Map<String, Object> bean : beans){
					if(bean.get("quRowId").toString().equals(score.get("id").toString())){
						score.put("anCount", bean.get("count"));
						score.put("avgScore", Float.parseFloat(bean.get("avgScore").toString()));
					}
				}
				count += Integer.parseInt(score.get("anCount").toString());
			}
			for(Map<String, Object> score : scores){
				score.put("anAllCount", count);
			}
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {//排序题
			List<Map<String, Object>> beans = examDao.queryOrderQuGroupStat(question);
			List<Map<String, Object>> orderQus = (List<Map<String, Object>>) question.get("questionOrderBy");
			for(Map<String, Object> bean : beans){
				for(Map<String, Object> orderQu : orderQus){
					if(bean.get("quRowId").toString().equals(orderQu.get("id").toString())){
						orderQu.put("anOrderSum", bean.get("sumOrderNum"));
					}
				}
			}
		}
		return question;
	}

	/**
	 * 
	     * @Title: insertExamMationCopyById
	     * @Description: 复制试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertExamMationCopyById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String surveyId = ToolUtil.getSurFaceId();
		map.put("id", surveyId);
		map.put("sId", ToolUtil.randomStr(6, 12));//用于短链接的ID
		map.put("dirType", 2);//2试卷
		map.put("surveyModel", 1);//试卷所属的试卷模块   1试卷模块
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		//复制试卷
		examDao.insertExamMationCopyById(map);
		List<Map<String, Object>> questions = examDao.queryQuestionMationCopyById(map);
		for(Map<String, Object> question : questions){
			question.put("copyFormId", question.get("id"));
			question.put("id", ToolUtil.getSurFaceId());
			question.put("createTime", DateUtil.getTimeAndToString());
			question.put("belongId", surveyId);
			copyQuestionOptionListMation(question);
		}
		examDao.addQuestionMationCopyByExamId(questions);
	}
	
	/**
	 * 
	     * @Title: copyQuestionOptionListMation
	     * @Description: 复制题目选项
	     * @param question
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void copyQuestionOptionListMation(Map<String, Object> question) throws Exception {
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));//获取题目类型
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
			List<Map<String, Object>> questionRadios = examDao.queryQuestionRadioListByCopyId(question);//获取多行填空题
			for(Map<String, Object> questionRadio : questionRadios){
				questionRadio.put("id", ToolUtil.getSurFaceId());
				questionRadio.put("createTime", DateUtil.getTimeAndToString());
				questionRadio.put("quId", question.get("id"));
			}
			if(!questionRadios.isEmpty())
				examDao.addQuestionRadioMationCopyList(questionRadios);
		} else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
			List<Map<String, Object>> questionCheckBoxs = examDao.queryQuestionCheckBoxListByCopyId(question);//获取多选题
			for(Map<String, Object> questionCheckBox : questionCheckBoxs){
				questionCheckBox.put("id", ToolUtil.getSurFaceId());
				questionCheckBox.put("createTime", DateUtil.getTimeAndToString());
				questionCheckBox.put("quId", question.get("id"));
			}
			if(!questionCheckBoxs.isEmpty())
				examDao.addQuestionCheckBoxMationCopyList(questionCheckBoxs);
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
			List<Map<String, Object>> questionMultiFillBlanks = examDao.queryQuestionMultiFillBlankListByCopyId(question);//获取多行填空题
			for(Map<String, Object> questionMultiFillBlank : questionMultiFillBlanks){
				questionMultiFillBlank.put("id", ToolUtil.getSurFaceId());
				questionMultiFillBlank.put("createTime", DateUtil.getTimeAndToString());
				questionMultiFillBlank.put("quId", question.get("id"));
			}
			if(!questionMultiFillBlanks.isEmpty())
				examDao.addQuestionMultiFillBlankMationCopyList(questionMultiFillBlanks);
		} else if (quType.equals(QuType.BIGQU.getActionName())) {
		} else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) || quType.equals(QuType.CHENSCORE.getActionName())
				|| quType.equals(QuType.CHENFBK.getActionName()) || quType.equals(QuType.COMPCHENRADIO.getActionName())) {// 矩阵单选，矩阵多选，矩阵填空题，复合矩阵单选
			List<Map<String, Object>> questionChenRows = examDao.queryQuestionChenRowListByCopyId(question);//获取行选项
			List<Map<String, Object>> questionChenColumns = examDao.queryQuestionChenColumnListByCopyId(question);//获取列选项
			for(Map<String, Object> questionChenRow : questionChenRows){
				questionChenRow.put("id", ToolUtil.getSurFaceId());
				questionChenRow.put("createTime", DateUtil.getTimeAndToString());
				questionChenRow.put("quId", question.get("id"));
			}
			if(!questionChenRows.isEmpty())
				examDao.addQuestionChenRowMationCopyList(questionChenRows);
			for(Map<String, Object> questionChenColumn : questionChenColumns){
				questionChenColumn.put("id", ToolUtil.getSurFaceId());
				questionChenColumn.put("createTime", DateUtil.getTimeAndToString());
				questionChenColumn.put("quId", question.get("id"));
			}
			if(!questionChenColumns.isEmpty())
				examDao.addQuestionChenColumnMationCopyList(questionChenColumns);
			if(quType.equals(QuType.COMPCHENRADIO.getActionName())){//如果是复合矩阵单选题， 则还有题选项
				List<Map<String, Object>> questionChenOptions = examDao.queryQuestionChenOptionListByCopyId(question);//获取选项
				for(Map<String, Object> questionChenOption : questionChenOptions){
					questionChenOption.put("id", ToolUtil.getSurFaceId());
					questionChenOption.put("createTime", DateUtil.getTimeAndToString());
					questionChenOption.put("quId", question.get("id"));
				}
			}
		} else if (quType.equals(QuType.SCORE.getActionName())) {
			List<Map<String, Object>> questionScores = examDao.queryQuestionScoreListByCopyId(question);//获取评分题
			for(Map<String, Object> questionScore : questionScores){
				questionScore.put("id", ToolUtil.getSurFaceId());
				questionScore.put("createTime", DateUtil.getTimeAndToString());
				questionScore.put("quId", question.get("id"));
			}
			if(!questionScores.isEmpty())
				examDao.addQuestionScoreMationCopyList(questionScores);
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {
			List<Map<String, Object>> questionOrderBys = examDao.queryQuestionOrderByListByCopyId(question);//获取排序题
			for(Map<String, Object> questionOrderBy : questionOrderBys){
				questionOrderBy.put("id", ToolUtil.getSurFaceId());
				questionOrderBy.put("createTime", DateUtil.getTimeAndToString());
				questionOrderBy.put("quId", question.get("id"));
			}
			if(!questionOrderBys.isEmpty())
				examDao.addQuestionOrderByMationCopyList(questionOrderBys);
		}
	}

	/**
	 * 
	     * @Title: queryAnswerExamMationByIp
	     * @Description: 判断此试卷当前的状态
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAnswerExamMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(judgeWhetherExaming(map, inputObject.getLogParams(), outputObject)){
			//获取试卷信息
			Map<String, Object> surveyMation = examDao.queryExamMationById(map);
			outputObject.setBean(surveyMation);
		}
	}

	/**
	 * 
	     * @Title: insertAnswerExamMationByIp
	     * @Description: 用户回答试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	@Transactional(value="transactionManager")
	public void insertAnswerExamMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> data = JSONUtil.toBean(map.get("jsonData").toString(), null);
		map.put("id", data.get("surveyId"));
		if(judgeWhetherExaming(map, inputObject.getLogParams(), outputObject)){
			map.put("ip", ToolUtil.getIpByRequest(inputObject.getRequest()));//获取答卷ip
			
			String ipAddr = map.get("ip").toString();//试卷调查ip
			HttpServletRequest request = inputObject.getRequest();
			Map<String, Map<String, Object>> quMaps = buildSaveExamMap(request, data);
			//试卷答卷信息
			Map<String, Object> surveyAnswer = new HashMap<>();
			String addr = IPSeeker.getCountry(ipAddr);
			surveyAnswer.put("ipAddr", ipAddr);
			surveyAnswer.put("addr", addr);
			surveyAnswer.put("city", IPSeeker.getCurCityByCountry(addr));
			surveyAnswer.put("id", data.get("surveyId"));
			surveyAnswer.put("dataSource", 0);
			surveyAnswer.put("bgAnDate", map.get("bgAnDate"));
			saveAnswer(surveyAnswer, quMaps, inputObject.getLogParams().get("id").toString(), map.get("quAnswerFile").toString());
		}
	}
	
	
	
	/**
	 * 是否可以参加考试
	 * @param map
	 * @param user
	 * @param outputObject
	 * @return
	 * @throws Exception
	 */
	public boolean judgeWhetherExaming(Map<String, Object> map, Map<String, Object> user, OutputObject outputObject) throws Exception{
		Map<String, Object> surveyMation = examDao.queryExamMationById(map);//获取试卷信息
		//是否可以参加考试，true：可以；false：不可以
		boolean yesOrNo = false;
		if(surveyMation != null && !surveyMation.isEmpty()){
			if("1".equals(surveyMation.get("surveyState").toString())){
				//如果当前用户是学生
				if(user.containsKey("studentId") && !ToolUtil.isBlank(user.get("studentId").toString())){
					String studentId = user.get("studentId").toString();
					//1.获取学生当前所在班级信息，判断是否符合考试资格
					Map<String, Object> stuExamAuth = examDao.queryStuExamAuthMationByStuId(studentId, map.get("id").toString());
					if(stuExamAuth != null && !stuExamAuth.isEmpty()){
						//2.判断该学生是否参加过考试
						Map<String, Object> examIng = examDao.queryWhetherExamIngByStuId(studentId, map.get("id").toString());
						if(examIng != null && !examIng.isEmpty()){
							outputObject.setreturnMessage("您已参加过该考试。");
						}else{
							//可以参加考试
							yesOrNo = true;
						}
					}else{
						outputObject.setreturnMessage("您不具备该考试权限。");
					}
				}else{
					outputObject.setreturnMessage("非学生无法参与考试。");
				}
			}else{
				outputObject.setreturnMessage("考试已结束。");
			}
		}else{
			outputObject.setreturnMessage("该考试信息已不存在");
		}
		return yesOrNo;
	}
	
	/**
	 * 
	     * @Title: buildSaveExamMap
	     * @Description: 用户回答试卷时获取key-value值
	     * @param request
	     * @param @return
	     * @throws Exception    参数
	     * @return Map<String,Map<String,Object>>    返回类型
	     * @throws
	 */
	public Map<String, Map<String, Object>> buildSaveExamMap(HttpServletRequest request, Map<String, Object> params) throws Exception {
		// 是非
		Map<String, Map<String, Object>> quMaps = new HashMap<String, Map<String, Object>>();
		Map<String, Object> yesnoMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.YESNO.getIndex() + "_");
		quMaps.put("yesnoMaps", yesnoMaps);
		// 填空
		Map<String, Object> fillblankMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.FILLBLANK.getIndex() + "_");
		quMaps.put("fillblankMaps", fillblankMaps);
		// 多项填空
		//dfillValue；quItemId问题选项id；otherText其他文本信息
		String dfillValue, quItemId, otherText;
		//map中间参数；anCheckbox选项对象；mapRow矩阵题中间对象
		Map<String, Object> map, anCheckbox, mapRow;
		Map<String, Object> dfillblankMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.MULTIFILLBLANK.getIndex() + "_");
		for (String key : dfillblankMaps.keySet()) {
			dfillValue = dfillblankMaps.get(key).toString();
			map = WebUtils.getParametersStartingWith(request, dfillValue);
			dfillblankMaps.put(key, map);
		}
		quMaps.put("multifillblankMaps", dfillblankMaps);
		// 多行填空
		Map<String, Object> answerMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.ANSWER.getIndex() + "_");
		quMaps.put("answerMaps", answerMaps);
		// 复合单选
		Map<String, Object> compRadioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.COMPRADIO.getIndex() + "_");
		Map<String, Object> anRadio;
		for (String key : compRadioMaps.keySet()) {
			quItemId = compRadioMaps.get(key).toString();
			otherText = params.get("text_qu_" + QuType.COMPRADIO.getIndex() + "_" + key + "_" + quItemId).toString();
			anRadio = new HashMap<>();
			anRadio.put("quId", key);
			anRadio.put("quItemId", quItemId);
			anRadio.put("otherText", otherText);
			compRadioMaps.put(key, anRadio);
		}
		quMaps.put("compRadioMaps", compRadioMaps);
		// 复合多选
		Map<String, Object> compCheckboxMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.COMPCHECKBOX.getIndex() + "_");
		for (String key : compCheckboxMaps.keySet()) {
			dfillValue = compCheckboxMaps.get(key).toString();
			map = WebUtils.getParametersStartingWith(request, "tag_" + dfillValue);
			for (String key2 : map.keySet()) {
				quItemId = map.get(key2).toString();
				otherText = params.get("text_" + dfillValue + quItemId).toString();
				anCheckbox = new HashMap<>();
				anCheckbox.put("quItemId", quItemId);
				anCheckbox.put("otherText", otherText);
				map.put(key2, anCheckbox);
			}
			compCheckboxMaps.put(key, map);
		}
		quMaps.put("compCheckboxMaps", compCheckboxMaps);
		// 枚举
		Map<String, Object> enumMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.ENUMQU.getIndex() + "_");
		quMaps.put("enumMaps", enumMaps);
		// 评分题
		Map<String, Object> scoreMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.SCORE.getIndex() + "_");
		for (String key : scoreMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, scoreMaps.get(key).toString());
			scoreMaps.put(key, map);
		}
		quMaps.put("scoreMaps", scoreMaps);
		// 排序
		Map<String, Object> quOrderMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.ORDERQU.getIndex() + "_");
		for (String key : quOrderMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, quOrderMaps.get(key).toString());
			quOrderMaps.put(key, map);
		}
		quMaps.put("quOrderMaps", quOrderMaps);
		
		Map<String, Object> chenRadioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENRADIO.getIndex() + "_");
		for (String key : chenRadioMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, chenRadioMaps.get(key).toString());
			chenRadioMaps.put(key, map);
		}
		quMaps.put("chenRadioMaps", chenRadioMaps);
		
		Map<String, Object> chenCheckboxMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENCHECKBOX.getIndex() + "_");
		for (String key : chenCheckboxMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, chenCheckboxMaps.get(key).toString());
			for (String keyRow : map.keySet()) {
				mapRow = WebUtils.getParametersStartingWith(request, map.get(keyRow).toString());
				map.put(keyRow, mapRow);
			}
			chenCheckboxMaps.put(key, map);
		}
		quMaps.put("chenCheckboxMaps", chenCheckboxMaps);
		
		Map<String, Object> chenScoreMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENSCORE.getIndex() + "_");
		for (String key : chenScoreMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, chenScoreMaps.get(key).toString());
			for (String keyRow : map.keySet()) {
				mapRow = WebUtils.getParametersStartingWith(request, map.get(keyRow).toString());
				map.put(keyRow, mapRow);
			}
			chenScoreMaps.put(key, map);
		}
		quMaps.put("chenScoreMaps", chenScoreMaps);
		
		Map<String, Object> chenFbkMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENFBK.getIndex() + "_");
		for (String key : chenFbkMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, chenFbkMaps.get(key).toString());
			for (String keyRow : map.keySet()) {
				mapRow = WebUtils.getParametersStartingWith(request, map.get(keyRow).toString());
				map.put(keyRow, mapRow);
			}
			chenFbkMaps.put(key, map);
		}
		quMaps.put("chenFbkMaps", chenFbkMaps);
		// 单选
		Map<String, Object> radioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.RADIO.getIndex() + "_");
		for (String key : radioMaps.keySet()) {
			quItemId = radioMaps.get(key).toString();
			String keyStr = "text_qu_" + QuType.RADIO.getIndex() + "_" + key + "_" + quItemId;
			otherText = !params.containsKey(keyStr) ? "" : params.get(keyStr).toString();
			anRadio = new HashMap<>();
			anRadio.put("quId", key);
			anRadio.put("quItemId", quItemId);
			anRadio.put("otherText", otherText);
			radioMaps.put(key, anRadio);
		}
		quMaps.put("compRadioMaps", radioMaps);
		// 多选
		Map<String, Object> checkboxMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHECKBOX.getIndex() + "_");
		for (String key : checkboxMaps.keySet()) {
			dfillValue = checkboxMaps.get(key).toString();
			map = WebUtils.getParametersStartingWith(request, dfillValue);
			for (String key2 : map.keySet()) {
				quItemId = map.get(key2).toString();
				otherText = params.get("text_" + dfillValue + quItemId).toString();
				anCheckbox = new HashMap<>();
				anCheckbox.put("quItemId", quItemId);
				anCheckbox.put("otherText", otherText);
				map.put(key2, anCheckbox);
			}
			checkboxMaps.put(key, map);
		}
		quMaps.put("compCheckboxMaps", checkboxMaps);
		
		Map<String, Object> chenCompChenRadioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.COMPCHENRADIO.getIndex() + "_");
		for (String key : chenCompChenRadioMaps.keySet()) {
			map = WebUtils.getParametersStartingWith(request, chenCompChenRadioMaps.get(key).toString());
			for (String keyRow : map.keySet()) {
				mapRow = WebUtils.getParametersStartingWith(request, map.get(keyRow).toString());
				map.put(keyRow, mapRow);
			}
			chenCompChenRadioMaps.put(key, map);
		}
		quMaps.put("compChenRadioMaps", chenCompChenRadioMaps);
		
		return quMaps;
	}
	
	/**
	 * 保存答案
	 *
	 * @param surveyAnswer
	 * @param quMaps
	 * @param stuId 答卷学生id
	 * @param quAnswerFile
	 * @throws Exception
	 */
	private void saveAnswer(Map<String, Object> surveyAnswer, Map<String, Map<String, Object>> quMaps, String stuId, String quAnswerFile) throws Exception{
		// 获取试卷信息
		Map<String, Object> surveyMation = examDao.queryExamMationById(surveyAnswer);
		String answerId = ToolUtil.getSurFaceId();
		// 试卷答案
		surveyAnswer.put("bgAnDate", surveyAnswer.get("bgAnDate"));
		surveyAnswer.put("endAnDate", DateUtil.getTimeAndToString());
		surveyAnswer.put("totalTime", DateUtil.getDistanceMinute(surveyAnswer.get("bgAnDate").toString(), surveyAnswer.get("endAnDate").toString()));//分钟
		surveyAnswer.put("answerId", answerId);
		
		int anCount = 0;//回答的题目数，包含多个答案的数量
		// 保存答案
		// 是非题
		Map<String, Object> yesnoMaps = quMaps.get("yesnoMaps");
		anCount += saveAnYesnoMaps(surveyAnswer, yesnoMaps);
		// 单选题
		Map<String, Object> radioMaps = quMaps.get("radioMaps");
		anCount += saveAnRadioMaps(surveyAnswer, radioMaps);
		// 多选题
		Map<String, Object> checkboxMaps = quMaps.get("checkboxMaps");
		anCount += saveAnCheckboxMaps(surveyAnswer, checkboxMaps);
		// 填空题
		Map<String, Object> fillblankMaps = quMaps.get("fillblankMaps");
		anCount += saveAnFillMaps(surveyAnswer, fillblankMaps);
		// 多项填空题
		Map<String, Object> multifillblankMaps = quMaps.get("multifillblankMaps");
		anCount += saveAnMultiFillMaps(surveyAnswer, multifillblankMaps);
		// 问答题
		Map<String, Object> answerMaps = quMaps.get("answerMaps");
		anCount += saveAnAnswerMaps(surveyAnswer, answerMaps);
		// 复合单选题
		Map<String, Object> compRadioMaps = quMaps.get("compRadioMaps");
		anCount += saveCompAnRadioMaps(surveyAnswer, compRadioMaps);
		// 复合多选题
		Map<String, Object> compCheckboxMaps = quMaps.get("compCheckboxMaps");
		anCount += saveCompAnCheckboxMaps(surveyAnswer, compCheckboxMaps);
		// 枚举题
		Map<String, Object> enumMaps = quMaps.get("enumMaps");
		anCount += saveEnumMaps(surveyAnswer, enumMaps);
		// 评分题
		Map<String, Object> scoreMaps = quMaps.get("scoreMaps");
		anCount += saveScoreMaps(surveyAnswer, scoreMaps);
		// 排序题 quOrderMaps
		Map<String, Object> quOrderMaps = quMaps.get("quOrderMaps");
		anCount += saveQuOrderMaps(surveyAnswer, quOrderMaps);
		// 矩阵单选题
		Map<String, Object> chehRadioMaps = quMaps.get("chenRadioMaps");
		anCount += saveChenRadioMaps(surveyAnswer, chehRadioMaps);
		// 矩阵多选题
		Map<String, Object> chehCheckboxMaps = quMaps.get("chenCheckboxMaps");
		anCount += saveChenCheckboxMaps(surveyAnswer, chehCheckboxMaps);
		// 矩阵填空题
		Map<String, Object> chenFbkMaps = quMaps.get("chenFbkMaps");
		anCount += saveChenFbkMaps(surveyAnswer, chenFbkMaps);
		// 复合矩阵单选题
		Map<String, Object> compChehRadioMaps = quMaps.get("compChenRadioMaps");
		anCount += saveCompChehRadioMaps(surveyAnswer, compChehRadioMaps);
		// 矩阵填空题
		Map<String, Object> chenScoreMaps = quMaps.get("chenScoreMaps");
		anCount += saveChenScoreMaps(surveyAnswer, chenScoreMaps);
		
		//回答的题项目数
		surveyAnswer.put("completeItemNum", anCount);
		//是否完成
		surveyAnswer.put("isComplete", 1);
		//是否是有效数据
		surveyAnswer.put("isEffective", 1);
		//答卷学生id
		surveyAnswer.put("stuId", stuId);
		surveyAnswer.put("completeNum", surveyMation.get("surveyQuNum"));
		surveyAnswer.put("quNum", surveyMation.get("surveyQuNum"));
		examDao.insertExamAnswer(surveyAnswer);
		
		// 保存答案中的图片附件信息
		saveAnswerFileUrl(answerId, quAnswerFile, stuId);
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: saveAnswerFileUrl
	    * @Description: 保存答案中的图片附件信息
	    * @param answerId 答案id
	    * @param jsonStr 每道题对应的图片附件
	    * @return void    返回类型
	    * @throws
	 */
	@SuppressWarnings("unchecked")
	private void saveAnswerFileUrl(String answerId, String jsonStr, String stuId) throws Exception{
		if(ToolUtil.isJson(jsonStr)){
			List<Map<String, Object>> jArray = JSONUtil.toList(jsonStr, null);
			List<Map<String, Object>> entitys = new ArrayList<>();
			for(int i = 0; i < jArray.size(); i++){
				Map<String, Object> bean = jArray.get(i);
				bean.put("answerId", answerId);
				bean.put("stuId", stuId);
				entitys.add(bean);
			}
			if(!entitys.isEmpty()){
				examDao.saveAnswerFileUrl(entitys);
			}
		}
	}
	
	/**
	 * 保存是非题答案
	 *
	 * @param surveyAnswer
	 * @param yesnoMaps
	 * @throws Exception
	 */
	public int saveAnYesnoMaps(Map<String, Object> surveyAnswer,Map<String,Object> yesnoMaps) throws Exception{
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		List<Map<String, Object>> beans = new ArrayList<>();
		int answerQuCount = 0;
		if (yesnoMaps != null){
			Map<String, Object> bean;
			for (String key : yesnoMaps.keySet()) {
				answerQuCount++;
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("yesnoAnswer", yesnoMaps.get(key).toString());
				beans.add(bean);
			}
		}
		if(!beans.isEmpty())
			examDao.saveAnYesnoMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存单选题答案
	 *
	 * @param surveyAnswer
	 * @param radioMaps
	 * @throws Exception
	 */
	private int saveAnRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> radioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (radioMaps != null){
			Map<String, Object> bean;
			for (String key : radioMaps.keySet()) {
				answerQuCount++;
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("quItemId", radioMaps.get(key).toString());
				beans.add(bean);
			}
		}
		if(!beans.isEmpty())
			examDao.saveAnRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存多项填空题答案
	 *
	 * @param surveyAnswer
	 * @param dfillMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveAnMultiFillMaps(Map<String, Object> surveyAnswer,Map<String,Object> dfillMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (dfillMaps != null){
			Map<String, Object> map, bean;
			for (String key : dfillMaps.keySet()) {
				map = (Map<String, Object>) dfillMaps.get(key);
				if(map != null && map.size() > 0){
					for (String keyMap : map.keySet()) {
						answerQuCount++;
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("surveyId", surveyId);
						bean.put("surveyAnswerId", surveyAnswerId);
						bean.put("quId", key);
						bean.put("quItemId", keyMap);
						bean.put("answerValue", !map.containsKey(keyMap) ? "" :map.get(keyMap).toString());
						beans.add(bean);
					}
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveAnMultiFillMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存评分题
	 *
	 * @param surveyAnswer
	 * @param scoreMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveScoreMaps(Map<String, Object> surveyAnswer,Map<String,Object> scoreMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (scoreMaps != null){
			Map<String,Object> mapRows, bean;
			for (String key : scoreMaps.keySet()) {
				mapRows = (Map<String, Object>) scoreMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					answerQuCount++;
					bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("rowId", keyRow);
					bean.put("scoreValue", mapRows.get(keyRow).toString());
					beans.add(bean);
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveScoreMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存矩阵多选题
	 *
	 * @param surveyAnswer
	 * @param chenCheckboxMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveChenCheckboxMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenCheckboxMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenCheckboxMaps != null){
			Map<String,Object> mapRows, mapRow, bean;
			for (String key : chenCheckboxMaps.keySet()) {
				mapRows = (Map<String, Object>) chenCheckboxMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					mapRow = (Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("surveyId", surveyId);
						bean.put("surveyAnswerId", surveyAnswerId);
						bean.put("quId", key);
						bean.put("rowId", keyRow);
						bean.put("colId", keyCol);
						beans.add(bean);
					}
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveChenCheckboxMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 复合单选题
	 *
	 * @param surveyAnswer
	 * @param compRadioMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveCompAnRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> compRadioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(compRadioMaps != null){
			Map<String, Object> tempAnRadio, bean;
			for (String key : compRadioMaps.keySet()) {
				answerQuCount++;
				tempAnRadio = (Map<String, Object>) compRadioMaps.get(key);
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("quItemId", tempAnRadio.get("quItemId"));
				bean.put("otherText", tempAnRadio.get("otherText"));
				beans.add(bean);
			}
		}
		if(!beans.isEmpty())
			examDao.saveCompAnRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 复合矩阵单选题
	 *
	 * @param surveyAnswer
	 * @param compChenRadioMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveCompChehRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> compChenRadioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(compChenRadioMaps != null){
			Map<String,Object> mapRows, mapRow, bean;
			for (String key : compChenRadioMaps.keySet()) {
				mapRows = (Map<String, Object>) compChenRadioMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					mapRow = (Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("surveyId", surveyId);
						bean.put("surveyAnswerId", surveyAnswerId);
						bean.put("quId", key);
						bean.put("rowId", keyRow);
						bean.put("colId", keyCol);
						bean.put("optionId", mapRow.get(keyCol).toString());
						beans.add(bean);
					}
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveCompChehRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 矩陈评分题
	 *
	 * @param surveyAnswer
	 * @param chenScoreMaps
	 * @throws Exception
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int saveChenScoreMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenScoreMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenScoreMaps != null){
			Map<String,Object> mapRows, mapRow, bean;
			for (String key : chenScoreMaps.keySet()) {
				mapRows = (Map<String, Object>) chenScoreMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					mapRow = (Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("surveyId", surveyId);
						bean.put("surveyAnswerId", surveyAnswerId);
						bean.put("quId", key);
						bean.put("rowId", keyRow);
						bean.put("colId", keyCol);
						bean.put("answerValue", mapRow.get(keyCol).toString());
						beans.add(bean);
					}
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveChenScoreMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存多选题答案
	 *
	 * @param surveyAnswer
	 * @param checkboxMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveAnCheckboxMaps(Map<String, Object> surveyAnswer,Map<String,Object> checkboxMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(checkboxMaps != null){
			Map<String, Object> map, bean;
			for (String key : checkboxMaps.keySet()) {
				map = (Map<String, Object>) checkboxMaps.get(key);
				for (String keyMap : map.keySet()) {
					answerQuCount++;
					bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("quItemId", map.get(keyMap).toString());
					beans.add(bean);
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveAnCheckboxMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存单项填空题答案
	 *
	 * @param surveyAnswer
	 * @param fillMaps
	 * @throws Exception
	 */
	private int saveAnFillMaps(Map<String, Object> surveyAnswer,Map<String,Object> fillMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(fillMaps != null){
			Map<String, Object> bean;
			for (String key : fillMaps.keySet()) {
				answerQuCount++;
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("answerValue", fillMaps.get(key).toString());
				beans.add(bean);
			}
		}
		if(!beans.isEmpty())
			examDao.saveAnFillMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存判断题答案
	 *
	 * @param surveyAnswer
	 * @param anAnswerMaps
	 * @throws Exception
	 */
	private int saveAnAnswerMaps(Map<String, Object> surveyAnswer,Map<String,Object> anAnswerMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(anAnswerMaps != null){
			Map<String, Object> bean;
			for (String key : anAnswerMaps.keySet()) {
				answerQuCount++;
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("answerValue", anAnswerMaps.get(key).toString());
				beans.add(bean);
			}
		}
		if(!beans.isEmpty())
			examDao.saveAnAnswerMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存复合多选题答案
	 *
	 * @param surveyAnswer
	 * @param compCheckboxMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveCompAnCheckboxMaps(Map<String, Object> surveyAnswer,Map<String,Object> compCheckboxMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(compCheckboxMaps != null){
			Map<String, Object> map, tempAnCheckbox, bean;
			for (String key : compCheckboxMaps.keySet()) {
				map = (Map<String, Object>) compCheckboxMaps.get(key);
				for (String keyMap : map.keySet()) {
					answerQuCount++;
					tempAnCheckbox = (Map<String, Object>) map.get(keyMap);
					bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("quItemId", tempAnCheckbox.get("quItemId"));
					bean.put("otherText", tempAnCheckbox.get("otherText"));
					beans.add(bean);
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveCompAnCheckboxMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存枚举题
	 *
	 * @param surveyAnswer
	 * @param enumMaps
	 * @throws Exception
	 */
	private int saveEnumMaps(Map<String, Object> surveyAnswer,Map<String,Object> enumMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(enumMaps != null){
			Map<String, Object> bean;
			for (String key : enumMaps.keySet()) {
				answerQuCount++;
				bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("quItemNum", Integer.parseInt(key.split("_")[1]));
				bean.put("answerValue", enumMaps.get(key).toString());
				beans.add(bean);
			}
		}
		if(!beans.isEmpty())
			examDao.saveEnumMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存排序题
	 *
	 * @param surveyAnswer
	 * @param quOrderMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveQuOrderMaps(Map<String, Object> surveyAnswer,Map<String,Object> quOrderMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(quOrderMaps != null){
			Map<String,Object> mapRows, bean;
			for (String key : quOrderMaps.keySet()) {
				mapRows = (Map<String, Object>) quOrderMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					answerQuCount++;
					bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("rowId", keyRow);
					bean.put("orderNumValue", mapRows.get(keyRow).toString());
					beans.add(bean);
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveQuOrderMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存矩阵单选题
	 *
	 * @param surveyAnswer
	 * @param chenRadioMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveChenRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenRadioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenRadioMaps != null){
			Map<String,Object> mapRows, bean;
			for (String key : chenRadioMaps.keySet()) {
				mapRows = (Map<String, Object>) chenRadioMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					answerQuCount++;
					bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("rowId", keyRow);
					bean.put("colId", mapRows.get(keyRow).toString());
					beans.add(bean);
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveChenRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存矩阵填空题
	 *
	 * @param surveyAnswer
	 * @param chenFbkMaps
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int saveChenFbkMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenFbkMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenFbkMaps != null){
			Map<String,Object> mapRows, mapRow, bean;
			for (String key : chenFbkMaps.keySet()) {
				mapRows = (Map<String, Object>) chenFbkMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					mapRow = (Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("surveyId", surveyId);
						bean.put("surveyAnswerId", surveyAnswerId);
						bean.put("quId", key);
						bean.put("rowId", keyRow);
						bean.put("colId", keyCol);
						bean.put("answerValue", mapRow.get(keyCol).toString());
						beans.add(bean);
					}
				}
			}
		}
		if(!beans.isEmpty())
			examDao.saveChenFbkMaps(beans);
		return answerQuCount;
	}

	/**
	 * 
	     * @Title: updateExamMationEndById
	     * @Description: 手动结束试卷
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateExamMationEndById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = examDao.queryExamMationById(map);//获取试卷信息
		if(surveyMation != null && !surveyMation.isEmpty()){
			if("1".equals(surveyMation.get("surveyState").toString())){//执行中
				map.put("realEndTime", DateUtil.getTimeAndToString());
				examDao.updateExamMationEndById(map);
			}
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
		}
	}
	
	/**
	 * 获取每道题的答案
	 * @param question
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void getQuestionAnswerListMation(Map<String, Object> question, String answerId) throws Exception {
		// 获取题目类型
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
		String quId = question.get("id").toString();
		// 获取学生上传的图片信息
		Map<String, Object> url = examDao.queryAnswerFileURLByQuIdAndAnswerId(answerId, quId);
		if(url != null && !url.isEmpty()){
			question.put("url", url.containsKey("url") ? url.get("url") : "");
		}else{
			question.put("url", "");
		}
		if (quType.equals(QuType.RADIO.getActionName())) {
			// 单选题
			Map<String, Object> answer = examDao.queryRadioAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.COMPRADIO.getActionName())) {
			// 复合单选题
			
		} else if (quType.equals(QuType.CHECKBOX.getActionName())) {
			// 多选题
			List<Map<String, Object>> answer = examDao.queryCheckBoxAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.COMPCHECKBOX.getActionName())) {
			// 复合多选题
			
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
			// 多项填空题
			List<Map<String, Object>> answer = examDao.queryMultiFillblankAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.BIGQU.getActionName())) {
			// 大题
			
		} else if (quType.equals(QuType.CHENRADIO.getActionName())) {
			// 矩阵单选题
			List<Map<String, Object>> answer = examDao.queryChenRadioAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.CHENCHECKBOX.getActionName())) {
			// 矩阵多选题
			List<Map<String, Object>> answer = examDao.queryChenCheckBoxAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.CHENSCORE.getActionName())) {
			// 矩阵评分题
			List<Map<String, Object>> answer = examDao.queryChenScoreAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.CHENFBK.getActionName())) {
			// 矩阵填空题
			List<Map<String, Object>> answer = examDao.queryChenFbkAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.COMPCHENRADIO.getActionName())) {
			// 复合矩阵单选题
			
		} else if (quType.equals(QuType.SCORE.getActionName())) {
			// 评分题
			List<Map<String, Object>> answer = examDao.queryScoreAnswerById(quId, answerId);
			List<Map<String, Object>> questionScore = (List<Map<String, Object>>) question.get("quScores");
			// 指定选择的分数
			for(Map<String, Object> bean: questionScore){
				bean.put("answserScore", 0);
				for(Map<String, Object> item: answer){
					if(item.get("quRowId").toString().equals(bean.get("id").toString())){
						bean.put("answserScore", item.get("answserScore"));
					}
				}
			}
			question.put("quScores", questionScore);
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {
			// 排序题
			List<Map<String, Object>> answer = examDao.queryOrderQuAnswerById(quId, answerId);
			question.put("answer", answer);
		} else if (quType.equals(QuType.FILLBLANK.getActionName())) {
			// 填空题
			Map<String, Object> answer = examDao.queryFillBlankAnswerById(quId, answerId);
			question.put("answer", answer);
		}
	}

	/**
	 * 
	     * @Title: queryExamAnswerMationByAnswerId
	     * @Description: 获取答卷详情信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamAnswerMationByAnswerId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取试卷信息-根据答卷id
		Map<String, Object> surveyMation = examDao.queryExamMationByAnswerId(map);
		if(surveyMation != null && !surveyMation.isEmpty()){
			surveyMation.put("answerId", map.get("answerId"));
			List<Map<String, Object>> questions = examDao.queryQuestionDetailListByBelongId(surveyMation);//获取试卷中的题
			int pageNo = 1;
			for(Map<String, Object> question : questions){
				String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
				if(quType.equals(QuType.PAGETAG.getActionName())){
					pageNo++;
				}
			}
			for(Map<String, Object> question : questions){
				question.put("pageNo", pageNo);
				getQuestionOptionListMation(question);
				getQuestionAnswerListMation(question, map.get("answerId").toString());
			}
			
			if("2".equals(surveyMation.get("state").toString())){
				//已批阅
				surveyMation.put("markFraction", "<span class='has-fraction kx'>" + surveyMation.get("markFraction").toString() + "</span>");
			}else{
				//待批阅
				surveyMation.put("markFraction", "<span class='has-fraction pg'>" + surveyMation.get("markFraction").toString() + "</span>");
			}
			
			surveyMation.put("pageNo", pageNo);
			outputObject.setBean(surveyMation);
			outputObject.setBeans(questions);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该答卷信息不存在。");
		}
	}
	
	/**
	 * 
	     * @Title: queryExamAnswerMationToMarkingByAnswerId
	     * @Description: 批阅试卷时获取答卷信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamAnswerMationToMarkingByAnswerId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取试卷信息-根据答卷id
		Map<String, Object> surveyMation = examDao.queryExamAnswerMationToMarkingByAnswerId(map);
		if(surveyMation != null && !surveyMation.isEmpty()){
			//获取试卷中的题
			List<Map<String, Object>> questions = examDao.queryQuestionListByBelongId(surveyMation);
			int pageNo = 1;
			for(Map<String, Object> question : questions){
				String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
				if(quType.equals(QuType.PAGETAG.getActionName())){
					pageNo++;
				}
			}
			for(Map<String, Object> question : questions){
				question.put("pageNo", pageNo);
				getQuestionOptionListMation(question);
				getQuestionAnswerListMation(question, map.get("answerId").toString());
			}
			
			surveyMation.put("pageNo", pageNo);
			outputObject.setBean(surveyMation);
			outputObject.setBeans(questions);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该答卷信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: insertExamAnswerResultMation
	     * @Description: 批阅试卷提交答卷结果
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void insertExamAnswerResultMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String arrayStr = map.get("arrayStr").toString();
		if(ToolUtil.isJson(arrayStr)){
			String answerId = map.get("answerId").toString();
			//处理数据
			List<Map<String, Object>> entitys = JSONUtil.toList(arrayStr, null);
			if(entitys.size() == 0){
				outputObject.setreturnMessage("请填写题目所得分");
				return;
			}
			int fraction = 0;
			String entityFraction = "";
			for(Map<String, Object> entity: entitys){
				entityFraction = entity.get("fraction").toString();
				if(!ToolUtil.isBlank(entityFraction)){
					fraction += Integer.parseInt(entityFraction);
				}else{
					entity.put("fraction", 0);
				}
			}
			//答卷信息对象
			Map<String, Object> examAnswerMation = new HashMap<>();
			examAnswerMation.put("id", answerId);
			examAnswerMation.put("state", 2);//修改为：是
			examAnswerMation.put("markFraction", fraction);//总分数
			examAnswerMation.put("markPeople", inputObject.getLogParams().get("id"));//阅卷人
			examAnswerMation.put("markStartTime", map.get("markStartTime"));//阅卷开始时间
			examAnswerMation.put("markEndTime", DateUtil.getTimeAndToString());//阅卷结束时间
			examDao.insertAnswerQuMation(entitys);
			examDao.updateExamAnswerMation(examAnswerMation);
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}
	
	/**
	 * 
	     * @Title: queryExamMationDetailById
	     * @Description: 获取试卷详情信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取试卷信息
		Map<String, Object> examMation = examDao.queryExamMationDetailById(map);
		if(examMation != null && !examMation.isEmpty()){
			// 获取试卷中的试题信息
			List<Map<String, Object>> questions = examDao.queryQuestionListByBelongId(examMation);
			int pageNo = 1;
			for(Map<String, Object> question : questions){
				String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
				if(quType.equals(QuType.PAGETAG.getActionName())){
					pageNo++;
				}
			}
			for(Map<String, Object> question : questions){
				question.put("pageNo", pageNo);
				getQuestionOptionListMation(question);
				// 知识点数量回显
				String knowledgeIds = question.containsKey("knowledgeIds") ? question.get("knowledgeIds").toString() : ",";
				if(ToolUtil.isBlank(knowledgeIds)){
					question.put("questionKnowledge", 0);
				}else{
					question.put("questionKnowledge", knowledgeIds.split(",").length);
				}
			}
			// 获取阅卷人信息
			List<Map<String, Object>> markPeople = examDao.queryExamMarkPeopleBySurveyId(map);
			examMation.put("markPeople", markPeople);
			examMation.put("pageNo", pageNo);
			outputObject.setBean(examMation);
			outputObject.setBeans(questions);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
		}
	}
	
	/**
	 * 
	     * @Title: queryExamAnswerMationDetailById
	     * @Description: 获取试卷答题情况信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamAnswerMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = examDao.queryExamAnswerMationDetailById(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: queryExamAndMarkPeopleMationDetailById
	     * @Description: 获取试卷详情信息以及阅卷人信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExamAndMarkPeopleMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取试卷信息
		Map<String, Object> surveyMation = examDao.queryExamMationDetailById(map);
		if(surveyMation != null && !surveyMation.isEmpty()){
			//获取阅卷人信息
			List<Map<String, Object>> markPeople = examDao.queryExamMarkPeopleBySurveyId(map);
			outputObject.setBean(surveyMation);
			outputObject.setBeans(markPeople);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该试卷信息不存在。");
		}
	}

	/**
	 * 
	     * @Title: editMarkPeopleMationDetailById
	     * @Description: 修改阅卷人信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editMarkPeopleMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String arrayStr = map.get("arrayStr").toString();
		if(ToolUtil.isJson(arrayStr)){
			//处理数据
			List<Map<String, Object>> entitys = JSONUtil.toList(arrayStr, null);
			//删除之前的阅卷人数据
			examDao.deleteMarkPeopleMationDetailById(map);
			//新增新的阅卷人数据
			if(!entitys.isEmpty()){
				examDao.insertMarkPeopleMationDetailById(entitys);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

}
