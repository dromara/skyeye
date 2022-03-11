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

    var intercourseTime = GetUrlParam('intercourseTime');

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
            { field: 'mealBuyStoreName', title: '套餐购买门店', align: 'left', width: 120 },
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
            { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", rowspan: '2', templet: function(d){
                return shopUtil.getMealOrderWhetherGiveName(d);
            }},
            { field: 'createName', title: '服务顾问', width: 120 },
            { field: 'createTime', title: '操作时间', align: 'center', width: 150 }
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
            url: "../../tpl/keepFitOrder/keepFitOrderDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "keepFitOrderDetails",
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
        return {
            orderNum: $("#orderNum").val(),
            memberName: $("#memberName").val(),
            memberPhone: $("#memberPhone").val(),
            memberCarPlate: $("#memberCarPlate").val(),
            type: $("#type").val(),
            serviceTechnicianName: $("#serviceTechnicianName").val(),
            createName: $("#createName").val(),
            intercourseTime: intercourseTime
        };
    }

    exports('dayKeepFitOrderList', {});
});
