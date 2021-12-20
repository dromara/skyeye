
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

		var parentType;
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "knowledgetype006",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: getFileContent('tpl/knowledgetype/knowledgetypeeditTemplate.tpl'),
		 	ajaxSendAfter:function(json){
 	        	matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		name: $("#typeName").val()
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "knowledgetype007", params:params, type:'json', callback:function(json){
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
		 	}
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});