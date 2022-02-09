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
        
        textool.init({
	    	eleId: 'description',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset', 'clear']
	    });

        // 加载我所在的门店
        shopUtil.queryStaffBelongStoreList(function (json){
            $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
        });
        
	    matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    contacts: $("#contacts").val(),
                    phone: $("#phone").val(),
                    email: $("#email").val(),
                    address: $("#address").val(),
                    description: $("#description").val(),
                    storeId: $("#storeId").val()
                };
                AjaxPostUtil.request({url: shopBasePath + "member002", params: params, type: 'json', method: "POST", callback: function(json){
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

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});