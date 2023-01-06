
var objectId = "";

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
    if (isNull(objectId)) {
        winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
        return false;
    }

    showGrid({
        id: "showForm",
        url: reqBasePath + "queryServiceBeanCustom",
        params: {className: objectId},
        pagination: false,
        method: 'GET',
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function (hdb, json) {
            json.bean.serviceBean.tenantName = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("tenantEnum", 'enumFiledName', json.bean.serviceBean.tenant, 'name');
            json.bean.serviceBean.flowableName = json.bean.serviceBean.flowable ? '是' : '否';
        },
        ajaxSendAfter: function (json) {
            matchingLanguage();
            form.render();
        }
    });
});