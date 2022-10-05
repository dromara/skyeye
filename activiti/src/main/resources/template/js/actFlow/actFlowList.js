
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

	// 流程模型列表
	authBtn('1552960740348');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'queryActFlowList',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
			{ colspan: '4', title: '模型信息', align: 'center'},
			{ colspan: '2', title: '发布信息', align: 'center'},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120, rowspan: '2' },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150, rowspan: '2' },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120, rowspan: '2' },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150, rowspan: '2' },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 340, toolbar: '#tableBar', rowspan: '2' }
	    ], [
			{ field: 'flowName', title: '模型名称', width: 150 },
			{ field: 'modelId', title: '模型ID', width: 100 },
			{ field: 'modelKey', title: '模型key', width: 250 },
			{ field: 'version', title: '版本', width: 80, templet: function (d) {return d.model.version}},
			{ field: 'deploymentId', title: '发布状态', align: "center", width: 80, templet: function (d) {
				if (isNull(d.model.deploymentId)) {
					return "<span class='state-new'>未发布</span>";
				} else {
					return "<span class='state-up'>已发布</span>";
				}
			}},
			{ field: 'processDefinitionVersion', title: '版本', width: 100, templet: function (d) {
				if (isNull(d.procdef) || isNull(d.procdef.version)) {
					return '';
				}
				return d.procdef.version
			}}
		]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称，模型key", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
		} else if (layEvent === 'modelDesign') { // 流程设计
			modelDesign(data);
		} else if (layEvent === 'fb') { //发布
        	fb(data);
        } else if (layEvent === 'versionLevel') { //版本升级
        	versionLevel(data);
        } else if (layEvent === 'qxfb') { // 取消发布
        	qxfb(data);
        } else if (layEvent === 'ecportXML') { //导出xml
        	ecportXML(data);
        }
    });

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "deleteActFlowMationById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 发布
	function fb(data) {
		layer.confirm('确认发布选中数据吗？', { icon: 3, title: '发布模型' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: flowableBasePath + "activitimode003", params: {modelId: data.modelId}, type: 'json', callback: function (json) {
				winui.window.msg("发布成功", {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	// 版本升级
	function versionLevel(data) {
		layer.confirm('确认升级选中数据吗？', { icon: 3, title: '版本升级' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: flowableBasePath + "activitimode003", params: {modelId: data.modelId}, type: 'json', callback: function (json) {
				winui.window.msg("升级成功", {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	// 取消发布
	function qxfb(data){
		layer.confirm('确认取消发布选中数据吗？', { icon: 3, title: '取消发布' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "activitimode007", params: {deploymentId: data.deploymentId}, type: 'json', callback: function (json) {
				winui.window.msg("取消发布成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 导出xml
	function ecportXML(data) {
		window.open(flowableBasePath + "activitimode010?modelId=" + data.modelId);
    }

    // 新增
    $("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/actFlow/actFlowAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "actFlowAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/actFlow/actFlowEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "actFlowEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 流程设计
	function modelDesign(data) {
		_openNewWindows({
			url: "../../static/modeler.html?modelId=" + data.modelId,
			title: "绘制流程",
			pageId: "canverActivitiModel",
			area: ['100vw', '100vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
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
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('actFlowList', {});
});
