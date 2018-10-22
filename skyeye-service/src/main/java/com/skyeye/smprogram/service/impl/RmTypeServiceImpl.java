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
import com.skyeye.smprogram.dao.RmTypeDao;
import com.skyeye.smprogram.service.RmTypeService;

@Service
public class RmTypeServiceImpl implements RmTypeService{
	
	@Autowired
	private RmTypeDao rmTypeDao;

	/**
	 * 
	     * @Title: queryRmTypeList
	     * @Description: 获取小程序分类列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = rmTypeDao.queryRmTypeList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertRmTypeMation
	     * @Description: 新增小程序分类列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertRmTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmTypeDao.queryRmTypeByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			Map<String, Object> item = rmTypeDao.queryRmTypeISTop(map);//获取最靠前的小程序分类
			if(item == null){
				map.put("sort", 1);
			}else{
				map.put("sort", Integer.parseInt(item.get("sort").toString()) + 1);
			}
			rmTypeDao.insertRmTypeMation(map);
		}else{
			outputObject.setreturnMessage("该类型名称已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteRmTypeMationById
	     * @Description: 删除小程序分类信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteRmTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmTypeDao.queryRmTypeGroupNumById(map);
		if(bean == null){
			rmTypeDao.deleteRmTypeById(map);
		}else{
			if(Integer.parseInt(bean.get("groupNum").toString()) == 0){//该小程序分类下没有分组
				rmTypeDao.deleteRmTypeById(map);
			}else{
				outputObject.setreturnMessage("该类型下存在小程序分组，无法删除。");
			}
		}
	}

	/**
	 * 
	     * @Title: queryRmTypeMationToEditById
	     * @Description: 编辑小程序分类信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmTypeDao.queryRmTypeMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmTypeMationById
	     * @Description: 编辑小程序分类信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmTypeDao.queryRmTypeMationByIdAndName(map);
		if(bean == null){
			rmTypeDao.editRmTypeMationById(map);
		}else{
			outputObject.setreturnMessage("该类型名称已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: editRmTypeSortTopById
	     * @Description: 小程序分类展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = rmTypeDao.queryRmTypeISTopByThisId(map);//根据排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前分类，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			rmTypeDao.editRmTypeSortTopById(map);
			rmTypeDao.editRmTypeSortTopById(topBean);
		}
	}

	/**
	 * 
	     * @Title: editRmTypeSortLowerById
	     * @Description: 小程序分类展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = rmTypeDao.queryRmTypeISLowerByThisId(map);//根据排序获取这条数据的下一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后分类，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			rmTypeDao.editRmTypeSortTopById(map);
			rmTypeDao.editRmTypeSortTopById(topBean);
		}
	}
	
	
	
}
