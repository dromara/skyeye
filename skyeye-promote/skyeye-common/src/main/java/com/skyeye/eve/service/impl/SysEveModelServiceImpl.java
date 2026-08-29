/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.SysEveModelDao;
import com.skyeye.eve.entity.model.SysEveModel;
import com.skyeye.eve.enums.SysEveModelAttrType;
import com.skyeye.eve.service.SysEveModelService;
import com.skyeye.eve.service.SysEveModelTypeService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveModelServiceImpl
 * @Description: 素材管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/14 9:10
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "素材管理", groupName = "素材管理", tenant = TenantEnum.PLATE)
public class SysEveModelServiceImpl extends SkyeyeBusinessServiceImpl<SysEveModelDao, SysEveModel> implements SysEveModelService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private SysEveModelTypeService sysEveModelTypeService;

    @Override
    public QueryWrapper<SysEveModel> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveModel> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String type = commonPageInfo.getType();
        if (StrUtil.isEmpty(type)) {
            throw new CustomException("类型不能为空");
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFirstTypeId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getFirstTypeId), commonPageInfo.getFirstTypeId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getSecondTypeId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getSecondTypeId), commonPageInfo.getSecondTypeId());
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getType), type);
        if (Integer.parseInt(type) == SysEveModelAttrType.PERSONAL.getKey()) {
            // 个人模板
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getCreateId), userId);
        }

        return queryWrapper;
    }

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        sysEveModelTypeService.setNameMationForMap(beans, "firstTypeId", "firstTypeName", "typeName");
        sysEveModelTypeService.setNameMationForMap(beans, "secondTypeId", "secondTypeName", "typeName");
        beans.forEach(bean -> {
            bean.put("type", SysEveModelAttrType.getName(Integer.parseInt(bean.get("type").toString())));
        });
        return beans;
    }

    @Override
    public void validatorEntity(SysEveModel entity) {
        super.validatorEntity(entity);
        QueryWrapper<SysEveModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getTitle), entity.getTitle());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getType), entity.getType());
        if (entity.getType() == SysEveModelAttrType.PERSONAL.getKey()) {
            // 个人模板
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModel::getCreateId), userId);
        }
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        SysEveModel checkModelMation = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkModelMation)) {
            throw new CustomException("该标题名称已存在，请更换");
        }
    }

    @Override
    public void deletePostpose(SysEveModel entity) {
        FileUtil.deleteFile(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + entity.getLogo());
    }

    @Override
    public SysEveModel selectById(String id) {
        SysEveModel sysEveModel = super.selectById(id);
        sysEveModel.setTypeName(SysEveModelAttrType.getName(sysEveModel.getType()));
        sysEveModelTypeService.setNameDataMation(sysEveModel, SysEveModel::getFirstTypeId, "typeName");
        sysEveModelTypeService.setNameDataMation(sysEveModel, SysEveModel::getSecondTypeId, "typeName");
        return sysEveModel;
    }

}

