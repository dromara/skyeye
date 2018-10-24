package com.skyeye.smprogram.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.smprogram.dao.RmGroupMemberDao;
import com.skyeye.smprogram.service.RmGroupMemberService;

@Service
public class RmGroupMemberServiceImpl implements RmGroupMemberService{
	
	@Autowired
	private RmGroupMemberDao rmGroupMemberDao;

	/**
	 * 
	     * @Title: queryRmGroupMemberList
	     * @Description: 获取小程序组件列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmGroupMemberList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String tPath = "assets/smpropic/" ;
		map.put("basePath", tPath);
		List<Map<String, Object>> beans = rmGroupMemberDao.queryRmGroupMemberList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
	/**
	 * 
	     * @Title: insertRmGroupMemberMation
	     * @Description: 添加小程序组件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertRmGroupMemberMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		Map<String, Object> item = rmGroupMemberDao.queryRmGroupMemberISTop(map);//获取最靠前的小程序组件
		if(item == null){
			map.put("sort", 1);
		}else{
			map.put("sort", Integer.parseInt(item.get("sort").toString()) + 1);
		}
		rmGroupMemberDao.insertRmGroupMemberMation(map);
	}

	/**
	 * 
	     * @Title: editRmGroupMemberSortTopById
	     * @Description: 小程序组件展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmGroupMemberSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = rmGroupMemberDao.queryRmGroupMemberISTopByThisId(map);//根据排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前组件，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			rmGroupMemberDao.editRmGroupMemberSortTopById(map);
			rmGroupMemberDao.editRmGroupMemberSortTopById(topBean);
		}
	}

	/**
	 * 
	     * @Title: editRmGroupMemberSortLowerById
	     * @Description: 小程序组件展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmGroupMemberSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = rmGroupMemberDao.queryRmGroupMemberISLowerByThisId(map);//根据排序获取这条数据的下一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后组件，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			rmGroupMemberDao.editRmGroupMemberSortTopById(map);
			rmGroupMemberDao.editRmGroupMemberSortTopById(topBean);
		}
	}

	/**
	 * 
	     * @Title: deleteRmGroupMemberById
	     * @Description: 删除小程序组件信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void deleteRmGroupMemberById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmGroupMemberDao.queryUseRmGroupMemberNumById(map);
		if(bean == null){
			String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
			Map<String, Object> item = rmGroupMemberDao.queryRmGroupMemberMationById(map);
			String basePath = tPath + "\\assets\\smpropic\\" + item.get("printsPicUrl").toString();
			ToolUtil.deleteFile(basePath);
			rmGroupMemberDao.deleteRmGroupMemberById(map);
			
		}else{
			if(Integer.parseInt(bean.get("groupUseMemberNum").toString()) == 0){//该组件没有正在使用中
				String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
				Map<String, Object> item = rmGroupMemberDao.queryRmGroupMemberMationById(map);
				String basePath = tPath + "\\assets\\smpropic\\" + item.get("printsPicUrl").toString();
				ToolUtil.deleteFile(basePath);
				rmGroupMemberDao.deleteRmGroupMemberById(map);
			}else{
				outputObject.setreturnMessage("该组件正在使用中，无法删除。");
			}
		}
	}

	/**
	 * 
	     * @Title: queryRmGroupMemberMationToEditById
	     * @Description: 编辑小程序组件信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmGroupMemberMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String tPath = "assets/smpropic/" ;
		map.put("basePath", tPath);
		Map<String, Object> bean = rmGroupMemberDao.queryRmGroupMemberMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmGroupMemberMationById
	     * @Description: 编辑小程序组件信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("static-access")
	@Override
	public void editRmGroupMemberMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> item = rmGroupMemberDao.queryRmGroupMemberMationById(map);
		if(!item.get("printsPicUrl").toString().equals(map.get("img").toString())){//保存的图片和原来的图片不符
			//删除原来的图片
			String tPath = inputObject.getRequest().getSession().getServletContext().getRealPath("/");
			String basePath = tPath + "\\assets\\smpropic\\" + item.get("printsPicUrl").toString();
			ToolUtil.deleteFile(basePath);
		}
		rmGroupMemberDao.editRmGroupMemberMationById(map);
	}
	
}
