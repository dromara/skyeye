
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'opTable', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	var opTable;

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		initTable();
	});

    function initTable(){
		opTable = layui.opTable.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'schoolteachersubject001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'userName', title: '姓名', rowspan: '3', align: 'left', width: 90, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.userName + '</a>';
		        }},
		        { field: 'userPhoto', title: '头像', align: 'center', width: 60, templet: function(d){
		        	if(isNull(d.userPhoto)){
		        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
		        	} else {
		        		return '<img src="' + fileBasePath + d.userPhoto + '" class="photo-img" lay-event="userPhoto">';
		        	}
		        }},
		        { field: 'subjectNum', title: '科目技能数', align: 'center', width: 100 },
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
		        { field: 'schoolName', title: '学校', align: 'left', width: 120},
		        { field: 'phone', title: '手机号', align: 'center', width: 100},
		        { field: 'qq', title: 'QQ', align: 'center', width: 100},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 190, toolbar: '#tableBar'}
		    ]],
		    openNetwork: {
				openCols: [
					{field: 'skillName', title: '技能'}
        		],
        		/**
		         *
		         * @param data 当前行数据
		         * @param success 成功
		         * @param message 显示异常消息[没有数据 出错 等]
		         */
        		onNetwork: function (data, success, message) {
        			var str = "";
        			for(var i = 0; i < data.skill.length; i++){
        				str += '<span class="layui-badge layui-bg-orange" style="margin-right:5px;">' + data.skill[i].subjectName + '</span>';
        			}
        			success({skillName: str})
        		}
		    },
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		layui.table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'subBind') { //科目技能绑定
	        	subBind(data);
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
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	//科目技能绑定
	function subBind(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolteacherSub/schoolteacherSubBind.html", 
			title: "科目技能绑定",
			pageId: "schoolteacherSubBind",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//教师详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolteacher/schoolteacherdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "schoolteacherdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	layui.table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	layui.table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return {
			userName: $("#userName").val(),
			userSex: $("#userSex").val(),
			userIdCard: $("#userIdCard").val(),
			schoolId: $("#schoolId").val()
		};
	}
    
    exports('schoolteacherSublist', {});
});
