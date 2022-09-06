
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
            url: flowableBasePath + "erpworkprocedure008",
            params: {rowId: parent.rowId},
            pagination: false,
            template: getFileContent('tpl/erpWorkProcedure/erpWorkProcedureDetailTemplate.tpl'),
            ajaxSendLoadBefore: function(hdb) {
            },
            ajaxSendAfter:function (json) {
            	matchingLanguage();
            }
        });
    });
});