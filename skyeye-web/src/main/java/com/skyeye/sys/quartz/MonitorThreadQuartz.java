/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.sys.quartz;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysMonitorDao;
import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import com.skyeye.eve.service.SysQuartzRunHistoryService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.jedis.impl.JedisClientServiceImpl;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: MonitorThreadQuartz
 * @Description: 通过线程去获取系统的实时信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 23:09
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@SuppressWarnings("restriction")
@Component
public class MonitorThreadQuartz {

    private static Logger log = LoggerFactory.getLogger(MonitorThreadQuartz.class);

    private int kb = 1024;

    private static final String QUARTZ_ID = QuartzConstants.SysQuartzMateMationJobType.MONITOR_THREAD_QUARTZ.getQuartzId();

    @Autowired
	private SysMonitorDao sysMonitorDao;

    @Autowired
	public JedisClientService jedisClientService;

    @Autowired
    private SysQuartzRunHistoryService sysQuartzRunHistoryService;

    private int count = 1;

    /**
     * 定时器执行方法
     * @throws Exception 
     */
    @Scheduled(cron="0 */1 * * * ?") //每一分钟执行一次
    public void queryComMation() throws Exception {
        String historyId = sysQuartzRunHistoryService.startSysQuartzRun(QUARTZ_ID);
        try{
            log.info("读取");
            // 可使用内存
            long totalMemory = Runtime.getRuntime().totalMemory() / kb / kb;
            // 剩余内存
            long freeMemory = Runtime.getRuntime().freeMemory() / kb / kb;
            // 最大可使用内存
            long maxMemory = Runtime.getRuntime().maxMemory() / kb / kb;
            OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            // 操作系统
            String osName = System.getProperty("os.name");
            // 总的物理内存
            long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb / kb;
            // 剩余的物理内存
            long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb / kb;
            // 已使用的物理内存
            long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb / kb;
            // 获得线程总数
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            ThreadGroup topGroup = group;
            // 遍历线程组树，获取根线程组
            while (group != null) {
                topGroup = group;
                group = group.getParent();
            }
            int totalThread = topGroup.activeCount();
            //cpu使用率
            double cpuRatio = 0;
            if (osName.toLowerCase().startsWith("windows")) {
                cpuRatio = ToolUtil.getCpuRatioForWindows();
            }
            //ip
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            Map<String, Object> bean = new HashMap<>();
            bean.put("ip", ip);//ip
            bean.put("totalMemory", totalMemory);//可使用内存
            bean.put("freeMemory", freeMemory);//剩余内存
            bean.put("maxMemory", maxMemory);//最大可使用内存
            bean.put("osName", osName);//操作系统
            bean.put("totalMemorySize", totalMemorySize);//总的物理内存
            bean.put("freePhysicalMemorySize", freePhysicalMemorySize);//剩余的物理内存
            bean.put("usedMemory", usedMemory);//已使用的物理内存
            bean.put("totalThread", totalThread);//线程总数
            bean.put("cpuRatio", cpuRatio);//cpu使用率
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("createTime", DateUtil.getTimeAndToString());
            sysMonitorDao = SpringUtils.getBean("sysMonitorDao");
            jedisClientService = SpringUtils.getBean(JedisClientServiceImpl.class);
            sysMonitorDao.insertMonitorMation(bean);
            //格式化时间为时分
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            bean.put("createTime", ToolUtil.formatDateByPattern(simpleDateFormat.parse(bean.get("createTime").toString()), "HH:mm"));
            jedisClientService.set("server_mation", JSONUtil.toJsonStr(bean));
            jedisClientService.expire("server_mation:", 1800);//时间为30分钟
            count++;
            if(count >= 500){
                count = 1;
                sysMonitorDao.deleteMonitorSaveFiveHandlber(bean);
            }
        } catch (Exception e){
            sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_ERROR.getState());
        }
        sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_SUCCESS.getState());
    }

}
