
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
		    form = layui.form;
	    
	    //选择离职时间
		laydate.render({
			elem: '#quitTime',
			range: false
		});
	    
		matchingLanguage();
	    form.render();
	    form.on('submit(submit)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			quitTime: $("#quitTime").val(),
        			quitReason: $("#quitReason").val(),
    				rowId:parent.rowId,
        		};
    			AjaxPostUtil.request({url:reqBasePath + "staff006", params:params, type:'json', callback:function(json){
    				if(json.returnCode == 0){
    					parent.layer.close(index);
		 	        	parent.refreshCode = '0';
    				}else{
    					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    				}
    			}});
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});