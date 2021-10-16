/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SchoolQuestionBankService;

@Controller
public class SchoolQuestionBankController {
	
	@Autowired
	private SchoolQuestionBankService schoolQuestionBankService;
	
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
	@RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationList")
	@ResponseBody
	public void querySchoolQuestionBankMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.querySchoolQuestionBankMationList(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuRadioMation")
	@ResponseBody
	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuRadioMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/deleteSchoolQuestionBankMationById")
	@ResponseBody
	public void deleteSchoolQuestionBankMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.deleteSchoolQuestionBankMationById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuRadioMationToEditById")
	@ResponseBody
	public void queryQuRadioMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuRadioMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuCheckBoxMation")
	@ResponseBody
	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuCheckBoxMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuCheckBoxMationToEditById")
	@ResponseBody
	public void queryQuCheckBoxMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuCheckBoxMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuFillblankMation")
	@ResponseBody
	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuFillblankMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuFillblankMationToEditById")
	@ResponseBody
	public void queryQuFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuFillblankMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuScoreMation")
	@ResponseBody
	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuScoreMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuScoreMationToEditById")
	@ResponseBody
	public void queryQuScoreMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuScoreMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuOrderbyMation")
	@ResponseBody
	public void addQuOrderbyMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuOrderbyMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuOrderbyMationToEditById")
	@ResponseBody
	public void queryQuOrderbyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuOrderbyMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuMultiFillblankAddMation")
	@ResponseBody
	public void addQuMultiFillblankAddMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuMultiFillblankAddMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuMultiFillblankMationToEditById")
	@ResponseBody
	public void queryQuMultiFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuMultiFillblankMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/addQuChenMation")
	@ResponseBody
	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.addQuChenMation(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/queryQuChenMationToEditById")
	@ResponseBody
	public void queryQuChenMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.queryQuChenMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationListToChoose")
	@ResponseBody
	public void querySchoolQuestionBankMationListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.querySchoolQuestionBankMationListToChoose(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationListByIds")
	@ResponseBody
	public void querySchoolQuestionBankMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.querySchoolQuestionBankMationListByIds(inputObject, outputObject);
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
	@RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationAllList")
	@ResponseBody
	public void querySchoolQuestionBankMationAllList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolQuestionBankService.querySchoolQuestionBankMationAllList(inputObject, outputObject);
	}
	
}
