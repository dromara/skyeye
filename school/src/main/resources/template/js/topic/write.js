
// 以下两个参数开启团队权限时有值
var objectId = '', objectKey = '';
var objectParams = {};
// 根据以下两个参数判断：工作流的判断是否要根据serviceClassName的判断
var serviceClassName;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery'], function (exports) {
    winui.renderColor();
    var $ = layui.$;
    var objectId = GetUrlParam("objectId");
    var objectKey = GetUrlParam("objectKey");
    objectParams.subjectClassesId = GetUrlParam("subjectClassesId");
        dsFormUtil.initAddPageForStatic('content', 'FP2024072000007', {
            savePreParams: function (params) {
                params.objectId = objectId;
                params.objectKey = objectKey;
                params.subjectClassesId = objectParams.subjectClassesId;
            }
        });


});