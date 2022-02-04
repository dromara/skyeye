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
            url: shopBasePath + "store003",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){},
            ajaxSendAfter:function(data){
            	
            	textool.init({
			    	eleId: 'remark',
			    	maxlength: 400,
			    	tools: ['count', 'copy', 'reset', 'clear']
			    });

                // 加载区域
                shopUtil.getShopAreaMation(function (json){
                    $("#areaId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
                    $("#areaId").val(data.bean.areaId);
                });
            	
			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            name: $("#name").val(),
                            areaId: $("#areaId").val(),
                            remark: $("#remark").val()
                        };
                        AjaxPostUtil.request({url: shopBasePath + "store005", params: params, type: 'json', method: "PUT", callback: function(json){
                            if(json.returnCode == 0){
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            }else{
                                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            }
                        }, async: true});
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