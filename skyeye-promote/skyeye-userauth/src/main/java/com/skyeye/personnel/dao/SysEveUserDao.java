/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.entity.userauth.user.UserTreeQueryDo;
import com.skyeye.personnel.entity.SysEveUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserDao
 * @Description: 用户管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 15:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserDao extends SkyeyeBaseMapper<SysEveUser> {

    List<Map<String, Object>> querySysUserList(CommonPageInfo pageInfo);

    Map<String, Object> queryMationByUserCode(@Param("userCode") String userCode);

    int editRoleIdsByUserId(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryDeskTopsMenuByUserId(@Param("userId") String userId);

    Map<String, Object> queryUserDetailsMationByUserId(@Param("userId") String userId);

    int editUserDetailsMationByUserId(Map<String, Object> map);

    Map<String, Object> queryUserSchoolMationByUserId(@Param("userId") String userId);

    List<Map<String, Object>> queryUserStaffToTree(UserTreeQueryDo queryDo);

    List<Map<String, Object>> queryUserStaffDepToTree(UserTreeQueryDo queryDo);

    List<Map<String, Object>> queryTalkGroupUserListByUserId(UserTreeQueryDo queryDo);

    Map<String, Object> queryWxUserMationByOpenId(@Param("openId") String openId);

    Map<String, Object> queryWxUserMationByAppIdAndOpenId(@Param("appId") String appId, @Param("openId") String openId);

    Map<String, Object> queryUserMationByOpenId(@Param("openId") String openId);

    Map<String, Object> queryUserMationByAppIdAndOpenId(@Param("appId") String appId, @Param("openId") String openId);

    Map<String, Object> queryUserBindMationByUserId(@Param("userId") String userId);

    Map<String, Object> queryUserBindMationByUserIdAndAppId(@Param("userId") String userId, @Param("appId") String appId);

    List<Map<String, Object>> queryUserBindListByUserId(@Param("userId") String userId);

    int updateBindUserMation(Map<String, Object> map);

    int insertWxUserMation(Map<String, Object> map);

    int unbindUserMationByUserIdAndAppId(@Param("userId") String userId, @Param("appId") String appId);

}
