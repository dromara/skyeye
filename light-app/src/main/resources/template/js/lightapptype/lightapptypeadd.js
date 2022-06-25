
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
	    
 		//初始化上传
 		$("#iconpicPath").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 6,
            "function": function (_this, data) {
                show("#iconpicPath", data);
            }
        });
 		
 		// 图标类型变化事件
 		form.on('radio(iconType)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//icon
	    		$(".menuIconTypeIsTwo").addClass("layui-hide");
	    		$(".menuIconTypeIsOne").removeClass("layui-hide");
	    	}else if(val == '2'){//图片
	    		$(".menuIconTypeIsTwo").removeClass("layui-hide");
	    		$(".menuIconTypeIsOne").addClass("layui-hide");
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		// 图标选中事件
 	    $("body").on("focus", "#iconPath", function(e){
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#iconPath").val(sysIconChooseClass);
				$("#iconShow").css({'color': 'black'});
				$("#iconShow").attr("class", "fa fa-fw " + $("#iconPath").val());
			});
 	    });
 	
 	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			typeName: $("#typeName").val(),
        			iconType: data.field.iconType
 	        	};
 	        	if(data.field.iconType == '1'){
 	        		params.iconPath = $("#iconPath").val();
 	        	}else if(data.field.iconType == '2'){
 	        		params.iconPath = $("#iconpicPath").find("input[type='hidden'][name='upload']").attr("oldurl");
 	        	} else {
 	        		winui.window.msg("状态值错误。", {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	AjaxPostUtil.request({url:reqBasePath + "lightapptype002", params:params, type: 'json', callback: function(json){
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