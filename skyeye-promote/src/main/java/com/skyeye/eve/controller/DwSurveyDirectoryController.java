package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.DwSurveyDirectoryService;


@Controller
public class DwSurveyDirectoryController {
	
	@Autowired
	private DwSurveyDirectoryService dwSurveyDirectoryService;
	
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
	@RequestMapping("/post/DwSurveyDirectoryController/queryDwSurveyDirectoryList")
	@ResponseBody
	public void queryDwSurveyDirectoryList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.queryDwSurveyDirectoryList(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/insertDwSurveyDirectoryMation")
	@ResponseBody
	public void insertDwSurveyDirectoryMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.insertDwSurveyDirectoryMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/queryDwSurveyDirectoryMationById")
	@ResponseBody
	public void queryDwSurveyDirectoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.queryDwSurveyDirectoryMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/queryDwSurveyMationById")
	@ResponseBody
	public void queryDwSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.queryDwSurveyMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/editDwSurveyMationById")
	@ResponseBody
	public void editDwSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.editDwSurveyMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuFillblankMation")
	@ResponseBody
	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuFillblankMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuScoreMation")
	@ResponseBody
	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuScoreMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuOrderquMation")
	@ResponseBody
	public void addQuOrderquMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuOrderquMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuPagetagMation")
	@ResponseBody
	public void addQuPagetagMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuPagetagMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuRadioMation")
	@ResponseBody
	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuRadioMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuCheckBoxMation")
	@ResponseBody
	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuCheckBoxMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuMultiFillblankMation")
	@ResponseBody
	public void addQuMultiFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuMultiFillblankMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuParagraphMation")
	@ResponseBody
	public void addQuParagraphMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuParagraphMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/addQuChenMation")
	@ResponseBody
	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.addQuChenMation(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionMationById")
	@ResponseBody
	public void deleteQuestionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionChenColumnMationById")
	@ResponseBody
	public void deleteQuestionChenColumnMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionChenColumnMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionChenRowMationById")
	@ResponseBody
	public void deleteQuestionChenRowMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionChenRowMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionRadioOptionMationById")
	@ResponseBody
	public void deleteQuestionRadioOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionRadioOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionChedkBoxOptionMationById")
	@ResponseBody
	public void deleteQuestionChedkBoxOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionChedkBoxOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionScoreOptionMationById")
	@ResponseBody
	public void deleteQuestionScoreOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionScoreOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionOrderOptionMationById")
	@ResponseBody
	public void deleteQuestionOrderOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionOrderOptionMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteQuestionMultiFillblankOptionMationById")
	@ResponseBody
	public void deleteQuestionMultiFillblankOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteQuestionMultiFillblankOptionMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSurveyStateToReleaseById
	     * @Description: 问卷发布
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DwSurveyDirectoryController/editSurveyStateToReleaseById")
	@ResponseBody
	public void editSurveyStateToReleaseById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.editSurveyStateToReleaseById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/queryDwSurveyDirectoryMationByIdToHTML")
	@ResponseBody
	public void queryDwSurveyDirectoryMationByIdToHTML(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.queryDwSurveyDirectoryMationByIdToHTML(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/deleteSurveyMationById")
	@ResponseBody
	public void deleteSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.deleteSurveyMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/querySurveyFxMationById")
	@ResponseBody
	public void querySurveyFxMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.querySurveyFxMationById(inputObject, outputObject);
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
	@RequestMapping("/post/DwSurveyDirectoryController/insertSurveyMationCopyById")
	@ResponseBody
	public void insertSurveyMationCopyById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dwSurveyDirectoryService.insertSurveyMationCopyById(inputObject, outputObject);
	}
	
}
