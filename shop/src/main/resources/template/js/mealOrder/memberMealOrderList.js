var rowId = "";

var memberMation = {};

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
    authBtn('1644239500255');

    memberMation = parent.memberMation;

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
            { field: 'orderNum', title: '订单号', align: 'left', width: 180, fixed: 'left', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.orderNum + '</a>';
            }},
            { field: 'contacts', title: '会员名称', width: 100 },
            { field: 'phone', title: '会员手机号', width: 100, align: "center"},
            { field: 'payablePrice', title: '应付金额', width: 100, align: "left"},
            { field: 'payPrice', title: '实付金额', width: 100, align: "left"},
            { field: 'state', title: '订单状态', width: 80, align: "center", templet: function (d) {
                return shopUtil.getMealOrderStateName(d);
            }},
            { field: 'natureName', title: '订单性质', width: 80, align: "center"},
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
            { field: 'createName', title: '专属顾问', width: 120 },
            { field: 'createTime', title: '创建时间', align: 'center', width: 150 },
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
            callBack: function(refreshCode) {
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/mealOrder/storeMealOrderAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeMealOrderAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

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
    function loadTable(){
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            orderNum: $("#orderNum").val(),
            memberId: memberMation.id
        };
    }

    exports('memberMealOrderList', {});
});
