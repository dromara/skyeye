/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.PropertiesUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IBarCodeService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.dao.MaterialNormsCodeDao;
import com.skyeye.material.entity.*;
import com.skyeye.material.service.MaterialNormsCodeHisService;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.classenum.PickNormsCodeUseState;
import com.skyeye.rest.shop.service.IShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialNormsCodeServiceImpl
 * @Description: 商品条形码服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 8:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品条形码", groupName = "商品管理")
public class MaterialNormsCodeServiceImpl extends SkyeyeBusinessServiceImpl<MaterialNormsCodeDao, MaterialNormsCode> implements MaterialNormsCodeService {

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private IBarCodeService iBarCodeService;

    @Autowired
    private MaterialNormsCodeHisService materialNormsCodeHisService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<MaterialNormsCode> wrapper) {
        setCustomerWrapper(inputObject, wrapper);
    }

    private static void setCustomerWrapper(InputObject inputObject, QueryWrapper<MaterialNormsCode> wrapper) {
        MaterialNormsCodeQueryDo commonPageInfo = inputObject.getParams(MaterialNormsCodeQueryDo.class);
        if (StrUtil.isNotEmpty(commonPageInfo.getMaterialId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getMaterialId), commonPageInfo.getMaterialId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getNormsId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getNormsId), commonPageInfo.getNormsId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getDepotId())) {
            wrapper.and(wr ->
                wr.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getDepotId), commonPageInfo.getDepotId())
                    .or().eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getCreateDepotId), commonPageInfo.getDepotId()));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromObjectId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getFromObjectId), commonPageInfo.getFromObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getToObjectId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getToObjectId), commonPageInfo.getToObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getDepartmentId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getDepartmentId), commonPageInfo.getDepartmentId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFarmId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getFarmId), commonPageInfo.getFarmId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getStoreId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getStoreId), commonPageInfo.getStoreId());
        }
        if (commonPageInfo.getInDepot() != null) {
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getInDepot), commonPageInfo.getInDepot());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            wrapper.like(MybatisPlusUtil.toColumns(MaterialNormsCode::getCodeNum), commonPageInfo.getKeyword());
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(MaterialNormsCode::getCreateTime));
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(MaterialNormsCode::getCodeNum));
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> dataList = super.queryPageDataList(inputObject);
        materialService.setMationForMap(dataList, "materialId", "materialMation");
        materialNormsService.setMationForMap(dataList, "normsId", "normsMation");
        erpDepotService.setMationForMap(dataList, "depotId", "depotMation");
        iBarCodeService.setBarCodeMationForMap(dataList, "id", getServiceClassName());
        return dataList;
    }

    @Override
    public void deletePreExecution(String id) {
        MaterialNormsCode materialNormsCode = selectById(id);
        if (materialNormsCode.getInDepot() != MaterialNormsCodeInDepot.NOT_IN_STOCK.getKey()) {
            throw new CustomException("已入库/已出库编码无法删除");
        }
    }

    @Override
    public void deletePostpose(String id) {
        iBarCodeService.deleteByObjectId(id);
    }

    @Override
    public void updatePostpose(List<MaterialNormsCode> entity, String userId) {
        if (CollectionUtil.isEmpty(entity)) {
            return;
        }
        // 计算出入库历史
        String currentTime = DateUtil.getTimeAndToString();
        List<MaterialNormsCodeHis> hisList = new ArrayList<>();
        entity.forEach(materialNormsCode -> {
            MaterialNormsCodeHis materialNormsCodeHis = new MaterialNormsCodeHis();
            materialNormsCodeHis.setNormsCodeId(materialNormsCode.getId());
            materialNormsCodeHis.setType(materialNormsCode.getType());
            materialNormsCodeHis.setOperatorTime(currentTime);
            if (materialNormsCode.getInDepot() == MaterialNormsCodeInDepot.WAREHOUSING.getKey()) {
                // 入库
                materialNormsCodeHis.setOperatorId(materialNormsCode.getFromObjectId());
                materialNormsCodeHis.setOperatorKey(materialNormsCode.getFromObjectKey());
            } else if (materialNormsCode.getInDepot() == MaterialNormsCodeInDepot.OUTBOUND.getKey()) {
                // 出库
                materialNormsCodeHis.setOperatorId(materialNormsCode.getToObjectId());
                materialNormsCodeHis.setOperatorKey(materialNormsCode.getToObjectKey());
            }
            hisList.add(materialNormsCodeHis);
        });
        materialNormsCodeHisService.createEntity(hisList, StrUtil.EMPTY);
    }

    @Override
    public void insertMaterialNormsCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> list = JSONUtil.toList(params.get("list").toString(), null);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        String createDepotId = params.get("createDepotId").toString();
        List<String> normsIdList = list.stream().map(bean -> bean.get("normsId").toString()).distinct().collect(Collectors.toList());
        if (list.size() != normsIdList.size()) {
            throw new CustomException("存在重复的商品规格信息，请确认");
        }
        List<String> materialIdList = list.stream().map(bean -> bean.get("materialId").toString()).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> materialNormsMap = materialNormsService.selectMapByIds(normsIdList);
        for (Map<String, Object> bean : list) {
            String materialId = bean.get("materialId").toString();
            String normsId = bean.get("normsId").toString();
            Material material = materialMap.get(materialId);
            MaterialNorms materialNorms = materialNormsMap.get(normsId);
            if (ObjectUtil.isEmpty(material) || ObjectUtil.isEmpty(materialNorms)) {
                throw new CustomException("商品/规格信息不存在，请确认");
            }
        }

        // 创建任务
        Map<String, Object> jobBody = new HashMap<>();
        // 是否创建任务
        jobBody.put("whetherCreatTask", false);
        jobBody.put("list", JSONUtil.toJsonStr(list));
        jobBody.put("createDepotId", createDepotId);
        jobBody.put("className", MaterialNormsCodeServiceImpl.class.getName());
        jobBody.put("tenantId", tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY);
        String topic = PropertiesUtil.getPropertiesValue("${topic.material-norms-code-generate-barcode}");
        jobBody.put("topic", topic);
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(jobBody));
        jobMateMation.setUserId(inputObject.getLogParams().get("id").toString());
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    @Override
    public List<MaterialNormsCode> queryMaterialNormsCodeByCodeNum(String depotId, List<String> codeNumList, Integer... indepot) {
        if (CollectionUtil.isEmpty(codeNumList)) {
            return new ArrayList<>();
        }
        List<String> codeNumTmpList = codeNumList.stream().distinct().collect(Collectors.toList());
        QueryWrapper<MaterialNormsCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MaterialNormsCode::getCodeNum), codeNumTmpList);
        // 库存状态
        List<Integer> indepotList = Arrays.asList(indepot);
        if (CollectionUtil.isNotEmpty(indepotList)) {
            queryWrapper.in(MybatisPlusUtil.toColumns(MaterialNormsCode::getInDepot), indepotList);
        }
        if (StrUtil.isNotEmpty(depotId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getDepotId), depotId);
        }

        List<MaterialNormsCode> normsCodeList = list(queryWrapper);
        return normsCodeList;
    }

    @Override
    public List<MaterialNormsCode> queryMaterialNormsCode(String depotId, String normsId, Integer type, Integer indepot) {
        QueryWrapper<MaterialNormsCode> queryWrapper = new QueryWrapper<>();
        // 库存状态
        if (indepot != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getInDepot), indepot);
        }
        if (type != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getType), type);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getDepotId), depotId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getNormsId), normsId);

        List<MaterialNormsCode> normsCodeList = list(queryWrapper);
        return normsCodeList;
    }

    @Override
    public void queryNormsBarCodeList(InputObject inputObject, OutputObject outputObject) {
        MaterialNormsCodeQueryDo commonPageInfo = inputObject.getParams(MaterialNormsCodeQueryDo.class);
        QueryWrapper<MaterialNormsCode> wrapper = new QueryWrapper<>();
        setCustomerWrapper(inputObject, wrapper);

        if (commonPageInfo.getNumber() != null) {
            wrapper.last(String.format(Locale.ROOT, "limit %s", commonPageInfo.getNumber()));
        } else {
            wrapper.last(String.format(Locale.ROOT, "limit %s", 20));
        }
        wrapper.select(MybatisPlusUtil.toColumns(MaterialNormsCode::getCodeNum));
        List<MaterialNormsCode> materialNormsCodeList = list(wrapper);
        List<String> codeNumList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
        outputObject.setBeans(codeNumList);
        outputObject.settotal(codeNumList.size());
    }

    @Override
    public void updateEntityPick(List<MaterialNormsCode> materialNormsCodeList) {
        if (CollectionUtil.isEmpty(materialNormsCodeList)) {
            return;
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        updateEntity(materialNormsCodeList, userId);
    }

    @Override
    public void queryNormsStockDetailList(InputObject inputObject, OutputObject outputObject) {
        MaterialNormsCodeQueryDo commonPageInfo = inputObject.getParams(MaterialNormsCodeQueryDo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<MaterialNormsCode> wrapper = new QueryWrapper<>();
        setCustomerWrapper(inputObject, wrapper);
        if (StrUtil.equals(commonPageInfo.getType(), "noUse")) {
            // 未使用
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getPickUseState), PickNormsCodeUseState.WAIT_USE.getKey());
        } else if (StrUtil.equals(commonPageInfo.getType(), "used")) {
            // 已使用
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getPickUseState), PickNormsCodeUseState.USED.getKey());
        }

        List<MaterialNormsCode> materialNormsCodeList = list(wrapper);
        List<Map<String, Object>> dataList = JSONUtil.toList(JSONUtil.toJsonStr(materialNormsCodeList), null);
        materialService.setMationForMap(dataList, "materialId", "materialMation");
        materialNormsService.setMationForMap(dataList, "normsId", "normsMation");
        erpDepotService.setMationForMap(dataList, "depotId", "depotMation");
        iBarCodeService.setBarCodeMationForMap(dataList, "id", getServiceClassName());
        iDepmentService.setMationForMap(dataList, "departmentId", "departmentMation");
        farmService.setMationForMap(dataList, "farmId", "farmMation");
        outputObject.setBeans(dataList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryStoreNormsStockDetailList(InputObject inputObject, OutputObject outputObject) {
        MaterialNormsCodeQueryDo commonPageInfo = inputObject.getParams(MaterialNormsCodeQueryDo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<MaterialNormsCode> wrapper = new QueryWrapper<>();
        setCustomerWrapper(inputObject, wrapper);
        if (StrUtil.equals(commonPageInfo.getType(), "noUse")) {
            // 未使用
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getPickUseState), PickNormsCodeUseState.WAIT_USE.getKey());
        } else if (StrUtil.equals(commonPageInfo.getType(), "used")) {
            // 已使用
            wrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getPickUseState), PickNormsCodeUseState.USED.getKey());
        }
        List<MaterialNormsCode> materialNormsCodeList = list(wrapper);
        List<Map<String, Object>> dataList = JSONUtil.toList(JSONUtil.toJsonStr(materialNormsCodeList), null);
        materialService.setMationForMap(dataList, "materialId", "materialMation");
        materialNormsService.setMationForMap(dataList, "normsId", "normsMation");
        erpDepotService.setMationForMap(dataList, "depotId", "depotMation");
        iBarCodeService.setBarCodeMationForMap(dataList, "id", getServiceClassName());
        iShopStoreService.setMationForMap(dataList, "storeId", "storeMation");
        outputObject.setBeans(dataList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryMaterialNormsCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        List<String> normsCodeList = Arrays.asList(params.get("normsCodeList").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(normsCodeList)) {
            return;
        }
        String storeUseState = params.get("storeUseState").toString();

        QueryWrapper<MaterialNormsCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getStoreId), storeId);
        queryWrapper.in(MybatisPlusUtil.toColumns(MaterialNormsCode::getCodeNum), normsCodeList);
        if (StrUtil.isNotEmpty(storeUseState)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsCode::getStoreUseState), storeUseState);
        }
        List<MaterialNormsCode> materialNormsCodeList = list(queryWrapper);
        outputObject.setBeans(materialNormsCodeList);
        outputObject.settotal(materialNormsCodeList.size());
    }

    @Override
    public void editStoreMaterialNormsCodeUseState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<String> ids = Arrays.asList(params.get("ids").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        String storeUseState = params.get("storeUseState").toString();
        UpdateWrapper<MaterialNormsCode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, ids);
        updateWrapper.set(MybatisPlusUtil.toColumns(MaterialNormsCode::getStoreUseState), storeUseState);
        update(updateWrapper);
    }

}
