
var companyId = "";
var departmentId = "";
var jobId = "";
var rowId = "";

var staffId = "";

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
	
	var bodyList = new Array();
	if(auth('1600612678928')){
		bodyList.push({name: '录入证书', icon: 'fa fa-certificate', click: function(obj) { addCertificate(obj.row); }});
	}
	if(auth('1601106597111')){
		bodyList.push({name: '录入教育背景', icon: 'fa fa-stack-overflow', click: function(obj) { addEducation(obj.row); }});
	}
	if(auth('1601542957698')){
		bodyList.push({name: '录入家庭成员', icon: 'fa fa-home', click: function(obj) { addFamily(obj.row); }});
	}
	if(auth('1601612400686')){
		bodyList.push({name: '录入工作履历', icon: 'fa fa-list-alt', click: function(obj) { addJobResume(obj.row); }});
	}
	if(auth('1601628157655')){
		bodyList.push({name: '录入语言能力', icon: 'fa fa-language', click: function(obj) { addLanguage(obj.row); }});
	}
	if(auth('1601634348149')){
		bodyList.push({name: '录入合同信息', icon: 'fa fa-file-contract', click: function(obj) { addContract(obj.row); }});
	}
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'staff001',
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
        contextmenu: {
        	body: bodyList
        },
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', fixed: 'left', type: 'numbers'},
	        { field: 'userName', title: '姓名', rowspan: '3', align: 'left', width: 100, fixed: 'left', templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.userName + '</a>';
	        }},
			{ field: 'jobNumber', title: '工号', rowspan: '3', align: 'left', width: 100, fixed: 'left'},
	        { field: 'staffType', title: '类型', rowspan: '3', align: 'left', width: 90, templet: function (d) {
	        	if(d.staffType == 1){
	        		return '普通员工';
	        	}else if(d.staffType == 2){
	        		return '教职工';
	        	} else {
	        		return '参数错误';
	        	}
	        }},
	        { field: 'email', title: '邮箱', rowspan: '3', align: 'left', width: 170 },
	        { field: 'userPhoto', title: '头像', rowspan: '3', align: 'center', width: 60, templet: function (d) {
	        	if(isNull(d.userPhoto)){
	        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
	        	} else {
	        		return '<img src="' + systemCommonUtil.getFilePath(d.userPhoto) + '" class="photo-img" lay-event="userPhoto">';
	        	}
	        }},
			{ field: 'userId', title: '系统账号', rowspan: '3', align: 'center', width: 80, templet: function (d) {
				if(!isNull(d.userId)){
					return "<span class='state-up'>已分配</span>";
				} else {
					return "<span class='state-down'>未分配</span>";
				}
			}},
	        { field: 'userIdCard', title: '身份证', rowspan: '3', align: 'center', width: 160 },
			{ field: 'workTimeNum', title: '考勤班次', rowspan: '3', align: 'center', width: 80 },
	        { field: 'userSex', title: '性别', width: 80, rowspan: '3', align: 'center', templet: function (d) {
	        	if(d.userSex == '0'){
	        		return "保密";
	        	}else if(d.userSex == '1'){
	        		return "男";
	        	}else if(d.userSex == '2'){
	        		return "女";
	        	} else {
	        		return "参数错误";
	        	}
	        }},
	        { field: 'state', title: '状态', rowspan: '3', width: 60, align: 'center', templet: function (d) {
				return getStaffStateName(d);
	        }},
	        { title: '公司信息', align: 'center', colspan: '3'},
	        { field: 'phone', title: '手机号', rowspan: '3', align: 'center', width: 100},
	        { field: 'homePhone', title: '家庭电话', rowspan: '3', align: 'center', width: 100},
	        { field: 'qq', title: 'QQ', rowspan: '3', align: 'left', width: 100},
	        { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '3', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
	    ],[
	    	{ field: 'companyName', title: '公司', align: 'left', width: 120},
	        { field: 'departmentName', title: '部门', align: 'left', width: 120},
	        { field: 'jobName', title: '职位', align: 'left', width: 120}
	       ]
	    ],
	    done: function(json){
	    	soulTable.render(this);
    		matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入员工姓名、员工工号", function () {
				table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
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
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuserstaff/sysEveUserStaffEdit.html",
			title: "编辑员工",
			pageId: "sysEveUserStaffEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 员工详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuserstaff/sysEveUserStaffDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysEveUserStaffDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//离职
	function leave(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuserstaff/syseveuserstaffleave.html", 
			title: "员工离职",
			pageId: "syseveuserstaffleave",
			area: ['50vw', '55vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 转教职工
	function turnTeacher(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuserstaff/turnTeacher.html", 
			title: "转教职工",
			pageId: "syseveuserstaffleave",
			area: ['50vw', '55vh'],
			callBack: function(refreshCode){
				winui.window.msg("操作成功，请通知该员工重新登录帐号。", {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 录入证书
	function addCertificate(data){
    	staffId = data.id;
    	_openNewWindows({
			url: "../../tpl/sysStaffCertificate/sysStaffCertificateAdd.html",
			title: "录入证书",
			pageId: "sysStaffCertificateAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }
    
    // 录入教育背景
	function addEducation(data){
    	staffId = data.id;
    	_openNewWindows({
			url: "../../tpl/sysStaffEducation/sysStaffEducationAdd.html",
			title: "录入教育背景",
			pageId: "sysStaffEducationAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }
    
    // 录入家庭成员
	function addFamily(data){
    	staffId = data.id;
    	_openNewWindows({
			url: "../../tpl/sysStaffFamily/sysStaffFamilyAdd.html",
			title: "录入家庭成员",
			pageId: "sysStaffFamilyAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }
    
    // 录入工作履历
	function addJobResume(data){
    	staffId = data.id;
    	_openNewWindows({
			url: "../../tpl/sysStaffJobResume/sysStaffJobResumeAdd.html",
			title: "录入工作履历",
			pageId: "sysStaffJobResumeAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }
    
    // 录入语言能力
	function addLanguage(data){
    	staffId = data.id;
    	_openNewWindows({
			url: "../../tpl/sysStaffLanguage/sysStaffLanguageAdd.html",
			title: "录入语言能力",
			pageId: "sysStaffLanguageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }
    
    // 录入合同信息
	function addContract(data){
    	staffId = data.id;
    	_openNewWindows({
			url: "../../tpl/sysStaffContract/sysStaffContractAdd.html",
			title: "录入合同信息",
			pageId: "sysStaffContractAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }

	// 新增员工
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/syseveuserstaff/sysEveUserStaffAdd.html?type=1",
			title: "新增员工",
			pageId: "sysEveUserStaffAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	form.render();

    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('syseveuserstafflist', {});
});
