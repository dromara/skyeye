package com.skyeye.authority.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.authority.dao.SysEveWinLockBgPicDao;
import com.skyeye.authority.service.SysEveWinLockBgPicService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class SysEveWinLockBgPicServiceImpl implements SysEveWinLockBgPicService{
	
	@Autowired
	private SysEveWinLockBgPicDao sysEveWinLockBgPicDao;

	/**
	 * 
	     * @Title: querySysEveWinLockBgPicList
	     * @Description: 获取win系统锁屏桌面图片列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveWinLockBgPicList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveWinLockBgPicDao.querySysEveWinLockBgPicList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertSysEveWinLockBgPicMation
	     * @Description: 添加win系统锁屏桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysEveWinLockBgPicMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		sysEveWinLockBgPicDao.insertSysEveWinLockBgPicMation(map);
	}

	/**
	 * 
	     * @Title: deleteSysEveWinLockBgPicMationById
	     * @Description: 删除win系统锁屏桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void deleteSysEveWinLockBgPicMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveWinLockBgPicDao.querySysEveMationById(map);
		String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
		String basePath = tPath + bean.get("picUrl").toString();
		ToolUtil.deleteFile(basePath);
		sysEveWinLockBgPicDao.deleteSysEveWinLockBgPicMationById(map);
	}

}
