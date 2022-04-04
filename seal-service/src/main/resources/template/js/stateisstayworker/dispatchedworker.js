
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";//人员选择类型，1.多选；其他。单选

//工单接收人信息
var serviceUser = {};

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    var cooperationUser = new Array();//工单协助人集合
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice013",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/stateisstayworker/dispatchedworkerTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function(json){
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(dispatchedWorker)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		        			rowId: parent.rowId,
		        			serviceUserId: '',
		        			cooperationUserId: ''
		 	        	};
		 	        	//接收人
		 	        	params.serviceUserId = isNull(serviceUser.userId) ? "" : serviceUser.userId;
		 	        	if(isNull(params.serviceUserId)){
		                	winui.window.msg('请选择工单接收人', {icon: 2, time: 2000});
		                	return false;
		                }
		                //协助人
		 	        	if(cooperationUser.length > 0 && !isNull($('#cooperationUserId').tagEditor('getTags')[0].tags)){
		 	        		var cooperationUserId = "";
		                    $.each(cooperationUser, function (i, item) {
		                    	cooperationUserId += item.id + ',';
		                    });
		                    params.cooperationUserId = cooperationUserId;
		                }
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "sealseservice014", params: params, type: 'json', callback: function(json){
	 		 	   			if (json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			} else {
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
		//工单接收人选择
	    $("body").on("click", "#serviceUserIdSelPeople", function(e){
			_openNewWindows({
 				url: "../../tpl/serviceworker/serviceworkershowlist.html", 
 				title: "选择接收人",
 				pageId: "serviceworkershowlist",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#serviceUserId").val(serviceUser.userName);
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
 	                }
 				}});
		});
		
		//工单协助人
	    $('#cooperationUserId').tagEditor({
	        initialTags: [],
	        placeholder: '请选择工单协助人',
	        beforeTagDelete: function(field, editor, tags, val) {
	        	var inArray = -1;
		    	$.each(cooperationUser, function(i, item) {
		    		if(val === item.name) {
		    			inArray = i;
		    			return false;
		    		}
		    	});
		    	if(inArray != -1) { //如果该元素在集合中存在
		    		cooperationUser.splice(inArray, 1);
		    	}
	        }
	    });
		//工单协助人选择
		$("body").on("click", "#cooperationUserIdSelPeople", function(e){
			checkType = '1';
			userReturnList = [].concat(cooperationUser);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#cooperationUserId').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#cooperationUserId').tagEditor('removeTag', tags[i]);
						}
						cooperationUser = [].concat(userReturnList);
					    //添加新的tag
						$.each(cooperationUser, function(i, item){
							$('#cooperationUserId').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});