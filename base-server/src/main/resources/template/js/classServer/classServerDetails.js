
var objectId = "";
var appId = "";

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

    objectId = GetUrlParam("objectId");
    appId = GetUrlParam("appId");
    if (isNull(objectId)) {
        winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
        return false;
    }

    loadDetail();
    function loadDetail() {
        showGrid({
            id: "showForm",
            url: reqBasePath + "queryServiceBeanCustom",
            params: {className: objectId, appId: appId},
            pagination: false,
            method: 'GET',
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function (hdb, json) {
                json.bean.serviceBean.tenantName = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("tenantEnum", 'enumFiledName', json.bean.serviceBean.tenant, 'name');
                json.bean.serviceBean.flowableName = json.bean.serviceBean.flowable ? '是' : '否';
                json.bean.serviceBean.teamAuthName = json.bean.serviceBean.teamAuth ? '是' : '否';
            },
            ajaxSendAfter: function (json) {
                matchingLanguage();
                form.render();
            }
        });
    }

    $("body").on("click", "#editBean", function() {
        parent._openNewWindows({
            url: "../../tpl/classServer/classServerEdit.html?objectId=" + objectId + "&appId=" + appId,
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "classServerEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadDetail();
            }});
    });
});