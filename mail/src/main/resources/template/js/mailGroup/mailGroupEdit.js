
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    AjaxPostUtil.request({url:reqBasePath + "mailGroup004", params:{rowId: parent.rowId}, type: 'json', callback: function(json){
   			if(json.returnCode == 0){
   				$("#name").val(json.bean.name);
    			$("#desc").val(json.bean.desc);
    			
    			matchingLanguage();
    			form.render();
    	 	    form.on('submit(formAddBean)', function (data) {
    	 	        if (winui.verifyForm(data.elem)) {
    	 	        	var params = {
    	        			name: $("#name").val(),
    	        			desc: $("#desc").val(),
    	        			rowId: parent.rowId
    	 	        	};
    	 	        	AjaxPostUtil.request({url:reqBasePath + "mailGroup005", params:params, type: 'json', callback: function(json){
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
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	    
 	    // 取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});