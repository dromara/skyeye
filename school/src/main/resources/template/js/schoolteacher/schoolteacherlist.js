
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

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		initTable();
	});

	// 学校教师列表
    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'schoolteacher001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'userName', title: '姓名', rowspan: '3', align: 'left', width: 150, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.jobNumber + ' ' + d.userName + '</a>';
		        }},
		        { field: 'email', title: '邮箱', align: 'left', width: 170 },
		        { field: 'userPhoto', title: '头像', align: 'center', width: 60, templet: function(d){
		        	if(isNull(d.userPhoto)){
		        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
		        	} else {
		        		return '<img src="' + fileBasePath + d.userPhoto + '" class="photo-img" lay-event="userPhoto">';
		        	}
		        }},
		        { field: 'userIdCard', title: '身份证', align: 'center', width: 160 },
		        { field: 'userSex', title: '性别', width: 60, align: 'center', templet: function(d){
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
		        { field: 'state', title: '状态', width: 60, align: 'center', templet: function(d){
		        	if(d.state == '1'){
		        		return "<span class='state-up'>在职</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-down'>离职</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'schoolName', title: '学校', align: 'left', width: 120},
		        { field: 'phone', title: '手机号', align: 'center', width: 100},
		        { field: 'qq', title: 'QQ', align: 'center', width: 100}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'userPhoto') { //头像预览
	        	layer.open({
	        		type:1,
	        		title:false,
	        		closeBtn:0,
	        		skin: 'demo-class',
	        		shadeClose:true,
	        		content:'<img src="' + fileBasePath + data.userPhoto + '" style="max-height:600px;max-width:100%;">',
	        		scrollbar:false
	            });
	        }else if (layEvent === 'details') { //教师详情
	        	details(data);
	        }
	    });
	    form.render();
    }
	
	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	
	// 教师详情
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
	
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		userName:$("#userName").val(),
			userSex:$("#userSex").val(),
			userIdCard:$("#userIdCard").val(),
			schoolId:$("#schoolId").val()
    	};
	}
    
    exports('schoolteacherlist', {});
});
