
var rowId = "";
var disRowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'table', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	form = layui.form,
    	table = layui.table,
    	element = layui.element;
    
    var customerId = parent.rowId;
    
    var tabTable = {
    	tab0: {load: true, initMethod: opportunityList}, // 商机列表
    	tab1: {load: false, initMethod: contractList}, // 合同列表
    	tab2: {load: false, initMethod: serviceList}, // 售后服务列表
    	tab3: {load: false, initMethod: documentaryList}, // 跟单列表
    	tab4: {load: false, initMethod: contactsList}, // 联系人列表
    	tab5: {load: false, initMethod: discussList} // 讨论板列表
    };
    
    showGrid({
	 	id: "showForm",
	 	url: flowableBasePath + "customer005",
	 	params: {rowId: customerId},
	 	pagination: false,
	 	template: getFileContent('tpl/customermanage/customerdetailsTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter: function(json){
	 		// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUpload": json.bean.enclosureInfo});

        	element.on('tab(customerOtherDetail)', function(obj){
				var mation = tabTable["tab" + obj.index];
				if(!isNull(mation)){
					if(!mation.load){
						tabTable["tab" + obj.index].load = true;
						mation.initMethod();
					}
				}
			});
			
			// 默认加载商机列表
			opportunityList();
			
        	matchingLanguage();
	 		form.render();
	 	}
	});
	
	// 商机列表
	function opportunityList(){
		table.render({
		    id: 'opportunityTable',
		    elem: '#opportunityTable',
		    method: 'post',
		    url: flowableBasePath + 'customer008',
		    where: getOpportunityParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '商机名称', align: 'left', width: 300, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'responsId', title: '商机负责人', align: 'left', width: 120 },
		        { field: 'estimatePrice', title: '预计成交金额（元）', align: 'left', width: 150 },
		        { field: 'estimateEndTime', title: '预计结单日期', align: 'center', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 120 },
		        { field: 'state', title: '状态', align: 'left', width: 120 }
		    ]]
		});
		
		table.on('tool(opportunityTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	opportunityDetails(data);
	        }
	    });
		
		// 商机搜索表单
		$("body").on("click", "#opportunitySearch", function(){
			table.reload("opportunityTable", {page: {curr: 1}, where: getOpportunityParams()});
		});
	}
	
	// 商机详情
	function opportunityDetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmOpportunity/crmopportunitydetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "crmopportunitydetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 商机列表查询条件
	function getOpportunityParams(){
		return {
			rowId: customerId,
			searchType: "opportunity",
			orderNum: "",
			searchName: $("#opportunityTitle").val(),
			searchState: $("#opportunityState").val(),
			searchTime: $("#opportunityTime").val()
		};
	}
	
	// 合同列表
	function contractList(){
		table.render({
		    id: 'contractTable',
		    elem: '#contractTable',
		    method: 'post',
		    url: flowableBasePath + 'customer008',
		    where: getContractParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '合同名称', align: 'left', width: 300, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'num', title: '合同编号', align: 'left', width: 120 },
		        { field: 'price', title: '合同金额（元）', align: 'left', width: 120 },
		        { field: 'signingTime', title: '签约日期', align: 'center', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 120 },
		        { field: 'state', title: '状态', align: 'left', width: 120 }
		    ]]
		});
		
		table.on('tool(contractTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	contractDetails(data);
	        }
	    });
		
		// 合同搜索表单
		$("body").on("click", "#contractSearch", function(){
			table.reload("contractTable", {page: {curr: 1}, where: getContractParams()});
		});
	}
	
	// 合同详情
	function contractDetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmcontractmanage/mycrmcontractdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "crmcontractdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 合同列表查询条件
	function getContractParams(){
		return {
			rowId: customerId,
			searchType: "contract",
			orderNum: "",
			searchName: $("#contractTitle").val(),
			searchState: $("#contractState").val(),
			searchTime: $("#contractTime").val()
		};
	}
	
	// 售后服务列表
	function serviceList(){
		table.render({
		    id: 'serviceTable',
		    elem: '#serviceTable',
		    method: 'post',
		    url: flowableBasePath + 'customer008',
		    where: getServiceParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'orderNum', title: '单号', align: 'center', width: 220, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
		        }},
		        { field: 'typeName', title: '服务类型', align: 'left', width: 120 },
		        { field: 'modeName', title: '服务方式', align: 'left', width: 120 },
		        { field: 'declarationTime', title: '报单时间', align: 'center', width: 120 },
		        { field: 'state', title: '状态', align: 'left', width: 120 }
		    ]]
		});
		
		table.on('tool(serviceTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	serviceDetails(data);
	        }
	    });
		
		// 售后服务搜索表单
		$("body").on("click", "#serviceSearch", function(){
			table.reload("serviceTable", {page: {curr: 1}, where: getServiceParams()});
		});
	}
	
	// 售后服务详情
	function serviceDetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealseservice/sealseservicedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 售后服务列表查询条件
	function getServiceParams(){
		return {
			rowId: customerId,
			searchType: "service",
			orderNum: $("#serviceTitle").val(),
			searchName: "",
			searchState: $("#serviceState").val(),
			searchTime: $("#serviceTime").val()
		};
	}
	
	// 跟单列表
	function documentaryList(){
		table.render({
		    id: 'documentaryTable',
		    elem: '#documentaryTable',
		    method: 'post',
		    url: flowableBasePath + 'customer008',
		    where: getDocumentaryParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'detail', title: '详细内容', align: 'center', width: 220, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.detail + '</a>';
		        }},
		        { field: 'opportunityName', title: '商机', align: 'left', width: 300},
		        { field: 'typeName', title: '类型', align: 'center', width: 120 },
		        { field: 'documentaryTime', title: '跟单时间', align: 'center', width: 150 },
		        { field: 'createName', title: '跟单人', align: 'center', width: 120 }
		    ]]
		});
		
		table.on('tool(documentaryTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	documentaryDetails(data);
	        }
	    });
	}
	
	// 跟单详情
	function documentaryDetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/documentarymanage/documentaryDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "documentaryDetails",
			area: ['70vw', '70vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 跟单列表查询条件
	function getDocumentaryParams(){
		return {
			rowId: customerId,
			searchType: "documentary",
			orderNum: "",
			searchName: "",
			searchState: "",
			searchTime: ""
		};
	}
	
	// 联系人列表
	function contactsList(){
		table.render({
		    id: 'contactsTable',
		    elem: '#contactsTable',
		    method: 'post',
		    url: flowableBasePath + 'customer008',
		    where: getContactsParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'contacts', title: '联系人', align: 'left', width: 100 },
		        { field: 'department', title: '部门', align: 'left', width: 100 },
		        { field: 'job', title: '职位', align: 'left', width: 100 },
		        { field: 'workPhone', title: '办公电话', align: 'left', width: 100 },
		        { field: 'mobilePhone', title: '移动电话', align: 'center', width: 100 },
		        { field: 'email', title: '邮箱', align: 'left', width: 140 },
		        { field: 'qq', title: 'QQ', align: 'left', width: 100 },
		        { field: 'wechat', title: '微信', align: 'left', width: 100 },
		        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
		        { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
		        { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100}
		    ]]
		});
		
		// 联系人搜索表单
		$("body").on("click", "#contactsSearch", function(){
			table.reload("contactsTable", {page: {curr: 1}, where: getContactsParams()});
		});
	}
	
	// 联系人列表查询条件
	function getContactsParams(){
		return {
			rowId: customerId,
			searchType: "contacts",
			orderNum: "",
			searchName: $("#contactsTitle").val(),
			searchState: "",
			searchTime: ""
		};
	}
	
	// 讨论板列表
	function discussList(){
		table.render({
		    id: 'discussTable',
		    elem: '#discussTable',
		    method: 'post',
		    url: flowableBasePath + 'customer008',
		    where: getDiscussParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '主题', align: 'left', width: 300, templet: function(d){
		        	return '<a lay-event="discussDetails" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'opportunityName', title: '所属商机', align: 'left', width: 200 },
		        { field: 'createName', title: '作者', align: 'left', width: 80 },
		        { field: 'replyNum', title: '回复', align: 'left', width: 80 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 120 },
		        { field: 'recoveryTime', title: '最后回复时间', align: 'center', width: 120 }
		    ]]
		});
		
		table.on('tool(discussTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'discussDetails'){ // 详情
	        	discussDetails(data);
	        }
	    });
		
		// 讨论板搜索表单
		$("body").on("click", "#discussSearch", function(){
			table.reload("discussTable", {page: {curr: 1}, where: getDiscussParams()});
		});
	}
	
	// 讨论板详情
	function discussDetails(data){
		// 商机id
		disRowId = data.id;
		// 讨论板id
		rowId = data.opportunityId;
		_openNewWindows({
			url: "../../tpl/crmdiscuss/discussdetail.html", 
			title: data.title,
			pageId: "discussdetailpage",
			maxmin: true,
			callBack: function(refreshCode){
			}});
	}
	
	// 讨论板列表查询条件
	function getDiscussParams(){
		return {
			rowId: customerId,
			searchType: "discuss",
			orderNum: "",
			searchName: $("#contactsTitle").val(),
			searchState: "",
			searchTime: ""
		};
	}
    
});