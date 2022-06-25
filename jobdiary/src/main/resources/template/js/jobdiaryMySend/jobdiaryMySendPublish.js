
var layedit;

var userList = new Array();//选择用户返回的集合或者进行回显的集合

// 发送日志
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;
	    layedit = layui.layedit;
	    
	    //日报标题
	    var today = new Date();
	    var submitTime = today.getFullYear() + '年' + (today.getMonth() + 1) + '月' + today.getDate() + '日日报'; 
	    $("#jobTitle").attr('value',submitTime);
	    
	    //周报标题
	    var getWeekOfYear = function(){
	    	var today = new Date();
	    	var firstDay = new Date(today.getFullYear(), 0, 1);
	    	var dayOfWeek = firstDay.getDay(); 
	    	var spendDay = 1;
	    	if (dayOfWeek != 0) {
	    		spendDay = 7 - dayOfWeek + 1;
	    	}
	    	firstDay = new Date(today.getFullYear(),0, 1 + spendDay);
	    	var d = Math.ceil((today.valueOf() - firstDay.valueOf()) / 86400000);
	    	var result = Math.ceil(d/7);
	    	return result + 1;
    	};
	    var submitWeekTime = today.getFullYear() + '年第' + getWeekOfYear() + '周周报'; 
	    $("#jobWeekTitle").attr('value', submitWeekTime);
	    
	    //月报标题
	    var submitMonthTime = today.getFullYear() + '年' + (today.getMonth() + 1) + '月份月报'; 
	    $("#jobMonthTitle").attr('value', submitMonthTime);
	    
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
	    var completedContent = layedit.build('completedcontent', layEditParams);
	    var incompleteContent = layedit.build('incompletecontent', layEditParams);
	    var coordinaContent = layedit.build('coordinacontent', layEditParams);
	    var weekCompletedContent = layedit.build('weekCompletedContent', layEditParams);
	    var weekWorkSummaryContent = layedit.build('weekWorkSummaryContent', layEditParams);
	    var weekNextWorkPlanContent = layedit.build('weekNextWorkPlanContent', layEditParams);
	    var weekCoordinaJobContent = layedit.build('weekCoordinaJobContent', layEditParams);
	    var monthCompletedContent = layedit.build('monthCompletedContent', layEditParams);
	    var monthWorkSummaryContent = layedit.build('monthWorkSummaryContent', layEditParams);
	    var monthNextWorkPlanContent = layedit.build('monthNextWorkPlanContent', layEditParams);
	    var monthCoordinaJobContent = layedit.build('monthCoordinaJobContent', layEditParams);
	    
	    matchingLanguage();
	    form.render();
	    
	    form.on('switch(todycompletedImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#completedtext").parent().hide();
	    		$("#completedcontent").parent().show();
	    		layedit.setContent(completedContent, $("#completedtext").val(), false);
 			} else {
 				$("#completedtext").parent().show();
	    		$("#completedcontent").parent().hide();
	    		$("#completedtext").val(layedit.getText(completedContent));
 			}
 		});
	    form.on('switch(todyincompleteImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#incompletetext").parent().hide();
	    		$("#incompletecontent").parent().show();
	    		layedit.setContent(incompleteContent,$("#incompletetext").val(),false);
 			} else {
 				$("#incompletetext").parent().show();
	    		$("#incompletecontent").parent().hide();
	    		$("#incompletetext").val(layedit.getText(incompleteContent));
 			}
 		});
	    form.on('switch(todycoordinaImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#coordinatext").parent().hide();
	    		$("#coordinacontent").parent().show();
	    		layedit.setContent(coordinaContent,$("#coordinatext").val(),false);
 			} else {
 				$("#coordinatext").parent().show();
	    		$("#coordinacontent").parent().hide();
	    		$("#coordinatext").val(layedit.getText(coordinaContent));
 			}
 		});

		skyeyeEnclosure.init('enclosureUpload,weekenclosureUpload,monthenclosureUpload');
	    form.on('submit(daysubmit)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			jobRemark: encodeURIComponent($("#jobRemark").val()),
        			jobTitle: $("#jobTitle").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
        		};
        		if(userList.length == 0 || isNull($('#userName').tagEditor('getTags')[0].tags)){
                    winui.window.msg('请选择收件人', {icon: 2, time: 2000});
                    return false;
                } else {
					var userInfo = "";
                    $.each(userList, function (i, item) {
                        userInfo += item.id + ',';
                    });
                    params.userInfo = userInfo;
                }
        		if(data.field.todycompleted === 'true'){
        			if(isNull(layedit.getContent(completedContent))){
        				winui.window.msg('请填写今日已完成工作', {icon: 2, time: 2000});
        				return false;
        			} else {
        				params.completedJob = encodeURIComponent(layedit.getContent(completedContent));
        			}
        		} else {
        			if(isNull($("#completedtext").val())){
        				winui.window.msg('请填写今日已完成工作', {icon: 2, time: 2000});
        				return false;
        			} else {
        				params.completedJob = encodeURIComponent($("#completedtext").val());
        			}
        		}
        		if(data.field.todyincomplete === 'true'){
        			params.incompleteJob = encodeURIComponent(layedit.getContent(incompleteContent));
        		} else {
        			params.incompleteJob = encodeURIComponent($("#incompletetext").val());
        		}
        		if(data.field.todycoordina === 'true'){
        			params.coordinaJob = encodeURIComponent(layedit.getContent(coordinaContent));
        		} else {
        			params.coordinaJob = encodeURIComponent($("#coordinatext").val());
        		}
        		
        		AjaxPostUtil.request({url:reqBasePath + "diary002", params:params, type: 'json', callback: function(json){
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
	    
	    //取消日报发送
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });

	    form.on('switch(weekCompletedImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#weekCompletedText").parent().hide();
	    		$("#weekCompletedContent").parent().show();
	    		layedit.setContent(weekCompletedContent, $("#weekCompletedText").val(), false);
 			} else {
 				$("#weekCompletedText").parent().show();
	    		$("#weekCompletedContent").parent().hide();
	    		$("#weekCompletedText").val(layedit.getText(weekCompletedContent));
 			}
 		});
	    form.on('switch(weekWorkSummaryImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#weekWorkSummaryText").parent().hide();
	    		$("#weekWorkSummaryContent").parent().show();
	    		layedit.setContent(weekWorkSummaryContent, $("#weekWorkSummaryText").val(), false);
 			} else {
 				$("#weekWorkSummaryText").parent().show();
	    		$("#weekWorkSummaryContent").parent().hide();
	    		$("#weekWorkSummaryText").val(layedit.getText(weekWorkSummaryContent));
 			}
 		});
	    form.on('switch(weekNextWorkPlanImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#weekNextWorkPlanText").parent().hide();
	    		$("#weekNextWorkPlanContent").parent().show();
	    		layedit.setContent(weekNextWorkPlanContent, $("#weekNextWorkPlanText").val(), false);
 			} else {
 				$("#weekNextWorkPlanText").parent().show();
	    		$("#weekNextWorkPlanContent").parent().hide();
	    		$("#weekNextWorkPlanText").val(layedit.getText(weekNextWorkPlanContent));
 			}
 		});
	    form.on('switch(weekCoordinaJobImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#weekCoordinaJobText").parent().hide();
	    		$("#weekCoordinaJobContent").parent().show();
	    		layedit.setContent(weekCoordinaJobContent, $("#weekCoordinaJobText").val(), false);
 			} else {
 				$("#weekCoordinaJobText").parent().show();
	    		$("#weekCoordinaJobContent").parent().hide();
	    		$("#weekCoordinaJobText").val(layedit.getText(weekCoordinaJobContent));
 			}
 		});
	    
	    form.on('submit(weekSubmit)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			jobRemark: encodeURIComponent($("#weekJobRemark").val()),
        			jobTitle: $("#jobWeekTitle").val(),
					weekenclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('weekenclosureUpload')
        		};
        		if(userList.length == 0 || isNull($('#weekUserName').tagEditor('getTags')[0].tags)){
                    winui.window.msg('请选择收件人', {icon: 2, time: 2000});
                    return false;
                } else {
					var weekUserInfo = "";
                    $.each(userList, function (i, item) {
                        weekUserInfo += item.id + ',';
                    });
                    params.weekUserInfo = weekUserInfo;
                }
        		if(data.field.weekCompleted === 'true'){
        			if(isNull(layedit.getContent(weekCompletedContent))){
        				winui.window.msg('请填写本周已完成工作', {icon: 2, time: 2000});
        				return false;
        			} else {
        				params.completedJob = encodeURIComponent(layedit.getContent(weekCompletedContent));
        			}
        		} else {
        			if(isNull($("#weekCompletedText").val())){
        				winui.window.msg('请填写本周已完成工作', {icon: 2, time: 2000});
        				return false;
        			} else {
        				params.completedJob = encodeURIComponent($("#weekCompletedText").val());
        			}
        		}
        		if(data.field.weekCoordinaJob === 'true'){
        			params.coordinaJob = encodeURIComponent(layedit.getContent(weekCoordinaJobContent));
        		} else {
        			params.coordinaJob = encodeURIComponent($("#weekCoordinaJobText").val());
        		}
        		if(data.field.weekNextWorkPlan === 'true'){
        			params.nextWorkPlan = encodeURIComponent(layedit.getContent(weekNextWorkPlanContent));
        		} else {
        			params.nextWorkPlan = encodeURIComponent($("#weekNextWorkPlanText").val());
        		}
        		if(data.field.weekWorkSummary === 'true'){
        			params.thisWorkSummary = encodeURIComponent(layedit.getContent(weekWorkSummaryContent));
        		} else {
        			params.thisWorkSummary = encodeURIComponent($("#weekWorkSummaryText").val());
        		}
        		
        		AjaxPostUtil.request({url:reqBasePath + "diary009", params:params, type: 'json', callback: function(json){
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
	    
	    //取消周报发送
	    $("body").on("click", "#weekCancle", function(){
	    	parent.layer.close(index);
	    });
	    
	    form.on('switch(monthCompletedImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#monthCompletedText").parent().hide();
	    		$("#monthCompletedContent").parent().show();
	    		layedit.setContent(monthCompletedContent, $("#monthCompletedText").val(), false);
 			} else {
 				$("#monthCompletedText").parent().show();
	    		$("#monthCompletedContent").parent().hide();
	    		$("#monthCompletedText").val(layedit.getText(monthCompletedContent));
 			}
 		});
	    form.on('switch(monthWorkSummaryImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#monthWorkSummaryText").parent().hide();
	    		$("#monthWorkSummaryContent").parent().show();
	    		layedit.setContent(monthWorkSummaryContent, $("#monthWorkSummaryText").val(), false);
 			} else {
 				$("#monthWorkSummaryText").parent().show();
	    		$("#monthWorkSummaryContent").parent().hide();
	    		$("#monthWorkSummaryText").val(layedit.getText(monthWorkSummaryContent));
 			}
 		});
	    form.on('switch(monthNextWorkPlanImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#monthNextWorkPlanText").parent().hide();
	    		$("#monthNextWorkPlanContent").parent().show();
	    		layedit.setContent(monthNextWorkPlanContent, $("#monthNextWorkPlanText").val(), false);
 			} else {
 				$("#monthNextWorkPlanText").parent().show();
	    		$("#monthNextWorkPlanContent").parent().hide();
	    		$("#monthNextWorkPlanText").val(layedit.getText(monthNextWorkPlanContent));
 			}
 		});
	    form.on('switch(monthCoordinaJobImagetext)', function (data) {
 			//是否图文模式
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.value === 'true' || data.elem.value === true){
 				$("#monthCoordinaJobText").parent().hide();
	    		$("#monthCoordinaJobContent").parent().show();
	    		layedit.setContent(monthCoordinaJobContent, $("#monthCoordinaJobText").val(), false);
 			} else {
 				$("#monthCoordinaJobText").parent().show();
	    		$("#monthCoordinaJobContent").parent().hide();
	    		$("#monthCoordinaJobText").val(layedit.getText(monthCoordinaJobContent));
 			}
 		});
	    form.on('submit(monthSubmit)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			jobRemark: encodeURIComponent($("#monthJobRemark").val()),
        			jobTitle: $("#jobMonthTitle").val(),
					monthenclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('monthenclosureUpload')
        		};
        		if(userList.length == 0 || isNull($('#monthUserName').tagEditor('getTags')[0].tags)){
                    winui.window.msg('请选择收件人', {icon: 2, time: 2000});
                    return false;
                } else {
					var monthUserInfo = "";
                    $.each(userList, function (i, item) {
                        monthUserInfo += item.id + ',';
                    });
                    params.monthUserInfo = monthUserInfo;
                }
        		if(data.field.monthCompleted === 'true'){
        			if(isNull(layedit.getContent(monthCompletedContent))){
        				winui.window.msg('请填写本月已完成工作', {icon: 2, time: 2000});
        				return false;
        			} else {
        				params.completedJob = encodeURIComponent(layedit.getContent(monthCompletedContent));
        			}
        		} else {
        			if(isNull($("#monthCompletedText").val())){
        				winui.window.msg('请填写本月已完成工作', {icon: 2, time: 2000});
        				return false;
        			} else {
        				params.completedJob = encodeURIComponent($("#monthCompletedText").val());
        			}
        		}
        		if(data.field.monthCoordinaJob === 'true'){
        			params.coordinaJob = encodeURIComponent(layedit.getContent(monthCoordinaJobContent));
        		} else {
        			params.coordinaJob = encodeURIComponent($("#monthCoordinaJobText").val());
        		}
        		if(data.field.monthNextWorkPlan === 'true'){
        			params.nextWorkPlan = encodeURIComponent(layedit.getContent(monthNextWorkPlanContent));
        		} else {
        			params.nextWorkPlan = encodeURIComponent($("#monthNextWorkPlanText").val());
        		}
        		if(data.field.monthWorkSummary === 'true'){
        			params.thisWorkSummary = encodeURIComponent(layedit.getContent(monthWorkSummaryContent));
        		} else {
        			params.thisWorkSummary = encodeURIComponent($("#monthWorkSummaryText").val());
        		}
        		
        		AjaxPostUtil.request({url:reqBasePath + "diary012", params:params, type: 'json', callback: function(json){
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
	    
	    //取消月报发送
	    $("body").on("click", "#monthCancle", function(){
	    	parent.layer.close(index);
	    });
	    
        //日报接收人
        $('#userName').tagEditor({
            initialTags: [],
            placeholder: '请选择收件人',
			editorTag: false,
            beforeTagDelete: function(field, editor, tags, val) {
				userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
            }
        });
        //日报人员选择
        $("body").on("click", "#userNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('userName', userReturnList));
			});
        });
        //周报接收人
        $('#weekUserName').tagEditor({
            initialTags: [],
            placeholder: '请选择收件人',
			editorTag: false,
            beforeTagDelete: function(field, editor, tags, val) {
				userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
            }
        });
        //周报人员选择
        $("body").on("click", "#weekUserNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('weekUserName', userReturnList));
			});
        });
        //月报接收人
        $('#monthUserName').tagEditor({
            initialTags: [],
            placeholder: '请选择收件人',
			editorTag: false,
            beforeTagDelete: function(field, editor, tags, val) {
				userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
            }
        });
        //月报人员选择
        $("body").on("click", "#monthUserNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('monthUserName', userReturnList));
			});
        });

	});
});