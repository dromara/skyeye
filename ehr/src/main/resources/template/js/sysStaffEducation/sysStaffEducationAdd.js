
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
	    
	    // 下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option.tpl');
	    
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
	    
 		// 学历
		showGrid({
		 	id: "educationId",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 2},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	    
	    // 学习形式
	    showGrid({
		 	id: "learningModalityId",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 3},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	    
	    // 学校性质
	    showGrid({
		 	id: "schoolNature",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 4},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });

		skyeyeEnclosure.init('enclosureUpload');
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			graductionSchool: $("#graductionSchool").val(),
 	        		major: $("#major").val(),
 	        		admissionTime: $("#admissionTime").val(),
 	        		graduationTime: $("#graduationTime").val(),
 	        		educationId: $("#educationId").val(),
 	        		learningModalityId: $("#learningModalityId").val(),
 	        		schoolNature: $("#schoolNature").val(),
	 	        	staffId: parent.staffId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "sysstaffeducation002", params: params, type: 'json', callback: function(json){
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

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});