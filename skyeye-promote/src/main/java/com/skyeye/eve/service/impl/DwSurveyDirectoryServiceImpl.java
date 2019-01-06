package com.skyeye.eve.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.constans.CheckType;
import com.skyeye.common.constans.QuType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.DwSurveyDirectoryDao;
import com.skyeye.eve.service.DwSurveyDirectoryService;


@Service
public class DwSurveyDirectoryServiceImpl implements DwSurveyDirectoryService{
	
	@Autowired
	private DwSurveyDirectoryDao dwSurveyDirectoryDao;

	/**
	 * 
	     * @Title: queryDwSurveyDirectoryList
	     * @Description: 获取调查问卷列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDwSurveyDirectoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryDwSurveyDirectoryList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertDwSurveyDirectoryMation
	     * @Description: 新增调查问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertDwSurveyDirectoryMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("sId", ToolUtil.randomStr(6, 12));//用于短链接的ID
		map.put("dirType", 2);//2问卷
		map.put("surveyModel", 1);//问卷所属的问卷模块   1问卷模块
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		map.put("surveyNote", "非常感谢您的参与！如有涉及个人信息，我们将严格保密。");
		dwSurveyDirectoryDao.insertDwSurveyDirectoryMation(map);
	}
	
	/**
	 * 
	     * @Title: queryDwSurveyDirectoryMationById
	     * @Description: 获取调查问卷题目信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDwSurveyDirectoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> questions = dwSurveyDirectoryDao.queryQuestionListByBelongId(map);//获取问卷中的题
		for(Map<String, Object> question : questions){
			getQuestionOptionListMation(question);
		}
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		outputObject.setBean(surveyMation);
		outputObject.setBeans(questions);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: getQuestionOptionListMation
	     * @Description: 获取问题项
	     * @param @param question
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public Map<String, Object> getQuestionOptionListMation(Map<String, Object> question) throws Exception {
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));//获取题目类型
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
			List<Map<String, Object>> questionRadio = dwSurveyDirectoryDao.queryQuestionRadioListByQuestionId(question);//获取多行填空题
			question.put("questionRadio", questionRadio);
		} else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
			List<Map<String, Object>> questionCheckBox = dwSurveyDirectoryDao.queryQuestionCheckBoxListByQuestionId(question);//获取多选题
			question.put("questionCheckBox", questionCheckBox);
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
			List<Map<String, Object>> questionMultiFillBlank = dwSurveyDirectoryDao.queryQuestionMultiFillBlankListByQuestionId(question);//获取多行填空题
			question.put("questionMultiFillBlank", questionMultiFillBlank);
		} else if (quType.equals(QuType.BIGQU.getActionName())) {
			List<Map<String, Object>> childQuestions = dwSurveyDirectoryDao.queryChildQuestionListByBelongId(question);//获取问卷中的题
			for(Map<String, Object> item : childQuestions){
				getQuestionOptionListMation(item);
			}
			question.put("option", childQuestions);
		} else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) || quType.equals(QuType.CHENSCORE.getActionName())
				|| quType.equals(QuType.CHENFBK.getActionName()) || quType.equals(QuType.COMPCHENRADIO.getActionName())) {// 矩阵单选，矩阵多选，矩阵填空题，复合矩阵单选
			List<Map<String, Object>> questionChenRow = dwSurveyDirectoryDao.queryQuestionChenRowListByQuestionId(question);//获取行选项
			List<Map<String, Object>> questionChenColumn = dwSurveyDirectoryDao.queryQuestionChenColumnListByQuestionId(question);//获取列选项
			for(Map<String, Object> bean : questionChenRow){
				bean.put("questionChenColumn", questionChenColumn);
			}
			question.put("questionChenRow", questionChenRow);
			question.put("questionChenColumn", questionChenColumn);
			if(quType.equals(QuType.COMPCHENRADIO.getActionName())){//如果是复合矩阵单选题， 则还有题选项
				List<Map<String, Object>> questionChenOption = dwSurveyDirectoryDao.queryQuestionChenOptionListByQuestionId(question);//获取选项
				question.put("questionChenOption", questionChenOption);
			}
		} else if (quType.equals(QuType.SCORE.getActionName())) {
			List<Map<String, Object>> questionScore = dwSurveyDirectoryDao.queryQuestionScoreListByQuestionId(question);//获取评分题
			question.put("quScores", questionScore);
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {
			List<Map<String, Object>> questionOrderBy = dwSurveyDirectoryDao.queryQuestionOrderByListByQuestionId(question);//获取排序题
			question.put("questionOrderBy", questionOrderBy);
		}
		List<Map<String, Object>> questionLogic = dwSurveyDirectoryDao.queryQuestionLogicListByQuestionId(question);// 获取逻辑信息
		question.put("questionLogic", questionLogic);
		return question;
	}

	/**
	 * 
	     * @Title: queryDwSurveyMationById
	     * @Description: 获取调查问卷信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDwSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		outputObject.setBean(surveyMation);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editDwSurveyMationById
	     * @Description: 编辑调查问卷信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDwSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dwSurveyDirectoryDao.editDwSurveyMationById(map);//编辑问卷信息
	}

	/**
	 * 
	     * @Title: addQuFillblankMation
	     * @Description: 添加填空题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.FILLBLANK.getIndex());
		String quId = "";
		if(!ToolUtil.isNumeric(map.get("checkType").toString()))
			map.put("checkType", CheckType.valueOf(map.get("checkType").toString()).getIndex());
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuScoreMation
	     * @Description: 添加评分题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.SCORE.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray score = JSONArray.fromObject(map.get("scoreTd").toString());//获取模板绑定信息
		if(score.size() > 0){
			List<Map<String, Object>> quScore = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < score.size(); i++){
				JSONObject object = (JSONObject) score.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quScore.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionScoreMationList(quScore);
			map.put("quItems", quScore);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("geLe", object.getString("geLe"));
				bean.put("scoreNum", object.getString("scoreNum"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuOrderquMation
	     * @Description: 添加排序题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuOrderquMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.ORDERQU.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray orderqu = JSONArray.fromObject(map.get("orderquTd").toString());//获取模板绑定信息
		if(orderqu.size() > 0){
			List<Map<String, Object>> quOrderqu = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < orderqu.size(); i++){
				JSONObject object = (JSONObject) orderqu.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quOrderqu.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionOrderquMationList(quOrderqu);
			map.put("quItems", quOrderqu);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuPagetagMation
	     * @Description: 添加分页标记
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuPagetagMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.PAGETAG.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuRadioMation
	     * @Description: 添加单选题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.RADIO.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray radio = JSONArray.fromObject(map.get("radioTd").toString());//获取模板绑定信息
		if(radio.size() > 0){
			List<Map<String, Object>> quRadio = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < radio.size(); i++){
				JSONObject object = (JSONObject) radio.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("isNote", object.getString("isNote"));
				bean.put("checkType", CheckType.valueOf(object.get("checkType").toString()).getIndex());
				bean.put("isRequiredFill", object.getString("isRequiredFill"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quRadio.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionRadioMationList(quRadio);
			map.put("quItems", quRadio);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuCheckBoxMation
	     * @Description: 添加多选题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.CHECKBOX.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray checkbox = JSONArray.fromObject(map.get("checkboxTd").toString());//获取模板绑定信息
		if(checkbox.size() > 0){
			List<Map<String, Object>> quCheckbox = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < checkbox.size(); i++){
				JSONObject object = (JSONObject) checkbox.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("isNote", object.getString("isNote"));
				bean.put("checkType", CheckType.valueOf(object.get("checkType").toString()).getIndex());
				bean.put("isRequiredFill", object.getString("isRequiredFill"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quCheckbox.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionCheckBoxMationList(quCheckbox);
			map.put("quItems", quCheckbox);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuMultiFillblankMation
	     * @Description: 添加多选填空题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuMultiFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.MULTIFILLBLANK.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray multiFillblank = JSONArray.fromObject(map.get("multiFillblankTd").toString());//获取模板绑定信息
		if(multiFillblank.size() > 0){
			List<Map<String, Object>> quMultiFillblank = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < multiFillblank.size(); i++){
				JSONObject object = (JSONObject) multiFillblank.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quMultiFillblank.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionMultiFillblankMationList(quMultiFillblank);
			map.put("quItems", quMultiFillblank);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuParagraphMation
	     * @Description: 添加段落题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuParagraphMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("quType", QuType.PARAGRAPH.getIndex());
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: addQuChenMation
	     * @Description: 添加矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		int quType = QuType.getIndex(map.get("quType").toString());
		if(-1 == quType){
			outputObject.setreturnMessage("参数值错误！");
			return;
		}else{
			map.put("quType", quType);
		}
		String quId = "";
		if(ToolUtil.isBlank(map.get("quId").toString())){
			quId = ToolUtil.getSurFaceId();
			map.put("id", quId);
			map.put("quTag", 1);
			map.put("visibility", 1);
			map.put("createTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.addQuestionMation(map);
		}else{
			quId = map.get("quId").toString();
			dwSurveyDirectoryDao.editQuestionMationById(map);
		}
		
		JSONArray column = JSONArray.fromObject(map.get("column").toString());//获取模板绑定信息
		if(column.size() > 0){
			List<Map<String, Object>> quColumn = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < column.size(); i++){
				JSONObject object = (JSONObject) column.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quColumn.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionColumnMationList(quColumn);
			map.put("quColumnItems", quColumn);
		}
		
		JSONArray row = JSONArray.fromObject(map.get("row").toString());//获取模板绑定信息
		if(row.size() > 0){
			List<Map<String, Object>> quRow = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < row.size(); i++){
				JSONObject object = (JSONObject) row.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("optionId", object.getString("optionId"));
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("quId", quId);
				bean.put("visibility", 1);
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				quRow.add(bean);
			}
			dwSurveyDirectoryDao.addQuestionRowMationList(quRow);
			map.put("quRowItems", quRow);
		}
		
		JSONArray array = JSONArray.fromObject(map.get("logic").toString());//获取模板绑定信息
		if(array.size() > 0){
			List<Map<String, Object>> quLogics = new ArrayList<>();
			List<Map<String, Object>> editquLogics = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("cgQuItemId", object.getString("cgQuItemId"));
				bean.put("skQuId", object.getString("skQuId"));
				bean.put("logicType", object.getString("logicType"));
				bean.put("title", object.getString("key"));
				bean.put("ckQuId", quId);
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("visibility", object.getString("visibility"));
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					bean.put("id", ToolUtil.getSurFaceId());
					quLogics.add(bean);
				}else{
					bean.put("id", object.getString("quLogicId"));
					editquLogics.add(bean);
				}
			}
			if(!quLogics.isEmpty())
				dwSurveyDirectoryDao.addQuestionLogicsMationList(quLogics);
			if(!editquLogics.isEmpty())
				dwSurveyDirectoryDao.editQuestionLogicsMationList(editquLogics);
			quLogics.addAll(editquLogics);
			map.put("quLogics", quLogics);
		}
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: deleteQuestionMationById
	     * @Description: 删除问题
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> question = dwSurveyDirectoryDao.queryQuestionMationById(map);
		if(question != null){
			if(question.get("surveyState").toString().equals("0")){//设计状态
				String tableName = QuType.getTableName(Integer.parseInt(question.get("quType").toString()));
				if(!ToolUtil.isBlank(tableName)){
					String[] questionId = QuType.getQuestionId(Integer.parseInt(question.get("quType").toString())).split(",");
					String[] str = tableName.split(",");
					for(int i = 0; i < str.length; i++){
						map.put("tableName", str[i]);
						map.put("key", questionId[i]);
						dwSurveyDirectoryDao.deleteQuestionOptionMationByQuId(map);
					}
				}
				dwSurveyDirectoryDao.deleteQuestionMationById(map);//执行物理删除问题
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionMationById(map);//执行逻辑删除问题
			}
			dwSurveyDirectoryDao.updateQuestionOrderByIdByQuId(question);//修改该问题排序后面的序号减1
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionChenColumnMationById
	     * @Description: 删除矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题列选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionChenColumnMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionChenColumnById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionChenColumnMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionChenColumnMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionChenRowMationById
	     * @Description: 删除矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题行选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionChenRowMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionChenRowById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionChenRowMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionChenRowMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionRadioOptionMationById
	     * @Description: 删除单选题选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionRadioOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionRadioOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionRadioOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionRadioOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionChedkBoxOptionMationById
	     * @Description: 删除多选题选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionChedkBoxOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionChedkBoxOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionChedkBoxOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionChedkBoxOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionScoreOptionMationById
	     * @Description: 删除评分题选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionScoreOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionScoreOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionScoreOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionScoreOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionOrderOptionMationById
	     * @Description: 删除排序选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionOrderOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionOrderOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionOrderOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionOrderOptionMationById(map);//执行逻辑删除问题
			}
		}
	}

	/**
	 * 
	     * @Title: deleteQuestionMultiFillblankOptionMationById
	     * @Description: 删除多项填空题选项
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteQuestionMultiFillblankOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> option = dwSurveyDirectoryDao.queryQuestionMultiFillblankOptionById(map);
		if(option != null){
			if(option.get("surveyState").toString().equals("0")){//设计状态
				dwSurveyDirectoryDao.deleteQuestionMultiFillblankOptionMationById(map);//执行物理删除
			}else{//执行中或者结束
				dwSurveyDirectoryDao.deleteLogicQuestionMultiFillblankOptionMationById(map);//执行逻辑删除问题
			}
		}
	}
	
}
