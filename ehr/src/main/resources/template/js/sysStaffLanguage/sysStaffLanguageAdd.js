
// 员工语种等级
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
	    
	    laydate.render({ 
 			elem: '#getTime',
 	 		trigger: 'click'
 		});
 		
 		// 语种
		showGrid({
		 	id: "languageId",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 6},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	    
	    form.on('select(languageId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#levelId").html("");
				form.render('select');
			}else{
				// 语种等级
				showGrid({
				 	id: "levelId",
				 	url: reqBasePath + "sysstafflanguagelevel008",
				 	params: {typeId: data.value},
				 	pagination: false,
				 	template: selTemplate,
				 	ajaxSendLoadBefore: function(hdb){},
				 	ajaxSendAfter:function(json){
				 		form.render('select');
				 	}
			    });
			}
		});

		skyeyeEnclosure.init('enclosureUpload');
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			languageId: $("#languageId").val(),
 	        		levelId: $("#levelId").val(),
 	        		getTime: $("#getTime").val(),
	 	        	staffId: parent.staffId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "sysstafflanguage002", params: params, type: 'json', callback: function(json){
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

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});