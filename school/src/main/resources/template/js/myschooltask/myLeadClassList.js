
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
	
    //当前我带领的班级
	function initTable(){
		
		table.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: schoolBasePath + 'myschooltask001',
	        where:{gradeId: $("#gradeId").val(), schoolId: $("#schoolId").val()},
	        even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
	        cols: [[
	        	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'schoolName', width: 200, title: '学校'},
	            { field: 'className', width: 150, title: '班级', align: 'center', templet: function(d){
	        		return d.sessionYear + '届' + d.gradeName + d.className;
		        }},
	            { field: 'limitNumber', width: 80, align: 'center', title: '限制人数', templet: function(d){
	        		return d.limitNumber + '人';
		        }},
		        { field: 'actualNumber', width: 80, align: 'center', title: '实际人数', templet: function(d){
	        		return d.actualNumber + '人';
		        }},
	            { field: 'floorName', width: 120, title: '教学楼'}
	        ]],
		    done: function(){
		    	matchingLanguage();
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
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{gradeId: $("#gradeId").val(), schoolId: $("#schoolId").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{gradeId: $("#gradeId").val(), schoolId: $("#schoolId").val()}});
    }
    
    exports('myLeadClassList', {});
});
