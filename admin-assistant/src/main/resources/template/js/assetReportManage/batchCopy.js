
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        textool = layui.textool,
        form = layui.form;
    var assetId = getNotUndefinedVal(GetUrlParam("assetId"));

    if (!isNull(assetId)) {
        AjaxPostUtil.request({url: sysMainMation.admBasePath + "queryAssetById", params: {"id": assetId}, type: 'json', method: 'GET', callback: function (json) {
            // 资产名称赋值
            $("#assetId").val(json.bean.name);
            $("#assetId").attr(initTableChooseUtil.chooseInputDataIdKey, json.bean.id);
        }});
    }

    textool.init({
        eleId: 'barCode',
        tools: ['copy', 'reset', 'clear']
    });

    $("body").on("click", ".chooseAssetBtn", function (e) {
        adminAssistantUtil.assetCheckType = false; // 选择类型，默认单选，true:多选，false:单选
        adminAssistantUtil.openAssetChoosePage(function (checkAssetMation){
            // 获取表格行号
            $("#assetId").attr(initTableChooseUtil.chooseInputDataIdKey, checkAssetMation.id);
            $("#assetId").val(checkAssetMation.name);
        });
    });

    matchingLanguage();
    form.render();
    form.on('submit(getBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            let params = {
                assetId: getNotUndefinedVal($("#assetId").attr(initTableChooseUtil.chooseInputDataIdKey)),
                state: $("#state").val(),
                limit: $("#number").val(),
                page: 1
            }
            AjaxPostUtil.request({url: sysMainMation.admBasePath + "queryAssetReportCodeList", params: params, type: 'json', method: 'POST', callback: function (json) {
                $("#barCode").val(json.rows.join('\n'));
                $("#tips").html("共计获取个" + json.total + "条形码");
            }});
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});