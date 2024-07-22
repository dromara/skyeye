
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	var componentId = GetUrlParam("componentId");
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryAttrByComponentId',
	    where: getTableParams(),
	    even: true,
		page: false,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'applicationName', title: '所属服务', width: 200, templet: function (d) {
				return isNull(d.serviceBean) ? '' : d.serviceBean.applicationName;
			}},
			{ field: 'appId', title: 'APPID', width: 220, templet: function (d) {
				return isNull(d.serviceBean) ? '' : d.serviceBean.appId;
			}},
	        { field: 'groupName', title: '所属分组', width: 160, templet: function (d) {
				return isNull(d.serviceBean) ? '' : d.serviceBean.groupName;
			}},
			{ field: 'name', title: '所属功能', width: 160, templet: function (d) {
				return isNull(d.serviceBean) ? '' : d.serviceBean.name;
			}},
			{ field: 'attrKey', title: '属性', width: 150 },
			{ field: 'name', title: '属性名称', width: 100 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar' }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { // 删除
			del(data);
		}
	});

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: reqBasePath + "deleteAttrDefinitionCustomById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
	function getTableParams() {
		return $.extend(true, {componentId: componentId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('classServerList', {});
});
