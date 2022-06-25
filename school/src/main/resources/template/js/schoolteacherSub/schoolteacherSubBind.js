
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    //获取教师部分信息以及当前拥有的技能列表
	    AjaxPostUtil.request({url: schoolBasePath + "schoolteachersubject002", params: {rowId: parent.rowId}, type: 'json', callback: function(json){
   			if (json.returnCode == 0) {
   				$("#userName").html(json.bean.userName);
   				$("#schoolName").html(json.bean.schoolName);
   				
 	   			//初始化教师所属学校科目列表
				showGrid({
				 	id: "subjectList",
				 	url: schoolBasePath + "schoolsubjectmation006",
				 	params: {schoolId: json.bean.schoolId},
				 	pagination: false,
				 	template: getFileContent('tpl/template/checkbox-property.tpl'),
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(data){
				 		var skill = json.bean.skill;
				 		for(var i = 0; i < skill.length; i++){
							$('input:checkbox[rowId="' + skill[i].id + '"]').attr("checked", true);
						}
				 		form.render('checkbox');
				 	}
			    });
			    
			    matchingLanguage();
			    form.render();
		 	    form.on('submit(formAddBean)', function (data) {
		 	    	
		 	        if (winui.verifyForm(data.elem)) {
		 	        	
		 	        	//获取选中的科目技能信息
		 	        	var propertyIds = "";
			        	$.each($('input:checkbox:checked'),function(){
			        		propertyIds = propertyIds + $(this).attr("rowId") + ",";
			            });
		 	        	
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		propertyIds: propertyIds
		 	        	};
		 	        	
		 	        	AjaxPostUtil.request({url: schoolBasePath + "schoolteachersubject003", params:params, type: 'json', callback: function(json){
			 	   			if (json.returnCode == 0) {
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			} else {
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
		 	        }
		 	        return false;
		 	    });
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});