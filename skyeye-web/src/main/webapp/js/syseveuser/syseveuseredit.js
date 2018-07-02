layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
	    var userPhoto = "";
	    var picArray = new Array();//已加载的上传文件的ID
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/syseveuser/syseveusereditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(v1,options){
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
		 		$("#showForm").find('.fileUploadContent').each(function(i, dom) {
		 			if($.inArray($(dom).attr("id"), picArray) < 0){
		        		  picArray.push($(dom).attr("id"));
			 			var fileType = "*";
			 			if($(dom).attr("id") == "userPhoto")//图片上传
			 				fileType = filePicType;
			 			var vali = $(dom).attr('validate');
			 			if(vali)
			 				vali = eval('(' + vali + ')').vali;
			 			var maxNum = vali.maxnum; //最大数量
			 			var minNum = vali.minnum; //最小数量
			 			var required = vali.required; //必传
			 			if(!maxNum)
			 				maxNum = 10;
			 			var msg = '';
			 			if(required)
			 				msg = '请至少上传1个文件,';
			 			if(minNum && required)
			 				msg = '请至少上传' + minNum + '个文件,';
			 			if(maxNum)
			 				msg += '至多可上传' + maxNum + '个文件';
	
			 			var fileParam = {
			 				id: '#' + $(dom).attr('id'),
			 				onUploadFun: onUploadFun,//回执函数
			 				maxFileNumber: maxNum,
			 				uploadUrl: '/common/upload',//上传路径
			 				async: false,//是否异步
			 				fileType: fileType,//文件类型
			 				msg: msg//提示消息
			 			}
			 			fileUpload(fileParam);
		 			}
		 		});
		 		form.render();
		 	    form.on('submit(formEditMenu)', function (data) {
//		 	    	console.log(data.elem);
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
		 	}
		});
	    
	    //上传回执函数
	    function onUploadFun(opt, data) {
	    	if(data.code == 1) {
	    		if(opt.uploadId == "userPhoto") {
	    			userPhoto = data.content;
	    		}
	    	}
	    }
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});