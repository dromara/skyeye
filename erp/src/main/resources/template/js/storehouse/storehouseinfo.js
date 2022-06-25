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
            url: flowableBasePath + "storehouse007",
            params: {rowId: parent.rowId},
            pagination: false,
            template: simpleTemplate,
            ajaxSendAfter:function(json){
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