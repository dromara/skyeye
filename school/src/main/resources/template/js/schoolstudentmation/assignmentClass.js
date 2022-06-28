
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
		    
		var stuSessionYear = "";//该学生上学的年份
		var gradePointYear = "";//年级当前属于哪一年上学的学生
		
		//获取学生信息展示用于分班
		AjaxPostUtil.request({url:schoolBasePath + "studentmation004", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
   				stuSessionYear = json.bean.sessionYear;
   				$("#showForm").html(getDataUseHandlebars($("#assignmentTemplate").html(), json));
   				//加载当前选中的年级是哪一届的以及这一届的班级信息
				showGrid({
				 	id: "classId",
				 	url: schoolBasePath + "grademation009",
				 	params: {gradeId: json.bean.gradeId},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option.tpl'),
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(data){
				 		gradePointYear = data.bean.year
				 		$("#gradeSessionYear").val(gradePointYear);
				 		$("#gradeSessionYearSpan").html(gradePointYear);
				 		form.render('select');
				 	}
			    });
		        matchingLanguage();
			    form.on('submit(formAddBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			classId: $("#classId").val(),
		        			rowId: parent.rowId
			        	};
			        	//判断该学生和当前年级指向的入学年份是否一致
			        	if(gradePointYear != stuSessionYear){
			        		var msg = "该生属于" + stuSessionYear + "届学生，年级指向" + gradePointYear + "届，是否继续？";
							layer.confirm(msg, { icon: 3, title: '确认操作' }, function (index) {
								layer.close(index);
					            saveData(params)
							});
			        	} else {
			        		saveData(params)
			        	}
			        }
			        return false;
			    });
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
   		
   		//保存数据
   		function saveData(params){
   			AjaxPostUtil.request({url:schoolBasePath + "studentmation005", params: params, type: 'json', callback: function (json) {
 	   			if (json.returnCode == 0) {
 	   				parent.layer.close(index);
	 	        	parent.refreshCode = '0';
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
   		}
		    
	});
	    
});