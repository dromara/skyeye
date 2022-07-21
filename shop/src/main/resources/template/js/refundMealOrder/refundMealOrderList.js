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

    // 加载我所在的门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
    });
    form.on('select(storeId)', function(data) {
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    });

    // 套餐订单性质
    sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["shopMealOrderNature"]["key"], 'select', "natureId", '', form);

    laydate.render({elem: '#refundTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'queryRefundMealOrderList',
        where: getTableParams(),
        even: true,
        page: true,
        toolbar: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'orderNum', title: '订单号', align: 'left', width: 180, fixed: 'left', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.orderNum + '</a>';
            }},
            { field: 'contacts', title: '会员名称', width: 100 },
            { field: 'phone', title: '会员手机号', width: 100, align: "center"},
            { field: 'refundCreateName', title: '服务顾问', width: 130, align: "left"},
            { field: 'createName', title: '专属顾问', width: 130, align: "left"},
            { field: 'natureName', title: '订单性质', width: 100, align: "left"},
            { field: 'modelType', title: '车型', width: 100, align: "left"},
            { field: 'plate', title: '车牌', width: 100, align: "left"},
            { field: 'vinCode', title: 'VIN码', width: 150, align: "left"},
            { field: 'state', title: '审核状态', width: 80, align: "center", templet: function (d) {
                if(d.state == 1){
                    return "待审核";
                }else if(d.state == 2){
                    return "退款驳回";
                }else if(d.state == 3){
                    return "已退款";
                }else {
                    return "取消退款";
                }
            }},
            { field: 'storeName', title: '缴费门店', width: 150, align: "left"},
            { field: 'mealTitle', title: '套餐名称', width: 150, align: "left"},
            { field: 'mealPrice', title: '应缴金额', width: 120, align: "left"},
            { field: 'payPrice', title: '实缴金额', width: 120, align: "left"},
            { field: 'mealNum', title: '总保养次数', width: 120, align: "left"},
            { field: 'remainMealNum', title: '剩余保养次数', width: 120, align: "left"},
            { field: 'mealSinglePrice', title: '单次保养金额', width: 120, align: "left"},
            { field: 'refundPrice', title: '退款金额', width: 120, align: "left"},
            { field: 'type', title: '订单来源', width: 80, align: "center", templet: function (d) {
                if(d.type == 1){
                    return "线上下单";
                } else {
                    return "线下下单";
                }
            }},
            { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", templet: function (d) {
                return shopUtil.getMealOrderWhetherGiveName(d);
            }},
            { field: 'refundTime', title: '申请退款时间', align: 'center', width: 150 },
            { field: 'refundReasonName', title: '退款原因', width: 120, align: "left"},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'rejection') { // 驳回
            rejection(data);
        }else if(layEvent == 'select'){ // 详情
            select(data)
        }else if(layEvent == 'pass'){ // 退款
            pass(data)
        }
    });

    // 驳回
    function rejection(data){
        var msg = '确定驳回退款金额为：' + data.refundPrice + '元的退款申请吗';
        layer.confirm(msg, {icon: 3, title: '驳回'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "approvelRefundMealOrder", params: {id: data.id, state: 2}, type: 'json', method: "POST", callback: function (json) {
                winui.window.msg('驳回成功', {icon: 1, time: 2000});
                loadTable();
            }, async: true});
        });
    }

    // 退款
    function pass(data){
        var msg = '此次退款金额为：' + data.refundPrice + '元 ';
        layer.confirm(msg, {icon: 3, title: '退款'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "approvelRefundMealOrder", params: {id: data.id, state: 3}, type: 'json', method: "POST", callback: function (json) {
                winui.window.msg('退款成功.', {icon: 1, time: 2000});
                loadTable();
            }, async: true});
        });
    }

    // 详情
    function select(data){
        rowId = data.mealOrderId;
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
        var storeId = $("#storeId").val();
        if(isNull(storeId)){
            storeId = "-";
        }
        var startTime = "", endTime = "";
        if(!isNull($("#refundTime").val())){
            startTime = $("#refundTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#refundTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            memberName: $("#memberName").val(),
            memberPhone: $("#memberPhone").val(),
            plate: $("#plate").val(),
            vinCode: $("#vinCode").val(),
            createName: $("#createName").val(),
            consultantName: $("#consultantName").val(),
            state: $("#state").val(),
            type: $("#type").val(),
            whetherGive: $("#whetherGive").val(),
            natureId: $("#natureId").val(),
            storeId: storeId,
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('refundMealOrderList', {});
});
