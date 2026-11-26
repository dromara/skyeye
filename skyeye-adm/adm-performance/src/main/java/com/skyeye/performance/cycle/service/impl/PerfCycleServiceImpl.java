package com.skyeye.performance.cycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.performance.assessment.service.PerfAssessmentService;
import com.skyeye.performance.classenum.PerfCycleState;
import com.skyeye.performance.cycle.dao.PerfCycleDao;
import com.skyeye.performance.cycle.entity.PerfCycle;
import com.skyeye.performance.cycle.service.PerfCycleService;
import com.skyeye.performance.scheme.entity.PerfScheme;
import com.skyeye.performance.scheme.service.PerfSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "绩效考核周期", groupName = "员工绩效")
public class PerfCycleServiceImpl extends SkyeyeBusinessServiceImpl<PerfCycleDao, PerfCycle>
    implements PerfCycleService {

    @Autowired
    private PerfSchemeService perfSchemeService;

    @Autowired
    @Lazy
    private PerfAssessmentService perfAssessmentService;

    @Override
    public void validatorEntity(PerfCycle entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            PerfCycle old = selectById(entity.getId());
            if (old != null && PerfCycleState.CLOSED.getKey().equals(old.getCycleState())) {
                throw new CustomException("周期已关闭，不允许修改");
            }
        }
        if (entity.getStartTime().compareTo(entity.getEndTime()) > 0) {
            throw new CustomException("开始日期不能晚于结束日期");
        }
        QueryWrapper<PerfCycle> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfCycle::getName), entity.getName());
        wrapper.eq(MybatisPlusUtil.toColumns(PerfCycle::getSchemeId), entity.getSchemeId());
        if (StrUtil.isNotEmpty(entity.getId())) {
            wrapper.ne(CommonConstants.ID, entity.getId());
        }
        if (getOne(wrapper, false) != null) {
            throw new CustomException("同一方案下周期名称已存在");
        }
    }

    @Override
    public void deletePreExecution(String id) {
        if (perfAssessmentService.countByCycleId(id) > 0) {
            throw new CustomException("该周期已生成考核单，无法删除");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        List<String> schemeIds = beans.stream()
            .filter(bean -> bean.get("schemeId") != null)
            .map(bean -> bean.get("schemeId").toString())
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(schemeIds)) {
            Map<String, PerfScheme> schemeMap = perfSchemeService.selectByIds(schemeIds.toArray(new String[0]))
                .stream().collect(Collectors.toMap(PerfScheme::getId, item -> item, (a, b) -> a));
            beans.forEach(bean -> {
                String schemeId = String.valueOf(bean.getOrDefault("schemeId", StrUtil.EMPTY));
                bean.put("schemeMation", schemeMap.get(schemeId));
            });
        }
        return beans;
    }

    @Override
    public PerfCycle selectById(String id) {
        PerfCycle cycle = super.selectById(id);
        cycle.setCycleStateMation(PerfCycleState.getMation(cycle.getCycleState()));
        perfSchemeService.setDataMation(cycle, PerfCycle::getSchemeId);
        return cycle;
    }

    @Override
    public void generateAssessments(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String cycleId = params.get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        PerfCycle cycle = selectById(cycleId);
        if (PerfCycleState.CLOSED.getKey().equals(cycle.getCycleState())) {
            throw new CustomException("周期已关闭，无法生成考核单");
        }
        int count = perfAssessmentService.generateByCycle(cycle, userId);
        if (!PerfCycleState.PROCESSING.getKey().equals(cycle.getCycleState())) {
            cycle.setCycleState(PerfCycleState.PROCESSING.getKey());
            updateById(cycle);
            refreshCache(cycleId);
        }
        outputObject.setBean(count);
    }

    @Override
    public void closeCycle(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PerfCycle cycle = selectById(id);
        cycle.setCycleState(PerfCycleState.CLOSED.getKey());
        updateById(cycle);
        refreshCache(id);
    }
}
