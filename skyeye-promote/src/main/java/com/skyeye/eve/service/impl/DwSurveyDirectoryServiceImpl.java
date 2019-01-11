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
				for(Map<String, Object> item : questionChenColumn){
					item.put("rowId", bean.get("id"));
				}
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
			List<Map<String, Object>> editquScore = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < score.size(); i++){
				JSONObject object = (JSONObject) score.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quScore.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquScore.add(bean);
				}
			}
			if(!quScore.isEmpty())
				dwSurveyDirectoryDao.addQuestionScoreMationList(quScore);
			if(!editquScore.isEmpty())
				dwSurveyDirectoryDao.editQuestionScoreMationList(editquScore);
			quScore.addAll(editquScore);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
			List<Map<String, Object>> editquOrderqu = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < orderqu.size(); i++){
				JSONObject object = (JSONObject) orderqu.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quOrderqu.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquOrderqu.add(bean);
				}
			}
			if(!quOrderqu.isEmpty())
				dwSurveyDirectoryDao.addQuestionOrderquMationList(quOrderqu);
			if(!editquOrderqu.isEmpty())
				dwSurveyDirectoryDao.editQuestionOrderquMationList(editquOrderqu);
			quOrderqu.addAll(editquOrderqu);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
			List<Map<String, Object>> editquRadio = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < radio.size(); i++){
				JSONObject object = (JSONObject) radio.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("isNote", object.getString("isNote"));
				if(!ToolUtil.isNumeric(object.get("checkType").toString()))
					bean.put("checkType", CheckType.valueOf(object.get("checkType").toString()).getIndex());
				else
					bean.put("checkType", object.get("checkType").toString());
				bean.put("isRequiredFill", object.getString("isRequiredFill"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quRadio.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquRadio.add(bean);
				}
			}
			if(!quRadio.isEmpty())
				dwSurveyDirectoryDao.addQuestionRadioMationList(quRadio);
			if(!editquRadio.isEmpty())
				dwSurveyDirectoryDao.editQuestionRadioMationList(editquRadio);
			quRadio.addAll(editquRadio);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
			List<Map<String, Object>> editquCheckbox = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < checkbox.size(); i++){
				JSONObject object = (JSONObject) checkbox.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				bean.put("isNote", object.getString("isNote"));
				if(!ToolUtil.isNumeric(object.get("checkType").toString()))
					bean.put("checkType", CheckType.valueOf(object.get("checkType").toString()).getIndex());
				else
					bean.put("checkType", object.get("checkType").toString());
				bean.put("isRequiredFill", object.getString("isRequiredFill"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quCheckbox.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquCheckbox.add(bean);
				}
			}
			if(!quCheckbox.isEmpty())
				dwSurveyDirectoryDao.addQuestionCheckBoxMationList(quCheckbox);
			if(!editquCheckbox.isEmpty())
				dwSurveyDirectoryDao.editQuestionCheckBoxMationList(editquCheckbox);
			quCheckbox.addAll(editquCheckbox);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
			List<Map<String, Object>> editquMultiFillblank = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < multiFillblank.size(); i++){
				JSONObject object = (JSONObject) multiFillblank.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quMultiFillblank.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquMultiFillblank.add(bean);
				}
			}
			if(!quMultiFillblank.isEmpty())
				dwSurveyDirectoryDao.addQuestionMultiFillblankMationList(quMultiFillblank);
			if(!editquMultiFillblank.isEmpty())
				dwSurveyDirectoryDao.editQuestionMultiFillblankMationList(editquMultiFillblank);
			quMultiFillblank.addAll(editquMultiFillblank);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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
			List<Map<String, Object>> editquColumn = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < column.size(); i++){
				JSONObject object = (JSONObject) column.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quColumn.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquColumn.add(bean);
				}
			}
			if(!quColumn.isEmpty())
				dwSurveyDirectoryDao.addQuestionColumnMationList(quColumn);
			if(!editquColumn.isEmpty())
				dwSurveyDirectoryDao.editQuestionColumnMationList(editquColumn);
			quColumn.addAll(editquColumn);
			map.put("quColumnItems", quColumn);
		}
		
		JSONArray row = JSONArray.fromObject(map.get("row").toString());//获取模板绑定信息
		if(row.size() > 0){
			List<Map<String, Object>> quRow = new ArrayList<>();
			List<Map<String, Object>> editquRow = new ArrayList<>();
			Map<String, Object> user = inputObject.getLogParams();
			for(int i = 0; i < row.size(); i++){
				JSONObject object = (JSONObject) row.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("orderById", object.getString("key"));
				bean.put("optionName", object.getString("optionValue"));
				if(ToolUtil.isBlank(object.getString("optionId"))){
					bean.put("quId", quId);
					bean.put("visibility", 1);
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", user.get("id"));
					bean.put("createTime", ToolUtil.getTimeAndToString());
					quRow.add(bean);
				}else{
					bean.put("id", object.getString("optionId"));
					editquRow.add(bean);
				}
			}
			if(!quRow.isEmpty())
				dwSurveyDirectoryDao.addQuestionRowMationList(quRow);
			if(!editquRow.isEmpty())
				dwSurveyDirectoryDao.editQuestionRowMationList(editquRow);
			quRow.addAll(editquRow);
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
				if(ToolUtil.isBlank(object.getString("quLogicId"))){
					bean.put("ckQuId", quId);
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

	/**
	 * 
	     * @Title: editSurveyStateToReleaseById
	     * @Description: 发布问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSurveyStateToReleaseById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		if("0".equals(surveyMation.get("surveyState").toString())){//设计状态可以发布问卷
			map.put("startTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.editSurveyStateToReleaseById(map);
		}else{
			outputObject.setreturnMessage("该问卷已发布，请刷新数据。");
		}
	}

	/**
	 * 
	     * @Title: queryDwSurveyDirectoryMationByIdToHTML
	     * @Description: 获取调查问卷题目信息用来生成html页面
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDwSurveyDirectoryMationByIdToHTML(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> questions = dwSurveyDirectoryDao.queryQuestionListByBelongId(map);//获取问卷中的题
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
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		surveyMation.put("pageNo", pageNo);
		outputObject.setBean(surveyMation);
		outputObject.setBeans(questions);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: deleteSurveyMationById
	     * @Description: 删除问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dwSurveyDirectoryDao.deleteSurveyMationById(map);//删除问卷
	}

	/**
	 * 
	     * @Title: querySurveyFxMationById
	     * @Description: 分析报告问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySurveyFxMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> questions = dwSurveyDirectoryDao.queryQuestionListByBelongId(map);//获取问卷中的题
		for(Map<String, Object> question : questions){
			question.put("quTypeName", QuType.getCName(Integer.parseInt(question.get("quType").toString())));
			getQuestionOptionListMation(question);
			getQuestionOptionReportListMation(question);
		}
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		outputObject.setBean(surveyMation);
		outputObject.setBeans(questions);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: getQuestionOptionReportListMation
	     * @Description: 统计获取数量
	     * @param @param question
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getQuestionOptionReportListMation(Map<String, Object> question) throws Exception {
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));//获取题目类型
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {//单选  复合单选
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryRadioGroupStat(question);
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
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryCheckBoxGroupStat(question);
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
			Map<String, Object> bean = dwSurveyDirectoryDao.queryFillBlankGroupStat(question);
			question.put("rowContent", bean.get("emptyCount"));
			question.put("optionContent", bean.get("blankCount"));
			question.put("anCount", bean.get("blankCount"));
		} else if (quType.equals(QuType.ANSWER.getActionName())) {//多行填空题
			Map<String, Object> bean = dwSurveyDirectoryDao.queryAnswerGroupStat(question);
			question.put("rowContent", bean.get("emptyCount"));
			question.put("optionContent", bean.get("blankCount"));
			question.put("anCount", bean.get("blankCount"));
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {//组合填空
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryMultiFillBlankGroupStat(question);
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
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryEnumQuGroupStat(question);
			if(beans.isEmpty())
				question.put("anCount", 0);
			else
				question.put("anCount", beans.size());
		} else if (quType.equals(QuType.CHENRADIO.getActionName())){//矩阵单选题
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryChenRadioGroupStat(question);
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
			for(Map<String, Object> row : rows){
				row.put("anAllCount", count);
				List<Map<String, Object>> columns = (List<Map<String, Object>>) row.get("questionChenColumn");
				for(Map<String, Object> column : columns){
					column.put("anChenRadios", beans);
				}
			}
		} else if (quType.equals(QuType.CHENFBK.getActionName())){//矩阵填空题
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryChenFbkGroupStat(question);
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
			for(Map<String, Object> row : rows){
				row.put("anAllCount", count);
				List<Map<String, Object>> columns = (List<Map<String, Object>>) row.get("questionChenColumn");
				for(Map<String, Object> column : columns){
					column.put("anChenFbks", beans);
				}
			}
		} else if(quType.equals(QuType.CHENCHECKBOX.getActionName())){//矩阵多选题
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryChenCheckBoxGroupStat(question);
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
			for(Map<String, Object> row : rows){
				row.put("anAllCount", count);
				List<Map<String, Object>> columns = (List<Map<String, Object>>) row.get("questionChenColumn");
				for(Map<String, Object> column : columns){
					column.put("anChenCheckboxs", beans);
				}
			}
		} else if(quType.equals(QuType.CHENSCORE.getActionName())){//矩阵评分题
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryChenScoreGroupStat(question);
			List<Map<String, Object>> rows = (List<Map<String, Object>>) question.get("questionChenRow");
			for(Map<String, Object> row : rows){
				List<Map<String, Object>> columns = (List<Map<String, Object>>) row.get("questionChenColumn");
				for(Map<String, Object> column : columns){
					column.put("anChenScores", beans);
				}
			}
		}else if (quType.equals(QuType.SCORE.getActionName())) {//评分题
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryScoreGroupStat(question);
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
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryOrderQuGroupStat(question);
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
	     * @Title: insertSurveyMationCopyById
	     * @Description: 复制问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSurveyMationCopyById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String surveyId = ToolUtil.getSurFaceId();
		map.put("id", surveyId);
		map.put("sId", ToolUtil.randomStr(6, 12));//用于短链接的ID
		map.put("dirType", 2);//2问卷
		map.put("surveyModel", 1);//问卷所属的问卷模块   1问卷模块
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		//复制问卷
		dwSurveyDirectoryDao.insertSurveyMationCopyById(map);
		List<Map<String, Object>> questions = dwSurveyDirectoryDao.queryQuestionMationCopyById(map);
		for(Map<String, Object> question : questions){
			question.put("copyFormId", question.get("id"));
			question.put("id", ToolUtil.getSurFaceId());
			question.put("createTime", ToolUtil.getTimeAndToString());
			question.put("belongId", surveyId);
			copyQuestionOptionListMation(question);
		}
		dwSurveyDirectoryDao.addQuestionMationCopyBySurveyId(questions);
	}
	
	/**
	 * 
	     * @Title: copyQuestionOptionListMation
	     * @Description: 复制题目选项
	     * @param @param question
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void copyQuestionOptionListMation(Map<String, Object> question) throws Exception {
		String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));//获取题目类型
		if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
			List<Map<String, Object>> questionRadios = dwSurveyDirectoryDao.queryQuestionRadioListByCopyId(question);//获取多行填空题
			for(Map<String, Object> questionRadio : questionRadios){
				questionRadio.put("id", ToolUtil.getSurFaceId());
				questionRadio.put("createTime", ToolUtil.getTimeAndToString());
				questionRadio.put("quId", question.get("id"));
			}
			if(!questionRadios.isEmpty())
				dwSurveyDirectoryDao.addQuestionRadioMationCopyList(questionRadios);
		} else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
			List<Map<String, Object>> questionCheckBoxs = dwSurveyDirectoryDao.queryQuestionCheckBoxListByCopyId(question);//获取多选题
			for(Map<String, Object> questionCheckBox : questionCheckBoxs){
				questionCheckBox.put("id", ToolUtil.getSurFaceId());
				questionCheckBox.put("createTime", ToolUtil.getTimeAndToString());
				questionCheckBox.put("quId", question.get("id"));
			}
			if(!questionCheckBoxs.isEmpty())
				dwSurveyDirectoryDao.addQuestionCheckBoxMationCopyList(questionCheckBoxs);
		} else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
			List<Map<String, Object>> questionMultiFillBlanks = dwSurveyDirectoryDao.queryQuestionMultiFillBlankListByCopyId(question);//获取多行填空题
			for(Map<String, Object> questionMultiFillBlank : questionMultiFillBlanks){
				questionMultiFillBlank.put("id", ToolUtil.getSurFaceId());
				questionMultiFillBlank.put("createTime", ToolUtil.getTimeAndToString());
				questionMultiFillBlank.put("quId", question.get("id"));
			}
			if(!questionMultiFillBlanks.isEmpty())
				dwSurveyDirectoryDao.addQuestionMultiFillBlankMationCopyList(questionMultiFillBlanks);
		} else if (quType.equals(QuType.BIGQU.getActionName())) {
		} else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) || quType.equals(QuType.CHENSCORE.getActionName())
				|| quType.equals(QuType.CHENFBK.getActionName()) || quType.equals(QuType.COMPCHENRADIO.getActionName())) {// 矩阵单选，矩阵多选，矩阵填空题，复合矩阵单选
			List<Map<String, Object>> questionChenRows = dwSurveyDirectoryDao.queryQuestionChenRowListByCopyId(question);//获取行选项
			List<Map<String, Object>> questionChenColumns = dwSurveyDirectoryDao.queryQuestionChenColumnListByCopyId(question);//获取列选项
			for(Map<String, Object> questionChenRow : questionChenRows){
				questionChenRow.put("id", ToolUtil.getSurFaceId());
				questionChenRow.put("createTime", ToolUtil.getTimeAndToString());
				questionChenRow.put("quId", question.get("id"));
			}
			if(!questionChenRows.isEmpty())
				dwSurveyDirectoryDao.addQuestionChenRowMationCopyList(questionChenRows);
			for(Map<String, Object> questionChenColumn : questionChenColumns){
				questionChenColumn.put("id", ToolUtil.getSurFaceId());
				questionChenColumn.put("createTime", ToolUtil.getTimeAndToString());
				questionChenColumn.put("quId", question.get("id"));
			}
			if(!questionChenColumns.isEmpty())
				dwSurveyDirectoryDao.addQuestionChenColumnMationCopyList(questionChenColumns);
			if(quType.equals(QuType.COMPCHENRADIO.getActionName())){//如果是复合矩阵单选题， 则还有题选项
				List<Map<String, Object>> questionChenOptions = dwSurveyDirectoryDao.queryQuestionChenOptionListByCopyId(question);//获取选项
				for(Map<String, Object> questionChenOption : questionChenOptions){
					questionChenOption.put("id", ToolUtil.getSurFaceId());
					questionChenOption.put("createTime", ToolUtil.getTimeAndToString());
					questionChenOption.put("quId", question.get("id"));
				}
			}
		} else if (quType.equals(QuType.SCORE.getActionName())) {
			List<Map<String, Object>> questionScores = dwSurveyDirectoryDao.queryQuestionScoreListByCopyId(question);//获取评分题
			for(Map<String, Object> questionScore : questionScores){
				questionScore.put("id", ToolUtil.getSurFaceId());
				questionScore.put("createTime", ToolUtil.getTimeAndToString());
				questionScore.put("quId", question.get("id"));
			}
			if(!questionScores.isEmpty())
				dwSurveyDirectoryDao.addQuestionScoreMationCopyList(questionScores);
		} else if (quType.equals(QuType.ORDERQU.getActionName())) {
			List<Map<String, Object>> questionOrderBys = dwSurveyDirectoryDao.queryQuestionOrderByListByCopyId(question);//获取排序题
			for(Map<String, Object> questionOrderBy : questionOrderBys){
				questionOrderBy.put("id", ToolUtil.getSurFaceId());
				questionOrderBy.put("createTime", ToolUtil.getTimeAndToString());
				questionOrderBy.put("quId", question.get("id"));
			}
			if(!questionOrderBys.isEmpty())
				dwSurveyDirectoryDao.addQuestionOrderByMationCopyList(questionOrderBys);
		}
	}

}
