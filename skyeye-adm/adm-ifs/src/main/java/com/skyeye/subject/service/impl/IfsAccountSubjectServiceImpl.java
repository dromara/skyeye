/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.subject.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.subject.dao.IfsAccountSubjectDao;
import com.skyeye.subject.entity.AccountSubject;
import com.skyeye.subject.service.IfsAccountSubjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: IfsAccountSubjectServiceImpl
 * @Description: 会计科目管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/27 12:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "会计科目管理", groupName = "会计科目管理")
public class IfsAccountSubjectServiceImpl extends SkyeyeBusinessServiceImpl<IfsAccountSubjectDao, AccountSubject> implements IfsAccountSubjectService {

    @Override
    public void validatorEntity(AccountSubject entity) {
        super.validatorEntity(entity);
        // 校验基础信息
        QueryWrapper<AccountSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AccountSubject::getNum), entity.getNum());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AccountSubject checkMation = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkMation)) {
            throw new CustomException("this 【num】 is exist.");
        }
    }

    @Override
    public void queryEnabledSubjectList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AccountSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AccountSubject::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<AccountSubject> accountSubjectList = list(queryWrapper);
        accountSubjectList.forEach(accountSubject -> {
            accountSubject.setName(String.format(Locale.ROOT, "%s_%s", accountSubject.getNum(), accountSubject.getName()));
        });
        outputObject.setBeans(accountSubjectList);
        outputObject.settotal(accountSubjectList.size());
    }

    @Override
    public void initDefaultSubjects(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> created = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        Map<String, AccountSubject> numMap = loadExistingByNum();
        Set<String> parentNums = new HashSet<>();
        for (DefaultAccountSubjectCatalog.SubjectDef def : DefaultAccountSubjectCatalog.all()) {
            if (StrUtil.isNotBlank(def.getParentNum())) {
                parentNums.add(def.getParentNum());
            }
        }
        for (DefaultAccountSubjectCatalog.SubjectDef def : DefaultAccountSubjectCatalog.all()) {
            if (numMap.containsKey(def.getNum())) {
                skipped.add(def.getNum());
                continue;
            }
            try {
                AccountSubject parent = StrUtil.isBlank(def.getParentNum()) ? null : numMap.get(def.getParentNum());
                AccountSubject entity = new AccountSubject();
                entity.setName(def.getName());
                entity.setNum(def.getNum());
                entity.setType(def.getType());
                entity.setEnabled(EnableEnum.ENABLE_USING.getKey());
                entity.setAmountDirection(def.getAmountDirection());
                entity.setParentId(parent == null ? StrUtil.EMPTY : parent.getId());
                entity.setLevel(parent == null ? 1 : (parent.getLevel() == null ? 1 : parent.getLevel()) + 1);
                entity.setIsLeaf(parentNums.contains(def.getNum()) ? WhetherEnum.DISABLE_USING.getKey() : WhetherEnum.ENABLE_USING.getKey());
                entity.setAuxCustomer(flag(def.isAuxCustomer()));
                entity.setAuxSupplier(flag(def.isAuxSupplier()));
                entity.setAuxMaterial(flag(def.isAuxMaterial()));
                entity.setAuxDepartment(flag(def.isAuxDepartment()));
                entity.setAuxProject(flag(def.isAuxProject()));
                entity.setAuxDepot(WhetherEnum.DISABLE_USING.getKey());
                entity.setCashFlag(flag(def.isCash()));
                createEntity(entity, userId);
                numMap.put(def.getNum(), selectById(entity.getId()));
                created.add(def.getNum());
            } catch (Exception ex) {
                failed.add(def.getNum() + "(" + StrUtil.blankToDefault(ex.getMessage(), "失败") + ")");
            }
        }
        markParentsNotLeaf(parentNums, numMap);
        Map<String, Object> bean = new HashMap<>();
        bean.put("created", created);
        bean.put("skipped", skipped);
        bean.put("failed", failed);
        bean.put("createdCount", created.size());
        bean.put("skippedCount", skipped.size());
        bean.put("failedCount", failed.size());
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    private Map<String, AccountSubject> loadExistingByNum() {
        Map<String, AccountSubject> numMap = new HashMap<>();
        for (AccountSubject subject : list()) {
            if (StrUtil.isNotBlank(subject.getNum())) {
                numMap.put(subject.getNum(), subject);
            }
        }
        return numMap;
    }

    private void markParentsNotLeaf(Set<String> parentNums, Map<String, AccountSubject> numMap) {
        for (String parentNum : parentNums) {
            AccountSubject parent = numMap.get(parentNum);
            if (parent == null || WhetherEnum.DISABLE_USING.getKey().equals(parent.getIsLeaf())) {
                continue;
            }
            UpdateWrapper<AccountSubject> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, parent.getId());
            uw.set(MybatisPlusUtil.toColumns(AccountSubject::getIsLeaf), WhetherEnum.DISABLE_USING.getKey());
            update(uw);
            refreshCache(parent.getId());
        }
    }

    private Integer flag(boolean on) {
        return on ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey();
    }

}

