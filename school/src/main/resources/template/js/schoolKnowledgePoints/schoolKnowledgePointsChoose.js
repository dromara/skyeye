
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
		
	var checkType = '1';//知识点选择类型：1.单选；2.多选
	
	if (!isNull(parent.schoolKnowledgeCheckType)){
		checkType = parent.schoolKnowledgeCheckType;
	}
	
	//设置提示信息
	var s = "知识点选择规则：";
	if(checkType == "1"){
		s += '1.单选，双击指定行数据即可选中；';
	} else {
		s += '1.多选；';
		//显示保存按钮
		$("#saveCheckBox").show();
	}
	s += '如没有查到要选择的知识点，请检查知识点信息是否满足当前规则。';
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
    
    form.on('select(gradeId)', function(data) {
		if(isNull(data.value) || data.value === '请选择'){
			$("#subjectId").html("");
			form.render('select');
		} else {
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
		 	ajaxSendLoadBefore: function(hdb) {},
		 	ajaxSendAfter:function (json) {
		 		form.render('select');
		 	}
	    });
	}
	
	function initTable(){
		if(checkType == '2'){
			//初始化值
			var ids = [];
			$.each(parent.schoolKnowledgeMationList, function(i, item){
				ids.push(item.id);
			});
			tableCheckBoxUtil.setIds({
				gridId: 'messageTable',
				fieldName: 'id',
				ids: ids
			});
			tableCheckBoxUtil.init({
				gridId: 'messageTable',
				filterId: 'messageTable',
				fieldName: 'id'
			});
		}

		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'knowledgepoints007',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: checkType == '1' ? 'radio' : 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'title', width:250, title: '标题', templet: function (d) {
			        return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			    }},
		        { field: 'type', width:80, title: '类型', align: 'center', templet: function (d) {
		        	if(d.type == 1){
		        		return '<span style="color: blue">' + d.typeName + '</span>';
		        	} else {
		        		return '<span style="color: goldenrod">' + d.typeName + '</span>';
		        	}
		        }},
		        { field: 'schoolName', width: 200, title: '学校'},
	            { field: 'gradeName', width: 80, align: 'center', title: '年级'},
	            { field: 'subjectName', width: 80, align: 'center', title: '科目'},
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 }
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	if(checkType == '1'){
			    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
						var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						dubClick.find("input[type='radio']").prop("checked", true);
						form.render();
						var chooseIndex = JSON.stringify(dubClick.data('index'));
						var obj = res.rows[chooseIndex];
						parent.schoolKnowledgeMation = obj;
						
						parent.refreshCode = '0';
						parent.layer.close(index);
					});
					
					$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
						var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						click.find("input[type='radio']").prop("checked", true);
						form.render();
					})
		    	} else {
		    		//多选
		    		//设置选中
		    		tableCheckBoxUtil.checkedDefault({
						gridId: 'messageTable',
						fieldName: 'id'
					});
		    	}
		    }
		});
		
		form.render();
	}
	
	//保存按钮-多选才有
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		AjaxPostUtil.request({url:schoolBasePath + "knowledgepoints008", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
			parent.schoolKnowledgeMationList = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
   		}});
	});
	
	form.render();
	form.on('submit(formSearch)', function (data) {
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
			title: $("#title").val(), 
    		schoolId: $("#schoolId").val(), 
    		gradeId: $("#gradeId").val(), 
    		subjectId: $("#subjectId").val(),
    		typeState: $("#typeState").val()
		};
	}
	
    exports('schoolKnowledgePointsChoose', {});
});