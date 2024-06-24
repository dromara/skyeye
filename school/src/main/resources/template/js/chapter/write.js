
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
    var objectId = GetUrlParam("objectId");
    var objectKey = GetUrlParam("objectKey");
    var subjectClassesId = GetUrlParam("subjectClassesId");


    if (isNull(id)) {
        dsFormUtil.initAddPageForStatic('content', 'FP2023082800010', {
            savePreParams: function (params) {
                params.objectId = objectId;
                params.objectKey = objectKey;
                params.subjectClassesId = subjectClassesId;
            }
        });
    } else {
        AjaxPostUtil.request({
            url: sysMainMation.schoolBasePath + "queryChapterById",
            params: {id: id},
            type: 'json',
            method: 'GET',
            callback: function (json) {
                let data = json.bean;
                dsFormUtil.initEditPageForStatic('content', 'FP2023082800011', data, {
                    savePreParams: function (params) {
                        params.subjectClassesId = subjectClassesId;
                    }
                });
            }
        });
    }

});