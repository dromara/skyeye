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
import com.skyeye.eve.service.ExamService;


@Controller
public class ExamController {
	
	@Autowired
	private ExamService examService;
	
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
	@RequestMapping("/post/ExamController/queryExamList")
	@ResponseBody
	public void queryExamList(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamList(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryMyExamList")
	@ResponseBody
	public void queryMyExamList(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryMyExamList(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/insertExamMation")
	@ResponseBody
	public void insertExamMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.insertExamMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamMationById")
	@ResponseBody
	public void queryExamMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuFillblankMation")
	@ResponseBody
	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuFillblankMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuScoreMation")
	@ResponseBody
	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuScoreMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuOrderquMation")
	@ResponseBody
	public void addQuOrderquMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuOrderquMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuPagetagMation")
	@ResponseBody
	public void addQuPagetagMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuPagetagMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuRadioMation")
	@ResponseBody
	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuRadioMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuCheckBoxMation")
	@ResponseBody
	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuCheckBoxMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuMultiFillblankMation")
	@ResponseBody
	public void addQuMultiFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuMultiFillblankMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: addQuParagraphMation
	     * @Description: 添加段落题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExamController/addQuParagraphMation")
	@ResponseBody
	public void addQuParagraphMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuParagraphMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/addQuChenMation")
	@ResponseBody
	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.addQuChenMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionMationById")
	@ResponseBody
	public void deleteQuestionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionChenColumnMationById")
	@ResponseBody
	public void deleteQuestionChenColumnMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionChenColumnMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionChenRowMationById")
	@ResponseBody
	public void deleteQuestionChenRowMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionChenRowMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionRadioOptionMationById")
	@ResponseBody
	public void deleteQuestionRadioOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionRadioOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionChedkBoxOptionMationById")
	@ResponseBody
	public void deleteQuestionChedkBoxOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionChedkBoxOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionScoreOptionMationById")
	@ResponseBody
	public void deleteQuestionScoreOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionScoreOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionOrderOptionMationById")
	@ResponseBody
	public void deleteQuestionOrderOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionOrderOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteQuestionMultiFillblankOptionMationById")
	@ResponseBody
	public void deleteQuestionMultiFillblankOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteQuestionMultiFillblankOptionMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editExamStateToReleaseById
	     * @Description: 试卷发布
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExamController/editExamStateToReleaseById")
	@ResponseBody
	public void editExamStateToReleaseById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.editExamStateToReleaseById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamMationByIdToHTML")
	@ResponseBody
	public void queryExamMationByIdToHTML(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamMationByIdToHTML(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/deleteExamMationById")
	@ResponseBody
	public void deleteExamMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.deleteExamMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamFxMationById")
	@ResponseBody
	public void queryExamFxMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamFxMationById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/insertExamMationCopyById")
	@ResponseBody
	public void insertExamMationCopyById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.insertExamMationCopyById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryAnswerExamMationByIp")
	@ResponseBody
	public void queryAnswerExamMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryAnswerExamMationByIp(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/insertAnswerExamMationByIp")
	@ResponseBody
	public void insertAnswerExamMationByIp(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.insertAnswerExamMationByIp(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/updateExamMationEndById")
	@ResponseBody
	public void updateExamMationEndById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.updateExamMationEndById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamAnswerMationByAnswerId")
	@ResponseBody
	public void queryExamAnswerMationByAnswerId(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamAnswerMationByAnswerId(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamAnswerMationToMarkingByAnswerId")
	@ResponseBody
	public void queryExamAnswerMationToMarkingByAnswerId(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamAnswerMationToMarkingByAnswerId(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/insertExamAnswerResultMation")
	@ResponseBody
	public void insertExamAnswerResultMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.insertExamAnswerResultMation(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamMationDetailById")
	@ResponseBody
	public void queryExamMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamMationDetailById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamAnswerMationDetailById")
	@ResponseBody
	public void queryExamAnswerMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamAnswerMationDetailById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/queryExamAndMarkPeopleMationDetailById")
	@ResponseBody
	public void queryExamAndMarkPeopleMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.queryExamAndMarkPeopleMationDetailById(inputObject, outputObject);
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
	@RequestMapping("/post/ExamController/editMarkPeopleMationDetailById")
	@ResponseBody
	public void editMarkPeopleMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
		examService.editMarkPeopleMationDetailById(inputObject, outputObject);
	}
	
}
