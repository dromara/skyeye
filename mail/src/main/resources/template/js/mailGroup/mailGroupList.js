
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

	authBtn('1634980505597');

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'mailGroup001',
		where: getTableParams(),
		even:true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'name', title: '类别', width: 100},
			{ field: 'desc', title: '简介', width: 600 },
			{ field: 'perpleNum', title: '人数', width: 80 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
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
		}else if (layEvent === 'del') { // 删除
			del(data, obj);
		}
	});

	// 新建
	$("body").on("click", "#addMailType", function (e) {
		_openNewWindows({
			url: "../../tpl/mailGroup/mailGroupAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "mailGroupAdd",
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
			url: "../../tpl/mailGroup/mailGroupEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "mailGroupEdit",
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
            AjaxPostUtil.request({url: reqBasePath + "mailGroup003", params:{rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
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
	$("body").on("click", "#reloadTable", function (e) {
		loadTable();
	});

	function refreshTable(){
		table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function loadTable(){
		table.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams(){
		return {};
	}
	
    exports('mailGroupList', {});
});
