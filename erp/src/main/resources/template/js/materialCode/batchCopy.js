
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
    var selTemplate = getFileContent('tpl/template/select-option.tpl');
    var materialId = getNotUndefinedVal(GetUrlParam("materialId"));

    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryAllMaterialList", params: {}, type: 'json', method: 'GET', callback: function (json) {
        $("#materialId").html(getDataUseHandlebars(selTemplate, json));
        if (!isNull(materialId)) {
            $("#materialId").val(materialId);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryNormsListByMaterialId", params: {materialId: materialId}, type: 'json', method: 'GET', callback: function (result) {
                $("#normsId").html(getDataUseHandlebars(selTemplate, result));
                form.render("select");
            }, async: false});
        }
        form.render("select");
        skyeyeClassEnumUtil.showEnumDataListByClassName("materialNormsCodeInDepot", 'radio', "inDepot", '', form);

        erpOrderUtil.getDepotList(function (json){
            // 加载仓库数据
            $("#depotId").html(getDataUseHandlebars(selTemplate, json));
            form.render('select');
        });
    }});

    form.on('select(materialId)', function (data) {
        var materialId = $("#materialId").val();
        if (isNull(materialId)) {
            $("#normsId").html("");
        } else {
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryNormsListByMaterialId", params: {materialId: materialId}, type: 'json', method: 'GET', callback: function (json) {
                $("#normsId").html(getDataUseHandlebars(selTemplate, json));
                form.render("select");
            }});
        }
    });

    textool.init({
        eleId: 'barCode',
        tools: ['copy', 'reset', 'clear']
    });

    matchingLanguage();
    form.render();
    form.on('submit(getBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            let params = {
                materialId: $("#materialId").val(),
                normsId: $("#normsId").val(),
                inDepot: dataShowType.getData('inDepot'),
                number: $("#number").val(),
                limit: $("#number").val(),
                depotId: $("#depotId").val(),
                page: 1
            }
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryNormsBarCodeList", params: params, type: 'json', method: 'POST', callback: function (json) {
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