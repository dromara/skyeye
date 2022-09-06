var layedit;

var userList = new Array();//选择用户返回的集合或者进行回显的集合

// 我的周报
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
		    layedit = layui.layedit;
	    var reg = new RegExp("<br>", "g");

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
    	 	}
	    };

	    showGrid({
		 	id: "showForm",
		 	url: sysMainMation.jobdiaayBasePath + "diary018",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/jobdiaryMySend/jobdiaryMySendWeekEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 		hdb.registerHelper("compare1", function(v1, options){
					return v1.replace(reg, "\n");
				});
		 	},
		 	ajaxSendAfter:function (json) {
		 		var userNames = "";
                userList = json.bean.userInfo;
                $.each(userList, function (i, item) {
                	userNames += item.name + ',';
                });
                //人员选择
                $('#userName').tagEditor({
                	initialTags: userNames.split(','),
                	placeholder: '请选择接收人',
					editorTag: false,
                	beforeTagDelete: function(field, editor, tags, val) {
						userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
                	}
                });
		 		var weekCompletedContent = layedit.build('weekCompletedContent', layEditParams);
			    var weekWorkSummaryContent = layedit.build('weekWorkSummaryContent', layEditParams);
			    var weekNextWorkPlanContent = layedit.build('weekNextWorkPlanContent', layEditParams);
			    var weekCoordinaJobContent = layedit.build('weekCoordinaJobContent', layEditParams);
				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});
			    //人员选择
                $("body").on("click", "#userNameSelPeople", function (e) {
					systemCommonUtil.userReturnList = [].concat(userList);
					systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
					systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
					systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
					systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
						// 重置数据
						userList = [].concat(systemCommonUtil.tagEditorResetData('userName', userReturnList));
					});
                });
                
                matchingLanguage();
			    form.render();
			    form.on('switch(weekCompletedImagetext)', function (data) {
		 			//是否图文模式
		 			$(data.elem).val(data.elem.checked);
		 			if (data.elem.value === 'true' || data.elem.value === true){
		 				$("#weekCompletedText").parent().hide();
			    		$("#weekCompletedContent").parent().show();
			    		layedit.setContent(weekCompletedContent, $("#weekCompletedText").val().replace(/\n|\r\n/g, "<br>"), false);
		 			} else {
		 				$("#weekCompletedText").parent().show();
			    		$("#weekCompletedContent").parent().hide();
			    		$("#weekCompletedText").val(layedit.getText(weekCompletedContent).replace(reg, "\n").replace(/<[^>]+>/g,""));
		 			}
		 		});
			    form.on('switch(weekWorkSummaryImagetext)', function (data) {
		 			//是否图文模式
		 			$(data.elem).val(data.elem.checked);
		 			if (data.elem.value === 'true' || data.elem.value === true){
		 				$("#weekWorkSummaryText").parent().hide();
			    		$("#weekWorkSummaryContent").parent().show();
			    		layedit.setContent(weekWorkSummaryContent, $("#weekWorkSummaryText").val().replace(/\n|\r\n/g, "<br>"), false);
		 			} else {
		 				$("#weekWorkSummaryText").parent().show();
			    		$("#weekWorkSummaryContent").parent().hide();
			    		$("#weekWorkSummaryText").val(layedit.getText(weekWorkSummaryContent).replace(reg, "\n").replace(/<[^>]+>/g,""));
		 			}
		 		});
			    form.on('switch(weekNextWorkPlanImagetext)', function (data) {
		 			//是否图文模式
		 			$(data.elem).val(data.elem.checked);
		 			if (data.elem.value === 'true' || data.elem.value === true){
		 				$("#weekNextWorkPlanText").parent().hide();
			    		$("#weekNextWorkPlanContent").parent().show();
			    		layedit.setContent(weekNextWorkPlanContent, $("#weekNextWorkPlanText").val().replace(/\n|\r\n/g, "<br>"), false);
		 			} else {
		 				$("#weekNextWorkPlanText").parent().show();
			    		$("#weekNextWorkPlanContent").parent().hide();
			    		$("#weekNextWorkPlanText").val(layedit.getText(weekNextWorkPlanContent).replace(reg, "\n").replace(/<[^>]+>/g,""));
		 			}
		 		});
			    form.on('switch(weekCoordinaJobImagetext)', function (data) {
		 			//是否图文模式
		 			$(data.elem).val(data.elem.checked);
		 			if (data.elem.value === 'true' || data.elem.value === true){
		 				$("#weekCoordinaJobText").parent().hide();
			    		$("#weekCoordinaJobContent").parent().show();
			    		layedit.setContent(weekCoordinaJobContent, $("#weekCoordinaJobText").val().replace(/\n|\r\n/g, "<br>"), false);
		 			} else {
		 				$("#weekCoordinaJobText").parent().show();
			    		$("#weekCoordinaJobContent").parent().hide();
			    		$("#weekCoordinaJobText").val(layedit.getText(weekCoordinaJobContent).replace(reg, "\n").replace(/<[^>]+>/g,""));
		 			}
		 		});
			    
			    form.on('submit(weekSubmit)', function (data) {
			        if (winui.verifyForm(data.elem)) {
		        		var params = {
		        			jobRemark: encodeURIComponent($("#weekJobRemark").val()),
		        			jobTitle: $("#jobWeekTitle").val(),
		        			id: json.bean.id,
							weekenclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
							userId: systemCommonUtil.tagEditorGetAllData('userName', userList)
		        		};
						if (isNull(params.userId)) {
							winui.window.msg('请选择收件人', {icon: 2, time: 2000});
							return false;
						}
		        		if (data.field.weekCompleted === 'true'){
		        			if(isNull(layedit.getContent(weekCompletedContent))){
		        				winui.window.msg('请填写本周已完成工作', {icon: 2, time: 2000});
		        				return false;
		        			} else {
		        				params.completedJob = encodeURIComponent(layedit.getContent(weekCompletedContent));
		        			}
		        		} else {
		        			if(isNull($("#weekCompletedText").val())) {
		        				winui.window.msg('请填写本周已完成工作', {icon: 2, time: 2000});
		        				return false;
		        			} else {
		        				params.completedJob = encodeURIComponent($("#weekCompletedText").val().replace(/\n|\r\n/g, "<br>"));
		        			}
		        		}
		        		if (data.field.weekCoordinaJob === 'true'){
		        			params.coordinaJob = encodeURIComponent(layedit.getContent(weekCoordinaJobContent));
		        		} else {
		        			params.coordinaJob = encodeURIComponent($("#weekCoordinaJobText").val().replace(/\n|\r\n/g, "<br>"));
		        		}
		        		if (data.field.weekNextWorkPlan === 'true'){
		        			params.nextWorkPlan = encodeURIComponent(layedit.getContent(weekNextWorkPlanContent));
		        		} else {
		        			params.nextWorkPlan = encodeURIComponent($("#weekNextWorkPlanText").val().replace(/\n|\r\n/g, "<br>"));
		        		}
		        		if (data.field.weekWorkSummary === 'true'){
		        			params.thisWorkSummary = encodeURIComponent(layedit.getContent(weekWorkSummaryContent));
		        		} else {
		        			params.thisWorkSummary = encodeURIComponent($("#weekWorkSummaryText").val().replace(/\n|\r\n/g, "<br>"));
		        		}
		        		
		        		AjaxPostUtil.request({url: sysMainMation.jobdiaayBasePath + "diary019", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
                        }});
			        }
			        return false;
		 	    });
		 	}
		});

	    // 取消
	    $("body").on("click", "#weekCancle", function() {
	    	parent.layer.close(index);
	    });
	});
});