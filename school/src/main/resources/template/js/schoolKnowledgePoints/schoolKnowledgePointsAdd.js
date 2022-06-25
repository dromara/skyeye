
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

		var ue = ueEditorUtil.initEditor('container');

		// 获取当前登陆用户所属的学校列表
		schoolUtil.queryMyBelongSchoolList(function (json) {
			$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
			form.render("select");
			// 加载年级
			initGrade();
		});
	    // 学校监听事件
		form.on('select(schoolId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#schoolId").html("");
				form.render('select');
			}else{
				// 加载年级
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
			 	ajaxSendAfter:function(json){
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
 		
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		title: $("#title").val(),
 	        		content: encodeURIComponent(ue.getContent()),
 	        		schoolId: $("#schoolId").val(),
        			gradeId: $("#gradeId").val(),
        			subjectId: $("#subjectId").val(),
        			typeState: $("input[name='typeState']:checked").val()
 	        	};
    			if(isNull(ue.getContent())){
    				winui.window.msg('请填写内容', {icon: 2, time: 2000});
    				return false;
    			}
    			AjaxPostUtil.request({url:schoolBasePath + "knowledgepoints002", params: params, type: 'json', callback: function(json){
    				if (json.returnCode == 0) {
    					parent.layer.close(index);
    	 	        	parent.refreshCode = '0';
    				}else{
    					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    				}
    			}});
 	        }
 	        return false;
 	    });
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});