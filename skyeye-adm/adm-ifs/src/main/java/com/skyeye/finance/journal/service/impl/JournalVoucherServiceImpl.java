package com.skyeye.finance.journal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.journal.classenum.JournalVoucherState;
import com.skyeye.finance.journal.dao.JournalVoucherDao;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.journal.service.JournalEntryService;
import com.skyeye.finance.journal.service.JournalVoucherService;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import com.skyeye.finance.period.entity.AccountPeriod;
import com.skyeye.finance.period.service.AccountPeriodService;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.subject.classenum.AmountDirection;
import com.skyeye.subject.entity.AccountSubject;
import com.skyeye.subject.service.IfsAccountSubjectService;
import com.skyeye.rest.crm.customer.service.ICrmCustomerService;
import com.skyeye.rest.erp.supplier.service.IErpSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 会计凭证：草稿 → 审核 → 过账；已过账可冲销。
 * 过账时回写科目余额；业务侧自动凭证走 createAndOptionallyPost。
 */
@Service
@SkyeyeService(name = "会计凭证管理", groupName = "财务中枢")
public class JournalVoucherServiceImpl extends SkyeyeBusinessServiceImpl<JournalVoucherDao, JournalVoucher> implements JournalVoucherService {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private AccountPeriodService accountPeriodService;

    @Autowired
    private SubjectBalanceService subjectBalanceService;

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    @Autowired
    private ICrmCustomerService iCrmCustomerService;

    @Autowired
    private IErpSupplierService iErpSupplierService;

