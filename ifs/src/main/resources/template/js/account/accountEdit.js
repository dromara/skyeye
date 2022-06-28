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
            url: flowableBasePath + "account003",
            params: {rowId:parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){

            },
            ajaxSendAfter:function (json) {
            	textool.init({
			    	eleId: 'remark',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
            	
                //设置是否默认
                $("input:radio[name=isDefault][value=" + json.bean.isDefault + "]").attr("checked", true);
                
                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            accountName: $("#accountName").val(),
                            serialNo: $("#serialNo").val(),
                            initialAmount: $("#initialAmount").val(),
                            isDefault: $("input[name='isDefault']:checked").val(),
                            remark: $("#remark").val()
                        };
                        AjaxPostUtil.request({url: flowableBasePath + "account005", params: params, type: 'json', method: "PUT", callback: function (json) {
                            if (json.returnCode == 0) {
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            } else {
                                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            }
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