
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

	authBtn('1552962689111');

	tableTree.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'companyjob001',
		where: getTableParams(),
		cols: [[
			{ field: 'name', title: '职位名称', width: 180 },
			{ field: 'id', title: '职位简介', width: 80, align: 'center', templet: function (d) {
				return '<i class="fa fa-fw fa-html5 cursor" lay-event="jobDesc"></i>';
			}},
			{ field: 'userNum', title: '员工数', width: 100 },
			{ field: 'companyName', title: '所属公司', width: 150 },
			{ field: 'departmentName', title: '所属部门', width: 100 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar' }
		]],
		isPage: false,
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch($("#messageTable")[0], json.searchFilter, form, "请输入名称", function () {
				tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	}, {
		keyId: 'id',
		keyPid: 'parentId',
		title: 'name',
		defaultShow: true,
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { //删除
			del(data, obj);
		} else if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'jobDesc') { //职位简介
			layer.open({
				id: '职位简介',
				type: 1,
				title: '职位简介',
				shade: 0.3,
				area: ['90vw', '90vh'],
				content: data.jobDesc
			});
		} else if (layEvent === 'jobScore') { //岗位定级
			jobScore(data);
		}
	});
	
	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteCompanyJobMationById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companyJob/companyJobEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "companyJobEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/companyJob/companyJobAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "companyJobAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 岗位定级
	function jobScore(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companyJobScore/companyJobScoreList.html",
			title: '岗位定级',
			pageId: "companyJobScoreList",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		tableTree.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('companyJobList', {});
});
