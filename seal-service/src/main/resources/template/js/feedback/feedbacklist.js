
var rowId = "";
var serviceId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'table'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	table = layui.table;
	    
	    serviceId = parent.rowId;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice039",
		 	params: {rowId: serviceId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter: function(json){
		 		matchingLanguage();
		 		form.render();
		 	    
		 		//待完工或者待评价的工单可以进行反馈操作
		 		if(json.bean.state != 4 && json.bean.state != 5){
		 			$("#addBean").remove();
		 		}
		 		
		 		
				table.render({
				    id: 'messageTable',
				    elem: '#messageTable',
				    method: 'post',
				    url: flowableBasePath + 'feedback001',
				    where: {serviceId: serviceId},
				    even: true,
				    page: true,
				    limits: [8, 16, 24, 32, 40, 48, 56],
				    limit: 8,
				    cols: [[
				        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
				        { field: 'typeName', title: '反馈类型', align: 'left', width: 120},
				        { field: 'createName', title: '反馈人', align: 'left', width: 80 },
				        { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 140 },
				        { field: 'content', title: '反馈内容', align: 'left', width: 300 },
				        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
				    ]]
				});
				
				table.on('tool(messageTable)', function (obj) {
			        var data = obj.data;
			        var layEvent = obj.event;
			        if (layEvent === 'edit') { //编辑
			        	edit(data);
			        }else if (layEvent === 'delete'){ //删除
			        	deleteRow(data);
			        }else if (layEvent === 'details'){ //详情
			        	details(data);
			        }
			    });
		 		
		 	}
		});
		
		$("body").on("click", "#reloadTable", function(){
	    	loadTable();
	    });
	    
	    function loadTable(){
	    	table.reload("messageTable", {where: {serviceId: serviceId}});
	    }
		
	    //新增
	    $("body").on("click", "#addBean", function(){
	    	_openNewWindows({
				url: "../../tpl/feedback/feedbackadd.html", 
				title: systemLanguage["com.skyeye.addPageTitle"][languageType],
				pageId: "feedbackadd",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
	                if (refreshCode == '0') {
	                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
	                	loadTable();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
	    });
	    
		//编辑
		function edit(data){
			rowId = data.id;
			_openNewWindows({
				url: "../../tpl/feedback/feedbackedit.html", 
				title: systemLanguage["com.skyeye.editPageTitle"][languageType],
				pageId: "feedbackedit",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
	                if (refreshCode == '0') {
	                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
	                	loadTable();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		}
		
		//删除
		function deleteRow(data){
			layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
				layer.close(index);
	            AjaxPostUtil.request({url: flowableBasePath + "feedback006", params: {rowId: data.id}, type: 'json', callback: function(json){
	    			if(json.returnCode == 0){
	    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
	    				loadTable();
	    			}else{
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
		}
		
		//详情
		function details(data){
			rowId = data.id;
			_openNewWindows({
				url: "../../tpl/feedback/feedbackdetails.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "feedbackdetails",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
				}});
		}
	    
	});
});