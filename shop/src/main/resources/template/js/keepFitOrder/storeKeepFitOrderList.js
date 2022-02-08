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
        url: shopBasePath + 'keepFitOrder001',
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
            { field: 'userType', title: '客户类型', width: 80, align: "center", templet: function(d){
                if(d.userType == 1){
                    return "匿名客户";
                }else{
                    return "会员";
                }
            }},
            { field: 'memberCarPlate', title: '车牌号', width: 100, align: "left"},
            { field: 'contacts', title: '会员名称', width: 100 },
            { field: 'phone', title: '会员手机号', width: 100, align: "center"},
            { field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
            { field: 'servicePrice', title: '服务费', width: 100, align: "left"},
            { field: 'storeName', title: '所属门店', align: 'left', width: 120 },
            { field: 'state', title: '支付状态', width: 150, align: "center", templet: function(d){
                if(d.state == 1){
                    return "<span class='state-down'>待支付</span>";
                }else if(d.state == 2){
                    return "<span class='state-up'>已支付(待核销)</span>";
                }else if(d.state == 3){
                    return "<span class='state-up'>已支付(已核销)</span>";
                }
            }},
            { field: 'payPrice', title: '实付金额', width: 100, align: "left"},
            { field: 'payTime', title: '实付日期', align: 'center', width: 150 },
            { field: 'type', title: '订单来源', width: 80, align: "center", templet: function(d){
                if(d.type == 1){
                    return "线上下单";
                }else{
                    return "线下下单";
                }
            }},
            { field: 'createName', title: '录入人', width: 120 },
            { field: 'createTime', title: '单据日期', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
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
        }
    });

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "deleteKeepFitOrder", params: {id: data.id}, type: 'json', method: "DELETE", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 详情
    function select(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/keepFitOrder/keepFitOrderDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "keepFitOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 完成支付
    function complatePay(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/keepFitOrder/complatePayKeepFitOrder.html",
            title: '完成支付',
            pageId: "complatePayKeepFitOrder",
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

    exports('storeKeepFitOrderList', {});
});
