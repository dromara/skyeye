
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;

    matchingLanguage();
    form.render();
});

/**
 * 文件上传成功后的回调函数
 * @param json
 */
function uploadFileCallback(json) {
    var params = {
        name: json.bean.name,
        path: json.bean.fileAddress,
        type: json.bean.fileType,
        size: json.bean.size,
        sizeType: json.bean.fileSizeType,
        catalog: isNull(parent.categoryId) ? '0' : parent.categoryId,
    };
    AjaxPostUtil.request({url: reqBasePath + "createEnclosure", params: params, type: 'json', method: 'POST', callback: function (json) {

    }});
}

