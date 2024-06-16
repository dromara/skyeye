
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
	// 关联的客户/供应商/会员购买或者出售的商品link的id
	var id = getNotUndefinedVal(GetUrlParam("id"));

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.erpBasePath + 'queryHolderNormsChildList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'normsCodeNum', title: '编码', align: 'left', width: 180, templet: function (d) {
				return d.normsCodeNum;
			}},
			{ field: 'barCodeMation', title: '条形码', align: 'center', width: 150, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.normsCodeMation?.barCodeMation?.imagePath) + '" class="photo-img" lay-event="barCode" style="width: 100px">';
			}},
			{ field: 'state', title: '状态', align: 'center', width: 100, templet: function(d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("holderNormsChildState", 'id', d.state, 'name');
			}}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'barCode') { // 条形码预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.normsCodeMation?.barCodeMation?.imagePath));
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
		let params = {
			holderId: id
		}
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('holderNormsChildList', {});
});
