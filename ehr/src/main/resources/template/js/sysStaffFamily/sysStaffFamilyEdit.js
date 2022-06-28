
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysstafffamily003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		$("input:radio[name=userSex][value=" + json.bean.sex + "]").attr("checked", true);
			    $("#emergencyContact").val(json.bean.emergencyContact);
			    
			    // 与本人关系
				showGrid({
				 	id: "relationshipId",
				 	url: reqBasePath + "sysstaffdatadictionary008",
				 	params: {typeId: 7},
				 	pagination: false,
				 	template: selTemplate,
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(data){
				 		$("#relationshipId").val(json.bean.relationshipId);
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
				 	ajaxSendAfter:function(data){
				 		$("#cardType").val(json.bean.cardType);
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
				 	ajaxSendAfter:function(data){
				 		$("#politicId").val(json.bean.politicId);
				 		form.render('select');
				 	}
			    });
			    
			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
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
			 	        	rowId: parent.rowId
		 	        	};
		 	        	
		 	        	AjaxPostUtil.request({url: reqBasePath + "sysstafffamily004", params: params, type: 'json', callback: function (json) {
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
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});