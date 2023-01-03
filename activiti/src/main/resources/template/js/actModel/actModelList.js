
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
	
	authBtn('1564106558827');

	var typeId = "";
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["actModelType"]["key"], 'selectTree', "typeList", '', form, function () {
		initTable();
	}, function (chooseId) {
		typeId = chooseId;
		refreshTable();
	});

	function initTable() {
		table.render({
			id: 'messageTable',
			elem: '#messageTable',
			method: 'post',
			url: flowableBasePath + 'queryActModelList',
			where: getTableParams(),
			even: true,
			page: true,
			limits: getLimits(),
			limit: getLimit(),
			cols: [[
				{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'title', title: '名称', align: 'left', width: 120, templet: function (d) {
					return '<a lay-event="dedails" class="notice-title-click">' + d.title + '</a>';
				}},
				{ field: 'actFlowName', title: '工作流模型', align: 'left', width: 120 },
				{ field: 'addPageUrl', title: '新增页面', align: 'left', width: 200 },
				{ field: 'editPageUrl', title: '编辑页面', align: 'left', width: 200 },
				{ field: 'typeName', title: '所属类型', align: 'left', width: 140 },
				{ field: 'iconBg', title: '背景', align: 'center', width: 80 },
				{ field: 'state', title: '状态', align: 'center', width: 100, templet: function (d) {
					if (d.state == '1') {
						return "<span class='state-new'>新建</span>";
					} else if (d.state == '2') {
						return "<span class='state-up'>上线</span>";
					} else if (d.state == '3') {
						return "<span class='state-down'>下线</span>";
					} else {
						return "参数错误";
					}
				}},
				{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
				{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
				{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar' }
			]],
			done: function(json) {
				matchingLanguage();
				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
			}
		});

		table.on('tool(messageTable)', function (obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if (layEvent === 'del') {  //移除
				del(data);
			} else if (layEvent === 'edit') { //编辑
				edit(data);
			} else if (layEvent === 'up') { //上线
				up(data);
			} else if (layEvent === 'down') { //下线
				down(data);
			} else if (layEvent === 'upMove') { //上移
				upMove(data);
			} else if (layEvent === 'downMove') { //下移
				downMove(data);
			} else if (layEvent === 'dedails') { //详情
				dedails(data);
			}
		});
	}
	
	// 新增申请类型实体
	$("body").on("click", "#addModle", function (e) {
		_openNewWindows({
			url: "../../tpl/actModel/actModelAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "actModelAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});
	
	// 删除工作流任务配置
	function del(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "deleteActModelById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 上线申请类型实体
	function up(data) {
		layer.confirm('确认上线选中数据吗？', { icon: 3, title: '上线申请类型实体' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "updateUpActModelById", params: {id: data.id}, type: 'json', method: 'PUT', callback: function (json) {
				winui.window.msg("上线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 下线申请类型实体
	function down(data) {
		layer.confirm('确认下线选中数据吗？', { icon: 3, title: '下线申请类型实体' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "updateDownActModelById", params: {id: data.id}, type: 'json', method: 'PUT', callback: function (json) {
				winui.window.msg("下线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑申请类型实体
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/actModel/actModelEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "actModelEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

	// 详情
	function dedails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/actModel/actModelDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "actModelDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}
		});
	}
	
	// 上移申请类型实体
	function upMove(data) {
        AjaxPostUtil.request({url: flowableBasePath + "editActModelOrderNumUpById", params: {id: data.id, typeId: data.typeId}, type: 'json', method: 'PUT', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	// 下移申请类型实体
	function downMove(data) {
        AjaxPostUtil.request({url: flowableBasePath + "editActModelOrderNumDownById", params: {id: data.id, typeId: data.typeId}, type: 'json', method: 'PUT', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

	function refreshTable() {
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {type: typeId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('actModelList', {});
});
