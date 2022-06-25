
var layedit;

var userList = new Array();//选择用户返回的集合或者进行回显的集合

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    form = layui.form,
    	laydate = layui.laydate;
	    layedit = layui.layedit;

	    initNoticeTypeId();
	    
	    //初始化一级公告类型
		function initNoticeTypeId(){
			showGrid({
			 	id: "noticeTypeId",
			 	url: reqBasePath + "noticetype011",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		//初始化二级公告类型
		function initSecondTypeId(){
			showGrid({
			 	id: "secondTypeId",
			 	url: reqBasePath + "noticetype013",
			 	params: {parentId: $("#noticeTypeId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		
		//一级公告类型监听事件
		form.on('select(noticeTypeId)', function(data){
			initSecondTypeId();
		});

		//群发类型变化事件
 		form.on('radio(sendType)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//群发所有人
	    		$("#sendTo").hide();
	    	}else if(val == '2'){//选择性群发
	    		$("#sendTo").show();
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		//定时通知变化事件
 		form.on('radio(timeSend)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//不设置定时通知
	    		$("#sendTime").hide();
	    	}else if(val == '2'){//设置定时通知
	    		$("#sendTime").show();
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		//定时通知时间选择器
 		laydate.render({
 		  elem: '#delayedTime'
 		  ,type: 'datetime'
 		});
 		
	    //公告内容富文本框
	    var content = layedit.build('content', {
	    	tool: [
    	       'strong' //加粗
    	       ,'italic' //斜体
    	       ,'underline' //下划线
    	       ,'del' //删除线
    	       ,'|' //分割线
    	       ,'left' //左对齐
    	       ,'center' //居中对齐
    	       ,'right' //右对齐
    	       ,'link' //超链接
    	       ,'unlink' //清除链接
    	       ,'face' //表情
    	       ,'code'
    	     ]
	    });
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		title: $("#title").val(),
 	        		typeId: $("#noticeTypeId").val(),
 	        		secondTypeId: $("#secondTypeId").val(),
 	        		sendType: $("input[name='sendType']:checked").val(),
 	        		timeSend: $("input[name='timeSend']:checked").val(),
 	        		whetherEmail: $("input[name='whetherEmail']:checked").val(),
 	        		content: encodeURIComponent(layedit.getContent(content))
 	        	};
 	        	if($("input[name='sendType']:checked").val() === '2'){	//选择性群发
 	        		if(userList.length == 0 && isNull($('#userName').tagEditor('getTags')[0].tags)){
 	 	        		winui.window.msg('请选择收件人', {icon: 2, time: 2000});
 	 	        		return false;
 	 	        	} else {
 	        			params.userInfo = JSON.stringify(userList);
 	        		}
 	        	} else {
 	        		params.userInfo = "";
 	        	}
 	        	if($("input[name='timeSend']:checked").val() === '2'){	//设置定时通知
 	        		if(isNull($("#delayedTime").val())){
 	        			winui.window.msg('请选择定时通知时间', {icon: 2, time: 2000});
 	 	        		return false;
 	        		} else {
 	        			params.delayedTime = $("#delayedTime").val();
 	        		}
 	        	} else {
 	        		params.delayedTime = "";
 	        	}
    			if(isNull(layedit.getContent(content))){
    				winui.window.msg('请填写公告内容', {icon: 2, time: 2000});
    				return false;
    			}
    			AjaxPostUtil.request({url:reqBasePath + "notice002", params:params, type: 'json', callback: function(json){
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
 	    
		$('#userName').tagEditor({
			initialTags: [],
			placeholder: '填写完成后直接回车即可',
			editorTag: false,
			beforeTagDelete: function(field, editor, tags, val) {
				userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
	        }
	    });
 	    
 	    //人员选择
		$("body").on("click", "#userNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "1"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('userName', userReturnList));
			});
		});
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});