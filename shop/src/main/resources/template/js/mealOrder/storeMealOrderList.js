var rowId = "";

var dataMation = {};

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

    // 加载我所在的门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
    });

    // 加载套餐订单性质
    shopUtil.queryMealOrderNatureList(function (json){
        $("#natureId").html(getDataUseHandlebars(selOption, json));
        form.render('select');
    });

    form.on('select(storeId)', function(data) {
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    });

    laydate.render({
        elem: '#createTime',
        range: '~'
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'mealOrder001',
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
            { field: 'plate', title: '车牌号', width: 100, align: "left"},
            { field: 'vinCode', title: 'VIN码', width: 100, align: "left"},
            { field: 'mealTitle', title: '套餐名称', width: 150, align: "left"},
            { field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
            { field: 'payPrice', title: '实付金额', width: 100, align: "left"},
            { field: 'mealNum', title: '总保养次数', width: 100, align: "left"},
            { field: 'remainMealNum', title: '剩余保养次数', width: 100, align: "left"},
            { field: 'state', title: '订单状态', width: 80, align: "center", templet: function (d) {
                return shopUtil.getMealOrderStateName(d);
            }},
            { field: 'natureName', title: '订单性质', width: 80, align: "center"},
            { field: 'createName', title: '专属顾问', width: 120 },
            { field: 'payTime', title: '支付时间', align: 'center', width: 150 },
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
            { field: 'createTime', title: '创建时间', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { // 删除
            delet(data);
        }else if(layEvent == 'select'){ // 详情
            select(data)
        }else if(layEvent == 'complatePay'){ // 完成支付
            complatePay(data)
        }else if(layEvent == 'cancleOrder'){ // 取消订单
            cancleOrder(data)
        }else if(layEvent == 'refundMealOrder'){ // 退款
            refundMealOrder(data)
        }else if(layEvent == 'cancleRefundMealOrder'){ // 取消退款
            cancleRefundMealOrder(data)
        }
    });

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "deleteMealOrder", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }, async: true});
        });
    }

    // 取消订单
    function cancleOrder(data){
        layer.confirm('确认取消该订单吗？', {icon: 3, title: '取消确认'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "cancleMealOrder", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg('取消成功.', {icon: 1, time: 2000});
                loadTable();
            }, async: true});
        });
    }

    // 取消退款
    function cancleRefundMealOrder(data){
        layer.confirm('确认取消退款申请吗？', {icon: 3, title: '取消退款'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "approvelRefundMealOrder", params: {id: data.refundOrderId, state: 4}, type: 'json', method: "POST", callback: function (json) {
                winui.window.msg('取消成功.', {icon: 1, time: 2000});
                loadTable();
            }, async: true});
        });
    }

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

    // 退款
    function refundMealOrder(data){
        dataMation = data;
        _openNewWindows({
            url: "../../tpl/refundMealOrder/refundMealOrder.html",
            title: '退款',
            pageId: "refundMealOrder",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 完成支付
    function complatePay(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/mealOrder/complatePayMealOrder.html",
            title: '完成支付',
            pageId: "complatePayMealOrder",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
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
            createName: $("#createName").val(),
            type: $("#type").val(),
            natureId: $("#natureId").val(),
            state: $("#state").val(),
            storeId: storeId,
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('storeMealOrderList', {});
});
