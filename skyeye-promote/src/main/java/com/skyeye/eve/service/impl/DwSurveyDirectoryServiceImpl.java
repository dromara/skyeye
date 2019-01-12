package com.skyeye.eve.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.constans.CheckType;
import com.skyeye.common.constans.QuType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.DwSurveyDirectoryDao;
import com.skyeye.eve.service.DwSurveyDirectoryService;
import com.skyeye.quartz.config.QuartzService;
import com.skyeye.quartz.entity.SysQuartz;


@Service
public class DwSurveyDirectoryServiceImpl implements DwSurveyDirectoryService{
	
	@Autowired
	private DwSurveyDirectoryDao dwSurveyDirectoryDao;
	
	@Autowired
	private QuartzService quartzService;

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
			List<Map<String, Object>> questions = dwSurveyDirectoryDao.queryQuestionListByBelongId(map);//获取问卷中的题
			if(!questions.isEmpty()){
				map.put("startTime", ToolUtil.getTimeAndToString());
				map.put("questionNum", questions.size());
				dwSurveyDirectoryDao.editSurveyStateToReleaseById(map);
				if("1".equals(surveyMation.get("ynEndTime").toString())){//是否依据时间结束
					SysQuartz sysQuartz = new SysQuartz();
					sysQuartz.setId(ToolUtil.getSurFaceId());
			        sysQuartz.setName(surveyMation.get("id").toString());
			        sysQuartz.setRemark("问卷调查-【" + surveyMation.get("surveyName").toString() + "】");
			        sysQuartz.setGroups("endSurveyMation");
			        sysQuartz.setCron(ToolUtil.getCrons1(surveyMation.get("endTime").toString()));
			        quartzService.addJob(sysQuartz);
				}
			}else{
				outputObject.setreturnMessage("该问卷没有调查项，无法发布问卷。");
			}
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
			for(Map<String, Object> bean : beans){
				bean.put("anAllCount", count);
				for(Map<String, Object> row : rows){
					if(row.get("id").toString().equals(bean.get("quRowId").toString()))
						bean.put("anCount", row.get("anCount").toString());
				}
			}
			question.put("anChenRadios", beans);
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
			for(Map<String, Object> bean : beans){
				bean.put("anAllCount", count);
				for(Map<String, Object> row : rows){
					if(row.get("id").toString().equals(bean.get("quRowId").toString()))
						bean.put("anCount", row.get("anCount").toString());
				}
			}
			question.put("anChenFbks", beans);
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
			for(Map<String, Object> bean : beans){
				bean.put("anAllCount", count);
				for(Map<String, Object> row : rows){
					if(row.get("id").toString().equals(bean.get("quRowId").toString()))
						bean.put("anCount", row.get("anCount").toString());
				}
			}
			question.put("anChenCheckboxs", beans);
		} else if(quType.equals(QuType.CHENSCORE.getActionName())){//矩阵评分题
			List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryChenScoreGroupStat(question);
			question.put("anChenScores", beans);
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

	/**
	 * 
	     * @Title: queryAnswerSurveyMationByIp
	     * @Description: 判断该ip的用户是否回答过此问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAnswerSurveyMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		if("2".equals(surveyMation.get("surveyState").toString())){
			if("4".equals(surveyMation.get("effective").toString()) || "1".equals(surveyMation.get("effectiveIp").toString())){//每台电脑或手机只能答一次
				Map<String, Object> answerMation = dwSurveyDirectoryDao.querySurveyAnswerMationByIp(map);
				if(answerMation != null && !answerMation.isEmpty()){
					outputObject.setreturnMessage("您已参与过该问卷，请休息一会儿。");
					return;
				}
			}else{//不做ip限制，则默认每五分钟才能答一次
				List<Map<String, Object>> answerMation = dwSurveyDirectoryDao.querySurveyAnswerMationOverFiveMinByIp(map);
				if(answerMation != null && !answerMation.isEmpty()){
					outputObject.setreturnMessage("您已参与过该问卷，请休息一会儿。");
					return;
				}
			}
			
			if("1".equals(surveyMation.get("ynEndNum").toString())){//是否依据收到的份数结束
				if(Integer.parseInt(surveyMation.get("answerNum").toString()) + 1 > Integer.parseInt(surveyMation.get("endNum").toString())){
					outputObject.setreturnMessage("问卷已结束。");
					return;
				}
			}
			
			if("1".equals(surveyMation.get("ynEndTime").toString())){//是否依据时间结束
				if(ToolUtil.compare(surveyMation.get("endTime").toString(), ToolUtil.getTimeAndToString())){//当前时间和设置的结束时间作比较
					outputObject.setreturnMessage("问卷已结束。");
					return;
				}
			}
		}else{
			outputObject.setreturnMessage("问卷已结束。");
			return;
		}
		outputObject.setBean(surveyMation);
	}

	/**
	 * 
	     * @Title: insertAnswerSurveyMationByIp
	     * @Description: 用户回答问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public void insertAnswerSurveyMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> data = JSONObject.fromObject(map.get("jsonData").toString());
		map.put("id", data.get("surveyId"));
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		if("4".equals(surveyMation.get("effective").toString()) || "1".equals(surveyMation.get("effectiveIp").toString())){//每台电脑或手机只能答一次
			Map<String, Object> answerMation = dwSurveyDirectoryDao.querySurveyAnswerMationByIp(map);
			if(answerMation != null && !answerMation.isEmpty()){
				outputObject.setreturnMessage("您已参与过该问卷，请休息一会儿。");
				return;
			}
		}else{//不做ip限制，则默认每五分钟才能答一次
			List<Map<String, Object>> answerMation = dwSurveyDirectoryDao.querySurveyAnswerMationOverFiveMinByIp(map);
			if(answerMation != null && !answerMation.isEmpty()){
				outputObject.setreturnMessage("您已参与过该问卷，请休息一会儿。");
				return;
			}
		}
		
		if("1".equals(surveyMation.get("ynEndNum").toString())){//是否依据收到的份数结束
			if(Integer.parseInt(surveyMation.get("answerNum").toString()) + 1 > Integer.parseInt(surveyMation.get("endNum").toString())){
				outputObject.setreturnMessage("问卷已结束。");
				return;
			}
		}
		
		if("1".equals(surveyMation.get("ynEndTime").toString())){//是否依据时间结束
			if(ToolUtil.compare(surveyMation.get("endTime").toString(), ToolUtil.getTimeAndToString())){//当前时间和设置的结束时间作比较
				outputObject.setreturnMessage("问卷已结束。");
				return;
			}
		}
		String ipAddr = map.get("ip").toString();//问卷调查ip
		HttpServletRequest request = inputObject.getRequest();
		Map<String, Map<String, Object>> quMaps = buildSaveSurveyMap(request, data);
		//问卷答卷信息
		Map<String, Object> surveyAnswer = new HashMap<>();
		String addr = IPSeeker.getCountry(ipAddr);
		String city = getCurCityByCountry(addr);
		surveyAnswer.put("ipAddr", ipAddr);
		surveyAnswer.put("addr", addr);
		surveyAnswer.put("city", city);
		surveyAnswer.put("id", data.get("surveyId"));
		surveyAnswer.put("dataSource", 0);
		surveyAnswer.put("bgAnDate", map.get("bgAnDate"));
		saveAnswer(surveyAnswer, quMaps);
	}
	
	/**
	 * 
	     * @Title: getCurCityByCountry
	     * @Description: 获取城市
	     * @param @param country
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public String getCurCityByCountry(String country) {
		String city = country;
		city = city.replaceAll("\\S+省|市.*|[内蒙古|广西]", "");
		city = city.replaceAll("清华大学.*", "北京");
		return city;
	}
	
	/**
	 * 
	     * @Title: buildSaveSurveyMap
	     * @Description: 用户回答问卷时获取key-value值
	     * @param @param request
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Map<String,Map<String,Object>>    返回类型
	     * @throws
	 */
	public Map<String, Map<String, Object>> buildSaveSurveyMap(HttpServletRequest request, Map<String, Object> params) throws Exception {
		Map<String, Map<String, Object>> quMaps = new HashMap<String, Map<String, Object>>();
		Map<String, Object> yesnoMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.YESNO.getIndex() + "_");// 是非
		quMaps.put("yesnoMaps", yesnoMaps);
		Map<String, Object> radioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.RADIO.getIndex() + "_");// 单选
		Map<String, Object> checkboxMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHECKBOX.getIndex() + "_");// 多选
		Map<String, Object> fillblankMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.FILLBLANK.getIndex() + "_");// 填空
		quMaps.put("fillblankMaps", fillblankMaps);
		Map<String, Object> dfillblankMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.MULTIFILLBLANK.getIndex() + "_");// 多项填空
		for (String key : dfillblankMaps.keySet()) {
			String dfillValue = dfillblankMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, dfillValue);
			dfillblankMaps.put(key, map);
		}
		quMaps.put("multifillblankMaps", dfillblankMaps);
		Map<String, Object> answerMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.ANSWER.getIndex() + "_");// 多行填空
		quMaps.put("answerMaps", answerMaps);
		Map<String, Object> compRadioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.COMPRADIO.getIndex() + "_");// 复合单选
		for (String key : compRadioMaps.keySet()) {
			String enId = key;
			String quItemId = compRadioMaps.get(key).toString();
			String otherText = params.get("text_qu_" + QuType.COMPRADIO.getIndex() + "_" + enId + "_" + quItemId).toString();
			Map<String, Object> anRadio = new HashMap<>();
			anRadio.put("quId", enId);
			anRadio.put("quItemId", quItemId);
			anRadio.put("otherText", otherText);
			compRadioMaps.put(key, anRadio);
		}
		quMaps.put("compRadioMaps", compRadioMaps);
		Map<String, Object> compCheckboxMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.COMPCHECKBOX.getIndex() + "_");// 复合多选
		for (String key : compCheckboxMaps.keySet()) {
			String dfillValue = compCheckboxMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, "tag_" + dfillValue);
			for (String key2 : map.keySet()) {
				String quItemId = map.get(key2).toString();
				String otherText = params.get("text_" + dfillValue + quItemId).toString();
				Map<String, Object> anCheckbox = new HashMap<>();
				anCheckbox.put("quItemId", quItemId);
				anCheckbox.put("otherText", otherText);
				map.put(key2, anCheckbox);
			}
			compCheckboxMaps.put(key, map);
		}
		quMaps.put("compCheckboxMaps", compCheckboxMaps);
		Map<String, Object> enumMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.ENUMQU.getIndex() + "_");// 枚举
		quMaps.put("enumMaps", enumMaps);
		Map<String, Object> scoreMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.SCORE.getIndex() + "_");// 分数
		for (String key : scoreMaps.keySet()) {
			String tag = scoreMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			scoreMaps.put(key, map);
		}
		quMaps.put("scoreMaps", scoreMaps);
		Map<String, Object> quOrderMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.ORDERQU.getIndex() + "_");// 排序
		for (String key : quOrderMaps.keySet()) {
			String tag = quOrderMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			quOrderMaps.put(key, map);
		}
		quMaps.put("quOrderMaps", quOrderMaps);
		Map<String, Object> chenRadioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENRADIO.getIndex() + "_");
		for (String key : chenRadioMaps.keySet()) {
			String tag = chenRadioMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			chenRadioMaps.put(key, map);
		}
		quMaps.put("chenRadioMaps", chenRadioMaps);
		Map<String, Object> chenCheckboxMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENCHECKBOX.getIndex() + "_");
		for (String key : chenCheckboxMaps.keySet()) {
			String tag = chenCheckboxMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			for (String keyRow : map.keySet()) {
				String tagRow = map.get(keyRow).toString();
				Map<String, Object> mapRow = WebUtils.getParametersStartingWith(request, tagRow);
				map.put(keyRow, mapRow);
			}
			chenCheckboxMaps.put(key, map);
		}
		quMaps.put("chenCheckboxMaps", chenCheckboxMaps);
		Map<String, Object> chenScoreMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENSCORE.getIndex() + "_");
		for (String key : chenScoreMaps.keySet()) {
			String tag = chenScoreMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			for (String keyRow : map.keySet()) {
				String tagRow = map.get(keyRow).toString();
				Map<String, Object> mapRow = WebUtils.getParametersStartingWith(request, tagRow);
				map.put(keyRow, mapRow);
			}
			chenScoreMaps.put(key, map);
		}
		quMaps.put("chenScoreMaps", chenScoreMaps);
		Map<String, Object> chenFbkMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.CHENFBK.getIndex() + "_");
		for (String key : chenFbkMaps.keySet()) {
			String tag = chenFbkMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			for (String keyRow : map.keySet()) {
				String tagRow = map.get(keyRow).toString();
				Map<String, Object> mapRow = WebUtils.getParametersStartingWith(request, tagRow);
				map.put(keyRow, mapRow);
			}
			chenFbkMaps.put(key, map);
		}
		quMaps.put("chenFbkMaps", chenFbkMaps);
		for (String key : radioMaps.keySet()) {
			String enId = key;
			String quItemId = radioMaps.get(key).toString();
			String otherText = params.get("text_qu_" + QuType.RADIO.getIndex() + "_" + enId + "_" + quItemId).toString();
			Map<String, Object> anRadio = new HashMap<>();
			anRadio.put("quId", enId);
			anRadio.put("quItemId", quItemId);
			anRadio.put("otherText", otherText);
			radioMaps.put(key, anRadio);
		}
		quMaps.put("compRadioMaps", radioMaps);
		for (String key : checkboxMaps.keySet()) {
			String dfillValue = checkboxMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, dfillValue);
			for (String key2 : map.keySet()) {
				String quItemId = map.get(key2).toString();
				String otherText = params.get("text_" + dfillValue + quItemId).toString();
				Map<String, Object> anCheckbox = new HashMap<>();
				anCheckbox.put("quItemId", quItemId);
				anCheckbox.put("otherText", otherText);
				map.put(key2, anCheckbox);
			}
			checkboxMaps.put(key, map);
		}
		quMaps.put("compCheckboxMaps", checkboxMaps);
		Map<String, Object> chenCompChenRadioMaps = WebUtils.getParametersStartingWith(request, "qu_" + QuType.COMPCHENRADIO.getIndex() + "_");
		for (String key : chenCompChenRadioMaps.keySet()) {
			String tag = chenCompChenRadioMaps.get(key).toString();
			Map<String, Object> map = WebUtils.getParametersStartingWith(request, tag);
			for (String keyRow : map.keySet()) {
				String tagRow = map.get(keyRow).toString();
				Map<String, Object> mapRow = WebUtils.getParametersStartingWith(request, tagRow);
				map.put(keyRow, mapRow);
			}
			chenCompChenRadioMaps.put(key, map);
		}
		quMaps.put("compChenRadioMaps", chenCompChenRadioMaps);
		return quMaps;
	}
	
	/**
	 * @throws Exception 
	 * 
	     * @Title: saveAnswer
	     * @Description: 保存答案
	     * @param @param surveyAnswer
	     * @param @param quMaps    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void saveAnswer(Map<String, Object> surveyAnswer, Map<String, Map<String, Object>> quMaps) throws Exception{
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(surveyAnswer);//获取问卷信息
		surveyMation.put("answerNum", Integer.parseInt(surveyMation.get("answerNum").toString()) + 1);
		dwSurveyDirectoryDao.editSurveyAnswerNumById(surveyMation);//修改回答数量
		if("1".equals(surveyMation.get("ynEndNum").toString())){//是否依据收到的份数结束
			if(Integer.parseInt(surveyMation.get("answerNum").toString()) + 1 >= Integer.parseInt(surveyMation.get("endNum").toString())){
				surveyMation.put("realEndTime", ToolUtil.getTimeAndToString());
				dwSurveyDirectoryDao.editSurveyStateToEndNumById(surveyMation);//结束调查
			}
		}
		//问卷答案
		surveyAnswer.put("bgAnDate", surveyAnswer.get("bgAnDate"));
		surveyAnswer.put("endAnDate", ToolUtil.getTimeAndToString());
		surveyAnswer.put("totalTime", ToolUtil.getDistanceDays(surveyAnswer.get("bgAnDate").toString(), surveyAnswer.get("endAnDate").toString()));//分钟
		surveyAnswer.put("answerId", ToolUtil.getSurFaceId());
		
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
		
		surveyAnswer.put("completeItemNum", anCount);
		int isComplete = 0;
		if(anCount >= Integer.parseInt(surveyMation.get("anItemLeastNum").toString())){
			isComplete = 1;
		}
		surveyAnswer.put("isComplete", isComplete);
		
		int isEffective = 0;
		if(anCount >= 0){//只要回答一题即为有效
			isEffective = 1;
		}
		surveyAnswer.put("isEffective", isEffective);
		surveyAnswer.put("completeNum", surveyMation.get("surveyQuNum"));
		surveyAnswer.put("quNum", surveyMation.get("surveyQuNum"));
		dwSurveyDirectoryDao.insertSurveyAnswer(surveyAnswer);
	}
	
	/**
	 * 保存是非题答案
	 * @param exambatchUser
	 * @param yesnoMaps
	 * @param session
	 * @throws Exception 
	 */
	public int saveAnYesnoMaps(Map<String, Object> surveyAnswer,Map<String,Object> yesnoMaps) throws Exception{
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (yesnoMaps != null)
			for (String key : yesnoMaps.keySet()) {
				answerQuCount++;
				Map<String, Object> bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("yesnoAnswer", yesnoMaps.get(key).toString());
				beans.add(bean);
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveAnYesnoMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存单选题答案
	 * @param exambatchUser
	 * @param radioMaps
	 * @param session
	 * @throws Exception 
	 */
	private int saveAnRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> radioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (radioMaps != null)
			for (String key : radioMaps.keySet()) {
				answerQuCount++;
				Map<String, Object> bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("quItemId", radioMaps.get(key).toString());
				beans.add(bean);
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveAnRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存多项填空题答案
	 * @param exambatchUser
	 * @param dfillMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveAnMultiFillMaps(Map<String, Object> surveyAnswer,Map<String,Object> dfillMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (dfillMaps != null)
			for (String key : dfillMaps.keySet()) {
				Map<String, Object> map= (Map<String, Object>) dfillMaps.get(key);
				if(map != null && map.size() > 0){
					for (String keyMap : map.keySet()) {
						answerQuCount++;
						Map<String, Object> bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("surveyId", surveyId);
						bean.put("surveyAnswerId", surveyAnswerId);
						bean.put("quId", key);
						bean.put("quItemId", keyMap);
						bean.put("answerValue", map.get(key).toString());
						beans.add(bean);
					}
				}
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveAnMultiFillMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存评分题
	 * @param surveyAnswer
	 * @param scoreMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveScoreMaps(Map<String, Object> surveyAnswer,Map<String,Object> scoreMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if (scoreMaps != null)
			for (String key : scoreMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) scoreMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					answerQuCount++;
					Map<String, Object> bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("rowId", keyRow);
					bean.put("scoreValue", mapRows.get(keyRow).toString());
					beans.add(bean);
				}
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveScoreMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存矩阵多选题
	 * @param surveyAnswer
	 * @param scoreMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveChenCheckboxMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenCheckboxMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenCheckboxMaps != null)
			for (String key : chenCheckboxMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) chenCheckboxMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					Map<String, Object> mapRow=(Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						Map<String, Object> bean = new HashMap<>();
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
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveChenCheckboxMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 复合单选题
	 * @param surveyAnswer
	 * @param compRadioMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveCompAnRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> compRadioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(compRadioMaps != null)
			for (String key : compRadioMaps.keySet()) {
				answerQuCount++;
				Map<String, Object> tempAnRadio = (Map<String, Object>) compRadioMaps.get(key);
				Map<String, Object> bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("quItemId", tempAnRadio.get("quItemId"));
				bean.put("otherText", tempAnRadio.get("otherText"));
				beans.add(bean);
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveCompAnRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 复合矩阵单选题
	 * @param surveyAnswer
	 * @param compRadioMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveCompChehRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> compChenRadioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(compChenRadioMaps != null)
			for (String key : compChenRadioMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) compChenRadioMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					Map<String, Object> mapRow = (Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						Map<String, Object> bean = new HashMap<>();
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
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveCompChehRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 矩陈评分题
	 * @param surveyAnswer
	 * @param chenScoreMaps
	 * @param session
	 * @throws Exception 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int saveChenScoreMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenScoreMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenScoreMaps != null)
			for (String key : chenScoreMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) chenScoreMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					Map<String, Object> mapRow = (Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						Map<String, Object> bean = new HashMap<>();
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
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveChenScoreMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存多选题答案
	 * @param exambatchUser
	 * @param checkboxMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveAnCheckboxMaps(Map<String, Object> surveyAnswer,Map<String,Object> checkboxMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(checkboxMaps != null)
			for (String key : checkboxMaps.keySet()) {
				Map<String, Object> map = (Map<String, Object>) checkboxMaps.get(key);
				for (String keyMap : map.keySet()) {
					answerQuCount++;
					Map<String, Object> bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("quItemId", map.get(keyMap).toString());
					beans.add(bean);
				}
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveAnCheckboxMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存单项填空题答案
	 * @param exambatchUser
	 * @param fillMaps
	 * @param session
	 * @throws Exception 
	 */
	private int saveAnFillMaps(Map<String, Object> surveyAnswer,Map<String,Object> fillMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(fillMaps != null)
			for (String key : fillMaps.keySet()) {
				answerQuCount++;
				Map<String, Object> bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("answerValue", fillMaps.get(key).toString());
				beans.add(bean);
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveAnFillMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存判断题答案
	 * @param exambatchUser
	 * @param anAnswerMaps
	 * @param session
	 * @throws Exception 
	 */
	private int saveAnAnswerMaps(Map<String, Object> surveyAnswer,Map<String,Object> anAnswerMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(anAnswerMaps != null)
			for (String key : anAnswerMaps.keySet()) {
				answerQuCount++;
				Map<String, Object> bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("answerValue", anAnswerMaps.get(key).toString());
				beans.add(bean);
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveAnAnswerMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存复合多选题答案
	 * @param exambatchUser
	 * @param checkboxMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveCompAnCheckboxMaps(Map<String, Object> surveyAnswer,Map<String,Object> compCheckboxMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(compCheckboxMaps != null)
			for (String key : compCheckboxMaps.keySet()) {
				Map<String, Object> map = (Map<String, Object>) compCheckboxMaps.get(key);
				for (String keyMap : map.keySet()) {
					answerQuCount++;
					Map<String, Object> tempAnCheckbox = (Map<String, Object>) map.get(keyMap);
					Map<String, Object> bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("quItemId", tempAnCheckbox.get("quItemId"));
					bean.put("otherText", tempAnCheckbox.get("otherText"));
					beans.add(bean);
				}
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveCompAnCheckboxMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存枚举题
	 * @param surveyAnswer
	 * @param enumMaps
	 * @param session
	 * @throws Exception 
	 */
	private int saveEnumMaps(Map<String, Object> surveyAnswer,Map<String,Object> enumMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(enumMaps != null)
			for (String key : enumMaps.keySet()) {
				answerQuCount++;
				Map<String, Object> bean = new HashMap<>();
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("surveyId", surveyId);
				bean.put("surveyAnswerId", surveyAnswerId);
				bean.put("quId", key);
				bean.put("quItemNum", Integer.parseInt(key.split("_")[1]));
				bean.put("answerValue", enumMaps.get(key).toString());
				beans.add(bean);
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveEnumMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存排序题 
	 * @param surveyAnswer
	 * @param enumMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveQuOrderMaps(Map<String, Object> surveyAnswer,Map<String,Object> quOrderMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(quOrderMaps != null)
			for (String key : quOrderMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) quOrderMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					answerQuCount++;
					Map<String, Object> bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("rowId", keyRow);
					bean.put("orderNumValue", mapRows.get(keyRow).toString());
					beans.add(bean);
				}
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveQuOrderMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存矩阵单选题
	 * @param surveyAnswer
	 * @param chehRadioMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveChenRadioMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenRadioMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenRadioMaps != null)
			for (String key : chenRadioMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) chenRadioMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					answerQuCount++;
					Map<String, Object> bean = new HashMap<>();
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("surveyId", surveyId);
					bean.put("surveyAnswerId", surveyAnswerId);
					bean.put("quId", key);
					bean.put("rowId", keyRow);
					bean.put("colId", mapRows.get(keyRow).toString());
					beans.add(bean);
				}
			}
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveChenRadioMaps(beans);
		return answerQuCount;
	}
	
	/**
	 * 保存矩阵填空题
	 * @param surveyAnswer
	 * @param chehRadioMaps
	 * @param session
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private int saveChenFbkMaps(Map<String, Object> surveyAnswer,Map<String,Object> chenFbkMaps) throws Exception {
		String surveyId = surveyAnswer.get("id").toString();
		String surveyAnswerId = surveyAnswer.get("answerId").toString();
		int answerQuCount = 0;
		List<Map<String, Object>> beans = new ArrayList<>();
		if(chenFbkMaps != null)
			for (String key : chenFbkMaps.keySet()) {
				Map<String,Object> mapRows = (Map<String, Object>) chenFbkMaps.get(key);
				for (String keyRow : mapRows.keySet()) {
					Map<String, Object> mapRow=(Map<String, Object>) mapRows.get(keyRow);
					for (String  keyCol : mapRow.keySet()) {
						answerQuCount++;
						Map<String, Object> bean = new HashMap<>();
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
		if(!beans.isEmpty())
			dwSurveyDirectoryDao.saveChenFbkMaps(beans);
		return answerQuCount;
	}

	/**
	 * 
	     * @Title: updateSurveyMationEndById
	     * @Description: 手动结束问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void updateSurveyMationEndById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		if("1".equals(surveyMation.get("surveyState").toString())){//执行中
			map.put("realEndTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.updateSurveyMationEndById(map);
		}
	}

}
