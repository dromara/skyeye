
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	   //初始化应用类型
		function initAppType(id){
			showGrid({
			 	id: "parentId",
			 	url: reqBasePath + "appworkpage001",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		$("#parentId").val(id);
			 		form.render('select');
			 	}
		    });
		}
		
	    //初始化数据
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "appworkpage005",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/appworkpage/appworkpageeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
				$("input:radio[name=urlType][value=" + json.bean.urlType + "]").attr("checked", true);
		 		$("#logo").upload({
                    "action": reqBasePath + "common003",
                    "data-num": "1",
                    "data-type": "PNG,JPG,jpeg,gif",
                    "uploadType": 12,
                    "data-value": json.bean.logo,
                    "function": function (_this, data) {
                        show("#logo", data);
                    }
                });
	 			
                matchingLanguage();
		 		form.render();
		 		
		 		initAppType(json.bean.parentId);
		 	    form.on('submit(formEditMenu)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId, 
		        			title: $("#title").val(),
		        			url: $("#url").val(),
							parentId: $("#parentId").val(),
							urlType: data.field.urlType
		 	        	};
		 	        	params.logo = $("#logo").find("input[name='upload']").attr("oldurl");
		 	        	AjaxPostUtil.request({url:reqBasePath + "appworkpage006", params:params, type:'json', callback:function(json){
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
 	    
 	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});