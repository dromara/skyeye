
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
            url: shopBasePath + "mealOrder002",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#simpleTemplate").html(),
            ajaxSendLoadBefore: function(hdb, json){
                json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
                if(json.bean.cancleState == 1){
                    json.bean.state = json.bean.state == 1 ? '待支付' : '已支付';
                }else{
                    json.bean.state = '已取消';
                }
                json.bean.type = json.bean.type == 1 ? '线上下单' : '线下下单';
            },
            ajaxSendAfter:function(json){

                matchingLanguage();
                form.render();
            }
        });

    });
});