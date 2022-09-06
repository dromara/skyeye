
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;
        var simpleTemplate = $("#simpleTemplate").html();

        showGrid({
            id: "showForm",
            url: shopBasePath + "member008",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: simpleTemplate,
            ajaxSendAfter:function (json) {
                $("#enabled").html(json.bean.enabled == "1" ? "<span class='state-up'>启用</span>" : "<span class='state-down'>禁用</span>");

                // 加载车辆信息
                loadCarMation();
                // 加载套餐信息
                loadMealMation();
                // 加载套餐订单信息
                loadMealOrderMation();
                // 加载保养订单信息
                loadKeepFitOrderMation();

                matchingLanguage();
                form.render();
            }
        });

        function loadCarMation(){
            AjaxPostUtil.request({url: shopBasePath + "memberCar001", params: {memberId: parent.rowId}, type: 'json', method: "POST", callback: function (json) {
                $.each(json.rows, function (i, item) {
                    if(item.insure == '1'){
                        item.insure = "已购买";
                    } else if (item.insure == '2'){
                        item.insure = "未购买";
                    }
                    item.stateName = shopUtil.getMemberCarEnableStateName(item.enabled);
                });
                $("#showForm").append(getDataUseHandlebars($("#memberCarTemplate").html(), json));
            }, async: false});
        }

        function loadMealMation(){
            AjaxPostUtil.request({url: shopBasePath + "queryMealMationByMemberId", params: {memberId: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
                $("#showForm").append(getDataUseHandlebars($("#memberMealTemplate").html(), json));
            }, async: false});
        }

        function loadMealOrderMation(){
            var params = {
                memberId: parent.rowId,
                limit: 500,
                page: 1
            };
            AjaxPostUtil.request({url: shopBasePath + "mealOrder001", params: params, type: 'json', method: "POST", callback: function (json) {
                $.each(json.rows, function (i, item) {
                    item.state = shopUtil.getMealOrderStateName(item);
                });
                $("#showForm").append(getDataUseHandlebars($("#memberMealOrderTemplate").html(), json));
            }, async: false});
        }

        function loadKeepFitOrderMation(){
            var params = {
                memberId: parent.rowId,
                limit: 500,
                page: 1
            };
            AjaxPostUtil.request({url: shopBasePath + "keepFitOrder001", params: params, type: 'json', method: "POST", callback: function (json) {
                $.each(json.rows, function (i, item) {
                    item.state = shopUtil.getKeepFitOrderStateName(item);
                });
                $("#showForm").append(getDataUseHandlebars($("#memberKeepFitOrderTemplate").html(), json));
            }, async: false});
        }

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});