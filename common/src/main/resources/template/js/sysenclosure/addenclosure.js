
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
	    
	    var parentId = parent.folderId;
	    
	    $("#parentFolderName").html(parent.folderName);
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	if(parentId == 1 || parentId === '1'){
 	        		parentId = "0";
 	        	}
 	        	var params = {
        			typeName: $("#typeName").val(),
        			parentId: parentId
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "sysenclosure002", params:params, type: 'json', callback: function(json){
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
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});