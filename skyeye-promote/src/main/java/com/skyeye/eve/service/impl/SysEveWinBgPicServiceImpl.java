package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEveWinBgPicDao;
import com.skyeye.eve.service.SysEveWinBgPicService;

@Service
public class SysEveWinBgPicServiceImpl implements SysEveWinBgPicService{
	
	@Autowired
	private SysEveWinBgPicDao sysEveWinBgPicDao;

	/**
	 * 
	     * @Title: querySysEveWinBgPicList
	     * @Description: 获取win系统桌面图片列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveWinBgPicList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveWinBgPicDao.querySysEveWinBgPicList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertSysEveWinBgPicMation
	     * @Description: 添加win系统桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysEveWinBgPicMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		sysEveWinBgPicDao.insertSysEveWinBgPicMation(map);
	}

	/**
	 * 
	     * @Title: deleteSysEveWinBgPicMationById
	     * @Description: 删除win系统桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void deleteSysEveWinBgPicMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveWinBgPicDao.querySysEveMationById(map);
		String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
		String basePath = tPath + bean.get("picUrl").toString();
		ToolUtil.deleteFile(basePath);
		sysEveWinBgPicDao.deleteSysEveWinBgPicMationById(map);
	}

	/**
	 * 
	     * @Title: querySysEveWinBgPicList
	     * @Description: 获取win系统桌面图片列表供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveWinBgPicListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveWinBgPicDao.querySysEveWinBgPicListToShow(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertSysEveWinBgPicMationByCustom
	     * @Description: 用户自定义上传添加win系统桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysEveWinBgPicMationByCustom(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		sysEveWinBgPicDao.insertSysEveWinBgPicMationByCustom(map);
	}

	/**
	 * 
	     * @Title: querySysEveWinBgPicCustomList
	     * @Description: 获取win系统桌面图片列表用户自定义
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveWinBgPicCustomList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		List<Map<String, Object>> beans = sysEveWinBgPicDao.querySysEveWinBgPicCustomList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: deleteSysEveWinBgPicMationById
	     * @Description: 删除win系统桌面图片信息用户自定义
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void deleteSysEveWinBgPicMationCustomById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveWinBgPicDao.querySysEveMationById(map);
		String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
		String basePath = tPath.substring(0, inputObject.getRequest().getSession().getServletContext().getRealPath("/").indexOf(Constants.PROJECT_WEB)) + bean.get("picUrl").toString();
		ToolUtil.deleteFile(basePath);
		sysEveWinBgPicDao.deleteSysEveWinBgPicMationById(map);
	}
	
}
