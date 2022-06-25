
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
        var selOption = getFileContent('tpl/template/select-option.tpl');

        // 加载套餐订单退款原因
        shopUtil.queryRefundMealOrderReasonList(function (json){
            $("#mealRefundReasonId").html(getDataUseHandlebars(selOption, json));
        });

        if(parent.dataMation.mealNum == parent.dataMation.remainMealNum){
            // 套餐未使用
            $("#refundPrice").val(parent.dataMation.unformatPayPrice);
        }else{
            var mealSinglePrice = division(parent.dataMation.unformatPayPrice, parent.dataMation.mealNum);
            var refundPrice = multiplication(mealSinglePrice, parent.dataMation.remainMealNum);
            $("#refundPrice").val(refundPrice);
        }

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    mealOrderChildId: parent.dataMation.mealOrderChildId,
                    mealRefundReasonId: $("#mealRefundReasonId").val(),
                    refundPrice: $("#refundPrice").val(),
                };

                AjaxPostUtil.request({url: shopBasePath + "refundMealOrder", params: params, type: 'json', method: "POST", callback: function(json){
                    if (json.returnCode == 0) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }else{
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }, async: true});
            }
            return false;
        });

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});