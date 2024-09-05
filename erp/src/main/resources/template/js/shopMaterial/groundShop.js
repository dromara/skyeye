
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

    $("body").on("click", ".folder-item", function (e) {
        var id = $(this).attr("id");
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryTransMaterialById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            const material = json.bean.materialMation;
            $(".note-title").html(material.name);
            $(".right-content").html(getDataUseHandlebars($("#materialTemplate").html(), json));
            const randomId = getRandomValueToString();
            $("#contentBox").html(`<script id="content${randomId}" name="content${randomId}" type="text/plain"></script>`);

            skyeyeClassEnumUtil.showEnumDataListByClassName("shopMaterialDistributionType", 'radio', "distributionType", json.bean.distributionType, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("shopMaterialDeliveryMethod", 'checkbox', "deliveryMethod", json.bean.deliveryMethod, form);
            textool.init({eleId: 'remark', maxlength: 200});
            // 商品图片
            $("#materialLogo").upload({
                "action": reqBasePath + "common003",
                "data-num": "1",
                "data-type": imageType,
                "uploadType": 29,
                "function": function (_this, data) {
                    show(_this, data);
                }
            });
            $("#materialCarouselImg").upload({
                "action": reqBasePath + "common003",
                "data-num": "6",
                "data-type": imageType,
                "uploadType": 29,
                "function": function (_this, data) {
                    show(_this, data);
                }
            });
            // 加载商品详情
            ue = ueEditorUtil.initEditor('content' + randomId);
            ue.addListener("ready", function () {
                ue.setContent('');
            });
            // 加载sku表格
            var skuData = {};
            $.each(material.materialNorms, function (index, item) {
                skuData[item.tableNum] = item;
            });
            var isDefaultList = skyeyeClassEnumUtil.getEnumDataListByClassName("commonIsDefault");
            var shopMaterialNormsLogoType = skyeyeClassEnumUtil.getEnumDataListByClassName("shopMaterialNormsLogoType");
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
                        {title: '轮播图', icon: ''},
                        {title: '成本价(元)<i class="red">*</i>', icon: 'layui-icon-cols'},
                        {title: '销售价(元)<i class="red">*</i>', icon: 'layui-icon-cols'},
                        {title: '是否默认<i class="red">*</i>', icon: ''},
                    ],
                    tbody: [
                        {type: 'select', field: 'logoType', verify: 'required', option: shopMaterialNormsLogoType.rows},
                        {type: 'image', field: 'logo', value: '', reqtext: ''},
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
                        content: encodeURIComponent(ue.getContent())
                    }
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
