
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'tableTreeDj', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		textool = layui.textool,
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
			json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
		},
		ajaxSendAfter: function (json) {
			skyeyeClassEnumUtil.showEnumDataListByClassName("teamObjectType", 'select', "objectType", json.bean.objectType, form);
			skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", json.bean.enabled, form);

			loadTreeTable();
			// 解析成员信息
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

			reloadTreeTable();

			textool.init({eleId: 'remark', maxlength: 200});
			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var teamRoleList = getTeamRoleList();
					if (teamRoleList.length == 0) {
						winui.window.msg('团队成员不能为空', {icon: 2, time: 2000});
						return false;
					}
					var teamObjectPermissionList = getTeamObjectPermissionList();
					var params = {
						id: parent.rowId,
						name: $("#name").val(),
						objectType: $("#objectType").val(),
						enabled: $("#enabled input:radio:checked").val(),
						remark: $("#remark").val(),
						teamRoleList: JSON.stringify(teamRoleList),
						teamObjectPermissionList: JSON.stringify(teamObjectPermissionList)
					};
					AjaxPostUtil.request({url: reqBasePath + "writeTeamTemplate", params: params, type: 'json', method: "POST", callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
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
				{ field: 'email', title: '邮箱', width: 200 },
				{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar' }
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

		tableTree.getTable().on('tool(messageTable)', function (obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if (layEvent === 'removeRole') {
				// 移除角色和该角色下的用户
				var tmp = [];
				$.each(treeTableData, function(index, item) {
					if (item.id != data.id && item.pId != data.id) {
						tmp.push(item);
					}
				});
				treeTableData = [].concat(tmp);

				restCheckbox();
				reloadTreeTable();
			} else if (layEvent === 'addUser') { // 添加成员
				var roleId = data.id;
				systemCommonUtil.userReturnList = [];
				systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
				systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
				systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
				systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
					var userList = [].concat(userReturnList);
					$.each(userList, function (i, item) {
						// 一个用户在一个团队中只能属于一个角色
						var checkParams = getInPoingArr(treeTableData, "id", item.id);
						if (checkParams == null) {
							item['pId'] = roleId;
							treeTableData.push(item);
						}
					});
					restCheckbox();
					reloadTreeTable();
				});
			} else if (layEvent === 'removeUser') { // 移除成员
				var roleId = data.pId;
				var userId = data.id;
				$.each(treeTableData, function(index, item) {
					if (!isNull(item) && item.pId == roleId && item.id == userId) {
						treeTableData.splice(index, 1);
					}
				});
				restCheckbox();
				reloadTreeTable();
			}
		});
	}

	// 刷新成员树表格
	function reloadTreeTable() {
		var data = $.extend(true, [], treeTableData);
		tableTree.reload("messageTable", {data: data});
		loadAuthList();
	}

	loadAuthList();
	form.on('select(objectType)', function(data) {
		checkTrueList = [];
		loadAuthList();
	});

	function getTeamRoleList() {
		var teamRoleList = [];
		$.each(treeTableData, function (i, item) {
			var teamRole = {};
			if (item.pId == '0') {
				// 角色
				teamRole['roleId'] = item.id;
				// 查询这个角色下的用户
				var roleUserList = [];
				$.each(treeTableData, function (j, bean) {
					if (bean.pId == item.id) {
						roleUserList.push({
							userId: bean.id
						});
					}
				});
				teamRole['teamRoleUserList'] = roleUserList;
				teamRoleList.push(teamRole);
			}
		});
		return teamRoleList;
	}

	function getTeamObjectPermissionList() {
		var checkRow = $("#authList input[type='checkbox']:checked");
		var teamObjectPermissionList = [];
		$.each(checkRow, function (i, item) {
			var id = $(item).attr('id');
			var str = id.split('_');
			var authGroupKey = str[0];
			var authKey = str[1];
			var roleId = str[2];
			var userId = str[3];
			teamObjectPermissionList.push({
				permissionKey: authGroupKey,
				permissionValue: authKey,
				ownerId: isNull(userId) ? roleId : userId,
				ownerKey: isNull(userId) ? sysServiceMation["dictData"]["key"] : sysServiceMation["userInfo"]["key"],
				fromType: 1
			});
		});
		return teamObjectPermissionList;
	}

	function loadAuthList() {
		var objectType = $('#objectType').val();
		// 加载该受用类型的团队可以设置哪些权限
		var colsList = teamObjectPermissionUtil.getAuthCols(objectType);
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
			$(`input[id='${id}']`).prop("checked", true);
		});
		form.render('checkbox');

		form.on('checkbox(checkClick)', function(obj) {
			var id = $(this).attr('id');
			var str = id.split('_');
			var authGroupKey = str[0];
			var authKey = str[1];
			var roleId = str[2];
			var userId = str[3];
			var name = authGroupKey + '_' + authKey + '_' + roleId;
			var _table = $(`div[lay-id='${authGroupKey}']`);
			if (isNull(userId)) {
				// 如果用户id为空，代表点击的是角色的选择box，需要把name是这个的都选中
				if (obj.elem.checked) {
					_table.find(`input[name='${name}']`).prop("checked", true);
				} else {
					_table.find(`input[name='${name}']`).prop("checked", false);
				}
			} else {
				if (obj.elem.checked) {
					if (_table.find(`input[name='${name}']:checked`).length
						== _table.find(`input[name='${name}']`).length - 1) {
						$(`input[id='${name}_']`).prop("checked", true);
					}
				} else {
					$(`input[id='${name}_']`).prop("checked", false);
				}
			}

			restCheckbox();
			form.render('checkbox');
		});
	}

	function restCheckbox() {
		// 将所有选中的重新放入 checkTrueList 中，防止出现操作成员信息时无法回显
		var checkRow = $("#authList input[type='checkbox']:checked");
		checkTrueList = [];
		$.each(checkRow, function (i, item) {
			checkTrueList.push($(item).attr('id'));
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

	$("body").on("click", "#addRole", function() {
		_openNewWindows({
			url: "../../tpl/sysDictData/sysDictDataSelectChoose.html?sysDictType=teamRole",
			title: '选择角色',
			pageId: "sysDictDataSelectChoose",
			area: ['50vw', '50vh'],
			callBack: function (refreshCode, turnData) {
				var checkParams = getInPoingArr(treeTableData, "id", turnData.id);
				if (checkParams == null) {
					turnData["pId"] = '0';
					turnData["lay_is_open"] = true;
					treeTableData.push(turnData);
					reloadTreeTable();
				} else {
					winui.window.msg("角色重复", {icon: 2, time: 2000});
				}
			}});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});