
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

	// 流程设计列表
	authBtn('1552960740348');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode002',
	    where: getTableParams(),
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'id', title: '模型编号', width: 100 },
	        { field: 'name', title: '模型名称', width: 150 },
	        { field: 'key', title: '编码', width: 120 },
			{ field: 'processKey', title: 'key', width: 250 },
	        { field: 'version', title: '版本', width: 80},
	        { field: 'deploymentId', title: '发布状态', align: "center", width: 80, templet: function(d){
	        	if(isNull(d.deploymentId)){
	        		return "<span class='state-new'>未发布</span>";
	        	} else {
	        		return "<span class='state-up'>已发布</span>";
	        	}
	        }},
	        { field: 'deploymentId', title: '部署id', width: 100},
	        { field: 'processDefinitionVersion', title: '部署版本', width: 100},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150},
	        { field: 'metaInfo', title: '元数据', width: 120},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 340, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'copyModel') { //模型拷贝
			copyModel(data);
		}else if (layEvent === 'fb') { //发布
        	fb(data);
        }else if (layEvent === 'versionLevel') { //版本升级
        	versionLevel(data);
        }else if (layEvent === 'qxfb') { //取消发布
        	qxfb(data);
        }else if (layEvent === 'ecportXML') { //导出xml
        	ecportXML(data);
        }
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
			refreshTable();
        }
        return false;
	});

	// 刷新数据
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除模型【' + obj.data.name + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除模型' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "activitimode006", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../static/modeler.html?modelId=" + rowId,
			title: "绘制流程",
			pageId: "canveractivitimodeledit",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//发布
	function fb(data){
		rowId = data.id;
		var msg = data ? '确认发布模型【' + data.name + '】吗？' : '确认发布选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '发布模型' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url:flowableBasePath + "activitimode003", params:{modelId: rowId}, type: 'json', callback: function(json){
				if (json.returnCode == 0) {
					winui.window.msg("发布成功", {icon: 1, time: 2000});
					loadTable();
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	}
	
	//版本升级
	function versionLevel(data){
		rowId = data.id;
		var msg = data ? '确认升级模型【' + data.name + '】版本吗？' : '确认升级选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '版本升级' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url:flowableBasePath + "activitimode003", params:{modelId: rowId}, type: 'json', callback: function(json){
				if (json.returnCode == 0) {
					winui.window.msg("升级成功", {icon: 1, time: 2000});
					loadTable();
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	}
	
	// 取消发布
	function qxfb(data, obj){
		var msg = obj ? '确认取消发布【' + obj.data.name + '】吗？' : '确认取消发布选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '取消发布' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "activitimode007", params:{deploymentId: data.deploymentId}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg("取消发布成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 拷贝模型
	function copyModel(data){
		layer.confirm('确认拷贝选中数据吗？', { icon: 3, title: '模型拷贝' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url:flowableBasePath + "activitimode028", params:{modelId: data.id}, type: 'json', callback: function(json){
				if (json.returnCode == 0) {
					winui.window.msg("拷贝成功", {icon: 1, time: 2000});
					loadTable();
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	}
	
	// 导出xml
	function ecportXML(data){
		window.open(flowableBasePath + "activitimode010?modelId=" + data.id);
    }
	
    //新增
    $("body").on("click", "#addBean", function() {
    	AjaxPostUtil.request({url:flowableBasePath + "activitimode001", params:{}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				_openNewWindows({
					url: "../../static/modeler.html?modelId=" + json.bean.id,
					title: "绘制流程",
					pageId: "canveractivitimodel",
					area: ['100vw', '100vh'],
					callBack: function(refreshCode){
		                if (refreshCode == '0') {
		                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
		                	loadTable();
		                } else if (refreshCode == '-9999') {
		                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
		                }
					}});
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

	// 搜索
	function refreshTable(){
		table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
	}

    function getTableParams(){
    	return {
			modelName: $("#modelName").val()
		};
	}
    
    exports('codemodellist', {});
});
