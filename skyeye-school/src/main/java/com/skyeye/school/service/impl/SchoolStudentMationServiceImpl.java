/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.school.dao.SchoolStudentMationDao;
import com.skyeye.school.entity.SchoolStudentExcelModel;
import com.skyeye.school.entity.SchoolStudentGlobalExcelDictHandler;
import com.skyeye.school.service.SchoolStudentMationService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchoolStudentMationServiceImpl implements SchoolStudentMationService{

	@Autowired
	private SchoolStudentMationDao schoolStudentMationDao;
	
	private static final String EXPORT_EXCEL_NAME = "学生导入模板";

	/**
	 * 
	     * @Title: querySchoolStudentMationList
	     * @Description: 获取在校学生信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolStudentMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolStudentMationDao.querySchoolStudentMationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSchoolStudentMation
	     * @Description: 新增学生信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void insertSchoolStudentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String homeMemberStr = map.get("homeMemberStr").toString();
		if(ToolUtil.isJson(homeMemberStr)){
			Map<String, Object> judgeIn = schoolStudentMationDao.queryStudentMationByIdCardAndSruNo(map);
			if(judgeIn == null || judgeIn.isEmpty()){
				String studentId = ToolUtil.getSurFaceId();//学生id
				String schoolId = map.get("schoolId").toString();//学校id
				//处理家庭成员数据
				List<Map<String, Object>> jArray = JSONUtil.toList(homeMemberStr, null);
				//家庭成员存储对象
				Map<String, Object> entity;
				List<Map<String, Object>> entitys = new ArrayList<>();
				for(int i = 0; i < jArray.size(); i++){
					entity = jArray.get(i);
					entity.put("id", ToolUtil.getSurFaceId());
					entity.put("studentId", studentId);
					entity.put("schoolId", schoolId);
					entitys.add(entity);
				}
				
				//处理家庭情况数据
				Map<String, Object> bean;
				List<Map<String, Object>> beans = new ArrayList<>();
				String[] homeSituation = map.get("homeSituation").toString().split(",");
				for(String str: homeSituation){
					if(!ToolUtil.isBlank(str)){
						bean = new HashMap<>();
						bean.put("situationId", str);
						bean.put("studentId", studentId);
						bean.put("schoolId", schoolId);
						beans.add(bean);
					}
				}
				
				//处理身心障碍数据
				Map<String, Object> item;
				List<Map<String, Object>> items = new ArrayList<>();
				String[] bodyMind = map.get("bodyMind").toString().split(",");
				for(String str: bodyMind){
					if(!ToolUtil.isBlank(str)){
						item = new HashMap<>();
						item.put("bodyMindId", str);
						item.put("studentId", studentId);
						item.put("schoolId", schoolId);
						items.add(item);
					}
				}
				
				map.put("id", studentId);
				map.put("createId", inputObject.getLogParams().get("id"));
				map.put("createTime", DateUtil.getTimeAndToString());
				//根据证件号码获取出生日期
				int idcardType = Integer.parseInt(map.get("idcardType").toString());
				String idCard = map.get("idCard").toString();
				if(idcardType == 1){
					//居民身份证
					Map<String, String> stu = ToolUtil.getBirAgeSex(idCard);
					map.put("birthday", stu.get("birthday"));
				}
				//默认密码为学生身份证后八位
				map.put("password", ToolUtil.MD5(idCard.substring(idCard.length() - 8, idCard.length())));
				
				//插入学生数据
				schoolStudentMationDao.insertSchoolStudentMation(map);
				//插入家庭成员
				if(!entitys.isEmpty()){
					schoolStudentMationDao.insertSchoolStudentParentsMation(entitys);
				}
				//插入家庭情况数据
				if(!beans.isEmpty()){
					schoolStudentMationDao.insertSchoolStudentHomeSituationMation(beans);
				}
				//插入身心障碍数据
				if(!items.isEmpty()){
					schoolStudentMationDao.insertSchoolStudentBodyMindMation(items);
				}
			}else{
				outputObject.setreturnMessage("学生证件号码或学号重复。");
			}
		}else{
			outputObject.setreturnMessage("数据格式错误。");
		}
	}

	/**
	 * 
	     * @Title: queryNotDividedIntoClassesSchoolStudentMationList
	     * @Description: 获取未分班的在校学生信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryNotDividedIntoClassesSchoolStudentMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取当前用户拥有的学校的数据权限
		map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = schoolStudentMationDao.queryNotDividedIntoClassesSchoolStudentMationList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: querySchoolStudentMationToOperatorById
	     * @Description: 获取学生部分信息展示供其他操作时查看
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolStudentMationToOperatorById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolStudentMationDao.querySchoolStudentMationToOperatorById(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
	     * @Title: editAssignmentClassByStuId
	     * @Description: 未分班学生分班
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editAssignmentClassByStuId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> stuAssignmentClass = schoolStudentMationDao.querySchoolStudentAssignmentClassMationById(map);
		//判断该学生是否分班
		if(stuAssignmentClass != null && !stuAssignmentClass.isEmpty()){
			//开始分班
			schoolStudentMationDao.editAssignmentClassByStuId(map);
		}else{
			outputObject.setreturnMessage("该学生已分班，请刷新列表。");
		}
	}

	/**
	 * 
	     * @Title: querySchoolStudentMationToEditById
	     * @Description: 获取学生信息用来编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolStudentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolStudentMationDao.querySchoolStudentMationToEditById(map);
		if(bean != null && !bean.isEmpty()){
			//获取家庭成员
			bean.put("stuParents", schoolStudentMationDao.querySchoolStudentParentsMationToEditById(map));
			//获取家庭情况数据
			bean.put("homeSituation", schoolStudentMationDao.querySchoolStudentHomeSituationMationToEditById(map));
			//获取身心障碍数据
			bean.put("bodyMind", schoolStudentMationDao.querySchoolStudentBodyMindMationToEditById(map));
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
	     * @Title: editSchoolStudentMationById
	     * @Description: 编辑学生信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSchoolStudentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String homeMemberStr = map.get("homeMemberStr").toString();
		if(ToolUtil.isJson(homeMemberStr)){
			Map<String, Object> judgeIn = schoolStudentMationDao.queryStudentMationByIdCardAndSruNo(map);
			if(judgeIn == null || judgeIn.isEmpty()){
				String studentId = map.get("id").toString();//学生id
				String schoolId = map.get("schoolId").toString();//学校id
				//处理家庭成员数据
				List<Map<String, Object>> jArray = JSONUtil.toList(homeMemberStr, null);
				//家庭成员存储对象
				Map<String, Object> entity;
				List<Map<String, Object>> entitys = new ArrayList<>();
				for(int i = 0; i < jArray.size(); i++){
					entity = jArray.get(i);
					entity.put("id", ToolUtil.getSurFaceId());
					entity.put("studentId", studentId);
					entity.put("schoolId", schoolId);
					entitys.add(entity);
				}
				
				//处理家庭情况数据
				Map<String, Object> bean;
				List<Map<String, Object>> beans = new ArrayList<>();
				String[] homeSituation = map.get("homeSituation").toString().split(",");
				for(String str: homeSituation){
					if(!ToolUtil.isBlank(str)){
						bean = new HashMap<>();
						bean.put("situationId", str);
						bean.put("studentId", studentId);
						bean.put("schoolId", schoolId);
						beans.add(bean);
					}
				}
				
				//处理身心障碍数据
				Map<String, Object> item;
				List<Map<String, Object>> items = new ArrayList<>();
				String[] bodyMind = map.get("bodyMind").toString().split(",");
				for(String str: bodyMind){
					if(!ToolUtil.isBlank(str)){
						item = new HashMap<>();
						item.put("bodyMindId", str);
						item.put("studentId", studentId);
						item.put("schoolId", schoolId);
						items.add(item);
					}
				}
				
				map.put("id", studentId);
				map.put("createId", inputObject.getLogParams().get("id"));
				map.put("createTime", DateUtil.getTimeAndToString());
				//根据证件号码获取出生日期
				int idcardType = Integer.parseInt(map.get("idcardType").toString());
				if(idcardType == 1){
					//居民身份证
					Map<String, String> stu = ToolUtil.getBirAgeSex(map.get("idCard").toString());
					map.put("birthday", stu.get("birthday"));
				}
				
				//编辑学生数据
				schoolStudentMationDao.editSchoolStudentMationById(map);
				//删除家庭成员
				schoolStudentMationDao.deleteSchoolStudentParentsMationByStuId(studentId);
				//插入家庭成员
				if(!entitys.isEmpty()){
					schoolStudentMationDao.insertSchoolStudentParentsMation(entitys);
				}
				//删除家庭情况数据
				schoolStudentMationDao.deleteSchoolStudentHomeSituationMationByStuId(studentId);
				//插入家庭情况数据
				if(!beans.isEmpty()){
					schoolStudentMationDao.insertSchoolStudentHomeSituationMation(beans);
				}
				//删除身心障碍数据
				schoolStudentMationDao.deleteSchoolStudentBodyMindMationByStuId(studentId);
				//插入身心障碍数据
				if(!items.isEmpty()){
					schoolStudentMationDao.insertSchoolStudentBodyMindMation(items);
				}
			}else{
				outputObject.setreturnMessage("学生证件号码或学号重复。");
			}
		}else{
			outputObject.setreturnMessage("数据格式错误。");
		}
	}

	/**
	 * 
	     * @Title: querySchoolStudentMationDetailById
	     * @Description: 获取学生信息详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySchoolStudentMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = schoolStudentMationDao.querySchoolStudentMationDetailById(map);
		if(bean != null && !bean.isEmpty()){
			//获取家庭成员
			bean.put("stuParents", schoolStudentMationDao.querySchoolStudentParentsMationDetailById(map));
			//获取家庭情况数据
			bean.put("homeSituation", schoolStudentMationDao.querySchoolStudentHomeSituationMationDetailById(map));
			//获取身心障碍数据
			bean.put("bodyMind", schoolStudentMationDao.querySchoolStudentBodyMindMationDetailById(map));
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
	     * @Title: exportSchoolStudentMationModel
	     * @Description: 导出学生模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void exportSchoolStudentMationModel(InputObject inputObject, OutputObject outputObject) throws Exception {
		ExportParams exportParams = new ExportParams();
		exportParams.setTitle(EXPORT_EXCEL_NAME);
		exportParams.setSheetName("学生信息");
		exportParams.setDictHandler(new SchoolStudentGlobalExcelDictHandler());
		exportParams.setCreateHeadRows(true);
		exportParams.setHeaderHeight(500D);
		exportParams.setType(ExcelType.XSSF);
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, SchoolStudentExcelModel.class, new ArrayList<>());
		try {
			ExcelUtil.fileWrite(workbook, inputObject.getResponse(), EXPORT_EXCEL_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
