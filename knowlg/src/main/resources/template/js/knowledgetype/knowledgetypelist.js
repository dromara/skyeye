var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		tableTree = layui.tableTreeDj;
	
	authBtn('1568476869775');

	tableTree.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.knowlgBasePath + 'knowledgetype001',
	    where: getTableParams(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '名称',  width: 360 },
	        { field: 'state', title: '状态', width: 80, align: 'center', templet: function (d) {
	        	if(d.state == '3'){
	        		return "<span class='state-down'>下线</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>上线</span>";
	        	}else if(d.state == '1'){
	        		return "<span class='state-new'>新建</span>";
	        	}
	        }},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	}, {
		keyId: 'id',
		keyPid: 'pId',
		title: 'name',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'delet') { //删除
        	delet(data);
        } else if (layEvent === 'up') { //上线
        	up(data);
        } else if (layEvent === 'down') { //下线
        	down(data);
        }
    });
	
	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/knowledgetype/knowledgetypeadd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "knowledgetypeadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.knowlgBasePath + "knowledgetype003", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 上线
	function up(data) {
		var msg = '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '上线知识库类型' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.knowlgBasePath + "knowledgetype004", params: {rowId: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg("上线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 下线
	function down(data) {
		var msg = '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '下线知识库类型' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.knowlgBasePath + "knowledgetype005", params: {rowId: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg("下线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgetype/knowledgetypeedit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "knowledgetypeedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			loadTable();
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
		tableTree.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return {
			name: $("#name").val()
		};
	}
    
    exports('knowledgetypelist', {});
});
