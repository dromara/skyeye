/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.MyNoteDao;
import com.skyeye.mq.job.JobMateService;
import com.skyeye.service.JobMateMationService;
import com.youbenzi.md2.export.FileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 
    * @ClassName: OutputNotesIsZipServiceImpl
    * @Description: 笔记输出为压缩包
    * @author 卫志强
    * @date 2020年8月23日
    *
 */
@Service("outputNotesIsZipService")
public class OutputNotesIsZipServiceImpl implements JobMateService{

	private static Logger LOGGER = LoggerFactory.getLogger(OutputNotesIsZipServiceImpl.class);
	
	@Autowired
	private JobMateMationService jobMateMationService;
	
	@Value("${IMAGES_PATH}")
	private String tPath;
	
	/**
	 * 系统水印
	 */
	@Value("${system.sysWaterMark}")
	private String sysWaterMark;
	
	/**
	 * 系统地址
	 */
	@Value("${webroot.ndc}")
	private String webRootNdc;
	
	@Autowired
	private MyNoteDao myNoteDao;
	
	@Override
	public void call(String data) throws Exception {
		Map<String, Object> map = JSONUtil.toBean(data, null);
		String jobId = map.get("jobMateId").toString();
		Map<String, Object> mation = new HashMap<>();
		try {
			LOGGER.info("start output job, jobId is {}", jobId);
			// 任务开始
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, "");
			String rowId = map.get("rowId").toString();
			// 类型1.文件夹2.笔记
			int type = Integer.parseInt(map.get("noteType").toString());
			if(type == 1){
				String zipFile = outputFolder(rowId, map.get("userId").toString());
				mation.put("filePath", zipFile);
			}else{
				String zipFile = outPutFileContent(rowId, map.get("userId").toString());
				mation.put("filePath", zipFile);
			}
			
			// 任务完成
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, JSONUtil.toJsonStr(mation));
		} catch (Exception e) {
			LOGGER.info("job is fail, this message is {}", e);
			// 任务失败
			mation.put("message", e);
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, JSONUtil.toJsonStr(mation));
		}
	}
	
	/**
	 * 
	    * @Title: outputFolder
	    * @Description: 文件夹(包含文件)输出为压缩包
	    * @param parentId
	    * @param userId
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	private String outputFolder(String parentId, String userId) throws Exception{
		// 1.获取该目录下的所有目录
		List<Map<String, Object>> beans = myNoteDao.queryAllFolderListByFolderId(parentId);
		// 2.获取所有目录下的所有文件
		List<Map<String, Object>> files = myNoteDao.queryAllFileListByFolderList(beans);
		// 文件存储基础路径
		String basePath = String.format("%s%s%s%s", tPath, "\\upload\\notes\\filezip\\", userId, "\\");
		ToolUtil.createFolder(basePath);
		for(Map<String, Object> bean : files){
			String content = bean.containsKey("content") ? bean.get("content").toString() : "";
			bean.put("fileAddress", outPutFileContent(basePath, content, Integer.parseInt(bean.get("fileType").toString()), userId));
			bean.put("content", "");
			bean.put("fileName", bean.get("fileName").toString() + ".pdf");
		}
		beans.addAll(files);
		// 重置父id
		for(Map<String, Object> folder: beans){
			String[] str = folder.get("parentId").toString().split(",");
			folder.put("directParentId", str[str.length - 1]);
		}
		// 将数据转化为树的形式，方便进行父id重新赋值
		beans = ToolUtil.listToTree(beans, "id", "directParentId", "children");
		// 打包--压缩包文件名
		String fileName = String.valueOf(System.currentTimeMillis());
		String strZipPath = basePath + fileName + ".zip";
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));
		try{
			ToolUtil.recursionZip(out, beans, "", tPath.replace("images", ""), 2);
		}finally{
			// 删除临时文件
			for(Map<String, Object> bean : files){
				FileUtil.deleteFile(tPath.replace("images", "") + bean.get("fileAddress").toString());
			}
			if(out != null)
				out.close();
		}
		return "/images/upload/notes/filezip/" + userId + "/" + fileName + ".zip";
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: outPutFileContent
	    * @Description: 单个文件输出
	    * @param basePath 基础路径
	    * @param content 内容
	    * @param type 类型
	    * @param userId 用户id
	    * @param @return    参数
	    * @return String    返回类型
	    * @throws
	 */
	private String outPutFileContent(String basePath, String content, int type, String userId) throws Exception{
		String fileName = String.valueOf(System.currentTimeMillis());
		if(ToolUtil.isBlank(content)){
			content = "暂无内容";
		}
		switch (type) {
		case 1:
			// 富文本编辑器
			
			break;
		case 2:
			// markdown笔记
			FileFactory.produce(content, basePath + "/" + fileName + ".pdf", webRootNdc, sysWaterMark);
			break;
		case 3:
			// word笔记
			
			break;
		case 4:
			// ecxel笔记
			
			break;
		default:
			break;
		}
		return "/images/upload/notes/filezip/" + userId + "/" + fileName + ".pdf";
	}
	
	/**
	 * 
	    * @Title: outPutFileContent
	    * @Description: 单个文件输出
	    * @param fileId 文件id
	    * @param userId 用户id
	    * @param @return
	    * @throws Exception    参数
	    * @return String    返回类型
	    * @throws
	 */
	private String outPutFileContent(String fileId, String userId) throws Exception{
		Map<String, Object> mation = myNoteDao.queryShareNoteById(fileId);
		mation.put("fileName", mation.get("title"));
		mation.put("fileType", mation.get("type"));
		// 文件存储基础路径
		String basePath = String.format("%s%s%s%s", tPath, "/upload/notes/filezip/", userId, "/");
		ToolUtil.createFolder(basePath);
		return outPutFileContent(basePath, mation.get("content").toString(), Integer.parseInt(mation.get("fileType").toString()), userId);
	}

}
