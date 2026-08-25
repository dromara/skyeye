/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.exception.CustomException;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.role.dao.RoleDao;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "AI角色", groupName = "AI角色")
public class RoleServiceImpl extends SkyeyeBusinessServiceImpl<RoleDao, Role> implements RoleService {

    @Autowired
    @Lazy
    private KnowledgeService knowledgeService;

    @Override
    public void validatorEntity(Role entity) {
        super.validatorEntity(entity);
        if (StrUtil.isNotBlank(entity.getKnowledgeId())) {
            Knowledge knowledge = knowledgeService.selectById(entity.getKnowledgeId());
            if (knowledge == null || StrUtil.isBlank(knowledge.getId())) {
                throw new CustomException("绑定的知识库不存在");
            }
        }
    }

    @Override
    @IgnoreTenant
    public Role selectById(String id) {
        Role role = super.selectById(id);
        knowledgeService.setDataMation(role, Role::getKnowledgeId);
        return role;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        knowledgeService.setMationForMap(beans, "knowledgeId", "knowledgeMation");
        return beans;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
        knowledgeService.setMationForMap(beans, "knowledgeId", "knowledgeMation");
        return beans;
    }

}
