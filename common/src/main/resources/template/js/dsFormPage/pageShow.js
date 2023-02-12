
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery'], function (exports) {
    winui.renderColor();
    var $ = layui.$;
    var pageId = GetUrlParam("pageId");
    if (isNull(pageId)) {
        winui.window.msg("请传入布局id", {icon: 2, time: 2000});
        return false;
    }
    // 获取布局信息
    AjaxPostUtil.request({url: reqBasePath + "dsformpage006", params: {id: pageId}, type: 'json', method: 'GET', callback: function (json) {
        console.log(json)
    }});

    exports('pageShow', {});
});
