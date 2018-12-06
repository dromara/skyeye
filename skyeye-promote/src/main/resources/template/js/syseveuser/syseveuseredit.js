
var companyId = "";
var departmentId = "";
var jobId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'fileUpload', 'dtree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    dtree = layui.dtree;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/syseveuser/syseveusereditTemplate.tpl'),
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
		 		
		 		//初始化公司
		 		dtree.render({
		 			elem: "#demoTree1",  //绑定元素
		 			url: reqBasePath + 'companymation007', //异步接口
		 			dataStyle: 'layuiStyle',
		 			done: function(){
		 				if($("#demoTree1 li").length > 0){
		 					for(var i = 0; i < $("#demoTree1 li").length; i++){
	 							if($("#demoTree1 li").eq(i).attr("data-id") == json.bean.companyId){
	 								$("#demoTree1 li").eq(i).children('div').click();
	 								return;
	 							}
	 						}
		 				}
		 			}
		 		});
		 		
		 		dtree.on("node('demoTree1')" ,function(param){
		 			companyId = param.nodeId;
		 			//初始化部门
		 			dtree.render({
		 				elem: "#demoTree2",  //绑定元素
		 				url: reqBasePath + 'companydepartment006?companyId=' + companyId, //异步接口
		 				dataStyle: 'layuiStyle',
		 				done: function(){
		 					departmentId = "";
		 					if($("#demoTree2 li").length > 0){
		 						for(var i = 0; i < $("#demoTree2 li").length; i++){
		 							if($("#demoTree2 li").eq(i).attr("data-id") == json.bean.departmentId){
		 								$("#demoTree2 li").eq(i).children('div').click();
		 								return;
		 							}
		 						}
		 					}else{
		 						jobId = "";
		 						//初始化职位
		 			 			dtree.render({
		 			 				elem: "#demoTree3",  //绑定元素
		 			 				url: reqBasePath + 'companyjob006?departmentId=0', //异步接口
		 			 				dataStyle: 'layuiStyle',
		 			 				done: function(){
		 			 				}
		 			 			});
		 					}
		 				}
		 			});
		 		});
		 		
		 		dtree.on("node('demoTree2')" ,function(param){
		 			departmentId = param.nodeId;
		 			//初始化职位
		 			dtree.render({
		 				elem: "#demoTree3",  //绑定元素
		 				url: reqBasePath + 'companyjob006?departmentId=' + departmentId, //异步接口
		 				dataStyle: 'layuiStyle',
		 				done: function(){
		 					jobId = "";
		 					if($("#demoTree3 li").length > 0){
		 						for(var i = 0; i < $("#demoTree3 li").length; i++){
		 							if($("#demoTree3 li").eq(i).attr("data-id") == json.bean.jobId){
		 								$("#demoTree3 li").eq(i).children('div').click();
		 								return;
		 							}
		 						}
		 					}
		 				}
		 			});
		 		});
		 		
		 		dtree.on("node('demoTree3')" ,function(param){
		 			jobId = param.nodeId;
		 		});
		 		
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	    	//表单验证
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		userName: $("#userName").val(),
		 	        		userIdCard: $("#userIdCard").val(),
		 	        		userSex: $("input[name='userSex']:checked").val(),
		 	        		companyId: companyId,
		 	        		departmentId: departmentId,
		 	        		jobId: jobId,
		 	        	};
		 	        	params.userPhoto = $("#userPhoto").find("input[type='hidden'][name='upload']").attr("oldurl");
		 	        	if(isNull(params.userPhoto)){
		 	        		top.winui.window.msg('请上传头像', {icon: 2,time: 2000});
		 	        		return false;
		 	        	}

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
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});