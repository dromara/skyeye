
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

	// 新增资产
	authBtn('1566465526122');

	// 资产列表管理开始
	table.render({
		id: 'assetlistTable',
		elem: '#assetlistTable',
		method: 'post',
		url: flowableBasePath + 'asset001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'assetName', title: '资产名称', width: 120, templet: function (d) {
				return '<a lay-event="assetlistdetails" class="notice-title-click">' + d.assetName + '</a>';
			}},
			{ field: 'assetImg', title: '图片', align: 'center', width: 60, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.assetImg) + '" class="photo-img" lay-event="assetImg">';
			}},
			{ field: 'typeId', title: '资产类型', width: 100 },
			{ field: 'numberPrefix', title: '资产编号前缀', width: 140 },
			{ field: 'readPrice', title: '参考价', width: 80 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#assetlisttableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入资产名称", function () {
				table.reloadData("assetlistTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(assetlistTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'assetlistdetails') { // 详情
			assetlistdetails(data);
		} else if (layEvent === 'assetlistdelet') { // 删除
			assetlistdelet(data);
		} else if (layEvent === 'assetlistedit') {	// 编辑
			assetlistedit(data);
		} else if (layEvent === 'assetImg') { // 图片预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.assetImg));
		}
    });

	// 详情
	function assetlistdetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManage/assetManageDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "assetManageDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	// 删除
	function assetlistdelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset003", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadassetTable();
    		}});
		});
	}

	// 新增
	$("body").on("click", "#assetlistaddBean", function() {
    	_openNewWindows({
			url: "../../tpl/assetManage/assetManageAdd.html",
			title: "新增资产",
			pageId: "assetManageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadassetTable();
			}});
    });

	// 编辑
	function assetlistedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManage/assetManageEdit.html",
			title: "编辑资产",
			pageId: "assetManageEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadassetTable();
			}});
	}

	form.render();
	// 刷新数据
    $("body").on("click", "#assetlistreloadTable", function() {
    	loadassetTable();
    });

    function loadassetTable(){
    	table.reloadData("assetlistTable", {where: getTableParams()});
    }

    function getTableParams(){
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("assetlistTable"));
    }

    exports('assetManageList', {});
});
