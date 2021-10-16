/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.BytesUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.SysEnclosureService;
import com.skyeye.jedis.JedisClientService;
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

@Service
public class SysEnclosureServiceImpl implements SysEnclosureService{
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	/**
	 * 
	     * @Title: querySysEnclosureListByUserId
	     * @Description: 获取我的附件分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEnclosureListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String parentId = map.get("parentId").toString();
		List<Map<String, Object>> beans = new ArrayList<>();
		if("0".equals(parentId)){//获取每个用户有包含的文件夹
			beans = Constants.getSysEnclosureZeroList();
		}else if("1".equals(parentId)){//获取一级分类
			Map<String, Object> user = inputObject.getLogParams();
			map.put("userId", user.get("id"));
			beans = sysEnclosureDao.querySysEnclosureFirstTypeListByUserId(map);
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			map.put("userId", user.get("id"));
			beans = sysEnclosureDao.querySysEnclosureSecondTypeListByUserId(map);
		}
		outputObject.setBeans(beans);
	}

	/**
	 * 
	     * @Title: insertSysEnclosureMationByUserId
	     * @Description: 新增我的附件分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysEnclosureMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		String parentId = map.get("parentId").toString();
		if(!"0".equals(parentId)){//新增的分类不是一级分类
			//根据所属父id和当前登录用户判断该父目录是否存在
			Map<String, Object> bean = sysEnclosureDao.querySysEnclosureMationByUserIdAndParentId(map);
			if(bean == null){
				outputObject.setreturnMessage("该父目录不存在或者不属于当前账号。");
				return;
			}else{
				//所选择的父目录的父id只能为0（该附件分类只能选择到二级分类）
				if(!"0".equals(bean.get("parentId").toString())){
					outputObject.setreturnMessage("所属文件夹不能为二级文件夹.");
					return;
				}
			}
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("state", "1");//默认状态正常
		sysEnclosureDao.insertSysEnclosureMationByUserId(map);
	}

	/**
	 * 
	     * @Title: querySysEnclosureFirstTypeListByUserId
	     * @Description: 获取我的附件一级分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEnclosureFirstTypeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> beans = sysEnclosureDao.querySysEnclosureFirstTypeListByUserId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: queryThisFolderChilsByFolderId
	     * @Description: 获取指定文件夹下的文件夹和文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryThisFolderChilsByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		if("1".equals(map.get("parentId").toString())){//如果父id为1，即：我的附件，则将parentId设置为0
			map.put("parentId", "0");
		}
		List<Map<String, Object>> beans = sysEnclosureDao.queryThisFolderChilsByFolderId(map);
		for(Map<String, Object> bean: beans){
			if(!"folder".equals(bean.get("fileType").toString())){//不是文件夹
				String size = BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("fileSize").toString()));
				bean.put("fileSize", size);
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: querySysEnclosureMationByUserIdToEdit
	     * @Description: 编辑我的附件分类时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEnclosureMationByUserIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> bean = sysEnclosureDao.querySysEnclosureMationByUserIdToEdit(map);
		outputObject.setBean(bean);
	}

	/**
	 * 
	     * @Title: editSysEnclosureMationByUserId
	     * @Description: 编辑我的附件分类/附件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysEnclosureMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String fileType = map.get("fileType").toString();
		if("folder".equals(fileType)){//编辑的附件数据为文件夹
			sysEnclosureDao.editSysEnclosureFolderMationByUserId(map);
		}else{//编辑的附件数据为文件
			sysEnclosureDao.editSysEnclosureFileMationByUserId(map);
		}
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
	@Transactional(value="transactionManager")
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
			String basePath = tPath + "\\upload\\enclosurefile\\" + userId;
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
					trueFileName = "/images/upload/enclosurefile/" + userId + "/" + newFileName ;
					map.put("fileType", fileExtName);//文件类型
					map.put("fileSizeType", "bytes");//文件大小单位
					map.put("id", ToolUtil.getSurFaceId());
					map.put("createId", userId);
					map.put("createTime", DateUtil.getTimeAndToString());
					map.put("fileAddress", trueFileName);//文件地址
					map.put("fileThumbnail", "-1");
					map.put("folderId", map.get("folderId"));
					List<Map<String, Object>> beans;
					if(ToolUtil.isBlank(jedisClient.get(Constants.getSysEnclosureFileModuleByMd5(map.get("md5").toString())))){
						beans = new ArrayList<>();
					}else{
						beans = JSONUtil.toList(jedisClient.get(Constants.getSysEnclosureFileModuleByMd5(map.get("md5").toString())).toString(), null);
					}
					beans.add(map);
					jedisClient.set(Constants.getSysEnclosureFileModuleByMd5(map.get("md5").toString()), JSONUtil.toJsonStr(beans));
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
	@SuppressWarnings({ "resource", "unchecked" })
	@Override
	@Transactional(value="transactionManager")
	public void insertUploadFileChunksByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		List<Map<String, Object>> beans = JSONUtil.toList(jedisClient.get(Constants.getSysEnclosureFileModuleByMd5(map.get("md5").toString())).toString(), null);
		List<File> fileList = new ArrayList<File>();
		File f;
		for(Map<String, Object> bean : beans){
			f = new File(tPath.replace("images", "") + bean.get("fileAddress").toString());
			fileList.add(f);
		}
		String fileName = map.get("name").toString();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//文件后缀
		String newFileName = String.valueOf(System.currentTimeMillis()) + "." + fileExtName;//新文件名
		String path = tPath + "\\upload\\enclosurefile" + "\\" + userId + "\\" + newFileName;//文件路径
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
		jedisClient.del(Constants.getSysEnclosureFileModuleByMd5(map.get("md5").toString()));
		//初始化文件对象内容
		String trueFileName = "/images/upload/enclosurefile/" + userId + "/" + newFileName ;
		map.put("fileType", fileExtName);//文件类型
		map.put("fileSizeType", "bytes");//文件大小单位
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", userId);
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("fileAddress", trueFileName);//文件地址
		sysEnclosureDao.insertUploadFileByUserId(map);
		outputObject.setBean(map);
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
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysEnclosureFileModuleByMd5(md5)))){
			beans = new ArrayList<>();
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.getSysEnclosureFileModuleByMd5(md5)).toString(), null);
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
				jedisClient.set(Constants.getSysEnclosureFileModuleByMd5(map.get("md5").toString()), JSONUtil.toJsonStr(beans));
				outputObject.setreturnMessage("文件上传失败");
			}
		}else{
			outputObject.setreturnMessage("文件上传失败");
		}
	}
	
	/**
	 * 
	     * @Title: querySysEnclosureListToTreeByUserId
	     * @Description: 获取我的附件库
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEnclosureListToTreeByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> beans = sysEnclosureDao.querySysEnclosureListToTreeByUserId(map);
		for(Map<String, Object> bean : beans){
			if("0".equals(bean.get("pId").toString())){
				bean.put("pId", "1");
			}
		}
		beans.addAll(Constants.getSysEnclosureZeroList());
		outputObject.setBeans(beans);
	}
	
	/**
	 * 
	     * @Title: queryAllPeopleToTree
	     * @Description: 人员选择获取所有公司和人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map = compareSelUserListByParams(map, inputObject);
		List<Map<String, Object>> beans = sysEnclosureDao.queryAllPeopleToTree(map);
		outputObject.setBeans(beans);
	}

	/**
	 * 
	     * @Title: queryCompanyPeopleToTreeByUserBelongCompany
	     * @Description: 人员选择根据当前用户所属公司获取这个公司的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCompanyPeopleToTreeByUserBelongCompany(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map = compareSelUserListByParams(map, inputObject);
		Map<String, Object> user = inputObject.getLogParams();
		//根据用户信息获取该用户所属的公司id。部门id，职位id
		Map<String, Object> company = sysEnclosureDao.queryCompanyMationByUserId(user);
		map.put("companyId", company.get("companyId"));
		List<Map<String, Object>> beans = sysEnclosureDao.queryCompanyPeopleToTreeByUserBelongCompany(map);
		outputObject.setBeans(beans);
	}
	
	/**
	 * 
	     * @Title: queryDepartmentPeopleToTreeByUserBelongDepartment
	     * @Description: 人员选择根据当前用户所属公司获取这个公司部门展示的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDepartmentPeopleToTreeByUserBelongDepartment(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map = compareSelUserListByParams(map, inputObject);
		Map<String, Object> user = inputObject.getLogParams();
		//根据用户信息获取该用户所属的公司id。部门id，职位id
		Map<String, Object> company = sysEnclosureDao.queryCompanyMationByUserId(user);
		map.put("companyId", company.get("companyId"));
		List<Map<String, Object>> beans = sysEnclosureDao.queryDepartmentPeopleToTreeByUserBelongDepartment(map);
		outputObject.setBeans(beans);
	}
	
	/**
	 * 
	     * @Title: queryJobPeopleToTreeByUserBelongJob
	     * @Description: 人员选择根据当前用户所属公司获取这个公司岗位展示的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryJobPeopleToTreeByUserBelongJob(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map = compareSelUserListByParams(map, inputObject);
		Map<String, Object> user = inputObject.getLogParams();
		//根据用户信息获取该用户所属的公司id。部门id，职位id
		Map<String, Object> company = sysEnclosureDao.queryCompanyMationByUserId(user);
		map.put("companyId", company.get("companyId"));
		List<Map<String, Object>> beans = sysEnclosureDao.queryJobPeopleToTreeByUserBelongJob(map);
		outputObject.setBeans(beans);
	}
	
	/**
	 * 
	     * @Title: querySimpleDepPeopleToTreeByUserBelongSimpleDep
	     * @Description: 人员选择根据当前用户所属公司获取这个公司同级部门展示的人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySimpleDepPeopleToTreeByUserBelongSimpleDep(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map = compareSelUserListByParams(map, inputObject);
		Map<String, Object> user = inputObject.getLogParams();
		//根据用户信息获取该用户所属的公司id。部门id，职位id
		Map<String, Object> company = sysEnclosureDao.queryCompanyMationByUserId(user);
		map.put("departmentId", company.get("departmentId"));
		List<Map<String, Object>> beans = sysEnclosureDao.querySimpleDepPeopleToTreeByUserBelongSimpleDep(map);
		outputObject.setBeans(beans);
	}
	
	/**
	 * 
	     * @Title: queryTalkGroupUserListByUserId
	     * @Description: 根据聊天组展示用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryTalkGroupUserListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map = compareSelUserListByParams(map, inputObject);
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		List<Map<String, Object>> beans = sysEnclosureDao.queryTalkGroupUserListByUserId(map);
		outputObject.setBeans(beans);
	}
	
	/**
	 * 获取人员列表时的参数转换
	 * @param map
	 * @param inputObject
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> compareSelUserListByParams(Map<String, Object> map, InputObject inputObject) throws Exception{
		//人员列表中是否包含自己--1.包含；其他参数不包含
		String chooseOrNotMy = map.get("chooseOrNotMy").toString();
		if(!"1".equals(chooseOrNotMy)){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("userId", user.get("id"));
		}
		//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		String chooseOrNotEmail = map.get("chooseOrNotEmail").toString();
		if("1".equals(chooseOrNotEmail)){
			map.put("hasEmail", "1");
		}
		return map;
	}

	/**
	 * 
	     * @Title: insertUploadFileToDataByUserId
	     * @Description: 一次性上传附件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	@Override
	public void insertUploadFileToDataByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(inputObject.getRequest().getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(inputObject.getRequest())) {
			String userId = inputObject.getLogParams().get("id").toString();
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) inputObject.getRequest();
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();
			//决定存储路径
			String basePath = tPath + "\\upload\\enclosurefile\\" + userId;
			String trueFileName = "";
			String fileName = "";
			while (iter.hasNext()) {
				// 一次遍历所有文件
				MultipartFile file = multiRequest.getFile(iter.next().toString());
				fileName = file.getOriginalFilename();// 文件名称
				//得到文件扩展名
				String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
				if (file != null) {
					File pack = new File(basePath);
					if(!pack.isDirectory())//目录不存在 
						pack.mkdirs();//创建目录
					// 自定义的文件名称
    				String newFileName = String.valueOf(System.currentTimeMillis()) + "." + fileExtName;
					String path = basePath + "\\" + newFileName;
					// 上传
					file.transferTo(new File(path));
					newFileName = "/images/upload/enclosurefile/" + userId + "/" + newFileName ;
					if(ToolUtil.isBlank(trueFileName)){
						trueFileName = newFileName;
					}else{
						trueFileName = trueFileName + "," + newFileName;
					}
					//存储附件到数据库
					Map<String, Object> bean = new HashMap<>();
					bean.put("fileType", fileExtName);//文件类型
					bean.put("fileSizeType", "bytes");//文件大小单位
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("createId", userId);
					bean.put("size", file.getSize());
					bean.put("folderId", "0");
					bean.put("name", fileName);
					bean.put("createTime", DateUtil.getTimeAndToString());
					bean.put("fileAddress", trueFileName);//文件地址
					sysEnclosureDao.insertUploadFileByUserId(bean);
					outputObject.setBean(bean);
					return;
				}
			}
		}
	}
	
}
