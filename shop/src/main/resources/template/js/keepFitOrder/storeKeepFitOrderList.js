
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
    authBtn('1722173155602');//新增

    // 加载我所在的门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
    });

    // 加载所有门店
    shopUtil.queryAllStoreList(function (json){
        $("#mealByStoreId").html(getDataUseHandlebars(selOption, json));
        form.render('select');
    });

    laydate.render({elem: '#createTime', range: '~'});

    form.on('select(storeId)', function(data) {
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'queryKeepFitOrderList',
        where: getTableParams(),
        even: true,
        page: true,
        toolbar: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
            { field: 'oddNumber', title: '订单编号', align: 'left', width: 180, fixed: 'left', templet: function (d) {
                    return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                }},
            { field: 'name', title: '门店', width: 200, templet: function (d) {
                    return getNotUndefinedVal(d.storeMation?.name);
            }},
            { field: 'name', title: '会员名称', rowspan: '2', align: 'left', width: 150, templet: function (d) {
                    return getNotUndefinedVal(d.objectMation?.name);
                }},
            { field: 'userType', title: '用户类型', width: 100, templet: function (d) {
                    if(d.type == 1){
                        return "匿名用户";
                    } else {
                        return "会员";
                    }
            }},
            { field: 'type', title: '订单来源', width: 120, align: "center", templet: function (d) {
                    if(d.type == 1){
                        return "用户下单";
                    } else {
                        return "工作人员下单";
                    }
            }},
            { field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
            { field: 'payPrice', title: '实付金额', width: 100, align: "left"},
            { field: 'payTime', title: '支付时间', width: 100, align: "left"},
            { field: 'state', title: '状态', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("KeepFitOrderState", 'id', d.state, 'name');
                }},
            { field: 'onlineDay', title: '预约日期', width: 100 },
            { field: 'onlineTime', title: '预约时间', width: 100 },
            { field: 'createName', title: '创建人', align: 'left', width: 120 },
            { field: 'createTime', title: '创建时间', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if(layEvent == 'details'){ // 详情
            details(data)
        } else if (layEvent == 'complateKeepFit'){ // 完成保养
            complateKeepFit(data)
        } else if (layEvent == 'pay'){ // 完成支付
            pay(data)
        } else if (layEvent == 'verification'){ // 核销
            verification(data)
        } else if (layEvent == 'del'){ // 取消订单
            del(data)
        }
    });

    // 新增
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024072800004', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeKeepFitAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 删除
    function del(data, obj) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "deleteKeepFitOrderById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024072800005&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "storeKeepFitDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 核销
    function verification(data) {
        layer.confirm("确认对该单据进行核销吗？", {icon: 3, title: "核销操作"}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "verificationKeepFitOrder", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg('核销成功', {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 支付
    function pay(data) {
        console.log(data)
        _openNewWindows({
            url: "../../tpl/keepFitOrder/complatePayKeepFitOrder.html?id=" + data.id,
            title: '支付',
            pageId: "complatePayKeepFitOrder",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 保养
    function complateKeepFit(data) {
        _openNewWindows({
            url: "../../tpl/keepFitOrder/complateKeepFitOrder.html?storeId=" + data.storeId + '&id=' + data.id ,
            title: '保养',
            pageId: "complateKeepFitOrder",
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
        var storeId = $("#storeId").val();
        if(isNull(storeId)){
            storeId = "-";
        }
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
            type: $("#type").val(),
            mealByStoreId: $("#mealByStoreId").val(),
            storeId: storeId,
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('storeKeepFitOrderList', {});
});
