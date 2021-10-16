/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEveDesktopDao;
import com.skyeye.eve.service.SysEveDesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysEveDesktopServiceImpl implements SysEveDesktopService {

	@Autowired
	private SysEveDesktopDao sysEveDesktopDao;
	
	/**
	 * 
	     * @Title: querySysDesktopList
	     * @Description: 查出所有桌面名称列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDesktopList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveDesktopDao.querySysDesktopList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertSysDesktopMation
	     * @Description: 新增桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysDesktopMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopByDesktopName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该桌面名称已存在，请更换");
		}else{
			Map<String, Object> itemCount = sysEveDesktopDao.querySysDesktopBySimpleLevel(map);
			Map<String, Object> user = inputObject.getLogParams();
			int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
			map.put("orderBy", thisOrderBy);
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", "1");//默认新建
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			sysEveDesktopDao.insertSysDesktopMation(map);
		}
	}
	
	/**
	 * 
	     * @Title: deleteSysDesktopById
	     * @Description: 删除桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopStateAndMenuNumById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以删除
			if("0".equals(bean.get("allNum").toString())){ //该桌面下没有菜单可以删除
                sysEveDesktopDao.deleteSysDesktopById(map);
            }else{
                outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
            }
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateUpSysDesktopById
	     * @Description: 上线桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以上线
			sysEveDesktopDao.updateUpSysDesktopById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownSysDesktopById
	     * @Description: 下线桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			sysEveDesktopDao.updateDownSysDesktopById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectSysDesktopById
	     * @Description: 通过id查找对应的桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = sysEveDesktopDao.selectSysDesktopById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysDesktopMationById
	     * @Description: 通过id编辑对应的桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDesktopMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以编辑
			Map<String, Object> b = sysEveDesktopDao.querySysDesktopByDesktopName(map);
			if(b != null && !b.isEmpty()){
				outputObject.setreturnMessage("该桌面名称已存在，请更换");
			}else{
				sysEveDesktopDao.editSysDesktopMationById(map);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editSysDesktopMationOrderNumUpById
	     * @Description: 桌面名称上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDesktopMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前桌面名称已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysEveDesktopDao.editSysDesktopMationOrderNumUpById(map);
			sysEveDesktopDao.editSysDesktopMationOrderNumUpById(bean);
		}
	}
	
	/**
	 * 
	     * @Title: editSysDesktopMationOrderNumDownById
	     * @Description: 桌面名称下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDesktopMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveDesktopDao.querySysDesktopDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前桌面名称已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysEveDesktopDao.editSysDesktopMationOrderNumUpById(map);
			sysEveDesktopDao.editSysDesktopMationOrderNumUpById(bean);
		}
	}

	/**
     * 
         * @Title: queryAllSysDesktopList
         * @Description: 获取全部的桌面名称用于系统菜单
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryAllSysDesktopList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String language = map.get("language").toString();
        List<Map<String, Object>> beans = sysEveDesktopDao.queryAllSysDesktopList(map);
        Map<String, Object> m = new HashMap<>();
        m.put("name", Constants.LANGUAGE_ZH.equals(language) ? "默认桌面" : "Default desktop");
        m.put("id", "winfixedpage00000000");
        beans.add(0, m);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
    
    /**
     * 
         * @Title: removeAllSysEveMenuByDesktopId
         * @Description: 一键移除所有菜单
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void removeAllSysEveMenuByDesktopId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("desktopId", "winfixedpage00000000");
        sysEveDesktopDao.removeAllSysEveMenuByDesktopId(map);
    }
	
}
