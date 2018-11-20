layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
	    
 		//初始化上传
 		$("#userPhoto").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 6,
            "function": function (_this, data) {
                show("#userPhoto", data);
            }
        });
 		
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	//表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		userCode: $("#userCode").val(),
 	        		userName: $("#userName").val(),
 	        		password: $("#password").val(),
 	        		userIdCard: $("#userIdCard").val(),
 	        		userSex: $("input[name='userSex']:checked").val(),
 	        	};
 	        	params.userPhoto = $("#userPhoto").find("input[type='hidden'][name='upload']").attr("oldurl");

 	        	AjaxPostUtil.request({url:reqBasePath + "sysAdd005", params:params, type:'json', callback:function(json){
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