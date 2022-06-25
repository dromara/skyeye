var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1573887926771');//列表
	
	//客户
	showGrid({
	 	id: "customer",
	 	url: flowableBasePath + "customer007",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(j){
	 		form.render('select');
	 	}
	});
	
	// 全部合同列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'mycrmcontract007',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '合同名称', align: 'left', width: 300, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'num', title: '合同编号', align: 'left', width: 120 },
	        { field: 'price', title: '合同金额（元）', align: 'left', width: 120 },
	        { field: 'signingTime', title: '签约日期', align: 'center', width: 100 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
	        { field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function(d){
	        	if(!isNull(d.processInstanceId)){
	        		return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        	} else {
	        		return "";
	        	}
	        }},
	        { field: 'stateName', title: '状态', width: 90, templet: function(d){
	        	if(d.state == '0'){
	        		return "<span>" + d.stateName + "</span>";
	        	}else if(d.state == '1'){
	        		return "<span class='state-new'>" + d.stateName + "</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>" + d.stateName + "</span>";
	        	}else if(d.state == '11'){
	        		return "<span class='state-up'>" + d.stateName + "</span>";
	        	}else if(d.state == '12'){
	        		return "<span class='state-down'>" + d.stateName + "</span>";
	        	}else if(d.state == '3'){
	        		return "<span class='state-error'>" + d.stateName + "</span>";
	        	}else if(d.state == '4'){
	        		return "<span class='state-error'>" + d.stateName + "</span>";
	        	}else if(d.state == '5'){
	        		return "<span class='state-error'>" + d.stateName + "</span>";
	        	}
	        }}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        }else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }
    });
	
	form.render();
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmcontractmanage/mycrmcontractdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "crmcontractdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		title: $("#title").val(),
    		customer: $("#customer").val(),
    		state: $("#state").val(),
    		createTime: $("#createTime").val()
    	};
    }
	
    exports('crmcontractlist', {});
});