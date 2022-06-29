
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
	
	authBtn('1595157157794');

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		// 加载年级
		initGradeId();
		initTable();
	});
	// 学校监听事件
	form.on('select(schoolId)', function(data){
		// 加载年级
 		initGradeId();
	});
	
	//所属年级
    function initGradeId(){
	    showGrid({
    	 	id: "gradeId",
    	 	url: schoolBasePath + "grademation006",
    	 	params: {schoolId: $("#schoolId").val()},
    	 	pagination: false,
    	 	template: getFileContent('tpl/template/select-option.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function (json) {
    	 		form.render('select');
    	 	}
        });
    }
    
    //年级监听事件
	form.on('select(gradeId)', function(data){
		if(isNull(data.value) || data.value === '请选择'){
	 		$("#classId").html("");
			form.render('select');
		} else {
			//加载班级
			loadThisGradeNowYear();
		}
	});
	
	//加载当前选中的年级是哪一届的以及这一届的班级信息
	function loadThisGradeNowYear(){
		showGrid({
		 	id: "classId",
		 	url: schoolBasePath + "grademation009",
		 	params: {gradeId: $("#gradeId").val()},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(data){
		 		form.render('select');
		 	},
		 	ajaxSendErrorAfter: function (json) {
		 		$("#classId").html("");
		 		form.render('select');
		 	}
	    });
	}

	function initTable(){
		table.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: schoolBasePath + 'studentmation001',
	        where: getTableParams(),
	        even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
	        cols: [[
	        	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'studentName', width: 100, title: '学生姓名', templet: function (d) {
			        return '<a lay-event="details" class="notice-title-click">' + d.studentName + '</a>';
			    }},
	            { field: 'studentNo', width: 140, title: '学号'},
		        { field: 'stuSex', width: 60, align: 'center', title: '性别'},
	            { field: 'nation', width: 80, title: '民族'},
	            { field: 'schoolName', width: 200, title: '所在学校'},
	            { field: 'gradeName', width: 100, title: '所在年级'},
	            { field: 'className', width: 100, title: '所在班级'},
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
	        if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
	    form.render();
	}
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolstudentmation/schoolStudentEdit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "schoolStudentEdit",
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
	
	// 详情
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
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    // 下载模板
    $("body").on("click", "#exportStudentModel", function() {
    	postDownLoadFile({
			url : schoolBasePath + 'studentmation009',
			params: getTableParams(),
			method : 'post'
		});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		gradeId: $("#gradeId").val(), 
    		schoolId: $("#schoolId").val(), 
    		classId: $("#classId").val(), 
    		studentName: $("#studentName").val()
    	};
    }
    
    exports('schoolStudentInSchooLlist', {});
});
