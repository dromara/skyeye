
var layedit;

var userList = new Array();//选择用户返回的集合或者进行回显的集合

//计划类型
var nowCheckType = "";

//计划时间段
var timeSolt = "";

// 编辑个人计划
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form,
	    	laydate = layui.laydate;
	    
	    layedit = layui.layedit;
	    
	    var myDate = new Date();  //获取当前时间
	    laydate.render({
			elem: '#notifyTime',
			type: 'datetime',
			min: myDate.toLocaleString(),
			done: function(value, date, endDate){
			}
		});
	    
	    AjaxPostUtil.request({url:reqBasePath + "sysworkplan007", params:{planId: parent.rowId}, type: 'json', callback: function(json){
   			if (json.returnCode == 0) {
   				nowCheckType = json.bean.planCycle;
   			    timeSolt = json.bean.startTime + '~' + json.bean.endTime;
   			    $("#title").val(json.bean.title);
   			    $("#content").val(json.bean.content);
   			    //计划周期名称展示
   			    $("#nowCheckTypeBox").html(getNowCheckTypeName(nowCheckType));
   			    //计划时间段展示
   			    $("#timeSlotBox").html(timeSolt);
   			    
   			    //将计划分配给...默认自己
   			    var carryPeopleName = [];
   			    if(json.bean.assignmentType === '2' || json.bean.assignmentType == 2){//他人执行
   			    	$("input:radio[name=assignmentType][value=2]").attr("checked", true);
   			    	$("#carryPeopleBox").removeClass("layui-hide");
   			    	carryPeopleName.push(json.bean.executors[0].userName);
   			    	userList.push({
   			    		id: json.bean.executors[0].userId,
   			    		name: json.bean.executors[0].userName,
   			    		email: json.bean.executors[0].email
   			    	});
   			    }
   			    //计划指定他人
   		        $('#carryPeople').tagEditor({
   		            initialTags: carryPeopleName,
   		            placeholder: '请选择指定人员',
					editorTag: false,
   		            beforeTagDelete: function(field, editor, tags, val) {
						userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
   		            }
   		        });
   		        
   		        //邮件通知...默认否
   		        if(json.bean.whetherMail === '1' || json.bean.whetherMail == 1){//是
   		        	$("#whetherMail").attr("checked", true);
   		        	$("#whetherMail").val("true");
   		        }
   		        
   		        //系统消息通知...默认是
   		        if(json.bean.whetherNotice === '2' || json.bean.whetherNotice == 2){//否
   		        	$("#whetherNotice").removeAttr("checked");
   		        	$("#whetherNotice").val("false");
   		        }
   		        
   		        //定时通知...默认否
   		        if(json.bean.whetherTime === '1' || json.bean.whetherTime == 1){//是
   		        	$("#whetherTime").attr("checked", true);
   		        	$("#whetherTime").val("true");
   		        	$("#notifyTimeBox").removeClass("layui-hide");
   		        	$("#notifyTime").val(json.bean.notifyTime);
   		        }

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosures});

   			    layedit.set({
   			    	uploadImage: {
   			    		url: reqBasePath + "common003", //接口url
   		    			type: 'post', //默认post
   		    			data: {
   		    				type: '13'
   		    			}
   			    	}
   			    });
   			    
   			    var layEditParams = {
   			    	tool: [
   		    	       'html'
   		    	       ,'strong' //加粗
   		    	       ,'italic' //斜体
   		    	       ,'underline' //下划线
   		    	       ,'del' //删除线
   		    	       ,'addhr'
   		    	       ,'|'
   		    	       ,'removeformat'
   		    	       ,'fontFomatt'
   		    	       ,'fontfamily'
   		    	       ,'fontSize'
   		    	       ,'colorpicker'
   		    	       ,'fontBackColor'
   		    	       ,'face' //表情
   		    	       ,'|' //分割线
   		    	       ,'left' //左对齐
   		    	       ,'center' //居中对齐
   		    	       ,'right' //右对齐
   		    	       ,'link' //超链接
   		    	       ,'unlink' //清除链接
   		    	       ,'code'
   		    	       ,'image' //插入图片
   		    	       ,'attachment'
   		    	       ,'table'
   		    	       ,'|'
   		    	       ,'fullScreen'
   		    	       ,'preview'
   		    	       ,'|'
   		    	       ,'help'
   		    	     ],
   		    	     uploadFiles: {
   		    	 		url: reqBasePath + "common003",
   		    	 		accept: 'file',
   		    	 		acceptMime: 'file/*',
   		    	 		size: '20480',
   		    	 		data: {
   		    				type: '13'
   		    			},
   		    	 		autoInsert: true, //自动插入编辑器设置
   		    	 		done: function(data) {
   		    	 		}
   		    	 	},
   		    	 	height: '500'
   			    };
   			    //富文本编辑器-内容
   			    var content = layedit.build('content', layEditParams);
   			    matchingLanguage();
   				form.render();
   				
   				form.on('radio(assignmentType)', function (data) {
   					var val = data.value;
   					if(val == '1'){//自己
   						$("#carryPeopleBox").addClass("layui-hide");
   					}else if(val == '2'){//他人
   						$("#carryPeopleBox").removeClass("layui-hide");
   					} else {
   						winui.window.msg('状态值错误', {icon: 2, time: 2000});
   					}
   				});
   				
   				//是否邮件通知
   		 		form.on('switch(whetherMailFilter)', function (data) {
   		 			//同步开关值
   		 			$(data.elem).val(data.elem.checked);
   		 		});
   		 		
   		 		//是否系统消息通知
   		 		form.on('switch(whetherNoticeFilter)', function (data) {
   		 			//同步开关值
   		 			$(data.elem).val(data.elem.checked);
   		 		});
   		 		
   		 		//是否定时通知
   		 		form.on('switch(whetherTimeFilter)', function (data) {
   		 			//同步开关值
   		 			$(data.elem).val(data.elem.checked);
   		 			if(data.elem.checked){
   		 				$("#notifyTimeBox").removeClass("layui-hide");
   		 			} else {
   		 				$("#notifyTimeBox").addClass("layui-hide");
   		 			}
   		 		});
   				
   			    form.on('submit(formEditBean)', function (data) {
   			        if (winui.verifyForm(data.elem)) {
   			        	if(isNull(encodeURIComponent(layedit.getContent(content)))){
   			        		winui.window.msg('请填写内容', {icon: 2, time: 2000});
   			        		return false;
   			        	}
   			        	var params = {
   		        			title: $("#title").val(),
   		        			content: encodeURIComponent(layedit.getContent(content)),
   		        			assignmentType: data.field.assignmentType,
   		        			carryPeople: "",
   		        			planId: parent.rowId,
							planEnclosure: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
   			        	};
   			        	
   			        	//他人执行
						if (params.assignmentType === '2') {
							//指定人员
							if (userList.length == 0 || isNull($('#carryPeople').tagEditor('getTags')[0].tags)) {
								winui.window.msg('请选择指定人员', {icon: 2, time: 2000});
								return false;
							} else {
								params.carryPeople = userList[0].id;
							}
						}
   			        	
   			        	//是否邮件通知
   			        	if($("#whetherMail").val() == 'true'){
   		 	        		params.whetherMail = '1';
   		 	        	} else {
   		 	        		params.whetherMail = '2';
   		 	        	}
   			        	
   			        	//是否内部消息通知
   			        	if($("#whetherNotice").val() == 'true'){
   		 	        		params.whetherNotice = '1';
   		 	        	} else {
   		 	        		params.whetherNotice = '2';
   		 	        	}
   			        	
   			        	//是否定时通知
   			        	if($("#whetherTime").val() == 'true'){
   		 	        		params.whetherTime = '1';
   		 	        	} else {
   		 	        		params.whetherTime = '2';
   		 	        	}
   			        	if(params.whetherTime === '1'){
   			        		if(isNull($("#notifyTime").val())){
   			        			winui.window.msg('请选择通知时间', {icon: 2, time: 2000});
   			        			return false;
   			        		} else {
   			        			params.notifyTime = $("#notifyTime").val();
   			        		}
   			        	} else {
   			        		params.notifyTime = null;
   			        	}
   			        	
   			        	AjaxPostUtil.request({url:reqBasePath + "sysworkplan008", params:params, type: 'json', callback: function(json){
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
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	    
        //计划指定他人-人员选择
        $("body").on("click", "#carryPeopleSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('carryPeople', userReturnList));
			});
        });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});