var workId = "";
var appealType = "1";
var appealReasonId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tableSelect'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	tableSelect = layui.tableSelect;
	    
	    var ids = "";
		tableSelect.render({
	    	elem: '#approvalId',	//定义输入框input对象
	    	checkedKey: 'id', //表格的唯一键值，非常重要，影响到选中状态 必填
	    	searchKey: 'userName',	//搜索输入框的name值 默认keyword
	    	searchPlaceholder: '审批人搜索',	//搜索输入框的提示文字 默认关键词搜索
	    	table: {	//定义表格参数，与LAYUI的TABLE模块一致，只是无需再定义表格elem
	    		url: flowableBasePath + 'checkwork008',
	    		where: {userName:$("#userName").val()},
	    		method: 'post',
	    		page: true,
	    	    limits: [8, 16, 24, 32, 40, 48, 56],
	    	    limit: 8,
	    		cols: [[
	    		    { type: 'radio'},
					{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
					{ field: 'userName', title: '审批人', width: 100 },
					{ field: 'userSex', title: '性别', width: 60, templet: function(d){
			        	if(d.userSex == '0'){
			        		return "保密";
			        	}else if(d.userSex == '1'){
			        		return "男";
			        	}else if(d.userSex == '2'){
			        		return "女";
			        	} else {
			        		return "参数错误";
			        	}
			        }},
			        { field: 'departmentName', title: '所在部门', width: 100 },
				]]
	    	},
	    	done: function (elem, data) {
	    		var newJson = data.data[0].userName;
	    		ids = data.data[0].id;
	    		elem.val(newJson);
	    		elem.attr('ts-selected', ids);
	    	}
	    })
	    
	    initAppealReasonId();
		//初始化申诉内容
		function initWorkId(){
			showGrid({
			 	id: "workId",
			 	url: flowableBasePath + "checkwork004",
			 	params: {appealType: appealType},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		
		//初始化申诉原因类型
		function initAppealReasonId(){
			showGrid({
			 	id: "appealReasonId",
			 	url: flowableBasePath + "checkworkreason010",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 		initWorkId();
			 	}
		    });
		}
	    
	    //申诉类型监听事件
		form.on('select(appealType)', function(data){
			appealType = data.value;
			workId = '';
			initWorkId();
		});
		
		//申诉内容监听事件
		form.on('select(workId)', function(data){
			workId = data.value;
		});
		
		//申诉原因类型监听事件
		form.on('select(appealReasonId)', function(data){
			appealReasonId = data.value;
		});
		
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	if(isNull(workId)){
 	        		winui.window.msg('请选择申诉内容', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	if(isNull(appealReasonId)){
 	        		winui.window.msg('请选择申诉原因类型', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	var params = {
 	        		appealType: appealType,
 	        		workId: workId,
 	        		appealReasonId: appealReasonId,
 	        		appealReason: encodeURIComponent($("#appealReason").val()),
 	        		approvalId: ids
 	        	};
 	        	if(!isNull($("#approvalId").val()) && !isNull(ids)){
 	        		AjaxPostUtil.request({url: flowableBasePath + "checkwork005", params:params, type: 'json', callback: function(json){
 	 	        		if (json.returnCode == 0) {
 	 	        			parent.layer.close(index);
 	 	        			parent.refreshCode = '0';
 	 	        		} else {
 	 	        			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	 	        		}
 	 	        	}});
        		} else {
        			winui.window.msg("请选择审批人!", {icon: 2, time: 2000});
        		}
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});