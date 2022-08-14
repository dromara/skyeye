
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

        showGrid({
            id: "showForm",
            url: flowableBasePath + "queryStoreHouseById",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#simpleTemplate").html(),
            ajaxSendAfter:function (json) {
                $("#isDefault").html(json.bean.isDefault == "1" ? "<span class='state-up'>是</span>" : "<span class='state-down'>否</span>");
                matchingLanguage();
                form.render();
            }
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});