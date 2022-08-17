var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        table = layui.table;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 加载区域
    shopUtil.getShopAreaMation(function (json){
        $("#areaId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
        form.render('select');
    });
    form.on('select(areaId)', function(data) {
        shopUtil.queryStoreListByAreaId(data.value, function (json){
            $("#storeId").html(getDataUseHandlebars(selOption, json));
            form.render('select');
        });
    });

    // 加载所有门店
    shopUtil.queryAllStoreList(function (json){
        $("#mealByStoreId").html(getDataUseHandlebars(selOption, json));
        form.render('select');
    });

    laydate.render({elem: '#createTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'keepFitOrder001',
        where: getTableParams(),
        even: true,
        page: true,
        toolbar: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', rowspan: '2', type: 'numbers'},
            { field: 'orderNum', title: '订单号', align: 'left', width: 180, fixed: 'left', rowspan: '2', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.orderNum + '</a>';
            }},
            { field: 'userType', title: '客户类型', width: 80, align: "center", rowspan: '2', templet: function (d) {
                if(d.userType == 1){
                    return "匿名客户";
                } else {
                    return "会员";
                }
            }},
            { field: 'contacts', title: '会员名称', width: 100, rowspan: '2' },
            { field: 'phone', title: '会员手机号', width: 100, align: "center", rowspan: '2'},
            { field: 'modelType', title: '车型', width: 100, align: "left", rowspan: '2'},
            { field: 'memberCarPlate', title: '车牌号', width: 100, align: "left", rowspan: '2'},
            { field: 'vinCode', title: 'VIN码', width: 120, align: "left", rowspan: '2'},
            { field: 'mealName', title: '套餐名称', align: 'left', width: 150, rowspan: '2' },
            { field: 'mealPrice', title: '应缴金额', align: 'left', width: 100, rowspan: '2' },
            { field: 'payPrice', title: '实缴金额', align: 'left', width: 100, rowspan: '2' },
            { field: 'mealSinglePrice', title: '套餐内消耗', align: 'left', width: 120, rowspan: '2' },
            { field: 'mealBuyStoreName', title: '缴费门店', align: 'left', width: 150, rowspan: '2' },
            { field: 'storeName', title: '保养门店', align: 'left', width: 150, rowspan: '2' },
            { field: 'createName', title: '服务顾问', width: 120, rowspan: '2' },
            { field: 'serviceTechnicianName', title: '维修技师', align: 'left', width: 120, rowspan: '2' },
            /*{ field: 'payablePrice', title: '应付金额', width: 100, align: "left", rowspan: '2'},
            { field: 'servicePrice', title: '服务费', width: 100, align: "left", rowspan: '2'},*/
            { title: '下单地址', align: 'center', colspan: '2'},
            /*{ field: 'payPrice', title: '实付金额', width: 100, align: "left", rowspan: '2' },
            { field: 'payTime', title: '实付日期', align: 'center', width: 150, rowspan: '2' },*/
            { field: 'type', title: '订单来源', width: 80, align: "center", rowspan: '2', templet: function (d) {
                if(d.type == 1){
                    return "线上下单";
                } else {
                    return "线下下单";
                }
            }},
            { field: 'state', title: '订单状态', width: 100, rowspan: '2', align: "center", templet: function (d) {
                return shopUtil.getKeepFitOrderStateName(d);
            }},
            { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", rowspan: '2', templet: function (d) {
                return shopUtil.getMealOrderWhetherGiveName(d);
            }},
            { field: 'createTime', title: '操作时间', align: 'center', width: 150, rowspan: '2' },
        ],[
            { field: 'areaName', title: '区域', align: 'left', width: 120},
            { field: 'storeName', title: '保养门店', align: 'left', width: 120}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if(layEvent == 'select'){ // 详情
            select(data)
        }
    });

    // 详情
    function select(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/keepFitOrder/keepFitOrderDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "keepFitOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
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
        var startTime = "", endTime = "";
        if (!isNull($("#createTime").val())) {
            startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            orderNum: $("#orderNum").val(),
            memberName: $("#memberName").val(),
            memberPhone: $("#memberPhone").val(),
            state: $("#state").val(),
            vinCode: $("#vinCode").val(),
            memberCarPlate: $("#memberCarPlate").val(),
            areaId: $("#areaId").val(),
            type: $("#type").val(),
            mealByStoreId: $("#mealByStoreId").val(),
            storeId: $("#storeId").val(),
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('allKeepFitOrderList', {});
});
