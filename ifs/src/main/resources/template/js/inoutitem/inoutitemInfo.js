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
            url: flowableBasePath + "inoutitem006",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#simpleTemplate").html(),
            ajaxSendAfter:function (json) {
                $("#inoutitemType").html((json.bean.inoutitemType == "1") ? '<span class="state-up">收入</span>' : '<span class="state-down">支出</span>');
                matchingLanguage();
                form.render();
            }
        });

    });
});