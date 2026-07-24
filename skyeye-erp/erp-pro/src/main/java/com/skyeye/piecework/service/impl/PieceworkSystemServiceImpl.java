package com.skyeye.piecework.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.UserStaffWorkstationType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.entity.FarmStaff;
import com.skyeye.farm.entity.FarmStation;
import com.skyeye.farm.service.FarmService;
import com.skyeye.farm.service.FarmStaffService;
import com.skyeye.farm.service.FarmStationService;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptProductNum;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptProductNumService;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptService;
import com.skyeye.piecework.dao.PieceworkSystemDao;
import com.skyeye.piecework.entity.PieceworkSystem;
import com.skyeye.piecework.service.PieceworkSystemService;
import com.skyeye.rest.checkwork.checkwork.ICheckWorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "计件数量或工时统计信息", groupName = "计件数量或工时统计信息", manageShow = false)
public class PieceworkSystemServiceImpl extends SkyeyeBusinessServiceImpl<PieceworkSystemDao, PieceworkSystem> implements PieceworkSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PieceworkSystemServiceImpl.class);

    @Autowired
    private FarmStaffService farmStaffService;

    @Autowired
    private MachinProcedureAcceptProductNumService machinProcedureAcceptProductNumService;

    @Autowired
    private MachinProcedureAcceptService machinProcedureAcceptService;

    @Autowired
    private ICheckWorkService iCheckWorkService;

    @Override
    public void writePieceworkSystem() {
        List<FarmStaff> farmStaffList1 = farmStaffService.queryFarmStaffList();
        List<String> staffIdList = farmStaffList1.stream().map(FarmStaff::getStaffId).collect(Collectors.toList());
        Map<String, Map<String, Object>> stringMapMap = iAuthUserService.queryUserMationListByStaffIds(staffIdList);

        // 构建员工ID到工种类型的映射
        Map<String, Integer> staffWorkTypeMap = stringMapMap.values().stream()
            .collect(Collectors.toMap(
                m -> m.get("staffId").toString(),
                m -> {
                    Object type = m.get("workstationType");
                    return (type != null) ? Integer.parseInt(type.toString()) : null;
                }
            ));

        Set<String> tempStaffIds = staffWorkTypeMap.entrySet().stream()
            .filter(e -> e.getValue().equals(UserStaffWorkstationType.HOURLY_WORKER.getKey())
                || e.getValue().equals(UserStaffWorkstationType.PIECE_WORKER.getKey()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        // 车间id→FarmStaff列表（只含临时工），保证单价/工位按当前车间取值
        Map<String, List<FarmStaff>> farmStaffMap = farmStaffList1.stream()
            .filter(staff -> tempStaffIds.contains(staff.getStaffId()))
            .collect(Collectors.groupingBy(FarmStaff::getFarmId));

        // 获取前一天日期
        LocalDate yesterday = LocalDate.now().minusDays(CommonNumConstants.NUM_ONE);
        int dayOfMonth = yesterday.getDayOfMonth();
        YearMonth yearMonth = YearMonth.from(yesterday);
        String month = yearMonth.toString();
        String yesterdayStr = yesterday.toString();

        // 遍历所有车间和临时员工
        for (Map.Entry<String, List<FarmStaff>> entry : farmStaffMap.entrySet()) {
            String farmId = entry.getKey();
            for (FarmStaff farmStaff : entry.getValue()) {
                String staffId = farmStaff.getStaffId();
                Map<String, Object> staffMation = stringMapMap.get(staffId);
                if (ObjectUtil.isEmpty(staffMation)) continue;

                // 安全获取工种类型
                Object typeObj = staffMation.get("workstationType");
                if (typeObj == null) {
                    LOGGER.warn("工种类型为空: staffId={}", staffId);
                    continue;
                }

                Integer workstationType;
                try {
                    workstationType = Integer.valueOf(typeObj.toString());
                } catch (NumberFormatException e) {
                    LOGGER.error("无效的工种类型: staffId={}, value={}", staffId, typeObj);
                    continue;
                }

                // 跳过正式工并清理历史记录
                if (workstationType.equals(UserStaffWorkstationType.CONTRACT_WORKER.getKey())) {
                    PieceworkSystem existingRecord = queryByStaffIdAndMonth(staffId, farmId, month);
                    if (ObjectUtil.isNotEmpty(existingRecord)) {
                        deleteById(existingRecord.getId());
                        LOGGER.info("删除正式工历史记录: staffId={}", staffId);
                    }
                    continue;
                }

                // 初始化变量
                int currentWorkType = 0;
                String dayValue = "";
                boolean hasWorkRecord = false;
                String hourlyPrice = "";
                BigDecimal piecePriceDecimal = BigDecimal.ZERO;

                if (workstationType.equals(UserStaffWorkstationType.HOURLY_WORKER.getKey())) {
                    // 小时工逻辑
                    hourlyPrice = staffMation.get("hourlyPrice").toString();

                    List<Map<String, Object>> checkWorkList = iCheckWorkService.queryInfoByStaffIdsAndDates(
                        staffId, yesterdayStr
                    );

                    long totalMilliseconds = 0;
                    for (Map<String, Object> checkWork : checkWorkList) {
                        String workHours = MapUtil.getStr(checkWork, "workHours");
                        if (StrUtil.isNotEmpty(workHours)) {
                            totalMilliseconds += calculateHours(workHours);
                        }
                    }
                    double hours = (double) TimeUnit.MILLISECONDS.toHours(totalMilliseconds);
                    dayValue = "B-" + hours;
                    currentWorkType = 2;
                    hasWorkRecord = true;
                } else if (workstationType.equals(UserStaffWorkstationType.PIECE_WORKER.getKey())) {
                    // 计件工逻辑：使用当前车间的计件单价
                    String piecePrice = farmStaff.getPieceWorkPrice();
                    piecePriceDecimal = (piecePrice == null || piecePrice.trim().isEmpty())
                        ? BigDecimal.ZERO : new BigDecimal(piecePrice.trim());

                    List<MachinProcedureAcceptProductNum> allPieces =
                        machinProcedureAcceptProductNumService.queryMachinProcedureAcceptProductNumByStaffId(staffId);

                    if (CollectionUtil.isNotEmpty(allPieces)) {
                        List<String> parentIds = allPieces.stream().map(MachinProcedureAcceptProductNum::getParentId)
                            .distinct().collect(Collectors.toList());

                        List<MachinProcedureAccept> acceptList =
                            machinProcedureAcceptService.queryProcedureAcceptByIds(parentIds);

                        // 只统计当前车间的验收单：日期 + farmId
                        Map<String, String> acceptCreateDateMap = new HashMap<>();
                        for (MachinProcedureAccept accept : acceptList) {
                            if (!farmId.equals(accept.getFarmId())) {
                                continue;
                            }
                            String createDate = accept.getCreateTime().substring(0, 10);
                            acceptCreateDateMap.put(accept.getId(), createDate);
                        }

                        String totalPieces = CommonNumConstants.NUM_ZERO.toString();
                        for (MachinProcedureAcceptProductNum piece : allPieces) {
                            String createDate = acceptCreateDateMap.get(piece.getParentId());
                            if (yesterdayStr.equals(createDate)) {
                                String allNumber = StrUtil.isEmpty(piece.getAllNumber()) ? CommonNumConstants.NUM_ZERO.toString() : piece.getAllNumber();
                                totalPieces = CalculationUtil.add(totalPieces, allNumber, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
                            }
                        }
                        // 当天在该车间无产量时不写记录
                        if (!CommonNumConstants.NUM_ZERO.toString().equals(totalPieces)) {
                            dayValue = "A-" + totalPieces;
                            currentWorkType = 1;
                            hasWorkRecord = true;
                        }
                    }
                }

                // 查询该员工本月的记录
                PieceworkSystem record = queryByStaffIdAndMonth(staffId, farmId, month);

                // 如果记录不存在且当天有工作记录，创建新记录
                if (ObjectUtil.isEmpty(record) && hasWorkRecord) {
                    record = new PieceworkSystem();
                    record.setStaffId(staffId);
                    record.setDepartmentId(staffMation.get("departmentId").toString());
                    record.setFarmId(farmId);
                    record.setFarmStationId(farmStaff.getFarmStationId());

                    record.setDayMouth(month);
                    for (int i = 1; i <= 31; i++) {
                        setFieldValue(record, getDayFieldName(i), "");
                    }
                    record.setTotalNumPrice(BigDecimal.ZERO);
                    record.setTotalTimePrice("0");

                    if (currentWorkType == 1 || currentWorkType == 2) {
                        record.setIsNumTime(currentWorkType);
                        if (currentWorkType == 2) {
                            record.setTotalTimePrice(hourlyPrice);
                        } else if (currentWorkType == 1) {
                            record.setTotalNumPrice(piecePriceDecimal);
                        }
                    }
                }

                if (record == null) continue;

                // 更新当天的值
                if (!dayValue.isEmpty()) {
                    setFieldValue(record, getDayFieldName(dayOfMonth), dayValue);

                    if (currentWorkType == 2) {
                        if (StrUtil.isBlank(record.getTotalTimePrice()) || "0".equals(record.getTotalTimePrice())) {
                            record.setTotalTimePrice(hourlyPrice);
                        }
                    } else if (currentWorkType == 1) {
                        if (record.getTotalNumPrice() == null || BigDecimal.ZERO.equals(record.getTotalNumPrice())) {
                            record.setTotalNumPrice(piecePriceDecimal);
                        }
                    }

                    // 重新计算整月汇总
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    double totalHours = 0;
                    BigDecimal totalPieces = BigDecimal.ZERO;

                    for (int i = 1; i <= 31; i++) {
                        String value = (String) getFieldValue(record, getDayFieldName(i));
                        if (value != null && !value.isEmpty()) {
                            if (value.startsWith("A-")) {
                                BigDecimal pieces = new BigDecimal(value.substring(2));
                                totalPieces = totalPieces.add(pieces);
                                if (record.getTotalNumPrice() != null) {
                                    totalAmount = totalAmount.add(
                                        record.getTotalNumPrice().multiply(pieces)
                                    );
                                }
                            } else if (value.startsWith("B-")) {
                                double hours = Double.parseDouble(value.substring(2));
                                totalHours += hours;
                                if (record.getTotalTimePrice() != null) {
                                    totalAmount = totalAmount.add(
                                        new BigDecimal(record.getTotalTimePrice()).multiply(BigDecimal.valueOf(hours))
                                    );
                                }

                            }
                        }
                    }

                    record.setAllNum(totalPieces);
                    record.setAllTime(String.valueOf(totalHours));
                    record.setAllPrice(totalAmount.toString());
                    record.setCreateTime(DateUtil.getTimeAndToString());

                    if (record.getIsNumTime() == null && (currentWorkType == 1 || currentWorkType == 2)) {
                        record.setIsNumTime(currentWorkType);
                    }
                }

                // 保存记录
                if (StrUtil.isEmpty(record.getId())) {
                    if (record.getIsNumTime() != null) {
                        super.createEntity(record, StrUtil.EMPTY);
                        LOGGER.debug("创建计件记录: staffId={}, type={}", staffId, record.getIsNumTime());
                    }
                } else {
                    super.updateEntity(record, StrUtil.EMPTY);
                }
            }
        }
    }

    @Autowired
    private FarmService farmService;

    @Autowired
    private FarmStationService farmStationService;

    @Override
    public void queryPieceworkSystem(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 车间ID
        String farmId = commonPageInfo.getFromId();
        // 员工信息
        String keyword = commonPageInfo.getKeyword();
        // 开始时间
        String startTime = commonPageInfo.getStartTime();
        // 结束时间
        String endTime = commonPageInfo.getEndTime();
        QueryWrapper<PieceworkSystem> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(farmId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PieceworkSystem::getFarmId), farmId);
        }
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.between(MybatisPlusUtil.toColumns(PieceworkSystem::getDayMouth), startTime, endTime);
        }
        List<PieceworkSystem> pieceworkSystemList = list(queryWrapper);
        List<String> farmIds = pieceworkSystemList.stream().map(PieceworkSystem::getFarmId).collect(Collectors.toList());
        List<Farm> farmList = farmService.queryFarmListByIds(farmIds);
        Map<String, Farm> farmMap = farmList.stream().collect(Collectors.toMap(Farm::getId, Farm -> Farm));
        List<String> farmStationIds = pieceworkSystemList.stream().map(PieceworkSystem::getFarmStationId).collect(Collectors.toList());
        Map<String, FarmStation> stationMap = farmStationService.queryFarmStationListByIds(farmStationIds).stream().collect(Collectors.toMap(FarmStation::getId, Farm -> Farm));
        List<String> staffIds = pieceworkSystemList.stream().map(PieceworkSystem::getStaffId).collect(Collectors.toList());
        Map<String, Map<String, Object>> stringMapMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        pieceworkSystemList.forEach(
            pieceworkSystem -> {
                FarmStation farmStation = stationMap.get(pieceworkSystem.getFarmStationId());
                if (farmStation != null) {
                    pieceworkSystem.setFarmStationMation(farmStation);
                }
                Farm farmMation = farmMap.get(pieceworkSystem.getFarmId());
                if (farmMation != null) {
                    pieceworkSystem.setFarmMation(farmMation);
                }
                Map<String, Object> staffMation = stringMapMap.get(pieceworkSystem.getStaffId());
                if (staffMation != null) {
                    pieceworkSystem.setStaffMation(staffMation);
                }
            }
        );
        if (StrUtil.isNotEmpty(keyword)) {
            pieceworkSystemList = pieceworkSystemList.stream()
                .filter(p -> p.getStaffMation() != null
                    && StrUtil.containsIgnoreCase(p.getStaffMation().get("userName").toString(), keyword))
                .collect(Collectors.toList());
        }
        outputObject.settotal(page.getTotal());
        outputObject.setBeans(pieceworkSystemList);
    }

    // 根据员工ID和月份查询记录
    public PieceworkSystem queryByStaffIdAndMonth(String staffId, String farmId, String month) {
        QueryWrapper<PieceworkSystem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PieceworkSystem::getStaffId), staffId)
            .eq(MybatisPlusUtil.toColumns(PieceworkSystem::getFarmId), farmId)
            .eq(MybatisPlusUtil.toColumns(PieceworkSystem::getDayMouth), month);
        return getOne(queryWrapper);
    }

    // 通过反射获取字段值
    private Object getFieldValue(PieceworkSystem obj, String fieldName) {
        try {
            Field field = PieceworkSystem.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field: " + fieldName, e);
        }
    }

    private void setFieldValue(PieceworkSystem obj, String fieldName, String value) {
        try {
            Field field = PieceworkSystem.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    private String getDayFieldName(int day) {
        switch (day) {
            case 1:
                return "oneDayNum";
            case 2:
                return "twoDayNum";
            case 3:
                return "threeDayNum";
            case 4:
                return "fourDayNum";
            case 5:
                return "fiveDayNum";
            case 6:
                return "sixDayNum";
            case 7:
                return "sevenDayNum";
            case 8:
                return "eightDayNum";
            case 9:
                return "nineDayNum";
            case 10:
                return "tenDayNum";
            case 11:
                return "elevenDayNum";
            case 12:
                return "twelveDayNum";
            case 13:
                return "thirteenDayNum";
            case 14:
                return "fourteenDayNum";
            case 15:
                return "fifteenDayNum";
            case 16:
                return "sixteenDayNum";
            case 17:
                return "seventeenDayNum";
            case 18:
                return "eighteenDayNum";
            case 19:
                return "nineteenDayNum";
            case 20:
                return "twentyDayNum";
            case 21:
                return "twentyOneDayNum";
            case 22:
                return "twentyTwoDayNum";
            case 23:
                return "twentyThreeDayNum";
            case 24:
                return "twentyFourDayNum";
            case 25:
                return "twentyFiveDayNum";
            case 26:
                return "twentySixDayNum";
            case 27:
                return "twentySevenDayNum";
            case 28:
                return "twentyEightDayNum";
            case 29:
                return "twentyNineDayNum";
            case 30:
                return "thirtyDayNum";
            case 31:
                return "thirtyOneDayNum";
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }


    /**
     * HH:MM:SS的日期转换为毫秒
     *
     * @param time1 时间1 HH:MM:SS
     * @return 两个时间之和 HH
     */
    private Long calculateHours(String time1) {
        // 将两个HH:MM:SS转换成毫秒
        String[] parts = time1.split(CommonCharConstants.COLON_MARK);
        long hours = Long.parseLong(parts[0]) * 3600 * 1000;
        long minutes = Long.parseLong(parts[1]) * 60 * 1000;
        long seconds = Long.parseLong(parts[2]) * 1000;
        return hours + minutes + seconds;
    }
}
