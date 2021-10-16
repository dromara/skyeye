/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.*;
import com.skyeye.eve.service.CommonService;
import com.skyeye.jedis.JedisClientService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @ClassName: CommonServiceImpl
 * @Description: 公共类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:29
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CommonServiceImpl implements CommonService{

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceImpl.class);
	
	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private SysEveWinBgPicDao sysEveWinBgPicDao;
	
	@Autowired
	private SysEveWinLockBgPicDao sysEveWinLockBgPicDao;
	
	@Autowired
	private SysEveWinThemeColorDao sysEveWinThemeColorDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;
	
	@Value("${IMAGES_PATH}")
	private String tPath;
	
	/**
	 * 
	     * @Title: uploadFile
	     * @Description: 上传文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void uploadFile(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(inputObject.getRequest().getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(inputObject.getRequest())) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) inputObject.getRequest();
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();
			int type = Integer.parseInt(map.get("type").toString());
			String basePath = tPath + Constants.FileUploadPath.getSavePath(type);
			Map<String, Object> bean = new HashMap<>();
			StringBuffer trueFileName = new StringBuffer();
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
    				String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
					String path = basePath + "/" + newFileName;
					LOGGER.info("upload file type is: {}, path is: {}", type, path);
					// 上传
					file.transferTo(new File(path));
					newFileName = Constants.FileUploadPath.getVisitPath(type) + newFileName;
					if(ToolUtil.isBlank(trueFileName.toString())){
						trueFileName.append(newFileName);
					}else{
						trueFileName.append(",").append(newFileName);
					}
				}
			}
			bean.put("picUrl", trueFileName.toString());
			bean.put("type", type);
			bean.put("fileName", fileName);
			outputObject.setBean(bean);
		}
	}

	/**
	 * 
	     * @Title: uploadFileBase64
	     * @Description: 上传文件Base64
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void uploadFileBase64(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		int type = Integer.parseInt(map.get("type").toString());
		String dataPrix = "";
		String data = "";//数据
		String imgStr = map.get("images").toString();
		imgStr = imgStr.replaceAll("\\+", "%2B").replaceAll(" ", "+");
		boolean a = false;//判断后缀是否为图片
        String fileType = null;//文件后缀
        String [] d = imgStr.split("base64,");
        // 决定存储路径
		String basePath = tPath + Constants.FileUploadPath.getSavePath(type);
		// 上传数据是否合法
        if(d != null && d.length == 2){
        	dataPrix = d[0];
            data = d[1];
          	// 获取上传图片后缀并判断数据是否合法
            if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){//data:image/jpeg;base64,base64编码的jpeg图片数据
    			fileType = "jpg";
    			a = true;
            } else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){//data:image/x-icon;base64,base64编码的icon图片数据
            	fileType = "ico";
            	a = true;
            } else if("data:image/gif;".equalsIgnoreCase(dataPrix)){//data:image/gif;base64,base64编码的gif图片数据
            	fileType = "gif";
            	a = true;
            } else if("data:image/png;".equalsIgnoreCase(dataPrix)){//data:image/png;base64,base64编码的png图片数据
            	fileType = "png";
            	a = true;
            }else{
            	outputObject.setreturnMessage("文件类型不正确，只允许上传jpg,png,jpeg格式的图片");
            }
            if(a){
    			try {
    				Base64 base64 = new Base64();
    				byte[] bytes = base64.decodeBase64(new String(data).getBytes());
    				File dirname = new File(basePath);
    				if (!dirname.isDirectory())// 目录不存在
    					dirname.mkdirs(); // 创建目录
    				// 自定义的文件名称
    				String trueFileName = String.valueOf(System.currentTimeMillis()) + "." + fileType;
    				// 设置存放图片文件的路径
    				String path = basePath + "\\" + trueFileName; 
    				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    				byte[] buffer = new byte[1024];
    				FileOutputStream out = new FileOutputStream(path);
    				int byteread;
    				while ((byteread = in.read(buffer)) > 0) {
    					out.write(buffer, 0, byteread); // 文件写操作
    				}
    				out.flush();
    				out.close();
    				Map<String, Object> bean = new HashMap<>();
    				bean.put("picUrl", trueFileName);
    				bean.put("type", type);
    				outputObject.setBean(bean);
    			} catch (Exception e) {

    			}
    		} else {
    			outputObject.setreturnMessage("文件类型不正确，只允许上传jpg,png,jpeg格式的图片");
    		}
        }else{
        	outputObject.setreturnMessage("上传失败，数据不合法");
        }
	}

	/**
	 * 
	     * @Title: downloadFileByJsonData
	     * @Description: 代码生成器生成下载文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void downloadFileByJsonData(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonData").toString(), null);
		List<Map<String, Object>> inBeans = new ArrayList<>();
		Map<String, Object> user = inputObject.getLogParams();
		String zipName = ToolUtil.getSurFaceId() + ".zip";
		String strZipPath = tPath + "/" + zipName;
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));
		byte[] buffer = new byte[1024];
		
		for(int i = 0; i < array.size(); i++){
			JSONObject object = (JSONObject) array.get(i);
			//加入压缩包
			ByteArrayInputStream stream = new ByteArrayInputStream(object.getStr("content").getBytes());
			if("javascript".equals(object.getStr("modelType").toLowerCase())){
				out.putNextEntry(new ZipEntry(object.getStr("fileName") + ".js"));
			}else{
				out.putNextEntry(new ZipEntry(object.getStr("fileName") + "." + object.getStr("modelType").toLowerCase()));
			}
			int len;
			// 读入需要下载的文件的内容，打包到zip文件
			while ((len = stream.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.closeEntry();
			Map<String, Object> bean = new HashMap<>();
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("tableName", object.getStr("tableName"));
			bean.put("groupId", object.getStr("groupId"));
			bean.put("modelId", object.getStr("modelId"));
			bean.put("content", object.getStr("content"));
			bean.put("createId", user.get("id"));
			bean.put("fileName", object.getStr("fileName"));
			if("javascript".equals(object.getStr("modelType").toLowerCase())){
				bean.put("fileType", "js");
			}else{
				bean.put("fileType", object.getStr("modelType").toLowerCase());
			}
			bean.put("filePath", zipName);
			bean.put("createTime", DateUtil.getTimeAndToString());
			inBeans.add(bean);
		}
		out.close();
		commonDao.insertCodeModelHistory(inBeans);
	}

	/**
	 * 
	     * @Title: querySysWinMationById
	     * @Description: 获取win系统桌列表信息供展示
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void querySysWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取win系统桌面图片列表供展示
		List<Map<String, Object>> winBgPic = null;
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysWinBgPicRedisKey()))){
			winBgPic = sysEveWinBgPicDao.querySysEveWinBgPicListToShow(map);
			jedisClient.set(Constants.getSysWinBgPicRedisKey(), JSONUtil.toJsonStr(winBgPic));
		}else{
			winBgPic = JSONUtil.toList(jedisClient.get(Constants.getSysWinBgPicRedisKey()), null);
		}
		//获取win系统锁屏桌面图片列表供展示
		List<Map<String, Object>> winLockBgPic = null;
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysWinLockBgPicRedisKey()))){
			winLockBgPic = sysEveWinLockBgPicDao.querySysEveWinBgPicListToShow(map);
			jedisClient.set(Constants.getSysWinLockBgPicRedisKey(), JSONUtil.toJsonStr(winLockBgPic));
		}else{
			winLockBgPic = JSONUtil.toList(jedisClient.get(Constants.getSysWinLockBgPicRedisKey()), null);
		}
		//获取win系统主题颜色列表供展示
		List<Map<String, Object>> winThemeColor = null;
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysWinThemeColorRedisKey()))){
			winThemeColor = sysEveWinThemeColorDao.querySysEveWinThemeColorListToShow(map);
			jedisClient.set(Constants.getSysWinThemeColorRedisKey(), JSONUtil.toJsonStr(winThemeColor));
		}else{
			winThemeColor = JSONUtil.toList(jedisClient.get(Constants.getSysWinThemeColorRedisKey()), null);
		}
		map.put("winBgPic", winBgPic);
		map.put("winLockBgPic", winLockBgPic);
		map.put("winThemeColor", winThemeColor);
		outputObject.setBean(map);
		outputObject.settotal(1);
	}
	
	/**
     * 
         * @Title: queryAllSysUserIsIncumbency
         * @Description: 获取所有在职的，拥有账号的员工
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryAllSysUserIsIncumbency(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = sysEveUserStaffDao.queryAllSysUserIsIncumbency(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
	
}
