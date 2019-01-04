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
	
}
