
// 员工奖惩信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
			textool = layui.textool;
	    // 下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option.tpl');
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysstaffrewardpunish003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
				systemCommonUtil.checkStaffMation = {
		 			id: json.bean.staffId,
		 			userName: json.bean.userName
		 		};
		 		laydate.render({ 
		 			elem: '#rewardPunishTime',
		 	 		trigger: 'click'
		 		});
		 		
		 		textool.init({
			    	eleId: 'content',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
			    
			    textool.init({
			    	eleId: 'desc',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
			    
			    // 奖惩分类
			    showGrid({
				 	id: "typeId",
				 	url: reqBasePath + "sysstaffdatadictionary008",
				 	params: {typeId: 5},
				 	pagination: false,
				 	template: selTemplate,
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(data){
				 		$("#typeId").val(json.bean.typeId),
				 		form.render('select');
				 	}
			    });

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

 	        	matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
		        			name: $("#name").val(),
		 	        		typeId: $("#typeId").val(),
		 	        		rewardPunishTime: $("#rewardPunishTime").val(),
		 	        		price: $("#price").val(),
		 	        		awardUnit: $("#awardUnit").val(),
		 	        		content: $("#content").val(),
		 	        		desc: $("#desc").val(),
			 	        	staffId: systemCommonUtil.checkStaffMation.id,
			 	        	rowId: parent.rowId,
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "sysstaffrewardpunish004", params: params, type: 'json', callback: function(json){
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
		 		form.render();
		 	}
	    });

	    // 选择员工
	    $("body").on("click", "#staffNameSel", function() {
			systemCommonUtil.userStaffCheckType = false; // 选择类型，默认单选，true:多选，false:单选
			systemCommonUtil.openSysAllUserStaffChoosePage(function (checkStaffMation){
				$("#staffName").val(checkStaffMation.jobNumber + "_" + checkStaffMation.userName);
			});
	    });
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});