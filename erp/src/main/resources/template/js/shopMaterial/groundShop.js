
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'flow', 'form', 'skuTable', 'fileUpload', 'textool'], function (exports) {
    winui.renderColor();
    var $ = layui.jquery,
        flow = layui.flow,
        form = layui.form,
        textool = layui.textool,
        skuTable = layui.skuTable;
    let skuTableObj, ue;

    // 默认展示所有启用的商品
    showMaterialList();

    // 搜索查询
    $("#searchTitle").bind("keypress", function() {
        var e =  window.event;
        if (e && e.keyCode == 13) { //回车键的键值为13
            showMaterialList();//刷新最新笔记列表
        }
    });

    let normsMap = {};
    $("body").on("click", ".folder-item", function (e) {
        let that = $(this);
        var id = that.attr("id");
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryTransMaterialById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            const material = json.bean.materialMation;
            $(".note-title").html(material.name);
            $(".right-content").html(getDataUseHandlebars($("#materialTemplate").html(), json));
            const randomId = getRandomValueToString();
            $("#contentBox").html(`<script id="content${randomId}" name="content${randomId}" type="text/plain"></script>`);

            $("#orderBy").val(isNull(json.bean.id) ? '0' : json.bean.orderBy);
            $("#giftPoint").val(isNull(json.bean.id) ? '0' : json.bean.giftPoint);
            $("#virtualSales").val(isNull(json.bean.id) ? '0' : json.bean.virtualSales);
            $("#remark").val(isNull(json.bean.id) ? '' : json.bean.remark);

            normsMap = {};
            $.each(material.materialNorms, function (index, item) {
                normsMap[item.tableNum] = item;
            });

            skyeyeClassEnumUtil.showEnumDataListByClassName("shopMaterialDistributionType", 'radio', "distributionType", json.bean.distributionType, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("shopMaterialDeliveryMethod", 'checkbox', "deliveryMethod", json.bean.deliveryMethod, form);
            textool.init({eleId: 'remark', maxlength: 200});
            // 商品图片
            $("#materialLogo").upload({
                "action": reqBasePath + "common003",
                "data-num": "1",
                "data-type": imageType,
                "uploadType": 29,
                "data-value": isNull(json.bean.id) ? '' : json.bean.logo,
                "function": function (_this, data) {
                    show(_this, data);
                }
            });
            $("#materialCarouselImg").upload({
                "action": reqBasePath + "common003",
                "data-num": "6",
                "data-type": imageType,
                "uploadType": 29,
                "data-value": isNull(json.bean.id) ? '' : json.bean.carouselImg,
                "function": function (_this, data) {
                    show(_this, data);
                }
            });
            // 加载商品详情
            ue = ueEditorUtil.initEditor('content' + randomId);
            ue.addListener("ready", function () {
                ue.setContent(isNull(json.bean.id) ? '' : json.bean.content);
            });
            // 加载sku表格
            var skuData = {};
            let shopMaterialNormsList = json.bean.shopMaterialNormsList;
            $.each(material.materialNorms, function (index, item) {
                let inPoingArr = getInPoingArr(shopMaterialNormsList, "normsId", item.id, null);
                if (isNull(inPoingArr)) {
                    // 未上架
                    item.whetherGround = '0';
                    item.isDefault = '2';
                } else {
                    // 已上架
                    item.whetherGround = '1';
                    item.isDefault = inPoingArr.isDefault;
                    item.estimatePurchasePrice = inPoingArr.estimatePurchasePrice;
                    item.salePrice = inPoingArr.salePrice;
                    item.carouselImg = inPoingArr.carouselImg;
                    item.logo = inPoingArr.logo;
                    item.logoType = inPoingArr.logoType;
                }

                skuData[item.tableNum] = item;
            });
            var isDefaultList = skyeyeClassEnumUtil.getEnumDataListByClassName("commonIsDefault");
            var shopMaterialNormsLogoType = skyeyeClassEnumUtil.getEnumDataListByClassName("shopMaterialNormsLogoType");
            var whetherEnum = skyeyeClassEnumUtil.getEnumDataListByClassName("whetherEnum");
            if (material.unit == 2) {
                isDefaultList = {rows: [{id: '1', name: '是'}]};
            }
            skuTableObj = skuTable.render({
                boxId: 'skyNorms',
                specTableElemId: 'fairy-spec-table',
                skuTableElemId: 'fairy-sku-table',
                type: '1',
                // 是否开启sku表行合并
                rowspan: true,
                // 多规格SKU表配置
                multipleSkuTableConfig: {
                    thead: [
                        {title: 'Logo类型<i class="red">*</i>', icon: ''},
                        {title: 'Logo', icon: ''},
                        {title: '是否上架<i class="red">*</i>', icon: ''},
                        {title: '轮播图', icon: ''},
                        {title: '成本价(元)<i class="red">*</i>', icon: 'layui-icon-cols'},
                        {title: '销售价(元)<i class="red">*</i>', icon: 'layui-icon-cols'},
                        {title: '是否默认<i class="red">*</i>', icon: ''},
                    ],
                    tbody: [
                        {type: 'select', field: 'logoType', verify: 'required', option: shopMaterialNormsLogoType.rows},
                        {type: 'image', field: 'logo', value: '', reqtext: ''},
                        {type: 'radio', field: 'whetherGround', verify: 'required', option: whetherEnum.rows},
                        {type: 'imageMulitple', field: 'carouselImg', uploadDataType: ["imageType"]},
                        {type: 'input', field: 'estimatePurchasePrice', value: '0', verify: 'required|money'},
                        {type: 'input', field: 'salePrice', value: '0', verify: 'required|money'},
                        {type: 'radioSingle', field: 'isDefault', verify: 'required', option: isDefaultList.rows, singleName: 'isDefaultName'},
                    ]
                },
                specData: material.normsSpec,
                skuData: skuData,
                otherMationData: material
            });
            matchingLanguage();
            form.render();
            form.on('submit(saveBean)', function (data) {
                if (winui.verifyForm(data.elem)) {
                    var params = {
                        id: isNull(json.bean.id) ? '' : json.bean.id,
                        materialId: id,
                        logo: $("#materialLogo").find("input[type='hidden'][name='upload']").attr("oldurl"),
                        content: encodeURIComponent(ue.getContent()),
                        remark: $("#remark").val(),
                        orderBy: $("#orderBy").val(),
                        giftPoint: $("#giftPoint").val(),
                        virtualSales: $("#virtualSales").val(),
                        distributionType: dataShowType.getData('distributionType'),
                        deliveryMethod: JSON.stringify(dataShowType.getData('deliveryMethod')),
                    }

                    var noError = false;
                    let skuData = skuTableObj.getFormSkuDataList();
                    skuData = skuData.filter(item => item.whetherGround == 1);
                    $.each(skuData, function (index, item) {
                        let norms = normsMap[item.tableNum];
                        item.normsId = norms.id;
                        item.logo = isNull(item.logo) ? '' : item.logo;
                        item.carouselImg = isNull(item.carouselImg) ? '' : item.carouselImg;
                        if (item.logoType == 2) {
                            // 单独设置LOGO
                            if (isNull(item.logo)) {
                                winui.window.msg('规格【' + norms.name + '】未上传LOGO，请上传', {icon: 2, time: 2000});
                                noError = true;
                                return false;
                            }
                        }

                        if (!isNull(item.isDefault)) {
                            if (item.isDefault.value == '1') {
                                let chooseNormsId = normsMap[item.isDefault.rowId].id;
                                if (chooseNormsId == item.normsId) {
                                    item.isDefault = '1';
                                } else {
                                    item.isDefault = '2';
                                }
                            } else {
                                item.isDefault = '2';
                            }
                        } else {
                            item.isDefault = '2';
                        }
                    });
                    if (noError) {
                        return false;
                    }
                    params.shopMaterialNormsList = JSON.stringify(skuData);

                    if (isNull(params.logo)) {
                        winui.window.msg('请上传商品LOGO', {icon: 2, time: 2000});
                        return false;
                    }
                    if (isNull(params.content)) {
                        winui.window.msg('请填写商品详情', {icon: 2, time: 2000});
                        return false;
                    }
                    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "saveShopMaterial", params: params, type: 'json', method: 'POST', callback: function (json) {
                        winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                        that.click();
                    }});
                }
                return false;
            });
        }});
    });

    matchingLanguage();
    form.render();

    // 所有启用的商品
    function showMaterialList(){
        $("#materialList").empty();
        flow.load({
            elem: '#materialList', //指定列表容器
            isAuto: true,
            scrollElem: "#folderChildList",
            done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
                var lis = [];
                var params = {
                    page: page,
                    limit: 15,
                    keyword: $("#searchTitle").val()
                };
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "material010", params: params, type: 'json', method: 'POST', callback: function (json) {
                    $.each(json.rows, function (index, item) {
                        item.shelvesStateName = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialShelvesState", 'id', item.shelvesState, 'name');
                    });
                    lis.push(getDataUseHandlebars($("#materialListTemplate").html(), json));
                    next(lis.join(''), (page * 15) < json.total);
                }});
            }
        });
    }

    exports('groundShop', {});
});
