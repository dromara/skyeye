
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;

    showGrid({
        id: "showBox",
        url: reqBasePath + "staff010",
        params: {},
        pagination: false,
        method: "GET",
        template: $("#showTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
        },
        ajaxSendAfter:function (json) {
        }
    });

    form.render();

    exports('userHolidayPool', {});
});
