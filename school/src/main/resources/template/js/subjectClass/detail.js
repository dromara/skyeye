
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

    subjectClassesId = GetUrlParam("subjectClassesId");

    if (isNull(subjectClassesId)) {
        winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
        return false;
    }

    loadDetail();
    function loadDetail() {
        showGrid({
            id: "showForm",
            url: schoolBasePath + "querySubjectClassesById",
            params: {id: subjectClassesId},
            pagination: false,
            method: 'GET',
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function (hdb, json) {
                var enabledValue = json.bean.enabled;
                var enumName = enabledValue === 1 ? '是' : enabledValue === 2 ? '否' : '未知';
                json.bean.enabledName = enumName;
            },
            ajaxSendAfter: function (json) {
                matchingLanguage();
                form.render();
            }
        });
    }
});