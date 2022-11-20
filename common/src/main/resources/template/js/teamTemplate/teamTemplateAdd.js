
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
	textool.init({eleId: 'remark', maxlength: 200});
	var treeTableData = [];

	skyeyeClassEnumUtil.showEnumDataListByClassName("teamObjectType", 'select', "objectType", '', form);
	skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);

	loadTreeTable();
	function loadTreeTable() {
		tableTree.render({
			id: 'messageTable',
			elem: '#messageTable',
			data: [{"id": "123qwe", "name": "阿萨斯多", "pId": "0", "lay_is_open": true},
				   {"id": "123qwe1", "name": "阿萨斯多qqq", "pId": "123qwe", "lay_is_open": true}],
			cols: [[
				{ field: 'name', title: '名称', width: 120 },
				{ field: 'menuNameEn', title: '部门', width: 150 },
				{ field: 'orderNum', title: '邮箱', width: 150 },
				{ field: 'desktopName', title: '联系方式', width: 120 },
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
			if (layEvent === 'removeRole') { // 移除角色
				$.each(treeTableData, function(index, item) {
					if (item.id == data.id) {
						treeTableData.splice(index, 1);
					}
				});
			} else if (layEvent === 'addUser') { // 添加成员
				systemCommonUtil.userReturnList = [];
				systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
				systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
				systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
				systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
					console.log(userReturnList)
				});
			} else if (layEvent === 'removeUser') { // 移除成员

			}
		});
	}

	loadAuthList();
	form.on('select(objectType)', function(data) {
		loadAuthList();
	});

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				dictName: $("#dictName").val(),
				dictCode: $("#dictCode").val(),
				enabled: $("#enabled input:radio:checked").val(),
				remark: $("#remark").val(),
			};
			AjaxPostUtil.request({url: reqBasePath + "writeDictTypeMation", params: params, type: 'json', method: "POST", callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	function loadAuthList() {
		var objectType = $('#objectType').val();
		teamObjectPermissionUtil.insertPageShow(objectType, 'authList', form);
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
					tableTree.reload("messageTable", {data: treeTableData});
				} else {
					winui.window.msg("角色重复", {icon: 2, time: 2000});
				}
			}});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});