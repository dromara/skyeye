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
            url: shopBasePath + "member003",
            params: {rowId:parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){},
            ajaxSendAfter:function(json){
            	
            	textool.init({
			    	eleId: 'description',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
            	
			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            contacts: $("#contacts").val(),
                            phone: $("#phone").val(),
                            email: $("#email").val(),
                            address: $("#address").val(),
                            description: $("#description").val()
                        };
                        AjaxPostUtil.request({url: shopBasePath + "member005", params: params, type: 'json', method: "PUT", callback: function(json){
                            if(json.returnCode == 0){
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            }else{
                                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            }
                        }});
                    }
                    return false;
                });
            }
        });

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});