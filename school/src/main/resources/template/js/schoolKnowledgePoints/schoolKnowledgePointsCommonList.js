
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
		
	var gradeId = "";
	var subjectId = "";
	
	// 初始化学校
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
	
	form.on('select(schoolId)', function(data){
		// 加载年级
 		initGradeId();
 		gradeId = "";
		subjectId = "";
 		$("#setting1").html("");
	});
	
	// 所属年级
    function initGradeId(){
	    showGrid({
    	 	id: "setting",
    	 	url: reqBasePath + "grademation006",
    	 	params: {schoolId: $("#schoolId").val()},
    	 	pagination: false,
    	 	template: $("#gradeTemplate").html(),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		form.render('select');
    	 	}
        });
    }
    
	// 初始化科目
	function initSubject(){
		showGrid({
		 	id: "setting1",
		 	url: reqBasePath + "schoolsubjectmation007",
		 	params: {gradeId: gradeId},
		 	pagination: false,
		 	template: $("#subjectTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	}
	
    function initTable(){
		// 表格渲染
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'knowledgepoints009',
		    where: getTablePatams(),
		    even:true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', width:250, title: '标题', templet: function(d){
			        return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			    }},
		        { field: 'schoolName', width: 200, title: '学校'},
	            { field: 'gradeName', width: 80, align: 'center', title: '年级'},
	            { field: 'subjectName', width: 80, align: 'center', title: '科目'},
		        { field: 'createTime', title: '创建时间', align: 'center', width: 140 }
		    ]],
		    done: function(){
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
		form.on('submit(formSearch)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
	        	refreshTable();
	        }
	        return false;
		});
    }
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolKnowledgePoints/schoolKnowledgePointsDetails.html", 
			title: "知识点信息",
			pageId: "schoolKnowledgePointsDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
    // 对左侧菜单项的点击事件--年级
	$("body").on("click", "#setting a", function(e){
		$("#setting a").removeClass("selected");
		$(this).addClass("selected");
		gradeId = $(this).attr("rowid");
		if(isNull(gradeId)){
			$("#setting1").html("");
		}else{
			initSubject();
		}
		refreshTable();
	});
	
	// 对左侧菜单项的点击事件--科目
	$("body").on("click", "#setting1 a", function(e){
		$("#setting1 a").removeClass("selected");
		$(this).addClass("selected");
		subjectId = $(this).attr("rowid");
		refreshTable();
	});
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTablePatams()});
    }
    
    function getTablePatams(){
    	return {
    		schoolId: $("#schoolId").val(), 
    		gradeId: gradeId, 
    		subjectId: subjectId
    	};
    }
    
    exports('schoolKnowledgePointsCommonList', {});
});
