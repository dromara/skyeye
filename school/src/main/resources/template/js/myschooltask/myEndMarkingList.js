
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		laydate = layui.laydate;
	
	laydate.render({elem: '#year', type: 'year', max: 'date'});

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		// 加载年级
		initGradeId();
		initTable();
	});
	form.on('select(schoolId)', function(data) {
		//加载年级
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
    	 	ajaxSendLoadBefore: function(hdb) {
    	 	},
    	 	ajaxSendAfter:function (json) {
    	 		form.render('select');
    	 	}
        });
    }

	function initTable(){
		table.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: schoolBasePath + 'myschooltask003',
	        where: getTableParams(),
	        even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
	        cols: [[
	        	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
	        	{ field: 'studentName', rowspan: '2', width: 80, title: '姓名'},
	        	{ field: 'studentNo', rowspan: '2', width: 140, align: 'center', title: '学号'},
		        { field: 'sessionYear', rowspan: '2', width: 80, align: 'center', title: '所属届'},
	            { field: 'schoolName', rowspan: '2', width: 150, title: '学校'},
	            { field: 'gradeName', rowspan: '2', width: 80, align: 'center', title: '年级'},
	            { field: 'surveyName', rowspan: '2', width: 200, title: '试卷名称', templet: function (d) {
			        return '<a lay-event="details" class="notice-title-click">' + d.surveyName + '</a>';
			    }},
            	{ title: '阅卷信息', align: 'center', colspan: '3'},
            	{ title: '答题信息', align: 'center', colspan: '3'}
	        ],[
		    	{ field: 'markStartTime', title: '开始时间', align: 'center', width: 120},
		        { field: 'markEndTime', title: '结束时间', align: 'center', width: 120},
		        { field: 'markFraction', title: '最后得分', align: 'center', width: 100},
		        { field: 'bgAnDate', title: '开始时间', align: 'center', width: 120},
		        { field: 'endAnDate', title: '结束时间', align: 'center', width: 120},
		        { field: 'totalTime', title: '耗时(分钟)', align: 'center', width: 100}
	        ]
		    ],
		    done: function(json) {
		    	matchingLanguage();
		    }
	    });
	    
	    table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
		
	    form.render();
	}
	
	
	form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
    });
    
    //详情
	function details(data) {
		rowId = data.surveyId;
		_openNewWindows({
			url: "../../tpl/examDetail/examPCDetail.html", 
			title: "试卷信息",
			pageId: "examPCDetail",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		gradeId: $("#gradeId").val(), 
    		schoolId: $("#schoolId").val(), 
    		year: $("#year").val(),
    		surveyName: $("#surveyName").val(),
    		studentName: $("#studentName").val(),
    		studentNo: $("#studentNo").val()
    	};
    }
    
    exports('myEndMarkingList', {});
});
