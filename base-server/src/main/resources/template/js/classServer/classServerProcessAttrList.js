
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
	    url: reqBasePath + '',
	    where: {className: objectId},
	    even: true,
	    page: false,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'attrKey', title: '属性', align: 'left', width: 150 },
			{ field: 'name', title: '名称', align: 'left', width: 120 },
			{ field: 'attrType', title: '类型', align: 'left', width: 120 },
			{ field: 'remark', title: '备注', align: 'left', width: 150 },
			{ field: 'required', title: '限制条件', align: 'left', width: 140 },
			{ field: 'modelAttribute', title: '是否模型属性', align: 'center', width: 100, templet: function (d) {
				if (d.modelAttribute) {
					return '是';
				}
				return '否';
			}},
			{ field: 'whetherInputParams', title: '是否作为入参', align: 'center', width: 100, templet: function (d) {
				if (d.whetherInputParams) {
					return '是';
				}
				return '否';
			}}
		]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	form.render();

    exports('classServerProcessAttrList', {});
});