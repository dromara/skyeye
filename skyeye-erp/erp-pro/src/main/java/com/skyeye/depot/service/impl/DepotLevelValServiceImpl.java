/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.classenum.GenerateDepotLevelValType;
import com.skyeye.depot.dao.DepotLevelValDao;
import com.skyeye.depot.entity.DepotLevel;
import com.skyeye.depot.entity.DepotLevelVal;
import com.skyeye.depot.service.DepotLevelService;
import com.skyeye.depot.service.DepotLevelValService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotLevelValServiceImpl
 * @Description: 仓库级别的值服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 10:12
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库级别的值管理", groupName = "仓库级别管理")
public class DepotLevelValServiceImpl extends SkyeyeBusinessServiceImpl<DepotLevelValDao, DepotLevelVal> implements DepotLevelValService {

    @Autowired
    private DepotLevelService depotLevelService;

    @Override
    public QueryWrapper<DepotLevelVal> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DepotLevelVal> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 仓库
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelVal::getDepotId), commonPageInfo.getObjectId());
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            // 级别
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelVal::getDepotLevelId), commonPageInfo.getHolderId());
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelVal::getParentId), CommonNumConstants.NUM_ZERO);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return beans;
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 查询子节点信息(包含当前节点)
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(ids, tenantId);
        beans = selectMapByIds(childIds).values().stream().map(bean -> BeanUtil.beanToMap(bean)).collect(Collectors.toList());
        beans.forEach(bean -> {
            bean.put("lay_is_open", true);
        });

        depotLevelService.setMationForMap(beans, "depotLevelId", "depotLevelMation");
        setMationForMap(beans, "parentId", "parentMation");
        return beans;
    }

    @Override
    public void validatorEntity(DepotLevelVal entity) {
        super.validatorEntity(entity);
        DepotLevel depotLevel;
        if (!StrUtil.equals(entity.getParentId(), "0")) {
            // 查询父级别的值
            DepotLevelVal parentLevelVal = selectById(entity.getParentId());
            // 查询该值对应的级别的子级别
            depotLevel = depotLevelService.queryChildDepotLevelById(entity.getDepotId(), parentLevelVal.getDepotLevelId());
        } else {
            depotLevel = depotLevelService.queryChildDepotLevelById(entity.getDepotId(), "0");
        }
        if (ObjectUtil.isNull(depotLevel)) {
            throw new CustomException("该值对应的级别下没有子级别！");
        }
        entity.setDepotLevelId(depotLevel.getId());
    }

    @Override
    public void deletePostpose(DepotLevelVal entity) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 查询子节点信息(包含当前节点)
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(Arrays.asList(entity.getId()), tenantId);
        deleteById(childIds);
    }

    @Override
    public List<DepotLevelVal> queryDepotLevelValListByParentId(String parentId) {
        QueryWrapper<DepotLevelVal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelVal::getParentId), parentId);
        return list(queryWrapper);
    }

    @Override
    public void queryAllDepotLevelValByDepotId(InputObject inputObject, OutputObject outputObject) {
        String depotId = inputObject.getParams().get("depotId").toString();
        if (StrUtil.isBlank(depotId)) {
            throw new CustomException("仓库id不能为空");
        }
        QueryWrapper<DepotLevelVal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelVal::getDepotId), depotId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DepotLevelVal::getCreateTime));
        List<DepotLevelVal> list = list(queryWrapper);
        List<Map<String, Object>> beans = list.stream().map(BeanUtil::beanToMap).collect(Collectors.toList());
        depotLevelService.setMationForMap(beans, "depotLevelId", "depotLevelMation");
        setMationForMap(beans, "parentId", "parentMation");
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void deleteDepotLevelValListByDepotLevelId(String... depotLevelId) {
        List<String> depotLevelIdList = Arrays.asList(depotLevelId);
        if (CollectionUtil.isEmpty(depotLevelIdList)) {
            return;
        }
        QueryWrapper<DepotLevelVal> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DepotLevelVal::getDepotLevelId), depotLevelIdList);
        remove(queryWrapper);
    }

    @Override
    public void batchGenerateDepotLevelVal(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Integer type = Integer.parseInt(params.get("type").toString());
        Integer number = Integer.parseInt(params.get("number").toString());
        String parentId = params.get("parentId").toString();
        String depotId = params.get("depotId").toString();
        if (number <= 0) {
            throw new CustomException("生成数量必须大于0！");
        }

        DepotLevel depotLevel;
        if (!StrUtil.equals(parentId, "0")) {
            // 查询父级别的值
            DepotLevelVal parentLevelVal = selectById(parentId);
            // 查询该值对应的级别的子级别
            depotLevel = depotLevelService.queryChildDepotLevelById(depotId, parentLevelVal.getDepotLevelId());
        } else {
            depotLevel = depotLevelService.queryChildDepotLevelById(depotId, "0");
        }
        if (ObjectUtil.isNull(depotLevel)) {
            throw new CustomException("该值对应的级别下没有子级别！");
        }
        // 查询当前级别的值下的所有子级别的值
        List<DepotLevelVal> depotLevelValList = queryDepotLevelValListByParentId(parentId);
        List<String> numberList = depotLevelValList.stream().map(DepotLevelVal::getNumber).collect(Collectors.toList());

        List<DepotLevelVal> saveDataList = new ArrayList<>();
        if (type == GenerateDepotLevelValType.NUMBER.getKey()) {
            // 生成数字
            int start = 1;
            while (saveDataList.size() < number) {
                if (!numberList.contains(String.valueOf(start))) {
                    DepotLevelVal depotLevelVal = new DepotLevelVal();
                    depotLevelVal.setParentId(parentId);
                    depotLevelVal.setDepotLevelId(depotLevel.getId());
                    depotLevelVal.setDepotId(depotId);
                    depotLevelVal.setNumber(String.valueOf(start));
                    saveDataList.add(depotLevelVal);
                }
                start++;
            }
        } else if (type == GenerateDepotLevelValType.LETTER.getKey()) {
            // 生成字母
            int start = 1;
            while (saveDataList.size() < number) {
                String code = convertToTitle(start);
                if (!numberList.contains(code)) {
                    DepotLevelVal depotLevelVal = new DepotLevelVal();
                    depotLevelVal.setParentId(parentId);
                    depotLevelVal.setDepotLevelId(depotLevel.getId());
                    depotLevelVal.setDepotId(depotId);
                    depotLevelVal.setNumber(code);
                    saveDataList.add(depotLevelVal);
                }
                start++;
            }
        }
        if (CollectionUtil.isNotEmpty(saveDataList)) {
            String userId = inputObject.getLogParams().get("id").toString();
            createEntity(saveDataList, userId);
        }
    }

    /**
     * 给一个整数 columnNumber ，返回它在 Excel 表中相对应的列名称。
     * 例如，columnNumber 为 1 ，返回 "A" ，columnNumber 为 27 ，返回 "AA" 。
     * 这个方法主要用于生成 Excel 表头。
     *
     * @param columnNumber
     * @return
     */
    public String convertToTitle(int columnNumber) {
        StringBuffer sb = new StringBuffer();
        while (columnNumber != 0) {
            columnNumber--;
            sb.append((char) (columnNumber % 26 + 'A'));
            columnNumber /= 26;
        }
        return sb.reverse().toString();
    }


}
