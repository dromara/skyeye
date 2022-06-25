
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
			 	url: reqBasePath + "lightapptype010",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		
		//初始化上传
 		$("#appLogo").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 6,
            "function": function (_this, data) {
                show("#appLogo", data);
            }
        });
	    
 		// 图标选中事件
 	    $("body").on("focus", "#appLogo", function(e){
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
        			typeId: $("#typeId").val()
 	        	};
 	        	params.appLogo = $("#appLogo").find("input[name='upload']").attr("oldurl");
 	        	if(isNull(params.appLogo)){
        			winui.window.msg("请选择应用logo", {icon: 2, time: 2000});
 	        		return false;
        	    }
 	        	AjaxPostUtil.request({url:reqBasePath + "lightapp002", params:params, type: 'json', callback: function(json){
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