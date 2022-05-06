
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
	
	//初始化学校
	showGrid({
		id: "schoolId",
		url: reqBasePath + "schoolmation008",
		params: {},
		pagination: false,
		template: getFileContent('tpl/template/select-option-must.tpl'),
		ajaxSendAfter: function(json){
			form.render('select');
			//加载年级
 			initGradeId();
			initTable();
		}
	});
	//学校监听事件
	form.on('select(schoolId)', function(data){
		//加载年级
 		initGradeId();
	});
	
	//所属年级
    function initGradeId(){
	    showGrid({
    	 	id: "gradeId",
    	 	url: reqBasePath + "grademation006",
    	 	params: {schoolId: $("#schoolId").val()},
    	 	pagination: false,
    	 	template: getFileContent('tpl/template/select-option.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		form.render('select');
    	 	}
        });
    }
    
	function initTable(){
		
		table.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: reqBasePath + 'studentmation003',
	        where:{gradeId: $("#gradeId").val(), schoolId: $("#schoolId").val(), studentName: $("#studentName").val()},
	        even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
	        cols: [[
	        	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'studentName', width: 100, title: '学生姓名', templet: function(d){
			        return '<a lay-event="details" class="notice-title-click">' + d.studentName + '</a>';
			    }},
	            { field: 'studentNo', width: 140, title: '学号'},
		        { field: 'stuSex', width: 60, align: 'center', title: '性别'},
	            { field: 'nation', width: 80, title: '民族'},
	            { field: 'schoolName', width: 200, title: '所在学校'},
	            { field: 'gradeName', width: 100, title: '所在年级'},
	            { field: 'sessionYear', width: 80, align: 'center', title: '所属届'},
	            { field: 'stuType', width: 100, title: '学生类型'},
	            { field: 'residenceTypeName', width: 100, title: '户口类型'},
		        { field: 'guardian', width: 80, title: '监护人'},
	            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	        ]],
		    done: function(){
		    	matchingLanguage();
		    }
	    });
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'assignmentClass') { //分班
	        	assignmentClass(data);
	        }else if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
	    form.render();
	}
	
	
	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	
	//分班
	function assignmentClass(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolstudentmation/assignmentClass.html", 
			title: "分班",
			pageId: "assignmentClass",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolstudentmation/schoolStudentDetail.html", 
			title: "学生信息",
			pageId: "schoolStudentDetail",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{gradeId: $("#gradeId").val(), schoolId: $("#schoolId").val(), studentName: $("#studentName").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{gradeId: $("#gradeId").val(), schoolId: $("#schoolId").val(), studentName: $("#studentName").val()}});
    }
    
    exports('schoolNotInClassStudentInSchooLlist', {});
});