    /**
     * 列表筛选：期间编码走 customParamsMap.periodCode（如 2026-03）。
     */
    @Override
    public QueryWrapper<JournalVoucher> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<JournalVoucher> wrapper = super.getQueryWrapper(commonPageInfo);
        String periodCode = commonPageInfo.getCustomParamsMapStr("periodCode");
        if (StrUtil.isNotBlank(periodCode)) {
            wrapper.eq(MybatisPlusUtil.toColumns(JournalVoucher::getPeriodCode), periodCode);
        }
        return wrapper;
    }

    @Override
    public void validatorEntity(JournalVoucher entity) {
        super.validatorEntity(entity);
        // 分录非空/科目/方向等由 @ApiModelProperty(required) 框架校验；此处只保留业务规则
        if (CollectionUtil.isEmpty(entity.getEntries()) || entity.getEntries().size() < 2) {
            throw new CustomException("凭证至少需要两行分录");
        }
        // 汇总借贷并校验平衡（金额小数位统一用 IfsConstants.NUM_AFTER_DOT）
        String debit = "0";
        String credit = "0";
        int line = 1;
        for (JournalEntry entry : entity.getEntries()) {
            if (CalculationUtil.compareTo(entry.getAmount(), "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) <= 0) {
                throw new CustomException("分录金额必须大于0");
            }
            entry.setLineNo(line++);
            if (AmountDirection.BORROW.getKey().equals(entry.getDirection())) {
                debit = CalculationUtil.add(debit, entry.getAmount(), IfsConstants.NUM_AFTER_DOT);
            } else {
                credit = CalculationUtil.add(credit, entry.getAmount(), IfsConstants.NUM_AFTER_DOT);
            }
        }
        if (CalculationUtil.compareTo(debit, credit, IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) != 0) {
            throw new CustomException("借贷不平衡：借方=" + debit + " 贷方=" + credit);
        }
        entity.setDebitTotal(debit);
        entity.setCreditTotal(credit);
        // 手工凭证默认草稿；业务事件默认 MANUAL，自动凭证由模板覆盖 eventType/sourceType
        if (entity.getState() == null) {
            entity.setState(JournalVoucherState.DRAFT.getKey());
        }
        if (StrUtil.isBlank(entity.getEventType())) {
            entity.setEventType(BizAcctEventType.MANUAL.getKey());
        }
        if (StrUtil.isBlank(entity.getSourceType())) {
            entity.setSourceType(BizAcctEventType.MANUAL.getKey());
        }
        // 期间码优先用凭证日期的 yyyy-MM；开账期间才允许新增/修改凭证
        if (StrUtil.isBlank(entity.getPeriodCode()) && StrUtil.isNotBlank(entity.getVoucherDate())
            && entity.getVoucherDate().length() >= 7) {
            entity.setPeriodCode(entity.getVoucherDate().substring(0, 7));
        }
        accountPeriodService.assertPeriodOpen(entity.getSetOfBooksId(), entity.getPeriodCode());
        AccountPeriod period = accountPeriodService.getOpenPeriod(entity.getSetOfBooksId(), entity.getPeriodCode());
        entity.setPeriodId(period.getId());
    }

    @Override
    protected void createPrepose(JournalVoucher entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        entity.setOddNumber(oddNumber);
        if (StrUtil.isBlank(entity.getName())) {
            entity.setName(oddNumber);
        }
    }

    @Override
    public void writePostpose(JournalVoucher entity, String userId) {
        // 主表落库后保存分录子表（SkyeyeLinkData）
        journalEntryService.saveLinkList(entity.getId(), entity.getEntries());
        super.writePostpose(entity, userId);
    }

    @Override
    public JournalVoucher getDataFromDb(String id) {
        JournalVoucher voucher = super.getDataFromDb(id);
        voucher.setEntries(journalEntryService.selectByPId(id));
        return voucher;
    }

    @Override
    public JournalVoucher selectById(String id) {
        JournalVoucher voucher = super.selectById(id);
        ifsSetOfBooksService.setDataMation(voucher, JournalVoucher::getSetOfBooksId);
        if (CollectionUtil.isNotEmpty(voucher.getEntries())) {
            List<String> subjectIds = voucher.getEntries().stream().map(JournalEntry::getSubjectId)
                .filter(StrUtil::isNotBlank).distinct().collect(java.util.stream.Collectors.toList());
            Map<String, Map<String, Object>> subjectMap = ifsAccountSubjectService.selectValIsMapByIds(subjectIds);
            voucher.getEntries().forEach(entry -> entry.setSubjectMation(subjectMap.get(entry.getSubjectId())));
            iCrmCustomerService.setName(voucher.getEntries(), "auxCustomerId", "auxCustomerName");
            iErpSupplierService.setName(voucher.getEntries(), "auxSupplierId", "auxSupplierName");
        }
        return voucher;
    }

    @Override
    protected void deletePostpose(String id) {
        // 仅草稿可删，避免已审核/已过账丢账
        JournalVoucher voucher = selectById(id);
        if (!JournalVoucherState.DRAFT.getKey().equals(voucher.getState())) {
            throw new CustomException("仅草稿凭证可删除");
        }
        journalEntryService.deleteByPId(id);
        super.deletePostpose(id);
    }

    /**
     * 审核：草稿 → 已审核；期间必须开放
     */
    @Override
    public void approveVoucher(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        JournalVoucher voucher = selectById(id);
        if (!JournalVoucherState.DRAFT.getKey().equals(voucher.getState())) {
            throw new CustomException("仅草稿可审核");
        }
        accountPeriodService.assertPeriodOpen(voucher.getSetOfBooksId(), voucher.getPeriodCode());
        UpdateWrapper<JournalVoucher> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, id);
        uw.set(MybatisPlusUtil.toColumns(JournalVoucher::getState), JournalVoucherState.APPROVED.getKey());
        update(uw);
        refreshCache(id);
        outputObject.setBean(selectById(id));
    }

    /**
     * 过账入口：回写科目余额后标记 POSTED
     */
    @Override
    public void postVoucher(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        JournalVoucher voucher = selectById(id);
        doPost(voucher, false);
        outputObject.setBean(selectById(id));
    }

    /**
     * 过账：手工必须已审核；业务自动过账/冲销凭证可跳过审核。期间必须开账。
     */
    private void doPost(JournalVoucher voucher, boolean skipApprove) {
        if (skipApprove) {
            if (!JournalVoucherState.DRAFT.getKey().equals(voucher.getState())
                && !JournalVoucherState.APPROVED.getKey().equals(voucher.getState())) {
                throw new CustomException("当前状态不可过账");
            }
        } else if (!JournalVoucherState.APPROVED.getKey().equals(voucher.getState())) {
            throw new CustomException("请先审核再过账");
        }
        accountPeriodService.assertPeriodOpen(voucher.getSetOfBooksId(), voucher.getPeriodCode());
        List<JournalEntry> entries = journalEntryService.selectByPId(voucher.getId());
        subjectBalanceService.applyPosting(voucher, entries);
        UpdateWrapper<JournalVoucher> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, voucher.getId());
        uw.set(MybatisPlusUtil.toColumns(JournalVoucher::getState), JournalVoucherState.POSTED.getKey());
        update(uw);
        refreshCache(voucher.getId());
    }

    /**
     * 冲销：按原分录方向取反生成新凭证并立即过账；原凭证标记作废（VOIDED）。
     * 不直接回滚余额，通过反向分录再次过账实现账务冲回。
     */
    @Override
    public void reverseVoucher(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        JournalVoucher origin = selectById(id);
        if (!JournalVoucherState.POSTED.getKey().equals(origin.getState())) {
            throw new CustomException("仅已过账凭证可冲销");
        }
        if (StrUtil.isNotBlank(origin.getReverseOfId())) {
            throw new CustomException("冲销凭证不可再次冲销");
        }
        QueryWrapper<JournalVoucher> reversedQ = new QueryWrapper<>();
        reversedQ.eq(MybatisPlusUtil.toColumns(JournalVoucher::getReverseOfId), origin.getId());
        reversedQ.last("LIMIT 1");
        if (ObjectUtil.isNotEmpty(getOne(reversedQ, false))) {
            throw new CustomException("该凭证已冲销");
        }
        accountPeriodService.assertPeriodOpen(origin.getSetOfBooksId(), origin.getPeriodCode());
        List<JournalEntry> originEntries = journalEntryService.selectByPId(id);
        JournalVoucher reverse = new JournalVoucher();
        reverse.setSetOfBooksId(origin.getSetOfBooksId());
        reverse.setPeriodId(origin.getPeriodId());
        reverse.setPeriodCode(origin.getPeriodCode());
        reverse.setVoucherDate(DateUtil.getYmdTimeAndToString());
        reverse.setSourceType(origin.getSourceType());
        reverse.setSourceId(origin.getSourceId());
        reverse.setSourceNo(origin.getSourceNo());
        reverse.setEventType(origin.getEventType());
        reverse.setReverseOfId(origin.getId());
        reverse.setIdempotencyKey("REV-" + origin.getId());
        reverse.setRemark("冲销凭证：" + origin.getOddNumber());
        // 借贷方向对调，金额与辅助核算原样带出
        List<JournalEntry> revEntries = new ArrayList<>();
        for (JournalEntry e : originEntries) {
            JournalEntry ne = new JournalEntry();
            ne.setSubjectId(e.getSubjectId());
            ne.setDirection(AmountDirection.BORROW.getKey().equals(e.getDirection())
                ? AmountDirection.LOAN.getKey() : AmountDirection.BORROW.getKey());
            ne.setAmount(e.getAmount());
            ne.setQty(e.getQty());
            ne.setSummary("冲销：" + StrUtil.blankToDefault(e.getSummary(), ""));
            ne.setAuxCustomerId(e.getAuxCustomerId());
            ne.setAuxSupplierId(e.getAuxSupplierId());
            ne.setAuxMaterialId(e.getAuxMaterialId());
            ne.setAuxDepartmentId(e.getAuxDepartmentId());
            ne.setAuxProjectId(e.getAuxProjectId());
            ne.setAuxDepotId(e.getAuxDepotId());
            revEntries.add(ne);
        }
        reverse.setEntries(revEntries);
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(reverse, userId);
        JournalVoucher saved = selectById(reverse.getId());
        doPost(saved, true);
        // 原凭证作废，保留追溯；余额已由冲销凭证过账冲回
        UpdateWrapper<JournalVoucher> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, origin.getId());
        uw.set(MybatisPlusUtil.toColumns(JournalVoucher::getState), JournalVoucherState.VOIDED.getKey());
        update(uw);
        refreshCache(origin.getId());
        outputObject.setBean(selectById(reverse.getId()));
    }

    /**
     * 业务事件/存货核算等调用：创建凭证，模板配置 autoPost=1 时直接过账。
     */
    @Override
    public JournalVoucher createAndOptionallyPost(JournalVoucher voucher, boolean autoPost) {
        String userId = StrUtil.EMPTY;
        if (InputObject.getLogParamsStatic() != null) {
            userId = String.valueOf(InputObject.getLogParamsStatic().getOrDefault("id", StrUtil.EMPTY));
        }
        if (StrUtil.isBlank(voucher.getId())) {
            voucher.setId(ToolUtil.getSurFaceId());
        }
        createEntity(voucher, userId);
        JournalVoucher saved = selectById(voucher.getId());
        if (autoPost) {
            doPost(saved, true);
            saved = selectById(voucher.getId());
        }
        return saved;
    }

    /**
     * 明细账：已过账凭证按期间展开分录，可按科目过滤
     */
    @Override
    public void queryDetailLedger(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String setOfBooksId = params.get("setOfBooksId").toString();
        String periodCode = params.get("periodCode").toString();
        String subjectId = params.get("subjectId") == null ? null : params.get("subjectId").toString();
        String auxCustomerId = params.get("auxCustomerId") == null ? null : params.get("auxCustomerId").toString();
        String auxSupplierId = params.get("auxSupplierId") == null ? null : params.get("auxSupplierId").toString();

        QueryWrapper<JournalVoucher> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getPeriodCode), periodCode);
        qw.in(MybatisPlusUtil.toColumns(JournalVoucher::getState),
            JournalVoucherState.POSTED.getKey(), JournalVoucherState.VOIDED.getKey());
        List<JournalVoucher> vouchers = list(qw);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (JournalVoucher v : vouchers) {
            List<JournalEntry> entries = journalEntryService.selectByPId(v.getId());
            for (JournalEntry e : entries) {
                if (StrUtil.isNotBlank(subjectId) && !subjectId.equals(e.getSubjectId())) {
                    continue;
                }
                if (StrUtil.isNotBlank(auxCustomerId) && !auxCustomerId.equals(e.getAuxCustomerId())) {
                    continue;
                }
                if (StrUtil.isNotBlank(auxSupplierId) && !auxSupplierId.equals(e.getAuxSupplierId())) {
                    continue;
                }
                Map<String, Object> row = new java.util.HashMap<>();
                row.put("voucherId", v.getId());
                row.put("oddNumber", v.getOddNumber());
                row.put("voucherDate", v.getVoucherDate());
                row.put("sourceType", v.getSourceType());
                row.put("sourceId", v.getSourceId());
                row.put("sourceNo", v.getSourceNo());
                row.put("subjectId", e.getSubjectId());
                AccountSubject subject = ifsAccountSubjectService.selectById(e.getSubjectId());
                row.put("subjectNum", subject == null ? "" : subject.getNum());
                row.put("direction", e.getDirection());
                row.put("amount", e.getAmount());
                row.put("auxCustomerId", e.getAuxCustomerId());
                row.put("auxSupplierId", e.getAuxSupplierId());
                row.put("auxDepartmentId", e.getAuxDepartmentId());
                row.put("auxDepotId", e.getAuxDepotId());
                row.put("summary", e.getSummary());
                row.put("reverseOfId", v.getReverseOfId());
                row.put("state", v.getState());
                rows.add(row);
            }
        }
        iCrmCustomerService.setNameForMap(rows, "auxCustomerId", "auxCustomerName");
        iErpSupplierService.setNameForMap(rows, "auxSupplierId", "auxSupplierName");
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }
}
