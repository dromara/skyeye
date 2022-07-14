
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function(exports) {
    winui.renderColor();
    layui.use(['form'], function(form) {
        var $ = layui.$;

        AjaxPostUtil.request({url: flowableBasePath + "checkworkbusinesstrip003", params: {rowId: parent.rowId}, type: 'json', method: 'GET', callback: function(json) {
            json.bean.stateName = getStateNameByState(json.bean.state, json.bean.stateName);

            $("#showForm").html(getDataUseHandlebars($("#useTemplate").html(), json));

            // 附件回显
            skyeyeEnclosure.showDetails({"enclosureUpload": json.bean.enclosureInfo});
            form.render();
            matchingLanguage();
        }});
    });
});