
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
		    form = layui.form,
		    textool = layui.textool;
	    
 		showGrid({
			id: "showForm",
			url: flowableBasePath + "erpmachin006",
			params: {rowId: parent.rowId},
			pagination: false,
			template: $("#useTemplate").html(),
			ajaxSendLoadBefore: function(hdb){
				hdb.registerHelper('compare1', function(v1, v2, options) {
		 			return (parseFloat(v1) * parseFloat(v2)).toFixed(2);
		 		});
			},
			ajaxSendAfter: function(json){
				textool.init({
			    	eleId: 'opinion',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
			    
			    matchingLanguage();
				form.render();
		 		form.on('submit(formAddBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var msg = '确认提交吗？';
			    		layer.confirm(msg, { icon: 3, title: '提交审批' }, function (i) {
			    			layer.close(i);
			    			var jStr = {
				    			content: $("#opinion").val(),
				    			status: $("input[name='flag']:checked").val(),
				    			rowId: parent.rowId
				            };
				            AjaxPostUtil.request({url:flowableBasePath + "erpmachin008", params: jStr, type: 'json', callback: function(json){
				 	   			if (json.returnCode == 0) {
			                    	parent.layer.close(index);
			                    	parent.refreshCode = '0';
				 	   			}else{
				 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				 	   			}
				 	   		}});
			    		});
			        }
			        return false;
			    });
			}
		});
		
		//图片预览
		$("body").on("click", ".barCode", function(e){
			layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + fileBasePath + $(this).attr("src") + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
		});
		
		//取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
		
	});
});