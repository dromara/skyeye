
var layedit;

var userList = new Array();//选择用户返回的集合或者进行回显的集合

//计划类型
var nowCheckType = "";

// 计划时间段
var timeSolt = "";

// 新增公司计划
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
	    
		if(!isNull(parent.timeSolt)){
			// 如果父页面传递时间段
			timeSolt = parent.timeSolt;
			// 计划时间段展示
			$("#timeSlotBox").html(timeSolt);
		} else {
			$(".hand-down-time").addClass("layui-hide");
			$(".choose-time").removeClass("layui-hide");
			laydate.render({
				elem: '#executeTime',
				range: '~'
			});
		}

		if(!isNull(parent.nowCheckType)){
			nowCheckType = parent.nowCheckType;
			// 计划周期名称展示
			$("#nowCheckTypeBox").html(getNowCheckTypeName(nowCheckType));
		} else {
			$(".hand-down-check").addClass("layui-hide");
			$(".choose-check").removeClass("layui-hide");
		}
	    
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

		skyeyeEnclosure.init('enclosureUpload');
		
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	if(isNull(encodeURIComponent(layedit.getContent(content)))){
	        		winui.window.msg('请填写内容', {icon: 2, time: 2000});
	        		return false;
	        	}
	        	var params = {
        			title: $("#title").val(),
        			content: encodeURIComponent(layedit.getContent(content)),
        			assignmentType: '2',
					nowCheckType: "",
					startTime: "",
					endTime: "",
        			carryPeople: "",
					planEnclosure: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	        	};
				// 执行时间
				if(!isNull(timeSolt)){
					params.startTime = timeSolt.split('~')[0] + " 00:00:00";
					params.endTime = timeSolt.split('~')[1] + " 23:59:59";
				} else {
					if(isNull($("#executeTime").val())){
						winui.window.msg('请选择执行时间', {icon: 2, time: 2000});
						return false;
					}
					params.startTime = $("#executeTime").val().split('~')[0].trim() + " 00:00:00";
					params.endTime = $("#executeTime").val().split('~')[1].trim() + " 23:59:59";
				}

				// 计划周期
				if(!isNull(nowCheckType)){
					params.nowCheckType = nowCheckType;
				} else {
					if(isNull($("#planCycle").val())){
						winui.window.msg('请选择计划周期', {icon: 2, time: 2000});
						return false;
					}
					params.nowCheckType = $("#planCycle").val();
				}
	        	
	        	//他人执行
	        	if(params.assignmentType === '2'){
					params.carryPeople = systemCommonUtil.tagEditorGetAllData('carryPeople', userList);
	        		// 指定人员
	        		if(isNull(params.carryPeople)){
	        			winui.window.msg('请选择人员', {icon: 2, time: 2000});
	        			return false;
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
	        	
	        	AjaxPostUtil.request({url: reqBasePath + "sysworkplan004", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
	        }
	        return false;
	    });
	    
	    //计划指定他人
        $('#carryPeople').tagEditor({
            initialTags: [],
            placeholder: '请选择人员',
			editorTag: false,
            beforeTagDelete: function(field, editor, tags, val) {
				userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
            }
        });
        
        //计划指定他人-人员选择
        $("body").on("click", "#carryPeopleSelPeople", function (e) {
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('carryPeople', userReturnList));
			});
        });
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});