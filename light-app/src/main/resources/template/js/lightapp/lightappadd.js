
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    initAppType();
	   	//初始化应用类型
		function initAppType(){
			showGrid({
			 	id: "typeId",
			 	url: sysMainMation.lightAppBasePath + "lightapptype010",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb) {},
			 	ajaxSendAfter:function (json) {
			 		form.render('select');
			 	}
		    });
		}

		// 初始化上传
		$("#appLogo").upload(systemCommonUtil.uploadCommon003Config('appLogo', 12, '', 1));

 		// 图标选中事件
 	    $("body").on("focus", "#appLogo", function (e) {
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#appLogo").val(sysIconChooseClass);
				$("#iconShow").css({'color': 'black'});
				$("#iconShow").attr("class", "fa fa-fw " + $("#appLogo").val());
			});
 	    });
 	
 	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			appName: $("#appName").val(),
        			appUrl: $("#appUrl").val(),
        			desc: $("#desc").val(),
        			typeId: $("#typeId").val(),
					appLogo: $("#appLogo").find("input[name='upload']").attr("oldurl")
 	        	};
 	        	if(isNull(params.appLogo)){
        			winui.window.msg("请选择应用logo", {icon: 2, time: 2000});
 	        		return false;
        	    }
 	        	AjaxPostUtil.request({url: sysMainMation.lightAppBasePath + "lightapp002", params: params, type: 'json', callback: function (json) {
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