
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui', 'element'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        element = layui.element,
        form = layui.form;
    let groupId = GetUrlParam("groupId");

    // 根据类型获取部分功能的使用说明
    systemCommonUtil.queryExplainMationByType(1, function (json) {
        $(".layui-colla-title").html(json.bean.title);
        $(".layui-colla-content").html(json.bean.content);
    });
    element.init();

    showGrid({
        id: "calcDiv",
        url: reqBasePath + "codemodel013",
        params: {groupId: groupId},
        pagination: false,
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json) {
            $.each(json.rows, function(i, item) {
                let index = i % titleColor.length;
                item.groupId = groupId;
                item.titleColor = titleColor[index];
            });
        },
        ajaxSendAfter:function (json) {
            form.render();
        }
    });

});