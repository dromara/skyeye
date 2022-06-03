
var rowId = "";

var surveyName = "";

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
	
	authBtn('1587278427992');
	
	laydate.render({
		elem: '#year', //指定元素
		type: 'year',
		max: 'date'
	});
	
	//初始化学校
	showGrid({
		id: "schoolId",
		url: schoolBasePath + "schoolmation008",
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
	
	form.on('select(schoolId)', function(data){
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
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		form.render('select');
    	 	}
        });
    }
    
    form.on('select(gradeId)', function(data){
		if(isNull(data.value) || data.value === '请选择'){
			$("#subjectId").html("");
			form.render('select');
		}else{
			//加载科目
			initSubject();
		}
	});
	
	//初始化科目
	function initSubject(){
		showGrid({
		 	id: "subjectId",
		 	url: schoolBasePath + "schoolsubjectmation007",
		 	params: {gradeId: $("#gradeId").val()},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	}
	
    function initTable(){
    	var params = {
    		surveyName: $("#surveyName").val(), 
    		surveyState: $("#surveyState").val(), 
    		gradeId: $("#gradeId").val(), 
    		schoolId: $("#schoolId").val(), 
    		year: $("#year").val(),
    		subjectId: $("#subjectId").val()
    	};
		
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'exam001-my',
		    where: params,
		    even:true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'surveyName', width:250, title: '试卷名称', templet: function(d){
			        return '<a lay-event="details" class="notice-title-click">' + d.surveyName + '</a>';
			    }},
		        { field: 'surveyState', width:80, title: '状态', templet: function(d){
		        	if(d.state == 1){
			        	return d.surveyState + '<i class="fa fa-pencil-square fa-fw cursor vary-color" lay-event="pcExaming" title="点击前往考试"></i>';
		        	}else{
		        		return d.surveyState;
		        	}
		        }},
		        { field: 'schoolName', width: 200, title: '所属学校'},
	            { field: 'gradeName', width: 80, align: 'center', title: '所属年级'},
	            { field: 'subjectName', width: 80, align: 'center', title: '科目'},
	            { field: 'sessionYear', width: 80, align: 'center', title: '所属届'},
		        { field: 'userName', width: 120, title: systemLanguage["com.skyeye.createName"][languageType], align: 'left'},
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'del') { //删除
	        	del(data, obj);
	        }else if (layEvent === 'edit') { //设计
	        	edit(data);
	        }else if (layEvent === 'fzWj') { //复制试卷
	        	fzWj(data);
	        }else if (layEvent === 'fxWj') { //分析报告
	        	fxWj(data);
	        }else if (layEvent === 'showFb') { //发布
	        	showFb(data, obj);
	        }else if (layEvent === 'endSurvey') { //结束调查
	        	endSurvey(data, obj);
	        }else if (layEvent === 'details') { //详情
	        	details(data);
	        }else if (layEvent === 'markExam') { //阅卷人
	        	markExam(data);
	        }
	    });
		
		form.render();
		form.on('submit(formSearch)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
	        	refreshTable();
	        }
	        return false;
		});
    }
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除试卷【' + obj.data.surveyName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除试卷' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: schoolBasePath + "exam025", params:{rowId: data.id}, type: 'json', callback: function(json) {
    			if(json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//设计
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/examdesign/examDesign.html", 
			title: "设计试卷",
			pageId: "examDesign",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//复制试卷
	function fzWj(data){
		rowId = data.id;
		surveyName = data.surveyName;
		_openNewWindows({
			url: "../../tpl/examdesigncopy/examDesignCopy.html", 
			title: "复制试卷",
			pageId: "examDesignCopy",
			area: ['500px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//分析报告
	function fxWj(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/examreport/examReport.html", 
			title: "分析报告",
			pageId: "examReport",
			maxmin: true,
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//发布
	function showFb(data, obj){
		var msg = obj ? '确认发布试卷【' + obj.data.surveyName + '】吗？' : '确认发布选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '试卷发布' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:schoolBasePath + "exam023", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("发布成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//结束调查
	function endSurvey(data, obj){
		var msg = obj ? '确认结束试卷【' + obj.data.surveyName + '】的考试吗？' : '确认结束选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '结束考试' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:schoolBasePath + "exam030", params:{surveyId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/examDetail/examPCDetail.html", 
			title: "试卷信息",
			pageId: "examPCDetail",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//阅卷人
	function markExam(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/markExam/markExamPeople.html", 
			title: "阅卷人",
			pageId: "markExamPeople",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/examdesign/examDesignAdd.html", 
			title: "新增试卷",
			pageId: "examDesignAdd",
			area: ['70vw', '60vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	var params = {
    		surveyName: $("#surveyName").val(), 
    		surveyState: $("#surveyState").val(), 
    		gradeId: $("#gradeId").val(), 
    		schoolId: $("#schoolId").val(), 
    		year: $("#year").val(),
    		subjectId: $("#subjectId").val()
    	};
    	table.reload("messageTable", {where: params});
    }
    
    function refreshTable(){
    	var params = {
    		surveyName: $("#surveyName").val(), 
    		surveyState: $("#surveyState").val(), 
    		gradeId: $("#gradeId").val(), 
    		schoolId: $("#schoolId").val(), 
    		year: $("#year").val(),
    		subjectId: $("#subjectId").val()
    	};
    	table.reload("messageTable", {page: {curr: 1}, where: params});
    }
    
    exports('examDesignMyList', {});
});
