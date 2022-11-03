
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

	authBtn('1552960199302');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sys001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', fixed: 'left' },
	        { field: 'userCode', title: '账号', width: 120, fixed: 'left' },
			{ field: 'userName', title: '姓名', align: 'left', width: 100, fixed: 'left', templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.userName + '</a>';
			}},
			{ field: 'jobNumber', title: '工号', align: 'left', width: 100, fixed: 'left'},
	        { field: 'email', title: '邮箱', width: 100 },
	        { field: 'sexName', title: '性别', width: 60, templet: function (d) {
	        	if(d.sexName == '0') {
	        		return "保密";
	        	} else if (d.sexName == '1'){
	        		return "男";
	        	} else if (d.sexName == '2'){
	        		return "女";
	        	} else {
	        		return "参数错误";
	        	}
	        }},
	        { field: 'companyName', title: '所属公司', width: 150 },
	        { field: 'departmentName', title: '所属部门', width: 120},
	        { field: 'jobName', title: '担任职位', width: 120},
	        { field: 'userLock', title: '是否锁定', align: 'center', width: 90, templet: '#checkboxTpl', unresize: true},
	        { field: 'roleName', title: '角色', width: 120},
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入账号、员工姓名、员工工号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'unlock') { // 解锁
			unlock(data);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'bindRole') { // 绑定角色
			bindRole(data);
		} else if (layEvent === 'userPhoto') { // 头像预览
			systemCommonUtil.showPicImg(fileBasePath + data.userPhoto);
		} else if (layEvent === 'details') { // 员工详情
			details(data);
		}
    });
	
	form.render();

	// 监听锁定操作
	form.on('checkbox(lockDemo)', function(obj) {
		if(obj.elem.checked){
			// 锁定
			lock(obj.value);
		} else {
			// 解锁
			unlock(obj.value);
		}
	});
	
	// 锁定
	function lock(id){
		AjaxPostUtil.request({url: reqBasePath + "sys002", params: {rowId: id}, type: 'json', method: "PUT", callback: function (json) {
			winui.window.msg("已成功锁定，该账号目前无法登录.", {icon: 1, time: 2000});
		}});
	}
	
	// 解锁
	function unlock(id){
		AjaxPostUtil.request({url: reqBasePath + "sys003", params: {rowId: id}, type: 'json', method: "PUT", callback: function (json) {
			winui.window.msg("账号恢复正常.", {icon: 1, time: 2000});
		}});
	}
	
	// 重置密码
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveUser/sysEveUserPasswordEdit.html",
			title: "重置密码",
			pageId: "sysEveUserPasswordEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 绑定角色
	function bindRole(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveUser/sysEveUserRoleBind.html",
			title: "绑定角色",
			pageId: "sysEveUserRoleBind",
			area: ['450px', '300px'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 添加用户
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysEveUser/sysEveUserAdd.html",
			title: "新增用户",
			pageId: "sysEveUserAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    // 员工详情
	function details(data) {
		rowId = data.staffId;
		_openNewWindows({
			url: "../../tpl/sysEveUserStaff/sysEveUserStaffDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysEveUserStaffDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('sysEveUserList', {});
});
