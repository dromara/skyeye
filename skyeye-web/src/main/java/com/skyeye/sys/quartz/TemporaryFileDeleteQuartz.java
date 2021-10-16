/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sys.quartz;

import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import com.skyeye.eve.service.SysQuartzRunHistoryService;
import com.skyeye.jedis.JedisClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 *
 * @ClassName: TemporaryFileDeleteQuartz
 * @Description: 定时删除临时的云压缩文件
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/14 11:11
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class TemporaryFileDeleteQuartz {

	private static Logger log = LoggerFactory.getLogger(TemporaryFileDeleteQuartz.class);

	private static final String QUARTZ_ID = QuartzConstants.SysQuartzMateMationJobType.TEMPORARY_FILE_DELETE_QUARTZ.getQuartzId();

	@Autowired
	public JedisClientService jedisClientService;

	@Value("${IMAGES_PATH}")
	private String tPath;

	@Autowired
	private SysQuartzRunHistoryService sysQuartzRunHistoryService;
	
	private long DAY_MINUTE_TIME = 24 * 60;
	
	/**
	 * 定时删除临时的云压缩文件,每天23点执行
	 *
	 * @throws Exception
	 */
	@Scheduled(cron="0 0 23 * * ?")
    public void deleteTemporaryFile() throws Exception {
		String historyId = sysQuartzRunHistoryService.startSysQuartzRun(QUARTZ_ID);
    	log.info("TemporaryFileDeleteQuartz start");
    	try{
			// 临时文件存储路径
			String basePath = tPath + "\\upload\\fileconsole\\temporaryfile\\";
			File pack = new File(basePath);
			if(!pack.isDirectory()){
				// 创建目录
				pack.mkdirs();
			}
			// 读取指定路径下的文件名和目录名
			getAllFileByRecursion(pack.listFiles());
		} catch (Exception e){
			sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_ERROR.getState());
    		log.warn("TemporaryFileDeleteQuartz error.", e);
		}
		log.info("TemporaryFileDeleteQuartz end");
		sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_SUCCESS.getState());
    }
    
    public void getAllFileByRecursion(File[] fileList) throws Exception{
    	for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				// 如果是文件,获取文件最后的修改时间
				String upTime = DateUtil.getDateStr(fileList[i].lastModified());
				// 获取当前时间和文件最后修改时间的时间差（多少分钟）
				long time = DateUtil.getDistanceMinute(upTime, DateUtil.getTimeAndToString());
				if(time > DAY_MINUTE_TIME){
					fileList[i].delete();
				}
			}else{
				//如果是文件夹
				getAllFileByRecursion(fileList[i].listFiles());
			}
		}
    }

}
