
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
		 	url: reqBasePath + "knowledgepoints004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#editTemplate").html(),
		 	ajaxSendAfter:function(json){
		 		$("input:radio[name=typeState][value=" + json.bean.type + "]").attr("checked", true);
		 		//初始化学校
				showGrid({
				 	id: "schoolId",
				 	url: reqBasePath + "schoolmation008",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option-must.tpl'),
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(data){
				 		$("#schoolId").val(json.bean.schoolId);
						form.render('select');
						//年级
				 		showGrid({
						 	id: "gradeId",
						 	url: reqBasePath + "grademation006",
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
								 	url: reqBasePath + "schoolsubjectmation007",
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
				 	}
			    });
			    //学校监听事件
				form.on('select(schoolId)', function(data){
					if(isNull(data.value) || data.value === '请选择'){
						$("#schoolId").html("");
						form.render('select');
					}else{
						//加载年级
						initGrade();
					}
				});
				
				//初始化年级
				function initGrade(){
					showGrid({
					 	id: "gradeId",
					 	url: reqBasePath + "grademation006",
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
		    				winui.window.msg('请填写内容', {icon: 2,time: 2000});
		    				return false;
		    			}
	 					AjaxPostUtil.request({url:reqBasePath + "knowledgepoints005", params: params, type: 'json', callback: function(json){
	 						if(json.returnCode == 0){
	 							parent.layer.close(index);
	 							parent.refreshCode = '0';
	 						}else{
	 							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 						}
	 					}});
	 				}
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});