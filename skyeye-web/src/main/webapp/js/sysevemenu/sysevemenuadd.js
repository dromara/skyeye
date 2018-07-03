layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
 		form.render();
 	    form.on('submit(formAddMenu)', function (data) {
 	    	//表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	//执行上传操作
 	        	$("#showForm").find('.fileUploadContent').each(function(i, dom) {
 	        		var id = $(dom).attr('id');
 	        		if(id != undefined && id == 'userPhoto') {
 	        			var opt = uploadTools.getOpt(id);
 	        			if(uploadTools.getFileNumber(opt) > 0) {
 	        				uploadTools.uploadFile(opt);
 	        			}
 	        		}
 	        	});
 	        	var params = {
 	        		rowId: parent.rowId,
 	        		userName: $("#userName").val(),
 	        		userIdCard: $("#userIdCard").val(),
 	        		userSex: $("input[name='userSex']:checked").val(),
 	        		userPhoto: "11"
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "sys005", params:params, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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