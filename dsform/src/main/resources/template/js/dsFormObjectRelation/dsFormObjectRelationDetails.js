
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;

    showGrid({
        id: "showForm",
        url: reqBasePath + "dsFormObjectRelation004",
        params: {id: parent.rowId},
        pagination: false,
        method: 'GET',
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function (hdb) {
        },
        ajaxSendAfter: function (json) {
            matchingLanguage();
            form.render();
        }
    });
});