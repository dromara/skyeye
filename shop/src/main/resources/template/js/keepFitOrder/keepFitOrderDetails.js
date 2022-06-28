
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;

        showGrid({
            id: "showForm",
            url: shopBasePath + "keepFitOrder002",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#simpleTemplate").html(),
            ajaxSendLoadBefore: function(hdb, json){
                json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
                json.bean.userType = json.bean.userType == 1 ? '匿名客户' : '会员';
                json.bean.state = shopUtil.getKeepFitOrderStateName(json.bean);
                json.bean.whetherGive = shopUtil.getMealOrderWhetherGiveName(json.bean);
                json.bean.type = json.bean.type == 1 ? '线上下单' : '线下下单';
            },
            ajaxSendAfter:function (json) {
                matchingLanguage();
                form.render();
            }
        });

    });
});