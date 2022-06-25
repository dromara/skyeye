
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form,
	    	laydate = layui.laydate;
	    
	    var myDate = new Date();  //获取当前时间
	    laydate.render({
			elem: '#notifyTime',
			type: 'datetime',
			min: myDate.toLocaleString(),
			done: function(value, date, endDate){
			}
		});
		
	    matchingLanguage();
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		if(isNull($("#notifyTime").val())){
        			winui.window.msg('请选择通知时间', {icon: 2, time: 2000});
        			return false;
        		}
	        	
	        	AjaxPostUtil.request({url:reqBasePath + "sysworkplan010", params: {planId: parent.rowId, notifyTime: $("#notifyTime").val()}, type: 'json', callback: function(json){
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
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});