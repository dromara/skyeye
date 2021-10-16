/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.app.wechat.util.PhoneConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.TAreaPhoneDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.TAreaPhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TAreaPhoneServiceImpl implements TAreaPhoneService{
	
	@Autowired
	private TAreaPhoneDao tAreaPhoneDao;
	
	@Autowired
    private JedisClientService jedisClient;

	/**
	 * 
	     * @Title: queryTAreaPhoneList
	     * @Description: 手机端查询省市区数据
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryTAreaPhoneList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(PhoneConstants.SYS_ALL_T_AREA_LIST))){
            beans = tAreaPhoneDao.queryTAreaPhoneList(map);
			beans = ToolUtil.listToTree(beans, "codeId", "parentCodeId", "children");
            jedisClient.set(PhoneConstants.SYS_ALL_T_AREA_LIST, JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(PhoneConstants.SYS_ALL_T_AREA_LIST), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}
	
}
