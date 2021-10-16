/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.KnowlgConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.KnowledgeContentDao;
import com.skyeye.eve.service.KnowledgeContentService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.knowlg.util.Html2Text;
import com.skyeye.knowlg.util.Word2Html;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 *
 * @ClassName: KnowledgeContentServiceImpl
 * @Description: 知识库内容管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:53
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class KnowledgeContentServiceImpl implements KnowledgeContentService {
	
	@Autowired
	private KnowledgeContentDao knowledgeContentDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	/**
	 * 
	     * @Title: queryKnowledgeContentList
	     * @Description: 获取知识库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgeContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = knowledgeContentDao.queryKnowledgeContentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertKnowledgeContentMation
	     * @Description: 添加知识库
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertKnowledgeContentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("state", "1");
		map.put("readNum", '0');
		map.put("createId", inputObject.getLogParams().get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		knowledgeContentDao.insertKnowledgeContentMation(map);
	}
	
	/**
	 * 
	     * @Title: selectKnowledgeContentById
	     * @Description: 通过id查找对应的知识库信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectKnowledgeContentById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = knowledgeContentDao.selectKnowledgeContentById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editKnowledgeContentById
	     * @Description: 编辑知识库信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editKnowledgeContentById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("createTime", DateUtil.getTimeAndToString());
		knowledgeContentDao.editKnowledgeContentById(map);
	}

	/**
	 * 
	     * @Title: deleteKnowledgeContentById
	     * @Description: 删除知识库
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteKnowledgeContentById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		knowledgeContentDao.deleteKnowledgeContentById(map);
	}

	/**
	 * 
	     * @Title: queryKnowledgeContentMationById
	     * @Description: 知识库详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgeContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = knowledgeContentDao.queryKnowledgeContentMationById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
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
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	@Override
	public void insertUploadFileByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(inputObject.getRequest().getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(inputObject.getRequest())) {
			Map<String, Object> user = inputObject.getLogParams();
			String userId = user.get("id").toString();
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) inputObject.getRequest();
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();
			String basePath = tPath + "\\upload\\wordfolder\\" + userId;
			String trueFileName = "";
			String fileName = "";
			while (iter.hasNext()) {
				// 一次遍历所有文件
				MultipartFile file = multiRequest.getFile(iter.next().toString());
				fileName = file.getOriginalFilename();// 文件名称
				//得到文件扩展名
				String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if (file != null) {
					File pack = new File(basePath);
					if(!pack.isDirectory())//目录不存在 
						pack.mkdirs();//创建目录
					// 自定义的文件名称
    				String newFileName = String.valueOf(System.currentTimeMillis()) + "." + fileExtName;
					String path = basePath + "\\" + newFileName;
					// 上传
					file.transferTo(new File(path));
					//初始化文件对象内容
					trueFileName = "/images/upload/wordfolder/" + userId + "/" + newFileName ;
					map.put("fileType", fileExtName);//文件类型
					map.put("fileSizeType", "bytes");//文件大小单位
					map.put("id", ToolUtil.getSurFaceId());
					map.put("createId", userId);
					map.put("createTime", DateUtil.getTimeAndToString());
					map.put("fileAddress", trueFileName);//文件地址
					map.put("fileThumbnail", "-1");
					List<Map<String, Object>> beans;
					if(ToolUtil.isBlank(jedisClient.get(KnowlgConstants.getSysWordFileModuleByMd5(map.get("md5").toString())))){
						beans = new ArrayList<>();
					}else{
						beans = JSONUtil.toList(jedisClient.get(KnowlgConstants.getSysWordFileModuleByMd5(map.get("md5").toString())), null);
					}
					beans.add(map);
					jedisClient.set(KnowlgConstants.getSysWordFileModuleByMd5(map.get("md5").toString()), JSONUtil.toJsonStr(beans));
				}
			}
		}
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
	@SuppressWarnings({ "unchecked", "resource" })
	@Override
	public void insertUploadFileChunksByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		List<Map<String, Object>> beans = JSONUtil.toList(jedisClient.get(KnowlgConstants.getSysWordFileModuleByMd5(map.get("md5").toString())), null);
		List<File> fileList = new ArrayList<File>();
		File f;
		for(Map<String, Object> bean : beans){
			f = new File(tPath.replace("images", "") + bean.get("fileAddress").toString());
			fileList.add(f);
		}
		String fileName = map.get("name").toString();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//文件后缀
		String newFileName = String.valueOf(System.currentTimeMillis()) + "." + fileExtName;//新文件名
		String path = tPath + "\\upload\\wordfolder" + "\\" + userId + "\\" + newFileName;//文件路径
		File outputFile = new File(path);
		//创建文件
		outputFile.createNewFile();
		//输出流
		FileChannel outChnnel = new FileOutputStream(outputFile).getChannel();
		//合并
		try{
			FileChannel inChannel;
			for(File file : fileList){
				inChannel = new FileInputStream(file).getChannel();
				inChannel.transferTo(0, inChannel.size(), outChnnel);
				inChannel.close();
				//删除分片
				file.delete();
			}
		}finally{
			if(outChnnel != null)
				outChnnel.close();
		}
		jedisClient.del(KnowlgConstants.getSysWordFileModuleByMd5(map.get("md5").toString()));
		try{
			String contentId = ToolUtil.getSurFaceId();
			Map<String, Object> entity = Word2Html.word2007ToHtml(outputFile, tPath + "\\upload\\ueditor\\", contentId);
			if("1".equals(entity.get("code").toString())){//成功
				String content = entity.get("content").toString();
				insertMation(contentId, fileName, content, userId);
			}else{
				entity = Word2Html.wordToHtml(outputFile, tPath + "\\upload\\ueditor\\");
				if("1".equals(entity.get("code").toString())){//成功
					String content = entity.get("content").toString();
					insertMation(contentId, fileName, content, userId);
				}
			}
		}finally{
			FileUtil.deleteFile(path);
		}
		outputObject.setBean(map);
	}

	private void insertMation(String contentId, String fileName, String content, String userId) throws Exception {
		Map<String, Object> bean = new HashMap<>();
		bean.put("id", contentId);
		bean.put("state", "1");
		bean.put("readNum", '0');
		bean.put("title", fileName);
		String text = Html2Text.getContent(Word2Html.getHtmlBody(content));
		bean.put("desc", text.length() > 200 ? text.substring(0, 200) : text.substring(0, text.length()));
		bean.put("content", Word2Html.getHtmlBodyAndCSS(content));
		bean.put("createId", userId);
		bean.put("createTime", DateUtil.getTimeAndToString());
		knowledgeContentDao.insertKnowledgeContentMation(bean);
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
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String md5 = map.get("md5").toString();
		String chunk = map.get("chunk").toString();
		List<Map<String, Object>> beans;
		if(ToolUtil.isBlank(jedisClient.get(KnowlgConstants.getSysWordFileModuleByMd5(md5)))){
			beans = new ArrayList<>();
		}else{
			beans = JSONUtil.toList(jedisClient.get(KnowlgConstants.getSysWordFileModuleByMd5(md5)), null);
		}
		Map<String, Object> bean = null;
		int index = -1;
		for(int i = 0; i < beans.size(); i++){
			Map<String, Object> item = beans.get(i);
			if(chunk.equals(item.get("chunk").toString())){
				bean = item;
				index = i;
				return;
			}
		}
		if(bean != null && !bean.isEmpty()){
			String fileAddress = tPath.replace("images", "") + bean.get("fileAddress").toString();
			File checkFile = new File(fileAddress);
			String chunkSize = map.get("chunkSize").toString();
			if(checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)){
			}else{
				beans.remove(index);
				jedisClient.set(KnowlgConstants.getSysWordFileModuleByMd5(map.get("md5").toString()), JSONUtil.toJsonStr(beans));
				outputObject.setreturnMessage("文件上传失败");
			}
		}else{
			outputObject.setreturnMessage("文件上传失败");
		}
	}

	/**
	 * 
	     * @Title: queryUnCheckedKnowledgeContentList
	     * @Description: 获取待审核的知识库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUnCheckedKnowledgeContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
 		List<Map<String, Object>> beans = knowledgeContentDao.queryUnCheckedKnowledgeContentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	 * @Title: queryKnowledgeContentByIdToCheck
	 * @Description: 获取知识库信息用于回显审核
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void queryKnowledgeContentByIdToCheck(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgeContentDao.queryKnowledgeContentByIdToCheck(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editKnowledgeContentToCheck
	     * @Description: 审核知识库
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editKnowledgeContentToCheck(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("examineId", inputObject.getLogParams().get("id"));
		map.put("examineTime", DateUtil.getTimeAndToString());
		knowledgeContentDao.editKnowledgeContentToCheck(map);
	}

	/**
	 * 
	     * @Title: queryCheckedKnowledgeContentList
	     * @Description: 获取已经审核的知识库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckedKnowledgeContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = knowledgeContentDao.queryCheckedKnowledgeContentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryUncheckedKnowledgeContent
	     * @Description: 未审核知识库详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUncheckedKnowledgeContent(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgeContentDao.queryUncheckedKnowledgeContent(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: queryCheckedKnowledgeContent
	     * @Description: 已审核的知识库详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckedKnowledgeContent(InputObject inputObject,OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgeContentDao.queryCheckedKnowledgeContent(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 获取企业知识库列表(已审核通过)
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllPassKnowledgeContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = knowledgeContentDao.queryAllPassKnowledgeContentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
