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
	    	eleId: 'remark',
	    	maxlength: 400,
	    	tools: ['count', 'copy', 'reset']
	    });
        
	    matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    plate: $("#plate").val(),
                    modelType: $("#modelType").val(),
                    vinCode: $("#vinCode").val(),
                    insure: $("input[name='insure']:checked").val(),
                    remark: $("#remark").val(),
                    memberId: parent.memberId
                };
                AjaxPostUtil.request({url: shopBasePath + "memberCar002", params: params, type: 'json', method: "POST", callback: function(json){
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