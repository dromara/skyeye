
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
            url: shopBasePath + "meal006",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: simpleTemplate,
            ajaxSendLoadBefore: function(hdb, json){
                json.bean.mealExplain = stringManipulation.textAreaShow(json.bean.mealExplain);
            },
            ajaxSendAfter:function (json) {
                $("#logo").attr("src", fileBasePath + json.bean.logo);
                if(json.bean.type == 1){
                    $("#type").html("保养套餐");
                }

                if(json.bean.state == 1){
                    $("#state").html("下线");
                } else {
                    $("#state").html("上线");
                }

                matchingLanguage();
                form.render();
            }
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});