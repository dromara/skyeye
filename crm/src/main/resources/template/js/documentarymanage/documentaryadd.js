
// 商机管理
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
	    	textool = layui.textool;

	    //跟单时间
		laydate.render({
			elem : '#documentaryTime',
			type : 'datetime',
			trigger : 'click'
		});
		
		textool.init({
	    	eleId: 'detail',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
	    
	    //分类
		showGrid({
		 	id: "typeId",
		 	url: reqBasePath + "crmdocumentarytype008",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(j){
		 		form.render('select');
		 		opportunityFrom();
		 	}
		});
		
		//商机
		function opportunityFrom(){
			showGrid({
			 	id: "opportunityId",
			 	url: reqBasePath + "opportunity008",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){
			 	},
			 	ajaxSendAfter:function(j){
			 		form.render('select');
			 	}
			});
		}

		skyeyeEnclosure.init('enclosureUpload');
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			detail: $("#detail").val(),
        			documentaryTime: $("#documentaryTime").val(),
					typeId: $("#typeId").val(),
					opportunityId: $("#opportunityId").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "documentary002", params: params, type: 'json', callback: function(json){
	 	   			if (json.returnCode == 0){
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});