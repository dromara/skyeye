
var typeId = "";
var secondTypeId = "";
var layedit;

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tableSelect', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    form = layui.form,
	    tableSelect = layui.tableSelect,
    	laydate = layui.laydate;
	    layedit = layui.layedit;
	
 		//定时通知时间选择器
 		laydate.render({
 		  elem: '#delayedTime'
 		  ,type: 'datetime'
 		});
 	
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		rowId: parent.rowId,
 	        		title: parent.title,
 	        		delayedTime: $("#delayedTime").val()
 	        	};
        		if(isNull(params.delayedTime)){
        			winui.window.msg('请选择定时通知时间', {icon: 2, time: 2000});
 	        		return false;
        		}
    			AjaxPostUtil.request({url:reqBasePath + "notice011", params:params, type: 'json', callback: function(json){
    				if (json.returnCode == 0) {
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