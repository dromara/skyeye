
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
	
	authBtn('1586519721272');
	
	laydate.render({elem: '#year', type: 'year', max: 'date'});

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		// 加载年级
		initGradeId();
		initTable();
	});
	form.on('select(schoolId)', function(data){
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
    	 	ajaxSendLoadBefore: function(hdb){
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
	        url: schoolBasePath + 'classmation001',
	        where: getTableParams(),
	        even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
	        cols: [[
	        	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'schoolName', width: 200, title: '所属学校'},
	            { field: 'className', width: 150, title: '班级', align: 'center', templet: function (d) {
	        		return d.year + '届' + d.gradeName + d.className;
		        }},
	            { field: 'limitNumber', width: 80, align: 'center', title: '限制人数', templet: function (d) {
	        		return d.limitNumber + '人';
		        }},
		        { field: 'actualNumber', width: 80, align: 'center', title: '实际人数', templet: function (d) {
	        		return d.actualNumber + '人';
		        }},
	            { field: 'stateName', width: 80, align: 'center', title: '类型', templet: function (d) {
		        	if(d.state == 1){
		        		return '<span style="color: blue">' + d.stateName + '</span>';
		        	} else {
		        		return '<span style="color: goldenrod">' + d.stateName + '</span>';
		        	}
		        }},
	            { field: 'masterStaffName', width: 80, title: '班主任'},
	            { field: 'floorName', width: 120, title: '教学楼'},
	            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	        ]],
		    done: function(){
		    	matchingLanguage();
		    }
	    });
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'del') { // 删除
	        	del(data, obj);
	        } else if (layEvent === 'edit') { // 编辑
	        	edit(data);
	        }
	    });
	    form.render();
	}
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:schoolBasePath + "classmation003", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolclassmation/schoolclassmationedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "schoolclassmationedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    // 新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/schoolclassmation/schoolclassmationadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "schoolclassmationadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable(){
		table.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams () {
		return {
			gradeId: $("#gradeId").val(),
			schoolId: $("#schoolId").val(),
			className: $("#className").val(),
			year: $("#year").val()
		};
	}
    
    exports('schoolclassmationlist', {});
});
