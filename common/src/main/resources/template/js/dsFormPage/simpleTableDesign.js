
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'soulTable', 'table'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        soulTable = layui.soulTable;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 获取属性
    AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionList", params: {className: parent.objectId}, type: 'json', method: "POST", callback: function (data) {
        $("#attrKey").html(getDataUseHandlebars(`<option value="">全部</option>{{#each rows}}<option value="{{attrKey}}">{{name}}</option>{{/each}}`, data));
        $("#attrKey").val(json.bean.attrKey);
    }, async: false});

    matchingLanguage();
    form.render();
    form.on('submit(formWriteBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var params = {
                id: isNull(parent.rowId) ? '' : parent.rowId,
                name: $("#name").val(),
                remark: $("#remark").val(),
                type: $("#type").val(),
                className: parent.objectId
            };
            AjaxPostUtil.request({url: reqBasePath + "writeDsFormPage", params: params, type: 'json', method: "POST", callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});