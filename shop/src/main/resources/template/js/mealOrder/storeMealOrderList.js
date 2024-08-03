var rowId = "";

var dataMation = {};

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
        soulTable = layui.soulTable;
    var selTemplate = getFileContent('tpl/template/select-option.tpl');
    //套餐订单

    // 加载列表数据权限
    loadAuthBtnGroup('messageTable', '1644237833988');

    // 加载当前用户所属门店
    let storeHtml = '';
    AjaxPostUtil.request({url: sysMainMation.shopBasePath + "storeStaff005", params: {}, type: 'json', method: "GET", callback: function(json) {
            storeHtml = getDataUseHandlebars(selTemplate, json);
            initTable(storeHtml);
        }, async: false});

    var storeId = "";
    form.on('select(storeId)', function(data) {
        var thisRowValue = data.value;
        storeId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.shopBasePath + 'queryMealOrderList',
            where: getTableParams(),
            even: true,
            page: true,
            toolbar: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'oddNumber', title: '订单编号', align: 'left', width: 180, templet: function (d) {
                        return '<a lay-event="detail" class="notice-title-click">' + d.oddNumber + '</a>';
                    }},
                { field: 'name', title: '会员名称', rowspan: '2', align: 'left', width: 150, templet: function (d) {
                        return getNotUndefinedVal(d.objectMation?.name);
                    }},
                { field: 'phone', title: '会员手机号', rowspan: '2', align: 'left', width: 150, templet: function (d) {
                        return getNotUndefinedVal(d.objectMation?.phone);
                    }},
                { field: 'absoluteAddress', title: '会员地址', rowspan: '2', align: 'left', width: 150, templet: function (d) {
                        return getNotUndefinedVal(d.objectMation?.absoluteAddress);
                    }},
                { field: 'storeName', title: '所属门店', rowspan: '2', align: 'left', width: 150, templet: function (d) {
                        return getNotUndefinedVal(d.storeMation?.name);
                    }},
                { field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
                { field: 'payPrice', title: '实付金额', width: 100, align: "left"},
                { field: 'state', title: '订单状态', rowspan: '2', width: 90, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("shopMealOrderState", 'id', d.state, 'name');
                    }},
                { field: 'natureName', title: '订单性质', width: 80, align: "center"},
                { field: 'createName', title: '专属顾问', width: 120 },
                { field: 'payTime', title: '支付时间', align: 'center', width: 150 },
                { field: 'type', title: '订单来源', width: 120, align: "center", templet: function (d) {
                    if(d.type == 1){
                        return "用户下单";
                    } else {
                        return "工作人员下单";
                    }
                }},
                { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("whetherEnum", 'id', d.whetherGive, 'name');
                }},
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
            ]],
            done: function(json) {
                matchingLanguage();
                soulTable.render(this);
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入订单编号", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                }, `<label class="layui-form-label">门店</label><div class="layui-input-inline">
						<select id="storeId" name="storeId" lay-filter="storeId" lay-search="">
						${storeHtml}
					</select></div>`);
            }
        });
    }

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { // 删除
            delet(data);
        } else if (layEvent == 'detail'){ // 详情
            detail(data)
        }else if (layEvent == 'completePayment'){ // 完成支付
            completePayment(data)
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024072800006', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeMealOrderAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.shopBasePath + "deleteMealOrderById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }, async: true});
        });
    }

    // 详情
    function detail(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024072800008&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "storeMealOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 完成支付
    function completePayment(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/mealOrder/complatePayMealOrder.html",
            title: '完成支付',
            pageId: "complatePayMealOrder",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        let params = {
            holderId: storeId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('storeMealOrderList', {});
});
