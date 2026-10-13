package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.classenum.AssetReportState;
import com.skyeye.eve.assets.dao.AssetDepreciationRunDao;
import com.skyeye.eve.assets.entity.Asset;
import com.skyeye.eve.assets.entity.AssetDepreciationDetail;
import com.skyeye.eve.assets.entity.AssetDepreciationRun;
import com.skyeye.eve.assets.entity.AssetReport;
import com.skyeye.eve.assets.service.AssetDepreciationDetailService;
import com.skyeye.eve.assets.service.AssetDepreciationRunService;
import com.skyeye.eve.assets.service.AssetReportService;
import com.skyeye.eve.assets.service.AssetService;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.event.service.BizAcctEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 固定资产月结批量折旧（直线法）。
 * 规则：当月新增次月起提；净值不低于残值；同一账套同一期间只允许成功跑一次。
 */
@Service
@SkyeyeService(name = "固定资产月结折旧", groupName = "资产模块")
public class AssetDepreciationRunServiceImpl
    extends SkyeyeBusinessServiceImpl<AssetDepreciationRunDao, AssetDepreciationRun>
    implements AssetDepreciationRunService {

    private static final int DEFAULT_USEFUL_LIFE_MONTHS = 60;
    private static final String DEFAULT_RESIDUAL_RATE = "5";

    @Autowired
    private AssetReportService assetReportService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetDepreciationDetailService assetDepreciationDetailService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Override
    protected QueryWrapper<AssetDepreciationRun> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AssetDepreciationRun> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AssetDepreciationRun::getPeriod));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AssetDepreciationRun::getCreateTime));
        return queryWrapper;
    }

    @Override
    public AssetDepreciationRun selectById(String id) {
        AssetDepreciationRun run = super.selectById(id);
        if (run != null) {
            run.setDetailList(assetDepreciationDetailService.selectByRunId(id));
        }
        return run;
    }

    @Override
    public void previewMonthlyDeprec(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String period = requiredPeriod(params);
        List<Map<String, Object>> lines = buildDeprecLines(period);
        String total = "0";
        for (Map<String, Object> line : lines) {
            total = CalculationUtil.add(CommonNumConstants.NUM_TWO, total, line.get("deprecAmount").toString());
            line.remove("_report");
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("period", period);
        bean.put("assetCount", lines.size());
        bean.put("totalAmount", total);
        outputObject.setBean(bean);
        outputObject.setBeans(lines);
        outputObject.settotal(lines.size());
    }

    @Override
    public void runMonthlyDeprec(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String period = requiredPeriod(params);
        String setOfBooksId = params.get("setOfBooksId").toString();
        AssetDepreciationRun exists = findSuccessRun(period, setOfBooksId);
        if (exists != null) {
            throw new CustomException("该账套期间已计提折旧，不可重复执行：" + period);
        }

        List<Map<String, Object>> lines = buildDeprecLines(period);
        if (CollectionUtil.isEmpty(lines)) {
            throw new CustomException("本期无可计提资产（请确认已入库、未报废/退货，且未提完）");
        }

        String userId = inputObject.getLogParams().get("id").toString();
        String total = "0";
        for (Map<String, Object> line : lines) {
            total = CalculationUtil.add(CommonNumConstants.NUM_TWO, total, line.get("deprecAmount").toString());
        }

        AssetDepreciationRun run = new AssetDepreciationRun();
        run.setPeriod(period);
        run.setSetOfBooksId(setOfBooksId);
        run.setTotalAmount(total);
        run.setAssetCount(lines.size());
        run.setState(CommonNumConstants.NUM_ONE);
        run.setRemark(StrUtil.blankToDefault(
            params.get("remark") == null ? null : params.get("remark").toString(),
            "固定资产月结折旧-" + period));
        createEntity(run, userId);

        List<AssetReport> toUpdate = new ArrayList<>();
        List<AssetDepreciationDetail> details = new ArrayList<>();
        for (Map<String, Object> line : lines) {
            AssetDepreciationDetail detail = new AssetDepreciationDetail();
            detail.setRunId(run.getId());
            detail.setAssetReportId(line.get("assetReportId").toString());
            detail.setAssetNum(line.get("assetNum") == null ? null : line.get("assetNum").toString());
            detail.setOriginalAmount(line.get("originalAmount").toString());
            detail.setResidualAmount(line.get("residualAmount").toString());
            detail.setAccumBefore(line.get("accumBefore").toString());
            detail.setDeprecAmount(line.get("deprecAmount").toString());
            detail.setAccumAfter(line.get("accumAfter").toString());
            details.add(detail);

            AssetReport report = (AssetReport) line.get("_report");
            report.setAccumDeprecAmount(detail.getAccumAfter());
            report.setLastDeprecMonth(period);
            if (StrUtil.isBlank(report.getDeprecStartMonth())) {
                report.setDeprecStartMonth(resolveStartMonth(report));
            }
            if (report.getUsefulLifeMonths() == null || report.getUsefulLifeMonths() <= 0) {
                report.setUsefulLifeMonths(DEFAULT_USEFUL_LIFE_MONTHS);
            }
            if (StrUtil.isBlank(report.getResidualRate())) {
                report.setResidualRate(DEFAULT_RESIDUAL_RATE);
            }
            toUpdate.add(report);
        }
        assetDepreciationDetailService.createEntity(details, userId);
        assetReportService.updateEntity(toUpdate, userId);

        // 业财一体：整批推一条折旧事件
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", BizAcctEventType.FIXED_ASSET_DEPREC.getKey());
            event.put("sourceType", "ADM_ASSET_DEPREC_RUN");
            event.put("sourceId", run.getId());
            event.put("sourceNo", "DEPREC-" + period + "-" + setOfBooksId);
            event.put("setOfBooksId", setOfBooksId);
            event.put("amount", total);
            event.put("summary", "固定资产月结折旧-" + period);
            bizAcctEventService.acceptEvent(event);
        } catch (Exception ignored) {
            // 失败可在业财事件台重试
        }

        run.setDetailList(details);
        outputObject.setBean(run);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private AssetDepreciationRun findSuccessRun(String period, String setOfBooksId) {
        QueryWrapper<AssetDepreciationRun> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationRun::getPeriod), period);
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationRun::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationRun::getState), CommonNumConstants.NUM_ONE);
        return getOne(qw, false);
    }

    private String requiredPeriod(Map<String, Object> params) {
        String period = params.get("period").toString().trim();
        if (StrUtil.isBlank(period)) {
            period = DateUtil.getPointTime(DateUtil.YYYY_MM);
        }
        if (period.length() != 7 || period.charAt(4) != '-') {
            throw new CustomException("折旧期间格式须为 YYYY-MM");
        }
        return period;
    }

    /**
     * 可计提：已入库且状态正常（非报废/退货）；期间 >= 开始月；未在本月提过；净值仍高于残值。
     */
    private List<Map<String, Object>> buildDeprecLines(String period) {
        QueryWrapper<AssetReport> qw = new QueryWrapper<>();
        String stateKey = MybatisPlusUtil.toColumns(AssetReport::getState);
        qw.isNotNull(stateKey).ne(stateKey, StrUtil.EMPTY);
        qw.ne(stateKey, AssetReportState.SCRAP.getKey());
        qw.ne(stateKey, AssetReportState.RETURN.getKey());
        List<AssetReport> reports = assetReportService.list(qw);
        if (CollectionUtil.isEmpty(reports)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> lines = new ArrayList<>();
        for (AssetReport report : reports) {
            String startMonth = resolveStartMonth(report);
            if (StrUtil.isBlank(startMonth) || period.compareTo(startMonth) < 0) {
                continue;
            }
            if (StrUtil.isNotBlank(report.getLastDeprecMonth()) && period.compareTo(report.getLastDeprecMonth()) <= 0) {
                continue;
            }
            Map<String, Object> calc = calcOneMonth(report, period);
            if (calc == null) {
                continue;
            }
            calc.put("_report", report);
            lines.add(calc);
        }
        if (CollectionUtil.isEmpty(lines)) {
            return lines;
        }
        List<String> assetIds = lines.stream()
            .map(line -> ((AssetReport) line.get("_report")).getAssetId())
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Asset> assetMap = CollectionUtil.isEmpty(assetIds)
            ? Collections.emptyMap() : assetService.selectMapByIds(assetIds);
        for (Map<String, Object> line : lines) {
            AssetReport report = (AssetReport) line.get("_report");
            Asset asset = assetMap.get(report.getAssetId());
            line.put("assetId", report.getAssetId());
            line.put("assetName", asset == null ? StrUtil.EMPTY : StrUtil.blankToDefault(asset.getName(), StrUtil.EMPTY));
            line.put("specifications", asset == null ? StrUtil.EMPTY : StrUtil.blankToDefault(asset.getSpecifications(), StrUtil.EMPTY));
            line.put("storageArea", StrUtil.blankToDefault(report.getStorageArea(), StrUtil.EMPTY));
            line.put("warehousingTime", StrUtil.blankToDefault(report.getWarehousingTime(), StrUtil.EMPTY));
            line.put("unitPrice", StrUtil.blankToDefault(report.getUnitPrice(), StrUtil.EMPTY));
        }
        return lines.stream()
            .sorted(Comparator.comparing(m -> String.valueOf(m.get("assetNum"))))
            .collect(Collectors.toList());
    }

    private Map<String, Object> calcOneMonth(AssetReport report, String period) {
        String original = StrUtil.blankToDefault(report.getUnitPrice(), "0");
        if (CalculationUtil.compareTo(original, "0", CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) <= 0) {
            return null;
        }
        int lifeMonths = report.getUsefulLifeMonths() == null || report.getUsefulLifeMonths() <= 0
            ? DEFAULT_USEFUL_LIFE_MONTHS : report.getUsefulLifeMonths();
        String residualRate = StrUtil.blankToDefault(report.getResidualRate(), DEFAULT_RESIDUAL_RATE);
        String residual = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, original,
            CalculationUtil.divide(residualRate, "100", CommonNumConstants.NUM_SIX));
        String accumBefore = StrUtil.blankToDefault(report.getAccumDeprecAmount(), "0");
        String netBook = CalculationUtil.subtract(original, accumBefore, CommonNumConstants.NUM_TWO);
        // 净值已到残值及以下，不再提
        if (CalculationUtil.compareTo(netBook, residual, CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) <= 0) {
            return null;
        }
        String depreciable = CalculationUtil.subtract(original, residual, CommonNumConstants.NUM_TWO);
        String monthly = CalculationUtil.divide(depreciable, String.valueOf(lifeMonths), CommonNumConstants.NUM_TWO);
        String remain = CalculationUtil.subtract(netBook, residual, CommonNumConstants.NUM_TWO);
        // 最后一期不超过剩余可提额
        String deprecAmount = monthly;
        if (CalculationUtil.compareTo(monthly, remain, CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) > 0) {
            deprecAmount = remain;
        }
        if (CalculationUtil.compareTo(deprecAmount, "0", CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) <= 0) {
            return null;
        }
        String accumAfter = CalculationUtil.add(CommonNumConstants.NUM_TWO, accumBefore, deprecAmount);

        Map<String, Object> line = new HashMap<>();
        line.put("assetReportId", report.getId());
        line.put("assetNum", report.getAssetNum());
        line.put("period", period);
        line.put("originalAmount", original);
        line.put("residualAmount", residual);
        line.put("usefulLifeMonths", lifeMonths);
        line.put("residualRate", residualRate);
        line.put("accumBefore", accumBefore);
        line.put("deprecAmount", deprecAmount);
        line.put("accumAfter", accumAfter);
        line.put("deprecStartMonth", resolveStartMonth(report));
        return line;
    }

    /**
     * 开始月：已配置优先；否则入库次月；无入库日则采购次月。
     */
    private String resolveStartMonth(AssetReport report) {
        if (StrUtil.isNotBlank(report.getDeprecStartMonth())) {
            return report.getDeprecStartMonth();
        }
        String baseTime = StrUtil.blankToDefault(report.getWarehousingTime(), report.getPurchaseTime());
        if (StrUtil.isBlank(baseTime) || baseTime.length() < 7) {
            return null;
        }
        return nextMonth(baseTime.substring(0, 7));
    }

    private String nextMonth(String yyyyMm) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(yyyyMm));
            cal.add(Calendar.MONTH, 1);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return null;
        }
    }
}
