
// 员工学历
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
		 	url: reqBasePath + "sysstaffeducation003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		// 入校时间
			    var insStart = laydate.render({ 
		 			elem: '#admissionTime',
		 	 		trigger: 'click',
		 	 		done: function(value, date){
		 	 			insEnd.config.min = lay.extend({}, date, {
		 	 				month: date.month - 1
		 	 			});
		 	 			insEnd.config.elem[0].focus();
		 	 		}
		 		});
			    
			    // 毕业时间
			    var insEnd = laydate.render({ 
		 			elem: '#graduationTime',
		 	 		trigger: 'click',
		 	 		done: function(value, date){
		 	 			insStart.config.max = lay.extend({}, date, {
		 	 				month: date.month - 1
		 	 			});
		 	 		}
		 		});

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

				// 学历
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeEducation"]["key"], 'select', "educationId", json.bean.educationId, form);

				// 学习形式
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeBgAndLearningForm"]["key"], 'select', "learningModalityId", json.bean.learningModalityId, form);

				// 学校性质
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeBgSchoolNature"]["key"], 'select', "schoolNature", json.bean.schoolNature, form);

			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
		        			graductionSchool: $("#graductionSchool").val(),
		 	        		major: $("#major").val(),
		 	        		admissionTime: $("#admissionTime").val(),
		 	        		graduationTime: $("#graduationTime").val(),
		 	        		educationId: $("#educationId").val(),
		 	        		learningModalityId: $("#learningModalityId").val(),
		 	        		schoolNature: $("#schoolNature").val(),
			 	        	rowId: parent.rowId,
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "sysstaffeducation004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 		form.render();
		 	}
	    });

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});