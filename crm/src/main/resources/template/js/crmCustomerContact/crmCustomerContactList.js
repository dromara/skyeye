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
	
	authBtn('1596375844035');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'customercontact001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'contacts', title: '联系人', align: 'left', width: 100 },
	        { field: 'customerName', title: '所属客户', align: 'left', width: 180 },
	        { field: 'department', title: '部门', align: 'left', width: 100 },
	        { field: 'job', title: '职位', align: 'left', width: 100 },
	        { field: 'workPhone', title: '办公电话', align: 'left', width: 100 },
	        { field: 'mobilePhone', title: '移动电话', align: 'center', width: 100 },
	        { field: 'email', title: '邮箱', align: 'left', width: 140 },
	        { field: 'qq', title: 'QQ', align: 'left', width: 100 },
	        { field: 'wechat', title: '微信', align: 'left', width: 100 },
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
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
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'delete'){ //删除
        	del(data);
        }
    });
    
    
	// 新增
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/crmCustomerContact/crmCustomerContactAdd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "crmCustomerContactAdd",
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
			url: "../../tpl/crmCustomerContact/crmCustomerContactEdit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "crmCustomerContactEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "customercontact005", params: {contactId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	form.render();
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		customerName: $("#customerName").val(),
    		contacts: $("#contacts").val(),
    		mobilePhone: $("#mobilePhone").val(),
    		email: $("#email").val(),
    		qq: $("#qq").val(),
    		wechat: $("#wechat").val()
    	};
    }
	
    exports('crmCustomerContactList', {});
});