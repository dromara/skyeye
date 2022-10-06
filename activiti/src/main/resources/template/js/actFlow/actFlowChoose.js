
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

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
			{ type: 'radio', fixed: 'left', rowspan: '2' },
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
			{ colspan: '4', title: '模型信息', align: 'center'},
			{ colspan: '2', title: '发布信息', align: 'center'},
			{ field: 'actModelId', title: '流程配置', align: "center", width: 80, rowspan: '2', templet: function (d) {
				if (isNull(d.actModelId)) {
					return "<span class='state-new'>未配置</span>";
				} else {
					return "<span class='state-up'>已配置</span>";
				}
			}},
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

			for (var i = 0; i < json.rows.length; i++) {
				// 未发布的或者已做配置的工作流模型设置为不可选中
				if(isNull(json.rows[i].model.deploymentId) || !isNull(json.rows[i].actModelId)){
					systemCommonUtil.disabledRow(json.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}

			$('#messageTable').next().find('.layui-table-body').find("table").find("tbody").children("tr").on('dblclick',function() {
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if(!dubClick.find("input[type='radio']").prop("disabled")) {
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = json.rows[chooseIndex];
					parent.actFlowMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				}
			});

			$('#messageTable').next().find('.layui-table-body').find("table").find("tbody").children("tr").on('click',function() {
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if(!click.find("input[type='radio']").prop("disabled")) {
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				}
			});
	    }
	});
	
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
    
    exports('actFlowChoose', {});
});
