
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
    var objectId = GetUrlParam("objectId");
    var objectKey = GetUrlParam("objectKey");
    var id = GetUrlParam("id");

    tabPageUtil.init({
        id: 'tab',
        prefixData: [{
            title: '已提交',
            pageUrl: '../../tpl/homework/hasSubmit.html?id=' + id,
        }],
        suffixData: [{
            title: '未提交',
            pageUrl: '../../tpl/homework/noSubmit.html?id=' + id,
        }],
        element: layui.element,
        object: {
            objectId: objectId,
            objectKey: objectKey,
        }
    });

    form.render();

});