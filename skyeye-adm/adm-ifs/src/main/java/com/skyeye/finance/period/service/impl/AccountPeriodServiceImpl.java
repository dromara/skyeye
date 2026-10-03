package com.skyeye.finance.period.service.impl;

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
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.period.classenum.AccountPeriodState;
import com.skyeye.finance.period.dao.AccountPeriodDao;
import com.skyeye.finance.period.entity.AccountPeriod;
import com.skyeye.finance.period.service.AccountPeriodService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 会计期间：账套下按 yyyy-MM 唯一；OPEN 才允许审核/过账/冲销。
 * 关账分业务关账(BIZ_CLOSED)、财务关账(FIN_CLOSED)，可 reopen。
 */
@Service
@SkyeyeService(name = "会计期间管理", groupName = "财务中枢")
public class AccountPeriodServiceImpl extends SkyeyeBusinessServiceImpl<AccountPeriodDao, AccountPeriod>
    implements AccountPeriodService {

    @Override
    public QueryWrapper<AccountPeriod> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AccountPeriod> wrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotBlank(commonPageInfo.getObjectId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(AccountPeriod::getSetOfBooksId), commonPageInfo.getObjectId());
        }
        String year = commonPageInfo.getCustomParamsMapStr("year");
        if (StrUtil.isNotBlank(year)) {
            String yearStr = year.length() >= 4 ? year.substring(0, 4) : year;
            wrapper.eq(MybatisPlusUtil.toColumns(AccountPeriod::getYear), Integer.parseInt(yearStr));
        }
        return wrapper;
    }

    @Override
    protected void setDefaultOrderBy(CommonPageInfo commonPageInfo, QueryWrapper<AccountPeriod> wrapper) {
        wrapper.orderByAsc(MybatisPlusUtil.toColumns(AccountPeriod::getPeriodCode));
    }

    @Override
    public void validatorEntity(AccountPeriod entity) {
        super.validatorEntity(entity);
        // 由年+月拼期间码，如 2026-03
        if (StrUtil.isBlank(entity.getPeriodCode()) && entity.getYear() != null && entity.getPeriod() != null) {
            entity.setPeriodCode(String.format(Locale.ROOT, "%04d-%02d", entity.getYear(), entity.getPeriod()));
        }
        if (entity.getState() == null) {
            entity.setState(AccountPeriodState.OPEN.getKey());
        }
        // 同一账套期间码唯一
        QueryWrapper<AccountPeriod> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AccountPeriod::getSetOfBooksId), entity.getSetOfBooksId());
        qw.eq(MybatisPlusUtil.toColumns(AccountPeriod::getPeriodCode), entity.getPeriodCode());
        if (StrUtil.isNotEmpty(entity.getId())) {
            qw.ne(CommonConstants.ID, entity.getId());
        }
        if (ObjectUtil.isNotEmpty(getOne(qw))) {
            throw new CustomException("该账套期间已存在");
        }
    }

    /** 取期间记录（不校验状态）；凭证保存时绑定 periodId 用 */
    @Override
    public AccountPeriod getOpenPeriod(String setOfBooksId, String periodCode) {
        QueryWrapper<AccountPeriod> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AccountPeriod::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(AccountPeriod::getPeriodCode), periodCode);
        AccountPeriod period = getOne(qw);
        if (ObjectUtil.isEmpty(period)) {
            throw new CustomException("会计期间不存在：" + periodCode);
        }
        return period;
    }

    /** 过账/审核/冲销前：期间必须为 OPEN */
    @Override
    public void assertPeriodOpen(String setOfBooksId, String periodCode) {
        AccountPeriod period = getOpenPeriod(setOfBooksId, periodCode);
        if (!AccountPeriodState.OPEN.getKey().equals(period.getState())) {
            throw new CustomException("会计期间已关闭，禁止过账：" + periodCode);
        }
    }

    @Override
    public void closeBizPeriod(InputObject inputObject, OutputObject outputObject) {
        changeState(inputObject, AccountPeriodState.BIZ_CLOSED.getKey());
    }

    @Override
    public void closeFinPeriod(InputObject inputObject, OutputObject outputObject) {
        changeState(inputObject, AccountPeriodState.FIN_CLOSED.getKey());
    }

    @Override
    public void reopenPeriod(InputObject inputObject, OutputObject outputObject) {
        changeState(inputObject, AccountPeriodState.OPEN.getKey());
    }

    private void changeState(InputObject inputObject, Integer state) {
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<AccountPeriod> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, id);
        uw.set(MybatisPlusUtil.toColumns(AccountPeriod::getState), state);
        update(uw);
        refreshCache(id);
    }

    /** 一次性生成指定年 12 个开放期间（已存在的跳过） */
    @Override
    public void initYearPeriods(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String setOfBooksId = params.get("setOfBooksId").toString();
        int year = Integer.parseInt(params.get("year").toString());
        List<AccountPeriod> list = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String code = String.format(Locale.ROOT, "%04d-%02d", year, m);
            QueryWrapper<AccountPeriod> qw = new QueryWrapper<>();
            qw.eq(MybatisPlusUtil.toColumns(AccountPeriod::getSetOfBooksId), setOfBooksId);
            qw.eq(MybatisPlusUtil.toColumns(AccountPeriod::getPeriodCode), code);
            if (ObjectUtil.isNotEmpty(getOne(qw))) {
                continue;
            }
            AccountPeriod p = new AccountPeriod();
            p.setSetOfBooksId(setOfBooksId);
            p.setYear(year);
            p.setPeriod(m);
            p.setPeriodCode(code);
            p.setName(code);
            p.setState(AccountPeriodState.OPEN.getKey());
            p.setStartDate(String.format(Locale.ROOT, "%04d-%02d-01", year, m));
            list.add(p);
        }
        if (!list.isEmpty()) {
            createEntity(list, inputObject.getLogParams().get("id").toString());
        }
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
