
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tableTreeDj', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		tableTree = layui.tableTreeDj,
		form = layui.form;
	var treeTableData = [];
	// 已经选中的权限对应关系
	var checkTrueList = [];

	showGrid({
		id: "showForm",
		url: reqBasePath + "queryTeamTemplateById",
		params: {id: parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#showTemplate").html(),
		ajaxSendLoadBefore: function(hdb, json) {
			json.bean.enabled = skyeyeClassEnumUtil.getEnumDataNameByClassName('commonEnable', 'id', json.bean.enabled, 'name');
			json.bean.objectTypeName = skyeyeClassEnumUtil.getEnumDataNameByClassName('teamObjectType', 'id', json.bean.objectType, 'name');
			json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
		},
		ajaxSendAfter: function (json) {
			loadTreeTable();
			// 解析成员信息
			if (!isNull(json.bean.teamRoleList)) {
				$.each(json.bean.teamRoleList, function (i, item) {
					treeTableData.push({
						id: item.roleId,
						pId: '0',
						name: item.name
					});
					$.each(item.teamRoleUserList, function (j, bean) {
						treeTableData.push({
							id: bean.userId,
							pId: item.roleId,
							name: bean.userMation.name,
							departmentName: bean.userMation.departmentName,
							phone: bean.userMation.phone,
							email: bean.userMation.email
						});
					});
				});
			}

			// 解析权限信息
			$.each(json.bean.teamObjectPermissionList, function (i, item) {
				var authGroupKey = item.permissionKey;
				var authKey = item.permissionValue;
				var checkParams = getInPoingArr(treeTableData, "id", item.ownerId);
				if (checkParams != null) {
					var roleId = checkParams.pId == '0' ? checkParams.id : checkParams.pId;
					var userId = checkParams.pId == '0' ? '' : checkParams.id;
					var id = authGroupKey + '_' + authKey + '_' + roleId + '_' + userId;
					checkTrueList.push(id);
				}
			});

			reloadTreeTable(json.bean.objectType);
			matchingLanguage();
			form.render();
		}
	});

	function loadTreeTable() {
		tableTree.render({
			id: 'messageTable',
			elem: '#messageTable',
			data: [],
			// 该参数不能删除，分页参数无效，目前只能通过设置10000来保证当前页最大的数据量
			limit: 10000,
			cols: [[
				{ field: 'name', title: '名称', width: 160 },
				{ field: 'departmentName', title: '部门', width: 120 },
				{ field: 'phone', title: '联系方式', width: 140 },
				{ field: 'email', title: '邮箱', width: 200 }
			]],
			done: function(json) {
				matchingLanguage();
			}
		}, {
			keyId: 'id',
			keyPid: 'pId',
			title: 'name',
			defaultShow: true,
		});
	}

	// 刷新成员树表格
	function reloadTreeTable(objectType) {
		var data = $.extend(true, [], treeTableData);
		tableTree.reload("messageTable", {data: data});
		loadAuthList(objectType);
	}

	function loadAuthList(objectType) {
		// 加载该受用类型的团队可以设置哪些权限
		var colsList = teamObjectPermissionUtil.getAuthColsDetails(objectType);
		$('#authList').html(getDataUseHandlebars($('#authTableTemplate').html(), {list: colsList}));
		$.each(colsList, function (i, item) {
			var data = $.extend(true, [], treeTableData);
			// 给数据设置权限组的key，
			$.each(data, function (j, bean) {
				bean.authGroupKey = item.id;
			});
			loadAuthTreeTable(item.id, item.cols, data);
		});

		$.each(checkTrueList, function (i, id) {
			$(`div[id='${id}']`).html(`<i class="fa fa-check" style="color: green"></i>`);
		});
	}

	function loadAuthTreeTable(id, cols, data) {
		tableTree.render({
			id: id,
			elem: '#' + id,
			data: data,
			// 该参数不能删除，分页参数无效，目前只能通过设置10000来保证当前页最大的数据量
			limit: 10000,
			cols: [cols],
		}, {
			keyId: 'id',
			keyPid: 'pId',
			title: 'name',
			defaultShow: true,
		});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});