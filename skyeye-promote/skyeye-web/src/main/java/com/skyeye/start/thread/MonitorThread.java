package com.skyeye.start.thread;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysMonitorDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.jedis.impl.JedisClientServiceImpl;
import com.sun.management.OperatingSystemMXBean;

/**
 * 
     * @ClassName: MonitorThread
     * @Description: 通过线程去获取系统的实时信息
     * @author 卫志强
     * @date 2018年6月8日
     *
 */
@SuppressWarnings("restriction")
@Component
public class MonitorThread{
	
	private int kb = 1024;
	
	@Autowired
	private SysMonitorDao sysMonitorDao;
	
	@Autowired
	public JedisClientService jedisClientService;
	
	private static Logger log = LoggerFactory.getLogger(MonitorThread.class);
	
    private int count = 1;

    /**
     * 定时器执行方法
     * @throws Exception 
     */
    @Scheduled(cron="0 */1 * * * ?") //每一分钟执行一次
    public void queryComMation() throws Exception {
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
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());
    	int totalThread = parentThread.activeCount();
    	//cpu使用率
        double cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
        	cpuRatio = ToolUtil.getCpuRatioForWindows();
        }
        Map<String, Object> bean = new HashMap<>();
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
        bean.put("createTime", ToolUtil.getTimeAndToString());
        sysMonitorDao = SpringUtils.getBean("sysMonitorDao");
        jedisClientService = SpringUtils.getBean(JedisClientServiceImpl.class);
        sysMonitorDao.insertMonitorMation(bean);
        jedisClientService.set("server_mation", JSON.toJSONString(bean));
        jedisClientService.expire("server_mation:", 1800);//时间为30分钟
		count++;
		if(count >= 500){
			count = 1;
			sysMonitorDao.deleteMonitorSaveFiveHandlber(bean);
		}
    }

}
