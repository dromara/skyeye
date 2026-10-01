/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.decorate.dao.StorePageDao;
import com.skyeye.decorate.entity.StoreBanner;
import com.skyeye.decorate.entity.StorePage;
import com.skyeye.decorate.entity.StorePageComp;
import com.skyeye.decorate.enums.StorePageCompType;
import com.skyeye.decorate.enums.StorePageType;
import com.skyeye.decorate.service.StoreBannerService;
import com.skyeye.decorate.service.StorePageCompService;
import com.skyeye.decorate.service.StorePageService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "门店装修页", groupName = "门店装修", tenant = TenantEnum.NO_ISOLATION)
public class StorePageServiceImpl extends SkyeyeBusinessServiceImpl<StorePageDao, StorePage> implements StorePageService {

    private static final int MAX_COMP = 20;
    private static final int MAX_RECOMMEND = 10;
    private static final int MAX_NOTICE_TEXT = 100;

    @Autowired
    private StoreDecorateAuthHelper storeDecorateAuthHelper;

    @Autowired
    private StorePageCompService storePageCompService;

    @Autowired
    private StoreBannerService storeBannerService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Override
    @IgnoreTenant
    public void queryStorePageDecorate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        storeDecorateAuthHelper.assertStoreDecorateAccess(storeId);
        Integer pageType = StorePageType.STORE_HOME.getKey();
        if (params.get("pageType") != null && StrUtil.isNotBlank(params.get("pageType").toString())) {
            pageType = Integer.parseInt(params.get("pageType").toString());
        }
        StorePage page = getOrInitPage(storeId, pageType);
        page.setCompList(storePageCompService.queryListByPageId(page.getId()));
        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("bannerList", storeBannerService.listEnabledByStoreId(storeId));
        outputObject.setBean(result);
        outputObject.settotal(1);
    }

    @Override
    @IgnoreTenant
    public void saveStorePageDecorate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        storeDecorateAuthHelper.assertStoreDecorateAccess(storeId);
        Integer pageType = StorePageType.STORE_HOME.getKey();
        if (params.get("pageType") != null && StrUtil.isNotBlank(params.get("pageType").toString())) {
            pageType = Integer.parseInt(params.get("pageType").toString());
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        Object rawCompList = params.get("compList");
        List<StorePageComp> compList;
        if (rawCompList instanceof String) {
            compList = JSONUtil.toList(rawCompList.toString(), StorePageComp.class);
        } else {
            compList = JSONUtil.toList(JSONUtil.toJsonStr(rawCompList), StorePageComp.class);
        }
        validateComps(storeId, compList);

        StorePage page = findByStoreAndType(storeId, pageType);
        if (page == null || StrUtil.isBlank(page.getId())) {
            page = new StorePage();
            page.setStoreId(storeId);
            page.setPageType(pageType);
            page.setName("门店首页");
            page.setEnabled(EnableEnum.ENABLE_USING.getKey());
            createEntity(page, userId);
        } else {
            page.setEnabled(EnableEnum.ENABLE_USING.getKey());
            updateEntity(page, userId);
        }
        String pageId = page.getId();
        storePageCompService.deleteByPageId(pageId);
        int order = 1;
        for (StorePageComp comp : compList) {
            comp.setId(null);
            comp.setPageId(pageId);
            comp.setStoreId(storeId);
            if (comp.getEnabled() == null) {
                comp.setEnabled(EnableEnum.ENABLE_USING.getKey());
            }
            if (comp.getShowPc() == null) {
                comp.setShowPc(WhetherEnum.ENABLE_USING.getKey());
            }
            if (comp.getShowApp() == null) {
                comp.setShowApp(WhetherEnum.ENABLE_USING.getKey());
            }
            if (comp.getOrderBy() == null) {
                comp.setOrderBy(order);
            }
            if (comp.getConfigJson() == null) {
                comp.setConfigJson(new HashMap<>());
            }
            order++;
        }
        if (CollectionUtil.isNotEmpty(compList)) {
            storePageCompService.createEntity(compList, userId);
        }
        StorePage saved = selectById(pageId);
        saved.setCompList(storePageCompService.queryListByPageId(pageId));
        outputObject.setBean(saved);
        outputObject.settotal(1);
    }

    @Override
    @IgnoreTenant
    public void queryStorePageForC(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        // clientType: app / pc，空则不过滤端
        String clientType = params.get("clientType") == null ? StrUtil.EMPTY : params.get("clientType").toString();
        ShopStore store = shopStoreService.selectById(storeId);
        if (store == null || StrUtil.isBlank(store.getId())) {
            throw new CustomException("门店不存在");
        }
        StorePage page = findByStoreAndType(storeId, StorePageType.STORE_HOME.getKey());
        List<StorePageComp> comps = new ArrayList<>();
        if (page != null && StrUtil.isNotBlank(page.getId())
            && Objects.equals(page.getEnabled(), EnableEnum.ENABLE_USING.getKey())) {
            comps = storePageCompService.queryListByPageId(page.getId()).stream()
                .filter(c -> Objects.equals(c.getEnabled(), EnableEnum.ENABLE_USING.getKey()))
                .filter(c -> {
                    if ("pc".equalsIgnoreCase(clientType)) {
                        return !Objects.equals(c.getShowPc(), WhetherEnum.DISABLE_USING.getKey());
                    }
                    if ("app".equalsIgnoreCase(clientType)) {
                        return !Objects.equals(c.getShowApp(), WhetherEnum.DISABLE_USING.getKey());
                    }
                    return true;
                })
                .sorted(Comparator.comparing(c -> c.getOrderBy() == null ? 0 : c.getOrderBy()))
                .collect(Collectors.toList());
        }
        List<StoreBanner> banners = storeBannerService.listEnabledByStoreId(storeId);
        Map<String, Object> result = new HashMap<>();
        result.put("store", store);
        result.put("page", page);
        result.put("compList", comps);
        result.put("bannerList", banners);
        result.put("hasDecorate", CollectionUtil.isNotEmpty(comps));
        outputObject.setBean(result);
        outputObject.settotal(1);
    }

    private StorePage getOrInitPage(String storeId, Integer pageType) {
        StorePage page = findByStoreAndType(storeId, pageType);
        if (page != null && StrUtil.isNotBlank(page.getId())) {
            return page;
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        page = new StorePage();
        page.setStoreId(storeId);
        page.setPageType(pageType);
        page.setName("门店首页");
        page.setEnabled(EnableEnum.ENABLE_USING.getKey());
        createEntity(page, userId);
        storePageCompService.createDefaultComps(page.getId(), storeId, userId);
        return page;
    }

    private StorePage findByStoreAndType(String storeId, Integer pageType) {
        QueryWrapper<StorePage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(StorePage::getStoreId), storeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(StorePage::getPageType), pageType);
        return getOne(queryWrapper, false);
    }

    private void validateComps(String storeId, List<StorePageComp> compList) {
        if (compList == null) {
            throw new CustomException("组件列表不能为空");
        }
        if (compList.size() > MAX_COMP) {
            throw new CustomException("组件数量不能超过" + MAX_COMP);
        }
        Map<Integer, Integer> typeCount = new HashMap<>();
        Set<Integer> validTypes = Arrays.stream(StorePageCompType.values()).map(StorePageCompType::getKey).collect(Collectors.toSet());
        for (StorePageComp comp : compList) {
            if (comp.getCompType() == null || !validTypes.contains(comp.getCompType())) {
                throw new CustomException("存在未知组件类型");
            }
            typeCount.merge(comp.getCompType(), 1, Integer::sum);
            if (Objects.equals(comp.getCompType(), StorePageCompType.NOTICE.getKey())) {
                String text = MapUtil.getStr(comp.getConfigJson(), "text", "");
                if (StrUtil.length(text) > MAX_NOTICE_TEXT) {
                    throw new CustomException("公告不能超过" + MAX_NOTICE_TEXT + "字");
                }
            }
            if (Objects.equals(comp.getCompType(), StorePageCompType.RECOMMEND.getKey())) {
                assertRecommendIds(storeId, comp.getConfigJson());
            }
        }
        assertTypeLimit(typeCount, StorePageCompType.STORE_HEADER, 1);
        assertTypeLimit(typeCount, StorePageCompType.BANNER, 1);
        assertTypeLimit(typeCount, StorePageCompType.NOTICE, 1);
        assertTypeLimit(typeCount, StorePageCompType.COUPON, 1);
        assertTypeLimit(typeCount, StorePageCompType.RECOMMEND, 1);
        assertTypeLimit(typeCount, StorePageCompType.GOODS, 1);
    }

    private void assertTypeLimit(Map<Integer, Integer> typeCount, StorePageCompType type, int max) {
        if (typeCount.getOrDefault(type.getKey(), 0) > max) {
            throw new CustomException(type.getValue() + "组件最多" + max + "个");
        }
    }

    private void assertRecommendIds(String storeId, Map<String, Object> configJson) {
        if (CollectionUtil.isEmpty(configJson)) {
            return;
        }
        // recommendIds 约定为 String 数组：门店商品 id（shopMaterialStore.id）
        List<String> ids = JSONUtil.toList(
            JSONUtil.toJsonStr(configJson.getOrDefault("recommendIds", Collections.emptyList())), String.class)
            .stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (ids.size() > MAX_RECOMMEND) {
            throw new CustomException("店长推荐最多" + MAX_RECOMMEND + "个商品");
        }
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(ids);
        if (CollectionUtil.isEmpty(materialByIds) || materialByIds.size() != ids.size()) {
            throw new CustomException("推荐商品不存在或不完整");
        }
        for (Map<String, Object> map : materialByIds) {
            Map<String, Object> sms = JSONUtil.toBean(JSONUtil.toJsonStr(map.get("shopMaterialStore")), null);
            if (CollectionUtil.isEmpty(sms) || !storeId.equals(MapUtil.getStr(sms, "storeId"))) {
                throw new CustomException("推荐商品必须属于本店");
            }
        }
    }
}
