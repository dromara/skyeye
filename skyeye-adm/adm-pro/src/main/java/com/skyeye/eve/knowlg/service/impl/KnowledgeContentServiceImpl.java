/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.knowlg.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.knowlg.classenum.KnowlgContentState;
import com.skyeye.eve.knowlg.dao.KnowledgeContentDao;
import com.skyeye.eve.knowlg.entity.KnowledgeContent;
import com.skyeye.eve.knowlg.service.KnowledgeContentService;
import com.skyeye.exception.CustomException;
import com.skyeye.knowlg.util.Word2Html;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: KnowledgeContentServiceImpl
 * @Description: 知识库管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "知识库", groupName = "知识库管理",allowDynamicAttrKey = false)
public class KnowledgeContentServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeContentDao, KnowledgeContent> implements KnowledgeContentService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryKnowledgeContentList(pageInfo);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        return beans;
    }

    @Override
    public void createPrepose(KnowledgeContent entity) {
        entity.setState(KnowlgContentState.IN_EXAMINE.getKey());
        entity.setReadNum(String.valueOf(CommonNumConstants.NUM_ZERO));
        if (StrUtil.isNotEmpty(entity.getFilePath())) {
            // 文件路径
            String path = tPath + entity.getFilePath();
            File file = new File(path);
            String imagesPath = tPath + FileConstants.FileUploadPath.getVisitPath(CommonNumConstants.NUM_THIRTEEN);
            try {
                Map<String, Object> word = Word2Html.word2007ToHtml(file, imagesPath);
                if ("1".equals(word.get("code").toString())) {
                    String content = word.get("content").toString();
                    entity.setContent(content);
                } else {
                    word = Word2Html.wordToHtml(file, imagesPath);
                    if ("1".equals(word.get("code").toString())) {
                        String content = word.get("content").toString();
                        entity.setContent(content);
                    }
                }
            } catch (Exception ee) {
                throw new CustomException(ee);
            } finally {
                FileUtil.deleteFile(path);
            }
        }
    }

    @Override
    protected void updatePrepose(KnowledgeContent entity) {
        entity.setState(KnowlgContentState.IN_EXAMINE.getKey());
    }

    @Override
    public KnowledgeContent selectById(String id) {
        KnowledgeContent knowledgeContent = super.selectById(id);
        iSysDictDataService.setDataMation(knowledgeContent, KnowledgeContent::getTypeId);
        iAuthUserService.setDataMation(knowledgeContent, KnowledgeContent::getExamineId);
        iAuthUserService.setName(knowledgeContent, "createId", "createName");
        return knowledgeContent;
    }

    /**
     * 获取知识库列表用于审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryAllKnowledgeContentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryKnowledgeContentList(pageInfo);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        iAuthUserService.setMationForMap(beans, "examineId", "examineMation");
        beans.forEach(bean -> {
            bean.put("serviceClassName", getServiceClassName());
        });
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 审核知识库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editKnowledgeContentToCheck(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        KnowledgeContent knowledgeContent = selectById(id);
        if (knowledgeContent.getState().equals(KnowlgContentState.IN_EXAMINE.getKey())) {
            String examineId = inputObject.getLogParams().get("id").toString();
            Integer state = Integer.parseInt(map.get("state").toString());
            String examineNopassReason = map.get("examineNopassReason").toString();
            if (state.equals(KnowlgContentState.NO_PASS.getKey())) {
                // 不通过
                if (StrUtil.isEmpty(examineNopassReason)) {
                    throw new CustomException("请填写不通过原因！");
                }
            }
            UpdateWrapper<KnowledgeContent> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeContent::getExamineId), examineId);
            updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeContent::getExamineTime), DateUtil.getTimeAndToString());
            updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeContent::getState), state);
            updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeContent::getExamineNopassReason), examineNopassReason);
            update(updateWrapper);
            refreshCache(id);
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面。");
        }
    }

    @Override
    @IgnoreTenant
    public void queryAllPassKnowledgeContentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        pageInfo.setState(String.valueOf(KnowlgContentState.PASS.getKey()));
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryKnowledgeContentList(pageInfo);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        iAuthUserService.setMationForMap(beans, "createId", "createMation");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryEightPassKnowlgList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<KnowledgeContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeContent::getState), KnowlgContentState.PASS.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(KnowledgeContent::getCreateTime));
        queryWrapper.last(String.format(Locale.ROOT, "limit %s", CommonNumConstants.NUM_EIGHT));
        queryWrapper.select(CommonConstants.ID,
            MybatisPlusUtil.toColumns(KnowledgeContent::getName),
            MybatisPlusUtil.toColumns(KnowledgeContent::getTypeId),
            MybatisPlusUtil.toColumns(KnowledgeContent::getCreateTime));
        List<KnowledgeContent> list = list(queryWrapper);
        iSysDictDataService.setDataMation(list, KnowledgeContent::getTypeId);
        // 获取我发表的审核通过的知识库数量
        String userId = inputObject.getLogParams().get("id").toString();
        Integer count = getKnowlgPassNumByUserId(userId);
        outputObject.setBeans(list);
        outputObject.settotal(count);
    }

    private Integer getKnowlgPassNumByUserId(String userId) {
        QueryWrapper<KnowledgeContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeContent::getState), KnowlgContentState.PASS.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeContent::getCreateId), userId);
        return Math.toIntExact(count(queryWrapper));
    }

}
