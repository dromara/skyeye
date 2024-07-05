
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

	var assetId = GetUrlParam("id");

	// 资产明细列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.admBasePath + 'queryAssetReportList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'assetMation', title: '资产名称', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.assetMation?.name);
			}},
			{ field: 'assetNum', title: '资产编号', width: 160 },
			{ field: 'unitPrice', title: '采购单价', width: 100 },
			{ field: 'fromId', title: '资产来源', width: 120, templet: function(d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("ADM_ASSET_FROM", d.fromId);
			}},
			{ field: 'barCodeMation', title: '条形码', align: 'center', width: 150, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.barCodeMation.imagePath) + '" class="photo-img" lay-event="barCode" style="width: 100px">';
			}},
			{ field: 'state', title: '状态', align: 'center', width: 100, templet: function(d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("assetReportState", 'id', d.state, 'name');
			}},
			{ field: 'state1', title: '入库状态', rowspan: '2', width: 90, templet: function (d) {
				// return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("assetReportState", 'id', d.state, 'name');
				if (isNull(d.state)) {
					return '未入库';
				} else {
					return '已入库';
				}
			}},

			{ field: 'assetAdminMation', title: '管理员', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.assetAdminMation?.name);
			}},
			{ field: 'useUserMation', title: '申领人', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.useUserMation?.name);
			}},
			{ field: 'storageArea', title: '存放区域', width: 140 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#messageTableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入资产编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'barCode') { // 条形码预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.barCodeMation.imagePath));
		} else if (layEvent === 'delete') { // 删除
			delet(data);
		}
    });

	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.admBasePath + "deleteAssetReportById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/assetReportManage/assetReportAdd.html?assetId=" + assetId,
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "assetReportAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 批量复制
	$("body").on("click", "#batchCopy", function() {
		_openNewWindows({
			url: "../../tpl/assetReportManage/batchCopy.html?assetId=" + assetId,
			title: '批量复制',
			pageId: "assetReportBatchCopy",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
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
			assetId: assetId
		}
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('assetReportList', {});
});
