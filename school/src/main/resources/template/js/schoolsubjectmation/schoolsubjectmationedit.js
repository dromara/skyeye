
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	textool = layui.textool;
	    
	    showGrid({
		 	id: "showForm",
		 	url: schoolBasePath + "schoolsubjectmation003",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/schoolsubjectmation/schoolsubjectmationeditTemplate.tpl'),
		 	ajaxSendAfter:function(json){
		 		
		 		textool.init({
			    	eleId: 'desc',
			    	maxlength: 500,
			    	tools: ['count', 'copy', 'reset']
			    });
		 		
		 		//初始化学校
				showGrid({
				 	id: "schoolId",
				 	url: schoolBasePath + "schoolmation008",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option-must.tpl'),
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(data){
				 		$("#schoolId").val(json.bean.schoolId);
				 		form.render("select");
				 	}
			    });
		 		
			    matchingLanguage();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		subjectName: $("#subjectName").val(),
		 	        		subjectNo: $("#subjectNo").val(),
		 	        		desc: $("#desc").val(),
		 	        		schoolId: $("#schoolId").val()
		 	        	};

		 	        	AjaxPostUtil.request({url:schoolBasePath + "schoolsubjectmation004", params:params, type: 'json', callback: function(json){
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