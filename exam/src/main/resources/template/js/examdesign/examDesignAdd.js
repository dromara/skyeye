layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;

		// 获取当前登陆用户所属的学校列表
		schoolUtil.queryMyBelongSchoolList(function (json) {
			$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
			form.render("select");
			// 加载年级
			initGrade();
			// 加载学期
			initSemester();
		});
	    //学校监听事件
		form.on('select(schoolId)', function(data) {
			if(isNull(data.value) || data.value === '请选择'){
				$("#schoolId").html("");
				form.render('select');
			} else {
				//加载年级
				initGrade();
				//加载学期
				initSemester();
			}
		});
		
		//初始化年级
		function initGrade(){
			showGrid({
			 	id: "gradeId",
			 	url: schoolBasePath + "grademation006",
			 	params: {schoolId: $("#schoolId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb) {},
			 	ajaxSendAfter:function (json) {
			 		form.render('select');
			 	}
		    });
		}
		//年级监听事件
		form.on('select(gradeId)', function(data) {
			if(isNull(data.value) || data.value === '请选择'){
				$("#subjectId").html("");
				$("#sessionYear").html("");
		 		$("#classList").html("");
				form.render('select');
			} else {
				//加载科目
				initSubject();
				//加载班级
				loadThisGradeNowYear();
			}
		});
		
		//初始化学期
		function initSemester(){
			showGrid({
			 	id: "semesterId",
			 	url: schoolBasePath + "schoolsemester006",
			 	params: {schoolId: $("#schoolId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb) {},
			 	ajaxSendAfter:function (json) {
			 		form.render('select');
			 	}
		    });
		}
		
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
		
		//加载当前选中的年级是哪一届的以及这一届的班级信息
		function loadThisGradeNowYear(){
			showGrid({
			 	id: "classList",
			 	url: schoolBasePath + "grademation009",
			 	params: {gradeId: $("#gradeId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/checkbox-property.tpl'),
			 	ajaxSendLoadBefore: function(hdb) {},
			 	ajaxSendAfter:function(data) {
			 		$("#sessionYear").html(data.bean.year + '届学生');
			 		form.render('checkbox');
			 	},
			 	ajaxSendErrorAfter: function (json) {
			 		$("#sessionYear").html("");
			 		$("#classList").html("");
			 	}
		    });
		}
	    
		matchingLanguage();
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	//获取选中的班级信息
 	        	var propertyIds = "";
	        	$.each($('input:checkbox:checked'),function(){
	        		propertyIds = propertyIds + $(this).attr("rowId") + ",";
	            });
	            if(isNull(propertyIds)){
	            	winui.window.msg('请选择班级', {icon: 2, time: 2000});
	            	return false;
	            }
	        	
	        	var params = {
        			surveyName: $("#surveyName").val(),
        			schoolId: $("#schoolId").val(),
        			gradeId: $("#gradeId").val(),
        			semesterId: $("#semesterId").val(),
        			subjectId: $("#subjectId").val(),
        			viewAnswer: $("input[name='viewAnswer']:checked").val(),
        			propertyIds: propertyIds
	        	};
	        	AjaxPostUtil.request({url:schoolBasePath + "exam002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
	        }
	        return false;
	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});