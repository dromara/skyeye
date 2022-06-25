
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
            url: shopBasePath + "memberCar008",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: simpleTemplate,
            ajaxSendLoadBefore: function(hdb, json){
                json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
            },
            ajaxSendAfter:function(json){

                if(json.bean.insure == 1){
                    $("#insure").html("已购买");
                } else {
                    $("#insure").html("未购买");
                }

                matchingLanguage();
                form.render();
            }
        });

    });
});