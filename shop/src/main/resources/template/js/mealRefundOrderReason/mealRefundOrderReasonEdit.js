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
        showGrid({
            id: "showForm",
            url: shopBasePath + "queryMealRefundOrderReasonById",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){},
            ajaxSendAfter:function(data){
            	
			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            id: parent.rowId,
                            titleCn: $("#titleCn").val(),
                            titleEn: $("#titleEn").val()
                        };
                        AjaxPostUtil.request({url: shopBasePath + "editMealRefundOrderReason", params: params, type: 'json', method: "PUT", callback: function (json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }, async: true});
                    }
                    return false;
                });
            }
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});