var rowId = "";
var searchType = "";

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
	
	authBtn('1586179447152');

	// 获取客户类型状态为上线的所有记录
	sysCustomerUtil.queryCustomerTypeIsUpList(function (data){
		$("#typeId").html(getDataUseHandlebars(selectOption, data));
		form.render('select');
	});

	// 获取已上线的客户来源类型
	sysCustomerUtil.queryCustomerFromIsUpList(function (data){
		$("#fromId").html(getDataUseHandlebars(selectOption, data));
		form.render('select');
	});

	// 获取已上线的客户所属行业列表
	sysCustomerUtil.queryCrmCustomerIndustryIsUpList(function (data){
		$("#industryId").html(getDataUseHandlebars(selectOption, data));
		form.render('select');
	});

	// 获取我创建的客户管理列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'customer011',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'name', title: '客户名称', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
	        }},
	        { field: 'typeName', title: '客户分类', align: 'left', width: 120 },
	        { field: 'fromName', title: '客户来源', align: 'left', width: 120 },
	        { field: 'industryName', title: '所属行业', align: 'left', width: 180 },
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
	        { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
	        { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { // 编辑
        	edit(data);
        }else if (layEvent === 'delete'){ // 删除
        	del(data);
        }else if (layEvent === 'details'){ // 详情
        	details(data);
        }
    });
	
	form.render();
	
	// 新增
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/customermanage/customeradd.html", 
			title: "新增客户",
			pageId: "customeradd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/customermanage/customeredit.html", 
			title: "编辑客户",
			pageId: "customeredit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/customermanage/customerdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "customerdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "customer006", params: {rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
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
	
    exports('myCreateList', {});
});