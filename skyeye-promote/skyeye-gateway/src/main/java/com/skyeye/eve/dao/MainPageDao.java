/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

public interface MainPageDao {

    @IgnoreTenant
    String queryCheckOnWorkNumByUserId(@Param("userId") String userId,
                                       @Param("tenantId") String tenantId);

    @IgnoreTenant
    String queryDiskCloudFileNumByUserId(@Param("userId") String userId,
                                         @Param("tenantId") String tenantId);

    @IgnoreTenant
    String queryForumNumByUserId(@Param("userId") String userId,
                                 @Param("tenantId") String tenantId);

    @IgnoreTenant
    String queryKnowledgeNumByUserId(@Param("userId") String userId,
                                     @Param("tenantId") String tenantId);

}
