/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.quartz;

import com.skyeye.annotation.api.ApiEntityColumn;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SysQuartzRunHistory
 * @Description: 系统定时任务启动历史对象
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/29 11:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class SysQuartzRunHistory implements Serializable {

    @ApiEntityColumn("主键id")
    private String id;

    @ApiEntityColumn("定时任务id")
    private String quartzId;

    @ApiEntityColumn("执行状态  1.执行中  2.执行成功  3.执行失败")
    private Integer state;

    @ApiEntityColumn("启动时间")
    private String startTime;

    @ApiEntityColumn("结束时间")
    private String endTime;

    @ApiEntityColumn("启动类型  1.自动启动  2.人工启动")
    private Integer startType;

    @ApiEntityColumn("如果start_type=2，则需要记录启动人")
    private String startUserId;

    public static enum State{
        START_RUNING(1, "执行中"),
        START_SUCCESS(2, "执行成功"),
        START_ERROR(3, "执行失败");
        private int state;
        private String name;
        State(int state, String name){
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

    public static enum StartType{
        AUTO_START(1, "自动启动"),
        MANUAL_START(2, "人工启动");
        private int type;
        private String name;
        StartType(int type, String name){
            this.type = type;
            this.name = name;
        }
        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }

}
