
var objectId = "";

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

	objectId = GetUrlParam("objectId");
	if (isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'queryActFlowListByClassName',
	    where: {className: objectId},
	    even: true,
	    page: false,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
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
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120, rowspan: '2' },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150, rowspan: '2' },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120, rowspan: '2' },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150, rowspan: '2' }
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
	    }
	});
	
	form.render();

    exports('classServerProcessList', {});
});