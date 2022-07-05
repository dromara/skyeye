
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
	    
	    var fileType = parent.fileType;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysenclosure005", params:{rowId: parent.fileId}, type: 'json', callback: function (json) {
			if(isNull(json.bean)){
				winui.window.msg("该数据不存在", {icon: 5,time: 2000});
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				$("#parentFolderName").html(parent.folderName);
				$("#typeName").val(json.bean.typeName);
			}
   		}});
	    
   		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			typeName: $("#typeName").val(),
        			fileType: fileType,
        			rowId: parent.fileId
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "sysenclosure006", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});