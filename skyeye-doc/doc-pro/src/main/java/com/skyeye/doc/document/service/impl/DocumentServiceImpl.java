/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.document.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.document.dao.DocumentDao;
import com.skyeye.doc.document.entity.Document;
import com.skyeye.doc.document.enums.DocumentType;
import com.skyeye.doc.document.service.DocumentService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DocumentServiceImpl
 * @Description: 文档服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/24 11:18
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "文档管理", groupName = "文档管理", tenant = TenantEnum.PLATE)
public class DocumentServiceImpl extends SkyeyeBusinessServiceImpl<DocumentDao, Document> implements DocumentService {

    @Override
    protected void validatorEntity(Document entity) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Document::getParentId), entity.getParentId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Document::getName), entity.getName());

        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Document one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("该节点下已存在同名文档，请更换");
        }
    }

    @Override
    public void queryAllDocumentByList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Document::getOrderBy));
        List<Document> documents = list(queryWrapper);
        iAuthUserService.setName(documents, "createId", "createName");
        iAuthUserService.setName(documents, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(documents);
        outputObject.settotal(documents.size());
    }

    @Override
    public Document selectById(String id) {
        Document document = super.selectById(id);
        iAuthUserService.setName(document, "createId", "createName");
        iAuthUserService.setName(document, "lastUpdateId", "lastUpdateName");
        return document;
    }

    @Override
    public void deleteDocumentById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        if (StrUtil.isEmpty(id)) {
            outputObject.setreturnMessage("文档ID不能为空");
            return;
        }

        // 使用递归CTE查询，一次性获取所有子级ID（推荐）
        List<String> allIds = skyeyeBaseMapper.queryAllChildIdsByParentId(Arrays.asList(id));
        if (CollectionUtil.isNotEmpty(allIds)) {
            deleteById(allIds);
        }
    }

    @Override
    public void queryAllEnabledDocumentByList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Document::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.select(CommonConstants.ID, CommonConstants.NAME, MybatisPlusUtil.toColumns(Document::getType),
            MybatisPlusUtil.toColumns(Document::getOrderBy), MybatisPlusUtil.toColumns(Document::getParentId));
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Document::getOrderBy));
        List<Document> documents = list(queryWrapper);
        outputObject.setBeans(documents);
        outputObject.settotal(documents.size());
    }

    @Override
    public void moveDocument(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = MapUtil.getStr(params, "id");
        String parentId = StrUtil.blankToDefault(MapUtil.getStr(params, "parentId"), "0");
        if (StrUtil.isBlank(id)) {
            throw new CustomException("文档ID不能为空");
        }
        Document document = selectById(id);
        if (ObjectUtil.isEmpty(document) || StrUtil.isBlank(document.getId())) {
            throw new CustomException("文档不存在");
        }
        if (StrUtil.equals(id, parentId)) {
            throw new CustomException("不能移动到自身下");
        }
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(Arrays.asList(id));
        if (CollectionUtil.isNotEmpty(childIds) && childIds.contains(parentId)) {
            throw new CustomException("不能移动到自身的子目录下");
        }
        if (!StrUtil.equals("0", parentId)) {
            Document parent = selectById(parentId);
            if (ObjectUtil.isEmpty(parent) || StrUtil.isBlank(parent.getId())) {
                throw new CustomException("目标目录不存在");
            }
            if (!DocumentType.DIRECTORY.getKey().equals(parent.getType())) {
                throw new CustomException("只能移动到目录下");
            }
        }
        String oldParentId = StrUtil.blankToDefault(document.getParentId(), "0");
        if (StrUtil.equals(oldParentId, parentId)) {
            outputObject.setBean(document);
            return;
        }
        QueryWrapper<Document> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq(MybatisPlusUtil.toColumns(Document::getParentId), parentId);
        nameWrapper.eq(MybatisPlusUtil.toColumns(Document::getName), document.getName());
        nameWrapper.ne(CommonConstants.ID, id);
        if (ObjectUtil.isNotEmpty(getOne(nameWrapper))) {
            throw new CustomException("目标目录下已存在同名文档，请先修改名称");
        }
        QueryWrapper<Document> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq(MybatisPlusUtil.toColumns(Document::getParentId), parentId);
        orderWrapper.select(MybatisPlusUtil.toColumns(Document::getOrderBy));
        orderWrapper.orderByDesc(MybatisPlusUtil.toColumns(Document::getOrderBy));
        orderWrapper.last("LIMIT 1");
        Document last = getOne(orderWrapper);
        int nextOrder = last == null || last.getOrderBy() == null ? 1 : last.getOrderBy() + 1;

        UpdateWrapper<Document> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Document::getParentId), parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Document::getOrderBy), nextOrder);
        update(updateWrapper);
        refreshCache(id);

        Document moved = selectById(id);
        outputObject.setBean(moved);
    }
}
