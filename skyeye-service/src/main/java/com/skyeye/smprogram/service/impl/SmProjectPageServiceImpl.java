package com.skyeye.smprogram.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClient;
import com.skyeye.smprogram.dao.SmProjectPageDao;
import com.skyeye.smprogram.service.SmProjectPageService;

@Service
public class SmProjectPageServiceImpl implements SmProjectPageService{
	
	@Autowired
	private SmProjectPageDao smProjectPageDao;
	
	@Autowired
	public JedisClient jedisClient;
	
	/**
	 * 
	     * @Title: queryProPageMationByProIdList
	     * @Description: 根据项目获取项目内部的页面
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryProPageMationByProIdList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectPageDao.queryProPageMationByProIdList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertProPageMationByProId
	     * @Description: 添加项目内部的页面
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertProPageMationByProId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		if(!jedisClient.exists(Constants.REDIS_PROJECT_PAGE_FILE_PATH)){//判断redis中是否存在文件路径--不存在
			Map<String, Object> redisFilePathNum = smProjectPageDao.queryFilePathNumMaxMation(map);
			if(redisFilePathNum == null){
				jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_PATH, Constants.REDIS_PROJECT_PAGE_FILE_PATH_NUM);//如果表中没数据，默认插入这条数据
			}else{
				if(redisFilePathNum.containsKey("redisFilePathNum")){
					jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_PATH, redisFilePathNum.get("redisFilePathNum").toString());//如果表中有数据，默认插入这条数据的值
				}else{
					jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_PATH, Constants.REDIS_PROJECT_PAGE_FILE_PATH_NUM);//如果表中没数据，默认插入这条数据
				}
			}
		}
		
		if(!jedisClient.exists(Constants.REDIS_PROJECT_PAGE_FILE_NAME)){//判断redis中是否存在文件名称--不存在
			Map<String, Object> redisFilePathNum = smProjectPageDao.queryFileNameNumMaxMation(map);
			if(redisFilePathNum == null){
				jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_NAME, Constants.REDIS_PROJECT_PAGE_FILE_NAME_NUM);//如果表中没数据，默认插入这条数据
			}else{
				if(redisFilePathNum.containsKey("redisFilePathNum")){
					jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_NAME, redisFilePathNum.get("redisFileNameNum").toString());//如果表中有数据，默认插入这条数据的值
				}else{
					jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_NAME, Constants.REDIS_PROJECT_PAGE_FILE_PATH_NUM);//如果表中没数据，默认插入这条数据
				}
			}
		}
		map.put("defaultFilePath", "page" + jedisClient.get(Constants.REDIS_PROJECT_PAGE_FILE_PATH).toString());//初始化默认文件路径
		map.put("defaultFileName", "page" + jedisClient.get(Constants.REDIS_PROJECT_PAGE_FILE_NAME).toString());//初始化默认文件名称
		map.put("defaultFilePathNum", jedisClient.get(Constants.REDIS_PROJECT_PAGE_FILE_PATH).toString());//初始化默认文件路径序列号
		map.put("defaultFileNameNum", jedisClient.get(Constants.REDIS_PROJECT_PAGE_FILE_NAME).toString());//初始化默认文件名称序列号
		jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_PATH, String.valueOf(Integer.parseInt(jedisClient.get(Constants.REDIS_PROJECT_PAGE_FILE_PATH).toString()) + 1));
		jedisClient.set(Constants.REDIS_PROJECT_PAGE_FILE_NAME, String.valueOf(Integer.parseInt(jedisClient.get(Constants.REDIS_PROJECT_PAGE_FILE_NAME).toString()) + 1));
		
		//获取当前项目中页面排序序列值最大的一个
		Map<String, Object> sortMax = smProjectPageDao.querySortMaxMationByProjectId(map);
		if(sortMax == null){
			map.put("sort", 1);
		}else{
			if(sortMax.containsKey("sort")){
				map.put("sort", Integer.parseInt(sortMax.get("sort").toString()) + 1);
			}else{
				map.put("sort", 1);
			}
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		smProjectPageDao.insertProPageMationByProId(map);
	}

	/**
	 * 
	     * @Title: editSmProjectPageSortTopById
	     * @Description: 小程序页面展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSmProjectPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = smProjectPageDao.querySmProjectPageISTopByThisId(map);//根据排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前页面，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			smProjectPageDao.editSmProjectPageSortTopById(map);
			smProjectPageDao.editSmProjectPageSortTopById(topBean);
		}
	}

	/**
	 * 
	     * @Title: editSmProjectPageSortLowerById
	     * @Description: 小程序页面展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSmProjectPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = smProjectPageDao.querySmProjectPageISLowerByThisId(map);//根据排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后页面，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			smProjectPageDao.editSmProjectPageSortLowerById(map);
			smProjectPageDao.editSmProjectPageSortLowerById(topBean);
		}
	}
	
	
	
}
