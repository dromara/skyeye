
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
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'maillist001',
		where: getTableParams(),
		even:true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'userName', title: '姓名', width: 100, templet: function(d){
				return '<a lay-event="details" class="notice-title-click">' + d.userName + '</a>';
			}},
			{ field: 'phone', title: '手机', width: 100 },
			{ field: 'telePhone', title: '电话', width: 100 },
			{ field: 'email', title: '个人邮箱', width: 180 },
			{ field: 'unitName', title: '单位', width: 270 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
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
		}else if (layEvent === 'del') { //删除
			del(data);
		}else if (layEvent === 'details') { //详情
			details(data);
		}
	});

	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/mail/mailEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "mailEdit",
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
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/mail/mailDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "mailDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 删除
	function del(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "maillist009", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});
	
	// 刷新数据
	$("body").on("click", "#reloadTable", function(e){
		loadTable();
	});

	// 刷新表格
	function refreshTable(){
		table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function loadTable(){
		table.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams(){
		return {
			category: 2,
			userName: $("#userName").val(),
			phone: $("#phone").val(),
			email: $("#email").val()
		};
	}

    exports('commonMailList', {});
});
