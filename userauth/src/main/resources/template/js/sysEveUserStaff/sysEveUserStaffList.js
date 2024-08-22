
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;

	authBtn('1555562812681');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'querySysUserStaffList',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    overflow: {
            type: 'tips',
            header: true,
            total: true
        },
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', fixed: 'left', type: 'numbers' },
	        { field: 'userName', title: '姓名', rowspan: '3', align: 'left', width: 100, fixed: 'left', templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.userName + '</a>';
	        }},
			{ field: 'jobNumber', title: '工号', rowspan: '3', align: 'left', width: 100, fixed: 'left'},
	        { field: 'type', title: '类型', rowspan: '3', align: 'left', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("userStaffType", 'id', d.type, 'name');
			}},
	        { field: 'email', title: '邮箱', rowspan: '3', align: 'left', width: 170 },
	        { field: 'userPhoto', title: '头像', rowspan: '3', align: 'center', width: 60, templet: function (d) {
				if (isNull(d.userPhoto)) {
					return '<img src="../../assets/images/os_windows.png" class="photo-img">';
				} else {
					return '<img src="' + systemCommonUtil.getFilePath(d.userPhoto) + '" class="photo-img" lay-event="userPhoto">';
				}
	        }},
			{ field: 'userId', title: '系统账号', rowspan: '3', align: 'center', width: 80, templet: function (d) {
				if (!isNull(d.userId)){
					return "<span class='state-up'>已分配</span>";
				} else {
					return "<span class='state-down'>未分配</span>";
				}
			}},
	        { field: 'userIdCard', title: '身份证', rowspan: '3', align: 'center', width: 160 },
	        { field: 'userSex', title: '性别', width: 80, rowspan: '3', align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("sexEnum", 'id', d.userSex, 'name');
			}},
	        { field: 'state', title: '状态', rowspan: '3', width: 60, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("userStaffState", 'id', d.state, 'name');
	        }},
	        { title: '公司信息', align: 'center', colspan: '3'},
	        { field: 'phone', title: '手机号', rowspan: '3', align: 'center', width: 100},
	        { field: 'homePhone', title: '家庭电话', rowspan: '3', align: 'center', width: 100},
	        { field: 'qq', title: 'QQ', rowspan: '3', align: 'left', width: 100},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '3', width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '3', align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '3', align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '3', align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '3', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
	    ],[
	    	{ field: 'companyName', title: '公司', align: 'left', width: 120},
	        { field: 'departmentName', title: '部门', align: 'left', width: 120},
	        { field: 'jobName', title: '职位', align: 'left', width: 120}
	       ]
	    ],
	    done: function(json) {
	    	soulTable.render(this);
    		matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入员工姓名、员工工号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'userPhoto') { //头像预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.userPhoto));
		} else if (layEvent === 'details') { //员工详情
			details(data);
		} else if (layEvent === 'leave') { //离职
			leave(data);
		} else if (layEvent === 'turnTeacher') { //转教职工
			turnTeacher(data);
		} else if (layEvent === 'addCertificate') { //录入证书
			addCertificate(data);
		} else if (layEvent === 'addEducation') { //录入教育背景
			addEducation(data);
		}
    });
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveUserStaff/sysEveUserStaffEdit.html",
			title: "编辑员工",
			pageId: "sysEveUserStaffEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 员工详情
	function details(data) {
		_openNewWindows({
			url: "../../tpl/sysEveUserStaff/userStaffManage.html?objectId=" + data.id + "&objectKey=" + data.serviceClassName,
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysEveUserStaffDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 离职
	function leave(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveUserStaff/sysEveUserStaffLeave.html",
			title: "员工离职",
			pageId: "sysEveUserStaffLeave",
			area: ['50vw', '55vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 转教职工
	function turnTeacher(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveUserStaff/turnTeacher.html",
			title: "转教职工",
			pageId: "turnTeacher",
			area: ['50vw', '55vh'],
			callBack: function (refreshCode) {
				winui.window.msg("操作成功，请通知该员工重新登录帐号。", {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 新增员工
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysEveUserStaff/sysEveUserStaffAdd.html?type=1",
			title: "新增员工",
			pageId: "sysEveUserStaffAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
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
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('sysEveUserStaffList', {});
});
