/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sys.quartz;

import com.skyeye.common.constans.ForumConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ForumContentDao;
import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import com.skyeye.eve.service.SysQuartzRunHistoryService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @ClassName: HotForumQuartz
 * @Description: 每天凌晨两点去计算每日热门贴
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 23:09
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class HotForumQuartz {

    private static final String QUARTZ_ID = QuartzConstants.SysQuartzMateMationJobType.HOT_FORUM_QUARTZ.getQuartzId();
	
    @Autowired
    public JedisClientService jedisClient;
    
    @Autowired
    public ForumContentDao forumContentDao;

    @Autowired
    private SysQuartzRunHistoryService sysQuartzRunHistoryService;
	
    /**
     * 定时器计算每日热门贴
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 2 * * ?") //每天凌晨两点执行一次
    public void editHotForumMation() throws Exception {
        String historyId = sysQuartzRunHistoryService.startSysQuartzRun(QUARTZ_ID);
        try{
            String yestoday = DateUtil.getSpecifiedDayMation(DateUtil.getTimeAndToString(), "yyyy-MM-dd", 0, 1, 7);  //昨天的时间
            String everydayBrowseKey = ForumConstants.forumEverydayBrowseIdsByTime(yestoday);
            if(!ToolUtil.isBlank(jedisClient.get(everydayBrowseKey))){//获取昨天被浏览过的帖子
                String str = jedisClient.get(everydayBrowseKey);
                String[] forumarr = str.split(",");
                List<Map<String, Object>> beans = new LinkedList<>();
                List<Map<String, Object>> list = new LinkedList<>();
                for(int i = 0, len = forumarr.length; i < len; i++){
                    //计算一天的浏览量
                    String browseNumsKey = ForumConstants.forumBrowseNumsByForumId(forumarr[i]);
                    String ybrowseNumsKey = ForumConstants.forumYesterdayBrowseNumsByForumId(forumarr[i]);
                    String bnownum = "0";
                    if(!ToolUtil.isBlank(jedisClient.get(browseNumsKey))){
                        bnownum = jedisClient.get(browseNumsKey);//帖子当前的浏览量
                    }
                    String byestodaynum = "0";
                    if(!ToolUtil.isBlank(jedisClient.get(ybrowseNumsKey))){
                        byestodaynum = jedisClient.get(ybrowseNumsKey);//帖子昨天的浏览量
                    }
                    String bnum = String.valueOf(Integer.parseInt(bnownum) - Integer.parseInt(byestodaynum));//帖子一天的浏览量

                    //计算一天的评论量
                    String commentNumsKey = ForumConstants.forumCommentNumsByForumId(forumarr[i]);
                    String ycommentNumsKey = ForumConstants.forumYesterdayCommentNumsByForumId(forumarr[i]);
                    String cnownum = "0";
                    if(!ToolUtil.isBlank(jedisClient.get(commentNumsKey))){
                        cnownum = jedisClient.get(commentNumsKey);//帖子当前的评论量
                    }
                    String cyestodaynum = "0";
                    if(!ToolUtil.isBlank(jedisClient.get(ycommentNumsKey))){
                        cyestodaynum = jedisClient.get(ycommentNumsKey);//帖子昨天的评论量
                    }
                    String cnum = String.valueOf(Integer.parseInt(cnownum) - Integer.parseInt(cyestodaynum));//帖子一天的评论量

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ToolUtil.getSurFaceId());
                    map.put("forumId", forumarr[i]);
                    map.put("bnum", bnum);
                    map.put("cnum", cnum);
                    map.put("time", yestoday);
                    list.add(map);
                    jedisClient.set(ybrowseNumsKey, bnownum);//更新浏览量
                    jedisClient.set(ycommentNumsKey, cnownum);//更新评论量

                    String everyforumEverydayNums = ForumConstants.everyforumEverydayNumsByIdAndTime(forumarr[i], yestoday);
                    jedisClient.set(everyforumEverydayNums, String.valueOf(Integer.parseInt(bnum) + Integer.parseInt(cnum)));//将每个帖子每天的浏览量+评论量存入redis中

                    if(list.size() >= 20){//每20条数据保存一次
                        forumContentDao.insertForumStatisticsDayByList(list);//将每天被浏览过的帖子存入统计表中
                        if(!beans.isEmpty()){
                            list.addAll(beans);
                            beans.clear();
                        }
                        list = sortListByNums(list, yestoday);//根据近七天的浏览量和评论量的算术平均值对list进行排序
                        beans.addAll(list.subList(0, 6));//取前六条放入beans中
                        list.clear();
                    }
                }

                if(!list.isEmpty()){
                    forumContentDao.insertForumStatisticsDayByList(list);//将每天被浏览过的帖子存入统计表中
                    if(!beans.isEmpty()){
                        list.addAll(beans);
                        beans.clear();
                    }
                    list = sortListByNums(list, yestoday);//根据近七天的浏览量和评论量的算术平均值对list进行排序
                    int count = list.size();
                    int pageMaxSize = 6;
                    if(count < pageMaxSize){
                        pageMaxSize = count;
                    }
                    beans.addAll(list.subList(0, pageMaxSize));//取前六条放入beans中
                }

                if(!beans.isEmpty()){
                    forumContentDao.insertForumHotByList(beans);
                }
                jedisClient.del(everydayBrowseKey);//清空每天被浏览过的帖子
            }else{
                jedisClient.del(everydayBrowseKey);//清空每天被浏览过的帖子
            }
        } catch (Exception e){
            sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_ERROR.getState());
        }
        sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_SUCCESS.getState());
    }
	
	/**
     * 获取过去7天内的日期数组
     * @return  日期数组
	 * @throws ParseException 
     */
    public static ArrayList<String> pastDay(String time) throws ParseException{
        ArrayList<String> pastDaysList = new ArrayList<>();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(time);
        for (int i = 6; i >= 0; i--) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - i);
            Date today = calendar.getTime();
            String result = sdf.format(today);
            pastDaysList.add(result);
        }
        return pastDaysList;
    }
    
    /**
     * 根据近七天的浏览量和评论量的算术平均值对list进行排序
     * @return  日期数组
	 * @throws ParseException 
     */
    public List<Map<String, Object>> sortListByNums(List<Map<String, Object>> list, String time) throws ParseException{
    	DecimalFormat df = new DecimalFormat("0.00");//格式化小数  
    	for(Map<String, Object> m : list){
			ArrayList<String> datelist = pastDay(time);//获取近七天的日期数组集合
			int nums = 0;
			for(String date : datelist){
				String everyforumEverydayNum = ForumConstants.everyforumEverydayNumsByIdAndTime(m.get("forumId").toString(), date);
				if(!ToolUtil.isBlank(jedisClient.get(everyforumEverydayNum))){//获取每个帖子每天的浏览量+评论量
					nums += Integer.parseInt(jedisClient.get(everyforumEverydayNum));
				}
			}
			m.put("nums",  String.valueOf(df.format((float)nums / 7)));//将帖子近七天的评论量、浏览量的算术平均值保留两位小数放入map中
		}
		//按帖子近七天的评论量、浏览量的算术平均值给集合排序
		list.sort(new Comparator<Map<String, Object>>() {//Comparator 比较器. 需要实现比较方法
			@Override
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				Integer m1num = (int)Float.parseFloat(m1.get("nums").toString()) * 100;
				Integer m2num = (int)Float.parseFloat(m2.get("nums").toString()) * 100;
				int flag = m1num.compareTo(m2num);
			    return -flag; // 取反，按倒序排列
			}
        });
        return list;
    }
	
}
