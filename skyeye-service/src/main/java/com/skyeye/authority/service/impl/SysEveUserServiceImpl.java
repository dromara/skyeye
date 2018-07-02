package com.skyeye.authority.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.authority.dao.SysEveUserDao;
import com.skyeye.authority.service.SysEveUserService;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Service
public class SysEveUserServiceImpl implements SysEveUserService{
	
	@Autowired
	public SysEveUserDao sysEveUserDao;
	
	/**
	 * 
	     * @Title: querySysUserList
	     * @Description: 获取管理员用户列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveUserDao.querySysUserList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: editSysUserLockStateToLockById
	     * @Description: 锁定账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserLockStateById(map);
		if(Constants.SYS_USER_LOCK_STATE_ISUNLOCK.equals(bean.get("userLock").toString())){//未锁定
			map.put("userLock", Constants.SYS_USER_LOCK_STATE_ISLOCK);//锁定
			sysEveUserDao.editSysUserLockStateToLockById(map);
		}else{
			outputObject.setreturnMessage("该账号已被锁定，请刷新页面.");
		}
	}

	/**
	 * 
	     * @Title: editSysUserLockStateToUnLockById
	     * @Description: 解锁账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserLockStateById(map);
		if(Constants.SYS_USER_LOCK_STATE_ISLOCK.equals(bean.get("userLock").toString())){//锁定
			map.put("userLock", Constants.SYS_USER_LOCK_STATE_ISUNLOCK);//解锁
			sysEveUserDao.editSysUserLockStateToUnLockById(map);
		}else{
			outputObject.setreturnMessage("该账号已解锁，请刷新页面.");
		}
	}
	
	/**
	 * 
	     * @Title: querySysUserMationToEditById
	     * @Description: 编辑账号时获取账号信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserDao.querySysUserMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysUserMationById
	     * @Description: 编辑账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysEveUserDao.editSysUserMationById(map);
	}
	
	
	
}
