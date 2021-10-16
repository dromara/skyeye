/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.constants;

/**
 * @ClassName: ScheduleDayConstants
 * @Description: 日程模块相关实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 11:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ScheduleDayConstants {

    /**
     * 日程关联其他模块id的类型
     */
    public interface ScheduleDayObjectType{
        /**
         * 任务计划
         */
        int OBJECT_TYPE_IS_PLAN = 1;

        /**
         * 项目任务
         */
        int OBJECT_TYPE_IS_PRO_TASK = 2;
    };

}
