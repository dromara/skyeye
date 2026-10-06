package com.skyeye.finance.template.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.journal.classenum.JournalVoucherState;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.template.dao.VoucherTemplateDao;
import com.skyeye.finance.template.entity.VoucherTemplate;
import com.skyeye.finance.template.entity.VoucherTemplateLine;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.finance.template.service.VoucherTemplateLineService;
import com.skyeye.finance.template.service.VoucherTemplateService;
import com.skyeye.subject.classenum.AmountDirection;
import com.skyeye.subject.entity.AccountSubject;
import com.skyeye.subject.service.IfsAccountSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 凭证模板：按业务事项(eventType)配置借贷科目与金额字段。
 * amountExpr 对应 payload 中的金额键（默认 amount）；金额为 0 的行跳过。
 */
@Service
@SkyeyeService(name = "凭证模板管理", groupName = "财务中枢")
public class VoucherTemplateServiceImpl extends SkyeyeBusinessServiceImpl<VoucherTemplateDao, VoucherTemplate> implements VoucherTemplateService {

    @Autowired
    private VoucherTemplateLineService voucherTemplateLineService;

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        ifsSetOfBooksService.setMationForMap(beans, "setOfBooksId", "setOfBooksMation");
        return beans;
    }

    @Override
    public void validatorEntity(VoucherTemplate entity) {
        super.validatorEntity(entity);
        if (CollectionUtil.isEmpty(entity.getLines()) || entity.getLines().size() < 2) {
            throw new CustomException("请至少两行模板分录");
        }
        int lineNo = 1;
        for (VoucherTemplateLine line : entity.getLines()) {
            line.setLineNo(lineNo++);
            AccountSubject subject = findSubject(line);
            if (ObjectUtil.isEmpty(subject)) {
                throw new CustomException("模板科目不存在");
            }
            line.setSubjectId(subject.getId());
        }
    }

    @Override
    public void writePostpose(VoucherTemplate entity, String userId) {
        voucherTemplateLineService.saveLinkList(entity.getId(), entity.getLines());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        voucherTemplateLineService.deleteByPId(id);
        super.deletePostpose(id);
    }

    @Override
    public VoucherTemplate getDataFromDb(String id) {
        VoucherTemplate template = super.getDataFromDb(id);
        template.setLines(voucherTemplateLineService.selectByPId(id));
        return template;
    }

    @Override
    public VoucherTemplate selectById(String id) {
        VoucherTemplate template = super.selectById(id);
        ifsSetOfBooksService.setDataMation(template, VoucherTemplate::getSetOfBooksId);
        if (CollectionUtil.isNotEmpty(template.getLines())) {
            List<String> subjectIds = template.getLines().stream().map(VoucherTemplateLine::getSubjectId)
                .filter(StrUtil::isNotBlank).distinct().collect(java.util.stream.Collectors.toList());
            Map<String, Map<String, Object>> subjectMap = ifsAccountSubjectService.selectValIsMapByIds(subjectIds);
            template.getLines().forEach(line -> line.setSubjectMation(subjectMap.get(line.getSubjectId())));
        }
        return template;
    }

    /**
     * 取启用模板：优先本账套配置，其次全局（setOfBooksId 空）。
     * orderByDesc(setOfBooksId) 使有账套值的排前面。
     */
    @Override
    public VoucherTemplate getEnabledTemplate(String eventType, String setOfBooksId) {
        VoucherTemplate template = findEnabledTemplate(eventType, setOfBooksId);
        if (ObjectUtil.isEmpty(template)) {
            throw new CustomException("未配置业务事项凭证模板：" + eventType);
        }
        if (CollectionUtil.isEmpty(template.getLines())) {
            throw new CustomException("凭证模板无分录行：" + eventType);
        }
        return template;
    }

    @Override
    public VoucherTemplate findEnabledTemplate(String eventType, String setOfBooksId) {
        if (StrUtil.isBlank(eventType)) {
            return null;
        }
        QueryWrapper<VoucherTemplate> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(VoucherTemplate::getEventType), eventType);
        qw.eq(MybatisPlusUtil.toColumns(VoucherTemplate::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotBlank(setOfBooksId)) {
            qw.and(w -> w.eq(MybatisPlusUtil.toColumns(VoucherTemplate::getSetOfBooksId), setOfBooksId)
                .or().isNull(MybatisPlusUtil.toColumns(VoucherTemplate::getSetOfBooksId))
                .or().eq(MybatisPlusUtil.toColumns(VoucherTemplate::getSetOfBooksId), ""));
        }
        qw.orderByDesc(MybatisPlusUtil.toColumns(VoucherTemplate::getSetOfBooksId));
        List<VoucherTemplate> list = list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        VoucherTemplate template = selectById(list.get(0).getId());
        return template;
    }

    private AccountSubject findSubject(VoucherTemplateLine line) {
        if (line == null || StrUtil.isBlank(line.getSubjectId())) {
            return null;
        }
        return ifsAccountSubjectService.selectById(line.getSubjectId());
    }

    @Override
    public void initDefaultTemplates(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> created = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (DefaultVoucherTemplateCatalog.TemplateDef def : DefaultVoucherTemplateCatalog.all()) {
            if (existsAnyTemplate(def.getEventType())) {
                skipped.add(def.getEventType() + "(已存在)");
                continue;
            }
            try {
                List<VoucherTemplateLine> lines = new ArrayList<>();
                List<String> missingNums = new ArrayList<>();
                for (DefaultVoucherTemplateCatalog.LineDef lineDef : def.getLines()) {
                    AccountSubject subject = findSubjectByNums(lineDef.getSubjectNums());
                    if (ObjectUtil.isEmpty(subject)) {
                        missingNums.add(String.join("/", lineDef.getSubjectNums()));
                        continue;
                    }
                    VoucherTemplateLine line = new VoucherTemplateLine();
                    line.setLineNo(lineDef.getLineNo());
                    line.setDirection(lineDef.getDirection());
                    line.setSubjectId(subject.getId());
                    line.setAmountExpr(lineDef.getAmountExpr());
                    line.setSummaryTpl(lineDef.getSummaryTpl());
                    lines.add(line);
                }
                if (!missingNums.isEmpty()) {
                    failed.add(def.getEventType() + "(缺科目:" + String.join(",", missingNums) + ")");
                    continue;
                }
                if (lines.size() < 2) {
                    failed.add(def.getEventType() + "(分录不足)");
                    continue;
                }
                VoucherTemplate template = new VoucherTemplate();
                template.setName("默认-" + def.getName());
                template.setEventType(def.getEventType());
                template.setEnabled(EnableEnum.ENABLE_USING.getKey());
                template.setAutoPost(WhetherEnum.DISABLE_USING.getKey());
                template.setLines(lines);
                createEntity(template, userId);
                created.add(def.getEventType());
            } catch (Exception ex) {
                failed.add(def.getEventType() + "(" + StrUtil.blankToDefault(ex.getMessage(), "失败") + ")");
            }
        }

        Map<String, Object> bean = new HashMap<>();
        bean.put("created", created);
        bean.put("skipped", skipped);
        bean.put("failed", failed);
        bean.put("createdCount", created.size());
        bean.put("skippedCount", skipped.size());
        bean.put("failedCount", failed.size());
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    private boolean existsAnyTemplate(String eventType) {
        QueryWrapper<VoucherTemplate> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(VoucherTemplate::getEventType), eventType);
        return count(qw) > 0;
    }

    private AccountSubject findSubjectByNums(String[] nums) {
        if (nums == null) {
            return null;
        }
        for (String num : nums) {
            if (StrUtil.isBlank(num)) {
                continue;
            }
            QueryWrapper<AccountSubject> qw = new QueryWrapper<>();
            qw.eq(MybatisPlusUtil.toColumns(AccountSubject::getNum), num);
            qw.last("LIMIT 1");
            AccountSubject subject = ifsAccountSubjectService.getOne(qw, false);
            if (ObjectUtil.isNotEmpty(subject)) {
                return subject;
            }
        }
        return null;
    }

    /**
     * 用模板 + 业务 payload 拼草稿凭证（未落库）。
     * 辅助核算开关为 1 时从 payload 取 customerId/supplierId 等写入分录。
     */
    @Override
    public JournalVoucher buildVoucherFromTemplate(VoucherTemplate template, Map<String, Object> payload) {
        JournalVoucher voucher = new JournalVoucher();
        voucher.setSetOfBooksId(str(payload.get("setOfBooksId"), template.getSetOfBooksId()));
        voucher.setVoucherDate(str(payload.get("voucherDate"), DateUtil.getYmdTimeAndToString()));
        if (voucher.getVoucherDate() != null && voucher.getVoucherDate().length() >= 7) {
            voucher.setPeriodCode(voucher.getVoucherDate().substring(0, 7));
        }
        voucher.setEventType(template.getEventType());
        voucher.setSourceType(str(payload.get("sourceType"), template.getEventType()));
        voucher.setSourceId(str(payload.get("sourceId"), null));
        voucher.setSourceNo(str(payload.get("sourceNo"), null));
        voucher.setIdempotencyKey(str(payload.get("idempotencyKey"), null));
        voucher.setRemark(str(payload.get("remark"), template.getName()));
        voucher.setState(JournalVoucherState.DRAFT.getKey());

        List<JournalEntry> entries = new ArrayList<>();
        int lineNo = 1;
        for (VoucherTemplateLine line : template.getLines()) {
            String amount = resolveAmount(line.getAmountExpr(), payload);
            // 金额为 0 跳过（如无成本时不生成成本行）
            if (StrUtil.isBlank(amount) || "0".equals(amount) || "0.00".equals(amount)) {
                continue;
            }
            AccountSubject subject = findSubject(line);
            if (ObjectUtil.isEmpty(subject)) {
                throw new CustomException("模板科目不存在：" + line.getSubjectId());
            }
            JournalEntry entry = new JournalEntry();
            entry.setLineNo(lineNo++);
            entry.setSubjectId(subject.getId());
            entry.setDirection(line.getDirection());
            entry.setAmount(amount);
            entry.setSummary(renderSummary(line.getSummaryTpl(), payload));
            if (WhetherEnum.ENABLE_USING.getKey().equals(subject.getAuxCustomer())) {
                entry.setAuxCustomerId(str(payload.get("customerId"), null));
            }
            if (WhetherEnum.ENABLE_USING.getKey().equals(subject.getAuxSupplier())) {
                entry.setAuxSupplierId(str(payload.get("supplierId"), null));
            }
            if (WhetherEnum.ENABLE_USING.getKey().equals(subject.getAuxMaterial())) {
                entry.setAuxMaterialId(str(payload.get("materialId"), null));
            }
            if (WhetherEnum.ENABLE_USING.getKey().equals(subject.getAuxDepartment())) {
                entry.setAuxDepartmentId(str(payload.get("departmentId"), null));
            }
            if (WhetherEnum.ENABLE_USING.getKey().equals(subject.getAuxProject())) {
                entry.setAuxProjectId(str(payload.get("projectId"), null));
            }
            if (WhetherEnum.ENABLE_USING.getKey().equals(subject.getAuxDepot())) {
                // 调拨：借方调入仓，贷方调出仓；其他事项用 depotId
                if (AmountDirection.BORROW.getKey().equals(line.getDirection())) {
                    entry.setAuxDepotId(str(payload.get("toDepotId"), str(payload.get("depotId"), null)));
                } else {
                    entry.setAuxDepotId(str(payload.get("fromDepotId"), str(payload.get("depotId"), null)));
                }
            }
            entries.add(entry);
        }
        if (entries.size() < 2) {
            throw new CustomException("模板生成分录不足两行，请检查金额字段");
        }
        voucher.setEntries(entries);
        return voucher;
    }

    /** amountExpr 为 payload 字段名；取不到则回退 amount */
    private String resolveAmount(String expr, Map<String, Object> payload) {
        String key = StrUtil.blankToDefault(expr, "amount");
        Object val = payload.get(key);
        if (val == null) {
            val = payload.get("amount");
        }
        return val == null ? "0" : val.toString();
    }

    /** 摘要模板 {key} 用 payload 替换；缺字段时 {summary} 回退到来源单号，避免原文留下 {summary} */
    private String renderSummary(String tpl, Map<String, Object> payload) {
        String fallback = str(payload.get("summary"), str(payload.get("sourceNo"), ""));
        if (StrUtil.isBlank(tpl)) {
            return fallback;
        }
        String result = tpl;
        for (Map.Entry<String, Object> e : payload.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                result = result.replace("{" + e.getKey() + "}", e.getValue().toString());
            }
        }
        result = result.replace("{summary}", fallback);
        result = result.replace("{sourceNo}", str(payload.get("sourceNo"), ""));
        result = result.replaceAll("\\{\\w+\\}", "");
        return StrUtil.blankToDefault(result.trim(), fallback);
    }

    private String str(Object val, String def) {
        if (val == null || StrUtil.isBlank(val.toString())) {
            return def;
        }
        return val.toString();
    }
}
