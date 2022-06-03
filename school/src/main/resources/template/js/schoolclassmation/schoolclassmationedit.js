
//选中的教师信息
var teacherMation = {};
var teacherCheckType = '1';//教师选择类型：1.单选；2.多选
var teacherWhetherIncludeMe = '1';//是否包含当前登录用户：1.是；2.否
var teacherWhetherHasCode = '2';//是否必须是已分配帐号的教师：1.是；2.否

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    layedit = layui.layedit;
	    
		showGrid({
		 	id: "showForm",
		 	url: schoolBasePath + "classmation004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/schoolclassmation/schoolclassmationeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		//教师赋值
		 		if(!isNull(json.bean.masterStaffId)){
		 			teacherMation = {
		 				staffId: json.bean.masterStaffId,
		 				userName: json.bean.userName
		 			};
		 			$("#masterStaffName").val(teacherMation.userName);
		 		}
		 		
		 		//加载教学楼
		 		initFloorId(json.bean.schoolId, json.bean.floorId);
		 		
		 		matchingLanguage();
		 		form.render();
		        
		 		form.on('submit(formEditBean)', function (data) {
			    	
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
			        		rowId: parent.rowId,
		        			className: $("#className").val(),
		        			masterStaffId: "",
		        			floorId: $("#floorId").val(),
		        			limitNumber: $("#limitNumber").val(),
		        			year: $("#year").html()
			        	};
			        	
			        	//选中的教师赋值
			        	if(!isNull(teacherMation.staffId)){
			        		params.masterStaffId = teacherMation.staffId;
			        	}
			        	
			        	AjaxPostUtil.request({url:schoolBasePath + "classmation005", params:params, type: 'json', callback: function(json){
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
	    
        //所属教学楼
        function initFloorId(schoolId, chooseFloorId){
		    showGrid({
	    	 	id: "floorId",
	    	 	url: schoolBasePath + "schoolfloormation006",
	    	 	params: {schoolId: schoolId},
	    	 	pagination: false,
	    	 	template: getFileContent('tpl/template/select-option.tpl'),
	    	 	ajaxSendLoadBefore: function(hdb){
	    	 	},
	    	 	ajaxSendAfter:function(json){
	    	 		$("#floorId").val(chooseFloorId);
	    	 		form.render('select');
	    	 	}
	        });
        }
        
        //教师选择
 	    $("body").on("click", "#masterStaffNameSel", function(e){
 	    	_openNewWindows({
 				url: "../../tpl/schoolteacher/teacherChoose.html", 
 				title: "选择教师",
 				pageId: "teacherChoose",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#masterStaffName").val(teacherMation.userName);
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
 	                }
 				}});
 	    });
		
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});