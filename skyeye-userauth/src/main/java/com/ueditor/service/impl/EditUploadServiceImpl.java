/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.ueditor.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.EditUploadDao;
import com.skyeye.jedis.JedisClientService;
import com.ueditor.service.EditUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EditUploadServiceImpl implements EditUploadService{
	
	@Autowired
	private EditUploadDao editUploadDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Value("${IMAGES_PATH}")
	private String tPath;
	
	/**
	 * 上传富文本图片
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> uploadContentPic(HttpServletRequest req) throws Exception {
		Map<String,Object> rs = new HashMap<>();
        MultipartHttpServletRequest mReq  =  null;
        MultipartFile file = null;
        String fileName = "";
        // 原始文件名   UEDITOR创建页面元素时的alt和title属性
        String originalFileName = "";
        try {
            mReq = (MultipartHttpServletRequest)req;
            // 从config.json中取得上传文件的ID
            file = mReq.getFile("upfile");
            // 取得文件的原始文件名称
            fileName = file.getOriginalFilename();
            originalFileName = fileName;
            /*你的处理图片的代码*/
            rs.put("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
            rs.put("title", originalFileName);
            rs.put("original", originalFileName);   
			File dirname = new File(tPath + "\\upload\\ueditor"); 
			if (!dirname.isDirectory()){ //目录不存在 
			     dirname.mkdirs(); //创建目录
			} 
			// 项目在容器中实际发布运行的根路径
			String realPath = tPath + "\\upload\\ueditor\\";
			// 自定义的文件名称
			String type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
			String trueFileName = String.valueOf(System.currentTimeMillis()) + "." + type;
			String path = realPath + trueFileName; /* System.getProperty("file.separator")+ */
			file.transferTo(new File(path));
			//能访问到你现在图片的路径
			String filePath = "/images/upload/ueditor/" + trueFileName;
			rs.put("url", filePath);         
			Map<String,Object> bean = new HashMap<>();
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("imgPath", filePath);
			bean.put("fileOriginalName", fileName);
			// 用户信息
			Map<String, Object> userMation = JSONUtil.toBean(jedisClient.get("userMation:" + req.getParameter("userToken").toString()), null);
			bean.put("createId", userMation.get("id"));
			bean.put("createTime", DateUtil.getTimeAndToString());
			bean.put("createType", "2");
			editUploadDao.insertFileImgMation(bean);
        } catch (Exception e) {
            rs.put("state", "文件上传失败!"); //在此处写上错误提示信息，这样当错误的时候就会显示此信息
            rs.put("url","");
            rs.put("title", "");
            rs.put("original", "");
        }
        return rs;
	}

	/**
	 * 回显富文本图片
	 */
	@Override
	public Map<String, Object> downloadContentPic(HttpServletRequest req) throws Exception {
		Map<String,Object> rs = new HashMap<>();
		rs.put("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
		Map<String,Object> bean = new HashMap<>();
		// 用户信息
		Map<String, Object> userMation = JSONUtil.toBean(jedisClient.get("userMation:" + req.getParameter("userToken").toString()), null);
		bean.put("createId", userMation.get("id"));
		bean.put("url", "");
		int page = Integer.parseInt(req.getParameter("start"))/Integer.parseInt(req.getParameter("size"));
		page++;
		int limit = Integer.parseInt(req.getParameter("size"));
		Page pages = PageHelper.startPage(page, limit);
		List<Map<String,Object>> beans = editUploadDao.queryFileImgMation(bean);
		//获取当前页数的总数
		long total = pages.getTotal();
		rs.put("list", beans);
		rs.put("totle", total);
		return rs;
	}
	
	
	
}
