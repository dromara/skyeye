
// 以下两个参数开启团队权限时有值
var objectId = '', objectKey = '';
// 根据以下两个参数判断：工作流的判断是否要根据serviceClassName的判断
var serviceClassName;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
    var id = GetUrlParam("id");

    // 工序验收的【编辑布局】
    dsFormUtil.initEditPageForStatic('content', 'FP2024071500001', {}, {
        savePreParams: function (params) {
            params.machinProcedureFarmId = id
            params.id = null
        },

        saveData: function (params) {
            // 保存数据
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "writeMachinProcedureAccept", params: params, type: 'json', method: "POST", callback: function(json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        },
    });
});