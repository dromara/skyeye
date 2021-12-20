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
	var selectOption = getFileContent('tpl/template/select-option.tpl');
	
	// 分类
	showGrid({
	 	id: "typeId",
	 	url: reqBasePath + "customertype008",
	 	params: {},
	 	pagination: false,
	 	template: selectOption,
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(j){
	 		form.render('select');
	 		customerFrom();
	 	}
	});
	
	// 来源
	function customerFrom(){
		showGrid({
		 	id: "fromId",
		 	url: reqBasePath + "crmcustomerfrom008",
		 	params: {},
		 	pagination: false,
		 	template: selectOption,
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(j){
		 		form.render('select');
		 		customerIndustry();
		 	}
		});
	}
	
	// 行业
	function customerIndustry(){
		showGrid({
		 	id: "industryId",
		 	url: reqBasePath + "crmcustomerindustry008",
		 	params: {},
		 	pagination: false,
		 	template: selectOption,
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(j){
		 		form.render('select');
		 	}
		});
	}
	
	// 公海客户群
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'customer012',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'name', title: '客户名称', align: 'left', width: 300, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
	        }},
	        { field: 'typeName', title: '客户分类', align: 'left', width: 120 },
	        { field: 'fromName', title: '客户来源', align: 'left', width: 120 },
	        { field: 'industryName', title: '所属行业', align: 'left', width: 180 },
	        { field: 'noDocumentaryDayNum', title: '未跟单天数', align: 'left', width: 100 },
	        { field: 'createName', title: '创建人', align: 'left', width: 80 },
	        { field: 'createTime', title: '创建时间', align: 'center', width: 100 },
	        { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 100 },
	        { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100}
	    ]],
	    done: function(json){
	    	matchingLanguage();
	    	var str = "超过" + json.bean. noDocumentaryDayNum + "天未跟单";
	    	if(parseInt(json.bean.noChargeId) == 1){
	    		str += '，或者未指定负责人';
	    	}
	    	str += '的客户。';
	    	$("#showInfo").html(str);
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ // 详情
        	details(data);
        }
    });
	
	form.render();
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/customermanage/customerdetails.html", 
			title: "客户详情",
			pageId: "customerdetails",
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
    		name: $("#customerName").val(),
    		typeId: $("#typeId").val(),
    		fromId: $("#fromId").val(),
    		industryId: $("#industryId").val()
    	};
    }
	
    exports('customerInternationalList', {});
});