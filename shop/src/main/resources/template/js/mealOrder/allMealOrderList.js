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

    // 套餐订单性质
    sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["shopMealOrderNature"]["key"], 'select', "natureId", '', form);

    laydate.render({elem: '#createTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'mealOrder001',
        where: getTableParams(),
        toolbar: true,
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', rowspan: '2', type: 'numbers'},
            { field: 'orderNum', title: '订单号', align: 'left', width: 180, fixed: 'left', rowspan: '2', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.orderNum + '</a>';
            }},
            { field: 'contacts', title: '会员名称', width: 100, rowspan: '2' },
            { field: 'phone', title: '会员手机号', width: 100, rowspan: '2', align: "center"},
            { title: '下单地址', align: 'center', colspan: '2'},
            { field: 'plate', title: '车牌号', width: 100, rowspan: '2', align: "left"},
            { field: 'vinCode', title: 'VIN码', width: 100, rowspan: '2', align: "left"},
            { field: 'mealTitle', title: '套餐名称', width: 150, rowspan: '2', align: "left"},
            { field: 'payablePrice', title: '应付金额', width: 100, align: "left", rowspan: '2'},
            { field: 'payPrice', title: '实付金额', width: 100, align: "left", rowspan: '2'},
            { field: 'mealNum', title: '总保养次数', width: 100, align: "left", rowspan: '2'},
            { field: 'remainMealNum', title: '剩余保养次数', width: 100, align: "left", rowspan: '2'},
            { field: 'state', title: '订单状态', width: 80, align: "center", rowspan: '2', templet: function (d) {
                return shopUtil.getMealOrderStateName(d);
            }},
            { field: 'natureName', title: '订单性质', width: 80, rowspan: '2', align: "center"},
            { field: 'createName', title: '专属顾问', width: 120, rowspan: '2' },
            { field: 'payTime', title: '支付时间', align: 'center', rowspan: '2', width: 150 },
            { field: 'type', title: '订单来源', width: 80, align: "center", rowspan: '2', templet: function (d) {
                if(d.type == 1){
                    return "线上下单";
                } else {
                    return "线下下单";
                }
            }},
            { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", rowspan: '2', templet: function (d) {
                return shopUtil.getMealOrderWhetherGiveName(d);
            }},
            { field: 'createTime', title: '创建时间', align: 'center', width: 150, rowspan: '2' },
        ],[
            { field: 'areaName', title: '区域', align: 'left', width: 120},
            { field: 'storeName', title: '门店', align: 'left', width: 120}
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
    function select(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/mealOrder/storeMealOrderDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "storeMealOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        var startTime = "", endTime = "";
        if(!isNull($("#createTime").val())){
            startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            orderNum: $("#orderNum").val(),
            memberName: $("#memberName").val(),
            memberPhone: $("#memberPhone").val(),
            vinCode: $("#vinCode").val(),
            plate: $("#plate").val(),
            type: $("#type").val(),
            natureId: $("#natureId").val(),
            state: $("#state").val(),
            areaId: $("#areaId").val(),
            storeId: $("#storeId").val(),
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('allMealOrderList', {});
});
