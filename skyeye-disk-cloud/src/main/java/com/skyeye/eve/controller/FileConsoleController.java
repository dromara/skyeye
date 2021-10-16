/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.FileConsoleService;

@Controller
public class FileConsoleController {
	
	@Autowired
	private FileConsoleService fileConsoleService;
	
	/**
	 * 
	     * @Title: queryFileFolderByUserId
	     * @Description: 根据当前用户获取目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryFileFolderByUserId")
	@ResponseBody
	public void queryFileFolderByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryFileFolderByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertFileFolderByUserId
	     * @Description: 添加目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertFileFolderByUserId")
	@ResponseBody
	public void insertFileFolderByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertFileFolderByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryFilesListByFolderId
	     * @Description: 获取这个目录下的所有文件+目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryFilesListByFolderId")
	@ResponseBody
	public void queryFilesListByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryFilesListByFolderId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteFileFolderById
	     * @Description: 删除目录以及目录下的所有文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/deleteFileFolderById")
	@ResponseBody
	public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.deleteFileFolderById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editFileFolderById
	     * @Description: 编辑目录名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/editFileFolderById")
	@ResponseBody
	public void editFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.editFileFolderById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertUploadFileByUserId
	     * @Description: 上传文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertUploadFileByUserId")
	@ResponseBody
	public void insertUploadFileByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertUploadFileByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertUploadFileChunksByUserId
	     * @Description: 上传文件合并块
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertUploadFileChunksByUserId")
	@ResponseBody
	public void insertUploadFileChunksByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertUploadFileChunksByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUploadFileChunksByChunkMd5
	     * @Description: 文件分块上传检测是否上传
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryUploadFileChunksByChunkMd5")
	@ResponseBody
	public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryUploadFileChunksByChunkMd5(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUploadFilePathById
	     * @Description: 文件获取路径
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryUploadFilePathById")
	@ResponseBody
	public void queryUploadFilePathById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryUploadFilePathById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUploadOfficeFileById
	     * @Description: office文件编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping(value="/post/FileConsoleController/editUploadOfficeFileById", method = RequestMethod.POST)
	@ResponseBody
	public void editUploadOfficeFileById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.editUploadOfficeFileById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllFileSizeByUserId
	     * @Description: 根据当前用户获取总文件大小
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryAllFileSizeByUserId")
	@ResponseBody
	public void queryAllFileSizeByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryAllFileSizeByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertFileCatalogToRecycleById
	     * @Description: 加入回收站
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertFileCatalogToRecycleById")
	@ResponseBody
	public void insertFileCatalogToRecycleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertFileCatalogToRecycleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryFileRecycleBinByUserId
	     * @Description: 我的回收站
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryFileRecycleBinByUserId")
	@ResponseBody
	public void queryFileRecycleBinByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryFileRecycleBinByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteFileRecycleBinById
	     * @Description: 回收站内容还原
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/deleteFileRecycleBinById")
	@ResponseBody
	public void deleteFileRecycleBinById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.deleteFileRecycleBinById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertFileToShareById
	     * @Description: 文件分享
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertFileToShareById")
	@ResponseBody
	public void insertFileToShareById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertFileToShareById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryShareFileListByUserId
	     * @Description: 文件分享列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryShareFileListByUserId")
	@ResponseBody
	public void queryShareFileListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryShareFileListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteShareFileById
	     * @Description: 删除文件分享外链
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/deleteShareFileById")
	@ResponseBody
	public void deleteShareFileById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.deleteShareFileById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryShareFileMationById
	     * @Description: 文件共享输入密码时获取文件信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryShareFileMationById")
	@ResponseBody
	public void queryShareFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryShareFileMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryShareFileMationCheckById
	     * @Description: 文件共享输入密码时校验
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryShareFileMationCheckById")
	@ResponseBody
	public void queryShareFileMationCheckById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryShareFileMationCheckById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryShareFileBaseMationById
	     * @Description: 获取分享文件基础信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryShareFileBaseMationById")
	@ResponseBody
	public void queryShareFileBaseMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryShareFileBaseMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryShareFileListByParentId
	     * @Description: 根据父id获取该id下分享的文件和文件夹
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryShareFileListByParentId")
	@ResponseBody
	public void queryShareFileListByParentId(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryShareFileListByParentId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertShareFileListToSave
	     * @Description: 分享文件保存
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertShareFileListToSave")
	@ResponseBody
	public void insertShareFileListToSave(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertShareFileListToSave(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryFileToShowById
	     * @Description: 文档在线预览
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryFileToShowById")
	@ResponseBody
	public void queryFileToShowById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryFileToShowById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertWordFileToService
	     * @Description: 新建word文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertWordFileToService")
	@ResponseBody
	public void insertWordFileToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertWordFileToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertExcelFileToService
	     * @Description: 新建excel文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertExcelFileToService")
	@ResponseBody
	public void insertExcelFileToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertExcelFileToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertPPTFileToService
	     * @Description: 新建ppt文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertPPTFileToService")
	@ResponseBody
	public void insertPPTFileToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertPPTFileToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertTXTFileToService
	     * @Description: 新建txt文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertTXTFileToService")
	@ResponseBody
	public void insertTXTFileToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertTXTFileToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertHtmlFileToService
	     * @Description: 新建html文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertHtmlFileToService")
	@ResponseBody
	public void insertHtmlFileToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertHtmlFileToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDuplicateCopyToService
	     * @Description: 创建副本
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertDuplicateCopyToService")
	@ResponseBody
	public void insertDuplicateCopyToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertDuplicateCopyToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryFileMationById
	     * @Description: 获取文件属性
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/queryFileMationById")
	@ResponseBody
	public void queryFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.queryFileMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertFileMationToPackageToFolder
	     * @Description: 文件打包
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertFileMationToPackageToFolder")
	@ResponseBody
	public void insertFileMationToPackageToFolder(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertFileMationToPackageToFolder(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertFileMationPackageToFolder
	     * @Description: 压缩包解压
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertFileMationPackageToFolder")
	@ResponseBody
	public void insertFileMationPackageToFolder(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertFileMationPackageToFolder(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertPasteCopyToService
	     * @Description: 文件或者文件夹复制
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertPasteCopyToService")
	@ResponseBody
	public void insertPasteCopyToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertPasteCopyToService(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertPasteCutToService
	     * @Description: 文件或者文件夹剪切
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertPasteCutToService")
	@ResponseBody
	public void insertPasteCutToService(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertPasteCutToService(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: queryOfficeUpdateTimeToKey
         * @Description: office文件编辑获取修改时间作为最新的key
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/FileConsoleController/queryOfficeUpdateTimeToKey")
    @ResponseBody
    public void queryOfficeUpdateTimeToKey(InputObject inputObject, OutputObject outputObject) throws Exception{
        fileConsoleService.queryOfficeUpdateTimeToKey(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryFileNumStatistics
         * @Description: 文件统计报表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/FileConsoleController/queryFileNumStatistics")
    @ResponseBody
    public void queryFileNumStatistics(InputObject inputObject, OutputObject outputObject) throws Exception{
        fileConsoleService.queryFileNumStatistics(inputObject, outputObject);
    }
    
    /**
	 * 
	     * @Title: insertFileMationToPackageDownload
	     * @Description: 文件打包下载
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/FileConsoleController/insertFileMationToPackageDownload")
	@ResponseBody
	public void insertFileMationToPackageDownload(InputObject inputObject, OutputObject outputObject) throws Exception{
		fileConsoleService.insertFileMationToPackageDownload(inputObject, outputObject);
	}
	
}
