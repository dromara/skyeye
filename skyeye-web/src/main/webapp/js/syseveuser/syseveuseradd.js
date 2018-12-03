
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
 		
 		form.verify({
 			password : function(value, item){
	            if(value.length < 6){
	                return "密码长度不能小于6位";
	            }
	        },
	        confirmPwd : function(value, item){
	            if($("#password").val() != value){
	                return "两次输入密码不一致，请重新输入！";
	            }
	        }
	    });
 		
 		//初始化公司
 		dtree.render({
 			elem: "#demoTree1",  //绑定元素
 			url: reqBasePath + 'companymation007', //异步接口
 			dataStyle: 'layuiStyle',
 			done: function(){
 				if($("#demoTree1 li").length > 0){
 					$("#demoTree1 li").eq(0).children('div').click();
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
 						$("#demoTree2 li").eq(0).children('div').click();
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
 						$("#demoTree3 li").eq(0).children('div').click();
 					}
 				}
 			});
 		});
 		
 		dtree.on("node('demoTree3')" ,function(param){
 			jobId = param.nodeId;
 		});
 		
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	//表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	if(isNull(jobId)){
 	        		top.winui.window.msg('请选择职位信息', {icon: 2,time: 2000});
 	        		return false;
 	        	}
 	        	var params = {
 	        		userCode: $("#userCode").val(),
 	        		userName: $("#userName").val(),
 	        		password: $("#password").val(),
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