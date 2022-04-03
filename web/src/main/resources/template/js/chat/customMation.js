layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys032",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/chat/customMationTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(v1, options){
					if(isNull(v1)){
						return path + "assets/img/uploadPic.png";
					}else{
						return basePath + v1;
					}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		//设置性别
		 		$("input:radio[name=userSex][value=" + json.bean.userSex + "]").attr("checked", true);
		 		//初始化上传
		 		$("#userPhoto").upload({
		            "action": reqBasePath + "common003",
		            "data-num": "1",
		            "data-type": "PNG,JPG,jpeg,gif",
		            "uploadType": 6,
		            "data-value": json.bean.userPhoto,
		            //该函数为点击放大镜的回调函数，如没有该函数，则不显示放大镜
		            "function": function (_this, data) {
		                show("#userPhoto", data);
		            }
		        });
		 		
		        matchingLanguage();
		 		form.render();
				
			    form.on('submit(formAddBean)', function (data) {
			    	
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			userIdCard: $("#userIdCard").val(),
		 	        		userSex: $("input[name='userSex']:checked").val(),
		 	        		userEmail: $("#userEmail").val(),
		 	        		userQq: $("#userQq").val(),
		 	        		userPhone: $("#userPhone").val(),
		 	        		userHomePhone: $("#userHomePhone").val(),
		 	        		userSign: $("#userSign").val()
		 	        	};
		 	        	params.userPhoto = $("#userPhoto").find("input[type='hidden'][name='upload']").attr("oldurl");
		 	        	if(isNull(params.userPhoto)){
		 	        		winui.window.msg('请上传个人头像', {icon: 2,time: 2000});
		 	        		return false;
		 	        	}
		 	        	AjaxPostUtil.request({url:reqBasePath + "sys033", params:params, type: 'json', callback: function(json){
			 	   			if(json.returnCode == 0){
			 	   				winui.window.msg(systemLanguage["com.skyeye.addOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			 	   			}
			 	   		}});
			        }
			        return false;
			    });
			    
		 	}
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});