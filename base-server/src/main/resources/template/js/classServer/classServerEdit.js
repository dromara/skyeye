
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
        },
        ajaxSendAfter: function (json) {

            // 获取所有编码规则
            AjaxPostUtil.request({url: reqBasePath + "getAllCodeRuleList", params: {}, type: 'json', method: "GET", callback: function(data) {
                $("#codeRuleId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
                $("#codeRuleId").val(json.bean.codeRuleId);
            }, async: false});

            matchingLanguage();
            form.render();
            form.on('submit(formEditBean)', function (data) {
                if (winui.verifyForm(data.elem)) {
                    var params = {
                        className: objectId,
                        codeRuleId: $("#codeRuleId").val(),
                        id: isNull(json.bean.id) ? "" : json.bean.id
                    };
                    AjaxPostUtil.request({url: reqBasePath + "saveServiceBeanCustom", params: params, type: 'json', method: "POST", callback: function (json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});
                }
                return false;
            });
        }
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});