
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
	    
	    // 下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option.tpl');
	    
	    // 与本人关系
		showGrid({
		 	id: "relationshipId",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 7},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	    
	    // 证件类型
		showGrid({
		 	id: "cardType",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 8},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	    
	    // 政治面貌
		showGrid({
		 	id: "politicId",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 1},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			name: $("#name").val(),
 	        		sex: $("input[name='userSex']:checked").val(),
 	        		relationshipId: $("#relationshipId").val(),
 	        		cardType: $("#cardType").val(),
 	        		cardNumber: $("#cardNumber").val(),
 	        		politicId: $("#politicId").val(),
 	        		workUnit: $("#workUnit").val(),
 	        		job: $("#job").val(),
 	        		emergencyContact: $("#emergencyContact").val(),
	 	        	staffId: parent.staffId
 	        	};
 	        	
 	        	AjaxPostUtil.request({url:reqBasePath + "sysstafffamily002", params: params, type: 'json', callback: function(json){
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