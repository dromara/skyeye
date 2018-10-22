package com.skyeye.common.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.skyeye.common.dao.CommonDao;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.CommonService;


@Service
public class CommonServiceImpl implements CommonService{
	
	@Autowired
	private CommonDao commonDao;

	/**
	 * 
	     * @Title: uploadFile
	     * @Description: 上传文件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void uploadFile(InputObject inputObject, OutputObject outputObject) throws Exception {

        List<MultipartFile> files =((MultipartHttpServletRequest)inputObject.getRequest()).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        String names = "";
        for (int i =0; i< files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                String fileName =file.getOriginalFilename();
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                String filePath=System.currentTimeMillis()+suffix;
                File f = new File("");
                if(!f.exists()){
                    f.mkdirs();
                }
                
            }
        }
	}
	
}
