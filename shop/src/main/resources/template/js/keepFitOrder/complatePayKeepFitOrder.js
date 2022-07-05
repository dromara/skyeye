
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;
        var orderNum = "";

        AjaxPostUtil.request({url: shopBasePath + "keepFitOrder002", params: {id: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
            orderNum = json.bean.orderNum;
            $("#payPrice").val(json.bean.unformatPayablePrice);
        }, async: false});

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    out_trade_no: orderNum,
                    total_fee: parseFloat($("#payPrice").val()).toFixed(2) * 100,
                };

                AjaxPostUtil.request({url: shopBasePath + "keepFitOrderNotify", params: params, type: 'json', method: "POST", callback: function (json) {
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
});