
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: schoolBasePath + "knowledgepoints004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#editTemplate").html(),
		 	ajaxSendAfter:function (json) {
		 		$("input:radio[name=typeState][value=" + json.bean.type + "]").attr("checked", true);
				// 获取当前登陆用户所属的学校列表
				schoolUtil.queryMyBelongSchoolList(function (data) {
					$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), data));
					$("#schoolId").val(json.bean.schoolId);
					form.render("select");
				});
				//年级
				showGrid({
					id: "gradeId",
					url: schoolBasePath + "grademation006",
					params: {schoolId: $("#schoolId").val()},
					pagination: false,
					template: getFileContent('tpl/template/select-option.tpl'),
					ajaxSendLoadBefore: function(hdb){},
					ajaxSendAfter:function(data){
						$("#gradeId").val(json.bean.gradeId);
						form.render('select');
						//科目
						showGrid({
							id: "subjectId",
							url: schoolBasePath + "schoolsubjectmation007",
							params: {gradeId: $("#gradeId").val()},
							pagination: false,
							template: getFileContent('tpl/template/select-option.tpl'),
							ajaxSendLoadBefore: function(hdb){},
							ajaxSendAfter:function(data){
								$("#subjectId").val(json.bean.subjectId);
								form.render('select');
							}
						});
					}
				});
			    //学校监听事件
				form.on('select(schoolId)', function(data){
					if(isNull(data.value) || data.value === '请选择'){
						$("#schoolId").html("");
						form.render('select');
					} else {
						//加载年级
						initGrade();
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
					 	ajaxSendLoadBefore: function(hdb){},
					 	ajaxSendAfter:function(data){
					 		form.render('select');
					 	}
				    });
				}
				//年级监听事件
				form.on('select(gradeId)', function(data){
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
					 	ajaxSendLoadBefore: function(hdb){},
					 	ajaxSendAfter:function(data){
					 		form.render('select');
					 	}
				    });
				}

				var ue = ueEditorUtil.initEditor('container');
			    ue.addListener("ready", function () {
			    	ue.setContent(json.bean.content);
			    });
		 		
			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
	 				if (winui.verifyForm(data.elem)) {
	 					var params = {
		 	        		title: $("#title").val(),
		 	        		content: encodeURIComponent(ue.getContent()),
		 	        		schoolId: $("#schoolId").val(),
		        			gradeId: $("#gradeId").val(),
		        			subjectId: $("#subjectId").val(),
		        			typeState: $("input[name='typeState']:checked").val(),
		        			rowId: parent.rowId
		 	        	};
		    			if(isNull(ue.getContent())){
		    				winui.window.msg('请填写内容', {icon: 2, time: 2000});
		    				return false;
		    			}
	 					AjaxPostUtil.request({url: schoolBasePath + "knowledgepoints005", params: params, type: 'json', callback: function(json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 					}});
	 				}
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});