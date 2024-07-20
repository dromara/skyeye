
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
    var id = getNotUndefinedVal(GetUrlParam("id"));

    textool.init({
        eleId: 'barCode',
        tools: ['copy', 'reset', 'clear']
    });

    matchingLanguage();
    form.render();
    form.on('submit(getBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            let params = {
                id: id,
                realNumber:  $("#realNumber").val(),
                profitNum: $("#profitNum").val(),
                lossNum: $("#lossNum").val(),
            }
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "complateInventoryChild", params: params, type: 'json', method: 'POST', callback: function (json) {
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



