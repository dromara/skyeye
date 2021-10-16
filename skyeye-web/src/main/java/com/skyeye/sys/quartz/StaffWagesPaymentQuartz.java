/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sys.quartz;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.dao.WagesPaymentHistoryDao;
import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import com.skyeye.eve.service.SysQuartzRunHistoryService;
import com.skyeye.jedis.JedisClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: StaffWagesPaymentQuartz
 * @Description: 薪资发放定时任务，每月15日上午10:15触发
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 21:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class StaffWagesPaymentQuartz {

    private static Logger LOGGER = LoggerFactory.getLogger(StaffWagesPaymentQuartz.class);

    private static final String QUARTZ_ID = QuartzConstants.SysQuartzMateMationJobType.STAFF_WAGES_PAYMENT_QUARTZ.getQuartzId();

    @Autowired
    private JedisClientService jedisClient;

    @Autowired
    private WagesPaymentHistoryDao wagesPaymentHistoryDao;

    @Autowired
    private SysQuartzRunHistoryService sysQuartzRunHistoryService;

    /**
     * 当前薪资发放中的员工id存储在redis的key，因为存在多台机器同时处理员工薪资的情况，所以不去主动删除该缓存信息，等待自动失效即可
     */
    private static final String IN_WAGES_PAYMENT_STAFF_REDIS_KEY = "inWagesPaymentStaffRedisKey";

    /**
     * 定时发放薪资功能，每月15日上午10:15触发
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 15 10 15 * ?")
    public void staffWagesPayment() throws Exception {
        String historyId = sysQuartzRunHistoryService.startSysQuartzRun(QUARTZ_ID);
        LOGGER.info("staff wagesPayment month is start");
        try{
            // 获取上个月的年月
            String lastMonthDate = DateUtil.getLastMonthDate();
            while (true){
                // 获取一条未发放的员工薪资信息
                Map<String, Object> staffWages = wagesPaymentHistoryDao.queryWaitPaymentStaffHistory(lastMonthDate, getStaffIdsFromRedis());
                // 如果已经没有要发放薪资的员工，则停止统计
                if(staffWages == null){
                    break;
                }
                String staffId = staffWages.get("staffId").toString();
                // 判断该员工的薪资发放是否在处理中，如果在处理中，则进行下一条
                if(isInWagesPaymentRedisMation(staffId)){
                    continue;
                }
                try {
                    // 锁定该员工为处理中
                    addStaffIdInWagesPaymentRedisMation(staffId);
                    // 开始发放
                    paymentStaffWages(staffWages, lastMonthDate);
                } catch (Exception e){
                    LOGGER.warn("deal with staff failed, staffId is {}", staffId, e);
                    break;
                } finally {
                    // 从正在处理中的员工集合数据中移除，说明该员工的薪资已经处理完成
                    removeStaffIdInWagesPaymentRedisMation(staffId);
                }
            }
            deleteWagesPaymentRedisMation();
        } catch (Exception e){
            sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_ERROR.getState());
            LOGGER.warn("StaffWagesPaymentQuartz error.", e);
        }
        LOGGER.info("staff wagesPayment month is end");
        sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_SUCCESS.getState());
    }

    /**
     * 薪资发放
     *
     * @param staffWages 员工薪资信息
     * @param lastMonthDate 上个月的年月
     */
    private void paymentStaffWages(Map<String, Object> staffWages, String lastMonthDate) throws Exception {
        String staffId = staffWages.get("staffId").toString();
        // 这里处理薪资发放的信息




        wagesPaymentHistoryDao.editToPaymentByStaffAndPayMonth(staffId, lastMonthDate);
    }

    /**
     * 加入到redis缓存
     *
     * @param staffIds 员工ids
     */
    private void setToRedis(List<String> staffIds){
        // 默认存储时间为六个小时
        jedisClient.set(IN_WAGES_PAYMENT_STAFF_REDIS_KEY, JSONUtil.toJsonStr(staffIds), 60 * 60 * 6);
    }

    /**
     * 移除指定的员工id存储在redis的缓存
     *
     * @param staffId 员工id
     */
    private void removeStaffIdInWagesPaymentRedisMation(String staffId){
        List<String> staffIds = getStaffIdsFromRedis();
        staffIds = staffIds.stream().filter(str -> !staffId.equals(str)).collect(Collectors.toList());
        setToRedis(staffIds);
    }

    /**
     * 添加指定的员工id存储在redis的缓存
     *
     * @param staffId 员工id
     */
    private void addStaffIdInWagesPaymentRedisMation(String staffId){
        List<String> staffIds = getStaffIdsFromRedis();
        staffIds.add(staffId);
        setToRedis(staffIds);
    }

    /**
     * 判断该员工的薪资信息是否在处理中
     *
     * @param staffId 员工id
     * @return true：处理中，false：未在处理
     */
    private boolean isInWagesPaymentRedisMation(String staffId){
        List<String> staffIds = getStaffIdsFromRedis();
        return staffIds.contains(staffId);
    }

    private void deleteWagesPaymentRedisMation(){
        jedisClient.del(IN_WAGES_PAYMENT_STAFF_REDIS_KEY);
    }

    private List<String> getStaffIdsFromRedis(){
        List<String> staffIds = new ArrayList<>();
        if(jedisClient.exists(IN_WAGES_PAYMENT_STAFF_REDIS_KEY)){
            staffIds = JSONUtil.toList(JSONUtil.parseArray(jedisClient.get(IN_WAGES_PAYMENT_STAFF_REDIS_KEY)), null);
        }
        return staffIds;
    }

}
