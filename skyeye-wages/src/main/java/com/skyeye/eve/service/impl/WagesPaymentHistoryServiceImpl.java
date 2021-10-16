/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.WagesPaymentHistoryDao;
import com.skyeye.eve.service.WagesPaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: WagesPaymentHistoryServiceImpl
 * @Description: 薪资发放历史管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:34
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class WagesPaymentHistoryServiceImpl implements WagesPaymentHistoryService {

    @Autowired
    private WagesPaymentHistoryDao wagesPaymentHistoryDao;

    public static enum STATE{
        START_WAIT_GRANT(1, "待发放"),
        START_GRANT(2, "已发放");
        private int state;
        private String name;
        STATE(int state, String name){
            this.state = state;
            this.name = name;
        }
        public int getState() {
            return state;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 获取所有已发放薪资发放历史列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAllGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("state", STATE.START_GRANT.getState());
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesPaymentHistoryDao.queryAllWagesPaymentHistoryList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取我的薪资发放历史列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMyWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("staffId", inputObject.getLogParams().get("staffId"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesPaymentHistoryDao.queryMyWagesPaymentHistoryList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取所有待发放薪资列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAllNotGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("state", STATE.START_WAIT_GRANT.getState());
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesPaymentHistoryDao.queryAllWagesPaymentHistoryList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }
}
