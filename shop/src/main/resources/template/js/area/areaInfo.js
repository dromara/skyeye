
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
            url: shopBasePath + "area006",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: simpleTemplate,
            ajaxSendAfter:function(json){
                matchingLanguage();
                form.render();
            }
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});