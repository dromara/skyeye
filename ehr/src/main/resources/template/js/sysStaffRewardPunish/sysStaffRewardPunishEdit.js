
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

	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysstaffrewardpunish003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
				systemCommonUtil.checkStaffMation = {
		 			id: json.bean.staffId,
		 			userName: json.bean.userName
		 		};
		 		laydate.render({elem: '#rewardPunishTime', trigger: 'click'});
		 		
		 		textool.init({eleId: 'content', maxlength: 200 });
			    
			    textool.init({eleId: 'desc', maxlength: 200});

				// 奖惩分类
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeRewardsAndPunishments"]["key"], 'select', "typeId", json.bean.typeId, form);

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
		 	        	AjaxPostUtil.request({url: reqBasePath + "sysstaffrewardpunish004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
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