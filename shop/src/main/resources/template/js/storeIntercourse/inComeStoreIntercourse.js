
var intercourseTime = "";
var keepfiStoreId = "";
var mealByStoreId = "";

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
    var selOption = getFileContent('tpl/template/select-option-must.tpl');

    // 加载我所在的门店--保养门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars(selOption, json));
    });
    form.on('select(storeId)', function(data) {
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    });

    laydate.render({
        elem: '#intercourseTime',
        range: '~'
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'queryStoreIntercourseList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'keepfiStoreName', title: '保养门店', width: 150 },
            { field: 'mealByStoreName', title: '缴费门店', width: 150 },
            { field: 'mealAllSinglePrice', title: '往来费用', align: 'center', width: 100},
            { field: 'intercourseTime', title: '往来时间', width: 120 },
            { field: 'state', title: '状态', align: 'center', width: 140, templet: function(d){
                if(d.state == 1){
                    return "<span class='state-down'>待确认(缴费门店)</span>";
                } else if(d.state == 2) {
                    return "<span class='state-up'>待确认(保养门店)</span>";
                } else {
                    return "<span class='state-up'>已确认</span>";
                }
            }},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'confirmPay') { // 确认操作
            confirmPay(data);
        } else if (layEvent === 'queryDetail') { // 查看明细
            queryDetail(data);
        }
    });

    function confirmPay(data){
        var msg = "是否收到（" + data.mealByStoreName + "）支付的：" + data.mealAllSinglePrice + "元保养往来费用？";
        layer.confirm(msg, { icon: 3, title: '提示' }, function (index) {
            var params = {
                id: data.id,
                state: 3
            };
            AjaxPostUtil.request({url: shopBasePath + "editStoreIntercourseState", params: params, type: 'json', method: "PUT", callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg("确认成功。", {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    function queryDetail(data){
        intercourseTime = data.intercourseTime;
        keepfiStoreId = data.keepfiStoreId;
        mealByStoreId = data.mealByStoreId;
        _openNewWindows({
            url: "../../tpl/keepFitOrder/dayKeepFitOrderList.html",
            title: '明细',
            pageId: "dayKeepFitOrderList",
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
        if(!isNull($("#intercourseTime").val())){
            startTime = $("#intercourseTime").val().split('~')[0].trim();
            endTime = $("#intercourseTime").val().split('~')[1].trim();
        }
        return {
            keepfitStoreId: $("#storeId").val(),
            mealByStoreName: $("#mealByStoreName").val(),
            state: $("#state").val(),
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('inComeStoreIntercourse', {});
});
