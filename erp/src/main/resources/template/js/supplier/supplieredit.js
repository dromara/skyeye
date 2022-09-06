layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
        	textool = layui.textool;
        showGrid({
            id: "showForm",
            url: flowableBasePath + "supplier003",
            params: {rowId: parent.rowId},
            pagination: false,
            template: getFileContent('tpl/supplier/suppliereditTemplate.tpl'),
            ajaxSendLoadBefore: function(hdb) {},
            ajaxSendAfter:function (json) {
            	
            	textool.init({eleId: 'description', maxlength: 200});
            	
			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            supplierName: $("#supplierName").val(),
                            contacts: $("#contacts").val(),
                            phonenum: $("#phonenum").val(),
                            email: $("#email").val(),
                            description: $("#description").val(),
                            advanceIn: isNull($("#advanceIn").val()) ? '0' : $("#advanceIn").val(),
                            beginNeedGet: isNull($("#beginNeedGet").val()) ? '0' : $("#beginNeedGet").val(),
                            beginNeedPay: isNull($("#beginNeedPay").val()) ? '0' : $("#beginNeedPay").val(),
                            allNeedGet: isNull($("#allNeedGet").val()) ? '0' : $("#allNeedGet").val(),
                            allNeedPay: isNull($("#allNeedPay").val()) ? '0' : $("#allNeedPay").val(),
                            fax: $("#fax").val(),
                            telephone: $("#telephone").val(),
                            address: $("#address").val(),
                            taxNum: $("#taxNum").val(),
                            bankName: $("#bankName").val(),
                            accountNumber: $("#accountNumber").val(),
                            taxRate: isNull($("#taxRate").val()) ? '0' : $("#taxRate").val()
                        };
                        AjaxPostUtil.request({url: flowableBasePath + "supplier005", params: params, type: 'json', callback: function (json) {
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
});