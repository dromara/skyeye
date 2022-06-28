
//教师选择参数
var chooseTeacherList = [];
var teacherCheckType = '2';//教师选择类型：1.单选；2.多选
var teacherWhetherIncludeMe = '2';//是否包含当前登录用户：1.是；2.否
var teacherWhetherHasCode = '1';//是否必须是已分配帐号的教师：1.是；2.否

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
		    
		//获取试卷详情信息以及阅卷人信息
		AjaxPostUtil.request({url:schoolBasePath + "exam036", params: {surveyId: parent.rowId}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
   				$("#showForm").html(getDataUseHandlebars($("#assignmentTemplate").html(), json));
   				//回显阅卷人
   				var str = "";
   				chooseTeacherList = [].concat(json.rows);
		        $.each(json.rows, function(i, row){
		        	str += row.userName + '(' + row.userSex + ')，';
		        });
		        $("#markPeople").val(str);
		        
		        matchingLanguage();
			    form.on('submit(formAddBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var array = new Array();
				    	$.each(chooseTeacherList, function(i, item){
				    		array.push({
				    			userId: item.userId,
				    			surveyId: parent.rowId
				    		});
				    	});
			        	var params = {
		        			surveyId: parent.rowId,
		        			arrayStr: JSON.stringify(array)
			        	};
			        	AjaxPostUtil.request({url:schoolBasePath + "exam037", params: params, type: 'json', callback: function (json) {
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
   		
   		//阅卷人选择
 	    $("body").on("click", "#markPeopleNameSel", function (e) {
 	    	_openNewWindows({
 				url: "../../tpl/schoolteacher/teacherChoose.html", 
 				title: "选择阅卷人",
 				pageId: "teacherChoose",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	var str = "";
 	                	$.each(chooseTeacherList, function(i, row){
				        	str += row.userName + '(' + row.userSex + ')，';
				        });
				        $("#markPeople").val(str);
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
 	                }
 				}});
 	    });
   		
	});
	    
});