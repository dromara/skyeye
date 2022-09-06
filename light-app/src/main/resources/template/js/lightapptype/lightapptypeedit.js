
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: sysMainMation.lightAppBasePath + "lightapptype003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/lightapptype/lightapptypeeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter:function (json) {
		 		//类型
		 		$("input:radio[name=iconType][value=" + json.bean.iconType + "]").attr("checked", true);
		 		if(json.bean.iconType == '1'){//icon
		 			$(".menuIconTypeIsTwo").addClass("layui-hide");
		 			$(".menuIconTypeIsOne").removeClass("layui-hide");
					// 初始化上传
					$("#iconpicPath").upload(systemCommonUtil.uploadCommon003Config('iconpicPath', 12, '', 1));
		 		}else if(json.bean.iconType == '2'){//图片
		 			$(".menuIconTypeIsTwo").removeClass("layui-hide");
					// 初始化上传
					$("#iconpicPath").upload(systemCommonUtil.uploadCommon003Config('iconpicPath', 12, json.bean.iconPath, 1));
		 			$(".menuIconTypeIsOne").addClass("layui-hide");
		 		}
		 		
		 		$("#iconShow").attr("class", "fa fa-fw " + $("#iconPath").val());
	 			$("#iconShow").css({'color': 'black'});
	 			
	 			matchingLanguage();
		 		form.render();
		 		
		 		//图标类型变化事件
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
		 	    $("body").on("focus", "#iconPath", function (e) {
					systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
						$("#iconPath").val(sysIconChooseClass);
						$("#iconShow").css({'color': 'black'});
						$("#iconShow").attr("class", "fa fa-fw " + $("#iconPath").val());
					});
		 	    });
		 		
		 	    form.on('submit(formEditMenu)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			typeName: $("#typeName").val(),
	 	        			iconType: data.field.iconType,
	 	        			rowId: parent.rowId,
		 	        	};
		 	        	if(data.field.iconType == '1'){
		 	        		params.iconPath = $("#iconPath").val();
		 	        	}else if(data.field.iconType == '2'){
		 	        		params.iconPath = $("#iconpicPath").find("input[type='hidden'][name='upload']").attr("oldurl");
		 	        	} else {
		 	        		winui.window.msg("状态值错误。", {icon: 2, time: 2000});
		 	        		return false;
		 	        	}
		 	        	AjaxPostUtil.request({url: sysMainMation.lightAppBasePath + "lightapptype004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
	    });
 	    
 	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});