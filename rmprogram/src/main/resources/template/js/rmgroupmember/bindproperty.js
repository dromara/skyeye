
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    // 加载标签属性
	    showGrid({
	     	id: "propertyList",
	     	url: reqBasePath + "rmproperty006",
	     	params: {},
	     	pagination: false,
	     	template: getFileContent('tpl/template/checkbox-property.tpl'),
	     	ajaxSendLoadBefore: function(hdb){
	     	},
	     	ajaxSendAfter:function(json){
	     		form.render('checkbox');
	     		var params = {
        			rowId: parent.rowId
        		};
	     		AjaxPostUtil.request({url:reqBasePath + "rmxcx039", params:params, type: 'json', callback: function(json){
					if (json.returnCode == 0) {
						if(json.total != 0){
							for(var i in json.rows){
								$('input:checkbox[rowId="' + json.rows[i].propertyId + '"]').attr("checked", true);
							}
						}
						form.render('checkbox');
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
	     	}
	    });
		
	    matchingLanguage();
 		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var propertyIds = "";
	        	$.each($('input:checkbox:checked'),function(){
	        		propertyIds = propertyIds + $(this).attr("rowId") + ",";
	            });
	        	if(isNull(propertyIds)){
	        		winui.window.msg('请先选择标签属性。', {icon: 2, time: 2000});
	        	} else {
	        		var params = {
	        			rowId: parent.rowId,
	        			propertyIds: propertyIds
	        		};
	        		AjaxPostUtil.request({url:reqBasePath + "rmxcx038", params:params, type: 'json', callback: function(json){
						if (json.returnCode == 0) {
							parent.layer.close(index);
							parent.refreshCode = '0';
						} else {
							winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
						}
					}});
	        	}
	        }
	        return false;
	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
	    
});