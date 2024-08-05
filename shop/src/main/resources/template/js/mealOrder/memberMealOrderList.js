var rowId = "";
var memberId = {};

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

    memberId = GetUrlParam("id");
    console.log(12345,memberId)


    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        params: {objectId:memberId},
        url: shopBasePath + 'queryMealMationByObjectId',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { field: 'name', title: '套餐名称', width: 120, align: "left", templet: function (d) {
                    return d.mealMation.name
                }},
            { field: 'name', title: '商品名称', width: 150, align: "left", templet: function (d) {
                    return d.materialMation.name
                }},
            { field: 'name', title: '商品规格', width: 180, align: "left", templet: function (d) {
                    return d.normsMation.name
                }},
            { field: 'mealPrice', title: '套餐价格', width: 100, align: "left"},
            { field: 'codeNum', title: '商品规格编码', width: 180, align: "left"},
            { field: 'startTime', title: '开始时间', width: 100, align: "left"},
            { field: 'endTime', title: '结束时间', width: 100, align: "left"},
            { field: 'state', title: '是否可用', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("whetherEnum", 'id', d.state, 'name');
                }},
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}

        ]],
        done: function(json) {
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if(layEvent == 'select'){ // 详情
            select(data)
        } else if (layEvent === 'returnOrder') { //编辑
            returnOrder(data);
        }
    });

    // 详情
    function select(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/mealOrder/storeMealOrderDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "storeMealOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    // 退款
    function returnOrder(data) {
        rowId = data.id;
        console.log(888,data)
        _openNewWindows({
            url: "../../tpl/refundMealOrder/refundMealOrder.html?objectId=" + data.objectId + '&price=' + data.mealMation.price + '&normsId=' + data.normsId + '&codeNum=' + data.codeNum ,
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "returnOrder",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    // function returnOrder(data) {
    //     layer.confirm("确认对该单据进行退款吗？", {icon: 3, title: "退款操作"}, function (index) {
    //         layer.close(index);
    //         var params = {
    //             mealOrderChildId: ,
    //             mealRefundReasonId: data.attrKey,
    //             storeId: appId
    //             refundPrice:
    //         };
    //         AjaxPostUtil.request({url: shopBasePath + "refundMealOrder", params: params, type: 'json', method: "PUT", callback: function (json) {
    //                 winui.window.msg('退款成功', {icon: 1, time: 2000});
    //                 loadTable();
    //             }});
    //     });
    // }


    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/mealOrder/storeMealOrderAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeMealOrderAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
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
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return {
            // orderNum: $("#orderNum").val(),
            objectId: memberId
        };
    }

    exports('memberMealOrderList', {});
});
