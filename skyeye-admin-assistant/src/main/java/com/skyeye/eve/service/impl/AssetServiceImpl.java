/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.AssetDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @ClassName: AssetServiceImpl
 * @Description: 资产管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:06
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetServiceImpl implements AssetService {

	@Autowired
	private AssetDao assetDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllAssetMation
	     * @Description: 遍历所有的资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = assetDao.selectAllAssetMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertAssetMation
	     * @Description: 新增资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = assetDao.queryAssetMationByAssetNum(map);
        if(bean != null && !bean.isEmpty()){
            outputObject.setreturnMessage("该资产编号已存在，不能重复添加！");
        }else{
            Set<Entry<String, Object>> entrys = map.entrySet();
            for(Map.Entry<String, Object> entry : entrys){
                if(ToolUtil.isBlank(entry.getValue().toString())){
                    map.put(entry.getKey(), null);
                }
            }
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createTime", DateUtil.getTimeAndToString());
            map.put("createId", inputObject.getLogParams().get("id"));
            Map<String, Object> cmap = new HashMap<>();
            cmap.put("id", inputObject.getLogParams().get("id"));//获取当前用户id
			Map<String, Object> b = sysEveUserStaffDao.queryCompanyIdByUserId(cmap);//根据用户id获取当前用户所在的公司
			map.put("companyId", b.get("companyId"));//将用户所在的公司作为资产的所属公司
            assetDao.insertAssetMation(map);
        }
	}

	/**
	 * 
	     * @Title: deleteAssetById
	     * @Description: 删除资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteAssetById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		assetDao.deleteAssetById(map);
	}

	/**
	 * 
	     * @Title: editAssetMationById
	     * @Description: 查询资产信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAssetMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = assetDao.queryAssetMationById(id);
		// 查询附件
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 查询资产管理人
		bean.put("assetAdmin", sysEveUserStaffDao.queryUserNameList(bean.get("assetAdmin").toString()));
		// 查询资产领用人
		bean.put("employeeId", sysEveUserStaffDao.queryUserNameList(bean.get("employeeId").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editAssetMationById
	     * @Description: 编辑资产
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAssetMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = assetDao.queryAssetMationByIdAndAssetNum(map);
        if(m == null){
            Set<Entry<String, Object>> entrys = map.entrySet();
            for(Map.Entry<String, Object> entry : entrys){
                if(ToolUtil.isBlank(entry.getValue().toString())){
                    map.put(entry.getKey(), null);
                }
            }
            assetDao.editAssetMationById(map);
        }else{
            outputObject.setreturnMessage("该资产编号已存在，不能重复保存");
        }
	}
	
	/**
	 * 
	     * @Title: selectAssetDetailsById
	     * @Description: 资产详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAssetDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = assetDao.selectAssetDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: updateAssetNormalById
	     * @Description: 资产恢复正常
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateAssetNormalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = assetDao.queryAssetState(map);
		if("2".equals(m.get("state").toString()) || "3".equals(m.get("state").toString())){//维修或者报废可以恢复正常
			assetDao.updateAssetNormalById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateAssetRepairById
	     * @Description: 资产维修
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateAssetRepairById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = assetDao.queryAssetState(map);
		if("1".equals(m.get("state").toString()) || "3".equals(m.get("state").toString())){//正常或者报废可以维修
			assetDao.updateAssetRepairById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateAssetScrapById
	     * @Description: 资产报废
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateAssetScrapById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = assetDao.queryAssetState(map);
		if("1".equals(m.get("state").toString()) || "2".equals(m.get("state").toString())){//正常或者维修可以报废
			assetDao.updateAssetScrapById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: queryUnUseAssetListByTypeId
	     * @Description: 根据资产类别获取未被使用的资产列表信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUnUseAssetListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		Map<String, Object> m = assetDao.queryUserCompanyById(user);
        map.put("companyId", m.get("companyId"));
		List<Map<String, Object>> beans = assetDao.queryUnUseAssetListByTypeId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

}
