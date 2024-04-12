
var clickId = "";//选中的用户组id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tagEditor'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1563451355229');//新增用户
	authBtn('1563451417564');//一键移除用户
	authBtn('1572334408610');//同步人员到工作流
	
	// 初始化左侧菜单用户组数据
	sysDictDataUtil.queryDictDataListByDictTypeCode('ACT_GROUP', function (data) {
		var str = getDataUseHandlebars($('#userGroupTemplate').html(), data);
		$("#setting").html(str);
		if (data.rows.length > 0) {
			clickId = data.rows[0].id;
			$("#setting").find("a[rowid='" + clickId + "']").addClass('selected');
		}
		// 展示用户组对应的用户列表
		showList();
	});

	//展示用户组对应的用户列表
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'actgroup007',
		    where: getTableParams(),
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'userName', title: '员工姓名', align: 'left', width: 120},
		        { field: 'companyName', title: '公司', align: 'left', width: 200 },
		        { field: 'departmentName', title: '部门', align: 'left', width: 150 },
		        { field: 'jobName', title: '职位', align: 'left', width: 200 },
		        { field: 'email', title: '邮箱', align: 'left', width: 200 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 80, toolbar: '#tableBar'}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入员工姓名", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if(layEvent === 'del'){  //移除
	        	del(data);
	        }
	    });
    }

	// 用户组点击事件
	$("body").on("click", ".setting-a-input", function (e) {
		e.stopPropagation();//阻止冒泡
	});
	$("body").on("contextmenu", ".setting-a-input", function (e) {
		e.stopPropagation();//阻止冒泡
	});

	// 对左侧菜单项的点击事件
	$("body").on("click", "#setting a", function (e) {
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		clickId = $(this).attr("rowid");
		loadTable();
	});
	
	// 新增用户
	$("body").on("click", "#addUser", function (e) {
		systemCommonUtil.userReturnList = [];
		systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
		systemCommonUtil.chooseOrNotEmail = "1"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
		systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
			var userInfo = "";
			$.each(userReturnList, function (i, item) {
				userInfo += item.id + ',';
			})
			AjaxPostUtil.request({url: flowableBasePath + "actgroup003", params: {rowId: clickId, userId: userInfo}, type: 'json', callback: function(json) {
				loadTable();
			}});
		});
	});
	
	// 一键移除指定用户组下的所有用户
	$("body").on("click", "#delUser", function (e) {
		if (!isNull(clickId)){
			var msg = '确认一键移除该用户组下的所有用户吗？';
			layer.confirm(msg, { icon: 3, title: '一键移除所有用户' }, function (index) {
				layer.close(index);
				AjaxPostUtil.request({url: flowableBasePath + "actgroup008", params: {rowId: clickId}, type: 'json', method: "POST", callback: function (json) {
					winui.window.msg("移除成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		} else {
			winui.window.msg("没有可移除用户的用户组！", {icon: 2, time: 2000});
		}
	});
	
	// 移除用户
	function del(data) {
		var msg = '确认移除该用户吗？';
		layer.confirm(msg, { icon: 3, title: '删除用户' }, function (index) {
			layer.close(index);
			var params = {
				rowId:  data.id
        	};
            AjaxPostUtil.request({url: flowableBasePath + "actgroup006", params: params, type: 'json', method: "POST", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 同步人员数据
    $("body").on("click", "#syncData", function() {
    	AjaxPostUtil.request({url: flowableBasePath + "activitimode015", params: {}, type: 'json', method: "POST", callback: function (json) {
			winui.window.msg("同步成功", {icon: 1, time: 2000});
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
		return $.extend(true, {groupId: clickId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

	exports('actGroupList', {});
});
