var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    // 加载我所在的门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
    });

    form.on('select(storeId)', function(data) {
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'mealOrder001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'orderNum', title: '订单号', align: 'left', width: 180, fixed: 'left', templet: function(d){
                    return '<a lay-event="select" class="notice-title-click">' + d.orderNum + '</a>';
                }},
            { field: 'contacts', title: '会员名称', width: 100 },
            { field: 'phone', title: '会员手机号', width: 100, align: "center"},
            { field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
            { field: 'payPrice', title: '实付金额', width: 100, align: "left"},
            { field: 'state', title: '订单状态', width: 80, align: "center", templet: function(d){
                return shopUtil.getMealOrderStateName(d);
            }},
            { field: 'payTime', title: '支付时间', align: 'center', width: 150 },
            { field: 'type', title: '订单来源', width: 80, align: "center", templet: function(d){
                if(d.type == 1){
                    return "线上下单";
                }else{
                    return "线下下单";
                }
            }},
            { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", templet: function(d){
                return shopUtil.getMealOrderWhetherGiveName(d);
            }},
            { field: 'createName', title: '专属顾问', width: 120 },
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
        }
    });

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "deleteMealOrder", params: {id: data.id}, type: 'json', method: "POST", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }, async: true});
        });
    }

    // 取消订单
    function cancleOrder(data){
        layer.confirm('确认取消该订单吗？', {icon: 3, title: '取消确认'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "cancleMealOrder", params: {id: data.id}, type: 'json', method: "PUT", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg('取消成功.', {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
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

    // 完成支付
    function complatePay(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/mealOrder/complatePayMealOrder.html",
            title: '完成支付',
            pageId: "complatePayMealOrder",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
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
        return {
            orderNum: $("#orderNum").val(),
            memberName: $("#memberName").val(),
            memberPhone: $("#memberPhone").val(),
            state: $("#state").val(),
            storeId: storeId
        };
    }

    exports('storeMealOrderList', {});
});
