
// 以下两个参数开启团队权限时有值
var objectId = '', objectKey = '';
// 根据以下两个参数判断：工作流的判断是否要根据serviceClassName的判断
var serviceClassName;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tagEditor', 'laydate'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
    var id = GetUrlParam("id");

    AjaxPostUtil.request({
        url: sysMainMation.schoolBasePath + "queryAssignmentSubById",
        params: {id: id},
        type: 'json',
        method: 'GET',
        callback: function (json) {
            let data = json.bean;
            dsFormUtil.initEditPageForStatic('content', 'FP2024071300013', data, {
                savePreParams: function (params) {
                }
            });
        }
    });

});