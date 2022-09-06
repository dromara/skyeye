var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;
	
	// 选择的试题列表
	var questionMationList = [].concat(parent.questionMationList);
	
	// 设置提示信息
	var s = "试题选择规则：1.多选；2.包含所有公开试题以及个人的私有试题。如没有查到要选择的试题，请检查试题信息是否满足当前规则。";
	$("#showInfo").html(s);

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		// 加载年级
		initGradeId();
		initTable();
	});

	form.on('select(schoolId)', function(data) {
		// 加载年级
 		initGradeId();
	});
	
	// 所属年级
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
    
    form.on('select(gradeId)', function(data) {
		if(isNull(data.value) || data.value === '请选择'){
			$("#subjectId").html("");
			form.render('select');
		} else {
			// 加载科目
			initSubject();
		}
	});
	
	// 初始化科目
	function initSubject(){
		showGrid({
		 	id: "subjectId",
		 	url: schoolBasePath + "schoolsubjectmation007",
		 	params: {gradeId: $("#gradeId").val()},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {},
		 	ajaxSendAfter:function (json) {
		 		form.render('select');
		 	}
	    });
	}
	
	function initTable(){
		// 初始化值
		var ids = [];
		$.each(questionMationList, function(i, item) {
			ids.push(item.quInBankId);
		});
		tableCheckBoxUtil.setIds({
			gridId: 'messageTable',
			fieldName: 'quInBankId',
			ids: ids
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'quInBankId'
		});
			
		
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'schoolquestionbank017',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		    	{ type: 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'quTitle', width:250, title: '题目', templet: function (d) {
			        return d.quTitle;
			    }},
		        { field: 'type', width:80, title: '类型', align: 'center', templet: function (d) {
		        	if(d.type == 1){
		        		return '<span style="color: blue">' + d.typeName + '</span>';
		        	} else {
		        		return '<span style="color: goldenrod">' + d.typeName + '</span>';
		        	}
		        }},
		        { field: 'cName', width: 100, title: '题型'},
		        { field: 'schoolName', width: 150, title: '学校'},
	            { field: 'gradeName', width: 80, align: 'center', title: '年级'},
	            { field: 'subjectName', width: 80, align: 'center', title: '科目'},
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 }
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
	    		//设置选中
	    		tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'quInBankId'
				});
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
	
	// 详情
	function details(data) {
		rowId = data.productId;
		_openNewWindows({
			url: "", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 保存
	$("body").on("click", "#saveChoose", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择试题", {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url:schoolBasePath + "schoolquestionbank018", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
			parent.questionMationList = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
   		}});
	});
	
	// 搜索表单
    form.render();
    form.on('submit(formSearch)', function (data) {
        // 表单验证
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });
	
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
			quTitle: $("#quTitle").val(), 
    		schoolId: $("#schoolId").val(), 
    		gradeId: $("#gradeId").val(), 
    		subjectId: $("#subjectId").val(),
    		type: $("#type").val(),
    		quType: $("#quType").val()
		};
	}
	
    exports('schoolQuestionBankChoose', {});
});