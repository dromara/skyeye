
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
	
	authBtn('1589084814532');
	
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
		 	url: reqBasePath + "schoolsubjectmation007",
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
		
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'knowledgepoints001',
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
		        { field: 'type', width:80, title: '类型', align: 'center', templet: function(d){
		        	if(d.type == 1){
		        		return '<span style="color: blue">' + d.typeName + '</span>';
		        	}else{
		        		return '<span style="color: goldenrod">' + d.typeName + '</span>';
		        	}
		        }},
		        { field: 'schoolName', width: 200, title: '学校'},
	            { field: 'gradeName', width: 80, align: 'center', title: '年级'},
	            { field: 'subjectName', width: 80, align: 'center', title: '科目'},
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
	        }else if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'details') { //详情
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
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "knowledgepoints003", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolKnowledgePoints/schoolKnowledgePointsEdit.html", 
			title: "编辑知识点",
			pageId: "schoolKnowledgePointsEdit",
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
			url: "../../tpl/schoolKnowledgePoints/schoolKnowledgePointsDetails.html", 
			title: "知识点信息",
			pageId: "schoolKnowledgePointsDetails",
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
			url: "../../tpl/schoolKnowledgePoints/schoolKnowledgePointsAdd.html", 
			title: "新增知识点",
			pageId: "schoolKnowledgePointsAdd",
			area: ['90vw', '90vh'],
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
    	table.reload("messageTable", {where: getTablePatams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTablePatams()});
    }
    
    function getTablePatams(){
    	return {
    		title: $("#title").val(), 
    		schoolId: $("#schoolId").val(), 
    		gradeId: $("#gradeId").val(), 
    		subjectId: $("#subjectId").val(),
    		typeState: $("#typeState").val()
    	};
    }
    
    exports('schoolKnowledgePointsList', {});
});
