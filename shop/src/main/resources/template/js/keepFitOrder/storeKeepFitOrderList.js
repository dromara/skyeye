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
            /*{ field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
            { field: 'servicePrice', title: '服务费', width: 100, align: "left"},*/
            { field: 'storeName', title: '保养门店', align: 'left', width: 120 },
            { field: 'state', title: '订单状态', width: 100, align: "center", templet: function(d){
                return shopUtil.getKeepFitOrderStateName(d);
            }},
            /*{ field: 'payPrice', title: '实付金额', width: 100, align: "left"},
            { field: 'payTime', title: '实付日期', align: 'center', width: 150 },*/
            { field: 'mealName', title: '套餐名称', align: 'left', width: 150 },
            { field: 'mealSinglePrice', title: '套餐内消耗', align: 'left', width: 120 },
            { field: 'serviceTechnicianName', title: '维修技师', align: 'left', width: 120 },
            { field: 'type', title: '订单来源', width: 80, align: "center", templet: function(d){
                if(d.type == 1){
                    return "线上下单";
                }else{
                    return "线下下单";
                }
            }},
            { field: 'createName', title: '服务顾问', width: 120 },
            { field: 'createTime', title: '操作时间', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
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
        }else if(layEvent == 'complateKeepFit'){ // 完成保养
            complateKeepFit(data)
        }else if(layEvent == 'verification'){ // 核销
            verification(data)
        }else if(layEvent == 'cancleOrder'){ // 取消订单
            cancleOrder(data)
        }
    });

    // 核销
    function verification(data){
        layer.confirm("确认对该单据进行核销吗？", {icon: 3, title: "核销操作"}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "verificationKeepFitOrder", params: {id: data.id}, type: 'json', method: "PUT", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg('核销成功', {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 取消订单
    function cancleOrder(data){
        layer.confirm('确认取消该订单吗？', {icon: 3, title: '取消确认'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "cancleKeepFitOrder", params: {id: data.id}, type: 'json', method: "PUT", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg('取消成功.', {icon: 1, time: 2000});
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

    // 保养完成
    function complateKeepFit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/keepFitOrder/complateKeepFitOrder.html",
            title: '保养完成',
            pageId: "complateKeepFitOrder",
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
            memberCarPlate: $("#memberCarPlate").val(),
            storeId: storeId
        };
    }

    exports('storeKeepFitOrderList', {});
});
