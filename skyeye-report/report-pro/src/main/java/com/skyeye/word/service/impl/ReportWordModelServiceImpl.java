/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.word.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.property.service.ReportPropertyService;
import com.skyeye.word.dao.ReportWordModelDao;
import com.skyeye.word.entity.WordModel;
import com.skyeye.word.entity.WordModelAttr;
import com.skyeye.word.service.ReportWordModelAttrService;
import com.skyeye.word.service.ReportWordModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportWordModelServiceImpl
 * @Description: 文字模型管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/5 16:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "文字模型管理", groupName = "文字模型管理")
public class ReportWordModelServiceImpl extends SkyeyeBusinessServiceImpl<ReportWordModelDao, WordModel> implements ReportWordModelService {

    @Autowired
    private ReportWordModelAttrService reportWordModelAttrService;

    @Autowired
    private ReportPropertyService reportPropertyService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        return beans;
    }

    @Override
    public void writePostpose(WordModel entity, String userId) {
        super.writePostpose(entity, userId);
        reportWordModelAttrService.save(entity.getId(), entity.getWordModelAttrList());
    }

    @Override
    public void deletePostpose(WordModel entity) {
        String basePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY);
        FileUtil.deleteFile(basePath + entity.getImgPath());
        // 删除属性
        reportWordModelAttrService.deleteByWordId(entity.getId());
    }

    @Override
    public WordModel getDataFromDb(String id) {
        WordModel wordModel = super.getDataFromDb(id);
        // 设置模型属性
        wordModel.setWordModelAttrList(reportWordModelAttrService.queryByWordId(id));
        return wordModel;
    }

    @Override
    public WordModel selectById(String id) {
        WordModel wordModel = super.selectById(id);
        reportPropertyService.setDataMation(wordModel.getWordModelAttrList(), WordModelAttr::getPropertyId);
        wordModel.setCustomAttr(getEditFontTextAttr(id));
        return wordModel;
    }

    private Map<String, Object> getEditFontTextAttr(String modelId) {
        Map<String, Object> item = new HashMap<>();
        item.put("editorType", 2);
        item.put("attrCode", "custom.textContent");
        item.put("modelId", modelId);
        item.put("edit", 1);
        item.put("typeName", "Style属性");
        item.put("name", "文字");
        item.put("defaultValue", "Hello, Skyeye.");
        // 是否显示在编辑框
        item.put("remark", "显示内容");
        return item;
    }

    @Override
    public List<WordModel> getDataFromDb(List<String> idList) {
        List<WordModel> wordModelList = super.getDataFromDb(idList);
        // 设置模型属性
        Map<String, List<WordModelAttr>> wordModelAttrMap = reportWordModelAttrService.queryByWordId(idList);
        wordModelList.forEach(wordModel -> {
            wordModel.setWordModelAttrList(wordModelAttrMap.get(wordModel.getId()));
        });
        return wordModelList;
    }

    @Override
    public List<WordModel> selectByIds(String... ids) {
        List<WordModel> wordModelList = super.selectByIds(ids);
        wordModelList.forEach(wordModel -> {
            reportPropertyService.setDataMation(wordModel.getWordModelAttrList(), WordModelAttr::getPropertyId);
            wordModel.setCustomAttr(getEditFontTextAttr(wordModel.getId()));
        });
        return wordModelList;
    }

    @Override
    public void getEnabledWordModelList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<WordModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WordModel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.select(CommonConstants.ID);
        List<WordModel> wordModelList = list(queryWrapper);

        List<String> ids = wordModelList.stream().map(WordModel::getId).collect(Collectors.toList());
        List<WordModel> wordModels = selectByIds(ids.toArray(new String[]{}));
        iSysDictDataService.setName(wordModels, "typeId", "typeName");
        outputObject.setBeans(wordModels);
        outputObject.settotal(wordModels.size());
    }

}
