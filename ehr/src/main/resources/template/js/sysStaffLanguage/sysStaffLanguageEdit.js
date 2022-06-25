
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysstafflanguage003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
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
				 	ajaxSendAfter:function(data){
				 		$("#languageId").val(json.bean.languageId);
				 		// 语种等级
						showGrid({
						 	id: "levelId",
						 	url: reqBasePath + "sysstafflanguagelevel008",
						 	params: {typeId: $("#languageId").val()},
						 	pagination: false,
						 	template: selTemplate,
						 	ajaxSendLoadBefore: function(hdb){},
						 	ajaxSendAfter:function(data){
						 		$("#levelId").val(json.bean.levelId);
						 		form.render('select');
						 	}
					    });
				 		form.render('select');
				 	}
			    });
			    
			    form.on('select(languageId)', function(data){
					if(isNull(data.value) || data.value === '请选择'){
						$("#levelId").html("");
						form.render('select');
					} else {
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

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

 	        	matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
		        			languageId: $("#languageId").val(),
		 	        		levelId: $("#levelId").val(),
		 	        		getTime: $("#getTime").val(),
			 	        	rowId: parent.rowId,
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "sysstafflanguage004", params: params, type: 'json', callback: function(json){
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