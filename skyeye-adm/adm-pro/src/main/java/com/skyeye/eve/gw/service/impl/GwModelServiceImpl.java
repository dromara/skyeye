/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.gw.dao.GwModelDao;
import com.skyeye.eve.gw.entity.GwModel;
import com.skyeye.eve.gw.service.GwModelService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: GwModelServiceImpl
 * @Description: 公文模版服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/29 9:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "公文模版", groupName = "公文模版")
public class GwModelServiceImpl extends SkyeyeBusinessServiceImpl<GwModelDao, GwModel> implements GwModelService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public void validatorEntity(GwModel entity) {
        super.validatorEntity(entity);
        if (!entity.getPath().endsWith(".docx")) {
            throw new CustomException("目前仅支持docx类型的word文档");
        }
    }

    @Override
    public void updatePrepose(GwModel entity) {
        GwModel oldModel = selectById(entity.getId());
        if (!StrUtil.equals(entity.getPath(), oldModel.getPath())) {
            // 如果文件有修改，则需要删除之前的文件
            FileUtil.deleteFile(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + oldModel.getPath());
        }
    }

    @Override
    public void queryEnabledGwModelList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<GwModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GwModel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<GwModel> gwModels = list(queryWrapper);
        outputObject.setBeans(gwModels);
        outputObject.settotal(gwModels.size());
    }
}
