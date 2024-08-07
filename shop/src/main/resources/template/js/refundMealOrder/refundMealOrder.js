
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
    var selTemplate = getFileContent('tpl/template/select-option.tpl');
    var price = GetUrlParam("price");
    var id = GetUrlParam("id");

    // 加载当前用户所属门店
    AjaxPostUtil.request({url: sysMainMation.shopBasePath + "storeStaff005", params: {}, type: 'json', method: "GET", callback: function(json) {
        $("#storeId").html(getDataUseHandlebars(selTemplate, json));
        // 套餐订单退款原因
        sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["shopMealRefundOrderReason"]["key"], 'select', "mealRefundReasonId", '', form);
        // 回显退款金额
        $("#refundPrice").val(price);
    }});

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var params = {
                mealOrderChildId: id,
                mealRefundReasonId: $("#mealRefundReasonId").val(),
                storeId: $("#storeId").val(),
                refundPrice: $("#refundPrice").val(),
            };

            AjaxPostUtil.request({url: shopBasePath + "refundMealOrder", params: params, type: 'json', method: "POST", callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }, async: true});
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});