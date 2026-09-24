/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.abnormalmarking.service.ServiceAbnormalMarkingService;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.dao.AfterSealDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.afterseal.service.SealFaultUseMaterialService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.dispatch.entity.SealDispatchConfig;
import com.skyeye.dispatch.service.SealDispatchConfigService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.ordertype.service.SealOrderTypeService;
import com.skyeye.worker.service.SealWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SealSeServiceServiceImpl
 * @Description: 工单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:23
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工单管理", groupName = "工单管理")
public class AfterSealServiceImpl extends SkyeyeBusinessServiceImpl<AfterSealDao, AfterSeal> implements AfterSealService {

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private ICustomerService iCustomerService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private SealWorkerService sealWorkerService;

    @Autowired
    private SealFaultUseMaterialService sealFaultUseMaterialService;

    @Autowired
    private SealOrderTypeService sealOrderTypeService;

    @Autowired
    private com.skyeye.afterseal.service.SealSignService sealSignService;

    @Autowired
    private SealDispatchConfigService sealDispatchConfigService;

    @Autowired
    private ServiceAbnormalMarkingService serviceAbnormalMarkingService;

    @Override
    public QueryWrapper<AfterSeal> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AfterSeal> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String state = commonPageInfo.getState();

        if (StrUtil.isNotEmpty(state)) {
            // 我创建的
            if (StrUtil.equals(state, "myCreate")) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getCreateId), userId);
            } else if (StrUtil.equals(state, AfterSealState.PENDING_ORDERS.getKey())
                || StrUtil.equals(state, AfterSealState.BE_SIGNED.getKey())
                || StrUtil.equals(state, AfterSealState.BE_COMPLETED.getKey())) {
                // 待接单，待签到，待完工 - 需要查询该用户的工单
                queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(AfterSeal::getState), state);
            } else if (StrUtil.equals(state, AfterSealState.BE_DISPATCHED.getKey())
                || StrUtil.equals(state, AfterSealState.BE_EVALUATED.getKey())
                || StrUtil.equals(state, AfterSealState.AUDIT.getKey())
                || StrUtil.equals(state, AfterSealState.COMPLATE.getKey())) {
                // 待派工，待评价，待审核，已完工 - 查询所有该状态的工单
                queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), state);
            }
        }

        String projectId = commonPageInfo.getCustomParamsMapStr("projectId");
        if (StrUtil.isNotEmpty(projectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getProjectId), projectId);
        }

        if (StrUtil.isNotEmpty(commonPageInfo.getTypeId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getTypeId), commonPageInfo.getTypeId());
        }

        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);

        iCustomerService.setMationForMap(beans, "holderId", "holderMation");
        iAuthUserService.setMationForMap(beans, "declarationId", "declarationMation");
        iAuthUserService.setMationForMap(beans, "serviceUserId", "serviceUserMation");
        return beans;
    }

    @Override
    public AfterSeal selectById(String id) {
        AfterSeal afterSeal = super.selectById(id);

        iAuthUserService.setDataMation(afterSeal, AfterSeal::getDeclarationId);
        afterSeal.setServiceUserMation(sealWorkerService.selectByUserId(afterSeal.getServiceUserId()));
        if (CollectionUtil.isNotEmpty(afterSeal.getCooperationUserId())) {
            afterSeal.setCooperationUserMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(afterSeal.getCooperationUserId())));
        }

        iMaterialService.setDataMation(afterSeal, AfterSeal::getProductId);
        iCustomerService.setDataMation(afterSeal, AfterSeal::getHolderId);

        sealOrderTypeService.setDataMation(afterSeal, AfterSeal::getOrderTypeId);

        // 异常标记
        serviceAbnormalMarkingService.setDataMation(afterSeal, AfterSeal::getServiceAbnormalMarkingId);
        return afterSeal;
    }

    @Override
    public void createPrepose(AfterSeal entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(AfterSealState.BE_DISPATCHED.getKey());
        } else {
            // 默认有接单人
            entity.setState(AfterSealState.PENDING_ORDERS.getKey());
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
        entity.setDeclarationId(InputObject.getLogParamsStatic().get("id").toString());
    }

    @Override
    protected void validatorEntity(AfterSeal entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            AfterSeal afterSeal = selectById(entity.getId());
            if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_DISPATCHED.getKey())
                || StrUtil.equals(afterSeal.getState(), AfterSealState.PENDING_ORDERS.getKey())) {
                // 待派工，待接单可以进行编辑
            } else {
                throw new CustomException("该数据状态已改变，请刷新页面！");
            }
        }
    }

    @Override
    protected void updatePrepose(AfterSeal entity) {
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(AfterSealState.BE_DISPATCHED.getKey());
        } else {
            // 默认有接单人
            entity.setState(AfterSealState.PENDING_ORDERS.getKey());
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
    }

    @Override
    protected void writePostpose(AfterSeal entity, String userId) {
        super.writePostpose(entity, userId);

        sendDispatchWork(entity.getId(), userId);
    }

    private void sendDispatchWork(String id, String userId) {
        // 发送消息
        Map<String, Object> notice = new HashMap<>();
        notice.put("serviceId", id);
        notice.put("type", MqConstants.JobMateMationJobType.WATI_WORKER_SEND.getJobType());
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(notice));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String serviceUserId = map.get("serviceUserId").toString();
        AfterSeal afterSeal = selectById(id);
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_DISPATCHED.getKey())) {
            // 封顶接单量校验
            SealDispatchConfig config = sealDispatchConfigService.getConfigForTenant();
            if (config != null && config.getCapOrderQuantity() != null && config.getCapOrderQuantity() > 0) {
                long currentCount = countWorkerCurrentOrderCount(serviceUserId);
                if (currentCount >= config.getCapOrderQuantity()) {
                    outputObject.setreturnMessage("该服务人员待接单和进行中的工单数已达封顶接单量（" + config.getCapOrderQuantity() + "），无法再指派新工单");
                    return;
                }
            }
            // 待派工可以进行派工
            UpdateWrapper<AfterSeal> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, afterSeal.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.PENDING_ORDERS.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), serviceUserId);
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getCooperationUserId), map.get("cooperationUserId").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getServiceTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            // 派工成功mq消息任务
            sendDispatchWork(id, afterSeal.getCreateId());
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void receivingSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        AfterSeal afterSeal = selectById(map.get("id").toString());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.PENDING_ORDERS.getKey())) {
            // 待接单可以进行接单
            updateStateById(afterSeal.getId(), AfterSealState.BE_SIGNED.getKey());
            refreshCache(afterSeal.getId());
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void deletePreExecution(AfterSeal afterSeal) {
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_DISPATCHED.getKey())
            || StrUtil.equals(afterSeal.getState(), AfterSealState.PENDING_ORDERS.getKey())) {
            // 待派工/待接单可以进行删除
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.BE_COMPLETED.getKey());
        List<AfterSeal> afterSealList = list(queryWrapper);
        outputObject.setBeans(afterSealList);
        outputObject.settotal(afterSealList.size());
    }

    @Override
    public void queryMyParticipatedPendingCompletedOrders(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();

        // 查询所有待完工状态的工单
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.BE_COMPLETED.getKey());

        // 条件1：我作为负责人
        // 条件2：我作为协助人（使用 JSON_CONTAINS 查询 JSON 数组）
        // 合并查询：我作为负责人 或 我作为协助人
        queryWrapper.and(wrapper -> {
            wrapper.or(w -> w.eq(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), currentUserId))
                .or(w -> w.apply("JSON_CONTAINS(cooperation_user_id, {0})", "\"" + currentUserId + "\""));
        });

        List<AfterSeal> afterSealList = list(queryWrapper);

        outputObject.setBeans(afterSealList);
        outputObject.settotal(afterSealList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void auditSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        AfterSeal afterSeal = selectById(map.get("id").toString());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_COMPLETED.getKey())) {
            // 只有待完工状态下可以完工，修改为待评价状态
            updateStateById(afterSeal.getId(), AfterSealState.BE_EVALUATED.getKey());
            refreshCache(afterSeal.getId());
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void updateStateById(String id, String state) {
        UpdateWrapper<AfterSeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public long countWorkerCurrentOrderCount(String serviceUserId) {
        if (StrUtil.isEmpty(serviceUserId)) return 0;
        QueryWrapper<AfterSeal> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), serviceUserId);
        qw.in(MybatisPlusUtil.toColumns(AfterSeal::getState),
            AfterSealState.PENDING_ORDERS.getKey(), AfterSealState.BE_SIGNED.getKey(), AfterSealState.BE_COMPLETED.getKey());
        return count(qw);
    }

    @Override
    public Map<String, Long> batchCountWorkerCurrentOrderCount(List<String> serviceUserIds) {
        if (CollectionUtil.isEmpty(serviceUserIds)) return new HashMap<>();
        QueryWrapper<AfterSeal> qw = new QueryWrapper<>();
        qw.in(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), serviceUserIds);
        qw.in(MybatisPlusUtil.toColumns(AfterSeal::getState),
            AfterSealState.PENDING_ORDERS.getKey(), AfterSealState.BE_SIGNED.getKey(), AfterSealState.BE_COMPLETED.getKey());
        List<AfterSeal> list = list(qw);
        return list.stream().collect(Collectors.groupingBy(AfterSeal::getServiceUserId, Collectors.counting()));
    }

    @Override
    public void finishSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        AfterSeal afterSeal = selectById(map.get("id").toString());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.AUDIT.getKey())) {
            // 待审核状态可以进行审核完工
            updateStateById(afterSeal.getId(), AfterSealState.COMPLATE.getKey());
            refreshCache(afterSeal.getId());
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void queryOverviewSealSeServiceOrder(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        // 查询售后服务总览信息
        Map<String, Object> resultMap = new HashMap<>();
        // 总工单数
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            queryWrapper.apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " <= {0}", tableSelectInfo.getEndTime())
                .apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " >= {0}", tableSelectInfo.getStartTime());
        }
        Long totalOrders = count(queryWrapper);
        resultMap.put("totalOrders", totalOrders);
        // 完成工单数
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        Long completedOrders = count(queryWrapper);
        resultMap.put("completedOrders", completedOrders);
        // 配件使用数
        resultMap.put("useCount", sealFaultUseMaterialService.queryUseCount(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime()));
        // 平均处理时长（从签到报工获取）
        if (completedOrders > CommonNumConstants.NUM_ZERO) {
            String totalWorkHours = sealSignService.getAllFinishedWorkHours(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
            resultMap.put("avgProcessTime", CalculationUtil.divide(totalWorkHours,
                String.valueOf(completedOrders), CommonNumConstants.NUM_TWO));
        } else {
            resultMap.put("avgProcessTime", CommonNumConstants.NUM_ZERO);
        }
        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Map<String, Long> getAllFinishedServiceNum(String startTime, String endTime) {
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " <= {0}", endTime)
                .apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " >= {0}", startTime);
        }
        List<AfterSeal> afterSealList = list(queryWrapper);
        return afterSealList.stream().collect(Collectors.groupingBy(AfterSeal::getServiceUserId, Collectors.counting()));
    }

    @Override
    public void querySealSeServiceOrderTypeStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        // 根据typeId进行分组统计
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            queryWrapper.apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " <= {0}", tableSelectInfo.getEndTime())
                .apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " >= {0}", tableSelectInfo.getStartTime());
        }
        List<AfterSeal> resultList = list(queryWrapper);
        iSysDictDataService.setDataMation(resultList, AfterSeal::getTypeId);
        // 根据typeId去重
        List<AfterSeal> distinctList = resultList.stream().
            collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AfterSeal::getTypeId))), ArrayList::new));
        // 获取typeId对应的name
        Map<String, String> stringMap = distinctList.stream().collect(Collectors.toMap(AfterSeal::getTypeId, bean -> {
            if (CollectionUtil.isNotEmpty(bean.getTypeMation())) {
                return bean.getTypeMation().get("dictName").toString();
            } else {
                return StrUtil.EMPTY;
            }
        }));

        Map<String, Long> collect = resultList.stream().collect(Collectors.groupingBy(AfterSeal::getTypeId, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : collect.entrySet()) {
            // 如果没有对应的name，则跳过
            if (StrUtil.isBlank(stringMap.get(entry.getKey()))) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("name", stringMap.get(entry.getKey()));
            map.put("value", entry.getValue());
            result.add(map);
        }
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void querySealSeServiceOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime, endTime;
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            startTime = tableSelectInfo.getStartTime();
            endTime = tableSelectInfo.getEndTime();
        } else {
            startTime = DateUtil.formatDate2Str(DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD);
            endTime = DateUtil.getYmdTimeAndToString();
        }
        List<String> dayList = DateUtil.getDays(startTime, endTime);
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime));
        queryWrapper.apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " <= {0}", endTime)
            .apply(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + " >= {0}", startTime);
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime));
        // 1. 新增的工单
        List<AfterSeal> afterSealList = list(queryWrapper);
        // 根据createTime进行分组统计
        Map<String, Long> collect = afterSealList.stream().collect(Collectors.groupingBy(bean -> {
            Date pointTime = DateUtil.getPointTime(bean.getCreateTime(), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
        }, Collectors.counting()));
        // 2. 完工的工单
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        List<AfterSeal> afterSealList2 = list(queryWrapper);
        // 根据createTime进行分组统计
        Map<String, Long> collect2 = afterSealList2.stream().collect(Collectors.groupingBy(bean -> {
            Date pointTime = DateUtil.getPointTime(bean.getCreateTime(), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
        }, Collectors.counting()));
        // 构建结果集
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> allNewOrders = new ArrayList<>();
        List<Long> completedOrders = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        for (String day : dayList) {
            allNewOrders.add(collect.getOrDefault(day, defaultValue) - collect2.getOrDefault(day, defaultValue));
            completedOrders.add(collect2.getOrDefault(day, defaultValue));
        }
        resultMap.put("allNewOrders", allNewOrders);
        resultMap.put("completedOrders", completedOrders);
        resultMap.put("dayList", dayList);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void querySealServiceOrderWorkerStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);

        // 先查询工单数量、工时、配件数的数据
        // 工单数量：从已审核通过的签到报工记录中统计（确保数据一致性）
        Map<String, Long> allServiceOrderNum = sealSignService.getOrderCountByUserId(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        Map<String, String> finishedServiceTime = sealSignService.getAllFinishedWorkHoursByUserId(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        Map<String, Long> useCount = sealFaultUseMaterialService.queryUseCountByUserId(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        // 合并所有数据中的用户ID（即使工人走了，只要有数据就能显示）
        Set<String> allUserIds = new HashSet<>();
        allUserIds.addAll(allServiceOrderNum.keySet());
        allUserIds.addAll(finishedServiceTime.keySet());
        allUserIds.addAll(useCount.keySet());

        if (CollectionUtil.isEmpty(allUserIds)) {
            return;
        }

        // 根据合并后的用户ID查询用户信息
        Map<String, Map<String, Object>> userInfoMap = iAuthUserService.queryUserNameList(new ArrayList<>(allUserIds));

        // 根据用户信息构建结果列表
        List<Map<String, Object>> beans = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);

        for (String userId : allUserIds) {
            Map<String, Object> bean = new HashMap<>();
            bean.put("userId", userId);

            // 设置用户信息
            Map<String, Object> userInfo = userInfoMap.getOrDefault(userId, new HashMap<>());
            bean.put("userMation", userInfo);

            // 完成工单数量
            Long orderNum = allServiceOrderNum.getOrDefault(userId, defaultValue);
            bean.put("orderNum", orderNum);

            // 平均工时
            if (orderNum == 0) {
                bean.put("avgProcessTime", CommonNumConstants.NUM_ZERO);
            } else {
                String userWorkHours = finishedServiceTime.getOrDefault(userId, "0");
                bean.put("avgProcessTime", CalculationUtil.divide(userWorkHours,
                    orderNum.toString(), CommonNumConstants.NUM_TWO));
            }

            // 配件使用数
            bean.put("totalParts", useCount.getOrDefault(userId, defaultValue));

            beans.add(bean);
        }

        // 根据平均工时倒序排序
        beans.sort((o1, o2) -> Double.compare(Double.parseDouble(o2.get("avgProcessTime").toString()), Double.parseDouble(o1.get("avgProcessTime").toString())));
        outputObject.setBeans(beans);
    }

}
