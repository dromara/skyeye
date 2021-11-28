/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.eve.dao.SysScheduleCommonDao;
import com.skyeye.eve.service.SysScheduleCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysScheduleCommonServiceImpl
 * @Description: 节假日公共部分
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/28 9:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysScheduleCommonServiceImpl implements SysScheduleCommonService {

    @Autowired
    private SysScheduleCommonDao sysScheduleCommonDao;

    /**
     * 判断指定日期是否是节假日
     *
     * @param day 日期，格式为yyyy-mm-dd
     * @return true：是节假日；false：不是节假日
     * @throws Exception
     */
    @Override
    public boolean judgeISHoliday(String day) throws Exception {
        List<Map<String, Object>> holiday = sysScheduleCommonDao.queryWhetherIsHolidayByDate(day);
        if(holiday == null || holiday.isEmpty()){
            return false;
        }
        return true;
    }

}
