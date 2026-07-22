/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.entity.Member;

/**
 * @ClassName: MemberService
 * @Description: 会员管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/2 15:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MemberService extends SkyeyeBusinessService<Member> {

    void queryMyWriteMemberList(InputObject inputObject, OutputObject outputObject);

    Member queryMemberByPhone(String phone);

    void editMemberPassword(String userId, String newPassword, int pwdNum);

    void updateCurrentLoginMemberNickname(InputObject inputObject, OutputObject outputObject);

    void updateCurrentLoginMemberAvatar(InputObject inputObject, OutputObject outputObject);

    void queryCurrentLoginMember(InputObject inputObject, OutputObject outputObject);
}
